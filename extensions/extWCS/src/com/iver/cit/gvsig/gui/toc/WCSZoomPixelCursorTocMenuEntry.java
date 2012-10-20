package com.iver.cit.gvsig.gui.toc;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrWCS;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.gui.toolListeners.WCSZoomPixelCursorListener;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.cit.gvsig.project.documents.view.toc.TocMenuEntry;
import com.iver.cit.gvsig.project.documents.view.toc.gui.FPopupMenu;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;

/**
 * @author Nacho Brodin <brodin_ign@gva.es>
 * 
 *         Entrada de menú para la activación de la funcionalidad de zoom a un
 *         pixel centrado en el cursor.
 */
public class WCSZoomPixelCursorTocMenuEntry extends TocMenuEntry {
	private JMenuItem properties;
	FLayer lyr = null;

	public void initialize(FPopupMenu m) {
		super.initialize(m);

		if (isTocItemBranch()) {
			lyr = getNodeLayer();
			// Opcciones para capas WCS
			if ((lyr instanceof FLyrWCS)) {
				properties = new JMenuItem(PluginServices.getText(this,
						"Zoom_pixel"));
				getMenu().add(properties);
				properties.setFont(FPopupMenu.theFont);
				getMenu().setEnabled(true);
				properties.addActionListener(this);

				IWindow window = PluginServices.getMDIManager()
						.getActiveWindow();

				if (!(window instanceof BaseView))
					return;

				MapControl mapCtrl = ((BaseView) window).getMapControl();

				StatusBarListener sbl = new StatusBarListener(mapCtrl);

				WCSZoomPixelCursorListener zp = new WCSZoomPixelCursorListener(
						mapCtrl);
				mapCtrl.addMapTool("zoom_pixel_cursor", new Behavior[] {
						new PointBehavior(zp), new MouseMovementBehavior(sbl) });
			}
		}
	}

	public void actionPerformed(ActionEvent e) {

		FLayer[] actives = getMapContext().getLayers().getActives();
		if (actives.length == 1) {
			lyr = getNodeLayer();
			IWindow window = PluginServices.getMDIManager().getActiveWindow();

			if (!(window instanceof BaseView))
				return;

			MapControl mapCtrl = ((BaseView) window).getMapControl();

			mapCtrl.setTool("zoom_pixel_cursor");
		}
	}
}