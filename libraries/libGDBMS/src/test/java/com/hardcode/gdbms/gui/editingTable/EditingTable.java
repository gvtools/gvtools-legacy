package com.hardcode.gdbms.gui.editingTable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
/**
 * @author Fernando González Cortés
 */
public class EditingTable extends JPanel {

	private JTable jTable = null;
	private JScrollPane jScrollPane = null;

	private DataSource ds;
	private DataWare dw = null;

	public void setDataSource(DataSource ds){
	    this.ds = ds;
	    MyCellEditor ce = new MyCellEditor();
	    getJTable().setModel(new DataSourceDataModel());
	    for (int i = 0; i < jTable.getColumnModel().getColumnCount(); i++) {
			jTable.getColumnModel().getColumn(i).setCellEditor(ce);
        }
	    getJTable().setCellEditor(ce);
	}

	/**
	 * This is the default constructor
	 */
	public EditingTable() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private  void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(300,200);
		this.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
	}
	/**
	 * This method initializes jTable
	 *
	 * @return javax.swing.JTable
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable() {
                protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                        int condition, boolean pressed) {
                    boolean value = super.processKeyBinding( ks, e,
                            condition, pressed );

                    // Make sure that the editor component has focus.
                    if ( isEditing() ) {
                        getEditorComponent().requestFocus();
                    }
                    return value;
                }
            };
		}
		return jTable;
	}
	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}


    /**
     * DOCUMENT ME!
     *
     * @author Fernando González Cortés
     */
    public class DataSourceDataModel extends AbstractTableModel {

        public DataSourceDataModel(){
            this.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int field = e.getColumn();
                    TableModel model = (TableModel)e.getSource();
                    String columnName = model.getColumnName(field);
                    Object data = model.getValueAt(row, field);
                    System.out.println(data);
                }
            });
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Value v;
            try {
                v = ValueFactory.createValueByType(aValue.toString(), ds.getFieldType(columnIndex));
                dw.setFieldValue(rowIndex, columnIndex, v);
            } catch (ReadDriverException e1) {
                throw new RuntimeException(e1);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (WriteDriverException e) {
            	 throw new RuntimeException(e);
			}
        }
        /**
         * DOCUMENT ME!
         *
         * @param col DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getColumnName(int col) {
            try {
                return ds.getFieldName(col).trim();
            } catch (ReadDriverException e) {
                return e.getMessage();
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int getColumnCount() {
            try {
                return ds.getFieldCount();
            } catch (ReadDriverException e) {
                return 0;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int getRowCount() {
            try {
                return (int) ds.getRowCount();
            } catch (ReadDriverException e) {
                return 0;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param row DOCUMENT ME!
         * @param col DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public Object getValueAt(int row, int col) {
            try {
                if (dw != null){
                    return dw.getFieldValue(row, col);
                }else{
                    return ds.getFieldValue(row, col);
                }
            } catch (ReadDriverException e) {
                return ValueFactory.createValue("").toString();
            }
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
    }


    /**
     * @throws DriverException
     *
     */
    public void startEditing() throws ReadDriverException {
        dw = ds.getDataWare(DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
        dw.start();
        dw.beginTrans();
    }

    public void commit() throws ReadDriverException, WriteDriverException {
        dw.commitTrans();
        dw.stop();
        dw = null;
    }

    public void rollBack() throws ReadDriverException, WriteDriverException {
        dw.rollBackTrans();
        dw.stop();
        dw = null;
    }

    public class MyCellEditor extends JTextField implements TableCellEditor {

        private ArrayList listeners = new ArrayList();
        private String initialValue;

        public MyCellEditor() {
            addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER){
                        stopCellEditing();
                    }else if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        cancelCellEditing();
                    }
                }
            });
        }

        /**
         * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
         */
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.setText(value.toString());
            return this;
        }

        /**
         * @see javax.swing.CellEditor#cancelCellEditing()
         */
        public void cancelCellEditing() {
            setText(initialValue);
            for (int i = 0; i < listeners.size(); i++) {
                CellEditorListener l = (CellEditorListener) listeners.get(i);
                ChangeEvent evt = new ChangeEvent(this);
                l.editingCanceled(evt);
            }
        }

        /**
         * @see javax.swing.CellEditor#stopCellEditing()
         */
        public boolean stopCellEditing() {
            for (int i = 0; i < listeners.size(); i++) {
                CellEditorListener l = (CellEditorListener) listeners.get(i);
                ChangeEvent evt = new ChangeEvent(this);
                l.editingStopped(evt);
            }

            return true;
        }

        /**
         * @see javax.swing.CellEditor#getCellEditorValue()
         */
        public Object getCellEditorValue() {
            return getText();
        }

        /**
         * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
         */
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        /**
         * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
         */
        public boolean shouldSelectCell(EventObject anEvent) {
            return false;
        }

        /**
         * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
         */
        public void addCellEditorListener(CellEditorListener l) {
            listeners.add(l);
        }

        /**
         * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
         */
        public void removeCellEditorListener(CellEditorListener l) {
            listeners.remove(l);
        }

    }

}
