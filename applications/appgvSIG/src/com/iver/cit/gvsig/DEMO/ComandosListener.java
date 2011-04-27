/*
 * Created on 17-jun-2003
 *
 * Copyright (c) 2003
 * Francisco Josï¿½Pearrubia Martï¿½ez
 * IVER Tecnologï¿½s de la Informacin S.A.
 * Salamanca 50
 * 46005 Valencia (        SPAIN )
 * +34 963163400
 * mailto:fran@iver.es
 * http://www.iver.es
 */
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
package com.iver.cit.gvsig.DEMO;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.cresques.cts.IProjection;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.map.DefaultMapLayer;
import org.geotools.map.MapLayer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.addlayer.AddLayerDialog;
import com.iver.cit.gvsig.addlayer.fileopen.FileOpenWizard;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrGT2_old;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.fmap.rendering.FGraphicLabel;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.gui.DataBaseOpenDialog;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.gui.FPanelLocConfig;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

// import java.awt.Frame;

/**
 * @author Administrador
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ComandosListener implements ActionListener {
	private MapControl m_MapControl;

	private MapContext m_Mapa;

	private SingleView theView;

	private static final boolean WKB_ENABLED = true;

	class MyImageObserver implements ImageObserver {

		private MapControl mapCtrl;

		public MyImageObserver(MapControl mapCtrl) {
			this.mapCtrl = mapCtrl;
		}

		public boolean imageUpdate(Image img, int infoflags, int x, int y,
				int width, int height) {
			if ((infoflags | ImageObserver.ALLBITS) != 0) {
				// TODO: AÑADIR ALGO ASÍ AL MAPCONTROL
				// if (mapCtrl.getStatus() == MapControl.ACTUALIZADO)
				{
					// System.out.println("Image " + infoflags);
					mapCtrl.drawGraphics();
				}
			}
			return true;
		}

	}

	private MyImageObserver theImgObserver;

	PruebasGT2 pruebasGT2 = new PruebasGT2();

	public ComandosListener(MapControl mapa, SingleView Owner) {
		m_Mapa = mapa.getMapContext();
		theView = Owner;
		m_MapControl = mapa;
	}

	/**
	 * Load the data from the specified dataStore and construct a
	 * {@linkPlain Context context} with a default style.
	 *
	 * @param url
	 *            The url of the shapefile to load.
	 * @param name
	 *            DOCUMENT ME!
	 *
	 * @throws IOException
	 *             is a I/O error occured.
	 * @throws DataSourceException
	 *             if an error occured while reading the data source.
	 * @throws FileNotFoundException
	 *             DOCUMENT ME!
	 */
	protected void loadLayer(DataStore store, String layerName)
			throws IOException, DataSourceException {
		final FeatureSource features = store.getFeatureSource(layerName);

		// Create the style
		final StyleBuilder builder = new StyleBuilder();
		final Style style;
		Class geometryClass = features.getSchema().getDefaultGeometry()
				.getType();

		if (LineString.class.isAssignableFrom(geometryClass)
				|| MultiLineString.class.isAssignableFrom(geometryClass)) {
			style = builder.createStyle(builder.createLineSymbolizer());
		} else if (Point.class.isAssignableFrom(geometryClass)
				|| MultiPoint.class.isAssignableFrom(geometryClass)) {
			style = builder.createStyle(builder.createPointSymbolizer());
		} else {
			style = builder.createStyle(builder.createPolygonSymbolizer(
					Color.ORANGE, Color.BLACK, 1));
		}

		final MapLayer layer = new DefaultMapLayer(features, style);
		layer.setTitle(layerName);

		FLyrGT2_old lyrGT2 = new FLyrGT2_old(layer);
		try {
			m_Mapa.getLayers().addLayer(lyrGT2);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO LWS Esto debe venir de fuera (proyeccion del fichero, o de la
		// vista)
		IProjection proj = CRSFactory.getCRS("EPSG:23030");
		ViewPort vp = m_Mapa.getViewPort();

		if (e.getActionCommand() == "ZOOM_TODO") {
			try {
				vp.setExtent(m_Mapa.getFullExtent());
				m_MapControl.drawMap(false);
			} catch (ReadDriverException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			m_MapControl.drawMap(false);
		}
		if (e.getActionCommand() == "ZOOM_PREVIO") {
			vp.setPreviousExtent();
		}
		if (e.getActionCommand() == "NETWORK_GENERATEREDFILE") {
//			FLyrVect lyr = (FLyrVect) m_Mapa.getLayers().getActives()[0];
//			NetworkWriter netBuilder = new NetworkWriter();
//
//			String redFilePath = lyr.getName().replaceFirst("\\Q.shp\\E", "");
//			File redFile = new File("c:/" + redFilePath + ".red");
//			String fieldType = "tipored"; String fieldDist = "length"; String fieldSense = "sen";
//			try {
//				netBuilder.setLayer(lyr, fieldType, fieldDist, fieldSense);
//				DbfWriter nodeWriter = new DbfWriter();
//				nodeWriter.setFile(new File("c:/nodes.dbf"));
//
//				DbfWriter edgeWriter = new DbfWriter();
//				edgeWriter.setFile(new File("c:/edges.dbf"));
//
//				netBuilder.setEdgeWriter(edgeWriter);
//				netBuilder.setNodeWriter(nodeWriter);
//
//				netBuilder.writeNetwork();
//				// netBuilder.createRedFile(lyr, redFile);
//			} catch (DriverException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (EditionException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			JOptionPane.showMessageDialog(null, "Hecho. ");
		}

		if (e.getActionCommand() == "SYMBOL_MANAGER") {
			if (e.getActionCommand() == "SYMBOL_MANAGER") {
				JDialog dialog = new JDialog();
				/*
				 * JSVGCanvas canvas = new JSVGCanvas(); try { canvas.setURI(
				 * new File("D:/java/eclipse30/eclipse/workspace/FMap
				 * 03/docs/fill1.svg").toURL().toString() ); } catch
				 * (MalformedURLException e1) { // TODO Auto-generated catch
				 * block e1.printStackTrace(); }
				 *b/
				SymbolSelector canvas = new SymbolSelector(null, FShape.POINT, new File("../com.iver.cit.gvsig.graph/symbols/"));

				dialog.getContentPane().add(canvas);

				dialog.setSize(600, 300);
				dialog.show(true);
				ISymbol symbol = canvas.getSymbol();
				FLyrVect lyr = (FLyrVect) m_Mapa.getLayers().getActives()[0];
				if (lyr!=null) {

				}*/
			}

		}

		if (e.getActionCommand().indexOf("GT2") != -1 && pruebasGT2 != null) {
			pruebasGT2.setMapContext(m_Mapa);
			pruebasGT2.actionPerformed(e);
		}

		if (e.getActionCommand() == "OPEN_LOCATOR") {
			// Set up the dialog that the button brings up.
			JDialog dialog = new JDialog();
			FPanelLocConfig m_panelLoc = new FPanelLocConfig(theView
					.getMapOverview());
			m_panelLoc.setPreferredSize(m_panelLoc.getSize());
			dialog.getContentPane().add(m_panelLoc);
			dialog.setModal(true);
			dialog.pack();
			dialog.show();
		}

		if (e.getActionCommand() == "ZOOM_MAS") {
			m_MapControl.setTool("zoomIn"); // Por defecto
		}

		if (e.getActionCommand() == "ZOOM_MENOS") {
			m_MapControl.setTool("zoomout");
		}

		if (e.getActionCommand() == "PAN") {
			m_MapControl.setTool("pan");
		}

		if (e.getActionCommand() == "INFO") {
			m_MapControl.setTool("info");
		}
		if (e.getActionCommand() == "SELRECT") {
			m_MapControl.setTool("rectSelection");
		}

		if (e.getActionCommand() == "MEDICION") {
			m_MapControl.setTool("medicion");
		}
		if (e.getActionCommand() == "MEDIRAREA") {
			m_MapControl.setTool("area");

			// Prueba de graficos
			theImgObserver = new MyImageObserver(m_MapControl);

			GraphicLayer lyr = m_MapControl.getMapContext().getGraphicsLayer();
			lyr.clearAllGraphics();
			lyr.clearSymbolsGraphics();

			File fIcon = new File("d:/sonic.gif");
			ISymbol theSymbol = null;
//			try {
			// picture marker symbol now belongs to extSymbology project
//				theSymbol = new PictureMarkerSymbol(fIcon, null);
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}

			// File fIcon = new File("d:/7bart.gif");


			int idSymbol = lyr.addSymbol(theSymbol);
			IGeometry geom = ShapeFactory.createPoint2D(500000, 4499980);
			FGraphic theGraphic = new FGraphic(geom, idSymbol);
			lyr.addGraphic(theGraphic);
			FGraphicLabel theLabel = new FGraphicLabel(geom, idSymbol,
					"Hola colega");
			// lyr.addGraphic(theLabel);
			m_MapControl.drawGraphics();
		}

		if ((e.getActionCommand() == "ADD_LAYER")
				|| (e.getActionCommand() == "ADD_MEMORY_LAYER")) {
			JDialog dlg = new JDialog();
			dlg.setModal(true);
			
			FileOpenWizard fileDlg = null; //new FileOpenWizard(new IFileOpen[] {VectorialFileDriver.class}); //, RasterDriver.class });
			DataBaseOpenDialog dbop = new DataBaseOpenDialog();
			// dbop.setClasses(new Class[]{DBDriver.class});

			AddLayerDialog fopen = new AddLayerDialog();
			fopen.addTab("Fichero", fileDlg);
			fopen.addTab("Base de datos", dbop);
			// fileDlg.setPreferredSize(fopen.getSize());
			dlg.setSize(fopen.getSize());
			dlg.getContentPane().add(fopen);
			dlg.pack();
			dlg.show();

			// TODO: Hacer lo de la capa WMS también.
			if (fileDlg.getFiles() == null)
				return;
			FLayer lyr = null;
			File[] files = fileDlg.getFiles();
			String[] driverNames = fileDlg.getDriverNames();
			Driver[] drivers = new Driver[driverNames.length];
			for (int i = 0; i < drivers.length; i++) {
				try {
					drivers[i] = LayerFactory.getDM().getDriver(driverNames[i]);
				} catch (DriverLoadException ex) {
					System.err.println("No se pudo cargar el driver "
							+ ex.getMessage());
				}
			}

			m_MapControl.getMapContext().beginAtomicEvent();

			for (int iFile = 0; iFile < files.length; iFile++) {
				File fich = files[iFile];
				String layerName = fich.getName();
				String layerPath = fich.getAbsolutePath();

				if (drivers[iFile] instanceof VectorialFileDriver) {
					lyr = LayerFactory.createLayer(layerName,
							(VectorialFileDriver) drivers[iFile], fich, proj);
				}/*
				else if (drivers[iFile] instanceof RasterDriver) {
					lyr = LayerFactory.createLayer(layerName,
					driverNames[iFile], fich, proj);
				}
				*/

				lyr.setVisible(true);
				lyr.setName(layerName);
				if (lyr != null) {
					try {
						theView.getMapControl().getMapContext().getLayers()
								.addLayer(lyr);
					} catch (CancelationException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					// TODO: Poner una variable y dibujar solo cuando
					// todas las capas hayan sido cargadas.
					// theView.getMapControl().drawMap();
					// theView.getTOC().refresh();

				}

			} // for
			m_MapControl.getMapContext().endAtomicEvent();
			theView.getTOC().refresh();

			return;

		}

		/**
		 *
		 * String pathFich = fc.getDirectory() + fc.getFile();
		 *
		 *
		 * System.out.println(pathFich); FLyrVect lyr = new
		 * FLyrVect(fc.getFile(),pathFich);
		 *
		 * m_Mapa.AddLayer(lyr); m_Owner.jLeyenda.Refresh();
		 *  }
		 *
		 */
		if (e.getActionCommand() == "EXPORT") {
			// Export jExport = new Export(m_MapControl);
		}

		if (e.getActionCommand() == "VIEW_TABLE") {
			FLayer[] actives = m_Mapa.getLayers().getActives();

			for (int i = 0; i < actives.length; i++) {
				if (actives[i] instanceof AlphanumericData) {
					AlphanumericData co = (AlphanumericData) actives[i];

					SelectableDataSource dataSource;
					try {
						dataSource = co.getRecordset();
						EditableAdapter ea = new EditableAdapter();
						ea.setOriginalDataSource(dataSource);
						ProjectTable projectTable = ProjectFactory.createTable(
								actives[i].getName(), ea);
						projectTable.setAssociatedTable(co);

						Table t = new Table();
						t.setModel(projectTable);
						JDialog myDialog = new JDialog(theView,
								"Tabla de Atributos ("
										+ t.getModel().getAssociatedTable()
												.getRecordset().getRowCount()
										+ " registros)");
						myDialog.setContentPane(t);

						// myViewer.addAttributeTableViewerListener(m_Owner);
						myDialog.pack();
						myDialog.show();
					} catch (ReadDriverException e1) {
						e1.printStackTrace();
					} catch (HeadlessException ex) {
						ex.printStackTrace();
					}

				}

			}

		}
	}
}
