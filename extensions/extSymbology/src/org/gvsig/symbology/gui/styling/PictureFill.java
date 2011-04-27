/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JBlank;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.gui.beans.swing.JFileChooser;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.symbology.fmap.symbols.PictureFillSymbol;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.gui.panels.ColorChooserPanel;
import com.iver.cit.gvsig.gui.styling.AbstractTypeSymbolEditor;
import com.iver.cit.gvsig.gui.styling.EditorTool;
import com.iver.cit.gvsig.gui.styling.SymbolEditor;
import com.iver.cit.gvsig.project.documents.view.legend.gui.JSymbolPreviewButton;

/**
 * <b>PictureFill</b> allows to store and modify the properties that fills a
 * polygon with a padding and an outline<p>
 * <p>
 * This functionality is carried out thanks to two tabs (picture fill and MarkerFillProperties)
 * which are included in the panel to edit the properities of a symbol (SymbolEditor)
 * how is explained in AbstractTypeSymbolEditor.<p>
 * <p>
 * The first tab (picture fill)permits the user to select the picture for the padding and
 * differentes options to modify it such as the angle(<b>incrAngle</b>) and the scale
 * (<b>incrScaleX,incrScaleY</b>). Also, there is an option to select a color for the
 * fill (<b>jccFillColor</b>).
 * <p>
 * The second tab is implementes as a MarkerFillProperties class and offers the possibilities
 * to change the separtion and the offset.
 *
 *
 *@see MarkerFillProperties
 *@see AbstractTypeSymbolEditor
 *@author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class PictureFill extends AbstractTypeSymbolEditor implements
ActionListener {

	private JLabel lblFileName;
	private JLabel lblSelFileName;
	private ArrayList<JPanel> tabs = new ArrayList<JPanel>();
	private MarkerFillProperties fillProperties;
	private File picFile;
	private JIncrementalNumberField incrAngle;
	private JIncrementalNumberField incrScaleX;
	private JIncrementalNumberField incrScaleY;
	private ColorChooserPanel jccFillColor;
	private ILineSymbol outline;
	private JSymbolPreviewButton btnOutline;
	private JCheckBox useBorder;

	private JButton btnBrowseFile;
    private JButton btnBrowseFileSelected;


	private ActionListener chooseAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			boolean isSelection;
			JLabel targetLbl;

			if (e.getSource().equals(btnBrowseFile)) {
				targetLbl = lblFileName;
				isSelection = false;
			} else {
				targetLbl = lblSelFileName;
				isSelection = true;
			}

			FileFilter ff = new FileFilter() {
				public boolean accept(File f) {
					if (f.isDirectory()) return true;
					String fName = f.getAbsolutePath();
					if (fName!=null) {
						fName = fName.toLowerCase();
						return fName.endsWith(".png")
						|| fName.endsWith(".gif")
						|| fName.endsWith(".jpg")
						|| fName.endsWith(".jpeg")
						|| fName.endsWith(".bmp")
						|| fName.endsWith(".svg");
					}
					return false;
				}

				public String getDescription() {
					return PluginServices.getText(this, "bitmap_and_svg_image_files");
				}
			};
			JUrlFileChooser jfc = new JUrlFileChooser(getName(), null);
			jfc.setFileFilter(ff);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setSelectedFile(picFile);
			jfc.setMultiSelectionEnabled(false);
			int returnVal = jfc.showOpenDialog(PictureFill.this.owner);
//			if(returnVal == JFileChooser.APPROVE_OPTION) {
//				File myFile = jfc.getSelectedFile();
//				lastDir = jfc.getCurrentDirectory();
//				if (myFile != null && myFile.exists()) {
//					if(!isSelection){
//						picFile = myFile;
//					}
//					else{
//						selPicFile = myFile;
//					}
//					try {
//						targetLbl.setText(myFile.toURL().toString());
//					} catch (MalformedURLException e1) {
//						NotificationManager.addError(PluginServices.getText(this, "invalid_url"), e1);
//					}
//					fireSymbolChangedEvent();
//				}
//			}
			if(returnVal == JFileChooser.APPROVE_OPTION) {

				URL url = jfc.getSelectedURL();
				if (url == null) return;
				targetLbl.setText(url.toString());
				fireSymbolChangedEvent();
			}
			boolean enabled = (lblFileName.getText()!="");
			enableControls(lblFileName.getText()!="");

		}
	};

	/**
	 * Constructor method
	 * @param owner
	 */
	public PictureFill(SymbolEditor owner) {
		super(owner);
		initialize();
	}

	/**
	 * Initializes the parameters that allows the user to fill the padding of
	 * a polygon with a picture style.To do it, two tabs are created (picture fill and
	 * MarkerFillProperties)inside the SymbolEditor panel with default values for the
     * different attributes.
	 */
	private void initialize() {
		JPanel myTab = new JPanel(new FlowLayout(FlowLayout.LEADING, 5,5));
		myTab.setName(PluginServices.getText(this, "picture_fill"));

		btnBrowseFile = new JButton(PluginServices.getText(this, "browse"));
		btnBrowseFile.addActionListener(chooseAction);

		btnBrowseFileSelected = new JButton(PluginServices.getText(this,"browse"));
		btnBrowseFileSelected.addActionListener(chooseAction);
//		btnBrowseFileSelected.setEnabled(false);

		JPanel aux2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JPanel auxLabelPic=new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JLabel lblName = new JLabel();
		lblName.setFont(lblName.getFont().deriveFont(Font.BOLD));
		lblName.setText(PluginServices.getText(this, "picture_file")+":");
		auxLabelPic.add(lblName);

		aux2.add(btnBrowseFile);
		aux2.add(lblFileName = new JLabel(""));

		JPanel auxLabelSelPic=new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JLabel lblSelName = new JLabel();
		lblSelName.setFont(lblSelName.getFont().deriveFont(Font.BOLD));
		lblSelName.setText(PluginServices.getText(this, "selection_picture_file")+":");
		auxLabelSelPic.add(lblSelName);

		JPanel aux4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		aux4.add(btnBrowseFileSelected);
		aux4.add(lblSelFileName = new JLabel(""));

		GridBagLayoutPanel aux = new GridBagLayoutPanel();
		aux.addComponent(new JBlank(5, 5));
		aux.addComponent(auxLabelPic);
		aux.addComponent(aux2);
		aux.addComponent(auxLabelSelPic);
		aux.addComponent(aux4);


		aux2 = new JPanel(new GridLayout(1, 2, 20, 5));
		GridBagLayoutPanel aux3;
		aux3 = new GridBagLayoutPanel();
		aux3.addComponent(PluginServices.getText(this, "angle")+":",
				incrAngle = new JIncrementalNumberField("0", 20));
		aux3.addComponent(PluginServices.getText(this, "scale")+"X:",
				incrScaleX = new JIncrementalNumberField(
						"1",
						20,
						0.01,
						Double.POSITIVE_INFINITY,
						0.1));
		incrScaleX.setDouble(1);
		aux3.addComponent(PluginServices.getText(this, "scale")+"Y:",
				incrScaleY = new JIncrementalNumberField(
						"1",
						20,
						0.01,
						Double.POSITIVE_INFINITY,
						0.1));
		incrScaleY.setDouble(1);
		aux2.add(aux3);

		aux3 = new GridBagLayoutPanel();
		aux3.addComponent(new JBlank(5,5));
		aux3.addComponent(new JLabel (PluginServices.getText(this, "fill_color")+":"));
		aux3.addComponent(new JBlank(5,5));
		aux3.addComponent(jccFillColor = new ColorChooserPanel(true,true));
		jccFillColor.setAlpha(255);

		aux3.addComponent(new JBlank(5,5));
		aux3.addComponent(new JBlank(5,5));
		aux2.add(aux3);

		aux.addComponent(aux2);
		aux.addComponent(new JBlank(10, 10));
		aux2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		aux2.add(btnOutline = new JSymbolPreviewButton(FShape.LINE));
		useBorder = new JCheckBox(PluginServices.getText(this, "use_outline"));
		aux.addComponent(useBorder);
		aux.addComponent(PluginServices.getText(this, "outline")+":",
				aux2);

		fillProperties = new MarkerFillProperties();
		myTab.add(aux);

		fillProperties.addActionListener(this);
		incrAngle.addActionListener(this);
		incrScaleX.addActionListener(this);
		incrScaleY.addActionListener(this);
		jccFillColor.addActionListener(this);
		btnOutline.addActionListener(this);
		useBorder.addActionListener(this);

		tabs.add(myTab);
		tabs.add(fillProperties);

		enableControls(false);

	}

	public EditorTool[] getEditorTools() {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented!");
	}

	public ISymbol getLayer() {
		File imageFile = new File(lblFileName.getText());
		File imageSelFile =  new File(lblSelFileName.getText());



		PictureFillSymbol sym=null;
		try {

			if( lblFileName.getText().equals("") )
				sym=null;

			else {
				sym =  new PictureFillSymbol(new URL(lblFileName.getText()),null);
				if (!lblSelFileName.getText().equals(""))
					sym = new PictureFillSymbol(new URL(lblFileName.getText()),new URL(lblSelFileName.getText()));

				sym.setHasFill(jccFillColor.getUseColorisSelected());
				Color c = jccFillColor.getColor();
				if (c != null)
					c = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
				sym.setFillColor(c);

				sym.setHasOutline(useBorder.isSelected());
				outline = (ILineSymbol) btnOutline.getSymbol();
				sym.setOutline(outline);

				sym.setAngle(incrAngle.getDouble()*FConstant.DEGREE_TO_RADIANS);
				sym.setXScale(incrScaleX.getDouble());
				sym.setYScale(incrScaleY.getDouble());
				sym.setMarkerFillProperties(fillProperties.getMarkerFillProperties());
			}

		} catch (IOException e) {
			return SymbologyFactory.getWarningSymbol
				(PluginServices.getText(this, "failed_acessing_files"), null, SymbolDrawingException.UNSUPPORTED_SET_OF_SETTINGS);
		}


		return sym;


	}

//	private void setControlsEnabled(boolean enabled) {
//		for (int i = 0; i < tabs.size(); i++) {
//			setOptionsEnabled(tabs.get(i), enabled);
//		}
//	}
//
//
//
//	private void setOptionsEnabled(Component c, boolean b) {
//		if (!(c.equals(btnBrowseFile) || tabs.contains(c)))
//			c.setEnabled(b);
//
//		System.out.println(c.getClass().getName()+", "+c.getName());
//		if (c instanceof Container) {
//			Container cont = (Container) c;
//			for(int j = 0; j < cont.getComponentCount(); j++) {
//				setOptionsEnabled(cont.getComponent(j), b);
//			}
//		}
//
//	}



	public String getName() {
		return PluginServices.getText(this, "picture_fill_symbol");
	}

	public Class getSymbolClass() {
		return PictureFillSymbol.class;
	}

	public JPanel[] getTabs() {
		return (JPanel[]) tabs.toArray(new JPanel[tabs.size()]);
	}

	public void refreshControls(ISymbol layer) {
		PictureFillSymbol sym = (PictureFillSymbol) layer;

		File imageFile = new File(sym.getImagePath());
		File selImageFile =  new File (sym.getSelImagePath());

//		boolean enabled = imageFile.exists();
//		setControlsEnabled(enabled);

		lblFileName.setText(sym.getImagePath());
		lblSelFileName.setText(sym.getSelImagePath());
		jccFillColor.setUseColorIsSelected(sym.hasFill());
		jccFillColor.setColor(sym.getFillColor());

		outline=sym.getOutline();
		btnOutline.setSymbol(outline);
		useBorder.setSelected(sym.hasOutline());

		incrAngle.setDouble(sym.getAngle()/FConstant.DEGREE_TO_RADIANS);
		incrScaleX.setDouble(sym.getXScale());
		incrScaleY.setDouble(sym.getYScale());
		fillProperties.setModel(sym.getMarkerFillProperties());

		enableControls(lblFileName.getText()!="");

	}

	private void enableControls(boolean enabled){
		btnBrowseFileSelected.setEnabled(enabled);
		incrAngle.setEnabled(enabled);
		incrScaleX.setEnabled(enabled);
		incrScaleY.setEnabled(enabled);
		incrAngle.setEnabled(enabled);
		incrScaleX.setEnabled(enabled);
		incrScaleY.setEnabled(enabled);
		jccFillColor.setEnabled(enabled);
		btnOutline.setEnabled(enabled);
		useBorder.setEnabled(enabled);

		fillProperties.setEnabled(enabled);
	}

	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();

		if(s.equals(jccFillColor)) {
			jccFillColor.getColor().getAlpha();
		}
		outline = (ILineSymbol) btnOutline.getSymbol();
		fireSymbolChangedEvent();
	}

}