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
package org.gvsig.fmap.tools;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JDialog;

import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.fmap.core.FLyrUtil;
import org.gvsig.fmap.core.NewFConverter;
import org.gvsig.topology.ui.util.HTMLPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.vividsolutions.jts.geom.Geometry;

public class GeometryInfoListener implements PointListener {

	private final Image img = PluginServices.getIconTheme().get("cursor-query-information").getImage();
	
	private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(img,
			new Point(16, 16), "");

	private MapControl mapCtrl;
	private static int TOL=7;
	
	NumberFormat nf = NumberFormat.getInstance();


	public GeometryInfoListener(MapControl mc) {
		this.mapCtrl = mc;
		nf.setMaximumFractionDigits(2);
	}


	/**
	 * When the users click over the view the point is caught and handled in this method, which will look
	 * for alfanumeric information at that feature in the active layers.
	 * @param event
	 *
	 * To use the old info tool, use again the point2 method!
	 * @throws BehaviorException
	 */
	public void point(PointEvent event) throws BehaviorException {

		Point imagePoint = new Point((int) event.getPoint().getX(), (int) event
				.getPoint().getY());
		Point2D mapPoint =  mapCtrl.getViewPort().toMapPoint(imagePoint);
		double tol = mapCtrl.getViewPort().toMapDistance(TOL);

		List<FLyrVect> activeLyrs = FLyrUtil.getActiveVectorialLyrs(mapCtrl.getMapContext());
		String text = "";
		
		int numActiveLyrs = activeLyrs.size();
		if(numActiveLyrs > 0){
			try {
				for (int i = 0; i < numActiveLyrs; i++) {
					FLyrVect lyr = activeLyrs.get(i);
					Rectangle2D rect = FGeometryUtil.getSnapRectangle(mapPoint.getX(), mapPoint.getY(), tol);
					IFeatureIterator iterator = 
						lyr.getSource().
						getFeatureIterator(rect, null, null, false);
					text += "<h3><b>"+PluginServices.getText(this, "LAYER")+"</b>: "+lyr.getName()+"</h3>";
					int numFeatures = 0;
					text += "<table border=\"1\">";
					while(iterator.hasNext()){
						
						numFeatures++;
						IFeature feature = iterator.next();
						Geometry geometry  = NewFConverter.toJtsGeometry(feature.getGeometry());
						text += "<tr><td><b>" + PluginServices.getText(this, "FEATURE_ID") + "</b>: "+ feature.getID() + "</td></tr>";
						text += "<tr>";
						text += "<td><b>"+
							PluginServices.getText(this, "AREA")+"</b>: "+
										nf.format(geometry.getArea())+
									"</td></tr>";
						text += "<tr><td><b>"+
						PluginServices.getText(this, "PERIMETER")+"</b>: "+
										nf.format(geometry.getLength())+
								"</td>";
						text += "</tr>";
						
						text += "<tr>";
						text += "<td><b>"+PluginServices.getText(this, "WKT")+"</b>: "+geometry.toText()+"</td>";
						text += "</tr>";
					}//while
					if(numFeatures == 0){
						text += PluginServices.getText(this, "NOT_FOUND_GEOMETRIES");
					}
					text += "</table>";
				}//for
				
				if(text != ""){
					HTMLPanel panel = new HTMLPanel(PluginServices.getText(this, "GEOMETRY_INFO"), text);
					if (PluginServices.getMainFrame() == null) {
						JDialog dialog = new JDialog();
						
						panel.setPreferredSize(new Dimension(200, 200));
						dialog.getContentPane().add(panel);
						dialog.setModal(false);
						dialog.pack();
						dialog.setVisible(true);
	
					} else {
						PluginServices.getMDIManager().addWindow(panel);
					}
				}
			} catch (ReadDriverException e) {
				NotificationManager.addError("GeometryInfo", e);
				e.printStackTrace();
			}
		}
			
		
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
	 */
	public Cursor getCursor() {
		return cur;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
	 */
	public boolean cancelDrawing() {
		return false;
	}


	public void pointDoubleClick(PointEvent event) throws BehaviorException {
	}
}