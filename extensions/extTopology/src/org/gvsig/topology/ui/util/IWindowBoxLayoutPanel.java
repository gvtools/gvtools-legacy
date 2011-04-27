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
package org.gvsig.topology.ui.util;

import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

/**
 * A box layout panel which can be added to ANDAMI's mdi manager as a modal and
 * resizable IWindow.
 * @author Alvaro Zabala
 *
 */
public class IWindowBoxLayoutPanel extends BoxLayoutPanel implements IWindow{
	private static final long serialVersionUID = -8972358453750122827L;
	private static final int DEFAULT_WINDOW_MODE = WindowInfo.MODALDIALOG|WindowInfo.RESIZABLE;
	
	
	
	WindowInfo winfo = null;
	
	public IWindowBoxLayoutPanel(String title, int w, int h, int windowMode){
		super();
		winfo = new WindowInfo(windowMode);
		winfo.setTitle(title);
		winfo.setWidth(w);
		winfo.setHeight(h);
	}
	
	public IWindowBoxLayoutPanel(String title, int w, int h){
		this(title, w, h, DEFAULT_WINDOW_MODE);
	}
	
	
	public WindowInfo getWindowInfo() {
		return winfo;
	}
	
	public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}
	
}
