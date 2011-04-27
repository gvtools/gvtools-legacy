/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.Types;
import java.util.BitSet;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiFrame.MainFrame;
import com.iver.andami.ui.mdiManager.IWindowListener;
import com.iver.andami.ui.mdiManager.IWindowTransform;
import com.iver.andami.ui.mdiManager.SingletonWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.layers.CancelEditingLayerException;
import com.iver.cit.gvsig.exceptions.table.CancelEditingTableException;
import com.iver.cit.gvsig.exceptions.table.StartEditingTableException;
import com.iver.cit.gvsig.exceptions.table.StopEditingTableException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.DefaultRow;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.AfterFieldEditEvent;
import com.iver.cit.gvsig.fmap.edition.AfterRowEditEvent;
import com.iver.cit.gvsig.fmap.edition.BeforeFieldEditEvent;
import com.iver.cit.gvsig.fmap.edition.BeforeRowEditEvent;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IEditionListener;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.commands.CommandListener;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.SelectionEvent;
import com.iver.cit.gvsig.fmap.layers.SelectionListener;
import com.iver.cit.gvsig.project.documents.table.EditionTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.gui.tablemodel.ColumnModel;
import com.iver.cit.gvsig.project.documents.table.gui.tablemodel.DataSourceDataModel;
import com.iver.utiles.swing.jtable.FieldSelectionEvent;
import com.iver.utiles.swing.jtable.FieldSelectionListener;
import com.iver.utiles.swing.jtable.SelectionHeaderSupport;

/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class Table extends JPanel implements SingletonWindow, EditionTable,
    IWindowListener,CommandListener, IWindowTransform, IEditionListener {
    private static Logger logger = Logger.getLogger(Table.class.getName());
    private javax.swing.JScrollPane jScrollPane = null;
    protected javax.swing.JTable table = null;
    protected ProjectTable model = null;
    protected JLabel jLabelStatus = null;
    protected MapContext fmap;
    protected boolean updating = false;
    private TableSelectionListener selectionListener = new TableSelectionListener();
    private long numReg = 0;
    protected SelectionHeaderSupport headerSelectionSupport = new SelectionHeaderSupport();
//    private long[] orderIndexes = null;
    private long[] orderIndexesInverted = null;
    private IRow[] rowsCopied = null;
    private WindowInfo m_viewInfo = null;
	private boolean isPalette=false;
//	private String[] antAliases;
//	private int[] antMapping;
//	private FBitSet oldSelection;
	/**
     * This is the default constructor
     */
    public Table() {
        super();
        initialize();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ProjectTable getModel() {
        return model;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public BitSet getSelectedFieldIndices() {
    	BitSet bs=headerSelectionSupport.getSelectedColumns();
    	return bs;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getSelectedRowIndices() {
        return getTable().getSelectedRows();
    }

    /**
     * DOCUMENT ME!
     */
    protected void refreshControls() {
        try {
            MainFrame mF = PluginServices.getMainFrame();

            if (mF != null) {
            	String title = PluginServices.getText(this, "Tabla") + ": " + model.getName();
            	if (model.getJoinedTables()!=null) {
            		String[] joinedTables = model.getJoinedTables();
            		for (int i=0; i<joinedTables.length; i++) {
            			title = title + " X " + joinedTables[i];
            		}
            	}
            	PluginServices.getMDIManager().getWindowInfo(Table.this).setTitle(title);
            }

            if (model.getAssociatedTable() != null) {
                this.fmap = ((FLayer) model.getAssociatedTable()).getMapContext();
            } else {
                this.fmap = null;
            }

            SelectableDataSource dataSource = model.getModelo().getRecordset();
            dataSource.mapExternalFields();
            logger.debug("dataSource.start()");
            dataSource.start();

            ColumnModel cm=new ColumnModel(model);
            getTable().setColumnModel(cm);

            AbstractTableModel dataModel = new DataSourceDataModel(model);

            getTable().setModel(dataModel);

            TableColumn column = null;
			for (int i = 0; i < model.getMapping().length; i++) {
			    column = table.getColumnModel().getColumn(i);
			    int w=model.getColumn(i).getWidth();
			    column.setPreferredWidth(w); //sport column is bigger
//			    System.err.println("Table.Dentro de refreshControls. column=" + column.toString());

			}
			int columnCount=dataModel.getColumnCount();
            for (int i=0;i<columnCount;i++) {
            	if (getModel().getModelo().getRecordset().getFieldType(i)==Types.STRUCT) {
            		TableColumn tc=getTable().getColumnModel().getColumn(i);
        	        ValueComplexRenderer vcr=new ValueComplexRenderer();
            		tc.setCellRenderer(vcr);
            		ValueComplexEditor vce=new ValueComplexEditor();
            		tc.setCellEditor(vce);
            	}
            }
			headerSelectionSupport.setTableHeader(getTable().getTableHeader());
            headerSelectionSupport.addFieldSelectionListener(new FieldSelectionListener() {
                    public void fieldSelected(FieldSelectionEvent e) {
                        if (PluginServices.getMainFrame() != null) {
                            PluginServices.getMainFrame().enableControls();
                        }
                    }
                });

            model.getModelo().getRecordset().addSelectionListener(selectionListener);

            updateSelection();
        } catch (ReadDriverException e) {
        	  NotificationManager.addError("No se pudo leer la información", e);
		}
    }

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     */
    public void setModel(ProjectTable table) {
        model = table;

        //Gestión del nombre de la ventana
        model.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("name")) {
                    	String title = PluginServices.getText(this, "Tabla") + ": " + model.getName();
                    	if (model.getJoinedTables()!=null) {
                    		String[] joinedTables = model.getJoinedTables();
                    		for (int i=0; i<joinedTables.length; i++) {
                    			title = title + " X " + joinedTables[i];
                    		}
                    	}
                    	PluginServices.getMDIManager().getWindowInfo(Table.this).setTitle(title);

                    } else if (evt.getPropertyName().equals("model")) {
                        refreshControls();
                    }
                }
            });

        IEditableSource ies=getModel().getModelo();
        ies.addEditionListener(this);

        refreshControls();


    }
    /**
     *
     */
    public void updateSelection() {
        updating = true;
        try {
            DefaultListSelectionModel sm = (DefaultListSelectionModel) getTable()
                                                                           .getSelectionModel();
            sm.clearSelection();

            BitSet bs = (model.getModelo().getRecordset()).getSelection();
            sm.setValueIsAdjusting(true);

            if (model.getOrderIndexes() != null) {
//            	if (orderIndexesInverted==null) {
                	orderInverter(model.getOrderIndexes());
//                }
                for (int i = 0; i < model.getOrderIndexes().length; i++) {
                    if (bs.get(i)) {
                        sm.addSelectionInterval((int) orderIndexesInverted[i],
                            (int) orderIndexesInverted[i]);
                        if (isEditing())
                        	table.setEditingRow((int)orderIndexesInverted[i]);
                    }
                }
            } else {
            	for (int i = bs.nextSetBit(0); i >= 0;
	                     i = bs.nextSetBit(i + 1)) {
	                 sm.addSelectionInterval(i, i);
	                 if (isEditing())
	                  	table.setEditingRow(i);
	            }
            }

            sm.setValueIsAdjusting(false);
            numReg = model.getModelo().getRowCount();

            jLabelStatus.setText(" " +
                (model.getModelo().getRecordset()).getSelection()
                 .cardinality() + " / " + numReg + " " +
                PluginServices.getText(this, "registros_seleccionados_total") +
                ".");
            if (PluginServices.getMainFrame() != null)
            	PluginServices.getMainFrame().enableControls();
        } catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        updating = false;
    }

    /**
     * This method initializes this
     */
    protected void initialize() {
        jLabelStatus = new JLabel();
        setLayout(new java.awt.BorderLayout());
        jLabelStatus.setText("");
        jLabelStatus.setName("");
        jLabelStatus.setPreferredSize(new java.awt.Dimension(100, 18));
        add(getJScrollPane(), java.awt.BorderLayout.CENTER);
        this.add(jLabelStatus, java.awt.BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(300,200));
    }

    /**
     * This method initializes table
     *
     * @return javax.swing.JTable
     */
    public javax.swing.JTable getTable() {
        if (table == null) {
            table = new javax.swing.JTable();
            table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
            table.setSelectionModel(new DefaultListSelectionModel());
//            table.getTableHeader().addMouseListener(new MouseHandler());
            table.addKeyListener(new TableKeyListener());
            table.addMouseListener(new MouseRow());
            table.setSelectionForeground(Color.blue);
    		table.setSelectionBackground(Color.yellow);
    		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {

                    	if (updating) {
                         	return;
                        }

                        SelectableDataSource dataModel = null;

                        try {
                            dataModel = Table.this.model.getModelo()
                                                        .getRecordset();
                        } catch (ReadDriverException e1) {
							e1.printStackTrace();
						}

                        DefaultListSelectionModel model = (DefaultListSelectionModel) table.getSelectionModel();
                        BitSet selection = dataModel.getSelection();
                        int firstIndex=e.getFirstIndex();
                        if (firstIndex >= 0) {
								for (int i = firstIndex; i <= e.getLastIndex(); i++) {
									if (Table.this.model.getOrderIndexes() != null) {
										selection.set((int) Table.this.model
												.getOrderIndexes()[i], model
												.isSelectedIndex(i));
									} else {
										selection.set(i, model
												.isSelectedIndex(i));
									}
								}
							}
                        if (e.getValueIsAdjusting() == false) {
                            if (fmap != null) {
                                fmap.endAtomicEvent();
                            }

                            dataModel.fireSelectionEvents();
                        } else {
                            if (fmap != null) {
                                fmap.beginAtomicEvent();
                            }
                        }

                        jLabelStatus.setText(" " + selection.cardinality() +
                            " / " + numReg + " " +
                            PluginServices.getText(this,
                                "registros_seleccionados_total") + ".");

                    }
                });

       		JTextField tf=new JTextField();
       		tf.addFocusListener(new FocusListener() {
	        	  public void focusGained(FocusEvent e) {
	        		// TODO Auto-generated method stub

	        	}
	        	  public void focusLost(FocusEvent e) {
	        		  table.editingStopped(new ChangeEvent(table));
	        	}
	           });
    		table.setDefaultEditor(Object.class, new DefaultEditor(tf));
//            table.setDefaultEditor(String.class, new DefaultEditor(tf));
//            table.setDefaultEditor(Object.class, new DefaultEditor(tf));
//            table.setDefaultEditor(BooleanValue.class,new BooleanTableCellEditor(table));
//            table.setDefaultEditor(StringValue.class,new DefaultCellEditor(tf));
//            table.setDefaultEditor(Number.class,new IntegerTableCellEditor());

        }

        return table;
    }
    protected class DefaultEditor extends DefaultCellEditor{
		public Component getTableCellEditorComponent(javax.swing.JTable table,
				Object value, boolean isSelected, int row, int column) {
			JTextField tf=(JTextField)super.getTableCellEditorComponent(table, value, isSelected,
					row, column);
			if (isSelected){
				tf.setBackground(new Color(230,220,220));
				tf.selectAll();

			}
			return tf;
		}

		public DefaultEditor(JTextField tf) {
			super(tf);
		    tf.addMouseListener(new MouseRow());
		    getComponent().addKeyListener(new KeyAdapter() {
				int keyPressed = 0;

				public void keyPressed(KeyEvent ke) {
					if (ke.getKeyCode() != KeyEvent.VK_TAB)
						keyPressed++;
					JTextField tf = (JTextField) getComponent();


					if (ke.getKeyCode() == KeyEvent.VK_RIGHT
							|| ke.getKeyCode() == KeyEvent.VK_ENTER) {
						int caretPosition = tf.getCaretPosition();
						if (caretPosition >= tf.getText().length()) {
							int x = table.getSelectedColumn();
							int y = table.getSelectedRow();
							if (x + 1 >= table.getColumnCount()) {
								x = 0;
								y++;
							} else {
								x++;
							}
							getComponent().setEnabled(false);
						}

					} else if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
						int caretPosition = tf.getCaretPosition();
						if (caretPosition <= 0) {
							int x = table.getSelectedColumn();
							int y = table.getSelectedRow();
							if (x == 0) {
								x = table.getColumnCount() - 1;
								if (y - 1 < 0)
									y = table.getRowCount() - 1;
								else
									y--;
							} else {
								x--;
							}
							getComponent().setEnabled(false);
						}

					}
				}

				public void keyReleased(KeyEvent ke) {
					keyPressed--;
					if (keyPressed < 0)
						keyPressed = 0;
					getComponent().setEnabled(true);
				}
			});

		}

		public Object getCellEditorValue() {
			String s = ((JTextField) (DefaultEditor.this.getComponent()))
					.getText();
			getComponent().setEnabled(true);
			return s;
		}

		public boolean isCellEditable(EventObject event) {
			// IF NUMBER OF CLICKS IS LESS THAN THE CLICKCOUNTTOSTART RETURN
			// FALSE
			// FOR CELL EDITING.
			if (event instanceof MouseEvent) {
				return ((MouseEvent) event).getClickCount() >= getClickCountToStart();
			}

			return true;
		}
	}



    /**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
    protected javax.swing.JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new javax.swing.JScrollPane();
            jScrollPane.setViewportView(getTable());
        }

        return jScrollPane;
    }

    /**
	 * @see com.iver.mdiApp.ui.MDIManager.SingletonWindow#getWindowModel()
	 */
    public Object getWindowModel() {
        return model;
    }

    /**
	 * This method is used to get <strong>an initial</strong> ViewInfo object
	 * for this Table. It is not intended to retrieve the ViewInfo object in a
	 * later time. <strong>Use PluginServices.getMDIManager().getViewInfo(view)
	 * to retrieve the ViewInfo object at any time after the creation of the
	 * object.
	 *
	 * @see com.iver.mdiApp.ui.MDIManager.IWindow#getWindowInfo()
	 */
    public WindowInfo getWindowInfo() {
    	if (m_viewInfo==null) {
    		m_viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE |
    	            WindowInfo.MAXIMIZABLE | WindowInfo.RESIZABLE);
    		m_viewInfo.setTitle(PluginServices.getText(this, "Tabla")+ " : " +model.getName());
    		m_viewInfo.setWidth(300);
    		m_viewInfo.setHeight(200);
    	}
        return m_viewInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param indexes
     *
     * @throws IOException
     */
    public void setOrder(long[] indexes){
    	model.setOrderIndexes(indexes);
        orderInverter(indexes);

        updating = true;
        ((DataSourceDataModel) getTable().getModel()).fireTableDataChanged();
        updating = false;

        updateSelection();
    }

    private void orderInverter(long[] indexes) {
    	orderIndexesInverted = new long[indexes.length];

        for (int i = 0; i < indexes.length; i++) {
            orderIndexesInverted[(int) indexes[i]] = i;
        }
	}

	/**
     * Quita los campos seleccionados
     */
    public void clearSelectedFields() {
        headerSelectionSupport.clearSelectedColumns();
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Value[] getValueRow(int index) {
        DataSourceDataModel dsdm = (DataSourceDataModel) getTable().getModel();
        Value[] values = new Value[dsdm.getColumnCount()];

        for (int i = 0; i < dsdm.getColumnCount(); i++) {
            values[i] = (Value) dsdm.getValueAt(index, i);
        }

        return values;
    }

    /* private void refresh() throws DriverException{
       //dw.commitTrans();
       //model.getModelo().stop();
       //dw.beginTrans();
           //DataSourceDataModel dsdm=(DataSourceDataModel)getTable().getModel();
           //dsdm.fireTableDataChanged();
       }*/
    /*  public void addEmptyRow() throws DriverException{
       ValueCollection valuePK=new ValueCollection();
           valuePK.setValues(new Value[]{ValueFactory.createValue(dw.getRowCount())});
           dw.insertEmptyRow(valuePK);
           refresh();
       }
     */
    /*        public void copySelectedRows() throws DriverException{
       int[] sel=getSelectedRowIndices();
       for(int i=0;i<sel.length;i++){
               rowsCopied.add(getValueRow(sel[i]));
       }
       }
     */
    /*        public void addSelectionToEnd() throws DriverException {
       for (int i=0;i<rowsCopied.size();i++){
               dw.insertFilledRow((Value[])rowsCopied.get(i));
       }
       refresh();
       }
     */
    /*public void delSelectionRow() throws DriverException{
       int[] sel=getSelectedRowIndices();
       for(int i=sel.length-1;i>=0;i--){
               dw.deleteRow(sel[i]);
       }
       refresh();
       }
     */
    /*public boolean isCopy(){
       return !rowsCopied.isEmpty();
       }
     */
    /*
       public void addSelectionToRow() throws DriverException {
                   int[] sel=getSelectedRowIndices();
                           dw.insertFilledRow((Value[])rowsCopied.get(i),sel[0]);
                   refresh();
           }
     */
    public void startEditing() throws StartEditingTableException {
		try {
			getModel().getModelo().startEdition(EditionEvent.ALPHANUMERIC);
		} catch (StartWriterVisitorException e) {
			throw new StartEditingTableException(getName(), e);
		}
	}
    private void initEditField(int[] x, int[] y) {
		if (getTable().getRowCount() > 0) {
			if (isEditing()) {

				if (x.length == 1 && y.length == 1)
					table.editCellAt(y[0], x[0]);
				JTextField tf = (JTextField) table.getEditorComponent();
				if (tf != null) {
					tf.selectAll();
					tf.requestFocus();
				}
			}
		}
	}
    /**
	 * DOCUMENT ME!
	 */
    public void stopEditing() {
        try {
        	this.stopEditingCell();

            FLyrVect lyr = (FLyrVect) getModel().getAssociatedTable();

            if ((lyr != null) &&
                    lyr.getSource() instanceof VectorialEditableAdapter) {
                    VectorialEditableAdapter vea = (VectorialEditableAdapter) lyr.getSource();
                    ISpatialWriter spatialWriter = (ISpatialWriter) vea.getOriginalDriver();
                    vea.stopEdition(spatialWriter,EditionEvent.ALPHANUMERIC);
                    lyr.setSource(vea.getOriginalAdapter());
                    lyr.setEditing(false);
                    refreshControls();

            } else {

                 IEditableSource ies=getModel().getModelo();
                 if (ies instanceof IWriteable)
                 {
                	 IWriteable w = (IWriteable) ies;
	                 IWriter writer = w.getWriter();
	                 if (writer == null){
	                	 throw new StopEditingTableException(getName(),null);
	                 }else{
	     				ITableDefinition tableDef = ies.getTableDefinition();
	    				writer.initialize(tableDef);
	    				ies.stopEdition(writer,EditionEvent.ALPHANUMERIC);
	                	ies.getSelection().clear();
	                	refreshControls();
	                 }
                 }

                 /*
                GdbmsWriter gdbmswriter = new GdbmsWriter();
                gdbmswriter.setDataWare(getModel().getModelo()
                                                              .getRecordset()
                                                              .getDataWare(DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER));
                gdbmswriter.preProcess();

                for (int i = 0; i < getModel().getModelo().getRowCount();
                        i++) {
                    gdbmswriter.process(getModel().getModelo().getRow(i));
                }

                gdbmswriter.postProcess();
                */

            }

        } catch (Exception e) {
            NotificationManager.addError("No se pudo guardar la edición", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     */
    public void hideColumns(int[] index) {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     */
    public void setUneditableColumns(int[] index) {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT ME!
     *
     * @param numColumns DOCUMENT ME!
     * @param values DOCUMENT ME!
     */
    public void setDefaultValues(int[] numColumns, Value[] values) {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Value getDefaultValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void cancelEditing() throws CancelEditingTableException {
    	this.cancelEditingCell();
        try {
			getModel().getModelo().cancelEdition(EditionEvent.ALPHANUMERIC);
		} catch (CancelEditingLayerException e) {
			throw new CancelEditingTableException(getName(),e);
		}
//        if (antAliases != null)
//        {
//	        getModel().setAliases(antAliases);
//	        getModel().setMapping(antMapping);
//	        getModel().recalculateColumnsFromAliases();
//        }
//        else
//        {
//        	System.err.println("Algo ha ido mal con antAliases");
//        }
//        getModel().getModelo().getRecordset().setSelection(oldSelection);
        refreshControls();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isEditing() {
        return getModel().getModelo().isEditing();
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
    	int row=table.getSelectedRow();
    	int column=table.getSelectedColumn();
    	if (row!=-1 && column!=-1 && table.getEditorComponent()!=null){
    		Value[] values=getValueRow(row);
    		JTextField jtf=(JTextField)table.getEditorComponent();
    		jtf.setText(values[column].toString());
    	}
        updating = true;
        ((DataSourceDataModel) getTable().getModel()).fireTableDataChanged();

        updating = false;
        if (table.getModel().getRowCount()==1){
        	refreshControls();
        }else{
        	updateSelection();
        }
        PluginServices.getMainFrame().enableControls();

    }

    /**
     * Add the rows that are passed like parameter and if parameter is null a
     * row is added empties.
     *
     * @param rows Rows to add or null.
     * @throws ExpansionFileWriteException
     * @throws ReadDriverException
     * @throws ValidateRowException
     *
     * @throws DriverIOException
     * @throws IOException
     */
    public void addRow(IRow[] rows) throws ValidateRowException, ReadDriverException, ExpansionFileWriteException {
    	IEditableSource ies=getModel().getModelo();
    	if (rows == null) {

            	if (getModel().getAssociatedTable()!=null){
            		JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),"No se puede añadir una fila a una tabla asociada a una capa.");
            		return;
            	}
                IRow row;
                int numAttr=getModel().getModelo()
                	.getRecordset()
                	.getFieldCount();
            	Value[] values=new Value[numAttr];
            	for (int i=0;i<numAttr;i++){
            		values[i]=ValueFactory.createNullValue();
            	}
                row = new DefaultRow(values);
//TODO Lo cambio pq da problemas
//                ies.addRow(row,"Fila en blanco",EditionEvent.ALPHANUMERIC);
                ies.doAddRow(row, EditionEvent.ALPHANUMERIC);

            } else {
            	ies.startComplexRow();
                for (int i = 0; i < rows.length; i++) {
                   ies.addRow(((IRowEdited) rows[i]).getLinkedRow(),"Pegar filas",EditionEvent.ALPHANUMERIC);
                }
                String description=PluginServices.getText(this,"add_rows");
                ies.endComplexRow(description);
            }
        refresh();

    }

    /**
     * Copy in the arraylist the rows that be selected just then.
     * @throws ExpansionFileReadException
     * @throws ReadDriverException
     *
     * @throws DriverIOException
     * @throws IOException
     */
    public void copyRow() throws ReadDriverException, ExpansionFileReadException {
        int[] index = getSelectedRowIndices();
        rowsCopied = new IRow[index.length];

        for (int i = 0; i < index.length; i++) {
            rowsCopied[i] = getModel().getModelo().getRow(index[i]);
        }
    }

    /**
     * Cut the rows that be selected just then.
     * @throws ExpansionFileReadException
     * @throws ReadDriverException
     *
     * @throws DriverIOException
     * @throws IOException
     */
    public void cutRow() throws ReadDriverException, ExpansionFileReadException {
        int[] index = getSelectedRowIndices();
        rowsCopied = new IRow[index.length];

        for (int i = 0; i < index.length; i++) {
            rowsCopied[i] = getModel().getModelo().getRow(index[i]);
        }

        removeRow();
    }

    /**
     * Remove in the rows that be selected just then.
     * @throws ExpansionFileReadException
     * @throws ReadDriverException
     *
     * @throws DriverIOException
     * @throws IOException
     */

    public void removeRow() throws ReadDriverException, ExpansionFileReadException{
    	int[] index = getSelectedRowIndices();
        getModel().getModelo().startComplexRow();
        long[] orderIndexes=getModel().getOrderIndexes();
        if (orderIndexes!=null){
        	TreeSet<Integer> shorted = new TreeSet<Integer>(new Comparator<Integer>(){
        		public int compare(Integer o1, Integer o2) {
					return o2.compareTo(o1);
				}
        	});
        	for (int i=0;i<index.length;i++){
        		shorted.add(new Integer((int)orderIndexes[index[i]]));
        	}
        	Iterator<Integer> iterator=shorted.iterator();
        	while (iterator.hasNext()) {
				Integer integer = (Integer) iterator.next();
				getModel().getModelo().removeRow(integer.intValue(),"Eliminar fila", EditionEvent.ALPHANUMERIC);
			}
        }else{
        	for (int i = index.length-1;i>=0; i--) {
        		getModel().getModelo().removeRow((int)index[i],"Eliminar fila", EditionEvent.ALPHANUMERIC);
        	}
        }
        if (getTable().getCellEditor() != null)
        	getTable().getCellEditor().cancelCellEditing();
        String description=PluginServices.getText(this,"remove_rows");
        getModel().getModelo().endComplexRow(description);
        getTable().clearSelection();
        getModel().setOrderIndexes(null);
        refresh();
    }

    /**
     * DOCUMENT ME!
     */
    public void addColumn(FieldDescription newField) {
    	EditableAdapter edAdapter = (EditableAdapter) getModel().getModelo();
    		try {
				edAdapter.addField(newField);
			} catch (ReadDriverException e) {
				e.printStackTrace();
				NotificationManager.addError(e);
			}
			if (getTable().getCellEditor() != null)
				getTable().getCellEditor().cancelCellEditing();
	        getModel().setModel(edAdapter); // Para que se recalculen los campos. TODO: Limpiear todo esto
	        // refresh();
	        refreshControls();
	}

    /**
     * DOCUMENT ME!
     */
    public void removeColumn() {
    	EditableAdapter edAdapter = (EditableAdapter) getModel().getModelo();
    	try {
    		BitSet selectedFields = getSelectedFieldIndices();
    		SelectableDataSource sds = edAdapter.getRecordset();
    		edAdapter.startComplexRow();
    		FieldDescription[] auxFlds = sds.getFieldsDescription();
    		for(int i=selectedFields.nextSetBit(0); i>=0; i=selectedFields.nextSetBit(i+1)) {
    			FieldDescription fld = auxFlds[i];
    			edAdapter.removeField(fld.getFieldAlias());
    		}
    		if (getTable().getCellEditor() != null)
				getTable().getCellEditor().cancelCellEditing();

	        edAdapter.endComplexRow(PluginServices.getText(this, "remove_fields"));
	        clearSelectedFields();
	        getModel().setModel(edAdapter); // Para que se recalculen los campos. TODO: Limpiear todo esto
	        // refresh();
	        refreshControls();
    	} catch (ReadDriverException e) {
    		e.printStackTrace();
			NotificationManager.addError(e);
		} catch (WriteDriverException e) {
			e.printStackTrace();
			NotificationManager.addError(e);
		}

    }

    /**
     * Return if we have rows copied or not.
     *
     * @return True if we have rows copied.
     */
    public boolean isCopied() {
        return (rowsCopied != null);
    }

    /**
     * Paste the arraylist rows.
     * @throws ExpansionFileWriteException
     * @throws ReadDriverException
     * @throws ValidateRowException
     *
     * @throws DriverIOException
     * @throws IOException
     */
    public void pasteRow() throws ValidateRowException, ReadDriverException, ExpansionFileWriteException {
        addRow(rowsCopied);
        //repaintAssociatedView();
    }

    /**
     * DOCUMENT ME!
     */
    public void windowActivated() {
        //if (isEditing() && getModel().getModelo() instanceof VectorialEditableAdapter)
        updateSelection();
    }

    /**
     * DOCUMENT ME!
     */
    public void windowClosed() {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT ME!
     *
     * @author Vicente Caballero Navarro
     */
    public class TableSelectionListener implements SelectionListener {
        /**
         * @see com.iver.cit.gvsig.fmap.layers.LegendListener#selectionChanged(com.iver.cit.gvsig.fmap.layers.LayerEvent)
         */
        public void selectionChanged(SelectionEvent e) {
            updateSelection();
            Table.this.repaint();
            //((ValueComplexRenderer)Table.this.getTable().getColumnModel().getColumn(getSelectedFieldIndices().nextSetBit(0)).getCellRenderer()).getValue();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author Vicente Caballero Navarro
     */
//    private class MouseHandler extends MouseAdapter {
//        /**
//         * DOCUMENT ME!
//         *
//         * @param e DOCUMENT ME!
//         */
//        public void mouseClicked(MouseEvent e) {
//
//            /* JTableHeader h = (JTableHeader) e.getSource();
//               TableColumnModel columnModel = h.getColumnModel();
//               int viewColumn = columnModel.getColumnIndexAtX(e.getX());
//               int column = columnModel.getColumn(viewColumn).getModelIndex();
//               if (column != -1) {
//               }*/
//        }
//    }
    /**
     * DOCUMENT ME!
     *
     * @author Vicente Caballero Navarro
     */
    private class TableKeyListener implements KeyListener {

		public void keyPressed(KeyEvent arg0) {
			//JTextField tf=(JTextField)table.getEditorComponent();
			//table.setCellSelectionEnabled(true);
			//FocusManager fm = FocusManager.getCurrentManager();
			//fm.focusPreviousComponent(table);
		}

		public void keyReleased(KeyEvent ke) {
			int[] row=table.getSelectedRows();
		    int[] column=table.getSelectedColumns();
		    initEditField(column,row);
		}

		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub

		}

    }

    /**
     * DOCUMENT ME!
     *
     * @author Vicente Caballero Navarro
     */
    private class MouseRow extends MouseAdapter {
       	public void mouseReleased(MouseEvent arg0) {
			super.mouseReleased(arg0);
			int[] row=table.getSelectedRows();
		    int[] column=table.getSelectedColumns();
		    initEditField(column,row);
		}

		/**
         * DOCUMENT ME!
         *
         * @param e DOCUMENT ME!
         */
    /*    public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);


        	PluginServices.getMainFrame().enableControls();

            if (e.getButton() == MouseEvent.BUTTON3) {
                new PopupMenu(e.getPoint());
            }
        }
*/
    }

	public void commandRepaint() {
		refresh();
	}

	public void commandRefresh() {
		commandRepaint();
	}

	public void toPalette() {
		isPalette=true;
		m_viewInfo.toPalette(true);
		m_viewInfo.setClosed(false);
		PluginServices.getMDIManager().changeWindowInfo(this,getWindowInfo());
	}

	public void restore() {
		isPalette=false;
		m_viewInfo.toPalette(false);
		m_viewInfo.setClosed(false);
		PluginServices.getMDIManager().changeWindowInfo(this,getWindowInfo());
	}

	public boolean isPalette() {
		return isPalette;
	}

	public static void main(String[] args) {
		JTextField tf=new JTextField("hola");
		tf.selectAll();
		JFrame frame=new JFrame();
		frame.getContentPane().add(tf);
		frame.setVisible(true);
	}

	public void stopEditingCell() {
    	if (table.isEditing()) {

    		// TODO: finalizar la edicion de la columna
    		this.table.getCellEditor().stopCellEditing();
    		this.refresh();

    	}

	}

	public void cancelEditingCell() {
    	if (table.isEditing()) {
    		// TODO: finalizar la edicion de la columna
    		this.table.getCellEditor().cancelCellEditing();
    		this.refresh();
    	}

	}

	public void processEvent(EditionEvent e) {
		if (e.getChangeType() == EditionEvent.STOP_EDITION)
		{
			refreshControls();
		}if (e.getChangeType() == EditionEvent.CANCEL_EDITION){
			try {
				getModel().createAlias();
			} catch (ReadDriverException e1) {
				NotificationManager.showMessageError(
						PluginServices.getText(this, "Error creating table aliases"), e1);
			}
			refreshControls();
		}

	}

	public void beforeRowEditEvent(IRow feat, BeforeRowEditEvent e) {
		// TODO Auto-generated method stub

	}

	public void afterRowEditEvent(IRow feat, AfterRowEditEvent e) {
		// TODO Auto-generated method stub

	}

	public void beforeFieldEditEvent(BeforeFieldEditEvent e) {
		// TODO Auto-generated method stub

	}

	public void afterFieldEditEvent(AfterFieldEditEvent e) {
			try {
				getModel().createAlias();
			} catch (ReadDriverException e1) {
				NotificationManager.showMessageError(
						PluginServices.getText(this, "Error creating table aliases"), e1);
			}
			clearSelectedFields();
//			refresh();
			refreshControls();
		// ((DataSourceDataModel) getTable().getModel()).fireTableDataChanged();

	}

	public Object getWindowProfile() {
		return WindowInfo.EDITOR_PROFILE;
	}
}