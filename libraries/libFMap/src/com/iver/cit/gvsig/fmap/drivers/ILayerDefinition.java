package com.iver.cit.gvsig.fmap.drivers;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author fjp
 *
 * Junto con ITableDefinition, sirve para declarar
 * los datos de una nueva capa. Lo usamos a la hora de 
 * crear una desde cero.
 */
public interface ILayerDefinition extends ITableDefinition {
	public int getShapeType();
	public void setShapeType(int shapeType);
	
	public CoordinateReferenceSystem getCrs();
	public void setCrs(CoordinateReferenceSystem proj);

}
