package com.iver.cit.gvsig.project.documents.table.gui;

import java.sql.Types;
import java.util.BitSet;
import java.util.Date;
import java.util.prefs.Preferences;

import org.apache.bsf.BSFException;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.EditionUtilities;
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
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
/**
 * @author Vicente Caballero Navarro
 */
public class EvalExpression {
	private FieldDescription[] fieldDescriptors;
	private int selectedIndex;
	private FieldDescription fieldDescriptor;
	private FLyrVect lv;
	private  IEditableSource ies =null;
	private Table table=null;
	private static Preferences prefs = Preferences.userRoot().node( "fieldExpressionOptions" );
	private int limit;
	public EvalExpression() {
		limit=prefs.getInt("limit_rows_in_memory",-1);
	}
	public void setTable(Table table) {
		BitSet columnSelected = table.getSelectedFieldIndices();
        fieldDescriptors = table.getModel().getModelo().getFieldsDescription();
        selectedIndex = columnSelected.nextSetBit(0);
        fieldDescriptor = fieldDescriptors[selectedIndex];
        this.table=table;
        lv=(FLyrVect)table.getModel().getAssociatedTable();
        if (lv ==null)
            ies=table.getModel().getModelo();
        else
            ies = (VectorialEditableAdapter) lv.getSource();
	}
	 public void setValue(Object obj,int i) {
	    	//VectorialEditableAdapter vea = (VectorialEditableAdapter) lv.getSource();
	    	 Value value = getValue(obj);
	    	 //System.out.println("num = "+i);
	    	 IRow feat=null;
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
	        if (obj instanceof Number || obj instanceof Date || obj instanceof Boolean || obj instanceof String) {

	        }else{
	        	throw new BSFException("incorrect");
	        }
	 }


	 /**
	     * Returns the value created from object.
	     *
	     * @param obj value.
	     *
	     * @return Value.
	     */
	    private Value getValue(Object obj) {
	        int typeField = fieldDescriptor.getFieldType();
	        Value value = null;//ValueFactory.createNullValue();

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
	                if (((Number) obj).intValue()==0){
	                	value=ValueFactory.createValue(false);
	                }else{
	                	value=ValueFactory.createValue(true);
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
	        }else{
	        	value=ValueFactory.createNullValue();
	        }

	        return value;
	    }
	public FieldDescription getFieldDescriptorSelected() {
		return fieldDescriptor;
	}
	public FieldDescription[] getFieldDescriptors() {
		return fieldDescriptors;
	}
//	public void setFieldValue(Object obj,int i) {
//    	try {
//			((DBFDriver)table.getModel().getModelo().getOriginalDriver()).setFieldValue(i,selectedIndex,obj);
//		} catch (DriverLoadException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    }
	public void saveEdits(int numRows) throws ReadDriverException, InitializeWriterException, StopWriterVisitorException {
		if (limit==-1 || numRows == 0 || (numRows % limit)!=0) {
			return;
		}
		ies.endComplexRow(PluginServices.getText(this, "expression"));
		if ((lv != null) &&
                lv.getSource() instanceof VectorialEditableAdapter) {
                VectorialEditableAdapter vea = (VectorialEditableAdapter) lv.getSource();
                ISpatialWriter spatialWriter = (ISpatialWriter) vea.getDriver();
                vea.cleanSelectableDatasource();
         		lv.setRecordset(vea.getRecordset()); // Queremos que el recordset del layer
         		// refleje los cambios en los campos.
         		ILayerDefinition lyrDef = EditionUtilities.createLayerDefinition(lv);
         		spatialWriter.initialize(lyrDef);
         		vea.saveEdits(spatialWriter,EditionEvent.ALPHANUMERIC);
         		vea.getCommandRecord().clearAll();
         } else {
              if (ies instanceof IWriteable){
             	 IWriteable w = (IWriteable) ies;
	                 IWriter writer = w.getWriter();
	                 if (writer == null){
	                 }else{
	     				ITableDefinition tableDef = ies.getTableDefinition();
	    				writer.initialize(tableDef);

	    				ies.saveEdits(writer,EditionEvent.ALPHANUMERIC);
	                	ies.getSelection().clear();
	                 }
              }
              ies.getCommandRecord().clearAll();
         }
		ies.startComplexRow();
    	 table.refresh();
    }
}
