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

import java.io.File;
import java.io.IOException;

import org.gvsig.symbology.AllTests;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.GTLayerFactory;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ILabelingStrategyPanel;

public class GeneralLabelingStrategyPanelTestCase extends LabelingPanelTestCase {
	
	public GeneralLabelingStrategyPanelTestCase() {
		super(GeneralLabeling.class);
	}
	
	@Override
	public FLayer getTestLayer() {
		AllTests.setUpDrivers();
		try {
			return GTLayerFactory.createVectorLayer("test layer",
					new File("src-test/test-data/layer-sample-files/cons_punt.shp"),
					com.iver.cit.gvsig.fmap.AllTests.TEST_DEFAULT_MERCATOR_PROJECTION);
		} catch (IOException e) {
			return null;
		}
	}
	
	@Override
	public MapContext getTestMapContext() {
		return com.iver.cit.gvsig.fmap.AllTests.newMapContext(
				com.iver.cit.gvsig.fmap.AllTests.TEST_DEFAULT_MERCATOR_PROJECTION);
	}
	
	@Override
	protected void initPanel(ILabelingStrategyPanel p) {
		GeneralLabeling.addLabelingMethod(DefaultLabeling.class);
	}
}
