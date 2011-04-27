package com.iver.cit.gvsig.fmap.drivers;

import org.cresques.cts.IProjection;

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
	
	public IProjection getProjection();
	public void setProjection(IProjection proj);

}
