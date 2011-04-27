/*
 * Created on 27-oct-2006
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
* $Id: LayerSelectionPanel.java 31783 2009-12-15 12:26:56Z fpenarrubia $
* $Log$
* Revision 1.4  2007-09-07 11:29:47  fjp
* Casi compila. Falta arreglar lo de FArrowSymbol y retocar el graphiclist de FMap.
*
* Revision 1.3  2006/11/08 19:32:44  azabala
* *** empty log message ***
*
* Revision 1.2  2006/11/03 19:39:29  azabala
* *** empty log message ***
*
* Revision 1.1  2006/10/27 18:26:07  azabala
* *** empty log message ***
*
*
*/
package org.gvsig.graph.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;

/**
 * Component that shows in a combobox all the TOC's layers, and allows
 * user to select one of them.
 * 
 * It optionally allows to filter layers for a given geometry type.
 * 
 * 
 * */
public class LayerSelectionPanel extends JPanel implements IWindow{

	private GridBagLayoutPanel contentPanel;
	
	FLayers layers = null;
	JComboBox layersComboBox = null;
	AcceptCancelPanel buttonPanel = null;
	
	
	private ActionListener okActionListener = new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			wasFinishPressed = true;
			closeParent(LayerSelectionPanel.this);
		}
	};

	private ActionListener cancelActionListener = new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			wasFinishPressed = false;
			closeParent(LayerSelectionPanel.this);
		}
	};
	String title = null;
	String introductoryText = null;
	
	boolean wasFinishPressed = false;
	
	/**
	 * Optinal filter to show in the combo box layers of a given
	 * geometry typ0e
	 * */
	ArrayList<Integer> geometryType = new ArrayList<Integer>();
	
	/**
	 * If it is not null, we'll filter layers to only show
	 * types of this class
	 * */
	Class layerType;
	private WindowInfo wi;
	
	
	
	public LayerSelectionPanel(FLayers tocLayers, 
			String title, 
			String introductoryText){
		this(tocLayers, title, introductoryText, (Class) null, -1);
		
	}
	
	public LayerSelectionPanel(FLayers layers, String title, 
				String introductoryText, Class class1, int geometryType) {
		super();
		this.layers = layers;
		this.introductoryText = introductoryText;
		this.layerType = class1;
		this.geometryType.add(geometryType);
		initialize();
	}

	public void addGeometryTypeConstraint(int geometryType){
		if(layerType == null || !(layerType.isAssignableFrom(FLyrVect.class)))
			return;
		this.geometryType.add(geometryType);
		updateLayerCombo();
	}
	
	public void setLayerTypeConstraint(Class layerClass){
		this.layerType = layerClass;
	}
		
	
	public FLayer getLayer() {
		String selectedLayer = (String)layersComboBox.getSelectedItem();
		if(selectedLayer != null){
			if(!(selectedLayer.trim().equals(""))) {
				// TODO: Creo que sería conveniente revisar getLayer para que tenga en cuenta los grupos de capas.
				// Por ahora, lo corrijo aquí solo
				SingleLayerIterator lyrIterator = new SingleLayerIterator(layers);
				while (lyrIterator.hasNext()) {
					FLayer aux = lyrIterator.next();
					if (aux.getName().equalsIgnoreCase(selectedLayer))
						return aux; 
				}
				return null;
			}
		}
        
        return null;
	}
	
	public void setFLayers(FLayers layers){
		this.layers = layers;
		updateLayerCombo();
	}
	
	public void initialize(){
		setLayout(new BorderLayout());
		contentPanel = new GridBagLayoutPanel();
		contentPanel.addComponent(new JLabel(introductoryText), GridBagConstraints.BOTH);
		JComboBox comboBox = getLayersComboBox();
		contentPanel.addComponent(PluginServices.getText(null, "layers") + ":", comboBox);
		add(contentPanel, BorderLayout.NORTH);
		add(getButtonPanel(), BorderLayout.SOUTH);
		
	}
	
	AcceptCancelPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new AcceptCancelPanel();
			buttonPanel.setOkButtonActionListener(okActionListener);
			buttonPanel.setCancelButtonActionListener(cancelActionListener);
		}
		return buttonPanel;
	}
	
	
	void updateLayerCombo(){
		DefaultComboBoxModel defaultModel = 
        	new DefaultComboBoxModel(getLayerNames());
        layersComboBox.setModel(defaultModel);
	}
	
	public boolean wasFinishPressed(){
		return wasFinishPressed;
	}
	
	protected FLayer[] getLayerForType(FLayers layers){
		FLayer[] solution = null;
		if(layers != null ){
			SingleLayerIterator lyrIterator = new SingleLayerIterator(layers);
			ArrayList list = new ArrayList();
			while (lyrIterator.hasNext()) {
				FLayer layer = lyrIterator.next();
				if (!layer.isAvailable())
					continue;
				if(layerType != null){
					if(!layer.getClass().isAssignableFrom(layerType))
						continue;
					if(layer instanceof FLyrVect){
						FLyrVect lyrVect = (FLyrVect) layer;
						try {
							// Para que no salte el error por culpa del AutomaticDataSource.
							// Nota: no lo cierro aposta.
							if (lyrVect.getSource() == null)
								continue;
							lyrVect.getSource().start();
							for (int j=0; j < geometryType.size(); j++) {
								if(lyrVect.getShapeType() == geometryType.get(j))
									list.add(layer);
							}
						} catch (ReadDriverException e) {
							continue;
						}
					}//if	
				}//if
			}//while
			solution = new FLyrVect[list.size()];
			list.toArray(solution);
		}//if
		
		return solution;
	}
	
	protected String[] getLayerNames() {
		String[] solution = null;
		ArrayList nameList = new ArrayList();
		FLayer[] layers = getLayerForType(this.layers);
		for(int i = 0; i < layers.length; i++){
			nameList.add(layers[i].getName());
		}
		solution = new String[nameList.size()];
		nameList.toArray(solution);
		return solution;
	}
	
	private JComboBox getLayersComboBox() {
		if (layersComboBox == null) {
            layersComboBox = new JComboBox();
		}
		updateLayerCombo();
		return layersComboBox;
	}
	
	public static void closeParent(JPanel component){
		Container container = component.getParent();
		Container parentOfContainer = null;
		while(! (container instanceof Window)){
			parentOfContainer = container.getParent();
			container = parentOfContainer;
		}
		((Window)container).dispose();
	}
	
	
	
	public WindowInfo getWindowInfo() {
		  if (wi==null) {
	            wi = new WindowInfo(WindowInfo.MODALDIALOG);
	            wi.setWidth(350);
	            wi.setHeight(120);
	            wi.setTitle(title);
	        }
	        return wi;
	}
	public Object getWindowProfile(){
		return WindowInfo.DIALOG_PROFILE;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"

