/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
package org.gvsig.symbology.gui.styling.editortools;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.filechooser.FileFilter;

import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.gui.beans.swing.JFileChooser;
import org.gvsig.symbology.fmap.styles.BackgroundFileStyle;
import org.gvsig.symbology.fmap.styles.ImageStyle;
import org.gvsig.symbology.fmap.styles.SVGStyle;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.ILabelStyle;
import com.iver.cit.gvsig.gui.styling.EditorTool;
import com.iver.cit.gvsig.gui.styling.StyleEditor;
import com.iver.utiles.XMLEntity;
/**
 *
 * LabelStyleOpenBackgroundFile.java
 *
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jan 15, 2008
 *
 */
public class LabelStyleOpenBackgroundFile extends EditorTool {

	public static final String LABEL_STYLE_BACKGROUND_FILECHOOSER = "LABEL_STYLE_BACKGROUND_FILECHOOSER";
	public static String DEFAULT_LABEL_BACKGROUND_FOLDER;

	private JButton btnOpenFile;
	private ILabelStyle style;

	public LabelStyleOpenBackgroundFile(JComponent targetEditor) {
		super(targetEditor);
	}


	@Override
	public AbstractButton getButton() {
		return getBtnOpenFile();
	}

	private JButton getBtnOpenFile() {
		if (btnOpenFile== null) {
			btnOpenFile = new JButton(PluginServices.getIconTheme().get("project-open"));
			btnOpenFile.setSize(EditorTool.SMALL_BTN_SIZE);
			btnOpenFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser jfChooser = new JFileChooser(
							LABEL_STYLE_BACKGROUND_FILECHOOSER,
							DEFAULT_LABEL_BACKGROUND_FOLDER);
					FileFilter ff = new FileFilter() {

						public boolean accept(File pathname) {
							if (pathname.isDirectory()) return true;
							String fileName = pathname.getAbsolutePath().toLowerCase();
							return fileName.endsWith("jpg") ||
								    fileName.endsWith("jpeg") ||
									fileName.endsWith("png") ||									
									fileName.endsWith("gif") ||
									fileName.endsWith("svg");
						}

						public String getDescription() {
							return PluginServices.getText(
									this, "all_supported_background_image_formats");
						}
					};

					jfChooser.setFileFilter(ff);
					if (jfChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
						File f = jfChooser.getSelectedFile();
						// this is a hack


						BackgroundFileStyle bgStyle = f.getAbsolutePath().toLowerCase().endsWith("svg") ?
							new SVGStyle() : new ImageStyle();

						try {
							bgStyle.setSource(f.toURL());
							style.setBackground(bgStyle.getXMLEntity());
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

					((StyleEditor) owner).restorePreviousTool();
					owner.repaint();
				}
			});
		}
		return btnOpenFile;
	}

	@Override
	public Cursor getCursor() {
		return Cursor.getDefaultCursor();
	}

	@Override
	public String getID() {
		return "4";
	}

	@Override
	public boolean isSuitableFor(Object obj) {
		return obj instanceof ILabelStyle;
	}

	@Override
	public void setModel(Object objectToBeEdited) {
		style = (ILabelStyle) objectToBeEdited;
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseDragged(MouseEvent e) { }

}
