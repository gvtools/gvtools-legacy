/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
 *   46009 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package org.gvsig.tools.annotations.labeling.fmap.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.BitSet;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.gvsig.symbology.fmap.labeling.parse.LabelExpressionParser;
import org.gvsig.symbology.fmap.labeling.parse.ParseException;
import org.gvsig.symbology.fmap.rendering.filter.operations.Expression;
import org.gvsig.symbology.fmap.rendering.filter.operations.ExpressionException;
import org.gvsig.tools.annotations.fmap.AnnotationsDrawer;
import org.gvsig.tools.annotations.labeling.gui.ConfigLabelingExpression;
import org.gvsig.tools.annotations.labeling.gui.SingleLabelingToolUI;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiFrame.SelectableToolBar;
import com.iver.cit.gvsig.EditionUtilities;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.layers.StartEditionLayerException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

/**
 *
 * SingleLabelingTool.java
 *
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es Mar 3, 2008
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es> Oct 2008
 */
public class SingleLabelingTool implements PointListener, PropertyChangeListener {
	private MapControl mc ;
	private Annotation_Layer targetLayer = null;
	private SingleLabelingToolUI ui = null;
	public static String TOOLNAME = "single-labeling";
	private AnnotationsDrawer drawingCache;
	private int minTolerance = 2;
	private int maxTolerance = 8;
	private LayerColListener layerListener;

	public SingleLabelingTool(MapControl mc, SingleLabelingToolUI toolUI) {
		this.mc = mc;
		initLayerListener();
		if (this.ui==null) {
			this.ui = toolUI;
			toolUI.addPropertyChangeListener(SingleLabelingToolUI.TOOL_CLOSED_PROP, this);
			toolUI.addPropertyChangeListener(SingleLabelingToolUI.TARGET_LAYER_CHANGED_PROP, this);
		}
	}

	private void initLayerListener() {
		layerListener = new LayerColListener();
		mc.getMapContext().getLayers().addLayerCollectionListener(layerListener);
	}

	public Annotation_Layer getTargetLayer() {
		return targetLayer;
	}

	public void setTargetLayer(Annotation_Layer layer) {
		this.targetLayer = layer;
	}

	public Cursor getCursor() {
		final ImageIcon img = PluginServices.getIconTheme().get("single-labeling-tool");
		Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(img.getImage(),
				new Point(img.getIconWidth()-4, img.getIconHeight()-1), "");

		return cur;
	}

	public void point(PointEvent event) throws BehaviorException {
		if (getTargetLayer()==null) {
			return;
		}
		FLayer[] activeLayers = mc.getMapContext().getLayers().getActives();
        if (activeLayers.length==0) {
        	return;
        }

        Point2D screenPoint = event.getPoint();
        Point2D mapPoint = mc.getViewPort().toMapPoint((int) screenPoint.getX(), (int) screenPoint.getY());

        BitSet bitset = null;
        int i=0;
        do { // iterate on the layers until we find a clicked feature on any of the layers
        	if (activeLayers[i] instanceof FLyrVect) {
        		FLyrVect layer = (FLyrVect) activeLayers[i];
        		try {
        			int currentTol=minTolerance;
        			do {
        				double tol = mc.getViewPort().toMapDistance(currentTol);
        				bitset = layer.queryByPoint(mapPoint, tol);
        			}
        			while (bitset.isEmpty() && (++currentTol<=maxTolerance));
        			if (!bitset.isEmpty()) {
        				for (int featIndex = bitset.nextSetBit(0); featIndex != -1; featIndex = bitset.nextSetBit(featIndex + 1)) {
        					// TODO reproyectar de coordenadas de la vista a coordenadas de la capa de anotaciones
        					IGeometry geom = ShapeFactory.createPoint2D(mapPoint.getX(), mapPoint.getY());
        					Object prop = layer.getProperty(ConfigLabelingExpression.PROPERTYNAME);
        					if (prop!=null && prop instanceof String) {
        						addToAnnotLayer(geom, getLabel(layer, (String) prop, featIndex));
        					}
        				}
        			}


        		} catch (ReadDriverException e) {
        			PluginServices.getLogger().error(e.getMessage(), e);
        		} catch (VisitorException e) {
        			PluginServices.getLogger().error(e.getMessage(), e);
        		}
        	}
        	i++;
        }
        while(i<activeLayers.length
        		&& (bitset==null || bitset.isEmpty())); // iterate on the layers until we find a clicked feature on any of the layers

        ui.activateWindow();
	}

	private String getLabel(FLyrVect layer, String strExpr, int featIndex) {
		Value[] values;
		try {
			values = layer.getRecordset().getRow(featIndex);

			String[] fieldNames = layer.getRecordset().getFieldNames();
			Hashtable<String, Value> symbol_table = new Hashtable<String, Value>();
			for (int i=0; i<fieldNames.length; i++) {
				symbol_table.put(fieldNames[i], values[i]);
			}
			LabelExpressionParser p = new LabelExpressionParser(
					new StringReader(strExpr),symbol_table);
			p.LabelExpression();
			Expression expr = (Expression) p.getStack().pop();
			Object labelContents;

				labelContents = expr.evaluate();
				String[] texts;
				if (String[].class.equals(labelContents.getClass())) {
					texts = (String[]) labelContents;
				} else {
					texts = new String[] { labelContents.toString() };
				}
				StringBuilder builder = new StringBuilder();
				for (int i=0; i<texts.length; i++) {
					builder.append(texts[i]);
				}
				return builder.toString();
		}
		catch (ExpressionException e) {
			PluginServices.getLogger().error(e.getMessage(), e);
		}
		catch (ReadDriverException e) {
			PluginServices.getLogger().error(e.getMessage(), e);
		} catch (ParseException e) {
			PluginServices.getLogger().error(e.getMessage(), e);
		}
		return "";
	}

	private void addToAnnotLayer(IGeometry geom, String labelText) {
		try {
			FLyrVect target = getTargetLayer();
			VectorialEditableAdapter vea = startEdition(target);
			String[] fields = target.getRecordset().getFieldNames();
			Value[] values = new Value[fields.length];
			for (int i=0; i<fields.length; i++) {
				if (fields[i].equalsIgnoreCase("Text")) {
					values[i] = ValueFactory.createValue(labelText);
				}
				else if (fields[i].equalsIgnoreCase("TypeFont")) {
					values[i] = ValueFactory.createValue(ui.getTextPropertiesPanel().getFontType());
				}
				else if (fields[i].equalsIgnoreCase("StyleFont")) {
					values[i] = ValueFactory.createValue(ui.getTextPropertiesPanel().getFontStyle());
				}
				else if (fields[i].equalsIgnoreCase("Color")) {
					values[i] = ValueFactory.createValue(ui.getTextPropertiesPanel().getColor().getRGB());
				}
				else if (fields[i].equalsIgnoreCase("Height")) {
					values[i] = ValueFactory.createValue(ui.getTextPropertiesPanel().getTextHeight());
				}
				else if (fields[i].equalsIgnoreCase("Rotate")) {
					values[i] = ValueFactory.createValue(ui.getTextPropertiesPanel().getRotation());
				}

			}
			addFeature(vea, geom, values);
		} catch (StartEditionLayerException e) {
			PluginServices.getLogger().error(e.getMessage(), e);
		} catch (ExpansionFileWriteException e) {
			PluginServices.getLogger().error(e.getMessage(), e);
		} catch (ValidateRowException e) {
			PluginServices.getLogger().error(e.getMessage(), e);
		} catch (ReadDriverException e) {
			PluginServices.getLogger().error(e.getMessage(), e);
		} catch (DriverIOException e) {
			PluginServices.getLogger().error(e.getMessage(), e);
		} catch (IOException e) {
			PluginServices.getLogger().error(e.getMessage(), e);
		}
	}

	public void pointDoubleClick(PointEvent event) throws BehaviorException {
		// TODO Auto-generated method stub

	}

	public boolean cancelDrawing() {
		// TODO Auto-generated method stub
		return false;
	}

	protected VectorialEditableAdapter startEdition(FLyrVect lyr) throws StartEditionLayerException {
		if (!lyr.isEditing()) {
			if (drawingCache==null) {
				drawingCache = new AnnotationsDrawer();
			}
			drawingCache.setTargetLayer(getTargetLayer());
			mc.getMapContext().setMapContextDrawer(drawingCache);

			lyr.setEditing(true);
		}
		if (lyr.getSource() instanceof VectorialEditableAdapter) {
			return (VectorialEditableAdapter) lyr.getSource();
		}
		else throw new StartEditionLayerException("Layer source is not VectorialEditableAdapter", new RuntimeException());
	}

	protected void stopEdition(FLyrVect lyr) {
		if (lyr!=null && lyr.isEditing()) {
			mc.getMapContext().setMapContextDrawerClass(null); // set default drawer
			if (lyr.getSource() instanceof VectorialEditableAdapter) {
				VectorialEditableAdapter vea = (VectorialEditableAdapter) lyr.getSource();
				ISpatialWriter writer = (ISpatialWriter) vea.getWriter();
				ILayerDefinition lyrDef;
				try {
					lyrDef = EditionUtilities.createLayerDefinition(lyr);
					writer.initialize(lyrDef);
					vea.saveEdits(writer, EditionEvent.GRAPHIC);
					lyr.setEditing(false);
				} catch (ReadDriverException e) {
					PluginServices.getLogger().error(e);
				} catch (InitializeWriterException e) {
					PluginServices.getLogger().error(e);
				} catch (StopWriterVisitorException e) {
					PluginServices.getLogger().error(e);
				} catch (StartEditionLayerException e) {
					PluginServices.getLogger().error(e);
				}
			}
		}
	}

	protected void addFeature(VectorialEditableAdapter layer, IGeometry geom, Value[] values) throws DriverIOException, IOException, ExpansionFileWriteException, ValidateRowException, ReadDriverException {
		addFeature(layer, geom, values, "AddLabel");
	}

	protected void addFeature(VectorialEditableAdapter layer, IGeometry geom, Value[] values, String comment) throws DriverIOException, IOException, ExpansionFileWriteException, ValidateRowException, ReadDriverException {
		mc.getMapContext().beginAtomicEvent();
		String newFID = layer.getNewFID();
		DefaultFeature df = new DefaultFeature(geom, values, newFID);
		layer.addRow(df, comment, EditionEvent.GRAPHIC);
		mc.getMapContext().endAtomicEvent();
		mc.drawMap(false);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof SingleLabelingToolUI) {
			SingleLabelingToolUI ui = (SingleLabelingToolUI) evt.getSource();
			if (evt.getPropertyName().equals(SingleLabelingToolUI.TOOL_CLOSED_PROP)) {
				// tool windows was closed, save edits now
				Annotation_Layer layer = getTargetLayer();
				stopEdition(layer);
				Object model = ui.getWindowModel();
				if (model instanceof BaseView) {
					BaseView view = (BaseView) model;
					PluginServices.getMDIManager().getWindowInfo(view).setSelectedTool("zoomIn");
					if (PluginServices.getMDIManager().getActiveWindow()==view) {
						// if view is active, then setSelectedTool is not enough
						mc.setTool("zoomIn");
						SelectableToolBar[] toolbars = PluginServices.getMainFrame().getToolbars();

						for (int i=0; i<toolbars.length; i++) {
							if (toolbars[i].getName().equals(PluginServices.getText(this, "View_Tools"))) {
								toolbars[i].setSelectedTool("ZOOM_IN");
								break;
							}
						}
					}
				}
			}
			else if (evt.getPropertyName().equals(SingleLabelingToolUI.TARGET_LAYER_CHANGED_PROP)){
				stopEdition(this.targetLayer); // stop edition just in case we were editing the old target layer
				this.targetLayer = ui.getTargetLayer();
				if (drawingCache!=null) {
					drawingCache.setTargetLayer(this.getTargetLayer());
				}
			}
		}
	}

	private class LayerColListener implements LayerCollectionListener {
		public void layerAdded(LayerCollectionEvent e) {
			// TODO Auto-generated method stub

		}

		public void layerAdding(LayerCollectionEvent e) throws CancelationException {
			// TODO Auto-generated method stub

		}

		public void layerMoved(LayerPositionEvent e) {
			// TODO Auto-generated method stub

		}

		public void layerMoving(LayerPositionEvent e) throws CancelationException {
			// TODO Auto-generated method stub

		}

		public void layerRemoved(LayerCollectionEvent e) {
			// TODO Auto-generated method stub

		}

		public void layerRemoving(LayerCollectionEvent e)
				throws CancelationException {
			try {
				if (e.getAffectedLayer()==getTargetLayer()) {
					setTargetLayer(null);
					ui.setTargetLayer(null);
				}
			}
			catch (Exception ex) { // ensure we don't disturb the removal process
				PluginServices.getLogger().error(ex.getMessage(), ex);
			}

		}

		public void visibilityChanged(LayerCollectionEvent e)
				throws CancelationException {
			// TODO Auto-generated method stub

		}

	}
}
