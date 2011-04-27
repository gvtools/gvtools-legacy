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

/* CVS MESSAGES:
*
* $Id: CharacterMarker.java 13828 2007-09-19 11:06:36Z jvidal $
* $Log$
* Revision 1.20  2007-09-19 11:06:36  jvidal
* bug solved
*
* Revision 1.19  2007/09/17 09:21:45  jaume
* refactored SymboSelector (added support for multishapedsymbol)
*
* Revision 1.18  2007/08/08 11:45:38  jaume
* some bugs fixed
*
* Revision 1.17  2007/08/07 11:21:05  jvidal
* javadoc
*
* Revision 1.16  2007/08/03 11:29:13  jaume
* refactored AbstractTypeSymbolEditorPanel class name to AbastractTypeSymbolEditor
*
* Revision 1.15  2007/07/30 12:56:04  jaume
* organize imports, java 5 code downgraded to 1.4 and added PictureFillSymbol
*
* Revision 1.14  2007/07/12 10:43:55  jaume
* *** empty log message ***
*
* Revision 1.13  2007/06/29 13:07:33  jaume
* +PictureLineSymbol
*
* Revision 1.12  2007/05/31 09:36:22  jaume
* *** empty log message ***
*
* Revision 1.11  2007/05/29 15:47:06  jaume
* *** empty log message ***
*
* Revision 1.10  2007/05/21 10:38:27  jaume
* *** empty log message ***
*
* Revision 1.9  2007/05/09 16:08:14  jaume
* *** empty log message ***
*
* Revision 1.8  2007/04/26 11:40:09  jaume
* added new components (JIncrementalNumberField)
*
* Revision 1.7  2007/04/20 07:54:38  jaume
* *** empty log message ***
*
* Revision 1.6  2007/04/19 14:22:20  jaume
* *** empty log message ***
*
* Revision 1.5  2007/04/05 16:08:34  jaume
* Styled labeling stuff
*
* Revision 1.4  2007/04/04 16:01:13  jaume
* *** empty log message ***
*
* Revision 1.3  2007/03/30 12:54:11  jaume
* *** empty log message ***
*
* Revision 1.2  2007/03/09 11:25:00  jaume
* Advanced symbology (start committing)
*
* Revision 1.1.2.7  2007/02/21 07:35:14  jaume
* *** empty log message ***
*
* Revision 1.1.2.6  2007/02/08 15:43:05  jaume
* some bug fixes in the editor and removed unnecessary imports
*
* Revision 1.1.2.5  2007/02/05 14:58:28  jaume
* *** empty log message ***
*
* Revision 1.1.2.3  2007/02/04 16:57:22  jaume
* working the map of characters
*
* Revision 1.1.2.2  2007/02/02 16:21:32  jaume
* *** empty log message ***
*
* Revision 1.1.2.1  2007/01/26 13:49:03  jaume
* *** empty log message ***
*
* Revision 1.1  2007/01/16 11:52:11  jaume
* *** empty log message ***
*
* Revision 1.3  2006/11/06 17:08:45  jaume
* *** empty log message ***
*
* Revision 1.2  2006/11/06 16:06:52  jaume
* *** empty log message ***
*
* Revision 1.1  2006/10/31 16:16:34  jaume
* *** empty log message ***
*
*
*/
package org.gvsig.symbology.gui.styling;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JComboBoxFontSizes;
import org.gvsig.gui.beans.swing.JComboBoxFonts;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.symbology.fmap.symbols.CharacterMarkerSymbol;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.gui.panels.ColorChooserPanel;
import com.iver.cit.gvsig.gui.styling.AbstractTypeSymbolEditor;
import com.iver.cit.gvsig.gui.styling.EditorTool;
import com.iver.cit.gvsig.gui.styling.Mask;
import com.iver.cit.gvsig.gui.styling.SymbolEditor;
import com.iver.cit.gvsig.gui.styling.SymbolPreviewer;



/**
* CharacterMarker allows the user to store and modify the properties that
* define a <b>character marker</b>.<p>
* <p>
* This functionality is carried out thanks to two tabs (character marker and mask)which
* are included in the panel to edit the properities of a symbol (SymbolEditor)how
* is explained in AbstractTypeSymbolEditor.<p>
* <p>
* The first tab (Character marker)allows the user to change the font (<b>cmbFonts</b>)of
* the symbol for the character marker, the size of that (<b>cmbFontSize</b>),the angle
*(<b>numberAngle</b>)if the user wants to show the character with some rotation,the color
* (<b>jcc</b>) and the offset (<b>txtYOffset,txtXOffset</b>).<p>
* <p>
* The second tab (<b>Mask</b>) modifies attributes of a mask for points such
*  as style,size and symbol (to represent a point in the map).<p>
*
*@see Mask
*@see AbstractTypeSymbolEditor
*@author jaume dominguez faus - jaume.dominguez@iver.es
*/
public class CharacterMarker extends AbstractTypeSymbolEditor implements ActionListener {
	private ArrayList<JPanel> tabs = new ArrayList<JPanel>();
	private JList jListSymbols;
	private JScrollPane jScrollPane;
	private JComboBoxFonts cmbFonts;
	//TODO: Comentarizado hasta que mask esté acabado
//	private Mask mask;
	private JComboBoxFontSizes cmbFontSize;
	private JIncrementalNumberField numberAngle;
	private ColorChooserPanel jcc;
	private JIncrementalNumberField txtYOffset;
	private JIncrementalNumberField txtXOffset;
	private Font font;
	private int unicode;
	private JCheckBox chkAdjustGlyph;

	/**
	 * contructor method
	 * @param owner
	 */
	public CharacterMarker(SymbolEditor owner) {
		super(owner);
		initialize();
	}
	/**
	 * Initializes the parameters that define a charactermarker.To do it, two tabs
	 * are created inside the SymbolEditor panel with default values for the
	 * different attributes of the character marker.This two tabs will be character
	 * marker tab (options of font,size,angle,color and the offset of the character
	 * marker)and a Mask added as a new tab.
	 *
	 */
	private void initialize() {
		JPanel myTab;
		{
			// Character marker tab
			myTab = new JPanel(new BorderLayout(10, 10));
			myTab.setName(PluginServices.getText(this, "character_marker"));
			myTab.setLayout(new BorderLayout(15,15));

			JPanel aux = new JPanel(new BorderLayout(15, 15));
			aux.add(new JLabel(PluginServices.getText(this, "font")+":"), BorderLayout.NORTH);
			aux.add(getCmbFonts(), BorderLayout.NORTH);

			getJListSymbols().setModel(
					new CharacterListModel(
							new Font((String) getCmbFonts().getSelectedItem(), Font.PLAIN, 14)));
			aux.add(getJScrollPane(), BorderLayout.CENTER);
			myTab.add(aux, BorderLayout.CENTER);

			GridBagLayoutPanel aux1 = new GridBagLayoutPanel();
			aux1.addComponent(PluginServices.getText(this, "size")+":", getCmbFontSize());
			aux1.addComponent(PluginServices.getText(this, "angle")+":", getNumberAngle());

			aux1.addComponent(PluginServices.getText(this, "color")+":", getColorChooser());

			GridBagLayoutPanel aux2 = new GridBagLayoutPanel();
			aux2.addComponent("X:", getTxtXOffset());
			aux2.addComponent("Y:", getTxtYOffset());
			aux2.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "offset")));
			aux1.addComponent(aux2);

			myTab.add(aux1, BorderLayout.EAST);
			myTab.add(getChkAdjustGlyph(), BorderLayout.SOUTH);

			tabs.add(myTab);
		}

		{
			// Mask tab
//			mask = new Mask(this);
//
//			tabs.add(mask);
		}

		jListSymbols.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				fireSymbolChangedEvent();
			}
		});
	}

	/**
	 * JCheckBox that allows the user to apply visual correction to glyph for
	 * precise size and position.
	 * @return JCheckBox
	 */
	private JCheckBox getChkAdjustGlyph() {
		if (chkAdjustGlyph == null) {
			chkAdjustGlyph = new JCheckBox(PluginServices.getText(this, "apply_visual_correction_to_glyph_for_precise_size_and_position"));
			chkAdjustGlyph.addActionListener(this);
		}

		return chkAdjustGlyph;
	}

	/**
	 * JIncrementalNumberField that controls the YOffset for the character that the
	 * user wants to use as a marker.
	 * @return JIncrementalNumberField
	 */
	private JIncrementalNumberField getTxtYOffset() {
		if (txtYOffset == null) {
			txtYOffset = new JIncrementalNumberField(String.valueOf(0), 7);
			txtYOffset.addActionListener(this);
		}

		return txtYOffset;
	}
	/**
	 * JIncrementalNumberField that controls the XOffset for the character that the
	 * user wants to use as a marker.
	 * @return JIncrementalNumberField
	 */
	private JIncrementalNumberField getTxtXOffset() {
		if (txtXOffset == null) {
			txtXOffset = new JIncrementalNumberField(String.valueOf(0), 7);
			txtXOffset.addActionListener(this);
		}

		return txtXOffset;
	}
	/**
	 * ColorChooserPanel used to select the color for the character that the
	 * user wants to use as a marker.
	 * @return ColorChooserPanel
	 */
	private ColorChooserPanel getColorChooser() {
		if (jcc == null) {
			jcc = new ColorChooserPanel();
			jcc.setAlpha(255);
			jcc.addActionListener(this);
		}

		return jcc;
	}
	/**
	 * JComboBoxFontSizes that allows the user to change the size of the character
	 * selected as a marker
	 * @return JComboBoxFontSizes
	 */
	private JComboBoxFontSizes getCmbFontSize() {
		if (cmbFontSize == null) {
			cmbFontSize = new JComboBoxFontSizes();
			cmbFontSize.addActionListener(this);
		}

		return cmbFontSize;
	}
	/**
	 * JIncrementalNumberField that controls the angle (if the user wants to show the
	 * character with some rotation) for the character that the user wants to use as
	 * a marker.
	 * @return JIncrementalNumberField
	 */

	private JIncrementalNumberField getNumberAngle() {
		if (numberAngle == null) {
			numberAngle = new JIncrementalNumberField(String.valueOf(0), 5);
			numberAngle.addActionListener(this);

		}

		return numberAngle;
	}

	/**
	 * JComboBoxFonts its a combobox of fonts where the user can select the font
	 * for the character marker.Depends on this font, the character will change its
	 * appearance.
	 * @returnJComboBoxFonts
	 */
	private JComboBoxFonts getCmbFonts() {
		if (cmbFonts == null) {
			cmbFonts = new JComboBoxFonts();
			cmbFonts.addActionListener(this);
		}
		return cmbFonts;
	}
	/**
	 * JScrollPane to select the exact character marker.The content of this pane
	 * will change depending on the font that the user had selected.
	 * @return JScrollPane
	 */

	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJListSymbols());
			jScrollPane.setPreferredSize(new Dimension(300, 180));
		}
		return jScrollPane;
	}
	/**
	 * Used with the JScrollPane to create the pane that allows the user to select the
	 * character marker.
	 * @see getJScrollPane()
	 * @return JList
	 */
	private JList getJListSymbols() {
		if (jListSymbols == null) {
			jListSymbols = new JList();
			jListSymbols.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			jListSymbols.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			jListSymbols.setVisibleRowCount(-1);
			jListSymbols.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					CharacterItem theChar = (CharacterItem) jListSymbols.getSelectedValue();
					if (theChar == null) return;
					font = theChar.font;
					unicode = theChar.glyph;
					fireSymbolChangedEvent();
				}
			});
			ListCellRenderer renderer = new ListCellRenderer() {
				private Color mySelectedBGColor = new Color(255,145,100,255);
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					CharacterItem theChar = (CharacterItem) value;
					CharacterMarkerSymbol sym = new CharacterMarkerSymbol(theChar.font, theChar.glyph, Color.BLACK);
					JPanel pnl = new JPanel();
					BoxLayout layout = new BoxLayout(pnl, BoxLayout.Y_AXIS);
					pnl.setLayout(layout);
					Color bgColor = (isSelected) ? mySelectedBGColor
							: getJListSymbols().getBackground();

					pnl.setBackground(bgColor);
					SymbolPreviewer sp = new SymbolPreviewer();
					sp.setAlignmentX(Component.CENTER_ALIGNMENT);
					int prevSize = 30;
					sp.setPreferredSize(new Dimension(prevSize, prevSize));
					sp.setSymbol(sym);
					sp.setBackground(bgColor);
					sym.setSize(prevSize*.8);
//					sym.setVisuallyCorrected(getChkAdjustGlyph().isSelected());
					pnl.add(sp);
					JLabel lbl = new JLabel(sym.getDescription());
					lbl.setBackground(bgColor);
					lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
					pnl.add(lbl);

					return pnl;
				}
			};
			jListSymbols.setCellRenderer(renderer);
		}
		return jListSymbols;
	}


	public String getName() {
		return PluginServices.getText(this, "character_marker_symbol");
	}

	public JPanel[] getTabs() {
		return (JPanel[]) tabs .toArray(new JPanel[0]);
	}

	public void refreshControls(ISymbol layer) {
		CharacterMarkerSymbol sym;
		try {
			sym = (CharacterMarkerSymbol) layer;
			font = sym.getFont();
			unicode = sym.getUnicode();
			getTxtXOffset().setDouble( sym.getOffset().getX() );
			getTxtYOffset().setDouble( -sym.getOffset().getY() );

			getColorChooser().setColor(sym.getColor());

			getCmbFonts().setSelectedItem(sym.getFont().getName());
			getCmbFontSize().setSelectedItem(new Integer(sym.getFont().getSize()));
			getJListSymbols().setSelectedIndex(sym.getUnicode());
			getNumberAngle().setDouble( sym.getRotation()/FConstant.DEGREE_TO_RADIANS );




			getChkAdjustGlyph().setSelected(sym.isVisuallyCorrected());
			// TODO mask
		} catch (IndexOutOfBoundsException ioEx) {
			NotificationManager.addWarning("Symbol layer index out of bounds", ioEx);
		} catch (ClassCastException ccEx) {
			NotificationManager.addWarning("Illegal casting from " +
					layer.getClassName() + " to " + getSymbolClass().
					getName() + ".", ccEx);

		}

	}

	public Class<? extends ISymbol> getSymbolClass() {
		return CharacterMarkerSymbol.class;
	}

	public ISymbol getLayer() {
		CharacterMarkerSymbol layer = new CharacterMarkerSymbol();
		if (font == null) {
			font = new Font((String) getCmbFonts().getSelectedItem(),
					                 Font.PLAIN,
					           (int) getCmbFontSize().getSelectedValue());
		}
		layer.setFont(font);
		layer.setUnicode(unicode);
		layer.setColor(getColorChooser().getColor());
		layer.setSize(getCmbFontSize().getSelectedValue());
		layer.setOffset(new Point2D.Double(
				getTxtXOffset().getDouble(),
				-getTxtYOffset().getDouble()));
		layer.setRotation(getNumberAngle().getDouble()*FConstant.DEGREE_TO_RADIANS);

		layer.setUnit(owner.getUnit());
		layer.setReferenceSystem(owner.getUnitsReferenceSystem());
//		layer.setMask(mask.getMask());
		layer.setVisuallyCorrected(getChkAdjustGlyph().isSelected());
		return layer;
	}

	/**
	 * Creates a list with the specified items.
	 *
	 */
	private class CharacterListModel implements ListModel {

		private Font font;
		private ArrayList<ListDataListener> listeners;

		/**
		 * constructor method
		 * @param font
		 */
		public CharacterListModel(Font font) {
			this.font = font;
		}

		public int getSize() {
			return font.getNumGlyphs();
		}

		public Object getElementAt(int index) {
			return new CharacterItem(font, index);
		}

		public void addListDataListener(ListDataListener l) {
			if (listeners == null)
				listeners = new ArrayList<ListDataListener>();
			listeners.add(l);
		}

		public void removeListDataListener(ListDataListener l) {
			if (listeners!=null)
				listeners.remove(l);
		}
	}

	private class CharacterItem {
		int glyph;
		Font font;
		public CharacterItem(Font font, int glyph) {
			this.font = font;
			this.glyph = glyph;
		}
	}

	public void actionPerformed(ActionEvent e) {
		JComponent c = (JComponent) e.getSource();
		if (c.equals(getCmbFonts())) {
			String fontName = (String) getCmbFonts().getSelectedItem();
			getJListSymbols().setModel(
					new CharacterListModel(
							new Font(fontName, Font.PLAIN, 10)));
			return;
		}

		fireSymbolChangedEvent();
	}

	public EditorTool[] getEditorTools() {
		return null;
	}
}
