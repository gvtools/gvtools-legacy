package com.iver.cit.gvsig.fmap.drivers;

import org.gvsig.remoteClient.wfs.exceptions.WFSException;

import com.iver.andami.PluginServices;

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
 * $Id: WFSDriverException.java 18381 2008-01-30 12:00:25Z jpiera $
 * $Log$
 * Revision 1.3  2006-09-05 15:41:51  jorpiell
 * Añadida la funcionalidad de cargar WFS desde el catálogo
 *
 * Revision 1.2  2006/05/23 13:20:46  jorpiell
 * Modificado el mensaje en blanco
 *
 * Revision 1.1  2006/04/19 12:50:16  jorpiell
 * Primer commit de la aplicación. Se puede hacer un getCapabilities y ver el mensaje de vienvenida del servidor
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class WFSDriverException extends WFSException {
	public static final String EXC_LAYER_DOESNT_EXIST = "cantLoad";
	private String message = "";

	public String getMessage() {
		return PluginServices.getText(this, "wfs_server_error") + "\n"
				+ format(message, 200);
	}

	/**
	 *
	 */
	public WFSDriverException() {
		super();
	}

	/**
	 * Crea WFSException.
	 * 
	 * @param message
	 */
	public WFSDriverException(String message) {
		super();
		this.message = message;
	}

	/**
	 * Crea WMSException.
	 * 
	 * @param cause
	 */
	public WFSDriverException(Throwable cause) {
		super(cause);
	}

	/**
	 * Cuts the message text to force its lines to be shorter or equal to
	 * lineLength.
	 * 
	 * @param message
	 *            , the message.
	 * @param lineLength
	 *            , the max line length in number of characters.
	 * @return the formated message.
	 */
	private static String format(String message, int lineLength) {
		if (message.length() <= lineLength)
			return message;
		String[] lines = message.split("\n");
		String theMessage = "";
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.length() < lineLength)
				theMessage += line + "\n";
			else {
				String[] chunks = line.split(" ");
				String newLine = "";
				for (int j = 0; j < chunks.length; j++) {
					int currentLength = newLine.length();
					chunks[j] = chunks[j].trim();
					if (chunks[j].length() == 0)
						continue;
					if ((currentLength + chunks[j].length() + " ".length()) <= lineLength)
						newLine += chunks[j] + " ";
					else {
						newLine += "\n" + chunks[j] + " ";
						theMessage += newLine;
						newLine = "";
					}
				}

			}
		}
		return theMessage;
	}

}
