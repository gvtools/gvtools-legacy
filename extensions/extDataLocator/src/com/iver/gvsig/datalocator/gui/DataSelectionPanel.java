/*
 * Created on 22-jun-2005
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package com.iver.gvsig.datalocator.gui;

import java.awt.FlowLayout;
import java.awt.event.ItemListener;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gvsig.gui.beans.swing.JButton;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.parser.ParseException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.IWindowListener;
import com.iver.andami.ui.mdiManager.SingletonWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.LayerCollection;
import com.iver.cit.gvsig.fmap.layers.layerOperations.Selectable;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.SelectedZoomVisitor;
import com.iver.gvsig.datalocator.DataLocatorExtension;

/**
 * @author jmorell
 */
public class DataSelectionPanel extends JPanel implements IWindow, IWindowListener, SingletonWindow {

	/**
     *
     */
    private static final long serialVersionUID = 1L;
    private JComboBox jComboBox = null;
	private JLabel jLabel = null;  //  @jve:decl-index=0:visual-constraint="597,16"
	private JLabel jLabel1 = null;  //  @jve:decl-index=0:visual-constraint="873,44"
	private JComboBox jComboBox1 = null;
	private JLabel jLabel2 = null;  //  @jve:decl-index=0:visual-constraint="847,16"
	private JComboBox jComboBox2 = null;
    private WindowInfo viewInfo = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private FLayer layerToZoom = null;
	private int fieldToZoomIndex = 0;
	private Value itemToZoom = null;
	private MapContext mapContext = null;
	private Preferences prefUsuario = null;
	private Vector layersListenerList = new Vector();
    private Vector vectorialLayers = null;
	private JCheckBox jChkBoxOpenFirstTime = null;
	private JPanel jPanelButtons = null;

	/**
	 * This method initializes
	 *
	 */
	public DataSelectionPanel(MapContext mapContext) {
		super();
		this.mapContext = mapContext;
		prefUsuario = Preferences.userRoot();
		initializeVectorialLayers();
        initializeLayerToZoom();
		initializeFieldToZoomIndex();
        initialize();

        int userOpen = prefUsuario.getInt("gvSIG.DataLocator.open_first_time", -1);
        if (userOpen == 1)
        	getJChkBoxOpenFirstTime().setSelected(true);
	}
    private void initializeVectorialLayers() {
    	unregisterLayersListener();
        vectorialLayers = new Vector();
        LayersIterator iter = DataLocatorExtension.newValidLayersIterator(mapContext.getLayers());


        while (iter.hasNext()) {
        	vectorialLayers.add(iter.nextLayer());
        }

        registerLayersListener();
    }
	private void initializeLayerToZoom() {
		String layerName = prefUsuario.get("LAYERNAME_FOR_DATA_LOCATION", "");
        if (layerName.equals("")) layerToZoom = (FLayer)vectorialLayers.get(0);
        boolean layerFound = false;
        for (int i=0;i<vectorialLayers.size();i++) {
            if (((FLayer)vectorialLayers.get(i)).getName().equals(layerName)) {
                layerFound = true;
                layerToZoom = (FLayer)vectorialLayers.get(i);
                break;
            }
        }
        if (!layerFound) layerToZoom = (FLayer)vectorialLayers.get(0);
        prefUsuario.put("LAYERNAME_FOR_DATA_LOCATION", layerToZoom.getName());

	}
	private void initializeFieldToZoomIndex() {
        fieldToZoomIndex = prefUsuario.getInt("FIELDINDEX_FOR_DATA_LOCATION", 0);
		AlphanumericData lyr = (AlphanumericData)layerToZoom;
		DataSource ds;
        try {
            ds = lyr.getRecordset();
            if (fieldToZoomIndex > (ds.getFieldCount()-1)) {
            	fieldToZoomIndex = 0;
            } else if (ds.getFieldCount() == 0) {
            	fieldToZoomIndex = -1;
            }
        } catch (ReadDriverException e) {
        	fieldToZoomIndex = -1;
            e.printStackTrace();
		}
	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
        jLabel2 = new JLabel();
        jLabel1 = new JLabel();
        jLabel = new JLabel();
        this.setLayout(null);
        this.setSize(350, 161);
        jLabel.setBounds(6, 6, 80, 23);
        jLabel.setText(PluginServices.getText(this,"Capa") + ":");
        jLabel1.setBounds(6, 34, 80, 23);
        jLabel1.setText(PluginServices.getText(this,"Campo") + ":");
        jLabel2.setBounds(6, 61, 80, 23);
        jLabel2.setText(PluginServices.getText(this,"Valor") + ":");
        this.add(getJComboBox(), null);
        this.add(jLabel, null);
        this.add(jLabel1, null);
        this.add(getJComboBox1(), null);
        this.add(jLabel2, null);
        this.add(getJComboBox2(), null);

        jPanelButtons = new JPanel();
        FlowLayout flowLayor = new FlowLayout(FlowLayout.RIGHT);
        flowLayor.setHgap(5);

        jPanelButtons.setLayout(flowLayor);
        jPanelButtons.setBounds(15,121,335,35);
        jPanelButtons.add(getJButton(), null);
        jPanelButtons.add(getJButton1(), null);

        this.add(jPanelButtons);

        this.add(getJChkBoxOpenFirstTime(), null);


	}
	private void registerLayersListener() {
		int i,j;
		FLayer layer;
		LayersListener listener;
		boolean found;
		for (i=0;i< vectorialLayers.size();i++) {
			found = false;
			layer = ((FLayer)vectorialLayers.get(i));
			for (j=0;j < layersListenerList.size(); j++) {
				listener = (LayersListener)layersListenerList.get(j);
				if ( layer.getParentLayer() == listener.getLayerCollection()) {
					found = true;
					break;
				}
			}
			if (!found) {
				listener = new LayersListener(layer.getParentLayer());
				layer.getParentLayer().addLayerCollectionListener(listener);
				layersListenerList.add(listener);
			}

		}
	}


	private void unregisterLayersListener() {
		int i;
		LayersListener listener;
		for (i=0;i<layersListenerList.size();i++) {
			listener = (LayersListener)layersListenerList.get(i);
			listener.getLayerCollection().removeLayerCollectionListener(listener);
		}
	}

	private String[] getLayerNames() {
		String[] layerNames = new String[vectorialLayers.size()];
	    for (int i=0;i<vectorialLayers.size();i++) {
	        layerNames[i] = ((FLayer)vectorialLayers.get(i)).getName();
	    }
	    return layerNames;
	}
	private String[] getFieldNames() {
		AlphanumericData lyr = (AlphanumericData)layerToZoom;
		DataSource ds;
        String[] fieldNames = null;
		try {
            ds = lyr.getRecordset();
			fieldNames = new String[ds.getFieldCount()];
			for (int i = 0; i < ds.getFieldCount(); i++) {
				fieldNames[i] = ds.getFieldName(i);
			}
        } catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fieldNames;
	}
	private Value[] getNewValues() {
		AlphanumericData lyr = (AlphanumericData)layerToZoom;
		DataSource ds;
		Value[] newValues = null;
		if (fieldToZoomIndex < 0)
			return null;
		try {
            ds = lyr.getRecordset();
            String sql = "select " + ds.getFieldName(fieldToZoomIndex) + " from " + ds.getName() + " where " + ds.getFieldName(fieldToZoomIndex) + " is not null;";
            ds = ds.getDataSourceFactory().executeSQL(sql, DataSourceFactory.AUTOMATIC_OPENING);

			//Quitar los nombres repetidos y ordenarlos
			TreeSet treeSet = new TreeSet(new Comparator() {
                public int compare(Object o1, Object o2) {
                    Value v1 = (Value) o1;
                    Value v2 = (Value) o2;
                    try {
                        if (((BooleanValue)v1.less(v2)).getValue()){
                            return -1;
                        }else if (((BooleanValue)v1.greater(v2)).getValue()){
                            return 1;
                        }else{
                            return 0;
                        }
                    } catch (IncompatibleTypesException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
			for (int i=0;i<ds.getRowCount();i++) {
			    Value value = ds.getFieldValue(i, 0);
			    treeSet.add(value);
			}
			newValues = (Value[])treeSet.toArray(new Value[0]);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DriverLoadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SemanticException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (EvaluationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return newValues;
	}
	private class LayersListener implements LayerCollectionListener {
		private LayerCollection theLayerCollection;
	    public LayersListener(FLayers layers){
	    	theLayerCollection = layers;
	    }

	    public LayerCollection getLayerCollection() {
	    	return theLayerCollection;
	    }
        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.layers.LayerCollectionListener#layerAdded(com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent)
         */
        public void layerAdded(LayerCollectionEvent e) {
            initializeVectorialLayers();
            ((ChangeLayerToZoomItemListener)jComboBox.getItemListeners()[0]).setLayers(vectorialLayers);
            jComboBox.removeAllItems();
		    DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(getLayerNames());
		    jComboBox.setModel(defaultModel);
			jComboBox.setSelectedItem(layerToZoom.getName());
        }
        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.layers.LayerCollectionListener#layerRemoved(com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent)
         */
        public void layerRemoved(LayerCollectionEvent e) {
            initializeVectorialLayers();
            ((ChangeLayerToZoomItemListener)jComboBox.getItemListeners()[0]).setLayers(vectorialLayers);
		    if (vectorialLayers.size()>0) {
	            jComboBox.removeAllItems();
				String[] layerNames = new String[vectorialLayers.size()];
			    boolean currentLayerRemoved = true;
				for (int i=0;i<vectorialLayers.size();i++) {
			        layerNames[i] = ((FLayer)vectorialLayers.get(i)).getName();
			        if (layerToZoom.getName().equals(layerNames[i])) currentLayerRemoved = false;
			    }
			    DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(layerNames);
			    jComboBox.setModel(defaultModel);
			    if (currentLayerRemoved) {
			        layerToZoom = ((FLayer)vectorialLayers.get(0));
	    			defaultModel = new DefaultComboBoxModel(getFieldNames());
	    		    jComboBox1.setModel(defaultModel);
	    		    fieldToZoomIndex = 0;
	    		    jComboBox1.setSelectedIndex(fieldToZoomIndex);
	    		    Value[] values =getNewValues();
	    			defaultModel = new DefaultComboBoxModel(values);
	    			jComboBox2.setModel(defaultModel);
	    			if (values != null) {
	    				itemToZoom = getNewValues()[0];
	    				jComboBox2.setSelectedItem(itemToZoom);
	    			}else {
	    				itemToZoom = null;
	    			}

			    }
			    jComboBox.setSelectedItem(layerToZoom.getName());
		    }else {
            	if (PluginServices.getMainFrame() == null)
            		((JDialog) (getParent().getParent().getParent().getParent())).dispose();
            	else
            		PluginServices.getMDIManager().closeWindow(DataSelectionPanel.this);
		    }
        }
        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.layers.LayerCollectionListener#layerMoved(com.iver.cit.gvsig.fmap.layers.LayerPositionEvent)
         */
        public void layerMoved(LayerPositionEvent e) {
            // TODO Auto-generated method stub

        }
        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.layers.LayerCollectionListener#layerAdding(com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent)
         */
        public void layerAdding(LayerCollectionEvent e) throws CancelationException {
            // TODO Auto-generated method stub

        }
        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.layers.LayerCollectionListener#layerMoving(com.iver.cit.gvsig.fmap.layers.LayerPositionEvent)
         */
        public void layerMoving(LayerPositionEvent e) throws CancelationException {
            // TODO Auto-generated method stub

        }
        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.layers.LayerCollectionListener#layerRemoving(com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent)
         */
        public void layerRemoving(LayerCollectionEvent e) throws CancelationException {
            // TODO Auto-generated method stub

        }
        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.layers.LayerCollectionListener#activationChanged(com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent)
         */
        public void activationChanged(LayerCollectionEvent e) throws CancelationException {
            // TODO Auto-generated method stub

        }
        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.layers.LayerCollectionListener#visibilityChanged(com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent)
         */
        public void visibilityChanged(LayerCollectionEvent e) throws CancelationException {
            // TODO Auto-generated method stub

        }
	}
    /* (non-Javadoc)
     * @see com.iver.andami.ui.mdiManager.View#getViewInfo()
     */
    public WindowInfo getWindowInfo() {
        if (viewInfo == null) {
            viewInfo=new WindowInfo(WindowInfo.MODELESSDIALOG | WindowInfo.PALETTE);
            viewInfo.setTitle(PluginServices.getText(this,"Localizador_por_atributo"));
            viewInfo.setHeight(getPreferredSize().height);
            viewInfo.setWidth(getPreferredSize().width);
        }
        return viewInfo;
    }
	/**
	 * This method initializes jComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
            DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(getLayerNames());
            jComboBox.setModel(defaultModel);
			jComboBox.setBounds(90, 6, 250, 23);
			jComboBox.setSelectedItem(layerToZoom.getName());
			ChangeLayerToZoomItemListener changeLayerToZoomItemListener = new ChangeLayerToZoomItemListener(vectorialLayers);
			jComboBox.addItemListener(changeLayerToZoomItemListener);
		}
		return jComboBox;
	}
	private class ChangeLayerToZoomItemListener implements ItemListener {
	    private Vector layers;
	    public ChangeLayerToZoomItemListener(Vector layers) {
	        this.layers = layers;
	    }
		public void itemStateChanged(java.awt.event.ItemEvent e) {
		    if (jComboBox.getItemCount()>0) {
                for (int i=0;i<layers.size();i++) {
                    if (((FLayer)layers.get(i)).getName().equals((String)jComboBox.getSelectedItem())) {
                        layerToZoom = (FLayer)layers.get(i);
                        break;
                    }
                }
    			fieldToZoomIndex = 0;
				prefUsuario.put("LAYERNAME_FOR_DATA_LOCATION", (String)jComboBox.getSelectedItem());
    		    DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(getFieldNames());
    		    jComboBox1.setModel(defaultModel);
    		    Value[] values =getNewValues();
    		    defaultModel = new DefaultComboBoxModel(values);
    			jComboBox2.setModel(defaultModel);
    			if ( values != null) {
    				jComboBox2.setSelectedIndex(0);
    				itemToZoom = values[0];
    			} else {
    				itemToZoom = null;
    			}
		    }
		}
        /**
         * @param layers The layers to set.
         */
        public void setLayers(Vector layers) {
            this.layers = layers;
        }
	}
	/**
	 * This method initializes jComboBox1
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox1() {
		if (jComboBox1 == null) {
			jComboBox1 = new JComboBox();
            DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(getFieldNames());
            jComboBox1.setModel(defaultModel);
			jComboBox1.setBounds(90, 34, 250, 23);
			jComboBox1.setSelectedIndex(fieldToZoomIndex);
			ChangeFieldItemListener changeFieldItemListener = new ChangeFieldItemListener(vectorialLayers);
			jComboBox1.addItemListener(changeFieldItemListener);
		}
		return jComboBox1;
	}
	private class ChangeFieldItemListener implements ItemListener {
	    public ChangeFieldItemListener(Vector layers) {
	    }
		public void itemStateChanged(java.awt.event.ItemEvent itemEvent) {
			String fieldToZoom = ((String)jComboBox1.getSelectedItem());
			AlphanumericData lyr = (AlphanumericData)layerToZoom;
			DataSource ds;
			try {
                ds = lyr.getRecordset();
    			for (int i=0;i<ds.getFieldCount();i++) {
    			    String fieldNamei = ds.getFieldName(i);
    			    if (fieldToZoom.equals(fieldNamei)) {
    			        fieldToZoomIndex = i;
    					prefUsuario.putInt("FIELDINDEX_FOR_DATA_LOCATION", i);
    			    }
    			}
                String sql = "select * from " + ds.getName() + " where " + ds.getFieldName(fieldToZoomIndex) + " is not null;";
                ds = ds.getDataSourceFactory().executeSQL(sql, DataSourceFactory.AUTOMATIC_OPENING);
    			Value[] values = new Value[(int)ds.getRowCount()];
    			for (int i=0;i<ds.getRowCount();i++) {
    			    Value value = ds.getFieldValue(i, fieldToZoomIndex);
                    values[i] = value;
    			}
    			//Quitar los nombres repetidos y ordenarlos
    			TreeSet treeSet = new TreeSet(new Comparator() {
                    public int compare(Object o1, Object o2) {
                        Value v1 = (Value) o1;
                        Value v2 = (Value) o2;
                        try {
                            if (((BooleanValue)v1.less(v2)).getValue()){
                                return -1;
                            }else if (((BooleanValue)v1.greater(v2)).getValue()){
                                return 1;
                            }else{
                                return 0;
                            }
                        } catch (IncompatibleTypesException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    			for (int i=0;i<values.length;i++) {
    			    treeSet.add(values[i]);
    			}
    			Value[] newValues = (Value[])treeSet.toArray(new Value[0]);
    		    DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(newValues);
    		    jComboBox2.setModel(defaultModel);
    		    if (newValues.length>0) jComboBox2.setSelectedIndex(0);
                if (newValues.length>0) {
                    itemToZoom = newValues[0];
                } else {
                    itemToZoom = null;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (DriverLoadException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SemanticException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch (EvaluationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * This method initializes jComboBox2
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox2() {
		if (jComboBox2 == null) {
			jComboBox2 = new JComboBox();
            DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(getNewValues());
            jComboBox2.setModel(defaultModel);
			jComboBox2.setSelectedIndex(-1);
			jComboBox2.setBounds(90, 61, 250, 23);
			ChangeItemToZoomItemListener changeItemToZoomItemListener = new ChangeItemToZoomItemListener(vectorialLayers);
			jComboBox2.addItemListener(changeItemToZoomItemListener);
		}
		return jComboBox2;
	}
	private class ChangeItemToZoomItemListener implements ItemListener {
	    private Vector layers;
	    public ChangeItemToZoomItemListener(Vector layers) {
	        this.layers = layers;
	    }
		public void itemStateChanged(java.awt.event.ItemEvent e) {
            for (int i=0;i<layers.size();i++) {
                if (((FLayer)layers.get(i)).getName().equals((String)jComboBox.getSelectedItem())) {
                    layerToZoom = (FLayer)layers.get(i);
                    break;
                }
            }
			itemToZoom = ((Value)jComboBox2.getSelectedItem());
		}
	}
	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			//jButton.setBounds(8, 121, 128, 23);
			jButton.setText(PluginServices.getText(this,"Zoom"));
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (layerToZoom == null || fieldToZoomIndex < 0 || itemToZoom == null)
						return;
				    if (jComboBox2.getSelectedIndex()!=-1) {
						AlphanumericData lyr = (AlphanumericData)layerToZoom;
						DataSource ds;
						Vector indices = new Vector();
						try {
			                ds = lyr.getRecordset();
							Selectable selectable = (Selectable)ds;
							FBitSet fBitSet = selectable.getSelection();

			    			for (int i=0;i<ds.getRowCount();i++) {
			    			    Value value = ds.getFieldValue(i, fieldToZoomIndex);
			    			    if (((BooleanValue)itemToZoom.equals(value)).getValue()) {
			    			        indices.add(new Integer(i));
			    			        fBitSet.set(i);
			    			    }
			    			}
							selectable.setSelection(fBitSet);
							SelectedZoomVisitor visitor = new SelectedZoomVisitor();
	                        ((VectorialData)layerToZoom).process(visitor);
				            mapContext.getViewPort().setExtent(visitor.getSelectBound());
				            selectable.clearSelection();

			            } catch (IncompatibleTypesException eeee) {
	                        // TODO Auto-generated catch block
	                        eeee.printStackTrace();
	                    } catch (ReadDriverException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (VisitorException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    } else if (itemToZoom == null) {
                        System.out.println("Localizador por atributo: El campo valor debe tener elementos no nulos para hacer el Zoom.");
                    } else {
				        System.out.println("Localizador por atributo: El campo valor debe estar inicializado antes de hacer Zoom.");
				    }
				}
			});
		}
		return jButton;
	}
	/**
	 * This method initializes jButton1
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			//jButton1.setBounds(141, 121, 128, 23);
			jButton1.setText(PluginServices.getText(this,"Salir"));
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
	            	if (PluginServices.getMainFrame() == null)
	            		((JDialog) (getParent().getParent().getParent().getParent())).dispose();
	            	else
	            		PluginServices.getMDIManager().closeWindow(DataSelectionPanel.this);
				}
			});
		}
		return jButton1;
	}
    /* (non-Javadoc)
     * @see com.iver.andami.ui.mdiManager.ViewListener#viewActivated()
     */
    public void windowActivated() {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see com.iver.andami.ui.mdiManager.ViewListener#viewClosed()
     */
    public void windowClosed() {
		unregisterLayersListener();
		// Guardamos la posici�n en las preferencias del usuario.
		WindowInfo vi = PluginServices.getMDIManager().getWindowInfo(this);
		prefUsuario.putInt("gvSIG.DataLocator.x", vi.getX());
		prefUsuario.putInt("gvSIG.DataLocator.y", vi.getY());
		prefUsuario.putInt("gvSIG.DataLocator.w", vi.getWidth());
		prefUsuario.putInt("gvSIG.DataLocator.h", vi.getHeight());
		vi.setClosed(true);
    }
    /* (non-Javadoc)
     * @see com.iver.andami.ui.mdiManager.SingletonView#getViewModel()
     */
    public Object getWindowModel() {
        // Debe devolver una cadena. Mirar Console del CorePlugin
        return "DataSelectionPanel";
    }
	/**
	 * This method initializes jChkBoxOpenFirstTime
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJChkBoxOpenFirstTime() {
		if (jChkBoxOpenFirstTime == null) {
			jChkBoxOpenFirstTime = new JCheckBox();
			jChkBoxOpenFirstTime.setBounds(new java.awt.Rectangle(90,89,179,23));
			jChkBoxOpenFirstTime.setText(PluginServices.getText(this, "open_first_time"));
			jChkBoxOpenFirstTime.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					if (jChkBoxOpenFirstTime.isSelected())
					{
						prefUsuario.putInt("gvSIG.DataLocator.open_first_time",1);
					}
					else
					{
						prefUsuario.putInt("gvSIG.DataLocator.open_first_time",0);
					}
				}
			});
		}
		return jChkBoxOpenFirstTime;
	}
	public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
