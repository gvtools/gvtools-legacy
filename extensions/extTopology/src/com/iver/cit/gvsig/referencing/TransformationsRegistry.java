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
package com.iver.cit.gvsig.referencing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.MismatchedSizeException;

import org.geotools.referencefork.geometry.DirectPosition2D;
import org.geotools.referencefork.referencing.operation.builder.AdvancedAffineBuilder;
import org.geotools.referencefork.referencing.operation.builder.AffineTransformBuilder;
import org.geotools.referencefork.referencing.operation.builder.BursaWolfTransformBuilder;
import org.geotools.referencefork.referencing.operation.builder.MappedPosition;
import org.geotools.referencefork.referencing.operation.builder.MathTransformBuilder;
import org.geotools.referencefork.referencing.operation.builder.ProjectiveTransformBuilder;
import org.geotools.referencefork.referencing.operation.builder.RubberSheetBuilder;
import org.geotools.referencefork.referencing.operation.builder.SimilarTransformBuilder;
import org.opengis.referencing.FactoryException;
import org.opengis.spatialschema.geometry.DirectPosition;
import org.opengis.spatialschema.geometry.MismatchedDimensionException;
import org.opengis.spatialschema.geometry.MismatchedReferenceSystemException;

import com.iver.andami.PluginServices;

public class TransformationsRegistry {

	public interface TransformationRegistryEntry {
		public String getName();

		public MathTransformBuilder createTransformBuilder(
				List<MappedPosition> links);
	}

	private static final Map<String, TransformationRegistryEntry> registeredTransform = new HashMap<String, TransformationRegistryEntry>();

	static {
		TransformationRegistryEntry projective = new TransformationRegistryEntry() {
			
			
			public MathTransformBuilder createTransformBuilder(
					List<MappedPosition> links) {
				
				return new ProjectiveTransformBuilder(links);
			}

			public String getName() {
				return PluginServices.getText(null, "PROJECTIVE_TRANSFORM");
			}
			
			//Move to an abstract class
			public String toString(){
				return getName();
			}
		};

		registerTransformation(projective);

		TransformationRegistryEntry affine = new TransformationRegistryEntry() {

			
			
			public MathTransformBuilder createTransformBuilder(
					List<MappedPosition> links) {
				return new AffineTransformBuilder(links);
				
			}

			public String getName() {
				return PluginServices.getText(null, "AFFINE_TRANSFORM");
			}
			
			public String toString(){
				return getName();
			}
		};

		registerTransformation(affine);

		TransformationRegistryEntry similar = new TransformationRegistryEntry() {

			
			
			public MathTransformBuilder createTransformBuilder(
					List<MappedPosition> links) {
				return new SimilarTransformBuilder(links);
			}

			public String getName() {
				return PluginServices.getText(null, "SIMILAR_TRANSFORM");
			}
			
			public String toString(){
				return getName();
			}
		};

		registerTransformation(similar);

		TransformationRegistryEntry bursaWolf = new TransformationRegistryEntry() {
			
			public MathTransformBuilder createTransformBuilder(
					List<MappedPosition> links) {
				return new BursaWolfTransformBuilder(links);
			}

			public String getName() {
				return PluginServices.getText(null, "BURSA_WOLF_TRANSFORM");
			}
			
			public String toString(){
				return getName();
			}
		};

		registerTransformation(bursaWolf);

		TransformationRegistryEntry rubberSheet = new TransformationRegistryEntry() {
			public MathTransformBuilder createTransformBuilder(
					List<MappedPosition> links) {
				return new RubberSheetBuilder(links, createRoi(links));
			}

			public String getName() {
				return PluginServices.getText(null, "RUBBER_SHEET_TRANSFORM");
			}
			
			public String toString(){
				return getName();
			}
		};

		registerTransformation(rubberSheet);

		TransformationRegistryEntry advAffine = new TransformationRegistryEntry() {
			
			public MathTransformBuilder createTransformBuilder(
					List<MappedPosition> links) {
				try {
					return new AdvancedAffineBuilder(links);
				} catch (MismatchedDimensionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MismatchedReferenceSystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MismatchedSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FactoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			public String getName() {
				return PluginServices
						.getText(null, "ADVANCED_AFFINE_TRANSFORM");
			}
			
			public String toString(){
				return getName();
			}
		};

		registerTransformation(advAffine);
	}

	public static void registerTransformation(TransformationRegistryEntry entry) {
		// TODO Use extension points
		if (registeredTransform.get(entry.getName()) == null) {
			registeredTransform.put(entry.getName(), entry);
		}
	}
	
	public static TransformationRegistryEntry getRegisteredTransform(String name){
		return registeredTransform.get(name);
		
	}
	
	
	public static Collection<TransformationRegistryEntry> getRegisteredTransforms(){
		return registeredTransform.values();
	}
	
	
	public static List<DirectPosition> createRoi(List<MappedPosition> mappedPositions){
		List<DirectPosition> solution = new ArrayList<DirectPosition>();
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
		
		for (int i = 0; i < mappedPositions.size(); i++) {
			MappedPosition position = mappedPositions.get(i);
			DirectPosition source = position.getSource();
			double[] sourceCoords = source.getCoordinates();
			if(sourceCoords[0] > maxX)
				maxX = sourceCoords[0];
			if(sourceCoords[0] < minX)
				minX = sourceCoords[0];
			if(sourceCoords[1] > maxY)
				maxY = sourceCoords[1];
			if(sourceCoords[1] < minY)
				minY = sourceCoords[1];
			
			
			
			DirectPosition target = position.getTarget();
			double[] targetCoords = target.getCoordinates();
			if(targetCoords[0] > maxX)
				maxX = targetCoords[0];
			if(targetCoords[0] < minX)
				minX = targetCoords[0];
			if(targetCoords[1] > maxY)
				maxY = targetCoords[1];
			if(targetCoords[1] < minY)
				minY = targetCoords[1];
			
		}
		//azabala: we add this epsilon because in Geotools implemention when a Coordinate falls on
    	//a rectangle edge, represented as GeneralPathX, contains() returns false
//    	final double EPSILON = 0.01;
		final double EPSILON = 0.5;
    	minX -= EPSILON;
    	maxX += EPSILON;
    	minY -= EPSILON;
    	maxY += EPSILON;
		
		//El orden de a, b, c y d es muy importante, pues RubberSheetBuilder no ordena los puntos
		DirectPosition a = new DirectPosition2D(minX, minY);
		DirectPosition b = new DirectPosition2D(minX, maxY);
		DirectPosition c = new DirectPosition2D(maxX, maxY);
		DirectPosition d = new DirectPosition2D(maxX, minY);
		
		solution.add(a);
		solution.add(b);
		solution.add(c);
		solution.add(d);
		
		return solution;
	}

}
