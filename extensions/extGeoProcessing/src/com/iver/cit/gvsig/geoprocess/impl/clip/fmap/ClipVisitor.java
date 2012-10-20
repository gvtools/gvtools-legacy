/*
 * Created on 16-feb-2006
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
 * $Id: ClipVisitor.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.3  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.2  2006/07/21 09:10:34  azabala
 * fixed bug 608: user doesnt enter any result file to the geoprocess panel
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.4  2006/06/08 18:20:23  azabala
 * optimizaciones: se usa consulta espacial para solo chequear los elementos de la primera capa que intersecten con la capa de recorte
 *
 * Revision 1.3  2006/06/02 18:21:28  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/05/31 09:10:12  fjp
 * Ubicación de IWriter
 *
 * Revision 1.1  2006/05/24 21:14:07  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.6  2006/03/28 16:27:36  azabala
 * *** empty log message ***
 *
 * Revision 1.5  2006/03/17 19:52:43  azabala
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/15 18:30:39  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/14 18:32:46  fjp
 * Cambio con LayerDefinition para que sea compatible con la definición de tablas también.
 *
 * Revision 1.2  2006/03/07 21:01:33  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/06 19:48:39  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/19 20:56:21  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/17 16:33:05  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.clip.fmap;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.ISchemaManager;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor;
import com.iver.cit.gvsig.geoprocess.core.util.GeometryUtil;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This class analyzes all features of a layer, clipping its geometries with the
 * convex hull of another layer. If the geometry of the feature analyzed doesnt
 * intersect with the convex hull geometry, the clipvisitor will ignore it.
 * 
 * It intersects, computes intersection and creates a new feature with the same
 * attributes and the new intersection geometry.
 * 
 * @author azabala
 * 
 */
public class ClipVisitor implements FeatureVisitor {

	/**
	 * Clipping geometry: the convex hull of the clipping layer
	 */
	private Geometry clippingConvexHull;
	/**
	 * It allows visitor to get attribute values link to geometries
	 */
	private SelectableDataSource recordset;
	/**
	 * Processes individual clip results
	 */
	private FeaturePersisterProcessor resultProcessor;

	/**
	 * If its different of null manages user selection (discarting any feature
	 * else)
	 */
	private FBitSet selection;

	/**
	 * Constructor. It requires clipping geometry.
	 * 
	 * @param clippingGeometry
	 */
	public ClipVisitor(Geometry clippingGeometry,
			ITableDefinition layerDefinition, ISchemaManager schemaManager,
			IWriter writer) {

		this.clippingConvexHull = clippingGeometry;
		try {
			this.resultProcessor = new FeaturePersisterProcessor(
					layerDefinition, schemaManager, writer);
		} catch (SchemaEditionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setSelection(FBitSet selection) {
		this.selection = selection;
	}

	/**
	 * clips feature's geometry with the clipping geometry, preserving feature's
	 * original attributes. If feature's geometry doesnt touch clipping
	 * geometry, it will be ignored.
	 */
	public void visit(IGeometry g, int index) throws VisitorException,
			ProcessVisitorException {

		// TODO METER EL PRINCIPIO Y EL FINAL EN EL VISITOR ABSTRACTO
		// ASÍ, TENDRIAMOS LA GESTIÓN TOPOLÓGICA EN CADA VISITOR

		if (g == null)
			return;
		if (selection != null) {
			if (!selection.get(index))
				return;
		}
		Geometry jtsGeom = g.toJTSGeometry();
		if (!jtsGeom.getEnvelope().intersects(clippingConvexHull.getEnvelope()))
			return;
		if (jtsGeom.intersects(clippingConvexHull)) {
			try {
				Geometry newGeom = jtsGeom.intersection(clippingConvexHull);
				resultProcessor.processJtsGeometry(newGeom, index);
			} catch (com.vividsolutions.jts.geom.TopologyException e) {
				e.printStackTrace();
				System.out.println(jtsGeom.toText());
				System.out.println(clippingConvexHull.toText());
				/*
				 * TODO En este caso, la de recorte chequearla al principio
				 * (cuando se calcula) y solo chequearemos las de entrada
				 * conforme nos van viniendo.
				 * 
				 * HAY QUE CREAR UN FRAMEWORK DE CORRECCIÓN TOPOLÓGICA DE
				 * GEOMETRIAS: -Mirando autointersecciones. -Mirando duplicados.
				 * -Eliminando colineales. ETC.
				 */
				if (!jtsGeom.isValid()) {
					System.out.println("La geometria de entrada no es valida");
					jtsGeom = GeometryUtil.removeDuplicatesFrom(jtsGeom);
				}
				if (!clippingConvexHull.isValid()) {
					System.out.println("La geometria de recorte no es valida");
					clippingConvexHull = GeometryUtil
							.removeDuplicatesFrom(clippingConvexHull);
				}
				try {
					Geometry newGeom = jtsGeom.intersection(clippingConvexHull);
					resultProcessor.processJtsGeometry(newGeom, index);
				} catch (com.vividsolutions.jts.geom.TopologyException ee) {
					ee.printStackTrace();
				} catch (ReadDriverException ee) {
					// TODO Auto-generated catch block
					ee.printStackTrace();
				}
				;
			} catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// if
	}

	public void stop(FLayer layer) throws VisitorException {
		resultProcessor.finish();
	}

	public boolean start(FLayer layer) throws StartVisitorException {
		if (layer instanceof AlphanumericData && layer instanceof VectorialData) {
			try {
				this.recordset = ((AlphanumericData) layer).getRecordset();
				this.resultProcessor.setSelectableDataSource(recordset);
			} catch (ReadDriverException e) {
				return false;
			}
			return true;
		}
		return false;
	}

	public SelectableDataSource getRecordset() {
		return recordset;
	}

	public void setRecordset(SelectableDataSource recordset) {
		this.recordset = recordset;
	}

	public String getProcessDescription() {
		return "Clipping features agaisnt a clip geometry";
	}
}
