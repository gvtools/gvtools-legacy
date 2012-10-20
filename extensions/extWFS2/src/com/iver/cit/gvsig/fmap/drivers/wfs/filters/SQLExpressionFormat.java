package com.iver.cit.gvsig.fmap.drivers.wfs.filters;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.gvsig.remoteClient.wfs.filters.FilterEncoding;
import org.gvsig.remoteClient.wfs.filters.ISQLExpressionFormat;

import Zql.ParseException;
import Zql.ZExp;
import Zql.ZqlParser;

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
 * $Id: SQLExpressionFormat.java 8847 2006-11-17 11:29:00Z ppiqueras $
 * $Log$
 * Revision 1.4.2.2  2006-11-17 11:28:45  ppiqueras
 * Corregidos bugs y aÃ±adida nueva funcionalidad.
 *
 * Revision 1.4  2006/10/24 07:27:56  jorpiell
 * Algunos cambios en el modelo que usa la tabla
 *
 * Revision 1.3  2006/10/23 07:37:04  jorpiell
 * Ya funciona el filterEncoding
 *
 * Revision 1.2  2006/10/10 12:55:06  jorpiell
 * Se ha añadido el soporte de features complejas
 *
 * Revision 1.1  2006/10/05 10:26:26  jorpiell
 * Añadidas las clases para obtener los filtros
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class SQLExpressionFormat implements ISQLExpressionFormat {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.remoteClient.wfs.filters.ISQLExpressionFormat#format(java.lang
	 * .String)
	 */
	public String format(String query) {
		if ((query == null) || (query.equals(""))) {
			return null;
		}
		InputStream is = new ByteArrayInputStream(query.getBytes());
		ZqlParser parser = new ZqlParser();
		parser.initParser(is);
		ZExp exp;
		try {
			exp = parser.readExpression();
			return exp.toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query;
	}

	/**
	 * Creates a Filter Encoding creator that uses this formatter to parse the
	 * SQL
	 * 
	 * @return
	 */
	public static FilterEncoding createFilter() {
		return new FilterEncoding(new SQLExpressionFormat());
	}

}
