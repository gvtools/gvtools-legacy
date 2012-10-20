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

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.gvsig.symbology.fmap.labeling.placements.CannotPlaceLabel;
import org.gvsig.symbology.fmap.labeling.placements.CompoundLabelPlacement;
import org.gvsig.symbology.fmap.labeling.placements.ILabelPlacement;
import org.gvsig.symbology.fmap.labeling.placements.LinePlacementConstraints;
import org.gvsig.symbology.fmap.labeling.placements.MultiShapePlacement;
import org.gvsig.symbology.fmap.labeling.placements.MultiShapePlacementConstraints;
import org.gvsig.symbology.fmap.labeling.placements.PointPlacementConstraints;
import org.gvsig.symbology.fmap.labeling.placements.PolygonPlacementConstraints;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.utiles.IPersistence;
import com.iver.utiles.NotExistInXMLEntity;
import com.iver.utiles.XMLEntity;

/**
 * 
 * PlacementManager.java
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jan 3, 2008
 * 
 */
public class PlacementManager {
	private static Logger logger = Logger.getLogger(PlacementManager.class
			.getName());
	private static ArrayList<Class<?>> installedLabelPlacements = new ArrayList<Class<?>>();
	private static ArrayList<ILabelPlacement> availableLabelPlacements;
	private static final CannotPlaceLabel cantPlaceLabel = new CannotPlaceLabel();

	private PlacementManager() {
	}

	public static ILabelPlacement getPlacement(
			IPlacementConstraints placementConstraints, int shapeType) {

		ArrayList<ILabelPlacement> suitablePlacements = new ArrayList<ILabelPlacement>();
		if (placementConstraints.getClass().equals(
				MultiShapePlacementConstraints.class)) {
			MultiShapePlacementConstraints msp = (MultiShapePlacementConstraints) placementConstraints;

			return new MultiShapePlacement(getPlacement(
					msp.getPointConstraints(), FShape.POINT), getPlacement(
					msp.getLineConstraints(), FShape.LINE), getPlacement(
					msp.getPolygonConstraints(), FShape.POLYGON));
		} else {
			for (Iterator<ILabelPlacement> iterator = getAvailablePlacements()
					.iterator(); iterator.hasNext();) {
				ILabelPlacement placement = iterator.next();
				if (placement.isSuitableFor(placementConstraints, shapeType)) {
					suitablePlacements.add(placement);
				}
			}

			if (suitablePlacements.size() == 0)
				return cantPlaceLabel;
			else if (suitablePlacements.size() == 1)
				return suitablePlacements.get(0);
			else
				return new CompoundLabelPlacement(
						(ILabelPlacement[]) suitablePlacements
								.toArray(new ILabelPlacement[suitablePlacements
										.size()]));

		}

	}

	public static void addLabelPlacement(Class<?> labelPlacementClass) {
		// if (!labelPlacementClass.isInstance(ILabelPlacement.class))
		// throw new IllegalArgumentException(
		// labelPlacementClass.getName()+" is not an valid label placement algorithm.");
		installedLabelPlacements.add(labelPlacementClass);

		// invalidate current list of available placements
		if (availableLabelPlacements != null)
			availableLabelPlacements.clear();
		availableLabelPlacements = null;
	}

	private static ArrayList<ILabelPlacement> getAvailablePlacements() {
		if (availableLabelPlacements == null) {
			availableLabelPlacements = new ArrayList<ILabelPlacement>(
					installedLabelPlacements.size());
			for (Iterator<Class<?>> iterator = installedLabelPlacements
					.iterator(); iterator.hasNext();) {
				Class<?> clazz = null;
				try {
					clazz = iterator.next();
					availableLabelPlacements.add((ILabelPlacement) clazz
							.newInstance());
				} catch (Exception e) {
					Logger.getLogger(PlacementManager.class).error(
							"couldn't install label placement '"
									+ clazz.getName(), e);
				}
			}
		}

		return availableLabelPlacements;
	}

	/**
	 * Creates a new instance of placement constraints from a vector layer. The
	 * placement constraints are created according the layer shape type.
	 * 
	 * @param layerDest
	 * @return
	 * @throws DriverException
	 */
	public static IPlacementConstraints createPlacementConstraints(int shapeType) {
		switch (shapeType % FShape.Z) {
		case FShape.LINE:
			return new LinePlacementConstraints();
		case FShape.POLYGON:
			return new PolygonPlacementConstraints();
		case FShape.POINT:
		case FShape.MULTIPOINT: // TODO (09/01/08) is this correct??? if not fix
								// it also in PlacementProperties (twice), ,
								// MarkerPlacementAroundPoint
			return new PointPlacementConstraints();
		case FShape.MULTI:
			return new MultiShapePlacementConstraints(
					createPlacementConstraints(FShape.POINT),
					createPlacementConstraints(FShape.LINE),
					createPlacementConstraints(FShape.POLYGON));
		}
		throw new Error("Shape type not yet supported");
		// return null;
	}

	public static IPlacementConstraints createPlacementConstraints(
			XMLEntity entity) {
		String className = null;
		try {
			className = entity.getStringProperty("className");
		} catch (NotExistInXMLEntity e) {
			logger.error("Symbol class name not set.\n"
					+ " Maybe you forgot to add the"
					+ " putProperty(\"className\", yourClassName)"
					+ " call in the getXMLEntity method of your symbol", e);
		}

		Class clazz = null;
		Object obj = null;
		try {
			clazz = Class.forName(className);
			obj = clazz.newInstance();
			((IPersistence) obj).setXMLEntity(entity);

		} catch (InstantiationException e) {
			logger.error(
					"Trying to instantiate an interface"
							+ " or abstract class + " + className + "\n"
							+ e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.error(null, e);
		} catch (ClassNotFoundException e) {
			logger.error("No class called " + className
					+ " was found.\nCheck the following.\n<br>"
					+ "\t- The fullname of the class you're looking "
					+ "for matches the value in the className "
					+ "property of the XMLEntity (" + className + ").\n<br>"
					+ "\t- The jar file containing your symbol class is in"
					+ "the application classpath<br>", e);
		}
		return (IPlacementConstraints) obj;
	}

}
