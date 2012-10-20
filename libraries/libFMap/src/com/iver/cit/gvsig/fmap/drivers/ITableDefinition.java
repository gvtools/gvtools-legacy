package com.iver.cit.gvsig.fmap.drivers;

/**
 * @author fjp
 * 
 *         Los métodos de esta interfaz definen una tabla.
 */
public interface ITableDefinition {

	public String getName();

	public void setName(String layerName);

	public FieldDescription[] getFieldsDesc();

	public void setFieldsDesc(FieldDescription[] fieldsDesc);

}