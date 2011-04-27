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
package org.gvsig.graph;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.gui.ODMatrixTask;
import org.gvsig.graph.gui.OdMatrixControlPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 *
 * ODMatrixControlPanel sets selectedWriter to allow multiple export formats
 */
public class ODMatrixExtension extends Extension {



	private static ArrayList<IODMatrixFileWriter> odMatrixWriters = new ArrayList<IODMatrixFileWriter>();

	private static IODMatrixFileWriter selectedWriter;
	
	public static void registerOdMatrixFormat(IODMatrixFileWriter w) {
		odMatrixWriters.add(w);
	}
	
	public static IODMatrixFileWriter[] getOdMatrixWriters() {
		return (IODMatrixFileWriter[]) odMatrixWriters.toArray(new IODMatrixFileWriter[0]);
	}
	
	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"odmatrix",
				this.getClass().getClassLoader().getResource("images/odmatrix.png")
			);		
		
		ODMatrixFileWriter4cols f1 = new ODMatrixFileWriter4cols();
		ODMatrixFileWriter4cols_minutes_km f2 = new ODMatrixFileWriter4cols_minutes_km();
		ODMatrixFileWriterRFormat f3 = new ODMatrixFileWriterRFormat();
		registerOdMatrixFormat(f1);
		registerOdMatrixFormat(f2);
		registerOdMatrixFormat(f3);
	}

	public void execute(String actionCommand) {
		
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapContext map = v.getMapControl().getMapContext();
		SingleLayerIterator it = new SingleLayerIterator(map.getLayers());

		if (actionCommand.equals("ODMATRIX")) {
			while (it.hasNext())
			{
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");

				if ( net != null)
				{
					OdMatrixControlPanel ctrlDlg = new OdMatrixControlPanel();
					try {
						ctrlDlg.setMapContext(map);
						PluginServices.getMDIManager().addWindow(ctrlDlg);
						if (ctrlDlg.isOkPressed()) {
							if (net.getLayer().getISpatialIndex() == null)
							{
								System.out.println("Calculando índice espacial (QuadTree, que es más rápido)...");
								net.getLayer().setISpatialIndex(
										NetworkUtils.createJtsQuadtree(net.getLayer()));
								System.out.println("Indice espacial calculado.");
							}
							FLyrVect layerOrigins = ctrlDlg.getOriginsLayer();
							FLyrVect layerDestinations = ctrlDlg.getDestinationsLayer();
							boolean bSameLayer = false;
							if (layerOrigins == layerDestinations)
								bSameLayer = true;
							double tolerance = ctrlDlg.getTolerance();
							GvFlag[] originFlags = NetworkUtils.putFlagsOnNetwork(layerOrigins,
									net, tolerance);
							GvFlag[] destinationFlags = null; 
							if (bSameLayer)
								destinationFlags = originFlags;
							else
								destinationFlags = NetworkUtils.putFlagsOnNetwork(layerDestinations, net, tolerance);
							
							File selectedFile = new File(ctrlDlg.getGeneratedFile());
							
							
							selectedWriter = odMatrixWriters.get(ctrlDlg.getFileFormat());
							
							ODMatrixTask task = new ODMatrixTask(net, originFlags, destinationFlags,
									selectedFile, selectedWriter);
							PluginServices.cancelableBackgroundExecution(task);
							// calculateOdMatrix(net, originFlags, destinationFlags, selectedFile);
							
							// TODO: ASK THE USER IF HE WANTS TO SAVE FLAGS TO AVOID PUTTING POINTS
							// ON NETWORK AGAIN
							
							return;
						} // isOkPressed
					} catch (BaseException e) {
						e.printStackTrace();
						if (e.getCode() == GraphException.FLAG_OUT_NETWORK) {
							JOptionPane.showMessageDialog((Component) PluginServices.getMainFrame(), PluginServices.getText(null, "there_are_points_outside_the_tolerance"));
//							NotificationManager.addError(e.getFormatString(), e);
						}
					}

				}
			} 
		}
		

	}
	
	public boolean isEnabled() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof View)
		{
			View v = (View) window;
	        MapControl mapCtrl = v.getMapControl();
			MapContext map = mapCtrl.getMapContext();
			
			SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
			while (it.hasNext())
			{
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");
				
				if ( net != null)
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean isVisible() {
		IWindow f = PluginServices.getMDIManager()
		 .getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
			return true;
		}
		return false;

	}

	public static IODMatrixFileWriter getSelectedWriter() {
		return selectedWriter;
	}

	public static void setSelectedWriter(IODMatrixFileWriter selectedWriter) {
		ODMatrixExtension.selectedWriter = selectedWriter;
	}


}


