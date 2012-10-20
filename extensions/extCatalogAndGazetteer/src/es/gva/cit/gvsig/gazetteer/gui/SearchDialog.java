/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package es.gva.cit.gvsig.gazetteer.gui;

import java.awt.Frame;
import java.awt.geom.Rectangle2D;

import javax.swing.JDialog;

import org.gvsig.i18n.Messages;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.ColorEvent;
import com.iver.cit.gvsig.fmap.ExtentEvent;
import com.iver.cit.gvsig.fmap.ProjectionEvent;
import com.iver.cit.gvsig.fmap.ViewPortListener;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

import es.gva.cit.catalog.querys.Coordinates;
import es.gva.cit.catalog.utils.Frames;
import es.gva.cit.gazetteer.GazetteerClient;
import es.gva.cit.gazetteer.querys.Feature;
import es.gva.cit.gazetteer.ui.search.SearchDialogPanel;

/**
 * @author Jorge Piera Llodra (piera_jor@gva.es)
 */
public class SearchDialog extends SearchDialogPanel implements IWindow,
		ViewPortListener {
	public WindowInfo m_windowInfo = null;
	public ConnectDialog parentDialog = null;
	public JDialog frame = null;

	public SearchDialog(GazetteerClient client, Object serverConnectFrame) {
		super(client, serverConnectFrame);
		parentDialog = (ConnectDialog) serverConnectFrame;
		setViewChangeListener();
		loadViewPortCoordinates();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.ui.mdiManager.View#getViewInfo()
	 */
	public WindowInfo getWindowInfo() {
		if (m_windowInfo == null) {
			m_windowInfo = new WindowInfo(WindowInfo.PALETTE);
			m_windowInfo.setTitle(Messages.getText("gazetteer_search") + " ["
					+ getCurrentServer() + "]");
			m_windowInfo.setHeight(80);
			m_windowInfo.setWidth(525);
		}
		return m_windowInfo;

	}

	public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}

	protected void showResultsActionPerformed(Feature[] features) {
		JDialog panel = new JDialog((Frame) PluginServices.getMainFrame(),
				false);
		Frames.centerFrame(panel, 420, 258);
		panel.setTitle(Messages.getText("gazetteer_search"));
		panel.setResizable(false);

		ShowResultsDialog dialog = new ShowResultsDialog(panel, client,
				features, lowerPanel.getResultsByPage(), getQuery());

		panel.getContentPane().add(dialog);
		panel.show();
	}

	protected void closeButtonActionPerformed() {
		closeJDialog();
	}

	/**
	 * Size button action performed
	 */
	protected void resizeButtonActionPerformed() {
		if (isMinimized) {
			frame.setSize(frame.getWidth(), 450);
			frame.doLayout();
			getLowerPanel().setVisible(true);
			getUpperPanel().setUpIcon();
		} else {
			frame.setSize(frame.getWidth(), 115);
			getLowerPanel().setVisible(false);
			getUpperPanel().setDownIcon();
		}
		isMinimized = !isMinimized;
	}

	/**
	 * Return button action
	 */
	protected void lastButtonActionPerformed() {
		closeJDialog();
		ConnectDialog serverConnect = (ConnectDialog) serverConnectFrame;
		parentDialog.setVisible(true);
		parentDialog.getControlsPanel().enableSearchButton(false);
		PluginServices.getMDIManager().addWindow(serverConnect);
	}

	public void closeJDialog() {
		frame.setVisible(false);
	}

	/**
	 * This method loads the view coordinates to the catalog search dialog
	 * 
	 */
	private void loadViewPortCoordinates() {
		BaseView activeView = (BaseView) PluginServices.getMDIManager()
				.getActiveWindow();

		Rectangle2D r2d = activeView.getMapControl().getViewPort().getExtent();

		try {
			getLowerPanel().setCoordinates(
					new Coordinates(r2d.getMinX(), r2d.getMaxY(),
							r2d.getMaxX(), r2d.getMinY()));
		} catch (NullPointerException E) {
			// We cant retrieve the coordinates if it doesn't
			// exist a loaded layer
		}
	}

	/*
	 * This method joins the viewPort event to the listener
	 */
	private void setViewChangeListener() {
		BaseView activeView = (BaseView) PluginServices.getMDIManager()
				.getActiveWindow();

		activeView.getMapControl().getViewPort().addViewPortListener(this);

	}

	public void extentChanged(ExtentEvent e) {
		loadViewPortCoordinates();

	}

	public void backColorChanged(ColorEvent e) {
		// TODO Auto-generated method stub

	}

	public void projectionChanged(ProjectionEvent e) {
		loadViewPortCoordinates();
	}

	/**
	 * @param frame
	 *            the frame to set
	 */
	public void setFrame(JDialog dialog) {
		this.frame = dialog;
	}

}
