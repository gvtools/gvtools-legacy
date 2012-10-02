package com.iver.cit.gvsig.fmap.layers;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.remoteClient.gml.schemas.XMLElement;
import org.gvsig.remoteClient.gml.types.IXMLType;
import org.gvsig.remoteClient.gml.types.XMLComplexType;
import org.gvsig.remoteClient.wfs.WFSStatus;
import org.gvsig.remoteClient.wfs.filters.FilterEncoding;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.drivers.WFSDriverException;
import com.iver.cit.gvsig.fmap.drivers.wfs.FMapWFSDriver;
import com.iver.cit.gvsig.fmap.drivers.wfs.FMapWFSDriverFactory;
import com.iver.cit.gvsig.fmap.drivers.wfs.filters.SQLExpressionFormat;
import com.iver.cit.gvsig.fmap.edition.wfs.WFSTLockFeaturesException;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

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
 * $Id: FLyrWFS.java 18381 2008-01-30 12:00:25Z jpiera $
 * $Log$
 * Revision 1.10.2.14  2007-06-29 10:55:12  jorpiell
 * Encoding problem fixed
 *
 * Revision 1.10.2.12  2007/01/25 16:11:15  jorpiell
 * Se han cambiado los imports que hacían referencia a remoteServices. Esto es así, porque se han renombrado las clases del driver de GML
 *
 * Revision 1.10.2.11  2007/01/08 09:51:39  ppiqueras
 * Corregidos bugs + nueva funcionalidad, como que almacene informaciÃ³n de los campos y valores que posee.
 *
 * Revision 1.27  2006/12/26 10:25:37  ppiqueras
 * Corregidas las dependencias con las nuevas ubicaciones de clases: IXMLType, XMLElement, IXMLComplexType, etc. (en libRemoteServices)
 *
 * Revision 1.26  2006/12/26 09:28:01  ppiqueras
 * Cambiado "atttibutes" en todas las aparaciones en atributos, mÃ©todos, clases, paquetes o comentarios por "fields". (SÃ³lo a aquellas que afectan a clases dentro del proyecto extWFS2).
 *
 * Revision 1.24  2006/12/15 13:55:17  ppiqueras
 * Almacena todos los campos y valores conocidos de la capa.
 *
 * Revision 1.23  2006/12/11 11:02:24  ppiqueras
 * Corregido bug -> que se mantenga la frase de filtrado
 *
 * Revision 1.22  2006/11/28 08:04:51  jorpiell
 * Se guarda la query para que pueda ser recuperada desde la ventana de propiedades
 *
 * Revision 1.21  2006/11/16 17:17:48  jorpiell
 * Se guarda el filtro en el gvp
 *
 * Revision 1.20  2006/11/16 13:29:47  jorpiell
 * Se ha reescrito losl método para  guardar y recuperar gvps
 *
 * Revision 1.19  2006/11/15 17:38:08  jorpiell
 * Ya se puede guardar una capa en WFS.
 *
 * Revision 1.18  2006/10/31 12:24:04  jorpiell
 * Comprobado el caso en el que los atributos no tienen tipo
 *
 * Revision 1.17  2006/10/31 09:55:28  jorpiell
 * Se ha modificado el constructor del WFS desde catálogo
 *
 * Revision 1.16  2006/10/31 09:38:15  jorpiell
 * Se ha creado una factoria para crear la capa. De ese modo no se repite código desde le panel de propiedades y desde el panel de la capa
 *
 * Revision 1.15  2006/10/23 07:37:04  jorpiell
 * Ya funciona el filterEncoding
 *
 * Revision 1.14  2006/10/10 12:55:06  jorpiell
 * Se ha añadido el soporte de features complejas
 *
 * Revision 1.13  2006/10/02 12:54:35  jorpiell
 * No se podía crear un mapa que tubiera la opción enlace vivo habilitada
 *
 * Revision 1.12  2006/10/02 09:09:45  jorpiell
 * Cambios del 10 copiados al head
 *
 * Revision 1.10.2.3  2006/09/29 14:12:53  luisw2
 * CRSFactory.getCRS substitutes ProjectionPool.get
 *
 * Revision 1.10.2.2  2006/09/28 08:54:01  jorpiell
 * Ya se puede reproyectar
 *
 * Revision 1.10.2.1  2006/09/26 07:36:24  jorpiell
 * El WFS no reproyectaba porque no se le asignaba a la capa un sistema de referencia. Ahora ya se hace.
 *
 * Revision 1.10  2006/09/05 15:41:52  jorpiell
 * Añadida la funcionalidad de cargar WFS desde el catálogo
 *
 * Revision 1.9  2006/07/05 12:05:41  jorpiell
 * Se ha modificado para que avise si se han recuperado las mismas features que marca el campo buffer
 *
 * Revision 1.8  2006/06/21 12:52:10  jorpiell
 * Añadido un icono para la capa WFS
 *
 * Revision 1.7  2006/06/21 12:35:45  jorpiell
 * Se ha añadido la ventana de propiedades. Esto implica añadir listeners por todos los paneles. Además no se muestra la geomatría en la lista de atributos y se muestran únicamnete los que se van a descargar
 *
 * Revision 1.6  2006/06/15 11:17:06  jorpiell
 * Se ha encontrado la forma de comprobar cuando se prodicia un error de parseo al hacer un hasnext (en la feature). Se atrapa y se lanza la excepción hacia arriba
 *
 * Revision 1.5  2006/06/14 07:57:19  jorpiell
 * Ya no se usa la estrategia ni se usa geotools para hacer el getFeature. Ahora se usa únicamente para el parseo de GML
 *
 * Revision 1.4  2006/06/05 16:49:31  caballero
 * poder editar y exportar
 *
 * Revision 1.3  2006/05/25 10:31:55  jorpiell
 * Se ha renombrado la clase WFSFields por WFSAttributes porque era algo confusa
 *
 * Revision 1.2  2006/05/23 13:21:59  jorpiell
 * Si hay algún problema en la carga se muestra un mensaje de error
 *
 * Revision 1.1  2006/05/19 12:54:11  jorpiell
 * Creada la capa WFS
 *
 *
 */
/**
 *  FMap's WFS Layer class.
 *
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class FLyrWFS extends FLyrVect{
	/**
	 * @param visualFilterQuery the visualFilterQuery to set
	 */
	public void setVisualFilterQuery(String visualFilterQuery) {
		this.visualFilterQuery = visualFilterQuery;
	}

	/**
	 * @return the bbox
	 */
	public Rectangle2D getBbox() {
		return bbox;
	}

	/**
	 * @param bbox the bbox to set
	 */
	public void setBbox(Rectangle2D bbox) {
		this.bbox = bbox;
	}

	/**
	 * @return the bboxPropertyName
	 */
	public String getBboxPropertyName() {
		return bboxPropertyName;
	}

	/**
	 * @param bboxPropertyName the bboxPropertyName to set
	 */
	public void setBboxPropertyName(String bboxPropertyName) {
		this.bboxPropertyName = bboxPropertyName;
	}

	private URL host = null;
	private String onlineResource = null;
	private String name = null;
	private String layerName = null;
	private String nameSpace = null;
	private String userName = null;
	private String password = null;
	private String FieldsQuery = null;
	private Rectangle2D bbox = null;
	private String bboxPropertyName = null;
	private String visualFilterQuery = null;
	private XMLElement[] fields = null;
	private int numfeatures = 100;
	private int timeout = 10000;
	private int wfstExpiryTime = 0;
	private String srs = null;
	private FMapWFSDriver wfsDriver = null;
	private WFSLayerNode wfsLayerNode = null;
	private Map allFieldsAndValuesKnown = null;
	private WFSStatus status = null;

	/**
	 * Constructor
	 */
	public FLyrWFS(){
		super();
		ExtensionPoint extensionPoint = (ExtensionPoint)ExtensionPointsSingleton.getInstance().get("WFSExtension");
		if (extensionPoint != null){
			try {
				WFSLayerListener listener = (WFSLayerListener)extensionPoint.create("WFSLayerListener");
				listener.setWfsLayer(this);
				addLayerListener(listener);
			} catch (Exception e) {
				//Impossible to Use the WFST lockoperation
			}
		}
	}

	/**
	 * Constructor to load a new layer from a catalog
	 * search
	 * @param args
	 * Params to load a new layer
	 * @throws WFSDriverException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws ConnectException 
	 * @throws DriverIOException 
	 */
	public FLyrWFS(Map args) throws WFSDriverException, ConnectException, MalformedURLException, IOException, DriverIOException{
		this();
		String[] sLayer = (String[])args.get("layer");
		String user = (String)args.get("user");
		String pwd = (String)args.get("pwd");
		String host = (String)args.get("host");	
		String projection = (String)args.get("projection");

		FMapWFSDriver driver = FMapWFSDriverFactory.getFMapDriverForURL(new URL(host));
		WFSLayerNode[] layers = driver.getLayerList();
		WFSLayerNode layer = driver.getLayerInfo(sLayer[0]);

		layer.setSelectedFields(layer.getFields());

		Vector vAtts = new Vector();
		if (layer.getFields().size() == 1){
			XMLElement element = (XMLElement)layer.getFields().get(0);
			if (element.getEntityType().getType() != IXMLType.COMPLEX){
				vAtts.add(element);
			}else{
				vAtts = ((XMLComplexType)element.getEntityType()).getAttributes();
			}
		}
		for (int i=0 ; i<layer.getFields().size() ; i++){
			XMLElement element = (XMLElement)layer.getFields().get(i);
			if (element.getEntityType().getType() != IXMLType.COMPLEX){
				vAtts.add((XMLElement)layer.getFields().get(i));
			}else{

			}
		}

		XMLElement[] atts = new XMLElement[vAtts.size()];
		for (int i=0 ; i<vAtts.size() ; i++){
			atts[i] = (XMLElement)vAtts.get(i);
		}

		setHost(host);
		setName(sLayer[0]);
		setLayerName(sLayer[0]);
		setWfsLayerNode(layer);
		setFields(atts);
		setUserName(user);
		setPassword(pwd);
		setNumfeatures(10000);
		setTimeout(10000);
		setWfsDriver(driver);			
		setCrs(ProjectionUtils.getCRS(projection));

		load();    	
	}


	/**
	 * Loads the features from the server
	 */
	public void load(){
		WFSAdapter adapter = new WFSAdapter();
		try {
			wfsDriver.getFeature(getWFSStatus());

			adapter.setDriver((VectorialDriver) wfsDriver);
			setSource(adapter);

			if (getLegend() == null){
				setLegend(LegendFactory.createSingleSymbolLegend(
						getShapeType()));
			}
		} catch (Exception e){
			e.printStackTrace();			
		}
	}

	/**
	 * Gets the WFS Status
	 * @return
	 */
	private WFSStatus getWFSStatus(){
		if (status == null){
			status = wfsDriver.getStatus();
		}else{
			status.setFeatureName(getLayerName());
		}
		status.setUserName(getUserName());
		status.setPassword(getPassword());
		status.setBuffer(getNumfeatures());
		status.setTimeout(getTimeout());
		status.setFields(getFieldNames());
		status.setOnlineResource(getOnlineResource());
		status.setSrs(getSrs());
		status.setBBox(getBbox());
		//Filter Encoding transformation
		FilterEncoding fe = SQLExpressionFormat.createFilter();				
		fe.setQuery(getFieldsQuery());
		status.setFilterQuery(fe.toString());
		status.setFilterVisualText(getVisualFilterQuery());
		return status;
	}

	/**
	 * @return Returns the layerName.
	 */
	public String getLayerName() {
		return layerName;
	}
	/**
	 * @param layerName The layerName to set.
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	/**
	 * @return Returns the numfeatures.
	 */
	public int getNumfeatures() {
		return numfeatures;
	}
	/**
	 * @param numfeatures The numfeatures to set.
	 */
	public void setNumfeatures(int numfeatures) {
		this.numfeatures = numfeatures;
	}

	/**
	 * @return Returns the pwd.
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param pwd The pwd to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return Returns the timeout.
	 */
	public int getTimeout() {
		return timeout;
	}
	/**
	 * @param timeout The timeout to set.
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	/**
	 * @return Returns the user.
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param user The user to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return The visual filtering text from "Where"
	 */
	public String getVisualFilterQuery() {
		return visualFilterQuery;
	}
	/**
	 * @param visualFilterQuery The visual filtering text from "Where"
	 */
	public void setVisualFilterQueyr(String visualFilterQuery) {
		this.visualFilterQuery = visualFilterQuery;
	}
	/**
	 * @return Returns the fields.
	 */
	public XMLElement[] getFields() {
		if (fields == null){
			return new XMLElement[0];
		}
		return fields;
	}

	/**
	 * Return the fields name
	 * @return
	 */
	public String[] getFieldNames(){
		Vector vFields = new Vector();
		for (int i=0 ; i<getFields().length ; i++){
			if ((getFields()[i].getEntityType() == null) || 
					(getFields()[i].getEntityType().getType() != IXMLType.COMPLEX)){
				vFields.add(getFields()[i].getName());
			}
		}
		String[] fields = new String[vFields.size()];
		for (int i=0 ; i<vFields.size() ; i++){
			fields[i] = (String)vFields.get(i);
		}
		return fields;
	}

	/**
	 * @param fields The fields to set.
	 */
	public void setFields(XMLElement[] fields) {
		this.fields = fields;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the host.
	 */
	public URL getHost() {
		return host;
	}
	/**
	 * @param host The host to set.
	 */
	public void setHost(URL host) {
		this.host = host;
	}

	/**
	 * @return Returns the wfs.
	 */
	public FMapWFSDriver getWfsDriver() {
		return wfsDriver;
	}

	/**
	 * @param wfs The wfs to set.
	 */
	public void setWfsDriver(FMapWFSDriver wfs) {
		this.wfsDriver = wfs;
	}


	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.FLyrDefault#getStrategy()
	 */
	//	public Strategy getStrategy() {
	//	if (wfsStrategy == null){
	//	wfsStrategy = new WFSStrategy(this);
	//	}
	//	return wfsStrategy;
	//	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.FLayer#getFullExtent()
	 */
	public Rectangle2D getFullExtent() {
		Rectangle2D extent = wfsDriver.getFullExtent();
		if (extent == null){
			return null;
		}
		return extent;
	}

	/**
	 * @return Returns the onlineResource.
	 */
	public String getOnlineResource() {
		return onlineResource;
	}

	/**
	 * @param onlineResource The onlineResource to set.
	 */
	public void setOnlineResource(String onlineResource) {
		this.onlineResource = onlineResource;
	}

	public HashMap getProperties() {
		HashMap info = new HashMap();
		info.put("name", getName());
		info.put("layerName", getLayerName());
		// layer text is no longer available
		//info.put("layerText", getLayerText());
		info.put("attributes", getFields());
		info.put("host", getHost());
		info.put("wfsLayerNode", getWfsLayerNode());
		WFSStatus status = new WFSStatus(getLayerName(),getNameSpace());
		status.setBuffer(getNumfeatures());
		status.setTimeout(getTimeout());
		status.setUserName(getUserName());
		status.setPassword(getPassword());
		status.setFilterQuery(getFieldsQuery());
		status.setFilterVisualText(getVisualFilterQuery());
		info.put("status",status);
		return info;
	}

	/**
	 * @return Returns the wfsLayerNode.
	 */
	public WFSLayerNode getWfsLayerNode() {
		return wfsLayerNode;
	}

	/**
	 * @param wfsLayerNode The wfsLayerNode to set.
	 */
	public void setWfsLayerNode(WFSLayerNode wfsLayerNode) {
		this.wfsLayerNode = wfsLayerNode;
	}

	public void setHost(String host2) {
		try {
			setHost(new URL(host2));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public ImageIcon getTocImageIcon() {			
		return new ImageIcon(PluginServices.getPluginServices("com.iver.cit.gvsig.wfs2").getClassLoader().getResource("images/icoLayer.png"));
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.FLyrVect#isPropertiesMenuVisible()
	 */
	public boolean isPropertiesMenuVisible(){
		return false;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}

	/**
	 * @return XMLEntity.
	 * @throws XMLException
	 */
	public XMLEntity getXMLEntity() throws XMLException {	
		XMLEntity xml = super.getXMLEntity();		

		xml.putProperty("fullExtent", StringUtilities.rect2String(getFullExtent()));

		// Host
		xml.putProperty("host", host.toExternalForm());

		// Layer name
		xml.putProperty("layerName", getLayerName());		
		xml.putProperty("name", getName());

		//Layer fields
		XMLElement[] fields = getFields();
		String strFields = "";
		for (int i=0 ; i<fields.length ; i++){
			//If is not the root node
			if (fields[i].getParentElement() != null){
				strFields = strFields + fields[i].getFullName();
				if (i < fields.length - 1){
					strFields = strFields + "~##SEP1##~";
				}
			}else{
				xml.putProperty("rootNode", true);
			}
		}
		xml.putProperty("fields", strFields);

		// User name
		xml.putProperty("user", getUserName());

		// SRS
		xml.putProperty("srs", getSrs());		

		// OnlineResources
		xml.putProperty("onlineResource", getOnlineResource());

		// TimeOut
		xml.putProperty("timeout", getTimeout());		

		// Buffer
		xml.putProperty("buffer", getNumfeatures());

		//Projection
		xml.putProperty("projection", ProjectionUtils.getAbrev(getCrs()));

		//Filter
		xml.putProperty("filterEncoding",getFieldsQuery());

		return xml;
	}

	/**
	 * @param xml XMLEntity
	 *
	 * @throws XMLException
	 * @throws DriverException
	 * @throws DriverI OException
	 */
	public void setXMLEntity(XMLEntity xml)throws XMLException {

		// Host
		try {
			host = new URL(xml.getStringProperty("host"));
		} catch (MalformedURLException e) {
			throw new XMLException(e);
		}

		// Layer name
		setLayerName(xml.getStringProperty("layerName"));			
		setName(xml.getStringProperty("name"));		

		// User name
		if (xml.contains("user")){
			setUserName(xml.getStringProperty("user"));
		}

		// SRS
		if (xml.contains("srs")){
			setSrs(xml.getStringProperty("srs"));
		}

		// OnlineResources
		if (xml.contains("onlineResource")){
			setOnlineResource(xml.getStringProperty("onlineResource"));
		}

		// TimeOut
		if (xml.contains("timeout")){
			setTimeout(xml.getIntProperty("timeout"));
		}

		// Buffer
		if (xml.contains("buffer")){
			setNumfeatures(xml.getIntProperty("buffer"));
		}

		//Projection
		if (xml.contains("projection")){
			setCrs(ProjectionUtils.getCRS(xml.getStringProperty("projection")));
		}       

		//Filter
		if (xml.contains("filterEncoding")){
			setFieldsQuery(xml.getStringProperty("filterEncoding"));
		}   

		// Layer fields
		FMapWFSDriver driver;
		try {
			driver = FMapWFSDriverFactory.getFMapDriverForURL(host);
			setWfsDriver(driver);	
			WFSAdapter adapter = new WFSAdapter();
			adapter.setDriver((VectorialDriver) wfsDriver);
			setSource(adapter);			
		} catch (Exception e) {
			throw new XMLException(e);	
		}
		driver.getLayerList();
		WFSLayerNode layer = driver.getLayerInfo(getLayerName());			
		XMLElement[] atts = null;		

		//		The root element always exists
		XMLElement rootElement = (XMLElement)layer.getFields().get(0);

		if (xml.contains("fields")) {
			String[] fields = xml.getStringProperty("fields").split("~##SEP1##~");	
			if ((fields.length == 1) && (fields[0].equals(""))){
				fields = new String[0];
			}
			//The root element always is a complex type
			Vector allFields = ((XMLComplexType)rootElement.getEntityType()).getAttributes();
			//If the root node has been selected
			if (xml.contains("rootNode")){
				if (xml.getBooleanProperty("rootNode")==true){
					atts = new XMLElement[fields.length + 1];
					atts[fields.length] = rootElement;
				}else{
					atts = new XMLElement[fields.length];
				}
			}else{
				atts = new XMLElement[fields.length];
			}
			//Adding the other fields
			for (int i=0 ; i<fields.length ; i++){
				for (int j=0 ; j<allFields.size() ; j++){
					XMLElement field = (XMLElement)allFields.get(j);
					if (field != null){
						XMLElement found = field.searchAttribute(fields[i]);
						if (found != null){
							atts[i] = found;
							break;
						}
					}
				}					
			}			
		}else{
			if (xml.contains("rootNode")){
				if (xml.getBooleanProperty("rootNode")==true){
					atts = new XMLElement[1];
					atts[0] = rootElement;
				}
			}
		}
		layer.setSelectedFields(atts);
		setWfsLayerNode(layer);
		setFields(atts);	
		setAvailable(true);

		try{
			//Set the legend
			driver.getFeature(getWFSStatus());
			super.setXMLEntity(xml);
			setLegend(LegendFactory.createFromXML(xml.getChild(0)));
		} catch (Exception e) {
			throw new XMLException(e);	
		}
	}

	/**
	 * @return Returns the fieldsQuery.
	 */
	public String getFieldsQuery() {
		return FieldsQuery;
	}

	/**
	 * @param fieldsQuery The fieldsQuery to set.
	 */
	public void setFieldsQuery(String fieldsQuery) {
		FieldsQuery = fieldsQuery;
	}

	/**
	 * Sets all fields and values known about this layer
	 * 
	 * @param _allFieldsAndValuesKnown A Map
	 */
	public void setAllFieldsAndValuesKnown(Map _allFieldsAndValuesKnown) {
		if (this.allFieldsAndValuesKnown == null)
			allFieldsAndValuesKnown = new HashMap();

		allFieldsAndValuesKnown = _allFieldsAndValuesKnown;
	}

	/**
	 * Gets all fields and values known about this layer
	 * 
	 * @return A Map
	 */
	public Map getAllFieldsAndValuesKnown() {
		return allFieldsAndValuesKnown;
	}

	/**
	 * @return the isWfstEditing
	 */
	public boolean isWfstEditing() {
		return wfsDriver.isWfstEditing();
	}

	/**
	 * @param isWfstEditing the isWfstEditing to set
	 */
	public void setWfstEditing(boolean isWfstEditing) {
		wfsDriver.setWfstEditing(isWfstEditing);
	}

	/**
	 * It locks all the features
	 * @param expiryTime
	 * The maximum time to edit
	 * @throws WFSTLockFeaturesException 
	 */
	public void lockCurrentFeatures(int expiryTime) throws WFSTLockFeaturesException{
		wfstExpiryTime = expiryTime;
		wfsDriver.lockCurrentFeatures(expiryTime);
	}

	/**
	 * @return the wfstExpiryTime
	 */
	public int getWfstExpiryTime() {
		return wfstExpiryTime;
	}

	/**
	 * @return true if the layer can be edit using WFST
	 */
	public boolean isTransactional(){
		return wfsDriver.isTransactional();
	}

	/**
	 * @param wfstExpiryTime the wfstExpiryTime to set
	 */
	public void setWfstExpiryTime(int wfstExpiryTime) {
		this.wfstExpiryTime = wfstExpiryTime;
	}	

	/**
	 * @return the isWfstGeometriesUpdated
	 */
	public boolean isWfstSrsBasedOnXML() {
		return getWFSStatus().isSRSBasedOnXML();
	}

	/**
	 * @param isWfstGeometriesUpdated the isWfstGeometriesUpdated to set
	 */
	public void setWfstSrsBasedOnXML(boolean isSrsBasedOnXML) {
		getWFSStatus().setSRSBasedOnXML(isSrsBasedOnXML);
	}

	/**
	 * @return the isLockFeaturesEnabled
	 */
	public boolean isWfstLockFeaturesEnabled() {
		return getWFSStatus().isLockFeaturesEnabled();
	}

	/**
	 * @param isLockFeaturesEnabled the isLockFeaturesEnabled to set
	 */
	public void setWfstLockFeaturesEnabled(boolean isLockFeaturesEnabled) {
		getWFSStatus().setLockFeaturesEnabled(isLockFeaturesEnabled);
	}	

	/**
	 * @return the nameSpace
	 */
	public String getNameSpace() {
		return nameSpace;
	}

	/**
	 * @param nameSpace the nameSpace to set
	 */
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	@Override
	public boolean isWritable() {
		return false;
	}	
}
