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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.geotools.referencefork.referencing.operation.builder.MappedPosition;
import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.fmap.tools.VectorListenerImpl;
import org.gvsig.fmap.tools.behavior.VectorBehavior;
import org.gvsig.referencing.DisactivableMappedPosition;
import org.gvsig.referencing.MappedPositionContainer;
import org.gvsig.referencing.ReferencingUtil;
import org.gvsig.topology.ui.util.BoxLayoutPanel;
import org.gvsig.topology.ui.util.GUIUtil;
import org.opengis.spatialschema.geometry.DirectPosition;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.fmap.tools.Events.MoveEvent;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Panel to digitize MappedPosition in mapcontrol.
 * 
 * MappedPosition is a 2D euclidean vector, useful to represent correspondency between equivalent
 * points.
 * 
 * @author Alvaro Zabala
 * 
 * 
 * TODO Puede ser mas intuitivo usar una tabla, en la que se muestre x0,y0-x1,y1, y que tenga una serie de botones
 *(borrar, redigitalizar en el mapa, añadir, etc) que se seleccionen en funcion de lo que haya seleccionado en la tabla
 *(borrar solo si hay un registro seleccionado, añadir siempre habilitado)
 *
 */
public class MappedPositionPanel extends BoxLayoutPanel {
	/**
	 * Serial version id
	 */
	private static final long serialVersionUID = 2689973568535047698L;
	
	/**
	 * Identifier of this mapped position
	 */
	int mappedPositionIdx;
	
	
//	private List<MappedPosition> linksList;
	/**
	 * List of mapped positions digitized in an adjust session
	 */
	private MappedPositionContainer linksList;
	
	/**
	 * Reference to the active view when user opened referencing dialog
	 * (when digitizing, referencing dialog is not visible, so user could active
	 * another view, causing problems.
	 */
	private MapControl currentView;
	
	/**
	 * mapped position associated to this panel
	 */
	private MappedPosition mappedPosition;

	
	String title = PluginServices.getText(null, "Mapped_Position");
	String xLbl = PluginServices.getText(null, "X=");
	String yLbl = PluginServices.getText(null, "Y=");
	
	JTextField originPtTextFieldX;
	JTextField originPtTextFieldY;
	
	JTextField destPtTextFieldX;
	JTextField destPtTextFieldY;
	
	JButton digitizeButton;
	
	
	/**
	 * Constructor
	 * @param 
	 * 
	 */
	public MappedPositionPanel(MappedPositionContainer linksList, MapControl currentView) {
		super();
		this.linksList = linksList;
		this.mappedPositionIdx = linksList.getCount();
		this.currentView = currentView;
		
		initialize();
	}
	
	private void initialize(){
		this.addRow(new JComponent[] { 
				new JLabel(title+" "+ mappedPositionIdx) , getDigitizeButton()});
		
		this.addRow(new JComponent[]{new JLabel(xLbl), 
									getOriginXTF(), 
									new JLabel(" ; "+yLbl), 
									getOriginYTF()});
		
		this.addRow(new JComponent[]{new JLabel(xLbl), 
				getDestXTF(), 
				new JLabel(" ; "+yLbl), 
				getDestYTF()/*, getDigitizeButton()*/});
	}
	
	public void setMappedPosition(MappedPosition mappedPosition){
		this.mappedPosition = mappedPosition;
		
		DirectPosition source = mappedPosition.getSource();
		DirectPosition dest = mappedPosition.getTarget();
		
		this.originPtTextFieldX.setText(source.getCoordinates()[0]+"");
		this.originPtTextFieldY.setText(source.getCoordinates()[1]+"");
		
		this.destPtTextFieldX.setText(dest.getCoordinates()[0]+"");
		this.destPtTextFieldY.setText(dest.getCoordinates()[1]+"");
		
	}
	
	public MappedPosition getMappedPosition() throws IllegalArgumentException {
		try {
			double x0 = Double.parseDouble(getOriginXTF().getText());
			double y0 = Double.parseDouble(getOriginYTF().getText());
			
			double x1 = Double.parseDouble(getDestXTF().getText());
			double y1 = Double.parseDouble(getDestYTF().getText());
			  
			ReferencingUtil ref = ReferencingUtil.getInstance();
			
			DirectPosition a = ref.create(new double[]{x0, y0} , null);
			DirectPosition b = ref.create(new double[]{x1, y1} , null);
			
			
			DisactivableMappedPosition p = new DisactivableMappedPosition(a,b);
			return p;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Valor no numerico", e);
		}
	}
	
	public void setEditable(boolean editable){
		originPtTextFieldX.setEditable(editable);
		originPtTextFieldY.setEditable(editable);
		destPtTextFieldX.setEditable(editable);
		destPtTextFieldY.setEditable(editable);
	}
	
	private JTextField getOriginXTF(){
		if(originPtTextFieldX == null){
			originPtTextFieldX = new JTextField(20);
			originPtTextFieldX.setEditable(false);
		}
		return originPtTextFieldX;
	}
	
	private JTextField getOriginYTF(){
		if(originPtTextFieldY == null){
			originPtTextFieldY = new JTextField(20);
			originPtTextFieldY.setEditable(false);
		}
		return originPtTextFieldY;
	}
	
	private JTextField getDestXTF(){
		if(destPtTextFieldX == null){
			destPtTextFieldX = new JTextField(20);
			destPtTextFieldX.setEditable(false);
		}
		return destPtTextFieldX;
	}
	
	private JTextField getDestYTF(){
		if(destPtTextFieldY == null){
			destPtTextFieldY = new JTextField(20);
			destPtTextFieldY.setEditable(false);
		}
		return destPtTextFieldY;
	}
	
	private JButton getDigitizeButton(){
		if(digitizeButton == null){
			digitizeButton = new JButton(" -> ");
			digitizeButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					try {
//						MDIManager mdiManager = PluginServices.getMDIManager(); 
//						if (mdiManager != null && mdiManager.getActiveWindow() != null && mdiManager.getActiveWindow() instanceof View) {
//							View view = (View) mdiManager.getActiveWindow();
//							final MapControl mapCtrl = view.getMapControl();
							final MapControl mapCtrl = currentView;
							if (mapCtrl != null) {//PROBLEMA. EL PRIMER VECTOR DE ERROR SE CONSTRUYE CUANDO EL MAPCONTROL ES NULL ARREGLAR!!!!!!
								String sTool = "digitizeVectorError";
								StatusBarListener sbl = new StatusBarListener(mapCtrl);	
								
								final JComponent thisContainer = (JComponent) GUIUtil.getInstance().getParentOfType(MappedPositionPanel.this, IWindow.class);
								
								VectorListenerImpl vl = new VectorListenerImpl(mapCtrl, linksList){
									public void vector(MoveEvent event) throws BehaviorException {
										super.vector(event);
										
										MappedPosition lastMappedPosition = 
											linksList.getMappedPosition(linksList.getCount() - 1);
										
										MappedPositionPanel.this.setMappedPosition(lastMappedPosition);
										
										if(thisContainer != null){
											if(!GUIUtil.getInstance().getParentOfType(thisContainer, JInternalFrame.class).isVisible()){
												GUIUtil.getInstance().getParentOfType(thisContainer, JInternalFrame.class).setVisible(true);
											}
												
										}//thisContainer
										mapCtrl.setPrevTool();
										
										if(!linksList.existsLinksLyr()){
											FLyrVect linkLyr = linksList.getLinkLyr(currentView.getProjection());
											MapContext mapContext = currentView.getMapContext();
											mapContext.beginAtomicEvent();
											mapContext.getLayers().addLayer(linkLyr);
											mapContext.endAtomicEvent();
										}
										mapCtrl.commandRepaint();
										
										
		
									}
								};
								
								mapCtrl.addMapTool(sTool, new Behavior[] {
										new VectorBehavior(vl, null),
										new MouseMovementBehavior(sbl) });
								mapCtrl.setTool(sTool);

								if(mappedPosition != null){
									DirectPosition source = mappedPosition.getSource();
									double[] sourceCoords = source.getCoordinates();
									DirectPosition dest = mappedPosition.getTarget();
									double[] destCoords = dest.getCoordinates();
									
									Envelope envelope = new Envelope(sourceCoords[0], 
																	   destCoords[0], 
																	   sourceCoords[1], 
																	   destCoords[1]);
									
									Rectangle2D rect = FGeometryUtil.envelopeToRectangle2D(envelope);
									mapCtrl.getMapContext().zoomToExtent(rect);
								}
								
								
								if (thisContainer != null) {
									GUIUtil.getInstance().getParentOfType(thisContainer, JInternalFrame.class).setVisible(false);
								}

							}// if mapCtrl != null
//						}// if f!=null
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}});
		}
		return digitizeButton;
	}
	
}
