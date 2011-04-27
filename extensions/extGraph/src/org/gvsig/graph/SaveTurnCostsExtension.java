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
import java.nio.charset.Charset;
import java.sql.Types;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.gvsig.graph.core.GvTurn;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.gui.RouteControlPanel;
import org.gvsig.graph.gui.TurnCostsTableChooser;
import org.gvsig.gui.beans.swing.JFileChooser;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.DefaultRow;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.TableDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.dbf.DbfWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class SaveTurnCostsExtension extends Extension {
	/**
	 * Component to control flags and routes
	 * */
	private RouteControlPanel controlPanel;
	
//	private String fieldType;
//	private String fieldDist;
//	private String fieldSense;

	public void initialize() {

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
				Network net = (Network) lyr.getProperty("network");

				if ( net != null)
				{
					try {
						save_turncosts(lyrVect, mapControl);
					} catch (ReadDriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}


	}

	private void save_turncosts(FLyrVect lyrVect, MapControl mapControl) throws ReadDriverException {
		Network net = (Network) lyrVect.getProperty("network");

		if ( net != null)
		{
			String curDir = System.getProperty("user.dir");

			JFileChooser fileChooser = new JFileChooser("dbf files", new File(curDir));
			fileChooser.setFileFilter(new FileFilter() {

				@Override
				public boolean accept(File f) {
					if (f.isDirectory())
						return true;
					String path = f.getPath().toLowerCase();
					if (path.endsWith(".dbf"))
						return true;
					return false;
				}

				@Override
				public String getDescription() {
					return (PluginServices.getText(this, "Ficheros_dbf"));
				}
				
			});
			int res = fileChooser.showSaveDialog((Component) PluginServices.getMainFrame());
			if (res==JFileChooser.APPROVE_OPTION) {
				File dbfFile =fileChooser.getSelectedFile();
				if (!dbfFile.getPath().toLowerCase().endsWith(".dbf"))
					dbfFile = new File(dbfFile.getPath() + ".dbf");

				FieldDescription[] fields = new FieldDescription[3];
				FieldDescription fieldFromId = new FieldDescription();
				fieldFromId.setFieldName("from");
				fieldFromId.setFieldType(Types.INTEGER);

				FieldDescription fieldToId = new FieldDescription();
				fieldToId.setFieldName("to");
				fieldToId.setFieldType(Types.INTEGER);

				FieldDescription fieldTurnCost = new FieldDescription();
				fieldTurnCost.setFieldName("turncost");
				fieldTurnCost.setFieldType(Types.DOUBLE);
				fieldTurnCost.setFieldDecimalCount(2);
				
				fields[0] = fieldFromId;
				fields[1] = fieldToId;
				fields[2] = fieldTurnCost;
								
				DbfWriter dbfWriter = new DbfWriter();
				dbfWriter.setFile(dbfFile);
				
				// We create a table definition for turncosts table.
				ITableDefinition tableDef = new TableDefinition();
				tableDef.setFieldsDesc(fields);
				tableDef.setName("turcosts");


				try {
					dbfWriter.initialize(tableDef);
					dbfWriter.setCharset(Charset.defaultCharset());
					
					dbfWriter.preProcess();
					
					for (int i=0; i < net.getTurnCosts().size(); i++) {
						GvTurn turn =  net.getTurnCosts().get(i);
						
						Value[] values = new Value[fields.length];
						values[0] = ValueFactory.createValue(turn.getIdArcFrom());
						values[1] = ValueFactory.createValue(turn.getIdArcTo());
						values[2] = ValueFactory.createValue(turn.getCost());
						DefaultRow myRow = new DefaultRow(values, i + "");
						IRowEdited editedRow = new DefaultRowEdited(myRow,
								DefaultRowEdited.STATUS_ADDED, i);
	
						dbfWriter.process(editedRow);
	
					}
					dbfWriter.postProcess();
				} catch (InitializeWriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (StartWriterVisitorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ProcessWriterVisitorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (StopWriterVisitorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		else
		{
			JOptionPane.showMessageDialog((JComponent) PluginServices.getMDIManager().getActiveWindow(),
						PluginServices.getText(this, "la_capa_no_tiene_red_asociada"));
		}
	}

	public boolean isEnabled() {
		IWindow f = PluginServices.getMDIManager()
		 .getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
		    View v = (View) f;
			MapContext map = v.getMapControl().getMapContext();
			SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
			while (it.hasNext())
			{
				FLayer aux = it.next();
				if (!aux.isAvailable())
					continue;

				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");

				if ( net != null)
				{
					if (net.getTurnCosts().size() > 0)
						return true;
				}
			}
			return false;

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
					if (!lyrVect.isAvailable())
						return false;
					int shapeType ;
					try {
						shapeType = lyrVect.getShapeType();
//						if (shapeType == FShape.LINE)
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
