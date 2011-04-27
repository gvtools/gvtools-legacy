/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
import java.sql.Types;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.core.loaders.NetworkLoader;
import org.gvsig.graph.core.loaders.NetworkRedLoader;
import org.gvsig.gui.beans.swing.JFileChooser;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class LoadDefaultNetworkExtension extends Extension {

	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"network",
				this.getClass().getClassLoader().getResource("images/network.png")
			);		

	}

	public void execute(String actionCommand) {
		IView view = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapControl = view.getMapControl();
		MapContext map = mapControl.getMapContext();
		SingleLayerIterator lyrIterator = new SingleLayerIterator(map
				.getLayers());
		while (lyrIterator.hasNext()) {
			FLayer lyr = lyrIterator.next();
			if ((lyr.isActive()) && (lyr instanceof FLyrVect))
			{
				FLyrVect lyrVect = (FLyrVect) lyr;
				int shapeType;
				try {
					shapeType = lyrVect.getShapeType();
					if ((shapeType & FShape.LINE) == FShape.LINE) 
//						if (shapeType == FShape.LINE)
					{
						if (actionCommand.equalsIgnoreCase("LOAD_NET")) {
							File netFile = NetworkUtils.getNetworkFile(lyrVect);
							loadNetwork(lyrVect, netFile);
							return;
						}
//						if (actionCommand.equalsIgnoreCase("LOAD_NET_FROM_FILE")) {
//							String curDir = System.getProperty("user.dir");
//
//							JFileChooser fileChooser = new JFileChooser("NET_FILES", new File(curDir));
//							fileChooser.setFileFilter(new FileFilter() {
//
//								@Override
//								public boolean accept(File f) {
//									String path = f.getPath().toLowerCase();
//									if (path.endsWith(".net"))
//										return true;
//									return false;
//								}
//
//								@Override
//								public String getDescription() {
//									return ".net files";
//								}
//								
//							});
//							int res = fileChooser.showOpenDialog((Component) PluginServices.getMainFrame());
//							if (res==JFileChooser.APPROVE_OPTION) {
//								File netFile =fileChooser.getSelectedFile();
//								loadNetwork(lyrVect, netFile);
//							}
//							
//							return;
//						}
						
					}
				} catch (BaseException e) {
					e.printStackTrace();
					NotificationManager.addError(e);
				}

			}
		}


	}

	/**
	 * Suponemos que en el proyecto hay 2 tablas, una con los nodos
	 * y otro con los edges.
	 * Cargamos la red a partir de esas tablas y se la
	 * asociamos a la capa. A partir de ah�, nuestras
	 * herramientas pueden ver si la capa activa tiene
	 * asociada o no una red y ponerse visibles / invisibles
	 * Otra posible soluci�n es llevar nuestra propia lista de capas
	 * con red (que ser� peque�ita), y as�, en lugar de recorrer
	 * el MapContext, recorremos nuestra lista para ver la
	 * capa que est� activa y con red. Me empieza a preocupar
	 * que todas las herramientas iteren por la colecci�n de
	 * capas para habilitarse/deshabilitarse:
	 * 100 herramientas * 100 capas = 10.000 comprobaciones
	 * Si comprobar algo cuesta 1 mseg => 10 segundos!!!
	 * @param lyrVect
	 * @throws ReadDriverException 
	 */
	private void loadNetworkFromTables(FLyrVect lyrVect) throws ReadDriverException {
		// Aqu� mostrar un di�lgo para seleccionar las tablas
		// de nodos y edges
		// y hacer un mapping (si es necesario) entre los
		// nombres de campos
		String tableNodes = "Nodes";
		String tableEdges = "Edges";

		ProjectExtension projectExt = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);

		ProjectTable ptNodes = projectExt.getProject().getTable(tableNodes);
		ProjectTable ptEdges = projectExt.getProject().getTable(tableEdges);

		SelectableDataSource sdsNodes = ptNodes.getModelo().getRecordset();


		SelectableDataSource sdsEdges = ptEdges.getModelo().getRecordset();

		NetworkLoader netLoader = new NetworkLoader(true);

		netLoader.setNodeReader(sdsNodes);
		netLoader.setEdgeReader(sdsEdges);

		IGraph g = netLoader.loadNetwork();

		System.out.println("Num nodos=" + g.numVertices() + " numEdges = " + g.numEdges());

		lyrVect.setProperty("network", g);

	}
	public void loadNetwork(FLyrVect lyrVect, File netFile) throws BaseException {
		// Aqu� mostrar un di�lgo para seleccionar las tablas
		// de nodos y edges
		// y hacer un mapping (si es necesario) entre los
		// nombres de campos

		// TODO: MOSTRAR UN CUADRO DE DI�LOGO CON UN COMBOBOX PARA QUE ESCOJA EL CAMPO DE NOMBRE DE CALLE.
		ArrayList aux = new ArrayList();
		FieldDescription[] fields = lyrVect.getRecordset().getFieldsDescription();
		for (int i=0; i<fields.length; i++)
		{
			if (fields[i].getFieldType() == Types.VARCHAR)
			{
				aux.add(fields[i].getFieldName());
			}
		}
		String fieldStreetName = (String) JOptionPane.showInputDialog((Component) PluginServices.getMainFrame(),
				PluginServices.getText(this, "select_street_route_field_name"),
				"gvSIG",
				JOptionPane.QUESTION_MESSAGE, 
				null,
				(Object[]) aux.toArray(new String[0]), 
				"NOMBRE");
		
		if (fieldStreetName == null)
			return;

		
		NetworkRedLoader netLoader = new NetworkRedLoader();
		
		netLoader.setNetFile(netFile);

		IGraph g = netLoader.loadNetwork();
		
		System.out.println("Num nodos=" + g.numVertices() + " numEdges = " + g.numEdges());

		Network net = new Network();
		// lyrVect.createSpatialIndex();
		net.setGraph(g);
		net.setLayer(lyrVect);
//		ShortestPathExtension.solver.setNetwork(net);
//		ShortestPathExtension.solver.setFielStreetName(fieldStreetName);

		lyrVect.setProperty("network", net);
		lyrVect.setProperty("network_fieldStreetName", fieldStreetName);

	}

	public boolean isEnabled() {
		IWindow f = PluginServices.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			MapContext mapa = model.getMapContext();
			FLayer[] activeLayers = mapa.getLayers().getActives();
			if (activeLayers.length > 0)
				if (activeLayers[0] instanceof FLyrVect){
					FLyrVect lyrVect = (FLyrVect) activeLayers[0];
					File netFile = NetworkUtils.getNetworkFile(lyrVect);
					if (netFile.exists())
						return true;

				}
		}
		return false;
	}

	public boolean isVisible() {
		IWindow f = PluginServices.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			MapContext mapa = model.getMapContext();
			FLayer[] activeLayers = mapa.getLayers().getActives();
			if (activeLayers.length > 0)
				if (activeLayers[0] instanceof FLyrVect){
					FLyrVect lyrVect = (FLyrVect) activeLayers[0];
					int shapeType ;
					try {
						if (!lyrVect.isAvailable())
							return false;
						
						shapeType = lyrVect.getShapeType();
//							if (shapeType == FShape.LINE)
						if ((shapeType & FShape.LINE) == FShape.LINE) 
							return true;
					} catch (ReadDriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
		}
		return false;

	}


}
