package com.iver.cit.gvsig.fmap.edition.wfs;

import java.io.IOException;
import java.io.OutputStream;

import org.gvsig.gpe.gml.writer.GPEGmlWriterHandlerImplementor;
import org.gvsig.gpe.gml.writer.profiles.Gml2WriterProfile;
import org.gvsig.gpe.writer.GPEWriterHandler;
import org.gvsig.gpe.writer.IGPEWriterHandlerImplementor;

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
public class WFSTWriterHandler extends GPEWriterHandler {
	private GPEGmlWriterHandlerImplementor implementor = null;
	private OutputStream os = null;
	private String currentFeature = null;
	private String currentElement = null;

	public WFSTWriterHandler(IGPEWriterHandlerImplementor writerImplementor,
			OutputStream os) {
		super(writerImplementor);
		implementor = (GPEGmlWriterHandlerImplementor) writerImplementor;
		implementor.setProfile(new Gml2WriterProfile());
		this.os = os;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.writers.GPEWriterHandler#endElement()
	 */
	public void endElement() {
		try {
			os.write(new String("</" + currentElement + ">").getBytes());
		} catch (IOException e) {
			getErrorHandler().addError(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.writers.GPEWriterHandler#endFeature()
	 */
	public void endFeature() {
		try {
			os.write(new String("</" + currentFeature + ">").getBytes());
		} catch (IOException e) {
			getErrorHandler().addError(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.writers.GPEWriterHandler#startElement(java.lang.String,
	 * java.lang.Object, java.lang.String)
	 */
	public void startElement(String name, Object value, String xsElementName) {
		currentElement = name;
		try {
			os.write(new String("<" + name + ">").getBytes());
			if (value != null) {
				os.write(new String(value.toString()).getBytes());
			}
		} catch (IOException e) {
			getErrorHandler().addError(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.writers.GPEWriterHandler#startFeature(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public void startFeature(String id, String name, String xsElementName) {
		currentFeature = name;
		try {
			os.write(new String("<" + name + ">").getBytes());
		} catch (IOException e) {
			getErrorHandler().addError(e);
		}
	}

}
