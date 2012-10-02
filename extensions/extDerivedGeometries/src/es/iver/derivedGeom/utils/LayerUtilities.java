package es.iver.derivedGeom.utils;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

import java.io.File;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.Driver;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

/**
 * <p>Useful utilities to manage different kind of layers.</p>
 *
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class LayerUtilities {
	/**
	 * <p>Identifier of the shape driver</p>
	 */
	public static final String SHAPE_DRIVER_ID = "gvSIG shp driver";

	/** 
	 * <p>Creates a new shape layer in <code>mapContext</code> with the specified name, at <code>path</code>.</p>
	 * 
	 * @param mapContext {@link MapControl MapControl}'s <code>MapContext</code> where create the layer.
	 * @param path the path where create the shape layer files (included the file name and main shape extension).
	 * @param name the name of the new layer.
	 * @param geometryType type of geometries for the new layer. (See {@link FShape FShape}).
	 * @param crs the projection of the new layer.
	 * @param fieldsDesc properties of the fields of the new layer.
	 *
	 * @return the new layer created, or <code>null</code> if has happened any problem
	 * 
	 * @throws Exception if fails creating the layer
	 */
	public static FLyrVect createShapeLayer(MapContext mapContext, File path,
			String name, int geometryType, CoordinateReferenceSystem crs,
			FieldDescription[] fieldsDesc) throws Exception {
		/* Gets the shape driver */
		Driver drv = LayerFactory.getDM().getDriver(SHAPE_DRIVER_ID);

		/* Sets the parameters of the creation */
	    SHPLayerDefinition layerDefinition = new SHPLayerDefinition();
	    layerDefinition.setFieldsDesc(fieldsDesc);
	    layerDefinition.setFile(path);
	    layerDefinition.setName(name);
	    layerDefinition.setShapeType(geometryType);

	    /* Gets the shape writer */
		ShpWriter writer = (ShpWriter)LayerFactory.getWM().getWriter("Shape Writer");
		
		/* Sets parameters to the writer */
		writer.setFile(path);
		writer.initialize(layerDefinition);

		/* Prepares the layer to be created */
		writer.preProcess();
		writer.postProcess();

		/* Creates the layer */
		FLyrVect layer =  (FLyrVect) LayerFactory.createLayer(name, (VectorialFileDriver) drv, path, crs);

//		/* Sets the geometry layer rules if layer has polygons */
//		VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
//		vea.getRules().clear();
//
//		if (vea.getShapeType() == FShape.POLYGON) {
//			IRule rulePol = new RulePolygon();
//			vea.getRules().add(rulePol);
//		}
		
		return layer;
	}
	
	/** 
	 * <p>Gets a shape layer in <code>mapContext</code> with the specified name, at <code>path</code>.</p>
	 * 
	 * @param path the path where the shape layer files are.
	 * @param name the name of the layer, if <code>null</code> will have the file name.
	 * @param crs the projection of the layer.
	 *
	 * @return the new layer created, or <code>null</code> if has happened any problem
	 * 
	 * @throws Exception if fails getting the layer
	 * 
	 * @see LayerFactory#createLayer(String, VectorialFileDriver, File, IProjection)
	 */
	public static FLyrVect getShapeLayer(File path, String name,
			CoordinateReferenceSystem crs) throws Exception {
		FLyrVect layer = null;

		/* Gets the shape driver */
		Driver driver = LayerFactory.getDM().getDriver(SHAPE_DRIVER_ID);

		/* Creates the layer */
		layer = (FLyrVect) LayerFactory.createLayer(name, (VectorialFileDriver) driver, path, crs);

		/* Returns the layer got */
		return layer;
	}
}
