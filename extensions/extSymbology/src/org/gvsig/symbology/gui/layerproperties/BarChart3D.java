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
package org.gvsig.symbology.gui.layerproperties;

import java.awt.BorderLayout;
import java.sql.Types;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.symbology.fmap.rendering.BarChart3DLegend;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.rendering.ILegend;

/**
 * 
 * BarChart3D.java
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jul 18, 2008
 * 
 */
public class BarChart3D extends ProportionalSymbols {
	private JIncrementalNumberField incrSize;

	public BarChart3D() {
		super();
		initialize();
	}

	public String getDescription() {
		return PluginServices.getText(this, "barchar3d_legend_desc");
	}

	public ImageIcon getIcon() {
		return null;
	}

	public ILegend getLegend() {
		return null;
	}

	public Class getLegendClass() {
		return BarChart3DLegend.class;
	}

	public JPanel getPanel() {
		return this;
	}

	public Class getParentClass() {
		return Statistics.class;
	}

	public String getTitle() {
		return PluginServices.getText(this, "barchar3d_legend");
	}

	/**
	 * Checks if an specific field contains numerical data
	 * 
	 * @param fieldType
	 *            index of the field
	 * 
	 * @return boolean true or false depending on the type of data (numerical or
	 *         not)
	 */
	private boolean isNumericField(int fieldType) {
		switch (fieldType) {
		case Types.BIGINT:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.NUMERIC:
		case Types.REAL:
		case Types.SMALLINT:
		case Types.TINYINT:
			return true;
		default:
			return false;
		}

	}

	public boolean isSuitableFor(FLayer layer) {
		if (layer instanceof FLyrVect) {
			try {
				FLyrVect lyr = (FLyrVect) layer;

				SelectableDataSource sds;
				sds = lyr.getRecordset();
				String[] fNames = sds.getFieldNames();
				for (int i = 0; i < fNames.length; i++) {
					if (isNumericField(sds.getFieldType(i))) {
						return true;
					}
				}
			} catch (ReadDriverException e) {
				return false;
			}
		}
		return false;
	}

	private void initialize() {
		{
			GridBagLayoutPanel p = new GridBagLayoutPanel();
			p.setBorder(BorderFactory.createTitledBorder(PluginServices
					.getText(this, "pie_properties")));
			p.addComponent(PluginServices.getText(this, "size"),
					incrSize = new JIncrementalNumberField("25", 7, 0.001,
							Double.POSITIVE_INFINITY, 1));

			((JPanel) getComponent(0)).add(p, BorderLayout.SOUTH);
		}
	}

	/*
	 * @Override public ISymbol getLayer() { BarChart3DSymbol layer = new
	 * BarChart3DSymbol(); layer.setSize(incrSize.getDouble());
	 * layer.setRowKeys(new String[] { "row1", "row2", "row3" });
	 * layer.setColumnKeys(new String[] { "col1", "col2", "col3" });
	 * layer.setValues( new double[] { 23, 61, 16 });
	 * 
	 * return layer; }
	 */

	@Override
	public String getName() {
		return PluginServices.getText(this, "barchart_3D");
	}

	public void setData(FLayer lyr, ILegend legend) {
		// TODO Auto-generated method stub

	}
}
