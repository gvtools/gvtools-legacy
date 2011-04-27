package org.gvsig.rastertools.roi.ui.listener;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Point2D;

import org.gvsig.fmap.raster.grid.roi.VectorialROI;
import org.gvsig.gui.beans.table.exceptions.NotInitializeException;
import org.gvsig.raster.util.RasterToolsUtil;
import org.gvsig.rastertools.roi.ui.ROIsTablePanel;

import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleMarkerSymbol;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.MeasureEvent;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.iver.cit.gvsig.fmap.tools.Listeners.PolylineListener;

public class DrawMouseViewListener implements PolylineListener,PointListener {
	
	private ROIsTablePanel 	tablePanel 	= null;
	private VectorialROI 		roi 				= null;

	public DrawMouseViewListener(ROIsTablePanel roiManagerPanel) {
		this.tablePanel = roiManagerPanel;
	}

	public void pointFixed(MeasureEvent event) throws BehaviorException {

	}

	public void points(MeasureEvent event) throws BehaviorException {

	}

	public void polylineFinished(MeasureEvent event) throws BehaviorException {
		GeneralPathX gp = event.getGP();
        IGeometry geometry = null;
        if (tablePanel.getMapControl().getCurrentTool().equals("drawPolygonROI"))
        	geometry = ShapeFactory.createPolygon2D(gp);
        else
        	geometry = ShapeFactory.createPolyline2D(gp);
		String roiName = "";

		int selectedRow;
		try {
			selectedRow = tablePanel.getTable().getSelectedRow();
			roiName = (String)tablePanel.getTable().getModel().getValueAt(selectedRow,0);
			roi = (VectorialROI)tablePanel.getROI(roiName);
			if(roi == null) {
				RasterToolsUtil.messageBoxError("error_roi_not_selected", tablePanel, new NullPointerException("ROI no seleccionada DrawMouseViewListener L 61"));
				return;
			}
			roi.addGeometry(geometry);
			int numGeometries;
			
			ISymbol sym = null;
			Color geometryColor = (Color)tablePanel.getTable().getModel().getValueAt(selectedRow, 4);
			
			if(tablePanel.getPolygonToolButton().isSelected()){
				numGeometries = ((Integer)tablePanel.getTable().getModel().getValueAt(selectedRow,1)).intValue();
				tablePanel.getTable().getModel().setValueAt(new Integer(numGeometries+1), selectedRow, 1);
				sym =SymbologyFactory.createDefaultFillSymbol();
				((IFillSymbol)sym).setFillColor(geometryColor);
			}
			else{
				numGeometries = ((Integer)tablePanel.getTable().getModel().getValueAt(selectedRow,2)).intValue();
				tablePanel.getTable().getModel().setValueAt(new Integer(numGeometries+1), selectedRow, 2);
				sym =SymbologyFactory.createDefaultLineSymbol();
				((ILineSymbol)sym).setLineColor(geometryColor);
			}
						
			GraphicLayer graphicLayer = tablePanel.getMapControl().getMapContext().getGraphicsLayer();
			
			FGraphic fGraphic = new FGraphic(geometry,graphicLayer.addSymbol(sym)); 
			tablePanel.getMapControl().getMapContext().getGraphicsLayer().addGraphic(fGraphic);
			tablePanel.getRoiGraphics(roiName).add(fGraphic);
			tablePanel.getMapControl().drawGraphics();
		} catch (NotInitializeException e) {
			RasterToolsUtil.messageBoxError("error_tabla_rois", tablePanel, e);
		}
		
	}

	public boolean cancelDrawing() {
		return true;
	}

	public Cursor getCursor() {
		return tablePanel.getToolCursor();
	}

	public void point(PointEvent event) throws BehaviorException {
		Point2D point = event.getPoint();
		Point2D p= tablePanel.getMapControl().getViewPort().toMapPoint(point);
        IGeometry geometry = ShapeFactory.createPoint2D(p.getX(),p.getY());
		String roiName = "";
		int selectedRow;
		try {
			selectedRow = tablePanel.getTable().getSelectedRow();
			roiName = (String)tablePanel.getTable().getModel().getValueAt(selectedRow,0);
			roi = (VectorialROI)tablePanel.getROI(roiName);
			roi.addGeometry(geometry);
			int numPoints = ((Integer)tablePanel.getTable().getModel().getValueAt(selectedRow,3)).intValue();
			tablePanel.getTable().getModel().setValueAt(new Integer(numPoints+1), selectedRow, 3);
			//tablePanel.getTable().getModel().setValueAt(new Integer(roi.getValues()), selectedRow, 5);
			
			GraphicLayer graphicLayer = tablePanel.getMapControl().getMapContext().getGraphicsLayer();
			Color geometryColor = (Color)tablePanel.getTable().getModel().getValueAt(selectedRow, 4);
			ISymbol sym = SymbologyFactory.createDefaultMarkerSymbol();
			((IMarkerSymbol)sym).setColor(geometryColor);
			((SimpleMarkerSymbol)sym).setStyle(SimpleMarkerSymbol.CIRCLE_STYLE);
			
			FGraphic fGraphic = new FGraphic(geometry,graphicLayer.addSymbol(sym)); 
			tablePanel.getMapControl().getMapContext().getGraphicsLayer().addGraphic(fGraphic);
			tablePanel.getRoiGraphics(roiName).add(fGraphic);
			tablePanel.getMapControl().drawGraphics();
		} catch (NotInitializeException e) {
			RasterToolsUtil.messageBoxError("error_tabla_rois", tablePanel, e);
		} /*catch (GridException e) {
			e.printStackTrace();
		}*/
	}

	public void pointDoubleClick(PointEvent event) throws BehaviorException {	
	}

}
