package org.gvsig.tableExport.xls;

import java.awt.Component;
import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import jxl.JXLException;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.DoubleValue;
import com.hardcode.gdbms.engine.values.FloatValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.utiles.GenericFileFilter;

public class ExportTableToExcelExtension extends Extension {

	private WritableCellFormat floatFormat = new WritableCellFormat(
			NumberFormats.FLOAT);
	private WritableCellFormat intFormat = new WritableCellFormat(
			NumberFormats.INTEGER);

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();
		Table table = (Table) v;
		try {
			exportToXLS(table);
		} catch (Exception e) {
			NotificationManager.showMessageError("error_en_el_proceso", e);
		}

	}

	/**
	 * DOCUMENT ME!
	 */
	public void exportToXLS(Table table) throws Exception {
		File file = this.askForFileName("xls",
				PluginServices.getText(this, "Ficheros_XLS"));
		if (file == null) {
			return;
		}
		exportToFile(table, file);
	}

	public void exportToFile(Table table, File trgfile) throws Exception {
		SelectableDataSource source;
		ITableDefinition orgDef;
		try {
			source = table.getModel().getModelo().getRecordset();
			source.start();
			orgDef = table.getModel().getModelo().getTableDefinition();
		} catch (Exception e) {
			throw new Exception("Error preparando la fuente", e); // TODO
																	// intenacionalizacion??
		}

		File file = new File(trgfile.getAbsoluteFile() + ".tmp");

		WritableWorkbook workbook = Workbook.createWorkbook(file);
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);

		writeHeader(sheet, orgDef);

		try {
			SourceIterator iter = new SourceIterator(source);
			Value[] values;
			Value value;
			int row = 1;
			int col;
			while (iter.hasNext()) {
				values = iter.nextValues();
				for (col = 0; col < values.length; col++) {
					value = values[col];
					if (!(value instanceof NullValue)) {
						sheet.addCell(getCell(row, col, value));
					}
				}
				row++;

			}

			workbook.write();

			workbook.close();

		} catch (Exception e) {
			throw new Exception("Error generando fichero", e); // TODO
																// intenacionalizacion??

		}

		file.renameTo(trgfile);

		try {
			source.stop();

		} catch (Exception e) {
			throw new Exception("Error cerrando ficheros", e); // TODO
																// intenacionalizacion??
		}

	}

	private WritableCell getCell(int row, int col, Value value) {
		if (value instanceof NumericValue) {
			if (value instanceof DoubleValue || value instanceof FloatValue) {
				return new Number(col, row,
						((NumericValue) value).doubleValue(), floatFormat);
			} else {
				return new Number(col, row, ((NumericValue) value).longValue(),
						intFormat);
			}
		} else {
			return new Label(col, row, value.toString());
		}

	}

	private void writeHeader(WritableSheet sheet, ITableDefinition orgDef)
			throws JXLException {
		FieldDescription[] fields = orgDef.getFieldsDesc();
		FieldDescription field;
		Label cell;
		int col;
		for (col = 0; col < fields.length; col++) {
			field = fields[col];
			cell = new Label(col, 0, field.getFieldName());
			sheet.addCell(cell);
		}
	}

	private File askForFileName(String ext, String extDescription) {
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(new GenericFileFilter(ext, extDescription));
		if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (file == null) {
				return null;
			}
			if (file.exists()) {
				int resp = JOptionPane.showConfirmDialog(
						(Component) PluginServices.getMainFrame(),
						PluginServices.getText(this,
								"fichero_ya_existe_seguro_desea_guardarlo"),
						PluginServices.getText(this, "guardar"),
						JOptionPane.YES_NO_OPTION);
				if (resp != JOptionPane.YES_OPTION) {
					return null;
				}
			}
			String name = file.getAbsolutePath();
			if (!name.toLowerCase().endsWith("." + ext.toLowerCase())) {
				file = new File(name + "." + ext);
			}
			return file;
		} else {
			return null;
		}

	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		} else if (v instanceof Table) {
			return true;
		} else {
			return false;
		}
	}

	private class SourceIterator implements Iterator {
		private int index;
		private long count;
		private FBitSet selection = null;
		private SelectableDataSource source;

		public SourceIterator(SelectableDataSource source)
				throws DriverException, ReadDriverException {
			this.source = source;
			if (source.getSelection().cardinality() == 0) {
				this.selection = null;
				this.index = 0;
				this.count = source.getRowCount();
			} else {
				this.selection = source.getSelection();
				this.index = selection.nextSetBit(0);
				this.count = selection.cardinality();
			}

		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			if (this.selection == null)
				return this.index < this.count;
			return this.index >= 0;
		}

		public Object next() {
			try {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return this.nextValues();
			} catch (DriverException e) {
				throw new RuntimeException(e);
			} catch (ReadDriverException e) {
				throw new RuntimeException(e);
			}
		}

		public Value[] nextValues() throws DriverException, ReadDriverException {

			Value[] values = this.source.getRow(this.index);
			if (this.selection == null) {
				this.index++;
			} else {
				this.index = this.selection.nextSetBit(this.index + 1);
			}
			return values;

		}
	}
}
