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
package es.gva.cit.gvsig.gazetteer.loaders;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.rendering.FGraphicLabel;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

import es.gva.cit.gazetteer.querys.Feature;
import es.gva.cit.gazetteer.querys.GazetteerQuery;
import es.gva.cit.gvsig.gazetteer.DeleteSearchesExtension;

/**
 * This class is used to load a new feature like a layer in gvSIG
 * 
 * @author Jorge Piera Llodra (piera_jor@gva.es)
 */
public class FeatureLoader {
	private MathTransform crsTransform;

	/**
	 * @param projection
	 *            Server projection
	 */
	public FeatureLoader(String sProjection) {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof BaseView) {
			BaseView activeView = (BaseView) PluginServices.getMDIManager()
					.getActiveWindow();

			CoordinateReferenceSystem crs = ProjectionUtils.getCRS(sProjection);
			if (crs == null) {
				crs = activeView.getMapControl().getViewPort().getCrs();
			}

			crsTransform = ProjectionUtils.getCrsTransform(crs, activeView
					.getMapControl().getViewPort().getCrs());
		}
	}

	/**
	 * It makes a zoom in gvSIG
	 * 
	 * @param feature
	 * @param query
	 *            Query that contains advanced options to search and to show the
	 *            results
	 * @return true or false if fail
	 */
	public boolean load(Feature feature, GazetteerQuery query) {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof BaseView) {
			addAndDrawLabel(feature,
					query.getOptions().getAspect().isKeepOld(), query
							.getOptions().getAspect().isPaintCurrent());

			if (query.getOptions().getAspect().isGoTo()) {
				focusCenter(feature);
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * This method focus the toponim in the center of the view
	 * 
	 * @param feature
	 *            Feature that contains the coordinates
	 */
	private void focusCenter(Feature feature) {
		BaseView activeView = (BaseView) PluginServices.getMDIManager()
				.getActiveWindow();

		CoordinateReferenceSystem crs = activeView.getCrs();
		ViewPort viewPort = activeView.getMapControl().getViewPort();
		Point2D point = getReprojectedPoint(feature.getCoordinates());

		Rectangle2D zoomExtent = null;
		if (viewPort.getAdjustedExtent() == null) {

		} else {
			Toolkit kit = Toolkit.getDefaultToolkit();
			double dpi = kit.getScreenResolution();
			Rectangle2D extent = ProjectionUtils.getExtent(crs,
					viewPort.getAdjustedExtent(),
					new Double(25000).doubleValue(),
					new Double(viewPort.getImageWidth()).doubleValue(),
					new Double(viewPort.getImageHeight()).doubleValue(),
					MapContext.CHANGE[viewPort.getMapUnits()],
					MapContext.CHANGE[viewPort.getDistanceUnits()], dpi);
			if (extent != null) {
				zoomExtent = new Rectangle2D.Double(point.getX()
						- extent.getWidth() / 2, point.getY()
						- extent.getHeight() / 2, extent.getWidth(),
						extent.getHeight());
				activeView.getMapControl().getMapContext()
						.zoomToExtent(zoomExtent);
			}
		}

	}

	/**
	 * It adds a new Label to the current view
	 * 
	 * @param feature
	 *            To obtain the coordinates and the toponim name
	 * @param isRemoveOldClicked
	 *            To remove or keep the old searches
	 */
	private void addAndDrawLabel(Feature feature, boolean isRemoveOldClicked,
			boolean isMarkedPlaceClicked) {
		BaseView activeView = (BaseView) PluginServices.getMDIManager()
				.getActiveWindow();

		GraphicLayer lyr = activeView.getMapControl().getMapContext()
				.getGraphicsLayer();

		if (isRemoveOldClicked) {
			lyr.clearAllGraphics();
		}

		if (isMarkedPlaceClicked) {
			int idSymbol = lyr.addSymbol(getSymbol());

			IGeometry geom = ShapeFactory.createPoint2D(new FPoint2D(
					getReprojectedPoint(feature.getCoordinates())));

			FGraphicLabel theLabel = new FGraphicLabel(geom, idSymbol,
					feature.getName());
			lyr.addGraphic(theLabel);

			DeleteSearchesExtension.setVisible();
			PluginServices.getMainFrame().enableControls();
		}

		activeView.getMapControl().drawGraphics();

		// This line could look stupid, but is necessary because grawGraphics
		// does't
		// remove the old Graphics searched
		activeView
				.getMapControl()
				.getViewPort()
				.setExtent(activeView.getMapControl().getViewPort().getExtent());
	}

	/**
	 * Creates a FSymbol
	 * 
	 * @return FSymbol
	 */
	private FSymbol getSymbol() {
		FSymbol theSymbol = new FSymbol(FConstant.SYMBOL_TYPE_TEXT);
		theSymbol.setColor(Color.RED);
		theSymbol.setStyle(FConstant.SYMBOL_STYLE_MARKER_CIRCLE);
		theSymbol.setFontColor(Color.BLACK);
		theSymbol.setSizeInPixels(true);
		theSymbol.setSize(10);
		return theSymbol;
	}

	/**
	 * Reprojects the new point
	 * 
	 * @param ptOrig
	 *            Origin point
	 * @return FPoint2D
	 */
	private Point2D getReprojectedPoint(Point2D ptOrigin) {
		return ProjectionUtils.transform(ptOrigin, getCrsTransform());
	}

	/**
	 * @return the coordTrans
	 */
	public MathTransform getCrsTransform() {
		return crsTransform;
	}

}
