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
package org.gvsig.symbology.gui.styling;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JFileChooser;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.symbology.fmap.symbols.PictureMarkerSymbol;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.cit.gvsig.gui.styling.AbstractTypeSymbolEditor;
import com.iver.cit.gvsig.gui.styling.EditorTool;
import com.iver.cit.gvsig.gui.styling.SymbolEditor;

/**
 * PictureMarker allows the user to store and modify the properties that define
 * a <b>picture marker symbol</b>.
 * <p>
 * <p>
 * This functionality is carried out thanks to a tab (simple marker)which is
 * included in the panel to edit the properities of a symbol (SymbolEditor)how
 * is explained in AbstractTypeSymbolEditor.
 * <p>
 * First of all, in the above mentioned tab the user will have options to change
 * the files from where the pictures for the symbol are taken (one for the
 * symbol when it is not selected in the map and the other when it is done).
 * <p>
 * <p>
 * Secondly, the user will have options to modify the pictures which had been
 * selected before (width and offset) .
 * 
 * @see AbstractTypeSymbolEditor
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class PictureMarker extends AbstractTypeSymbolEditor implements
		ActionListener {
	protected ArrayList<JPanel> tabs = new ArrayList<JPanel>();
	protected JIncrementalNumberField txtSize;
	protected JIncrementalNumberField txtX;
	protected JIncrementalNumberField txtY;
	// TODO: Comentarizado hasta que mask esté acabado
	// protected Mask mask;
	protected JLabel lblFileName;
	protected JLabel lblSelFileName;
	private File picFile;
	protected JLabel lblSize = new JLabel(PluginServices.getText(this, "width")
			+ ":");
	protected JLabel lblX = new JLabel(PluginServices.getText(this, "x_offset")
			+ ":");
	protected JLabel lblY = new JLabel(PluginServices.getText(this, "y_offset")
			+ ":");
	private JButton btn;
	private JButton btnSel;

	private ActionListener chooseAction = new ActionListener() {

		public void actionPerformed(ActionEvent e) {

			JLabel targetLbl;
			if (e.getSource().equals(btn)) {
				targetLbl = lblFileName;
			} else {
				targetLbl = lblSelFileName;
			}
			FileFilter ff = new FileFilter() {
				public boolean accept(File f) {
					if (f.isDirectory())
						return true;
					String fName = f.getAbsolutePath();
					if (fName != null) {
						fName = fName.toLowerCase();
						return fName.endsWith(".png") || fName.endsWith(".gif")
								|| fName.endsWith(".jpg")
								|| fName.endsWith(".jpeg")
								|| fName.endsWith(".bmp")
								|| fName.endsWith(".svg");
					}
					return false;
				}

				public String getDescription() {
					return PluginServices.getText(this,
							"bitmap_and_svg_image_files");
				}
			};
			JUrlFileChooser jfc = new JUrlFileChooser(getName());
			jfc.setFileFilter(ff);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setSelectedFile(picFile);
			jfc.setMultiSelectionEnabled(false);
			int returnVal = jfc.showOpenDialog(PictureMarker.this.owner);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				String url = jfc.getSelectedURLPath();
				if (url == null)
					return;
				targetLbl.setText(url);
				fireSymbolChangedEvent();
			}
			// if(returnVal == JFileChooser.APPROVE_OPTION) {
			// File myFile = jfc.getSelectedFile();
			// lastDir = jfc.getCurrentDirectory();
			// if (myFile != null && myFile.exists()) {
			// if (isSelection) {
			// selPicFile = myFile;
			// } else {
			// picFile = myFile;
			// }
			// try {
			// targetLbl.setText(myFile.toURL().toString());
			// } catch (MalformedURLException e1) {
			// NotificationManager.addError(PluginServices.getText(this,
			// "Error en la creaci?n" +
			// "de la URL"), e1);
			// }
			// fireSymbolChangedEvent();
			// }
			// }

			btnSel.setEnabled(lblFileName.getText() != "");

		}

	};

	public PictureMarker(SymbolEditor owner) {
		super(owner);
		initialize();
	}

	/**
	 * Initializes the parameters that define a picturmarker.To do it, a tab is
	 * created inside the SymbolEditor panel with default values for the
	 * different attributes of the picture marker.
	 */

	private void initialize() {
		JPanel myTab = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
		myTab.setName(PluginServices.getText(this, "picture_marker"));
		GridBagLayoutPanel aux = new GridBagLayoutPanel();

		// picture file label
		lblFileName = new JLabel();
		lblFileName.setFont(lblFileName.getFont().deriveFont(Font.BOLD));
		aux.addComponent(PluginServices.getText(this, "picture_file") + ":",
				lblFileName);

		// button browse
		btn = new JButton(PluginServices.getText(this, "browse"));
		btn.addActionListener(chooseAction);

		JPanel aux2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		aux2.add(btn);
		aux.addComponent("", aux2);

		// selection picture file
		lblSelFileName = new JLabel();
		lblSelFileName.setFont(lblSelFileName.getFont().deriveFont(Font.BOLD));
		aux.addComponent(PluginServices.getText(this, "selection_picture_file")
				+ ":", lblSelFileName);

		// button browse
		btnSel = new JButton(PluginServices.getText(this, "browse"));
		btnSel.addActionListener(chooseAction);
		btnSel.setEnabled(false);
		aux2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		aux2.add(btnSel);
		aux.addComponent("", aux2);

		// picture width
		txtSize = new JIncrementalNumberField("5", 25, 0,
				Double.POSITIVE_INFINITY, 0.5);
		aux2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		aux2.add(txtSize);
		aux.addComponent(lblSize, aux2);
		txtSize.setDouble(5);

		// picture xOffset
		txtX = new JIncrementalNumberField("0", 25);
		aux2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		aux2.add(txtX);
		aux.addComponent(lblX, aux2);

		// picture width
		txtY = new JIncrementalNumberField("0", 25);
		aux2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		aux2.add(txtY);
		aux.addComponent(lblY, aux2);

		// initialize defaults
		txtSize.addActionListener(this);
		txtX.addActionListener(this);
		txtY.addActionListener(this);
		// buttons have their own listener!!!!

		myTab.add(aux);
		tabs.add(myTab);

		// mask = new Mask(this);
		// tabs.add(mask);
	}

	public ISymbol getLayer() {
		try {
			PictureMarkerSymbol layer = null;

			if (lblFileName.getText().equals(""))
				layer = null;

			else {
				if (lblSelFileName.getText().equals("")) {
					layer = new PictureMarkerSymbol(new URL(
							lblFileName.getText()), null);
				} else {
					layer = new PictureMarkerSymbol(new URL(
							lblFileName.getText()), new URL(
							lblSelFileName.getText()));
				}
				layer.setIsShapeVisible(true);
				layer.setSize(txtSize.getDouble());
				layer.setOffset(new Point2D.Double(txtX.getDouble(), txtY
						.getDouble()));
				// layer.setMask(mask.getMask());
			}

			return layer;
		} catch (IOException e) {
			return SymbologyFactory.getWarningSymbol(
					PluginServices.getText(this, "failed_acessing_files"),
					null, SymbolDrawingException.UNSUPPORTED_SET_OF_SETTINGS);
		}

	}

	public String getName() {
		return PluginServices.getText(this, "picture_marker_symbol");

	}

	public JPanel[] getTabs() {
		return tabs.toArray(new JPanel[tabs.size()]);
	}

	public void refreshControls(ISymbol layer) {
		PictureMarkerSymbol sym;
		try {
			double size, xOffset, yOffset;
			String fileName = null, selectionFileName = null;
			if (layer == null) {
				// initialize defaults
				System.err.println(getClass().getName()
						+ ":: should be unreachable code");

				size = 1D;
				xOffset = 0D;
				yOffset = 0D;
				fileName = "-";
				selectionFileName = "-";
			} else {
				sym = (PictureMarkerSymbol) layer;

				size = sym.getSize();
				xOffset = sym.getOffset().getX();
				yOffset = sym.getOffset().getY();

				try {
					fileName = new URL(sym.getImagePath()).toString();
					selectionFileName = new URL(sym.getSelImagePath())
							.toString();
				} catch (MalformedURLException e) {
					NotificationManager.addError(
							PluginServices.getText(this, "invalid_url"), e);
				}
			}

			setValues(size, xOffset, yOffset, fileName, selectionFileName);
		} catch (IndexOutOfBoundsException ioEx) {
			NotificationManager.addWarning("Symbol layer index out of bounds",
					ioEx);
		} catch (ClassCastException ccEx) {
			NotificationManager.addWarning(
					"Illegal casting from " + layer.getClassName() + " to "
							+ getSymbolClass().getName() + ".", ccEx);

		}
	}

	protected void setValues(double size, double xOffset, double yOffset,
			String fileName, String selectionFileName) {
		txtSize.setDouble(size);
		txtX.setDouble(xOffset);
		txtY.setDouble(yOffset);
		lblFileName.setText(fileName);
		lblSelFileName.setText(selectionFileName);
		btnSel.setEnabled(lblFileName.getText() != "");
	}

	public Class getSymbolClass() {
		return PictureMarkerSymbol.class;
	}

	public EditorTool[] getEditorTools() {
		return null;

	}

	public void actionPerformed(ActionEvent e) {
		fireSymbolChangedEvent();
	}

}