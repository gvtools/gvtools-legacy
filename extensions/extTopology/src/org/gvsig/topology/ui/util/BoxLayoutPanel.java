/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
*
* $Id: 
* $Log: 
*/
package org.gvsig.topology.ui.util;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Utility class to easy creation of 'forms-style' panels.
 * Its very similar to GridBagLayoutPanel, but it is based in BoxLayout instead of
 * GridBagLayout.
 * @author Alvaro Zabala
 *
 */
public class BoxLayoutPanel extends JPanel {

	private static final long serialVersionUID = 3827966354089679323L;

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 20;
	
	public BoxLayoutPanel(){
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	
	public void addComponent(String fieldName, JComponent component){
		addComponent(fieldName, component, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	
	public void addComponent(String fieldName, JComponent component, int width, int height)
	{
		add(Box.createVerticalStrut(10));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalStrut(15));
		panel.add(new JLabel(fieldName));
		panel.add(Box.createHorizontalStrut(5));
		panel.add(component);
		panel.setPreferredSize(new Dimension(width, height));
		panel.setMinimumSize(new Dimension(width, height));
		panel.setMaximumSize(new Dimension(width, height));
		panel.setAlignmentX(LEFT_ALIGNMENT);
		add(panel);
	}
	
	public void addBlank(){
		add(Box.createVerticalStrut(10));
	}
	
	public void addRow(JComponent[] components){
		addRow(components, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	public void addRow(JComponent[] components, int width, int height){
		add(Box.createVerticalStrut(10));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalStrut(15));
		for(int i = 0; i < components.length; i++){
			panel.add(components[i]);
			panel.add(Box.createHorizontalStrut(5));
		}
		panel.setPreferredSize(new Dimension(width, height));
		panel.setMinimumSize(new Dimension(width, height));
		panel.setMaximumSize(new Dimension(width, height));
		panel.setAlignmentX(LEFT_ALIGNMENT);
		add(panel);
	}
	
	
	
	protected void addTitleLabel(  String sText, boolean isSectionTitleLabel){
		JLabel label = new JLabel();
		label.setText(sText);
		label.setPreferredSize(new java.awt.Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		
		if (isSectionTitleLabel){
			label.setFont(new java.awt.Font("Tahoma",1,11));
		}
		addRow(new JComponent[]{label});
	}
		
		
}
