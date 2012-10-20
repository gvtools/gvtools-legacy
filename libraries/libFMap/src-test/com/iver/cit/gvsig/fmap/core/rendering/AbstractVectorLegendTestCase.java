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
package com.iver.cit.gvsig.fmap.core.rendering;

import java.awt.Dimension;
import java.awt.Rectangle;

import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleFillSymbol;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;

/**
 * 
 * AbstractVectorLegendTestCase.java
 * 
 * NOTE: Although it has no abstract components, the class is abstract in
 * purpose since it does not test any concrete legend, but initializes legends
 * of this kind. Subclass it to test any concrete Legend: For example:
 * SingleLegendTestCase extends AbstractVectorLegendTestCase
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jun 11, 2008
 * 
 */
public abstract/* <-MUST BE ABSTRACT!! */class AbstractVectorLegendTestCase
		extends AbstractLegendTestCase {

	private static IFeature[] features;
	private Object[] sampleValues;

	public AbstractVectorLegendTestCase(
			Class<? extends IVectorLegend> legClazz, Object[] sampleValues) {
		super(legClazz);
		this.sampleValues = sampleValues;

	}

	public static IFeature[] getFeatures() {
		if (features == null) {
			features = new IFeature[4];
			// initialize test values
			for (int i = 0; i < features.length; i++) {

				// create the geometry associated to the feature
				int size = 200;
				Dimension d = new Dimension(size, size);
				Rectangle aShape = new Rectangle(i * size, i * size, d.width,
						d.height);
				IGeometry geom = ShapeFactory
						.createPolyline2D(new GeneralPathX(aShape));

				/*
				 * create a full-featured Feature with randomed values at its
				 * fields to avoid testing over the same values each time
				 */
				features[i] = new DefaultFeature(
						geom,
						TestClassifiedVectorLegend.mockDataSource.featureValues[i],
						"[" + i + "]");
			}
		}

		return features;
	}

	/**
	 * all vector legends must have a datasource from which take shapetype and/
	 * or other values. this method initializes the legend with the generic
	 * MockDataSource of the test suite.
	 */
	@Override
	public ILegend newInstance() {
		try {
			ILegend leg = (ILegend) legClazz.newInstance();
			IVectorLegend vl = (IVectorLegend) leg;
			vl.setDataSource(TestClassifiedVectorLegend.mockDataSource);
			vl.setShapeType(FShape.POLYGON);
			vl.setDefaultSymbol(new SimpleFillSymbol());

			initLegend(leg);
			return leg;
		} catch (Exception ex) {
			// Hey dude! it is a mock datasource, this can't happen!!!
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		return null;
	}

	@Override
	public Object[] getTestSampleValues() {
		return sampleValues;
	}

}
