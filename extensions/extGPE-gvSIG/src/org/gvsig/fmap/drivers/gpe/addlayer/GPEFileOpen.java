package org.gvsig.fmap.drivers.gpe.addlayer;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.gvsig.gpe.GPERegister;
import org.gvsig.gpe.parser.GPEParser;

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
public class GPEFileOpen extends JFileChooser{
	private ArrayList arrayFileFilter;
	
	public GPEFileOpen(){
		arrayFileFilter = new ArrayList();
		GPEParser[] parsers = GPERegister.getAllParsers();
		for (int i=0 ; i<parsers.length ; i++){
			arrayFileFilter.add(new GPEParserFileFilter(parsers[i]));
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.JFileChooser#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (f.isDirectory()){
			return true;
		}
		FileFilter filter = super.getFileFilter();
		return (filter.accept(f));		
	}	
}
