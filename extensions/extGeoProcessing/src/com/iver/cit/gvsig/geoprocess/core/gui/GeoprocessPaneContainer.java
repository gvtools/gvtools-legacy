/*
 * Created on 28-mar-2006
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
 * $Id: GeoprocessPaneContainer.java 24989 2008-11-12 12:28:13Z jcampos $
 * $Log$
 * Revision 1.10  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.9  2006/11/15 08:01:54  caballero
 * solución al guardar la interfaz gráfica en el proyecto
 *
 * Revision 1.8  2006/10/23 10:27:23  caballero
 * ancho y alto del panel
 *
 * Revision 1.7  2006/09/18 11:02:20  caballero
 * cambio tamaño container
 *
 * Revision 1.6  2006/08/29 07:56:30  cesar
 * Rename the *View* family of classes to *Window* (ie: SingletonView to SingletonWindow, ViewInfo to WindowInfo, etc)
 *
 * Revision 1.5  2006/08/29 07:13:56  cesar
 * Rename class com.iver.andami.ui.mdiManager.View to com.iver.andami.ui.mdiManager.IWindow
 *
 * Revision 1.4  2006/08/11 16:12:27  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/07/04 16:42:37  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/05/25 08:20:43  jmvivo
 * Modificado para que, si el proceso no ha podido ejecutarse, no cierre la ventana
 *
 * Revision 1.1  2006/05/24 21:13:09  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.1  2006/04/11 17:55:51  azabala
 * primera version en cvs
 *
 * Revision 1.2  2006/04/07 19:00:58  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/28 16:26:45  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.gvsig.gui.beans.AcceptCancelPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessController;
/**
 * This GUI class is a container to show gui components associated to
 * a geoprocess.
 * To correctly execute the geoprocess associated, this PaneContainer must
 * receive an AndamiCommand instance, which is controller class that
 * knows how to invoque geoprocess
 * @author azabala
 *
 */
public class GeoprocessPaneContainer extends JPanel implements IWindow {
	private static final long serialVersionUID = 1L;
	private AcceptCancelPanel buttonPanel = null;
	private JPanel mainPanel = new JPanel();  //  @jve:decl-index=0:visual-constraint="10,10"
	private WindowInfo viewInfo = null;
	private IGeoprocessController controller;

	private ActionListener okActionListener = new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			if (controller.launchGeoprocess()) {
				//So controller will launch geoprocess in background,
				//we can call cancel to close dialog
				cancel();
			}
		}
	};

	private ActionListener cancelActionListener = new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			cancel();
		}
	};

	/**
	 * Constructor from GUI component associated to a geoprocess
	 * @param mainPanel
	 */
	public GeoprocessPaneContainer(JPanel mainPanel) {
		super();
		this.mainPanel = mainPanel;
		initialize();
	}
	/**
	 * Implementation of ViewInfo andami interface.
	 */
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setTitle(PluginServices.getText(this,
					"Herramientas_de_analisis"));
			viewInfo.setWidth(controller.getWidth());
			viewInfo.setHeight(controller.getHeight());
		}
		return viewInfo;
	}
	
	public Object getWindowProfile(){
		return WindowInfo.DIALOG_PROFILE;
	}

	/**
	 * Sets AndamiCmd instance, which knows how to run geoprocess
	 * from user inputs in main panel.
	 * @param command
	 */
	public void setCommand(IGeoprocessController controller){
		this.controller = controller;
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
//		this.setLayout(new BorderLayout(10, 10));
		this.setLayout(new BorderLayout());
//		mainPanel.setSize(new java.awt.Dimension(430,390));
		this.setSize(new java.awt.Dimension(570,460));
		this.add(getMainPanel(), java.awt.BorderLayout.NORTH);
		this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);

		this.validate();
	}

	private JPanel getMainPanel() {
		return mainPanel;
	}
	/**
	 * This method initializes buttonPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private AcceptCancelPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new AcceptCancelPanel();
			buttonPanel.setOkButtonActionListener(okActionListener);
			buttonPanel.setCancelButtonActionListener(cancelActionListener);
		}
		return buttonPanel;
	}

	/**
	 * Closes parent dialog
	 *
	 */
	public void cancel(){
		if (PluginServices.getMainFrame() == null) {
			Container container = getParent();
			Container parentOfContainer = null;
			while(! (container instanceof Window)){
				parentOfContainer = container.getParent();
				container = parentOfContainer;
			}
			((Window)container).dispose();
		}else {
			PluginServices.getMDIManager().closeWindow(GeoprocessPaneContainer.this);
		}
	}

}  //  @jve:decl-index=0:visual-constraint="31,13"
