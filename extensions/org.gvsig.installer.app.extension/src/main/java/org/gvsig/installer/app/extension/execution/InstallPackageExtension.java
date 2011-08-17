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
 * 2010 {Prodevelop}   {Task}
 */

package org.gvsig.installer.app.extension.execution;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import org.gvsig.i18n.Messages;
import org.gvsig.installer.app.extension.GvSIGFolders;
import org.gvsig.installer.swing.api.SwingInstallerLocator;
import org.gvsig.tools.library.impl.DefaultLibrariesInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.Version;

/**
 * @author <a href="mailto:jpiera@gvsig.org">Jorge Piera Llodr&aacute;</a>
 */
public class InstallPackageExtension extends Extension {

    private static final Logger LOG = LoggerFactory
        .getLogger(InstallPackageExtension.class);

    public void execute(String actionCommand) {
    	int resp = JOptionPane.showConfirmDialog(
    			null,
    			Messages.getText("install_package_extension_warning"),
    			Messages.getText("select_an_option"),
    			JOptionPane.YES_NO_OPTION);
    	if (resp!=JOptionPane.OK_OPTION){
    		return;
    	}

        GvSIGFolders folders = new GvSIGFolders();
        try {
            PluginServices.getMDIManager().addCentredWindow(
                new InstallPackageWindow(folders.getApplicationFolder(),
                    folders.getPluginsFolder(), folders.getInstallFolder()));
        } catch (Exception e) {
            LOG.error("Error creating the wizard to install a package ", e);
        }
    }

    public void initialize() {

        // TODO: move to user preferences or an external configuration file
        String packageDownloadURL =
            "http://gvsig-desktop.forge.osor.eu/gvSIG-desktop/dists/"+(new Version()).getFormat()+"/packages.gvspki";

        try {
        	
//        	InitializeLibraries.initialize();
    		new DefaultLibrariesInitializer(this.getClass().getClassLoader()).fullInitialize();

        	
        	SwingInstallerLocator.getSwingInstallerManager()
                .setDefaultDownloadURL(new URL(packageDownloadURL));
        } catch (MalformedURLException e) {
            LOG.error(
                "Error creating the default packages download URL pointing to"
                    + packageDownloadURL, e);
        }
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isVisible() {
        return true;
    }

}
