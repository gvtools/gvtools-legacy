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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.gvsig.tools.backup.DefaultBackupGeneratorFactory;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.core.preferences.general.GeneralPage;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
import com.iver.utiles.save.BeforeSavingAdapter;
import com.iver.utiles.save.SaveEvent;

/**
 * <p>
 * Extension to add support for saving a back up of the <a href="">gvSIG</a>
 * project to be replaced.
 * </p>
 * <p>
 * Adds also a check box in the general preferences to enable or disable that
 * option.
 * </p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class BackUpProjectExtension extends Extension {

	private JCheckBox backUpProjectCBox;
	private BackupConfig backupConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		try {
			/*
			 * 1- Replaces in the General panel in the gvSIG preferences, a
			 * checkbox as an option to save a back up of the previous gvSIG
			 * project to be overwritten as a backup
			 */
			ExtensionPoints extensionPoints = ExtensionPointsSingleton
					.getInstance();
			ExtensionPoint extensionPoint = (ExtensionPoint) extensionPoints
					.get("AplicationPreferences");

			// Adds the new checkbox
			((GeneralPage) extensionPoint.get("GeneralPage"))
					.addComponent(getBackupProjectCheckBox());

			/*
			 * 2- Adds the listener to the ProjectExtension (extension that
			 * saves the gvSIG project
			 */
			ProjectExtension prjExtension = (ProjectExtension) PluginServices
					.getExtension(ProjectExtension.class);

			prjExtension.addListener(new BeforeSavingAdapter() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * com.iver.utiles.save.BeforeSavingAdapter#beforeSaving(com
				 * .iver.utiles.save.SaveEvent)
				 */
				public void beforeSaving(SaveEvent e) {
					/*
					 * 3- Performs a backup of the file as another file at the
					 * same directory, with ".bak" as the file extension
					 */
					if ((backUpProjectCBox != null)
							&& (backUpProjectCBox.isSelected())) {
						// 3.1- If exists the original file
						try {
							File source = e.getFile();

							// 3.2- Validates if can be written
							// 3.2.1- If there is a project in that path with
							// that name -> perform the backup, otherwise no
							if (source.exists()) {
								// 3.2.2- If can't be read -> notifies it, and
								// finish
								if (!source.canRead()) {
									JOptionPane
											.showMessageDialog(
													null,
													PluginServices
															.getText(null,
																	"The_project_hasnt_read_permissions"),
													PluginServices.getText(
															null, "Warning"),
													JOptionPane.WARNING_MESSAGE);
									return;
								}

								// 3.2.3- If can't be written -> notifies it,
								// and finish
								if (!source.canWrite()) {
									JOptionPane
											.showMessageDialog(
													null,
													PluginServices
															.getText(null,
																	"The_project_has_only_read_permissions"),
													PluginServices.getText(
															null, "Warning"),
													JOptionPane.WARNING_MESSAGE);
									return;
								}

								DefaultBackupGeneratorFactory factory = new DefaultBackupGeneratorFactory();

								// 3.3- Performs the backup
								factory.getBackupGenerator().backup(source);
							}
						} catch (Exception ex) {
							NotificationManager.showMessageError(
									PluginServices
											.getText(null,
													"Failed_doing_backup_of_project_to_be_overwritten"),
									ex);
						}
					}
				}
			});

			// Creates a new backupConfig
			backupConfig = new BackupConfig();

			// Sets the preference value
			backUpProjectCBox.setSelected(backupConfig
					.isBackupProjectToBeOverwritten());
		} catch (Exception e) {
			NotificationManager
					.showMessageError(
							PluginServices
									.getText(
											this,
											"Failed_initializing_the_backup_of_previous_project_to_be_overwritten_extension"),
							e);
		}
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
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		return true;
	}

	/**
	 * <p>
	 * Gets the check box in the General preferences that determines if it has
	 * to save a back up of the previous project to be replaced.
	 * </p>
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getBackupProjectCheckBox() {
		if (backUpProjectCBox == null) {
			backUpProjectCBox = new JCheckBox(PluginServices.getText(this,
					"options.general.backup_project_to_be_overwriten"));
			backUpProjectCBox.addActionListener(new ActionListener() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * java.awt.event.ActionListener#actionPerformed(java.awt.event
				 * .ActionEvent)
				 */
				public void actionPerformed(ActionEvent e) {
					backupConfig.setBackupProjectToBeOverwritten(((JCheckBox) e
							.getSource()).isSelected());
				}
			});
		}

		return backUpProjectCBox;
	}
}
