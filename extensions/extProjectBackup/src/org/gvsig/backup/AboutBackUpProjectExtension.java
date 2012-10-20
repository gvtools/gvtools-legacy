package org.gvsig.backup;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.About;
import com.iver.cit.gvsig.gui.panels.FPanelAbout;

/**
 * <p>
 * Backup GVP Extension of the <i>Consejería de Medio Ambiente de la Junta de
 * Castilla y León.</i>
 * </p>
 * project.
 * 
 * @author Vicente Caballero Navarro (vicente.caballero@iver.es)
 * @author Jaume Domínguez Faus (jaume.dominguez@iver.es)
 * @author César Martínez Izquierdo (cesar.martinez@iver.es)
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 * @author José Manuel Vivó Arnal (josemanuel.vivo@iver.es)
 */
public class AboutBackUpProjectExtension extends Extension {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#postInitialize()
	 */
	public void postInitialize() {
		About about = (About) PluginServices.getExtension(About.class);
		FPanelAbout panelAbout = about.getAboutPanel();
		java.net.URL aboutURL = this.getClass().getResource("/about.htm");
		panelAbout.addAboutUrl(
				PluginServices.getText(this, "extProjectBackup"), aboutURL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		return false;
	}
}
