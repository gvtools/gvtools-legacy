/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.arcims.gui.panels.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import es.prodevelop.cit.gvsig.arcims.gui.panels.ImageServicePanel;

public class ImageFormatSelector extends JPanel implements ChangeListener {
	private JRadioButton jpgRB;
	private JRadioButton gifRB;
	private JRadioButton png8RB;
	private JRadioButton png24RB;
	private ButtonGroup formatBG;
	private ActionListener listener;

	public ImageFormatSelector() {
		jpgRB = new JRadioButton("JPG", true);
		jpgRB.addChangeListener(this);
		jpgRB.setBounds(10, 0, 60, 20);

		gifRB = new JRadioButton("GIF", false);
		gifRB.addChangeListener(this);
		gifRB.setBounds(70, 0, 60, 20);

		png8RB = new JRadioButton("PNG8", false);
		png8RB.addChangeListener(this);
		png8RB.setBounds(130, 0, 60, 20);

		png24RB = new JRadioButton("PNG24", false);
		png24RB.addChangeListener(this);
		png24RB.setBounds(190, 0, 60, 20); // llega hasta 10 + 4 * 60 = 250

		formatBG = new ButtonGroup();

		formatBG.add(jpgRB);
		formatBG.add(gifRB);
		formatBG.add(png8RB);
		formatBG.add(png24RB);

		setLayout(null);
		setBounds(15, 23, 260, 20);

		add(jpgRB);
		add(gifRB);
		add(png8RB);
		add(png24RB);
	}

	public void setSelected(String format) {
		setAll(false);

		if (format.compareToIgnoreCase(jpgRB.getText()) == 0) {
			jpgRB.setSelected(true);
		}

		if (format.compareToIgnoreCase(gifRB.getText()) == 0) {
			gifRB.setSelected(true);
		}

		if (format.compareToIgnoreCase(png8RB.getText()) == 0) {
			png8RB.setSelected(true);
		}

		if (format.compareToIgnoreCase(png24RB.getText()) == 0) {
			png24RB.setSelected(true);
		}
	}

	public String getSelected() {
		if (jpgRB.isSelected()) {
			return jpgRB.getText();
		}

		if (gifRB.isSelected()) {
			return gifRB.getText();
		}

		if (png8RB.isSelected()) {
			return png8RB.getText();
		}

		if (png24RB.isSelected()) {
			return png24RB.getText();
		}

		return "";
	}

	private void setAll(boolean selected) {
		jpgRB.setSelected(selected);
		gifRB.setSelected(selected);
		png8RB.setSelected(selected);
		png24RB.setSelected(selected);
	}

	public void setAllEnabled(boolean enabled) {
		jpgRB.setEnabled(enabled);
		gifRB.setEnabled(enabled);
		png8RB.setEnabled(enabled);
		png24RB.setEnabled(enabled);
	}

	public void setThisEnabled(String format) {
		if (format.compareToIgnoreCase(jpgRB.getText()) == 0) {
			jpgRB.setEnabled(true);
		}

		if (format.compareToIgnoreCase(gifRB.getText()) == 0) {
			gifRB.setEnabled(true);
		}

		if (format.compareToIgnoreCase(png8RB.getText()) == 0) {
			png8RB.setEnabled(true);
		}

		if (format.compareToIgnoreCase(png24RB.getText()) == 0) {
			png24RB.setEnabled(true);
		}
	}

	public void addListener(ImageServicePanel panel) {
		listener = panel;
	}

	public void stateChanged(ChangeEvent arg0) {
		if (listener != null) {
			ActionEvent ae = new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, "");
			listener.actionPerformed(ae);
		}
	}
}
