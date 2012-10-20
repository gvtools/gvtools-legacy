package org.gvsig.gui.beans.defaultbuttonspanel;

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

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelEvent;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelListener;

/**
 * <code>TestDialogPanel</code>. Test para comprobar el funcionamiento de la
 * clase <code>DialogPanel</code>
 * 
 * @version 15/03/2007
 * @author BorSanZa - Borja Sanchez Zamorano (borja.sanchez@iver.es)
 */
public class TestDefaultButtonsPanel implements ButtonsPanelListener {
	private JFrame frame = new JFrame();

	class NewComponentDialog extends DefaultButtonsPanel {
		private static final long serialVersionUID = 4452922507292538671L;

		public NewComponentDialog() {
			super(ButtonsPanel.BUTTONS_YESNO);
			this.setLayout(new BorderLayout());
			JButton b = new JButton("prueba");
			this.add(b, java.awt.BorderLayout.NORTH);
			JButton c = new JButton("prueba2");
			this.add(c, java.awt.BorderLayout.CENTER);
		}
	}

	NewComponentDialog ncd;

	public TestDefaultButtonsPanel() {
		ncd = new NewComponentDialog();

		frame.setSize(640, 480);
		frame.getContentPane().add(ncd);

		ncd.addButtonPressedListener(this);
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new TestDefaultButtonsPanel();
	}

	public void actionButtonPressed(ButtonsPanelEvent e) {
		System.out.println("Botón pulsado: " + e.getButton());
	}
}