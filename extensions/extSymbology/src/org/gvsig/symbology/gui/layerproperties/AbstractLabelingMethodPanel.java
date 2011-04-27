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

import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingMethod;

public abstract class AbstractLabelingMethodPanel extends JPanel implements PropertyChangeListener {
	public static final String PLACEMENT_CONSTRAINTS = "PLACEMENT_CONSTRAINTS";
	public static final String ALLOW_OVERLAP = "ALLOW_OVERLAP";
	public static final String ZOOM_CONSTRAINTS= "ZOOM_CONSTRAINTS";
	
	protected FLyrVect layer;
	protected ILabelingMethod method;
	
	public abstract String getName();
	public abstract Class<? extends ILabelingMethod> getLabelingMethodClass();
	public void setModel(ILabelingMethod method, FLyrVect srcLayer) throws ReadDriverException {
		if (srcLayer == null) {
			throw new ReadDriverException("Null DATASOURCE!", null);
		}
		this.layer = srcLayer;

		try {
			if (method!= null && method.getClass().equals(getLabelingMethodClass())) {
				this.method = method;
			} else {
				this.method = getLabelingMethodClass().newInstance();
			}
			initializePanel();
			
		} catch (Exception e) {
			throw new ReadDriverException(
					srcLayer.getRecordset().getDriver().getName(), 
					new Error("Unable to load labeling method. Is it in your classpath?", e)); 
		}
		fillPanel(this.method, srcLayer.getRecordset());
	}
	
	protected abstract void initializePanel() ;
	public abstract void fillPanel(ILabelingMethod method, SelectableDataSource dataSource) throws ReadDriverException;
	
	@Override
	public String toString() {
		return getName();
	}
	
	public ILabelingMethod getMethod() {
		return method; 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return getClass().equals(obj.getClass());
	}
}
