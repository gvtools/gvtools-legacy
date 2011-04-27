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
package com.iver.cit.gvsig.fmap.layers;

import java.awt.geom.Rectangle2D;

import org.cresques.cts.IProjection;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.spatialindex.ISpatialIndex;


/**
 * Interfaz usada internamente para acceder a fuentes de datos vectoriales en
 * modo lectura
 */
public interface ReadableVectorial {
	/**
	 * Indica que se va a comenzar a hacer una serie de operaciones sobre el
	 * origen de datos con la finalidad de que dicho origen de datos se
	 * prepare (abra el fichero, comience una transacción, conecte al
	 * servidor, ...). Abre el fichero de índices en caso de que exista.
	 * @throws ReadDriverException TODO
	 * @throws OpenDriverException
	 * @throws InitializeDriverException
	 */
	void start() throws ReadDriverException, InitializeDriverException;

	/**
	 * Hace que se cierre el soporte físico de la capa. Cierra el fichero de
	 * índices en caso de que exista.
	 * @throws ReadDriverException TODO
	 */
	void stop() throws ReadDriverException;

	/**
	 * Devuelve la geometría a partir de un índice.
	 *
	 * @param index índice.
	 *
	 * @return Geometría.
	 * @throws ReadDriverException TODO
	 * @throws ExpansionFileReadException
	 */
	IGeometry getShape(int index) throws ReadDriverException, ExpansionFileReadException;

	/**
	 * Devuelve el número de Shape.
	 *
	 * @return Número de Shape.
	 * @throws ReadDriverException TODO
	 */
	int getShapeCount() throws ReadDriverException;

	/**
	 * Devuelve la extensión total de la capa.
	 *
	 * @return Extensión total.
	 * @throws ReadDriverException TODO
	 * @throws ExpansionFileReadException
	 */
	Rectangle2D getFullExtent() throws ReadDriverException, ExpansionFileReadException;

	/**
	 * Obtiene el tipo de las geometrías almacenadas en esta fuente de datos
	 *
	 * @return Obtiene el tipo de la capa. Es un bit-oring de los tipos
	 * 		   definidos en FShape POINT, LINE, POLYGON o TEXT;
	 * @throws ReadDriverException TODO
	 */
	int getShapeType() throws ReadDriverException;
	/**
	 * Obtiene una referencia al objeto que implementa la interfaz vectorial
	 * con el fin de que las Strategy puedan optimizar en función del driver.
	 *
	 * @return VectorialDriver
	 */
	public VectorialDriver getDriver();
	/**
	 * Establece el driver sobre el que actúa el adaptador
	 *
	 * @param driver
	 */
	public void setDriver(VectorialDriver driver);
	 /*
     * (non-Javadoc)
     *
     * @see com.iver.cit.gvsig.fmap.layers.VectorialAdapter#getRecordset()
     */
    public SelectableDataSource getRecordset() throws ReadDriverException;
    public DriverAttributes getDriverAttributes();

    /**
     * En la implementación por defecto podemos hacer que cada
     * feature tenga ID = numero de registro.
     * En el DBAdapter podríamos "overrride" este método y poner
     * como ID de la Feature el campo único escogido en
     * la base de datos. Básicamente es por comodidad.
     * @param numReg
     * @return
     * @throws ReadDriverException TODO
     * @throws ExpansionFileReadException
     */
    public IFeature getFeature(int numReg) throws ReadDriverException, ExpansionFileReadException;

    /* public IFeatureIterator getFeatureIterator(String strEPSG) throws DriverException;
    public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG) throws DriverException; */

	//void setRecordset(SelectableDataSource sds);



    /**
     * Returns an iterator over all the features of the vectorial data source.
     * It applies a relational algebra "projection" operator (selects the specified fields).
     *
     * It the param newProjection is not null, it reprojects all the returned features
     * to this projection
     *
     * @param fields fields we are intested in of the data source
     * @return feature iterator whose features has as attributes the specified fields
     */
    public IFeatureIterator getFeatureIterator(String[] fields, IProjection newProjection)
    				throws ReadDriverException;

    /**
     * Return a feature iterator from a given sql statement.
     * <br>
     * In this case, the statement will have the "projection" operator
     * (select campo1, campo2, ...etc) and the "selection" operator (where ....)
     *
     * @param sql statement which define a filter. SQL sintax is very extrict, and must
     *  be suported by GDBMS
     *
     * @param newProjection new projection for the returned features (if not null)
     * @return feature iterator
     * */
    public IFeatureIterator getFeatureIterator(String sql, IProjection newProjection) throws ReadDriverException;

    public IFeatureIterator getFeatureIterator(String sql, IProjection newProjection, boolean withSelection) throws ReadDriverException;

    /**
     * Makes an spatial query returning a feature iterator over the features which intersects
     * or are contained in the rectangle query. Applies a restriction to the alphanumeric fields
     * returned by the iterator, and a reprojection.
     * <br>
     * Details:
     * <ul>
     * <li>
     * If newProjection is null, rect Rectangle2D is consideered in the same projection that the source data.
     * </li>
     * <li>
     * If newProjection is not null, Rectangle2D is consideered in this projection (newProjection).
     * So the source driver must reproject this rectangle before to make the query, and after that
     * reproject all the returned features' geometry
     * </li>
     *
     * </ul>
     * @param rect
     * @param fields
     * @return
     */
    public IFeatureIterator getFeatureIterator(Rectangle2D rect, String[] fields,
    			IProjection newProjection, boolean fastResult) throws ReadDriverException;


    public IFeatureIterator getFeatureIterator() throws ReadDriverException;
    /**
     * Return the spatial index of the adapter.
     * Responsability of create spatial index instances is of the FLyrVect class, but
     * adapters need these index to build spatial query iterators.
     * */
    public ISpatialIndex getSpatialIndex();

    /**
     * Sets spatial index of the data source behind the adapter
     *
     * @param spatialIndex
     */
    public void setSpatialIndex(ISpatialIndex spatialIndex);

    /**
     * Sets the projection of the data readed by the associated driver
     * @param projection
     */
    public void setProjection(IProjection projection);

    public IProjection getProjection();
}
