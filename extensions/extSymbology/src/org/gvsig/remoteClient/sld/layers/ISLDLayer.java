/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package org.gvsig.remoteClient.sld.layers;

import java.util.ArrayList;

import org.gvsig.remoteClient.sld.SLDLayerFeatureConstraints;
import org.gvsig.remoteClient.sld.filterEncoding.Filter;
import org.gvsig.remoteClient.sld.styles.AbstractSLDStyle;
import org.gvsig.remoteClient.sld.symbolizers.ISLDSymbolizer;
/**
 * Interface that has to be implemented by all the sld Layers 
 *
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 */

public interface ISLDLayer {

	/**
	 * Obtains all the symbolizers for the specific shapeType which are contained in
	 * a layer
	 * 
	 * @param shapeType
	 * @return
	 */
	public ArrayList<ISLDSymbolizer> getSymbolizersByShapeType(int type);
	/**
	 * Obtains all the filters contained in a layer.
	 * 
	 * @return ArrayList of Filters
	 */
	public ArrayList<Filter> getLayerFilters();
	/**
	 * Obtains all the fieldNames contained in a Layer.
	 *  
	 * @return String[] with all the fieldNames
	 */
	public String[] getFieldNames();
	/**
	 * Returns true if the layer has rules with filters for symbolizers which has
	 * the same shapeType as the parameter (the shapeType of the layer).
	 * Otherwise, false.
	 * 
	 * @param shapeType of the layer
	 */
	public boolean layerHasFilterForSymbolizers(int shapeType);
	/**
	 * Returns true if the layer contains filters. Otherwise, false.
	 * 
	 * @return
	 */
	public boolean layerHasFilters();
	/**
	 * Returns true if the layer contains symbolizers of an specific shapeType.
	 * Otherwise, false.
	 * 
	 * @param shapeType
	 * @return
	 */
	public boolean layerHasSymbolizers(int shapeType);
	/**
	 * Returns the name of the layer
	 * @return
	 */
	public String getName();
	/**
	 * Sets the name of the layer
	 * 
	 * @param name
	 */
	public void setName(String name) ;
	/**
	 * Obtains the styles contained in a layer
	 * @return
	 */
	public ArrayList<AbstractSLDStyle> getLayerStyles() ;
	/**
	 * Sets the styles to be contained in a layer
	 * @param layerStyles
	 */
	public void setLayerStyles(ArrayList<AbstractSLDStyle> layerStyles);
	public ArrayList<SLDLayerFeatureConstraints> getLayerFeatureConstraints() ;
	public void setLayerFeatureConstraints(ArrayList<SLDLayerFeatureConstraints> layerFeatureConstraints) ;
	public void addLayerFeatureConstraint(SLDLayerFeatureConstraints layerFeatureCons);
	public void addLayerStyle(AbstractSLDStyle style);
}
