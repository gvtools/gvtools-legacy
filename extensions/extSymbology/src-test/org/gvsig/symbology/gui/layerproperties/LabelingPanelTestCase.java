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

import junit.framework.TestCase;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ILabelingStrategyPanel;

/**
 * 
 * LabelingPanelTestCase.java
 *
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jun 13, 2008
 *
 */
public abstract class LabelingPanelTestCase extends TestCase {
	protected Class<? extends ILabelingStrategyPanel> panelClazz;

	public LabelingPanelTestCase(Class<? extends ILabelingStrategyPanel> panelClazz) {
		this.panelClazz = panelClazz;
	}
	
	public ILabelingStrategyPanel newInstance() {
		try {
			ILabelingStrategyPanel p = panelClazz.newInstance();
			initPanel(p);
			return p;
		} catch (Exception e) {
			fail("Failed installing panel to test "+panelClazz.getName());
			return null;
		} 
	}
	
	/**
	 * this method is intended to completely set up the panels. Remember that
	 * you are testing GUI components. So, you must not initialize any labeling strategy
	 * here. just use it if you did design your panel to have plugable further panels, such
	 * are labeling method within the general labeling strategy panel.
	 * 
	 * to test the strategies itself you should refer to the 
	 * org.gvsig.symbology.fmap.labeling.TestLabelingStrategy test!!
	 * 
	 * @param p
	 */
	protected abstract void initPanel(ILabelingStrategyPanel p);
	
	public abstract FLayer getTestLayer();

	public abstract MapContext getTestMapContext() ;
	
	
}
