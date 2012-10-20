package org.gvsig.gpe.gml.writer.v2.geometries;

import java.io.IOException;

import org.gvsig.gpe.gml.utils.GMLTags;
import org.gvsig.gpe.gml.writer.GPEGmlWriterHandlerImplementor;
import org.gvsig.gpe.writer.ICoordinateSequence;
import org.gvsig.gpe.xml.stream.IXmlStreamWriter;

/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
 * $Id: BoundedByWriter.java 350 2008-01-09 12:53:07Z jpiera $
 * $Log$
 * Revision 1.7  2007/06/07 14:53:30  jorpiell
 * Add the schema support
 *
 * Revision 1.6  2007/05/16 13:00:48  csanchez
 * Actualización de libGPE-GML
 *
 * Revision 1.5  2007/05/14 11:18:12  jorpiell
 * Add the ErrorHandler to all the methods
 *
 * Revision 1.4  2007/05/08 10:24:16  jorpiell
 * Add comments to create javadocs
 *
 * Revision 1.3  2007/04/14 16:07:30  jorpiell
 * The writer has been created
 *
 * Revision 1.2  2007/04/13 13:16:00  jorpiell
 * Add the multiple geometries
 *
 * Revision 1.1  2007/04/12 11:36:15  jorpiell
 * Added new geometry writers
 *
 *
 */
/**
 * It writes a gml:BoundingShapeType object. Example:
 * <p>
 * 
 * <pre>
 * <code>
 * &lt;gml:boundedBy&gt;
 * &lt;gml:Box srsName="http://www.opengis.net/gml/srs/epsg.xml#4326"&gt;
 * &lt;gml:coord&gt;&lt;gml:X&gt;0&lt;/gml:X&gt;&lt;gml:Y&gt;0&lt;/gml:Y&gt;&lt;/gml:coord&gt;
 * &lt;gml:coord&gt;&lt;gml:X&gt;50&lt;/gml:X&gt;&lt;gml:Y&gt;50&lt;/gml:Y&gt;&lt;/gml:coord&gt;
 * &lt;/gml:Box&gt;
 * &lt;/gml:boundedBy&gt;
 * </code>
 * </pre>
 * 
 * </p>
 * 
 * @author Jorge Piera LLodr� (jorge.piera@iver.es)
 */
public class BoundedByWriter {

	/**
	 * It writes a gml:BoundedBy init tag
	 * 
	 * @param writer
	 *            Writer to write the labels
	 * @param handler
	 *            The writer handler implementor Geometry ID
	 * @param coords
	 *            A coordinates sequence
	 * @param srs
	 *            Spatial reference system
	 * @throws IOException
	 */
	public void start(IXmlStreamWriter writer,
			GPEGmlWriterHandlerImplementor handler, String id,
			ICoordinateSequence coords, String srs) throws IOException {
		writer.writeStartElement(GMLTags.GML_BOUNDEDBY);
		handler.getProfile().getBoxWriter()
				.start(writer, handler, id, coords, srs);
	}

	/**
	 * It writes a gml:BoundedBy end tag
	 * 
	 * @param writer
	 *            Writer to write the labels
	 * @param handler
	 *            The writer handler implementor
	 * @throws IOException
	 */
	public void end(IXmlStreamWriter writer,
			GPEGmlWriterHandlerImplementor handler) throws IOException {
		handler.getProfile().getBoxWriter().end(writer, handler);
		writer.writeEndElement();
	}
}
