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
package com.iver.cit.gvsig.geoprocess.impl.referencing.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.vecmath.MismatchedSizeException;

import org.geotools.referencing.operation.builder.RubberSheetBuilder;
import org.gvsig.referencing.MappedPositionContainer;
import org.gvsig.referencing.ReferencingUtil;
import org.gvsig.topology.ui.util.GUIUtil;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.geoprocess.impl.referencing.IReferencingGeoprocessUserEntries;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.referencing.TransformationsRegistry;
import com.iver.cit.gvsig.referencing.TransformationsRegistry.TransformationRegistryEntry;

public class ReferencingGeoprocessPanel extends AbstractGeoprocessGridbagPanel implements IReferencingGeoprocessUserEntries{

	private MapControl currentView;
	
//	private MappedPositionContainer verrorList;

//	private String controlPointsTitle;

	private String transformationTitle;

	private VectorErrorTable vectorErrorTable;
	
	private JComboBox transformationOptionCb;
	
	
	
	public ReferencingGeoprocessPanel(FLayers layers) {
		super(layers, PluginServices.getText(null, "SPATIAL_ADJUST"));
	}

	private static final long serialVersionUID = 5209039148987326055L;

	@Override
	protected void addSpecificDesign() {
		IWindow f = PluginServices.getMDIManager().getActiveWindow();
		View vista = (View) f;
		if (f != null) {
			MapControl mapControl = vista.getMapControl();
			this.currentView = mapControl;
		}
		
		
//		controlPointsTitle = PluginServices.getText(null, "Control_points");
		transformationTitle = PluginServices.getText(null, "Transform");
		
	
//		JLabel controlPointsLbl = new JLabel(controlPointsTitle);
//		addComponent(controlPointsLbl);
		
		JComboBox cb = getTransformationOptionCb();
		addComponent(vectorErrorTable = new VectorErrorTable(currentView, 
														 getInputLayer(),
														 (TransformationRegistryEntry)cb.getSelectedItem() ));
		
		
		JLabel transformTitleLbl = new JLabel(transformationTitle);
		addComponent(transformTitleLbl);
		
		addComponent(cb);
		
		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
	}
	
	

	private JComboBox getTransformationOptionCb(){
		if(transformationOptionCb == null){
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
//							MathTransformBuilder transformBuilder = 
//								selectedEntry.createTransformBuilder(verrorList.getAsList());
							vectorErrorTable.setTransformBuilderProvider(selectedEntry);
						}catch(MismatchedSizeException exception){
							GUIUtil.getInstance().messageBox(PluginServices.getText(this, "INCORRECT_VERROR_LINKS"), 
									PluginServices.getText(this, "TRANSFORMATION_ERROR"));
						}catch(MismatchedDimensionException exception){
							GUIUtil.getInstance().messageBox(PluginServices.getText(this, "INCORRECT_VERROR_LINKS"), 
									PluginServices.getText(this, "TRANSFORMATION_ERROR"));
						}catch(MismatchedReferenceSystemException exception){
							GUIUtil.getInstance().messageBox(PluginServices.getText(this, "INCORRECT_VERROR_LINKS"), 
									PluginServices.getText(this, "TRANSFORMATION_ERROR"));
						} catch (FactoryException exception) {
							GUIUtil.getInstance().messageBox(PluginServices.getText(this, "FACTORY_EXCEPTION"), 
									PluginServices.getText(this, "TRANSFORMATION_ERROR"));
						}
						
					}
				}// itemStateChange
			});
		}//if transformationCb
		return transformationOptionCb;
	}
	
	@Override
	/**
	 * This method is called when layer combo box selection changes.
	 */
	protected void processLayerComboBoxStateChange(ItemEvent e) {
		vectorErrorTable.setAdjustingLyr(getInputLayer());
	}

	public MathTransform getMathTransform() throws GeoprocessException {
		try {
//			return  ((TransformationRegistryEntry)getTransformationOptionCb().getSelectedItem()).
//							createTransformBuilder(verrorList.getAsList()).getMathTransform();
			MappedPositionContainer mp = this.vectorErrorTable.getVerrorContainer();
			return  ((TransformationRegistryEntry)getTransformationOptionCb().
															getSelectedItem()).
															createTransformBuilder(mp.getAsList()).
															getMathTransform();
		} catch (FactoryException e) {
			throw new GeoprocessException(e);
		}catch(RuntimeException re){
			re.printStackTrace();
			throw new GeoprocessException(re);
		}
	}

	public boolean onlyFirstLayerSelected() {
		return isFirstOnlySelected();
	}



	public FLyrVect[] getAuxiliarLyrs() {
		if(hasAuxiliarLyrs()){
			MappedPositionContainer mp = this.vectorErrorTable.getVerrorContainer();
			TransformationRegistryEntry transformRegistryEntry = 
				(TransformationRegistryEntry)getTransformationOptionCb().getSelectedItem();
			RubberSheetBuilder transformBuilder = 
				(RubberSheetBuilder) transformRegistryEntry.createTransformBuilder(mp.getAsList());
			FLyrVect tinLyr = ReferencingUtil.getInstance().getTinAsFMapLyr(transformBuilder, currentView.getProjection());
			return new FLyrVect[]{tinLyr};
		}else{
			return new FLyrVect[]{};
		}
	}



	public boolean hasAuxiliarLyrs() {
		//by now, the only transformation with auxiliar layers is Rubber Sheet Transform,
		//being this auxiliar layer TIN formed with mapped positions.
		TransformationRegistryEntry transformRegistryEntry = 
			(TransformationRegistryEntry)getTransformationOptionCb().getSelectedItem();
		return transformRegistryEntry.getName().
			equals(PluginServices.getText(null, "RUBBER_SHEET_TRANSFORM"));
	}

}
