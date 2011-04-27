/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package org.gvsig.graph.solvers;

import java.util.ArrayList;

import com.hardcode.gdbms.engine.values.DoubleValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;

/**
 * @author fjp
 * featureList es un array de IFeature.
 * Los campos que tiene cada feature son:
 * 0-> Id_Tramo (idArc sobre el que está ese tramo de ruta.
 * 1-> Weight
 * 2-> Length
 * 3-> Texto calle o via por la que pasa.
 *
 */
public class Route {
	
	public static final int ID_ARC_INDEX = 0;
	public static final int WEIGHT_INDEX = 1;
	public static final int LENGTH_INDEX = 2;
	public static final int TEXT_INDEX = 3;
	
	
	
	
	
	
	
	public IFeature addRouteFeature(IGeometry geom, int idArc, double weight, double length, String text)
	{
		Value[] values = new Value[4];
		values[0] = ValueFactory.createValue(idArc);
		values[1] = ValueFactory.createValue(weight);
		values[2] = ValueFactory.createValue(length);
		values[3] = ValueFactory.createValue(text);
		// System.out.println("Añado text= " + idArc + ": " + text);
		DefaultFeature feat = new DefaultFeature(geom, values, featureList.size() + "");
		featureList.add(feat);
		return feat;
	}
	private ArrayList featureList = new ArrayList();
	
	public ArrayList getFeatureList() 
	{
		return featureList;
	}
	
	public String getInstructions() {
		String instructions = "";
		double  distTotal = 0;
		for (int i=featureList.size()-1; i>=0; i--)
		{
			IFeature feat = (IFeature) featureList.get(i);
			DoubleValue length = (DoubleValue) feat.getAttribute(2);
			double dist = length.doubleValue();
			distTotal += dist;
			instructions = instructions + "\n" + feat.getAttribute(3) + " dist=" + dist;
		}
		return instructions;
	}
	
	public double getLength() {
		double  distTotal = 0;
		for (int i=0; i < featureList.size(); i++)
		{
			IFeature feat = (IFeature) featureList.get(i);
			DoubleValue length = (DoubleValue) feat.getAttribute(2);
			double dist = length.doubleValue();
			distTotal += dist;
		}
		return distTotal;
	}
	
	public double getCost() {
		double  costTotal = 0;
		for (int i=0; i < featureList.size(); i++)
		{
			IFeature feat = (IFeature) featureList.get(i);
			DoubleValue costVal = (DoubleValue) feat.getAttribute(1);
			double cost = costVal.doubleValue();
			costTotal += cost;
		}
		return costTotal;		
	}
}


