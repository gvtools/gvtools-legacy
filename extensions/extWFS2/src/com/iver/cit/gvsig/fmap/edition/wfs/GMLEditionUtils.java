package com.iver.cit.gvsig.fmap.edition.wfs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.fmap.drivers.gpe.writer.ExportGeometry;
import org.gvsig.gpe.exceptions.WriterHandlerCreationException;
import org.gvsig.gpe.writer.GPEWriterHandler;
import org.gvsig.remoteClient.gml.schemas.XMLElement;
import org.gvsig.remoteClient.gml.types.GMLGeometryType;
import org.gvsig.remoteClient.wfs.WFSClient;
import org.gvsig.remoteClient.wfs.WFSFeature;
import org.gvsig.remoteClient.wfs.WFSStatus;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

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
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class GMLEditionUtils {

	/**
	 * It creates a GML to insert a new feature
	 * 
	 * @param row
	 * @param fields
	 * @param crs
	 * @return
	 * @throws IOException
	 * @throws GPEWriterHandlerCreationException
	 */
	public static String getInsertQuery(IRowEdited row,
			FieldDescription[] fields, CoordinateReferenceSystem crs,
			WFSStatus status, WFSClient wfsClient) throws IOException,
			WriterHandlerCreationException {
		OutputStream os = new ByteArrayOutputStream();
		GPEWriterHandler writer = new WFSTWriterHandler(
				new WFSTWriterHandlerImplementor("WFST", "WFSTWriter", os), os);
		writer.setOutputStream(os);
		writer.startFeature(row.getAttribute(row.getAttributes().length - 1)
				.toString(), status.getFeatureName(), status
				.getNamespacePrefix());
		// Write the geometry
		XMLElement geometry = getGeometry(status, wfsClient);
		ExportGeometry exportGeometry = new ExportGeometry(writer);
		exportGeometry.setGeometry(geometry);
		exportGeometry.setSourceCrs(getCRSView());
		exportGeometry.setTargetCrs(getCRSLayer(status, wfsClient));
		// If the geometry is an element
		if (geometry != null) {
			writer.startElement(
					status.getNamespacePrefix() + ":" + geometry.getName(), "",
					null);
			exportGeometry.writeGeometry(((IFeature) row.getLinkedRow())
					.getGeometry());

			writer.endElement();
		} else {
			exportGeometry.writeGeometry(((IFeature) row.getLinkedRow())
					.getGeometry());
		}
		// Write the attributes
		for (int i = 0; i < row.getAttributes().length - 1; i++) {
			if ((geometry != null)
					&& (geometry.getName().compareTo(fields[i].getFieldName()) == 0)) {
				continue;
			}
			if (fields[i].getFieldName().compareTo("the geom") == 0) {
				continue;
			}
			if (fields[i].getFieldName().equals("objectidglds")) {
				continue;
			}
			String value = row.getAttributes()[i].toString();
			if (value.compareTo("") > 0) {
				writer.startElement(status.getNamespacePrefix() + ":"
						+ fields[i].getFieldName(), value, null);
				writer.endElement();
			}
		}
		writer.endFeature();
		return os.toString();
	}

	/**
	 * @return the View CRS. It is necessary because the updated coordinates are
	 *         on this CRS. This method will be removed.
	 */
	private static CoordinateReferenceSystem getCRSView() {
		BaseView view = (BaseView) PluginServices.getMDIManager()
				.getActiveWindow();
		return view.getCrs();
	}

	/**
	 * @return the Layer CRS. It is necessary because the updated coordinates
	 *         has to be converted to this CRS. This method will be removed.
	 */
	private static CoordinateReferenceSystem getCRSLayer(WFSStatus status,
			WFSClient wfsClient) {
		Hashtable features = wfsClient.getFeatures();
		WFSFeature feature = (WFSFeature) features.get(status.getFeatureName());
		Vector crss = feature.getSrs();
		if ((crss != null) && (crss.size() > 0)) {
			String srs = (String) crss.get(0);
			return ProjectionUtils.getCRS(srs);
		}
		return null;
	}

	/**
	 * Gets the geometric property of a feature
	 * 
	 * @param status
	 * @param wfsClient
	 * @return
	 */
	public static XMLElement getGeometry(WFSStatus status, WFSClient wfsClient) {
		Hashtable features = wfsClient.getFeatures();
		WFSFeature feature = (WFSFeature) features.get(status.getFeatureName());
		Vector fields = feature.getFields();
		if (fields.size() > 0) {
			fields = ((XMLElement) fields.get(0)).getChildren();
			for (int i = 0; i < fields.size(); i++) {
				XMLElement element = (XMLElement) fields.get(i);
				if (isGeometry(element)) {
					return element;
				}
			}
		}
		return null;
	}

	/**
	 * @param element
	 * @return If a element has a geometry
	 */
	private static boolean isGeometry(XMLElement element) {
		if (element.getEntityType() instanceof GMLGeometryType) {
			return true;
		}
		Vector fields = element.getChildren();
		boolean isGeometry = false;
		for (int i = 0; i < fields.size(); i++) {
			XMLElement childElement = (XMLElement) fields.get(i);
			if (element.getEntityType() instanceof GMLGeometryType) {
				isGeometry = true;
			}
		}
		return isGeometry;
	}

	/**
	 * Creates an Update WFST Query
	 * 
	 * @param row
	 * @param fields
	 * @param object
	 * @param status
	 * @param remoteServicesClient
	 * @param isGeometryUpdated
	 * @return
	 */
	public static String getUpdateQuery(IRowEdited row,
			FieldDescription[] fields, Object object, WFSStatus status,
			WFSClient remoteServicesClient) {
		StringBuffer query = new StringBuffer();
		// Update the geometry
		XMLElement geometry = getGeometry(status, remoteServicesClient);
		if (geometry != null) {
			OutputStream os = new ByteArrayOutputStream();
			GPEWriterHandler writer = new WFSTWriterHandler(
					new WFSTWriterHandlerImplementor("WFST", "WFSTWriter", os),
					os);
			writer.setOutputStream(os);
			ExportGeometry exportGeometry = new ExportGeometry(writer);
			exportGeometry.setGeometry(geometry);
			exportGeometry.setSourceCrs(getCRSView());
			exportGeometry.setTargetCrs(getCRSLayer(status,
					remoteServicesClient));
			exportGeometry.writeGeometry(((IFeature) row.getLinkedRow())
					.getGeometry());
			query.append(createProperty(status, geometry.getName(),
					os.toString()));
		}
		// Update the fields
		for (int i = 0; i < row.getAttributes().length - 1; i++) {
			if ((geometry != null)
					&& (geometry.getName().compareTo(fields[i].getFieldName()) == 0)) {
				continue;
			}
			if (fields[i].getFieldName().compareTo("the geom") == 0) {
				continue;
			}
			if (fields[i].getFieldName().equals("objectidglds")) {
				continue;
			}
			String value = row.getAttributes()[i].toString();
			if (value.compareTo("") > 0) {
				query.append(createProperty(status, fields[i].getFieldName(),
						value));
			}
		}
		return query.toString();
	}

	/**
	 * Creates a property for the WFS-T update request
	 * 
	 * @param status
	 * @param name
	 * @param value
	 * @return
	 */
	private static String createProperty(WFSStatus status, String name,
			String value) {
		StringBuffer query = new StringBuffer();
		query.append("<wfs:Property>");
		query.append("<wfs:Name>" + status.getNamespacePrefix() + ":" + name
				+ "</wfs:Name>");
		query.append("<wfs:Value>" + value + "</wfs:Value>");
		query.append("</wfs:Property>");
		return query.toString();
	}
}
