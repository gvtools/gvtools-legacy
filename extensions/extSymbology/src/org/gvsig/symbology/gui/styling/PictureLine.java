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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.gvsig.symbology.fmap.symbols.PictureLineSymbol;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.cit.gvsig.gui.styling.SymbolEditor;

/**
 * PictureLine initializes the properties that define a
 * <b>picture marker symbol</b> and are showed in the tab created by
 * PictureMarker which is called simple marker.<p>
 * <p>
 * Moreover, PictureLine has other methods such as getSymbolClass,getName,
 * refreshControls and getLayer.
 *
 *
 *@author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class PictureLine extends PictureMarker {
	public PictureLine(SymbolEditor owner) {
		super(owner);
		initialize();
	}
	/**
	 * Initializes the values in the tab
	 *
	 */
	private void initialize() {
//		tabs.remove(mask);
		lblSize.setText(PluginServices.getText(this, "width")+":");
		lblX.setText(PluginServices.getText(this, "scale")+" X:");
		lblY.setText(PluginServices.getText(this, "scale")+" Y:");
		txtX.setMinValue(0.1);
		txtX.setMaxValue(Double.POSITIVE_INFINITY);
		txtX.setStep(0.1);
		txtX.setDouble(1);

		txtY.setMinValue(0.1);
		txtY.setMaxValue(Double.POSITIVE_INFINITY);
		txtY.setStep(0.1);
		txtY.setDouble(1);
	}

	public Class getSymbolClass() {
		return PictureLineSymbol.class;
	}

	public String getName() {
		return PluginServices.getText(this, "picture_line_symbol");

	}

	public void refreshControls(ISymbol layer) {
		PictureLineSymbol sym;
		try {
			double size, xScale, yScale;
			String fileName = null, selectionFileName = null;

			if (layer == null) {
				// initialize defaults
				System.err.println(getClass().getName()+":: should be unreachable code");

				size = 1D;
				xScale = 1D;
				yScale = 1D;
				fileName = "-";
				selectionFileName = "-";
			} else {
				sym = (PictureLineSymbol) layer;

				size = sym.getLineWidth();
				xScale = sym.getXScale();
				yScale = sym.getYScale();

				try {
					fileName = new URL(sym.getImagePath()).toString();
					selectionFileName =new URL(sym.getSelImagePath()).toString();
				} catch (MalformedURLException e) {
					NotificationManager.addError(PluginServices.getText(this, "Error en la creaci?n" +
					"de la URL"), e);
				}

			}

			setValues(size, xScale, yScale, fileName, selectionFileName);
		} catch (IndexOutOfBoundsException ioEx) {
			NotificationManager.addWarning("Symbol layer index out of bounds", ioEx);
		} catch (ClassCastException ccEx) {
			NotificationManager.addWarning("Illegal casting from " +
					layer.getClassName() + " to " + getSymbolClass().
					getName() + ".", ccEx);

		}
	}

	public ISymbol getLayer() {

		try {
			PictureLineSymbol layer = null;


			if(lblFileName.getText().equals("") )
				layer=null;

			else {
				layer =  new PictureLineSymbol(new URL(lblFileName.getText()),null);
				if (!lblSelFileName.getText().equals(""))
					layer = new PictureLineSymbol(new URL(lblFileName.getText()),new URL(lblSelFileName.getText()));

				layer.setIsShapeVisible(true);
				layer.setLineWidth(txtSize.getDouble());
				layer.setXScale(txtX.getDouble());
				layer.setYScale(txtY.getDouble());
			}

			return layer;
		} catch (IOException e) {
			return SymbologyFactory.getWarningSymbol
				(PluginServices.getText(this, "failed_acessing_files"), null, SymbolDrawingException.UNSUPPORTED_SET_OF_SETTINGS);
		}

	}
}
