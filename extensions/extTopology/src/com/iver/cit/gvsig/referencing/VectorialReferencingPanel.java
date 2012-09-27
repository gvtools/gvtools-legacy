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
package com.iver.cit.gvsig.referencing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.vecmath.MismatchedSizeException;

import org.geotools.math.Statistics;
import org.geotools.referencing.operation.builder.MathTransformBuilder;
import org.gvsig.referencing.MappedPositionContainer;
import org.gvsig.topology.ui.util.GUIUtil;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import sun.awt.VerticalBagLayout;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.referencing.TransformationsRegistry.TransformationRegistryEntry;

/**
 * Referencing panel. GUI component which allows user to specify the needed params
 * to apply a transformation to a vectorial layer. 
 * @author Alvaro Zabala
 */
public class VectorialReferencingPanel extends AbstractGeoprocessGridbagPanel implements IWindow {

	private static final long serialVersionUID = 1749313592205873002L;
	
	//TODO Sustituir esto por puntos de extension
	
	//least squares methods
	
	
	private MappedPositionContainer linksList;
	/**
	 * Current view when user opened this referencing panel
	 */
	private MapControl currentView;
	
	
	
	/*
	 * GUI
	 * */
	private String controlPointsTitle;
	private JScrollPane scrollView;
	private JPanel mappedPositionContainer;
	private JButton addMappedPositionBtn; 
	
	private String transformationTitle;
	private JComboBox transformationOptionCb;
	private JButton transformButton;

	private WindowInfo viewInfo;
	

	
	
	public VectorialReferencingPanel(FLayers lyrs){
		super(lyrs, PluginServices.getText(null, "referencing_vectorial_layers"));
	}
		
	
	protected void addSpecificDesign() {
		
		IWindow f = PluginServices.getMDIManager().getActiveWindow();
		View vista = (View) f;
		if (f != null) {
			MapControl mapControl = vista.getMapControl();
			currentView = mapControl;
		}
		controlPointsTitle = PluginServices.getText(null, "Control_points");
		transformationTitle = PluginServices.getText(null, "Transform");
		linksList = new DisactivableMappedPositionContainerImpl();
		
		JLabel controlPointsLbl = new JLabel(controlPointsTitle);
		addComponent(controlPointsLbl);
		
		scrollView = new JScrollPane();
		mappedPositionContainer = getMappedPositionContainer();
		scrollView.setViewportView(mappedPositionContainer);
		scrollView.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		scrollView.setPreferredSize(new Dimension(600, 160));
		scrollView.setMinimumSize(new Dimension(600, 120));
		scrollView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		addComponent(scrollView);
		addComponent(getAddMappedPositionBtn());
		
		JLabel transformTitleLbl = new JLabel(transformationTitle);
		addComponent(transformTitleLbl);
		addComponent(getTransformationOptionCb());
		addComponent(getTransformBtn());
	}
	

	protected void processLayerComboBoxStateChange(ItemEvent e) {
		
		
	}
	
	private JPanel getMappedPositionContainer(){
		if(mappedPositionContainer == null){
			mappedPositionContainer = new JPanel(new VerticalBagLayout());
			
			mappedPositionContainer.add(new MappedPositionPanel(linksList, currentView));
		}
		return mappedPositionContainer;
	}
	
	private JButton getAddMappedPositionBtn(){
		if(addMappedPositionBtn == null){
			addMappedPositionBtn = new JButton(PluginServices.getText(this, "Add_Vector"));
			addMappedPositionBtn.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent arg0) {
					MappedPositionPanel newPanel = new MappedPositionPanel(linksList, currentView);
					mappedPositionContainer.add(newPanel);
					scrollView.repaint();
					scrollView.revalidate();
					mappedPositionContainer.repaint();
					scrollView.revalidate();
				}});
		}
		return addMappedPositionBtn;
	}
		
		
	private JButton getTransformBtn(){
		if(transformButton == null){
			transformButton = new JButton(PluginServices.getText(this, "transform_lyr"));
			transformButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					try {
						MathTransformBuilder transformBuilder =
							createTransformBuilder();
						MathTransform mathTransform = transformBuilder.getMathTransform();
						Statistics statistics = transformBuilder.getErrorStatistics();
						statistics.rms();
						
						
					}//TODO MOSTRAR MENSAJES DE ERROR PERSONALIZADOS 
					catch (MismatchedDimensionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MismatchedReferenceSystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MismatchedSizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FactoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TransformException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});//addActionListener
		}
		return transformButton;
	}
			
	
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODELESSDIALOG
					| WindowInfo.RESIZABLE | WindowInfo.PALETTE);
			viewInfo.setTitle(PluginServices.getText(null, "referencing_vectorial_layers"));
			viewInfo.setWidth(700);
			viewInfo.setHeight(465);
		}
		return viewInfo;
	}
	public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}
	
	private JComboBox getTransformationOptionCb(){
		if(transformationOptionCb == null)
		{
			transformationOptionCb = new JComboBox();
			
			Collection<TransformationRegistryEntry> transforms =
				TransformationsRegistry.getRegisteredTransforms();
			Iterator<TransformationRegistryEntry> it = transforms.iterator();
			while(it.hasNext()){
				transformationOptionCb.addItem(it.next());
			}
			
			transformationOptionCb.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						TransformationRegistryEntry selectedEntry = 
							(TransformationRegistryEntry) e.getItem();
						try{
							MathTransformBuilder transformBuilder = 
								selectedEntry.createTransformBuilder(linksList.getAsList());
//							vectorErrorTable.setTransformBuilder(transformBuilder);
//							vectorErrorTable.updateVErrorTable();
							
						}catch(MismatchedSizeException exception){
							GUIUtil.getInstance().messageBox(PluginServices.getText(this, "INCORRECT_VERROR_LINKS"), 
									PluginServices.getText(this, "TRANSFORMATION_ERROR"));
						}catch(MismatchedDimensionException exception){
							GUIUtil.getInstance().messageBox(PluginServices.getText(this, "INCORRECT_VERROR_LINKS"), 
									PluginServices.getText(this, "TRANSFORMATION_ERROR"));
						}catch(MismatchedReferenceSystemException exception){
							GUIUtil.getInstance().messageBox(PluginServices.getText(this, "INCORRECT_VERROR_LINKS"), 
									PluginServices.getText(this, "TRANSFORMATION_ERROR"));
						}
					}
				}// itemStateChange
			});
		}
		return transformationOptionCb;
	}
	
	
	private MathTransformBuilder createTransformBuilder() 
					throws MismatchedSizeException, 
					  MismatchedDimensionException,
					  MismatchedReferenceSystemException, 
					  FactoryException, TransformException{
		TransformationRegistryEntry selectedEntry = 
			(TransformationRegistryEntry) getTransformationOptionCb().getSelectedItem();
		return selectedEntry.createTransformBuilder(linksList.getAsList());
	}
}
