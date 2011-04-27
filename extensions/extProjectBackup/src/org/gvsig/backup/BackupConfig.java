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
import com.iver.utiles.IPersistence;
import com.iver.utiles.XMLEntity;

/**
 * Stores the backup configuration in memory, and offers a simple
 * interface to change them.
 *
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class BackupConfig implements IPersistence {
	public static final String BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID = "backup_project_to_be_overwritten";

	protected boolean backupProjectToBeOverwritten = false; // By default -> false
	protected XMLEntity xmlConfig = null;

	/**
	 * Creates a new BackupConfig instance
	 */
	public BackupConfig() {
		super();

		initConfig();
	}
	
	/*
	 * @see com.iver.utiles.IPersistance#getClassName()
	 */	
	public String getClassName() {
		return this.getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.utiles.IPersistance#getXMLEntity()
	 */
	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty(BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID, backupProjectToBeOverwritten);
		
		return xml;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.utiles.IPersistance#setXMLEntity(com.iver.utiles.XMLEntity)
	 */
	public void setXMLEntity(XMLEntity xml) {
		if (xml.contains(BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID)) {
			backupProjectToBeOverwritten = xml.getBooleanProperty(BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID);
		}
		else {
			backupProjectToBeOverwritten = false; // By default -> false
		}
	}

	/**
	 * Sets the XML property {@link BackupConfig#BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID BackupConfig#BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID}
	 *
	 * @param b the value to persist
	 */
	public void setBackupProjectToBeOverwritten(boolean b) {
		backupProjectToBeOverwritten = b;
		xmlConfig.putProperty(BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID, backupProjectToBeOverwritten);
	}
	
	/**
	 * Gets the XML property {@link BackupConfig#BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID BackupConfig#BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID}
	 *
	 * @return the value to persist
	 */
	public boolean isBackupProjectToBeOverwritten() {
		return backupProjectToBeOverwritten;
	}

	/**
	 * Initializes the configuration of the associated extension.
	 */
	private void initConfig() {
		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();
		XMLEntity child;
		boolean found = false;

		for (int i=0; i<xml.getChildrenCount(); i++) {
			child = xml.getChild(i); 
			if (child.contains(BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID)) {
				setXMLEntity(child);
				found = true;
				xmlConfig = child;
				backupProjectToBeOverwritten = child.getBooleanProperty(BACKUP_PROJECT_TO_BE_OVERWRITTEN_ID);
			}
		}
		if (!found)  {
			child = getXMLEntity();
			xml.addChild(child);
			xmlConfig = child;
			backupProjectToBeOverwritten = false;
		}
	}
}
