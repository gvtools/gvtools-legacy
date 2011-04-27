/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
*
* $Id: 
* $Log: 
*/
package org.gvsig.topology.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.topology.ui.util.GUIUtil;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiFrame.MDIFrame;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * This panel manages the selection of layers in TOC.
 * Allows to add layers from the toc, remove, and shows a dialog
 * with toc layers.
 * @author Alvaro Zabala
 *
 */
public class LayerSelectionPanel extends JPanel {

	private static final long serialVersionUID = 6171285621323810490L;
	
	/**
	 * text in the header of the component
	 */
	private String title;
	/**
	 * Component to show the available layers in a table
	 */
	private JTable lyrsTable;
	
	/**
	 * Layers selected in the component's table
	 */
	private List<FLayer> layers;
	
	/**
	 * MapContext of the active view
	 */
	private MapContext mapContext;
	
	/**
	 * Contains listeners interested in the addition or removing of layers
	 * to this component.
	 */
	private List<LayerSelectionListener> selectionListeners;
	
	private boolean readOnly;

	/**
	 * Constructor.
	 * 
	 * @param layers List of layers to initialize the table content
	 */
	public LayerSelectionPanel(List<FLayer> layers, MapContext mapContext){
		this(layers, mapContext, false);
	}
	
	public LayerSelectionPanel(List<FLayer> layers, MapContext mapContext, boolean readOnly){
		super();
		this.layers = layers;
		this.mapContext = mapContext;
		this.readOnly = readOnly;
		selectionListeners = new ArrayList<LayerSelectionListener>();
		initialize();
	}
	
	
	/**
	 * Returns the list of layers in the component's table
	 * @return
	 */
	public List<FLayer> getLayers(){
		return layers;
	}
	
	public void addLayerSelectionListener(LayerSelectionListener selectionListener){
		selectionListeners.add(selectionListener);
	}
	
	private void fireLayerSelectionEvent(SelectedLayerEvent event){
		for(int i = 0; i < selectionListeners.size(); i++){
			LayerSelectionListener listener = selectionListeners.get(i);
			listener.selectionEvent(event);
		}
	}
	
	
	private void initialize(){
		setLayout(new BorderLayout());
		JLabel titleLbl = new JLabel(PluginServices.getText(null,title));
		add(titleLbl, BorderLayout.NORTH);

		TableModel dataModel = new AbstractTableModel() {
			public int getColumnCount() {
				return 1;
			}

			public int getRowCount() {
				return layers.size();
			}

			public Object getValueAt(int row, int col) {
				FLayer lyr = layers.get(row);
				return lyr.getName();
			}
		};
		lyrsTable = new JTable();
		lyrsTable.setModel(dataModel);
		lyrsTable.getColumnModel().getColumn(0).setHeaderValue(
				PluginServices.getText(null, "Layers"));
		JScrollPane scrollTable = new JScrollPane(lyrsTable);
		add(scrollTable, BorderLayout.CENTER);
		
		if(! readOnly){
			add(getButtonsPanel(), BorderLayout.SOUTH);
		}//if ! readOnly
		
	}
	
	/**
	 * Creates JPanel with add layer, remove layer, remove all layers buttons.
	 * @return
	 */
	private JPanel getButtonsPanel(){
		JButton addButton = new JButton(PluginServices.getText(null, "Add_Lyr"));
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				LayersInTocPanel lyrsInTocPanel = new LayersInTocPanel(mapContext.getLayers(), layers);
				final JDialog dialog = new JDialog();
				dialog.setLayout(new BorderLayout());
				
				dialog.getContentPane().add(lyrsInTocPanel, BorderLayout.CENTER);
				
				final LayersInTocPanel tempPanel = lyrsInTocPanel;
				ActionListener okAction = new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						List<FLyrVect> newLyrs = tempPanel.getSelectedLyrs();
						for(int i = 0; i < newLyrs.size(); i++){
							FLyrVect lyr = newLyrs.get(i);
							if(!layers.contains(lyr)){//FIXME La capa que se añada aquí hay que quitarla de la FLayers a la que pertenezca
//								layers.add(lyr);
								SelectedLayerEvent event = new SelectedLayerEvent();
								event.lyrOfEvent = lyr;
								event.eventType = SelectedLayerEvent.ADDED_EVENT_TYPE;
								fireLayerSelectionEvent(event);
								
							}//if
						}//for
						lyrsTable.revalidate();
						dialog.dispose();
					}};
				ActionListener cancelAction = new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}};
				
				AcceptCancelPanel acceptCancelPnl = new AcceptCancelPanel(okAction, cancelAction);
				dialog.getContentPane().add(acceptCancelPnl, BorderLayout.SOUTH);
				dialog.setTitle(PluginServices.getText(this, "Capas_A_Añadir_A_La_Topologia"));
				dialog.setModal(true);
				dialog.pack();
				GUIUtil.getInstance().centerDialog(dialog, (MDIFrame) PluginServices.getMainFrame());
				dialog.setVisible(true);	
			}});
		
		JButton removeButton = new JButton(PluginServices.getText(this, "Remove_Lyr"));
		removeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int selectedLyr = lyrsTable.getSelectedRow();
				SelectedLayerEvent event = new SelectedLayerEvent();
				event.lyrOfEvent = layers.get(selectedLyr);
				event.eventType = SelectedLayerEvent.REMOVED_EVENT_TYPE;
				fireLayerSelectionEvent(event);
//				layers.remove(selectedLyr);
				lyrsTable.revalidate();
			}});
		
		JButton removeAllBtn = new JButton(PluginServices.getText(this, "Remove_All"));
		removeAllBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < layers.size(); i++){
					FLayer lyr = layers.get(i);
					SelectedLayerEvent event = new SelectedLayerEvent();
					event.lyrOfEvent = lyr;
					event.eventType = SelectedLayerEvent.REMOVED_EVENT_TYPE;
					fireLayerSelectionEvent(event);
				}
//				layers.clear();
				lyrsTable.revalidate();
			}
		});

		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(removeAllBtn);
		return buttonPanel;
	}
	
	/**
	 * Subclasses of this interface listen for events launched by this control
	 * 
	 * @author Alvaro Zabala
	 *
	 */
	public interface LayerSelectionListener{
		public void selectionEvent(SelectedLayerEvent event);
	}
	/**
	 * Event launched when this control adds or removes a layer.
	 * @author Alvaro Zabala
	 */
	class SelectedLayerEvent{
		final static int ADDED_EVENT_TYPE = 0;
		final static int REMOVED_EVENT_TYPE = 1;
		
		FLayer lyrOfEvent;
		int eventType;
	}
	
	
}
