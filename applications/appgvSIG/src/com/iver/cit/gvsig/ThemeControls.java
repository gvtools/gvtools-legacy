/*
 * Created on 31-may-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
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
package com.iver.cit.gvsig;

import java.awt.Component;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.BitSet;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.dxf.write.DxfGisWriter;
import com.iver.cit.gvsig.fmap.drivers.shp.SHP;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.operations.strategies.SelectedShapeVisitor;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.GenericFileFilter;


/**
 * Extensión de operaciones sobre el tema.
 *
 * @author Vicente Caballero Navarro
 */
public class ThemeControls extends Extension {
	private static Logger logger = Logger.getLogger(ThemeControls.class.getName());

	/**
	 * @see com.iver.mdiApp.plugins.IExtension#updateUI(java.lang.String)
	 */
	public void execute(String s) {
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
		IProjectView model = vista.getModel();
		MapContext mapa = model.getMapContext();
		MapControl mapCtrl = vista.getMapControl();
		logger.debug("Command : " + s);

        if (s.equals("SHAPE_SELECTED")) {
			createShape(mapa);
			 ((ProjectDocument)vista.getModel()).setModified(true);
		} else if (s.equals("DXF_SELECTED")) {
			createDxf(mapa);
			((ProjectDocument)vista.getModel()).setModified(true);
		} else if (s.equals("ZOOM_SELECT")) {
			Rectangle2D selectedExtent = mapa.getSelectionBounds();

			if (selectedExtent != null) {
				mapa.getViewPort().setExtent(selectedExtent);
				((ProjectDocument)vista.getModel()).setModified(true);
			}
		}
	}

	/**
	 * Crea un nuevo shape.
	 *
	 * @param map FMap de donde coger las capas a copiar.
	 */
	private void createShape(MapContext map) {
		if (map.getSelectionBounds() != null) {
			JFileChooser jfc = new JFileChooser();
			jfc.addChoosableFileFilter(new GenericFileFilter("shp",
					PluginServices.getText(this, "Shapefile")));

			if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
				File file=jfc.getSelectedFile();
				if (!(file.getPath().endsWith(".shp") || file.getPath().endsWith(".SHP"))){
					file=new File(file.getPath()+".shp");
				}
				//SHP.SHPFileFromSelected(map, file);
				SelectedShapeVisitor ssv=new SelectedShapeVisitor();
				try {
					map.getLayers().process(ssv);
					}  catch (ReadDriverException e1) {
						throw new RuntimeException("No se espera que SelectByPointVisitor lance esta excepción",
								e1);
					} catch (VisitorException e1) {
						throw new RuntimeException("No se espera que SelectByPointVisitor lance esta excepción",
								e1);
					}
				IGeometry[] fgs=ssv.getSelectedGeometries();
				SelectableDataSource sds=ssv.getSelectableDataSource();
				BitSet bitset=ssv.getBitSet();
				try {
					sds.start();
					SHP.SHPFileFromGeometries(fgs,bitset,sds,file);
					sds.stop();
				} catch (ReadDriverException e2) {
					NotificationManager.addError("No se pudo escribir la capa", e2);
				}
			}
		} // else {

		//}
	}

	/**
	 * Crea un DXF partiendo de los objetos seleccionados. Desarrollado en el
	 * piloto de CAD. Lo de aquí no sirve.
	 * @param map
	 */
	private void createDxf(MapContext map) {
		if (map.getSelectionBounds() != null) {
			JFileChooser jfc = new JFileChooser();
			jfc.addChoosableFileFilter(new GenericFileFilter("dxf",
					PluginServices.getText(this, "Dxffiles")));

			if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
				File file=jfc.getSelectedFile();
				if (!(file.getPath().endsWith(".dxf") || file.getPath().endsWith(".DXF"))){
					file=new File(file.getPath()+".dxf");
				}
				//SHP.SHPFileFromSelected(map, file);
				SelectedShapeVisitor ssv=new SelectedShapeVisitor();
				try {
					map.getLayers().process(ssv);
					} catch (ReadDriverException e1) {
						throw new RuntimeException("No se espera que SelectByPointVisitor lance esta excepción",
								e1);
					} catch (VisitorException e) {
						throw new RuntimeException("No se espera que SelectByPointVisitor lance esta excepción",
								e);
					}
				IGeometry[] fgs=ssv.getSelectedGeometries();
				DxfGisWriter dxfGisWriter = new DxfGisWriter();
				try {
					// map.getLayers() devuelve solo una capa porque la
                    // herramienta está inhabilitada cuando hay varias capas
                    // seleccionadas.
                    FLayer layer = map.getLayers().getActives()[0];
                    //dxfGisWriter.write(fgs, layer, file);
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				/*SelectableDataSource sds=ssv.getSelectableDataSource();
				BitSet bitset=ssv.getBitSet();
				try {
					sds.start();
					SHP.SHPFileFromGeometries(fgs,bitset,sds,file);
					sds.stop();
				} catch (com.hardcode.gdbms.engine.data.DriverException e2) {
					NotificationManager.addError("No se pudo escribir la capa", e2);
				}*/
			}
		}
	}

	/**
	 * @see com.iver.mdiApp.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
															 .getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {
			MapContext mapa = ((View) f).getModel().getMapContext();

			//View v = (View) f;

			return mapa.getLayers().getLayersCount() > 0;
		} else {
			return false;
		}
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		View f = (View) PluginServices.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		FLayer[] selected = f.getModel().getMapContext().getLayers().getActives();
		if (selected.length == 1 && selected[0] instanceof FLyrVect && selected[0].isAvailable()){
			return true;
		}
		return false;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}
}
