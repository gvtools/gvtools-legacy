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

/*
 * AUTHORS (In addition to CIT):
 * 2008 Prodevelop S.L. main development
 */

package org.gvsig.normalization.extensions;

import org.gvsig.normalization.preferences.NormPreferences;

import com.iver.andami.plugins.Extension;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 * Normalization Extension. This extension puts the normalization preferences.
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 */

public class NormalizationExtension extends Extension {

	private ExtensionPoints extensionPoints = ExtensionPointsSingleton
			.getInstance();

	/**
	 * This method executes the normalization file extension
	 * 
	 * @param actionCommand
	 */
	public void execute(String actionCommand) {

	}

	/**
	 * This method initializes some parameters of the extension
	 */

	public void initialize() {
		initializeCoreExtensions();
	}

	/**
	 * This method puts available the extension
	 * 
	 * @return enable
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * This method puts visible the extension
	 * 
	 * @return visible
	 */
	public boolean isVisible() {
		return true;
	}

	/**
	 * This method initializes the Normalization Preferences
	 */
	private void initializeCoreExtensions() {
		this.extensionPoints.add("AplicationPreferences", "Normalization",
				new NormPreferences());
	}

}
