package com.iver.cit.gvsig.fmap.drivers.wfs;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.gvsig.fmap.drivers.gpe.reader.GMLVectorialDriver;
import org.gvsig.remoteClient.gml.schemas.XMLElement;
import org.gvsig.remoteClient.wfs.WFSClient;
import org.gvsig.remoteClient.wfs.WFSFeature;
import org.gvsig.remoteClient.wfs.WFSOperation;
import org.gvsig.remoteClient.wfs.WFSServiceInformation;
import org.gvsig.remoteClient.wfs.WFSStatus;
import org.gvsig.remoteClient.wfs.exceptions.WFSException;
import org.gvsig.remoteClient.wms.ICancellable;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.BoundedShapes;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.WFSDriver;
import com.iver.cit.gvsig.fmap.drivers.WFSDriverException;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.wfs.FmapWFSWriter;
import com.iver.cit.gvsig.fmap.edition.wfs.GMLEditionUtils;
import com.iver.cit.gvsig.fmap.edition.wfs.WFSTLockFeaturesException;
import com.iver.cit.gvsig.fmap.layers.WFSLayerNode;
import com.iver.utiles.StringComparator;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

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
 * $Id: FMapWFSDriver.java 18428 2008-02-04 09:46:20Z jpiera $
 * $Log$
 * Revision 1.18.2.5  2006-11-17 11:28:45  ppiqueras
 * Corregidos bugs y aÃ±adida nueva funcionalidad.
 *
 * Revision 1.22  2006/11/16 13:31:10  jorpiell
 * Se ha eliminado la visivilidad del constructor
 *
 * Revision 1.21  2006/11/15 17:38:08  jorpiell
 * Ya se puede guardar una capa en WFS.
 *
 * Revision 1.20  2006/09/25 10:23:03  caballero
 * no multiType
 *
 * Revision 1.19  2006/09/18 12:07:31  jorpiell
 * Se ha sustituido geotools por el driver de remoteservices
 *
 * Revision 1.18  2006/07/24 08:27:35  jorpiell
 * Implementado el método getFieldType que permite la exportación a los diferentes tipos de ficheros (como shp,....)
 *
 * Revision 1.17  2006/07/21 10:02:43  caballero
 * solución provisional a WFS
 *
 * Revision 1.16  2006/07/12 11:55:22  jmvivo
 * *** empty log message ***
 *
 * Revision 1.15  2006/07/12 10:58:10  jmvivo
 * Añadido el metodo 'reload' del ObjectDriver (falta implementar)
 *
 * Revision 1.14  2006/07/11 11:55:41  jorpiell
 * Se ha añadido el fallo de tipo de dato en el log
 *
 * Revision 1.13  2006/06/15 10:34:12  jorpiell
 * Ya se pueden ver los atributos de las tablas
 *
 * Revision 1.12  2006/06/15 07:50:58  jorpiell
 * Añadida la funcionalidad de reproyectar y hechos algunos cambios en la interfaz
 *
 * Revision 1.11  2006/06/14 08:46:24  jorpiell
 * Se tiene en cuanta la opcion para refrescar las capabilities
 *
 * Revision 1.10  2006/06/14 07:57:19  jorpiell
 * Ya no se usa la estrategia ni se usa geotools para hacer el getFeature. Ahora se usa únicamente para el parseo de GML
 *
 * Revision 1.9  2006/06/05 16:49:42  caballero
 * poder editar y exportar
 *
 * Revision 1.8  2006/05/30 05:26:38  jorpiell
 * isWritable ha sido añadido a la interfaz, y hay que implementarlo
 *
 * Revision 1.7  2006/05/25 10:32:11  jorpiell
 * Se ha renombrado la clase WFSFields por WFSAttributes porque era algo confusa
 *
 * Revision 1.6  2006/05/23 13:21:28  jorpiell
 * Se tiene en cuanta el online resource
 *
 * Revision 1.5  2006/05/22 10:31:35  jorpiell
 * Cambio producido al cambiar una interfaz del geotools driver
 *
 * Revision 1.4  2006/05/22 10:11:10  jorpiell
 * Eliminadas algunas lineas innecesarias
 *
 * Revision 1.3  2006/05/19 12:53:54  jorpiell
 * Se le ha añadido un driver de geottols, por lo que tiene dos drivers. Para hacer el getFeatureInfo y el capablities utilizará elremoteServices y para hacer el getFeature usará el de geotools.
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
public class FMapWFSDriver implements WFSDriver, BoundedShapes,ObjectDriver,IWriteable{
	private WFSClient remoteServicesClient;
	private WFSLayerNode[] featuresList;
	private Hashtable hashFeatures;
	private DriverAttributes attr = new DriverAttributes();
	private GMLVectorialDriver driver = null;
	private boolean hasGeometry = false;
	//The GML driver to use
	private static Class gmlDriverClass = null;   
	//WFSTDriver
	private FmapWFSWriter writer = null;
	private WFSStatus status = null;
	private boolean isWfstEditing = false;	
	//The shape type
	private int shapeType = -1;

	FMapWFSDriver() {
		super();		
	}

	//Method used to register the GML parser
	public static void registerGmlDriver(Class clazz){
		gmlDriverClass = clazz;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.WFSDriver#getCapabilities(java.net.URL)
	 */
	public void getCapabilities(URL server)
	throws WFSDriverException {
		try {
			getClient(server).connect(null);
		} catch (Exception e) {
			throw new WFSDriverException(e);
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.WFSDriver#describeFeatureType(java.lang.String)
	 */
	public void describeFeatureType(String featureType, String nameSpace, ICancellable cancel) throws WFSException {
		status = new WFSStatus(featureType,nameSpace);
		remoteServicesClient.describeFeatureType(status, false, cancel);
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.WFSDriver#getFeature(org.gvsig.remoteClient.wfs.WFSStatus)
	 */
	public void getFeature(WFSStatus wfsStatus) throws WFSException {
		//Retrieve the file
		File file = remoteServicesClient.getFeature(wfsStatus, false, null);
		driver = new GMLVectorialDriver();
		//Reads the GML file
		try {			
			driver.open(file);
			driver.initialize();	
			hasGeometry = WFSUtils.getHasGeometry(featuresList,wfsStatus);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).error(e.getMessage());
			throw new WFSDriverException(e);
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShapeType()
	 */
	public int getShapeType() {
		if (shapeType == -1){
			XMLElement element = GMLEditionUtils.getGeometry(status, 
					remoteServicesClient);
			if (element != null){
				String gmlType = element.getEntityType().getName();
				if (gmlType.toLowerCase().indexOf("point") > 0){
					shapeType = FShape.POINT;
				}else if (gmlType.toLowerCase().indexOf("linestring") > 0){
					shapeType = FShape.LINE;
				}else if (gmlType.toLowerCase().indexOf("polygon") > 0){
					shapeType = FShape.POLYGON;
				}
			}
			if (shapeType == -1){
				if (getShapeCount() > 0){
					shapeType = getShapeType(0);
				}else{
					shapeType = FShape.MULTI;
				}						
			}
		}
		return shapeType;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShapeCount()
	 */
	public int getShapeCount() {
		return driver.getShapeCount();
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getDriverAttributes()
	 */
	public DriverAttributes getDriverAttributes() {
		return attr;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getFullExtent()
	 */
	public Rectangle2D getFullExtent() {
		return driver.getFullExtent();
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShape(int)
	 */
	public IGeometry getShape(int index) {
		return driver.getShape(index);		
	}

	/*
	 *  (non-Javadoc)
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return "WFS Driver";
	}



	/**
	 * Devuelve WFSClient a partir de su URL.
	 *
	 * @param url URL.
	 *
	 * @return WMSClient.
	 * @throws IOException
	 * @throws ConnectException
	 *
	 * @throws UnsupportedVersionException
	 * @throws IOException
	 */
	public WFSClient getClient(URL url) throws ConnectException, IOException {
		if (remoteServicesClient == null) {
			remoteServicesClient = new WFSClient(url.toString());
		}
		return remoteServicesClient;
	}

	/**
	 * Creates a new instance of a WFSClient.
	 * @param
	 * host
	 * @throws IOException
	 * @throws ConnectException
	 */
	public void createClient(URL host) throws ConnectException, IOException {
		remoteServicesClient = new WFSClient(host.toString());
	}

	/**
	 * Establishes the connection to the WfS server. Connecting to a WFS is
	 * an abstraction.<br>
	 * <p>
	 * Actually, it sends a GetCapabilities request to read the necessary
	 * data for building further DescribeFeatureType requests.
	 * </p>
	 * @throws IOException, DriverException.
	 */
	public boolean connect(boolean override,ICancellable cancel) throws IOException, DriverException {
		return remoteServicesClient.connect(override,cancel);
	}

	public boolean connect(ICancellable cancel) {
		return remoteServicesClient.connect(false, cancel);
	}

	/**
	 * Returns an array of WFSLayerNode's with the descriptors of
	 * all features (retrieved using the getCapabilities operation)
	 * @return WFSLayerNode[]
	 */
	public WFSLayerNode[] getLayerList(){
		if (hashFeatures == null) {
			hashFeatures = new Hashtable();
			Hashtable wfsFeatures  = remoteServicesClient.getFeatures();

			StringComparator stringComparator = new StringComparator();
			// Set spanish rules and with case sensitive
			Collator collator = Collator.getInstance(new Locale("es_ES"));		
			stringComparator.setLocaleRules(stringComparator.new LocaleRules(true, collator));
			stringComparator.setCaseSensitive(false);

			ArrayList keysList = new ArrayList(wfsFeatures.keySet());
			Collections.sort(keysList, stringComparator);

			Iterator keys = keysList.iterator();
			featuresList = new WFSLayerNode[wfsFeatures.size()];

			for (int i=0 ; i<wfsFeatures.size() ; i++){
				WFSLayerNode lyr = new WFSLayerNode();
				WFSFeature feature = (WFSFeature)wfsFeatures.get(keys.next());

				lyr.setName(feature.getName());
				lyr.setTitle(feature.getTitle());
				lyr.setAbstract(feature.getAbstract());
				lyr.setFields(feature.getFields());
				lyr.setSrs(feature.getSrs());
				lyr.setLatLonBbox(feature.getLatLonBbox());

				featuresList[i] = lyr;
				hashFeatures.put(lyr.getName(), lyr);
			}
		}
		return featuresList;
	}

	/**
	 * Returns all the feature information retrieved using a
	 * describeFeatureTypeOpearion
	 * @param layerName
	 * Feature name
	 * @return
	 */
	public WFSLayerNode getLayerInfo(String layerName,String nameSpace){
		WFSLayerNode lyr = (WFSLayerNode)hashFeatures.get(layerName);
		try {
			describeFeatureType(layerName, nameSpace, null);
			WFSFeature feature = (WFSFeature) remoteServicesClient.getFeatures().get(layerName);
			lyr.setFields(feature.getFields());
		} catch (WFSException e) {
			// The feature doesn't has fields
			e.printStackTrace();
		}		
		return lyr;		
	}

	/**
	 * Returns all the feature information retrieved using a
	 * describeFeatureTypeOpearion
	 * @param layerName
	 * Feature name
	 * @return
	 */
	public WFSLayerNode getLayerInfo(String layerName){
		return getLayerInfo(layerName, null);
	}


	/**
	 * @return The title of the service offered by the WMS server.
	 */
	public String getServiceTitle() {
		return remoteServicesClient.getServiceInformation().title;
	}

	/**
	 * @return The abstract of the service offered by the WMS server.
	 */
	public String getServiceAbstract() {
		return remoteServicesClient.getServiceInformation().abstr;
	}

	/**
	 * @return the online resource
	 */
	public String getOnlineResource(){
		WFSServiceInformation si = remoteServicesClient.getServiceInformation();
		return si.getOnline_resource();
	}

	/**
	 *
	 * @return the host
	 */
	public String getHost(){
		return remoteServicesClient.getHost();
	}

	/**
	 * @return the version of this client.
	 */
	public String getVersion() {
		return remoteServicesClient.getVersion();
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.WFSDriver#close()
	 */
	public void close() {
		// TODO Auto-generated method stub

	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.WFSDriver#open()
	 */
	public void open() throws DriverException {
		attr.setLoadedInMemory(true);

	}

	/*
	 *  (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() {
		throw new UnsupportedOperationException();
	}

	/*
	 *  (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#write(com.hardcode.gdbms.engine.data.edition.DataWare)
	 */
	public void write(DataWare dataWare) {

	}

	/*
	 *  (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.driver.GDBMSDriver#setDataSourceFactory(com.hardcode.gdbms.engine.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		// TODO Auto-generated method stub

	}

	/*
	 *  (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId) {
		return driver.getFieldValue(rowIndex, fieldId);
	}

	/*
	 *  (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() {
		return driver.getFieldCount();
	}

	/*
	 *  (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) {
		return driver.getFieldName(fieldId);
	}

	/*
	 *  (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() {
		return driver.getRowCount();
	}

	/*
	 *  (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
	 */
	public int getFieldType(int i) {
		return driver.getFieldType(i);
	}

	/*
	 *  (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldWidth(int)
	 */
	public int getFieldWidth(int i) {
		// TODO Auto-generated method stub
		return 100;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#isWritable()
	 */
	public boolean isWritable() {
		return false;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.BoundedShapes#getShapeBounds(int)
	 */
	public Rectangle2D getShapeBounds(int index) {
		return getShape(index).getBounds2D();
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.BoundedShapes#getShapeType(int)
	 */
	public int getShapeType(int index) {
		if (getShapeCount() > 0){
			return getShape(0).getGeometryType();
		}
		return FShape.MULTI;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#reload()
	 */
	public void reload() {
		try {
			getFeature(status);
		} catch (WFSException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.IWriteable#getWriter()
	 */
	public IWriter getWriter() {
		if (writer == null){
			writer = new FmapWFSWriter(this, isWfstEditing);
		}
		return writer;
	}

	/**
	 * @return the status
	 */
	public WFSStatus getStatus() {
		return status;
	}

	/**
	 * @return the isWfstEditing
	 */
	public boolean isWfstEditing() {
		return isWfstEditing;
	}

	/**
	 * @param isWfstEditing the isWfstEditing to set
	 */
	public void setWfstEditing(boolean isWfstEditing) {
		this.isWfstEditing = isWfstEditing;
		if (writer != null){
			writer.setWfstEditing(isWfstEditing);
		}
	}

	/**
	 * It locks all the features
	 * @param expiryTime
	 * The maximum time to edit
	 * @throws WFSTLockFeaturesException 
	 */
	public void lockCurrentFeatures(int expiryTime) throws WFSTLockFeaturesException{
		((FmapWFSWriter)getWriter()).lockCurrentFeatures(expiryTime);
	}

	/**
	 * @return the remoteServicesClient
	 */
	public WFSClient getRemoteServicesClient() {
		return remoteServicesClient;
	}

	/**
	 * @return true if the server supports WFST
	 */
	public boolean isTransactional(){
		String onlineResource = remoteServicesClient.getServiceInformation().getOnlineResource(WFSOperation.TRANSACTION);
		if (onlineResource == null){
			onlineResource = remoteServicesClient.getServiceInformation().getOnlineResource(WFSOperation.TRANSACTION, WFSOperation.PROTOCOL_POST);
			if (onlineResource == null){
				return false;
			}
		}
		return true;
	}
}
