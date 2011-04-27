package com.iver.cit.gvsig.geoprocess.util;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.EditionManager;
import com.iver.cit.gvsig.EditionUtilities;
import com.iver.cit.gvsig.StopEditing;
import com.iver.cit.gvsig.exceptions.layers.StartEditionLayerException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.legend.CreateSpatialIndexMonitorableTask;

public class StopEditingClean extends StopEditing {
	
	private View vista;
	
	public void execute(String s) {
		com.iver.andami.ui.mdiManager.IWindow[] windows = PluginServices.getMDIManager().getAllWindows();
		
		for(int i=0;i<windows.length;i++) {
			if(windows[i] instanceof IView)
				vista = (View) windows[i];
		}
		
		boolean isStop=false;
		IProjectView model = vista.getModel();
		MapContext mapa = model.getMapContext();
		FLayers layers = mapa.getLayers();
		EditionManager edMan = CADExtension.getEditionManager();
		if (s.equals("STOPEDITING")) {
			vista.getMapControl().getCanceldraw().setCanceled(true);
			FLayer[] actives = layers.getActives();
			// TODO: Comprobar que solo hay una activa, o al menos
			// que solo hay una en edición que esté activa, etc, etc
			for (int i = 0; i < actives.length; i++) {
				if (actives[i] instanceof FLyrVect && actives[i].isEditing()) {
					FLyrVect lv = (FLyrVect) actives[i];
					MapControl mapControl = vista.getMapControl();
					VectorialLayerEdited lyrEd = (VectorialLayerEdited)	edMan.getActiveLayerEdited();
					try {
						lyrEd.clearSelection(false);
					} catch (ReadDriverException e) {
						e.printStackTrace();
					}
					isStop=stopEditing(lv, mapControl);
					if (isStop){
						lv.removeLayerListener(edMan);
						if (lv instanceof FLyrAnnotation){
							FLyrAnnotation lva=(FLyrAnnotation)lv;
				            lva.setMapping(lva.getMapping());
						}
					}
				}
			}
			if (isStop) {
				vista.getMapControl().setTool("zoomIn");
				vista.hideConsole();
				vista.repaintMap();
				CADExtension.clearView();

			}
		}
//		PluginServices.getMainFrame().enableControls();
	}

	public boolean stopEditing(FLyrVect layer, MapControl mapControl) {

		VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();

		try {
			if (layer.isWritable()) {
				// GUARDAMOS EL TEMA
				saveLayer(layer);
				vea.getCommandRecord().removeCommandListener(mapControl);
				layer.setEditing(false);
				if (layer.isSpatiallyIndexed()){
	            	if(layer.getISpatialIndex() != null){
						try {
							PluginServices.cancelableBackgroundExecution(new CreateSpatialIndexMonitorableTask((FLyrVect)layer));
						} catch (ReadDriverException e) {
							e.printStackTrace();
						}
	                }
		        }
				return true;
			}

		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		} catch (StartEditionLayerException e) {
			NotificationManager.addError(e);
		}
		return false;

	}
	
	private void saveLayer(FLyrVect layer) throws ReadDriverException {
		layer.setProperty("stoppingEditing",new Boolean(true));
		VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
		
		ISpatialWriter writer = (ISpatialWriter) vea.getWriter();
		com.iver.andami.ui.mdiManager.IWindow[] views = PluginServices.getMDIManager().getAllWindows();
		for (int j = 0; j < views.length; j++) {
			if (views[j] instanceof Table) {
				Table table = (Table) views[j];
				if (table.getModel().getAssociatedTable() != null
						&& table.getModel().getAssociatedTable().equals(layer)) {
					table.stopEditingCell();
				}
			}
		}
		vea.cleanSelectableDatasource();
		layer.setRecordset(vea.getRecordset()); // Queremos que el recordset del layer
		// refleje los cambios en los campos.
		ILayerDefinition lyrDef = EditionUtilities.createLayerDefinition(layer);
		String aux="FIELDS:";
		FieldDescription[] flds = lyrDef.getFieldsDesc();
		for (int i=0; i < flds.length; i++){
			aux = aux + ", " + flds[i].getFieldAlias();
		}
		System.err.println("Escribiendo la capa " + lyrDef.getName() + " con los campos " + aux);
		try {
			writer.initialize(lyrDef);
		} catch (InitializeWriterException e) {
			e.printStackTrace();
		}
		try {
			vea.stopEdition(writer, EditionEvent.GRAPHIC);
		} catch (StopWriterVisitorException e) {
			e.printStackTrace();
		}
		layer.setProperty("stoppingEditing",new Boolean(false));
	}
	
	public boolean isEnabled() {
		return super.isEnabled();
	}

	public boolean isVisible() {
		return super.isVisible();
	}
	
}

