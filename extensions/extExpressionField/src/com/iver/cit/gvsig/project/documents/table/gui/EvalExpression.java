package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.EditionUtilities;
import com.iver.cit.gvsig.ExpressionFieldExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.IOperator;
import com.iver.cit.gvsig.project.documents.table.Index;

/**
 * This class implements the logic of a expression and fill a field of the table
 * 
 * To use this class to evaluate and execute an expression something like this
 * can be done:
 * 
 * ExpressionFieldExtension efe = (ExpressionFieldExtension)
 * PluginServices.getExtension(ExpressionFieldExtension.class);
 * 
 * EvalExpression ee = new EvalExpression(efe.getInterpreter(),
 * efe.getOperators()); ToggleEditing te = new ToggleEditing(); // put the layer
 * in edition mode te.startEditing(layer); ee.setLayer(layer, 7);
 * ee.evalExpression("toUpperCase([NOME_MAPA])"); // close edition mode and save
 * the layer te.stopEditing(layer, false);
 * 
 * @author Vicente Caballero Navarro
 */
public class EvalExpression {
	private FieldDescription[] fieldDescriptors;
	private FieldDescription fieldDescriptor;
	private FLyrVect layer;
	private IEditableSource ies = null;
	private static Preferences prefs = Preferences.userRoot().node(
			"fieldExpressionOptions");
	private int limit;
	private SelectableDataSource sds;
	private BSFManager interpreter;
	private Index indexRow;
	private int selectedIndex;
	private Table table;
	private ArrayList<IOperator> operators = new ArrayList<IOperator>();

	/**
	 * @deprecated
	 */
	public EvalExpression() {
		limit = prefs.getInt("limit_rows_in_memory", -1);
	}

	public EvalExpression(BSFManager interpreter, ArrayList<IOperator> operators) {
		limit = prefs.getInt("limit_rows_in_memory", -1);
		this.interpreter = interpreter;
		this.operators = operators;
	}

	/**
	 * 
	 * @param layer
	 *            Must be in edition or a ClassCastException will be thrown
	 * @param selectedIndex
	 *            The index of the field in the FieldDescription which will be
	 *            filled by the expression
	 */
	public void setLayer(FLyrVect layer, int selectedIndex) {
		this.layer = layer;
		ies = (VectorialEditableAdapter) layer.getSource();
		this.selectedIndex = selectedIndex;
		init();

	}

	public void setTable(Table table) {
		// TODO: table is only needed to make table.refresh on the dialog.
		// Probably a fireevent can be done to avoid this
		this.table = table;
		layer = (FLyrVect) table.getModel().getAssociatedTable();
		if (layer == null)
			ies = table.getModel().getModelo();
		else
			ies = (VectorialEditableAdapter) layer.getSource();
		BitSet columnSelected = table.getSelectedFieldIndices();
		selectedIndex = columnSelected.nextSetBit(0);
		init();
	}

	private void init() {
		try {
			sds = ies.getRecordset();
			fieldDescriptors = sds.getFieldsDescription();
			fieldDescriptor = fieldDescriptors[selectedIndex];
			interpreter.declareBean("sds", sds, SelectableDataSource.class);
			indexRow = new Index();
			interpreter.declareBean("indexRow", indexRow, Index.class);
		} catch (BSFException e) {
			e.printStackTrace();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
	}

	public void setValue(Object obj, int i) {
		// VectorialEditableAdapter vea = (VectorialEditableAdapter)
		// lv.getSource();
		Value value = getValue(obj);
		IRow feat = null;
		try {
			feat = ies.getRow(i).getLinkedRow().cloneRow();
		} catch (ExpansionFileReadException e) {
			NotificationManager.addError(e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}
		Value[] values = feat.getAttributes();
		values[selectedIndex] = value;
		feat.setAttributes(values);

		IRowEdited edRow = new DefaultRowEdited(feat,
				IRowEdited.STATUS_MODIFIED, i);
		try {
			ies.modifyRow(edRow.getIndex(), edRow.getLinkedRow(), "",
					EditionEvent.ALPHANUMERIC);
		} catch (ExpansionFileWriteException e) {
			NotificationManager.addError(e);
		} catch (ExpansionFileReadException e) {
			NotificationManager.addError(e);
		} catch (ValidateRowException e) {
			NotificationManager.addError(e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}

	}

	public void isCorrectValue(Object obj) throws BSFException {
		if (obj instanceof Number || obj instanceof Date
				|| obj instanceof Boolean || obj instanceof String) {

		} else {
			throw new BSFException("incorrect");
		}
	}

	private Value getValue(Object obj) {
		int typeField = fieldDescriptor.getFieldType();
		Value value = null;// ValueFactory.createNullValue();

		if (obj instanceof Number) {
			if (typeField == Types.DOUBLE || typeField == Types.NUMERIC) {
				double dv = ((Number) obj).doubleValue();
				value = ValueFactory.createValue(dv);
			} else if (typeField == Types.FLOAT) {
				float df = ((Number) obj).floatValue();
				value = ValueFactory.createValue(df);
			} else if (typeField == Types.INTEGER) {
				int di = ((Number) obj).intValue();
				value = ValueFactory.createValue(di);
			} else if (typeField == Types.BIGINT) {
				long di = ((Number) obj).longValue();
				value = ValueFactory.createValue(di);
			} else if (typeField == Types.VARCHAR) {
				String s = ((Number) obj).toString();
				value = ValueFactory.createValue(s);
			} else if (typeField == Types.BOOLEAN) {
				if (((Number) obj).intValue() == 0) {
					value = ValueFactory.createValue(false);
				} else {
					value = ValueFactory.createValue(true);
				}
			}
		} else if (obj instanceof Date) {
			if (typeField == Types.DATE) {
				Date date = (Date) obj;
				value = ValueFactory.createValue(date);
			} else if (typeField == Types.VARCHAR) {
				String s = ((Date) obj).toString();
				value = ValueFactory.createValue(s);
			}
		} else if (obj instanceof Boolean) {
			if (typeField == Types.BOOLEAN) {
				boolean b = ((Boolean) obj).booleanValue();
				value = ValueFactory.createValue(b);
			} else if (typeField == Types.VARCHAR) {
				String s = ((Boolean) obj).toString();
				value = ValueFactory.createValue(s);
			}
		} else if (obj instanceof String) {
			if (typeField == Types.VARCHAR) {
				String s = obj.toString();
				value = ValueFactory.createValue(s);
			}
		} else {
			value = ValueFactory.createNullValue();
		}

		return value;
	}

	public FieldDescription getFieldDescriptorSelected() {
		return fieldDescriptor;
	}

	public FieldDescription[] getFieldDescriptors() {
		return fieldDescriptors;
	}

	public void saveEdits(int numRows) throws ReadDriverException,
			InitializeWriterException, StopWriterVisitorException {
		if (limit == -1 || numRows == 0 || (numRows % limit) != 0) {
			return;
		}
		ies.endComplexRow(PluginServices.getText(this, "expression"));
		if ((layer != null)
				&& layer.getSource() instanceof VectorialEditableAdapter) {
			VectorialEditableAdapter vea = (VectorialEditableAdapter) layer
					.getSource();
			ISpatialWriter spatialWriter = (ISpatialWriter) vea.getDriver();
			vea.cleanSelectableDatasource();
			// We want that the recordset of the layer shows the changes of the
			// fields
			layer.setRecordset(vea.getRecordset());
			ILayerDefinition lyrDef = EditionUtilities
					.createLayerDefinition(layer);
			spatialWriter.initialize(lyrDef);
			vea.saveEdits(spatialWriter, EditionEvent.ALPHANUMERIC);
			vea.getCommandRecord().clearAll();
		} else {
			if (ies instanceof IWriteable) {
				IWriteable w = (IWriteable) ies;
				IWriter writer = w.getWriter();
				if (writer == null) {
				} else {
					ITableDefinition tableDef = ies.getTableDefinition();
					writer.initialize(tableDef);

					ies.saveEdits(writer, EditionEvent.ALPHANUMERIC);
					ies.getSelection().clear();
				}
			}
			ies.getCommandRecord().clearAll();
		}
		ies.startComplexRow();
	}

	/**
	 * Evaluate the expression.
	 * 
	 * @throws ReadDriverException
	 * @throws BSFException
	 */
	public boolean evalExpression(String expression)
			throws ReadDriverException, BSFException {
		long rowCount = sds.getRowCount();
		byte[] expressionBytes;
		String encoding = System.getProperty("file.encoding");
		try {
			expressionBytes = expression.getBytes(encoding);
			expression = new String(expressionBytes, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		expression = expression.replaceAll("\\[", "field(\"").replaceAll("\\]",
				"\")");

		interpreter.declareBean("ee", this, EvalExpression.class);
		interpreter.exec(ExpressionFieldExtension.JYTHON, null, -1, -1,
				"def expression():\n" + "  return " + expression + "");
		if (rowCount > 0) {
			try {
				interpreter.exec(ExpressionFieldExtension.JYTHON, null, -1, -1,
						"def isCorrect():\n"
								+ "    ee.isCorrectValue(expression())\n");
				interpreter.exec(ExpressionFieldExtension.JYTHON, null, -1, -1,
						"isCorrect()");
			} catch (BSFException ee) {
				String message = ee.getMessage();
				if (message.length() > 200) {
					message = message.substring(0, 200);
				}
				int option = JOptionPane.showConfirmDialog(
						(Component) PluginServices.getMainFrame(),
						PluginServices.getText(this, "error_expression") + "\n"
								+ message + "\n"
								+ PluginServices.getText(this, "continue?"));
				if (option != JOptionPane.OK_OPTION) {
					return false;
				}
			}
		}
		ies.startComplexRow();

		ArrayList exceptions = new ArrayList();
		interpreter.declareBean("exceptions", exceptions, ArrayList.class);
		FBitSet selection = sds.getSelection();
		if (selection.cardinality() > 0) {
			interpreter.declareBean("selection", selection, FBitSet.class);
			interpreter.exec(ExpressionFieldExtension.JYTHON, null, -1, -1,
					"def p():\n" + "  i=selection.nextSetBit(0)\n"
							+ "  while i >=0:\n" + "    indexRow.set(i)\n"
							+ "    obj=expression()\n"
							+ "    ee.setValue(obj,i)\n"
							+ "    ee.saveEdits(i)\n"
							+ "    i=selection.nextSetBit(i+1)\n");
		} else {
			interpreter.exec(ExpressionFieldExtension.JYTHON, null, -1, -1,
					"def p():\n" + "  for i in xrange(" + rowCount + "):\n"
							+ "    indexRow.set(i)\n"
							+
							// "    print i , expression() , repr (expression())\n"
							// +
							"    ee.setValue(expression(),i)\n"
							+ "    ee.saveEdits(i)\n");
		}
		try {
			interpreter.eval(ExpressionFieldExtension.JYTHON, null, -1, -1,
					"p()");
		} catch (BSFException ee) {

			JOptionPane.showMessageDialog(
					(Component) PluginServices.getMainFrame(),
					PluginServices.getText(this,
							"evaluate_expression_with_errors")
							+ " "
							+ (rowCount - indexRow.get())
							+ "\n"
							+ ee.getMessage());
		}

		ies.endComplexRow(PluginServices.getText(this, "expression"));

		return true;
	}

	public Table getTable() {
		return this.table;
	}

	public ArrayList<IOperator> getOperators() {
		return this.operators;
	}

	public FLyrVect getLayer() {
		return this.layer;
	}

	public BSFManager getInterpreter() {
		return this.interpreter;
	}
}