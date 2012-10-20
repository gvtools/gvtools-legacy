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
 * $Id: BufferCmd.java 7304 2006-09-15 10:44:24Z caballero $
 * $Log$
 * Revision 1.4  2006-09-15 10:42:54  caballero
 * extensibilidad de documentos
 *
 * Revision 1.3  2006/08/29 07:56:30  cesar
 * Rename the *View* family of classes to *Window* (ie: SingletonView to SingletonWindow, ViewInfo to WindowInfo, etc)
 *
 * Revision 1.2  2006/06/20 18:21:48  azabala
 * refactorización para que todos los nuevos geoprocesos cuelguen del paquete impl
 *
 * Revision 1.1  2006/05/24 21:08:45  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.3  2006/04/11 18:00:06  azabala
 * Primera version que funciona
 *
 * Revision 1.2  2006/04/07 08:34:38  fjp
 * Lanzando una excepción adecuada
 *
 * Revision 1.1  2006/03/28 16:24:39  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.wizard;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.gui.GeoprocessPaneContainer;
import com.iver.cit.gvsig.geoprocess.impl.buffer.BufferGeoprocessController;
import com.iver.cit.gvsig.geoprocess.impl.buffer.gui.GeoProcessingBufferPanel;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * It processes user button push on BufferGeoprocessToolbar
 * 
 * @author azabala
 * 
 */
public class BufferCmd implements AndamiCmd {

	public void execute() {
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
		final FLayers layers = vista.getModel().getMapContext().getLayers();
		GeoProcessingBufferPanel geoProcessingBufferPanel = new GeoProcessingBufferPanel(
				layers);
		GeoprocessPaneContainer container = new GeoprocessPaneContainer(
				geoProcessingBufferPanel);
		BufferGeoprocessController controller = new BufferGeoprocessController();
		controller.setView(geoProcessingBufferPanel);
		container.setCommand(controller);
		container.validate();
		container.repaint();
		PluginServices.getMDIManager().addWindow(container);
	}

}
