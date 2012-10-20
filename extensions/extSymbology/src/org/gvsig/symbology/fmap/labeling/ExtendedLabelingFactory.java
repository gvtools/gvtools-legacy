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
package org.gvsig.symbology.fmap.labeling;

import org.gvsig.symbology.fmap.labeling.placements.LinePlacementConstraints;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IZoomConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelingFactory;

/**
 * 
 * ExtendedLabelingFactory.java
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Mar 6, 2008
 * 
 */
public class ExtendedLabelingFactory {

	/**
	 * Given a layer, a labeling method, a label placements constraints, and a
	 * label zoom constraints it will figure out the best ILabelingStrategy that
	 * meets all the needs.
	 * 
	 * @param layer
	 *            , the target layer
	 * @param method
	 *            , the desired methods
	 * @param placement
	 *            , the desired placement constraints
	 * @param zoom
	 *            , the desired zoom constraints
	 * @return ILabelingStrategy
	 * @throws DriverException
	 */
	public static ILabelingStrategy createStrategy(FLayer layer,
			ILabelingMethod method, IPlacementConstraints placement,
			IZoomConstraints zoom) {

		if (placement instanceof LinePlacementConstraints
				&& placement.isFollowingLine()) {
			/*
			 * ILabelingStrategy strat = LabelingFactory.createStrategy(layer,
			 * method, placement, zoom); ILabelingMethod aMethod =
			 * strat.getLabelingMethod(); try { ILabelingMethod theMethod =
			 * (ILabelingMethod)
			 * Class.forName(aMethod.getClassName()).newInstance(); if
			 * (theMethod instanceof AbstractLabelingMethod) { Field[] ff =
			 * AbstractLabelingMethod.class.getDeclaredFields(); for (int i = 0;
			 * i < ff.length; i++) { int oldModifiers = ff[i].getModifiers();
			 * ff[i].setAccessible(true); if (ff[i].get(theMethod) instanceof
			 * LabelClass) { // the default class LabelClass lc = (LabelClass)
			 * ff[i].get(theMethod); XMLEntity xml = lc.getXMLEntity();
			 * xml.putProperty("className",
			 * SmartTextSymbolLabelClass.class.getName()); ff[i].set(theMethod,
			 * LabelingFactory.createLabelClassFromXML(xml)); } else { try {
			 * ArrayList<LabelClass> classes = (ArrayList<LabelClass>)
			 * ff[i].get(theMethod); ArrayList<SmartTextSymbolLabelClass>
			 * smClasses = new ArrayList<SmartTextSymbolLabelClass>(); for (int
			 * j = 0; j < classes.size(); j++) { XMLEntity xml =
			 * classes.get(j).getXMLEntity(); xml.putProperty("className",
			 * SmartTextSymbolLabelClass.class.getName());
			 * smClasses.add((SmartTextSymbolLabelClass)
			 * LabelingFactory.createLabelClassFromXML(xml)); }
			 * ff[i].set(theMethod, smClasses); } catch (ClassCastException
			 * ccEx) { ccEx.printStackTrace(); } } } } else throw new
			 * Exception("on purpose exception");
			 * 
			 * } catch (Exception e) { /* this should be unreachable this is
			 * only in case of panic keep a working config set. /
			 * Logger.getLogger(ExtendedLabelingFactory.class).error(
			 * Messages.getString
			 * ("failed_applying_following_line_will_use_default_confg"), e);
			 * placement = new LinePlacementConstraints(); }
			 */
		}

		return LabelingFactory.createStrategy(layer, method, placement, zoom);

	}
}
