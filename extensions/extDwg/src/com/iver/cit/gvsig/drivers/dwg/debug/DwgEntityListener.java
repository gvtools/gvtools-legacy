/*
 * Created on 10-ene-2007
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
* $Id: DwgEntityListener.java 23943 2008-10-15 07:12:20Z vcaballero $
* $Log$
* Revision 1.5.2.1  2007-02-28 07:35:06  jmvivo
* Actualizado desde el HEAD.
*
* Revision 1.5  2007/02/15 16:28:36  fdiaz
* Compatibilizado con branch v10
*
* Revision 1.4  2007/02/01 20:03:21  azabala
* *** empty log message ***
*
* Revision 1.3  2007/01/24 20:40:02  azabala
* added debug of dwg class
*
* Revision 1.2  2007/01/12 19:57:44  azabala
* *** empty log message ***
*
* Revision 1.1  2007/01/11 20:31:05  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.drivers.dwg.debug;

import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.drivers.dwg.DwgMemoryDriver;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
//import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.iver.cit.jdwglib.dwg.DwgHandleReference;
import com.iver.cit.jdwglib.dwg.DwgObject;

/**
 * <p>Listens events produced by the selection of a <i>control point</i> of any graphical geometry
 *  in layers of the associated {@link MapControl MapControl} object.</p>
 *
 * <p>Listens a single or double click of any mouse's button, and also the events produced when
 *  the position of the cursor has changed on the associated <code>MapControl</code> object.</p>
 *
 * <p>Uses {@link Cursor#CROSSHAIR_CURSOR Cursor#CROSSHAIR_CURSOR} as mouse's cursor image.</p>
 */
public class DwgEntityListener implements PointListener{
	/**
	 * Used to calculate the pixel at the associated <code>MapControl</code> down the
	 *  position of the mouse.
	 */
	public static int pixelTolerance = 3;

	/**
	 * Reference to the <code>MapControl</code> object that uses.
	 */
	private MapControl mapCtrl;

	/**
	 * The cursor used to work with this tool listener.
	 *
	 * @see #getCursor()
	 */
	private Cursor cur = java.awt.Cursor.
	    		getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);


    /**
     * <p>Creates a new <code>DwgEntityListener</code> object.</p>
     *
     * @param mc the <code>MapControl</code> where will be applied the changes
     */
	public DwgEntityListener(MapControl mc) {
	        this.mapCtrl = mc;
	    }

    /*
     * (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#point(com.iver.cit.gvsig.fmap.tools.Events.PointEvent)
     */
	public void point(PointEvent event) throws BehaviorException {

        Point2D pReal = mapCtrl.
        		getMapContext().
        		getViewPort().
        		toMapPoint(event.getPoint());

		SingleLayerIterator it = new SingleLayerIterator(mapCtrl.getMapContext().getLayers());
		while (it.hasNext())
		{
			FLayer aux = it.next();
			if (!aux.isActive())
				continue;
			if(! (aux instanceof FLyrVect))
				return;

			FLyrVect vectLyr = (FLyrVect) aux;
			VectorialDriver driver = vectLyr.getSource().getDriver();

//				Class dwgMemoryDriverClass = LayerFactory.getDM().getDriverClassByName("gvSIG DWG Memory Driver");
//				if(dwgMemoryDriverClass == null){
//					System.out.println("Driver dwg no disponible???");
//					return;
//				}
//				if(!dwgMemoryDriverClass.isInstance(driver))
//					return;
//				PluginClassLoader loader = PluginServices.
//					getPluginServices(this).getClassLoader();
//				try {
//					loader.loadClass(dwgMemoryDriverClass.toString());
//				} catch (ClassNotFoundException e1) {
//					return;
//				}

			if(!(driver instanceof com.iver.cit.gvsig.fmap.drivers.AbstractCadMemoryDriver))
					return;
			/*
			 * Comentarizar el if completo para que funcione en el libFMap del HEAD.
			 */
			if(!(driver instanceof DwgMemoryDriver)){
				JOptionPane.showConfirmDialog(null,
						"Esta herramienta sólo funciona con libFMap del HEAD.\nVer comentarios en el codigo de esta clase."
				);
				return;
			}

			final com.iver.cit.gvsig.fmap.drivers.AbstractCadMemoryDriver dwgDriver = (com.iver.cit.gvsig.fmap.drivers.AbstractCadMemoryDriver)driver;
			double realTol = mapCtrl.getViewPort().toMapDistance(pixelTolerance);

				Rectangle2D recPoint = new Rectangle2D.Double(pReal.getX() - (realTol / 2),
		                pReal.getY() - (realTol / 2), realTol, realTol);

				try {
					vectLyr.process(new FeatureVisitor(){
						public void visit(IGeometry g, int index) throws VisitorException {
							/*
							 * Cambiar la comentarizacion de las dos lineas de abajo
							 * para funcione la herramienta en libFMap del HEAD.
							 * No funciona en la v10 porque AbstractCadMemoryDriver
							 * no declara el metodo getCadSource.
							 * Quitar también el if que muestra el mensaje de arriba.
							 */

//								DwgObject dwgObj = (DwgObject) dwgDriver.getCadSource(index);
							DwgObject dwgObj = (DwgObject) ((DwgMemoryDriver)dwgDriver).getCadSource(index);

							DwgHandleReference handle = dwgObj.getLayerHandle();
							int lyrHdlCode = handle.getCode();
							int lyrHdl = handle.getOffset();
							JOptionPane.showConfirmDialog(null,
									"hdlCode="+lyrHdlCode+",hdl="+lyrHdl+"entity="+dwgObj.getClass().getName());
						}

						public String getProcessDescription() {
							return "";
						}

						public void stop(FLayer layer) {
						}

						public boolean start(FLayer layer) {
							return true;
						}}, recPoint);
				} catch (VisitorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ReadDriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//try
		}//while
    }

    /*
     * (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
     */
    public Cursor getCursor() {
        return cur;
    }

    /*
     * (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
     */
    public boolean cancelDrawing() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#pointDoubleClick(com.iver.cit.gvsig.fmap.tools.Events.PointEvent)
     */
    public void pointDoubleClick(PointEvent event) throws BehaviorException {
		point(event);
	}
}





