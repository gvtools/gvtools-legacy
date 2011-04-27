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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.symbology.fmap.styles.SimpleMarkerFillPropertiesStyle;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.styles.IMarkerFillPropertiesStyle;

/**
 * Implements a tab to modify attributes to fill the padding of a polygon
 * such offset and separation (between pictures or markers).<p>
 * <p>
 * This tab is used several times in different places in our applicattion becuase the
 * behaviour is the same if the user is filling the padding of a polygon using pictures
 * or makers .For this reason, in order to avoid the repetition of code, this class has been
 * created (instead of treat it like a simple tab). With this solution, the user
 * only has to refer it to use it (and do not need to create a tab and fill it again
 * and so on).
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class MarkerFillProperties extends JPanel {
	private static final long serialVersionUID = 2873569057822494979L;
	private static final double DEFAULT_SEPARATION = 20;
	private static final double DEFAULT_OFFSET = 10;
	private JIncrementalNumberField txtOffsetX;
	private JIncrementalNumberField txtOffsetY;
	private JIncrementalNumberField txtSeparationX;
	private JIncrementalNumberField txtSeparationY;
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	private ActionListener action = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < listeners.size(); i++) {
				((ActionListener) listeners.get(i)).actionPerformed(e);
			}
		}
	};
	/**
	 * Constructor method
	 *
	 */
	public MarkerFillProperties() {
		super();
		initialize();
	}

	/**
	 * Initializes the parameters to create a tab to modify attributes to fill the
	 * padding of a polygon such offset and separation (between pictures or markers)
	 *
	 */
	private void initialize() {
		GridLayout layout = new GridLayout();
		layout.setColumns(1);
		layout.setVgap(5);
		setName(PluginServices.getText(this, "fill_properties"));
		JPanel offsetPnl = new JPanel();
		offsetPnl.setBorder(BorderFactory.
				createTitledBorder(null,
						PluginServices.getText(this, "offset")));

		// add components to the offset panel here
		{
			JPanel aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5,5));
			aux.add(new JLabel("X:"));
			aux.add(txtOffsetX = new JIncrementalNumberField("0", 10, 0, 150,1));
			offsetPnl.add(aux);

			aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5,5));
			aux.add(new JLabel("Y:"));
			aux.add(txtOffsetY = new JIncrementalNumberField("0", 10, 0, 150,1));
			offsetPnl.add(aux);



		}
		layout.setRows(offsetPnl.getComponentCount());
		offsetPnl.setLayout(layout);

		add(offsetPnl);

		JPanel separationPnl = new JPanel();
		layout = new GridLayout();
		layout.setColumns(1);
		layout.setVgap(5);
		separationPnl.setBorder(BorderFactory.
				createTitledBorder(null,
						PluginServices.getText(this, "separation")));

		// add components to the separation panel here
		{
			JPanel aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5,5));
			aux.add(new JLabel("X:"));
			aux.add(txtSeparationX = new JIncrementalNumberField("0", 10,0, 150,1));
			separationPnl.add(aux);

			aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5,5));
			aux.add(new JLabel("Y:"));
			aux.add(txtSeparationY = new JIncrementalNumberField("0", 10, 0, 150,1));
			separationPnl.add(aux);
		}
		layout.setRows(separationPnl.getComponentCount());
		separationPnl.setLayout(layout);
		add(separationPnl);
		layout = new GridLayout();
		layout.setColumns(1);
		layout.setVgap(5);
		layout.setRows(getComponentCount());
		txtOffsetX.setDouble(DEFAULT_OFFSET);
		txtOffsetY.setDouble(DEFAULT_OFFSET);
		txtSeparationX.setDouble(DEFAULT_SEPARATION);
		txtSeparationY.setDouble(DEFAULT_SEPARATION);

		txtOffsetX.addActionListener(action);
		txtOffsetY.addActionListener(action);
		txtSeparationX.addActionListener(action);
		txtSeparationY.addActionListener(action);

		setLayout(layout);
	}
	/**
	 * Sets the graphical component that shows the properties of the model.
	 * @param fillProps,IMarkerFillPropertiesStyle
	 */
	public void setModel(IMarkerFillPropertiesStyle fillProps) {
		if (fillProps != null) {
			txtOffsetX.setDouble(fillProps.getXOffset());
			txtOffsetY.setDouble(fillProps.getYOffset());
			txtSeparationX.setDouble(fillProps.getXSeparation());
			txtSeparationY.setDouble(fillProps.getYSeparation());
		}
	}

	/**
	 * Obtains the MarkerFillProperties
	 *
	 * @return mfProps,IMarkerFillPropertiesStyle
	 */
	public IMarkerFillPropertiesStyle getMarkerFillProperties() {
		SimpleMarkerFillPropertiesStyle mfProps = new SimpleMarkerFillPropertiesStyle();
		mfProps.setXOffset(txtOffsetX.getDouble());
		mfProps.setYOffset(txtOffsetY.getDouble());
		mfProps.setXSeparation(txtSeparationX.getDouble());
		mfProps.setYSeparation(txtSeparationY.getDouble());
		return mfProps;
	}
	/**
	 * Permits the good operation of the JIncrementalNumberFields that are included
	 * in the panel
	 * @param l,ActionListener
	 */

	public void addActionListener(ActionListener l) {
		listeners.add(l);
	}

	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);
		txtOffsetX.setEnabled(enabled);
		txtOffsetY.setEnabled(enabled);
		txtSeparationX.setEnabled(enabled);
		txtSeparationY.setEnabled(enabled);
	}
}
