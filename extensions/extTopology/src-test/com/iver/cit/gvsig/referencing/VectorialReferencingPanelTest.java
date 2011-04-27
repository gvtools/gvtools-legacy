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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.ImageIcon;

import junit.framework.TestCase;

import org.cresques.cts.ICoordTrans;
import org.cresques.cts.IProjection;
import org.gvsig.exceptions.BaseException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.layers.ReloadLayerException;
import com.iver.cit.gvsig.exceptions.layers.StartEditionLayerException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.AbstractLinkProperties;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayerStatus;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerListener;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.ComposedLayer;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;

public class VectorialReferencingPanelTest extends TestCase {
	public void testPanel(){
javax.swing.JFrame f = new javax.swing.JFrame();
		
		FLayers lyrs = new FLayers();
		lyrs.addLayer(new FLyrVect(){

			public void addError(BaseException exception) {
				// TODO Auto-generated method stub
				
			}

			public boolean addLayerListener(LayerListener o) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean allowLinks() {
				// TODO Auto-generated method stub
				return false;
			}

			public FLayer cloneLayer() throws Exception {
				// TODO Auto-generated method stub
				return null;
			}

			public void draw(BufferedImage image, Graphics2D g,
					ViewPort viewPort, Cancellable cancel, double scale)
					throws ReadDriverException {
				// TODO Auto-generated method stub
				
			}

			public BufferedImage getCacheImageDrawnLayers() {
				// TODO Auto-generated method stub
				return null;
			}

			public ICoordTrans getCoordTrans() {
				// TODO Auto-generated method stub
				return null;
			}

			public BaseException getError(int i) {
				// TODO Auto-generated method stub
				return null;
			}

			public List getErrors() {
				// TODO Auto-generated method stub
				return null;
			}

			public Map getExtendedProperties() {
				// TODO Auto-generated method stub
				return null;
			}

			public FLayerStatus getFLayerStatus() {
				// TODO Auto-generated method stub
				return null;
			}

			public Rectangle2D getFullExtent() throws ReadDriverException,
					ExpansionFileReadException {
				// TODO Auto-generated method stub
				return null;
			}

			public String getInfoString() {
				// TODO Auto-generated method stub
				return null;
			}

			public LayerListener[] getLayerListeners() {
				// TODO Auto-generated method stub
				return null;
			}

			public URI[] getLink(Point2D point, double tolerance) {
				// TODO Auto-generated method stub
				return null;
			}

			public AbstractLinkProperties getLinkProperties() {
				// TODO Auto-generated method stub
				return null;
			}

			public MapContext getMapContext() {
				// TODO Auto-generated method stub
				return null;
			}

			public double getMaxScale() {
				// TODO Auto-generated method stub
				return 0;
			}

			public double getMinScale() {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getName() {
				// TODO Auto-generated method stub
				return "kk";
			}

			public int getNumErrors() {
				// TODO Auto-generated method stub
				return 0;
			}

			public FLayers getParentLayer() {
				// TODO Auto-generated method stub
				return null;
			}

			public Object getProperty(Object key) {
				// TODO Auto-generated method stub
				return null;
			}

			public ImageIcon getTocImageIcon() {
				// TODO Auto-generated method stub
				return null;
			}

			public Image getTocStatusImage() {
				// TODO Auto-generated method stub
				return null;
			}

			public XMLEntity getXMLEntity() throws XMLException {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isActive() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isAvailable() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isCachingDrawnLayers() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isDirty() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isEditing() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isInTOC() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isOk() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isVisible() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isWithinScale(double scale) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isWritable() {
				// TODO Auto-generated method stub
				return false;
			}

			public void load() throws LoadLayerException {
				// TODO Auto-generated method stub
				
			}

			public ComposedLayer newComposedLayer() {
				// TODO Auto-generated method stub
				return null;
			}

			public void print(Graphics2D g, ViewPort viewPort,
					Cancellable cancel, double scale,
					PrintRequestAttributeSet properties)
					throws ReadDriverException {
				// TODO Auto-generated method stub
				
			}

			public void reload() throws ReloadLayerException {
				// TODO Auto-generated method stub
				
			}

			public boolean removeLayerListener(LayerListener o) {
				// TODO Auto-generated method stub
				return false;
			}

			public void setActive(boolean selected) {
				// TODO Auto-generated method stub
				
			}

			public void setAvailable(boolean available) {
				// TODO Auto-generated method stub
				
			}

			public void setCacheImageDrawnLayers(
					BufferedImage cacheImageDrawnLayers) {
				// TODO Auto-generated method stub
				
			}

			public void setCachingDrawnLayers(boolean cacheDrawnLayers) {
				// TODO Auto-generated method stub
				
			}

			public void setCoordTrans(ICoordTrans ct) {
				// TODO Auto-generated method stub
				
			}

			public void setDirty(boolean dirty) {
				// TODO Auto-generated method stub
				
			}

			public void setEditing(boolean b) throws StartEditionLayerException {
				// TODO Auto-generated method stub
				
			}

			public void setFLayerStatus(FLayerStatus status) {
				// TODO Auto-generated method stub
				
			}

			public void setInTOC(boolean b) {
				// TODO Auto-generated method stub
				
			}

			public void setMaxScale(double maxScale) {
				// TODO Auto-generated method stub
				
			}

			public void setMinScale(double minScale) {
				// TODO Auto-generated method stub
				
			}

			public void setName(String name) {
				// TODO Auto-generated method stub
				
			}

			public void setParentLayer(FLayers root) {
				// TODO Auto-generated method stub
				
			}

			public void setProperty(Object key, Object obj) {
				// TODO Auto-generated method stub
				
			}

			public void setVisible(boolean visibility) {
				// TODO Auto-generated method stub
				
			}

			public void setXMLEntity(XMLEntity xml) throws XMLException {
				// TODO Auto-generated method stub
				
			}

			public void setXMLEntity03(XMLEntity xml) throws XMLException {
				// TODO Auto-generated method stub
				
			}

			public boolean visibleRequired() {
				// TODO Auto-generated method stub
				return false;
			}

			public IProjection getProjection() {
				// TODO Auto-generated method stub
				return null;
			}

			public void reProject(ICoordTrans ct) {
				// TODO Auto-generated method stub
				
			}});
		
		
		
//		VectorialReferencingPanel panel = new VectorialReferencingPanel(lyrs);
//		f.getContentPane().add(panel);
//		
//		f.setSize(600, 600);
//		
//		f.setVisible(true);
	}
}
