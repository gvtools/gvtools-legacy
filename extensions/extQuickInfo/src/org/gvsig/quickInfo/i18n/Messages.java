package org.gvsig.quickInfo.i18n;

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

import java.util.Locale;

/**
 * Bridge class to provide internationalization services to the library. It uses
 * the gvsig-i18n library as a backend, and includes its necessary
 * initialization.
 * 
 * @author Cesar Martinez Izquierdo
 */
public class Messages {
	/**
	 * Whether the class has been initialized
	 */
	private static boolean isInitialized = false;

	/**
	 * The name of the Java package containing this class
	 */
	private static final String packageName = Messages.class.getPackage()
			.getName();

	/**
	 * Loads the translations in the dictionary. It initializes the backend
	 * gvsig-i18n library
	 * 
	 */
	private static void init() {
		if (!org.gvsig.i18n.Messages.hasLocales()) {
			org.gvsig.i18n.Messages.addLocale(Locale.getDefault());
		}
		org.gvsig.i18n.Messages.addResourceFamily(packageName
				+ ".resources.translations.text",
				Messages.class.getClassLoader(), packageName);
	}

	/**
	 * Gets the translation associated with the provided translation key.
	 * 
	 * @param key
	 *            The translation key which identifies the target text
	 * @return The translation associated with the provided translation key.
	 */
	public static String getText(String key) {
		if (isInitialized == false) {
			init();
			isInitialized = true;
		}
		return org.gvsig.i18n.Messages.getText(key, packageName);
	}

}
