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
package org.gvsig.graph.tools;

import java.awt.Cursor;
import java.awt.geom.Point2D;

import javax.swing.JOptionPane;

import org.cresques.cts.GeoCalc;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;

public class FlagListener implements PointListener {
	public static final int TO_ARC = 0;
	public static final int TO_NODE = 1;
	public static int pixelTolerance = 10;
    private MapControl mapCtrl;
    private int idSymbolFlag = -1;
    private Cursor cur = java.awt.Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private int mode;

    
    public FlagListener(MapControl mc) {
        this.mapCtrl = mc;
    }


    /* (non-Javadoc)
     * @see org.gvsig.fmap.tools.Listeners.PointListener#point(org.gvsig.fmap.tools.Events.PointEvent)
     * The PointEvent method bring you a point in pixel coordinates. You
     * need to transform it to world coordinates. The class to do conversions
     * is ViewPort, obtained thru the MapContext of mapCtrl.
     */
    public void point(PointEvent event) throws BehaviorException {
    	com.iver.cit.gvsig.fmap.ViewPort vp = mapCtrl.getViewPort();
        Point2D pReal = vp.toMapPoint(event.getPoint());
        
		SingleLayerIterator it = new SingleLayerIterator(mapCtrl.getMapContext().getLayers());
		while (it.hasNext())
		{
			FLayer aux = it.next();
			if (!aux.isActive())
				continue;
			Network net = (Network) aux.getProperty("network");
			
			if ( net != null)
			{
				
				double realTol = vp.toMapDistance(pixelTolerance);
				Point2D pReal2 = vp.toMapPoint((int) event.getPoint().getX()+ pixelTolerance, (int) event.getPoint().getY()+ pixelTolerance);  
				
//				if ((vp.getProjection() != null) && !(vp.getProjection().isProjected())) {
//					realTol = vp.distanceWorld(pReal, pReal2);
//				}
				
				GvFlag flag;
				try {
					if (mode == TO_ARC)
						flag = net.addFlag(pReal.getX(), pReal.getY(), realTol);
					else
						flag = net.addFlagToNode(pReal.getX(), pReal.getY(), realTol);
					if (flag == null)
					{
						JOptionPane.showMessageDialog(null, PluginServices.getText(this, "point_not_on_the_network"));
						return;
					}
					NetworkUtils.addGraphicFlag(mapCtrl, flag);
					mapCtrl.drawGraphics();
					PluginServices.getMainFrame().enableControls();
				} catch (GraphException e) {
					e.printStackTrace();
					NotificationManager.addError(e);
				}
				
			}
		}
        
        
    }

    public Cursor getCursor() {
        return cur;
    }

    public boolean cancelDrawing() {
        return false;
    }


	public void pointDoubleClick(PointEvent event) throws BehaviorException {
		
	}


	public void setMode(int mode) {
		this.mode = mode;
		
	}

}


