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
package com.iver.cit.gvsig.cad;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.topology.Topology;

import statemap.State;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.cad.sm.TopologicalEditVertexCADToolContext;
import com.iver.cit.gvsig.cad.sm.TopologicalEditVertexCADToolContext.TopologicalEditVertexCADToolState;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.layers.StartEditionLayerException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.cit.gvsig.fmap.edition.AnnotationEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool;
import com.iver.cit.gvsig.gui.panels.TextFieldEdit;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * CAD tool to make a topological edition
 * 
 * (all the vertex of the affected geometries of a topology's layers will be
 * moved simultaneusly).
 * 
 * 
 * 
 * 
 * @author Alvaro Zabala
 * 
 */
public class TopologicalEditVertexCADTool extends SelectionCADTool {
	// FIXME
	/*
	 * La herramienta hace lo que tiene que hacer. El unico problema de
	 * funcionamiento que da es el siguiente: a) picamos en una geometria
	 * (poligono) pero no en su borde, por tanto, ni se pueden seleccionar
	 * handlers ni insertar.
	 * 
	 * b) el estado pasa a "selectedfeatures"; y debería dibujarse el poligono
	 * seleccionado con los handlers y el color de selección.
	 * 
	 * No obstante, se dibuja correctamente.
	 */
	public static final String COMMAND_STRING = "_topologicaleditvertex";

	/**
	 * Snap tolerance in screen pixels.
	 */
	public static int tolerance = 4;

	/**
	 * Finite state machine (based in FSM library) to manage state transitions
	 * of this tools
	 */
	private TopologicalEditVertexCADToolContext _fsm;

	protected Point2D firstPoint;

	protected String nextState;

	/**
	 * Edited rows which have selected handlers
	 */
	protected ArrayList<DefaultRowEditedWithLyrEdited> rowselectedHandlers = new ArrayList<DefaultRowEditedWithLyrEdited>();

	protected String type = PluginServices.getText(this, "simple");

	protected boolean multipleSelection = false;

	/**
	 * Crea un nuevo SelectionCADTool.
	 */
	public TopologicalEditVertexCADTool() {
	}

	/**
	 * Método de incio, para poner el código de todo lo que se requiera de una
	 * carga previa a la utilización de la herramienta.
	 */
	public void init() {
		_fsm = new TopologicalEditVertexCADToolContext(this);
		setNextTool("selection");
		setType(PluginServices.getText(this, "simple"));
	}

	public void transition(double x, double y, InputEvent event) {

		try {
			_fsm.addPoint(x, y, event);
		} catch (Exception e) {
			init();
		}

		List<FLyrVect> editionLyrs = new ArrayList<FLyrVect>();
		VectorialLayerEdited vle = getVLE();
		FLyrVect lv = (FLyrVect) vle.getLayer();
		editionLyrs.add(lv);
		Topology topology = getParentIfTopology(vle);
		if (topology != null) {
			List topologyLyrs = topology.getLayers();
			for (int i = 0; i < topologyLyrs.size(); i++) {
				FLyrVect lyrVect = (FLyrVect) topologyLyrs.get(i);
				if (lyrVect == vle.getLayer())
					continue;
				VectorialLayerEdited vleTopo = (VectorialLayerEdited) this
						.getCadToolAdapter().getEditionManager()
						.getLayerEdited(lyrVect);
				if (vleTopo != null)
					editionLyrs.add((FLyrVect) vleTopo.getLayer());
			}// for
		}// if topology

		IWindow[] views = (IWindow[]) PluginServices.getMDIManager()
				.getAllWindows();
		for (int i = 0; i < views.length; i++) {
			if (views[i] instanceof Table) {
				Table table = (Table) views[i];
				if (table.getModel().getAssociatedTable() != null) {
					for (int j = 0; j < editionLyrs.size(); j++) {
						FLyrVect lyr = editionLyrs.get(j);
						if (!lyr.isEditing())
							continue;
						if (table.getModel().getAssociatedTable().equals(lyr)) {
							table.updateSelection();
							break;
						}// if
					}// for j
				}// if
			}// if views instanceof
		}// for i

	}

	public void transition(double d) {
		_fsm.addValue(d);
	}

	public void transition(String s) throws CommandException {
		if (!super.changeCommand(s)) {
			_fsm.addOption(s);
		}
	}

	public String getNextState() {
		return nextState;
	}

	protected void pointDoubleClick(MapControl map) throws ReadDriverException {
		FLayer[] actives = map.getMapContext().getLayers().getActives();
		for (int i = 0; i < actives.length; i++) {
			if (actives[i] instanceof FLyrAnnotation && actives[i].isEditing()) {
				FLyrAnnotation lyrAnnotation = (FLyrAnnotation) actives[i];
				lyrAnnotation.setSelectedEditing();
				lyrAnnotation.setInEdition(lyrAnnotation.getRecordset()
						.getSelection().nextSetBit(0));
				FLabel fl = lyrAnnotation
						.getLabel(lyrAnnotation.getInEdition());
				if (fl != null) {
					View vista = (View) PluginServices.getMDIManager()
							.getActiveWindow();
					TextFieldEdit tfe = new TextFieldEdit(lyrAnnotation);
					tfe.show(
							vista.getMapControl().getViewPort()
									.fromMapPoint(fl.getOrig()),
							vista.getMapControl());
				}// if
			}// if
		}// for
	}

	public void addPoint(double x, double y, InputEvent event) {

		if (event != null && ((MouseEvent) event).getClickCount() == 2) {
			try {
				pointDoubleClick((MapControl) event.getComponent());
			} catch (ReadDriverException e) {
				NotificationManager.addError(e.getMessage(), e);
			}
			return;
		}

		State actualState = _fsm.getPreviousState();
		String status = actualState.getName();

		if (status.equals("TopologicalEdition.FirstPoint")) {
			firstPoint = new Point2D.Double(x, y);
			pointsPolygon.add(firstPoint);
		} else if (status.equals("TopologicalEdition.SecondPoint")) {
		} else if (status.equals("TopologicalEdition.WithFeatures")) {
		} else if (status.equals("TopologicalEdition.WithHandlers")) {
			VectorialLayerEdited vle = getVLE();
			finalizeVertexEdition(x, y, vle);

			Topology topology = getParentIfTopology(vle);
			if (topology != null) {
				List topologyLyrs = topology.getLayers();
				for (int i = 0; i < topologyLyrs.size(); i++) {
					FLyrVect lyrVect = (FLyrVect) topologyLyrs.get(i);
					if (lyrVect == vle.getLayer())
						continue;
					VectorialLayerEdited vleTopo = (VectorialLayerEdited) this
							.getCadToolAdapter().getEditionManager()
							.getLayerEdited(lyrVect);
					if (vleTopo != null) {
						finalizeVertexEdition(x, y, vleTopo);
					}
				}// for
			}// if topology

			firstPoint = new Point2D.Double(x, y);

			clearSelections();
		}
	}

	/**
	 * Overwrites this DefaultCADTool's method because this tool could put many
	 * layers in 'dirty' status.
	 */
	public void refresh() {
		VectorialLayerEdited vle = getVLE();
		// vle.getLayer().setDirty(true);

		Topology topology = getParentIfTopology(vle);
		if (topology != null) {
			List topologyLyrs = topology.getLayers();
			for (int i = 0; i < topologyLyrs.size(); i++) {
				FLyrVect lyrVect = (FLyrVect) topologyLyrs.get(i);
				if (lyrVect == vle.getLayer())
					continue;
				// if(lyrVect.isEditing())
				// lyrVect.setDirty(true);
			}// for
		}
		getCadToolAdapter().getMapControl().rePaintDirtyLayers();
	}

	/**
	 * DefaultCADTool#modifyFeature doesnt fit our needs, because we are working
	 * with more than one layer.
	 * 
	 * @param index
	 * @param row
	 * @param vle
	 */
	public void modifyFeature(int index, IFeature row, VectorialLayerEdited vle) {
		try {
			vle.getVEA().modifyRow(index, row, getName(), EditionEvent.GRAPHIC);
		} catch (ValidateRowException e) {
			NotificationManager.addError(e.getMessage(), e);
		} catch (ExpansionFileWriteException e) {
			NotificationManager.addError(e.getMessage(), e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(), e);
		}
		draw(row.getGeometry().cloneGeometry());
	}

	/**
	 * This method move the selected handlers and finalize the edition process
	 * of this tool.
	 * 
	 * @param x
	 * @param y
	 * @param vle
	 * @param vea
	 */
	private void finalizeVertexEdition(double x, double y,
			VectorialLayerEdited vle) {
		VectorialEditableAdapter vea = vle.getVEA();
		ArrayList selectedHandler = vle.getSelectedHandler();
		ArrayList selectedRow = vle.getSelectedRow();
		vea.startComplexRow();
		ArrayList<DefaultRowEditedWithLyrEdited> selectedRowsAux = new ArrayList<DefaultRowEditedWithLyrEdited>();

		for (int i = 0; i < selectedRow.size(); i++) {
			IRowEdited row = (IRowEdited) selectedRow.get(i);
			IFeature feat = (IFeature) row.getLinkedRow().cloneRow();
			IGeometry ig = feat.getGeometry();

			if (vea instanceof AnnotationEditableAdapter) {
				UtilFunctions.moveGeom(ig, x - firstPoint.getX(), y
						- firstPoint.getY());
			} else {
				// Movemos los handlers que hemos seleccionado
				// previamente dentro del método select()
				Handler[] handlers = ig.getHandlers(IGeometry.SELECTHANDLER);

				for (int k = 0; k < selectedHandler.size(); k++) {
					Handler h = (Handler) selectedHandler.get(k);
					for (int j = 0; j < handlers.length; j++) {
						if (h.getPoint().equals(handlers[j].getPoint()))
							handlers[j].set(x, y);
					}// for j
				}// for k
			}// else
			modifyFeature(row.getIndex(), feat, vle);
			DefaultRowEdited newRowEdited = new DefaultRowEdited(feat,
					IRowEdited.STATUS_MODIFIED, row.getIndex());
			selectedRowsAux.add(new DefaultRowEditedWithLyrEdited(vle,
					newRowEdited));
		}// for selectedRow size

		vle.setSelectionCache(VectorialLayerEdited.SAVEPREVIOUS,
				selectedRowsAux);
		String description = PluginServices.getText(this, "move_handlers");
		vea.endComplexRow(description);
	}

	/**
	 * Receives second point
	 * 
	 * @param x
	 * @param y
	 * @return numFeatures selected
	 */
	public int selectWithSecondPoint(double x, double y, InputEvent event) {
		VectorialLayerEdited vle = getVLE();
		PluginServices.getMDIManager().setWaitCursor();
		vle.selectWithSecondPoint(x, y);
		ArrayList selectedRow = vle.getSelectedRow();
		PluginServices.getMDIManager().restoreCursor();
		if (selectedRow.size() > 0) {
			nextState = "TopologicalEdition.WithSelectedFeatures";
		} else
			nextState = "TopologicalEdition.FirstPoint";
		return selectedRow.size();
	}

	/**
	 * Método para dibujar la lo necesario para el estado en el que nos
	 * encontremos.
	 * 
	 * @param g
	 *            Graphics sobre el que dibujar.
	 * @param selectedGeometries
	 *            BitSet con las geometrías seleccionadas.
	 * @param x
	 *            parámetro x del punto que se pase para dibujar.
	 * @param y
	 *            parámetro x del punto que se pase para dibujar.
	 */
	public void drawOperation(Graphics g, double x, double y) {
		TopologicalEditVertexCADToolState actualState = _fsm.getState();
		String status = actualState.getName();

		VectorialLayerEdited vle = getVLE();
		if (vle == null)
			return;

		ViewPort vp = vle.getLayer().getMapContext().getViewPort();

		List<VectorialLayerEdited> editionLyrs = new ArrayList<VectorialLayerEdited>();
		editionLyrs.add(vle);

		Topology topology = getParentIfTopology(vle);
		if (topology != null) {
			List topologyLyrs = topology.getLayers();
			for (int i = 0; i < topologyLyrs.size(); i++) {
				FLyrVect lyrVect = (FLyrVect) topologyLyrs.get(i);
				if (lyrVect == vle.getLayer())
					continue;
				VectorialLayerEdited vleTopo = (VectorialLayerEdited) this
						.getCadToolAdapter().getEditionManager()
						.getLayerEdited(lyrVect);
				if (vleTopo != null)
					editionLyrs.add(vleTopo);
			}// for
		}// if topology

		if (status.equals("TopologicalEdition.SecondPoint")) {
			for (int i = 0; i < editionLyrs.size(); i++) {
				VectorialLayerEdited vl = editionLyrs.get(i);

				GeneralPathX elShape = new GeneralPathX(
						GeneralPathX.WIND_EVEN_ODD, 4);
				elShape.moveTo(firstPoint.getX(), firstPoint.getY());
				elShape.lineTo(x, firstPoint.getY());
				elShape.lineTo(x, y);
				elShape.lineTo(firstPoint.getX(), y);
				elShape.lineTo(firstPoint.getX(), firstPoint.getY());
				ShapeFactory.createPolyline2D(elShape).draw((Graphics2D) g, vp,
						DefaultCADTool.geometrySelectSymbol);
				Image img = vl.getSelectionImage();
				g.drawImage(img, 0, 0, null);

			}// for
		} else if (status.equals("TopologicalEdition.WithHandlers")) {
			for (int i = 0; i < editionLyrs.size(); i++) {
				VectorialLayerEdited vl = editionLyrs.get(i);
				ArrayList selectedHandler = vl.getSelectedHandler();

				int selectionSize = selectedHandler.size();
				double[] xPrev = new double[selectionSize];
				double[] yPrev = new double[selectionSize];

				for (int k = 0; k < selectionSize; k++) {
					Handler h = (Handler) selectedHandler.get(k);
					xPrev[k] = h.getPoint().getX();
					yPrev[k] = h.getPoint().getY();
					h.set(x, y);
				}

				for (int j = 0; j < rowselectedHandlers.size(); j++) {
					DefaultRowEditedWithLyrEdited rowEd = rowselectedHandlers
							.get(j);
					// we skip those features which not are of the current VLE
					if (!(rowEd.getLayerEdited().getLayer().equals(vl
							.getLayer())))
						continue;
					IGeometry geom = ((IFeature) rowEd.getLinkedRow())
							.getGeometry().cloneGeometry();
					g.setColor(Color.gray);
					geom.draw((Graphics2D) g, vp,
							DefaultCADTool.axisReferencesSymbol);
				}// for j

				for (int k = 0; k < selectionSize; k++) {
					Handler h = (Handler) selectedHandler.get(k);
					h.set(xPrev[k], yPrev[k]);
				}

			}// for i

		} else {
			for (int i = 0; i < editionLyrs.size(); i++) {
				VectorialLayerEdited vl = editionLyrs.get(i);
				ArrayList selectedHandler = vl.getSelectedHandler();
				if (!vl.getLayer().isVisible())
					return;
				try {
					Image imgSel = vl.getSelectionImage();
					if (imgSel != null)
						g.drawImage(imgSel, 0, 0, null);
					Image imgHand = vl.getHandlersImage();
					if (imgHand != null)
						g.drawImage(imgHand, 0, 0, null);
				} catch (Exception e) {
				}
			}// for i
		}
	}

	public void addOption(String s) {
		State actualState = _fsm.getPreviousState();
		String status = actualState.getName();
		if (s.equals(PluginServices.getText(this, "cancel"))) {
			init();
			return;
		}
		if (status.equals("TopologicalEdition.FirstPoint")) {
			setType(s);
			return;
		}
		init();
	}

	public void addValue(double d) {
	}

	public String getStatus() {
		try {
			State actualState = _fsm.getPreviousState();
			String status = actualState.getName();
			return status;
		} catch (NullPointerException e) {
			return "TopologicalEdition.FirstPoint";
		}
	}

	private void clearSelection(VectorialLayerEdited vle)
			throws ReadDriverException {
		ArrayList selectedRow = vle.getSelectedRow();
		ArrayList selectedHandlers = vle.getSelectedHandler();
		selectedRow.clear();
		selectedHandlers.clear();
		VectorialEditableAdapter vea = vle.getVEA();
		FBitSet selection = vea.getSelection();
		selection.clear();
		vea.setSelectionImage(null);
		vea.setHandlersImage(null);
	}

	private void clearSelections() {
		rowselectedHandlers.clear();
		try {
			VectorialLayerEdited vle = getVLE();
			clearSelection(vle);

			Topology topology = getParentIfTopology(vle);
			if (topology != null) {
				List topologyLyrs = topology.getLayers();
				for (int i = 0; i < topologyLyrs.size(); i++) {
					FLyrVect lyrVect = (FLyrVect) topologyLyrs.get(i);
					if (lyrVect == vle.getLayer())
						continue;
					VectorialLayerEdited vleTopo = (VectorialLayerEdited) this
							.getCadToolAdapter().getEditionManager()
							.getLayerEdited(lyrVect);
					if (vleTopo != null)
						clearSelection(vleTopo);
				}// for
			}// if topology

		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
	}

	public void end() {
		if (!getNextTool().equals("selection"))
			CADExtension.setCADTool(getNextTool(), false);
		clearSelections();
	}

	public String getName() {
		return PluginServices.getText(this, "selection_");
	}

	/**
	 * Returns the FLayers parent of a VectorialLayerEdited's layer, if it is a
	 * topology.
	 * 
	 * @param vle
	 * @return
	 */
	private Topology getParentIfTopology(VectorialLayerEdited vle) {
		Topology topology = null;
		FLayers parentLyrs = vle.getLayer().getParentLayer();
		if (parentLyrs instanceof Topology) {
			topology = (Topology) parentLyrs;
		}
		return topology;
	}

	private void startEdition(Topology topology) {
		// getVLE returns the layer edited associated to the active layer.
		// So setEditing(true) changes the active layer, we need to save a
		// reference
		VectorialLayerEdited vle = getVLE();
		/*
		 * FIXME FLyrVect.setEditing(true) changes the TOC's active layer, to
		 * ensure a layer which is editing is the active in TOC. Until we could
		 * change this behavior, we must save the previous selected layer
		 */
		View view = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = view.getMapControl();
		FLayer[] activeLyrs = mapCtrl.getMapContext().getLayers().getActives();
		// Ponemos el resto de temas desactivados
		if (mapCtrl != null) {
			mapCtrl.getMapContext().getLayers().setActive(false);
		}

		// CADExtension.setCADTool("_selection",true);

		List topologyLayers = topology.getLayers();
		for (int i = 0; i < topologyLayers.size(); i++) {
			FLyrVect lyr = (FLyrVect) topologyLayers.get(i);
			if (lyr == vle.getLayer())// we skip the actual edited layer
				continue;
			try {
				if (!lyr.isEditing())
					lyr.setEditing(true);
			} catch (StartEditionLayerException e) {
				NotificationManager.addError(e.getMessage(), e);
			}
		}// for

		if (mapCtrl != null)
			mapCtrl.getMapContext().getLayers().setActive(false);
		for (int i = 0; i < activeLyrs.length; i++) {
			activeLyrs[i].setActive(true);
		}
	}

	class DefaultRowEditedWithLyrEdited extends DefaultRowEdited {

		VectorialLayerEdited layerEdited;

		public DefaultRowEditedWithLyrEdited(VectorialLayerEdited lyrEdited,
				IRowEdited rowEdited) {
			super(rowEdited.getLinkedRow(), rowEdited.getStatus(), rowEdited
					.getIndex());
			this.layerEdited = lyrEdited;
		}

		public VectorialLayerEdited getLayerEdited() {
			return layerEdited;
		}
	}

	private void selectFeaturesInTopology(Topology topology, double x, double y) {
		List topologyLyrs = topology.getLayers();
		for (int i = 0; i < topologyLyrs.size(); i++) {
			FLyrVect lyrVect = (FLyrVect) topologyLyrs.get(i);
			if (lyrVect == getVLE().getLayer())
				continue;
			VectorialLayerEdited vleTopo = (VectorialLayerEdited) this
					.getCadToolAdapter().getEditionManager()
					.getLayerEdited(lyrVect);
			vleTopo.selectWithPoint(x, y, multipleSelection);
		}// for
	}

	/**
	 * Overwrites default cad tool getVLE() method.
	 */
	public VectorialLayerEdited getVLE() {
		VectorialLayerEdited solution = null;
		IWindow activeWindow = PluginServices.getMDIManager().getActiveWindow();
		if (!(activeWindow instanceof View))
			return null;
		View vista = (View) activeWindow;
		MapContext mapContext = vista.getMapControl().getMapContext();
		LayersIterator it = new LayersIterator(mapContext.getLayers());
		while (it.hasNext()) {
			FLayer aux = (FLayer) it.next();
			if (!aux.isActive())
				continue;
			if (aux.isEditing()) {
				// FIXME When we put a layer in edition, only a TOC's layer
				// could be active.
				solution = (VectorialLayerEdited) CADExtension
						.getEditionManager().getLayerEdited(aux);
				break;
			}
		}// while
		return solution;
	}

	private List<DefaultRowEditedWithLyrEdited> getSelectedRowsWithTopology(
			Topology topology) {
		List<DefaultRowEditedWithLyrEdited> solution = new ArrayList<DefaultRowEditedWithLyrEdited>();

		ArrayList activeLyrSelection = getVLE().getSelectedRow();

		int selectionSize = activeLyrSelection.size();
		if (topology != null && selectionSize > 0) {

			for (int i = 0; i < selectionSize; i++) {
				IRowEdited rowEdited = (IRowEdited) activeLyrSelection.get(i);
				DefaultRowEditedWithLyrEdited newRowEdited = new DefaultRowEditedWithLyrEdited(
						getVLE(), rowEdited);
				solution.add(newRowEdited);
			}

			// we only select features in topology lyrs if the active layer has
			// a selected feature
			for (int i = 0; i < topology.getLayers().size(); i++) {
				FLyrVect lyrVect = (FLyrVect) topology.getLayers().get(i);
				if (lyrVect == getVLE().getLayer())
					continue;
				VectorialLayerEdited vleTopo = (VectorialLayerEdited) this
						.getCadToolAdapter().getEditionManager()
						.getLayerEdited(lyrVect);
				ArrayList topoSelectedRows = vleTopo.getSelectedRow();
				for (int j = 0; j < topoSelectedRows.size(); j++) {
					IRowEdited rowEdited = (IRowEdited) topoSelectedRows.get(j);
					DefaultRowEditedWithLyrEdited newRowEdited = new DefaultRowEditedWithLyrEdited(
							vleTopo, rowEdited);
					solution.add(newRowEdited);
				}
			}// for
		}

		return solution;
	}

	public boolean selectFeatures(double x, double y, InputEvent event) {
		TopologicalEditVertexCADToolState actualState = _fsm.getState();

		String status = actualState.getName();
		VectorialLayerEdited vle = getVLE();

		Topology topology = getParentIfTopology(vle);
		if (topology != null)// refinar esto, solo poner en edicion aquellas
								// capas que tengan vertice afectado
			startEdition(topology);

		if ((status.equals("TopologicalEdition.FirstPoint"))
				|| (status.equals("TopologicalEdition.WithSelectedFeatures"))) {
			PluginServices.getMDIManager().setWaitCursor();
			firstPoint = new Point2D.Double(x, y);
			vle.selectWithPoint(x, y, multipleSelection);
			if (topology != null)
				selectFeaturesInTopology(topology, x, y);
			PluginServices.getMDIManager().restoreCursor();
		}

		List<DefaultRowEditedWithLyrEdited> selectedRow = getSelectedRowsWithTopology(topology);

		if (selectedRow.size() > 0) {

			Point2D auxPoint = new Point2D.Double(x, y);
			double min = getCadToolAdapter().getMapControl().getViewPort()
					.toMapDistance(tolerance);

			// FIXME CAMBIAR ESTO. ANTES TENIA SENTIDO PORQUE LA CAPA ACTIVA DEL
			// TOC
			// SOLO PERMITIA SELECCIONAR UN ELEMENTO. AHORA PERMITE SELECCIONAR
			// VARIOS
			DefaultRowEditedWithLyrEdited rowEdited = selectedRow.get(0);
			vle.getSelectedHandler().clear();
			selectHandlers((IFeature) rowEdited.getLinkedRow(),
					rowEdited.getIndex(), vle, min, auxPoint);

			if (vle.getSelectedHandler().size() > 0) {
				nextState = "TopologicalEdition.WithHandlers";
			} else {
				IFeature selectedFeature = (IFeature) rowEdited.getLinkedRow();
				IGeometry originalGeometry = selectedFeature.getGeometry();
				if (FGeometryUtil.isInBoundary(originalGeometry, auxPoint, min)) {
					// we have picked in a boundary point, so we insert a new
					// vertex.
					IGeometry geomEdited = FGeometryUtil.insertVertex(
							originalGeometry, auxPoint, min);
					DefaultFeature df = new DefaultFeature(geomEdited,
							selectedFeature.getAttributes(), rowEdited.getID());
					VectorialEditableAdapter vea = vle.getVEA();
					try {
						vea.modifyRow(rowEdited.getIndex(), df,
								PluginServices.getText(this, "add_vertex"),
								EditionEvent.GRAPHIC);
					} catch (ExpansionFileWriteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExpansionFileReadException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ValidateRowException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ReadDriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					selectedFeature.setGeometry(geomEdited);
					selectHandlers(df, rowEdited.getIndex(),
							rowEdited.getLayerEdited(), min, auxPoint);
					nextState = "TopologicalEdition.WithHandlers";
				} else {
					nextState = "TopologicalEdition.WithSelectedFeatures";
				}
			}// else selected handlers size > 0

			// here we select vertices in topology's layer
			if (nextState.equals("TopologicalEdition.WithHandlers")
					&& selectedRow.size() > 1) {

				for (int i = 1; i < selectedRow.size(); i++) {
					rowEdited = selectedRow.get(i);

					rowEdited.getLayerEdited().getSelectedHandler().clear();

					selectHandlers((IFeature) rowEdited.getLinkedRow(),
							rowEdited.getIndex(), rowEdited.getLayerEdited(),
							min, auxPoint);

					if (rowEdited.getLayerEdited().getSelectedHandler().size() == 0) {
						// we try to insert a new vertex in the picked point

						IFeature selectedFeature = (IFeature) rowEdited
								.getLinkedRow();
						IGeometry originalGeometry = selectedFeature
								.getGeometry();
						if (FGeometryUtil.isInBoundary(originalGeometry,
								auxPoint, min)) {
							// we have picked in a boundary point, so we insert
							// a new
							// vertex.

							// TODO CAMBIAR ESTO POR INSERTHANDLER
							// CUANDO HAYAMOS COMPROBADO QUE FUNCIONA BIEN
							IGeometry geomEdited = FGeometryUtil.insertVertex(
									originalGeometry, auxPoint, min);
							DefaultFeature df = new DefaultFeature(geomEdited,
									selectedFeature.getAttributes(),
									rowEdited.getID());
							VectorialEditableAdapter vea = rowEdited
									.getLayerEdited().getVEA();
							try {
								vea.modifyRow(rowEdited.getIndex(), df,
										PluginServices.getText(this,
												"add_vertex"),
										EditionEvent.GRAPHIC);
							} catch (ExpansionFileWriteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExpansionFileReadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ValidateRowException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ReadDriverException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							selectedFeature.setGeometry(geomEdited);
							selectHandlers(df, rowEdited.getIndex(),
									rowEdited.getLayerEdited(), min, auxPoint);
						}
					}// else selected handlers size > 0
				}
			}
			return true;
		} else {
			nextState = "TopologicalEdition.SecondPoint";
			return true;

		}
	}

	private boolean selectHandlers(VectorialLayerEdited vle, Point2D auxPoint,
			double tam) {
		for (int i = 0; i < vle.getSelectedRow().size(); i++) {
			IRowEdited rowEd = (IRowEdited) vle.getSelectedRow().get(i);
			IFeature fea = (IFeature) rowEd.getLinkedRow();
			Handler[] handlers = fea.getGeometry().getHandlers(
					IGeometry.SELECTHANDLER);
			// y miramos los handlers de cada entidad seleccionada
			double min = tam;
			// int hSel = -1;

			for (int j = 0; j < handlers.length; j++) {
				Point2D handlerPoint = handlers[j].getPoint();
				double distance = auxPoint.distance(handlerPoint);
				if (distance <= min) {
					min = distance;
					// hSel = j;
					vle.getSelectedHandler().add(handlers[j]);
					rowselectedHandlers.add(new DefaultRowEditedWithLyrEdited(
							vle, rowEd));
				}
			}// for handlers

			if (vle.getSelectedHandler().size() == 0) {
				IGeometry originalGeometry = fea.getGeometry();
				if (FGeometryUtil.isInBoundary(originalGeometry, auxPoint, tam)) {
					// we have picked in a boundary point, so we insert a new
					// vertex.
					insertHandler(auxPoint, rowEd, tam, vle);
				}// if is in boundary
			}
		}// for vle.getselectedrow

		return vle.getSelectedHandler().size() != 0;
	}

	public int selectHandlers(double x, double y, InputEvent event) {
		Point2D auxPoint = new Point2D.Double(x, y);

		VectorialLayerEdited vle = getVLE();
		vle.getSelectedHandler().clear();

		// Se comprueba si se pincha en una gemometría
		PluginServices.getMDIManager().setWaitCursor();

		double tam = getCadToolAdapter().getMapControl().getViewPort()
				.toMapDistance(tolerance);
		rowselectedHandlers.clear();
		boolean selectedHandlers = selectHandlers(vle, auxPoint, tam);

		if (selectedHandlers) {
			Topology parentTopology = getParentIfTopology(vle);
			if (parentTopology != null) {
				List lyrs = parentTopology.getLayers();
				for (int i = 0; i < lyrs.size(); i++) {
					FLyrVect lyrVect = (FLyrVect) lyrs.get(i);
					if (lyrVect.equals(vle.getLayer()))
						continue;
					VectorialLayerEdited vleTopo = (VectorialLayerEdited) this
							.getCadToolAdapter().getEditionManager()
							.getLayerEdited(lyrVect);
					selectHandlers(vleTopo, auxPoint, tam);
				}// for
			}// parentTopology
		}// if
		PluginServices.getMDIManager().restoreCursor();
		if (selectedHandlers)
			return 1;
		else
			return 0;
	}

	/**
	 * From a given row edited, when newVertex is in its geometry boundary but
	 * its not a vertex, inserts newvertex in the boundary.
	 * 
	 * @param newVertex
	 *            point which is on a linear segment and its going to be a new
	 *            vertex
	 * @param rowEdited
	 * @param snapTolerance
	 */
	private void insertHandler(Point2D newVertex, IRowEdited rowEdited,
			double snapTolerance, VectorialLayerEdited vle) {
		IFeature fea = (IFeature) rowEdited.getLinkedRow();
		IGeometry originalGeometry = fea.getGeometry();
		IGeometry geomEdited = FGeometryUtil.insertVertex(originalGeometry,
				newVertex, snapTolerance);
		DefaultFeature df = new DefaultFeature(geomEdited, fea.getAttributes(),
				rowEdited.getID());
		VectorialEditableAdapter vea = vle.getVEA();
		try {
			vea.modifyRow(rowEdited.getIndex(), df,
					PluginServices.getText(this, "add_vertex"),
					EditionEvent.GRAPHIC);
			// TODO En realidad lo que deberiamos hacer es cambiar rowEdited (su
			// seleccion)
			fea.setGeometry(geomEdited);
		} catch (ExpansionFileWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpansionFileReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidateRowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		selectHandlers(df, rowEdited.getIndex(), vle, snapTolerance, newVertex);
	}

	private void selectHandlers(IFeature feature, int rowEditedIndex,
			VectorialLayerEdited vle, double snapTolerance,
			Point2D selectionPoint) {

		IGeometry geomEdited = feature.getGeometry();
		Handler[] newHandlers = geomEdited.getHandlers(IGeometry.SELECTHANDLER);
		for (int h = 0; h < newHandlers.length; h++) {
			if (newHandlers[h].getPoint().distance(selectionPoint) < snapTolerance) {
				vle.getSelectedHandler().add(newHandlers[h]);
				DefaultRowEditedWithLyrEdited rowEdited = new DefaultRowEditedWithLyrEdited(
						vle, new DefaultRowEdited(feature,
								IRowEdited.STATUS_MODIFIED, rowEditedIndex));
				rowselectedHandlers.add(rowEdited);
			}// if
		}// for
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type.equals("S") || type.equals("s")) {
			this.type = PluginServices.getText(this, "simple");
		} else {
			this.type = type;
		}
		pointsPolygon.clear();
	}

	public String toString() {
		return "_selection";
	}

	public void multipleSelection(boolean b) {
		multipleSelection = b;

	}
}
