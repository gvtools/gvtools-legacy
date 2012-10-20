/**
 *
 */
package com.iver.cit.gvsig.project.documents.view.toolListeners;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.MoveEvent;
import com.iver.cit.gvsig.fmap.tools.Events.RectangleEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PanListener;
import com.iver.cit.gvsig.fmap.tools.Listeners.RectangleListener;
import com.iver.cit.gvsig.project.documents.view.MapOverview;

/**
 * <p>
 * Listener for changes of the zoom caused by selecting a rectangular area on
 * the associated {@link MapOverview MapOverview} object with the first button
 * of the mouse.
 * </p>
 * 
 * <p>
 * If the kind of action was a movement on the associated object, updates the
 * <i>extent</i> of its rectangular area.
 * </p>
 * 
 * <p>
 * If the kind of action is the selection of a rectangular area, and is bigger
 * than 3x3 pixels, applies a <i>zoom in</i> operation centering its
 * <code>ViewPort</code> according the equivalent <i>extent</i> in map
 * coordinates.
 * </p>
 * 
 * @see ViewPort
 * 
 * @author jmvivo
 */
public class MapOverviewChangeZoomListener implements RectangleListener,
		PanListener {
	/**
	 * The image to display when the cursor is active.
	 */
	private final Image izoomin = PluginServices.getIconTheme()
			.get("cursor-zoom-in").getImage();

	/**
	 * The cursor used to work with this tool listener.
	 * 
	 * @see #getCursor()
	 */
	private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(
			izoomin, new Point(16, 16), "");

	/**
	 * Reference to the <code>MapControl</code> object that uses.
	 */
	protected MapControl mapControl;

	/**
	 * <p>
	 * Creates a new listener for changes of zoom at the associated
	 * {@link MapOverview MapOverview} object.
	 * </p>
	 * 
	 * @param mapControl
	 *            the <code>MapControl</code> object which represents the
	 *            <code>MapOverview</code>
	 */
	public MapOverviewChangeZoomListener(MapControl mapControl) {
		this.mapControl = mapControl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.RectangleListener#rectangle(com
	 * .iver.cit.gvsig.fmap.tools.Events.RectangleEvent)
	 */
	public void rectangle(RectangleEvent event) throws BehaviorException {

		if (!checkModifiers(event.getEvent())) {
			return;
		}
		MapOverview mov = (MapOverview) this.mapControl;
		ViewPort vpView = mov.getAssociatedMapContext().getViewPort();
		ViewPort vp = mov.getViewPort();

		if (vp.getExtent() != null && vpView.getExtent() != null) {

			// Recuperamos las coordenadas del evento en px
			Rectangle2D pxRectangle = event.getPixelCoordRect();
			// Recuperamos las coordenadas del evento en coordenadas de la vista
			// de localizador
			Rectangle2D realRectangle = event.getWorldCoordRect();

			if ((pxRectangle.getWidth() < 3) && (pxRectangle.getHeight() < 3)) {
				// rectangulo < 3 px no hacemos nada
				return;

			} else {
				// Cambiamos la extension de la vista asociada al localizador
				vpView.setExtent(realRectangle);
				mov.getAssociatedMapContext().invalidate();
				vpView.setExtent(realRectangle);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
	 */
	public Cursor getCursor() {
		return this.cur;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
	 */
	public boolean cancelDrawing() {
		return true; // ???
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PanListener#move(com.iver.cit
	 * .gvsig.fmap.tools.Events.MoveEvent)
	 */
	public void move(MoveEvent event) throws BehaviorException {

		if (!checkModifiers(event.getEvent())) {
			return;
		}
		System.out.println("mapOvervierChangeZoom");
		MapOverview mov = (MapOverview) this.mapControl;
		ViewPort vpView = mov.getAssociatedMapContext().getViewPort();
		ViewPort vp = mov.getViewPort();

		if (vp.getExtent() != null && vpView.getExtent() != null) {

			// Creamos un recuadro con las coordenadas del raton
			// traducidas a la del mapa
			Rectangle2D realRectangle = new Rectangle2D.Double();
			realRectangle.setFrameFromDiagonal(vp.toMapPoint(event.getFrom()),
					vp.toMapPoint(event.getTo()));

			// Establecemos la forma
			mov.refreshOverView(realRectangle);
		}

	}

	/**
	 * Determines if has pressed the button 1 of the mouse.
	 */
	private boolean checkModifiers(MouseEvent event) {
		int modifiers = event.getModifiers();
		int keyPressedMask = InputEvent.BUTTON1_MASK;
		return ((modifiers & keyPressedMask) == keyPressedMask);
	}
}
