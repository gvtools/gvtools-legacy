/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2007 IVER T.I. and Generalitat Valenciana.
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
 */
package org.gvsig.rastertools.utils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gvsig.rastertools.TestUI;

import com.iver.utiles.swing.JComboBox;

/**
 * 
 * 13/03/2009
 * @author Nacho Brodin nachobrodin@gmail.com
 */
public class TestPanelSustitution implements ActionListener {
	private TestUI 			jFrame = new TestUI("TestRasterFilterPanel");
	private JPanel	        panel1 = null;
	private JPanel	        panel2 = null;
	private JPanel	        main = null;
	private JButton         b = new JButton("Cambiar");
	private boolean         p1 = true;

	public TestPanelSustitution() {
		initialize();
	}

	public static void main(String[] args){
		new TestPanelSustitution();
	}

	private void initialize() {
		main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(getPanel1(), BorderLayout.CENTER);
		main.add(b, BorderLayout.SOUTH);

		jFrame.setSize(new java.awt.Dimension(645, 480));
		jFrame.setContentPane(main);
		jFrame.setResizable(true);
		jFrame.setTitle("TestRasterFilterPanel");
		jFrame.setVisible(true);
		b.addActionListener(this);
	}
	
	public JPanel getPanel1() {
		if(panel1 == null) {
			panel1 = new JPanel();
			panel1.setLayout(new BorderLayout());
			JTextField f = new JTextField();
			JLabel l = new JLabel("Prueba de panel principal");
			panel1.add(f, BorderLayout.CENTER);
			panel1.add(l, BorderLayout.SOUTH);
		}
		return panel1;
	}
	
	public JPanel getPanel2() {
		if(panel2 == null) {
			panel2 = new JPanel();
			panel2.setLayout(new BorderLayout());
			JComboBox f = new JComboBox();
			JLabel l = new JLabel("Este es el panel secundario");
			panel2.add(f, BorderLayout.NORTH);
			panel2.add(l, BorderLayout.SOUTH);
		}
		return panel2;
	}

	public void actionPerformed(ActionEvent e) {
		if(p1) {
			main.remove(getPanel1());
			main.add(getPanel2(), BorderLayout.CENTER);
		} else {
			main.remove(getPanel2());
			main.add(getPanel1(), BorderLayout.CENTER);
		}
		p1 = !p1;
		main.updateUI();
	}
}