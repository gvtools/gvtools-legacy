/**
 *
 */
package com.iver.cit.gvsig.fmap.edition;

/**
 * @author fjp
 * 
 */
public interface ISpatialWriter extends IWriter {

	public abstract boolean canWriteGeometry(int gvSIGgeometryType);

	// public void initialize(FLayer layer) throws EditionException;

	// void setFlatness(double flatness);

	// TODO: Casi seguro que necesitaremos algo como esto.
	// public void initialize(ILayerDefinition lyrDef) throws EditionException;

}
