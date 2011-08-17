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
package org.gvsig.installer.app.extension;

import java.io.File;

import com.iver.andami.Launcher;



/**
 * Gets the main gvSIG application folders.
 * 
 * @author gvSIG Team
 * @version $Id$
 */
public class GvSIGFolders {

    /**
     * Returns the gvSIG main application folder.
     * 
     * @return the gvSIG main application folder
     */
    public File getApplicationFolder() {
        // TODO: check if there is a better way to handle this
        return new File(System.getProperty("user.dir"));
    }

    /**
     * Returns the gvSIG plugins folder.
     * 
     * @return the gvSIG plugins folder
     */
    public File getPluginsFolder() {
        return new File(Launcher.getAndamiConfig().getPluginsDirectory())
            .getAbsoluteFile();
    }

    /**
     * Returns the default gvSIG folder with the installable package bundles.
     * 
     * @return the default gvSIG folder with the installable package bundles
     */
    public File getInstallFolder() {
        return new File(getApplicationFolder(), "install");
    }

}
