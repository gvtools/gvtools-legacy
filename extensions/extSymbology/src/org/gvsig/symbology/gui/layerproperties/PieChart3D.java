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

import java.sql.Types;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.symbology.fmap.rendering.PieChart3DLegend;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.gui.styling.SymbolEditor;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ILegendPanel;

/**
 * 
 * PieChart3D.java
 *
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jul 18, 2008
 *
 */
public class PieChart3D extends JPanel implements ILegendPanel {
	private static String[] sampleKeys = new String[] { "", " ", "  " };
	private static double[] sampleValues = new double[] { 23, 61, 16 };
	private JCheckBox chkClockwise;
	private JCheckBox chkCircular;
	private JCheckBox chkIgnoreZeroValues;
	private JIncrementalNumberField incrSize;
	private JIncrementalNumberField incrMinAngle;
	private JIncrementalNumberField incrDepthFactor;
	private JSlider alphaSlider;
	private ArrayList<JPanel> tabs;
	
	public PieChart3D(SymbolEditor owner) {
		initialize();
	}
	
	private void initialize() {
		tabs = new ArrayList<JPanel>();
		{
			GridBagLayoutPanel p = new GridBagLayoutPanel();
			p.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "pie_properties")));
			p.addComponent(
					PluginServices.getText(this, "size"),
					incrSize = new JIncrementalNumberField(
							"25",
							7,
							0.001,
							Double.POSITIVE_INFINITY,
							1)
					);
			
			p.addComponent(
					PluginServices.getText(this, "minimum_angle_to_draw"),
					incrMinAngle = new JIncrementalNumberField(
							"1",
							7,
							0.001,
							Double.POSITIVE_INFINITY,
							1)
					);
			p.addComponent(
					PluginServices.getText(this, "depth_factor"),
					incrDepthFactor = new JIncrementalNumberField(
							"0.3",
							7,
							0,
							1,
							0.1)
					);
			p.addComponent(chkClockwise = new JCheckBox(PluginServices.getText(this, "anticlockwise")));
			p.addComponent(chkCircular = new JCheckBox(PluginServices.getText(this, "always_circular")));
			p.addComponent(chkIgnoreZeroValues = new JCheckBox(PluginServices.getText(this, "ignore_zero_values")));
			p.addComponent(PluginServices.getText(this, "foreground_alpha")+":", alphaSlider = new JSlider(0, 100));
			
			p.setName(getName());
			tabs.add(p);
		}
		
	}

//
//	@Override
//	public ISymbol getLayer() {
//		PieChart3DSymbol layer = new PieChart3DSymbol();
//		layer.setClockwise(!chkClockwise.isSelected());
//		layer.setCircular(chkCircular.isSelected());
//		layer.setMinimumAngleToDraw(incrMinAngle.getDouble());
//		layer.setDepthFactor(incrDepthFactor.getDouble());
//		layer.setSize(incrSize.getDouble());
//		layer.setKeys(sampleKeys);
//		layer.setValues(sampleValues);
//		layer.setForegroundAlpha((float) (alphaSlider.getValue()*1.0/100));
//		layer.setIgnoreZeroValues(chkIgnoreZeroValues.isSelected());
//		return layer;
//	}

	public String getTitle() {
		return PluginServices.getText(this, "piechart_3D");
	}

//	@Override
//	public void refreshControls(ISymbol layer) {
//		if (layer == null) {
//			layer = new PieChart3DSymbol();
//		}
//		
//		PieChart3DSymbol pie = (PieChart3DSymbol) layer;
//		chkClockwise.setSelected(!pie.isClockwise());
//		chkCircular.setSelected(pie.isCircular());
//		incrMinAngle.setDouble(pie.getMinimumAngleToDraw());
//		incrDepthFactor.setDouble(pie.getDepthFactor());
//		incrSize.setDouble(pie.getSize());
//		alphaSlider.setValue(Math.round(pie.getForegroundAlpha()*100));
//	}

	public String getDescription() {
		return PluginServices.getText(this, "piechart_3D_legend_desc");
	}
	
	public ImageIcon getIcon() {
		return null;
	}
	
	public ILegend getLegend() {
		return null;
	}
	
	public Class getLegendClass() {
		return PieChart3DLegend.class;
	}
	
	public JPanel getPanel() {
		return this;
	}
	
	public Class getParentClass() {
		return Statistics.class;
	}
	
	
	/**
	 * Checks if an specific field contains numerical data
	 * 
	 * @param fieldType	index of the field
	 * 
	 * @return boolean	true or false depending on the type of data (numerical or not)
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
	
	public void setData(FLayer lyr, ILegend legend) {
		// TODO Auto-generated method stub
		
	}
	
	
}
