package com.iver.cit.gvsig.fmap.drivers;

import java.net.URL;

import org.gvsig.remoteClient.wfs.WFSStatus;
import org.gvsig.remoteClient.wfs.exceptions.WFSException;
import org.gvsig.remoteClient.wms.ICancellable;

import com.hardcode.gdbms.engine.data.driver.DriverException;

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
/* CVS MESSAGES:
 *
 * $Id: WFSDriver.java 18381 2008-01-30 12:00:25Z jpiera $
 * $Log$
 * Revision 1.3  2006-05-19 12:47:41  jorpiell
 * Se han añadido algunos métodos a esta interfaz
 *
 * Revision 1.2  2006/04/20 16:38:24  jorpiell
 * Ahora mismo ya se puede hacer un getCapabilities y un getDescribeType de la capa seleccionada para ver los atributos a dibujar. Queda implementar el panel de opciones y hacer el getFeature().
 *
 * Revision 1.1  2006/04/19 12:50:16  jorpiell
 * Primer commit de la aplicación. Se puede hacer un getCapabilities y ver el mensaje de vienvenida del servidor
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public interface WFSDriver extends VectorialDriver{
	/**
	 * Obtiene las posibilidades del servidor a partir de una URL.
	 *
	 * @param 
	 * server URL.
	 * @throws WFSDriverException 
	 */
	public void getCapabilities(URL server) throws WFSException;
	
	/**
	 * Obtiene la informacion asociada a una feature particular.
	 * Esta información puede ser un conjunto de campos o un
	 * esquema que los contiene. No devuleve nada. Simplemente
	 * actualiza la lista de features.
	 * @param featureType
	 * Nombre de la feature a a buscar
	 * @throws WFSDriverException
	 */
	public void describeFeatureType(String featureType, String nameSpace, ICancellable cancel)throws WFSException;
	
	/**
	 * The getFeature operation allows retrieval of features
	 * from a web feature service. 
	 * @param wfsStatus
	 * WFS client status
	 * @return File
	 * GML File
	 * @throws WFSDriverException
	 */
	public void getFeature(WFSStatus wfsStatus) throws WFSException;
	
	public void close();
	
    public void open() throws DriverException; 
 }
