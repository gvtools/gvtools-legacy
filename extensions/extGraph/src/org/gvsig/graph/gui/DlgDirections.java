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
package org.gvsig.graph.gui;

import java.awt.Frame;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.hardcode.gdbms.engine.values.DoubleValue;
import com.iver.cit.gvsig.fmap.core.IFeature;

public class DlgDirections extends JDialog {

	private JScrollPane jScrollPane = null;

	private JTextPane jTextPane = null;

	/**
	 * This method initializes
	 *
	 * @param b
	 * @param frame
	 *
	 */
	public DlgDirections(Frame frame, boolean b) {
		super(frame, b);
		initialize();
	}

	/**
	 * Es responsabilidad del interfaz de usuario presentar el modelo a su
	 * manera.
	 *
	 * @param features
	 */
	public void setModel(ArrayList featureList) {
		String cad = "Pasa por:";
		int cont = 0;
		double longitud = 0, sumaLong = 0, longAnt = 0;
		String cadAnt = "";
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		for (int i = featureList.size() - 1; i >= 0; i--) {
			// INSTRUCCIONES
			IFeature feat = (IFeature) featureList.get(i);
			DoubleValue length = (DoubleValue) feat.getAttribute(2);
			double dist = length.doubleValue();
			longitud = longitud + dist;
			sumaLong = sumaLong + dist;

			String cadProv = feat.getAttribute(3).toString();
			if ((cadAnt.compareTo(cadProv) != 0) && (cont > 0)) {
				longitud = dist;
				cad = cad + "\n" + cadAnt + " " + nf.format(longAnt)
						+ " metros";

			}
			longAnt = longitud;
			cadAnt = cadProv;

			cont = cont + 1;
		}
		cad = cad + "\n" + cadAnt + " " + nf.format(longAnt) + " metros";
		cad = cad + "\n" + "=================" + "\n"
				+ "Distancia total recorrida: " + nf.format(sumaLong)
				+ " metros";
		getJTextPane().setText(cad);
	}

	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
		this.setSize(new java.awt.Dimension(363, 254));
		this.setContentPane(getJScrollPane());

	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTextPane());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextPane
	 *
	 * @return javax.swing.JTextPane
	 */
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
		}
		return jTextPane;
	}

} // @jve:decl-index=0:visual-constraint="10,10"

