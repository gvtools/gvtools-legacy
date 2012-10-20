/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */

/**
 *
 */
package org.gvsig.remoteClient.arcims.utils;

/**
 * 
 * Class containing a description for TAGS defined in the GET_FEATURES request
 * returned from a ArcIMS Server
 * 
 * @author jsanz
 */
public class GetFeaturesTags {
	public final static int MAX_ROWS_PER_REQUEST = 1000;
	public final static int MIN_ROWS_PER_REQUEST = 700;
	public final static int ACCURACY_RATIO = 1;
	public final static String FIELD = "FIELD";
	public final static String FIELDS = "FIELDS";
	public final static String FEATURES = "FEATURES";
	public final static String FEATURE = "FEATURE";
	public final static String FEATURECOUNT = "FEATURECOUNT";
	public final static String POLYGON = "POLYGON";
	public final static String RING = "RING";
	public final static String COORDS = "COORDS";
	public final static String POLYLINE = "POLYLINE";
	public final static String PATH = "PATH";
	public final static String MULTIPOINT = "MULTIPOINT";
	public final static String NAME = "name";
	public final static String VALUE = "value";
	public final static String COUNT = "count";
	public final static String HASMORE = "hasmore";
}
