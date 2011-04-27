package org.gvsig.quickInfo;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gvsig.quickInfo.i18n.Messages;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * <p>Tests the generation of an XML document of tool tip text according the style of the "quick information" tool.</p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class TestQuickInfoFLayerSelected {
	public static void main(String args[]) {
		try {
			FLyrVect v = new FLyrVect();
			v.setName("CAPA VECTORIAL");
			
			QuickInfoFLayerSelected info = new QuickInfoFLayerSelected(v);
			HashMap lfields = info.getLayerFields();
			lfields.put("Campo1", new Vector(Arrays.asList(new String[]{"texto 1", "texto 4"})));
			lfields.put("Campo2", new Vector(Arrays.asList(new String[]{"texto 2", "texto 5"})));
			lfields.put("Campo3", new Vector(Arrays.asList(new String[]{"texto 3", "texto 6"})));
			info.setAnyLayerFieldAdded(true);
			
			HashMap clfields = info.getCalculatedLayerFields();
			clfields.put("CampoOp1", new Vector(Arrays.asList(new String[]{"texto 7", "texto 9"})));
			clfields.put("CampoOp2", new Vector(Arrays.asList(new String[]{"texto 8", "texto 10"})));
			info.getGeometryIDs().addAll(Arrays.asList(new GeometryIDInfo[]{new GeometryIDInfo("GID1"), new GeometryIDInfo("GID2")}));
			info.setAnyCalculatedLayerFieldsAdded(true);

			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
		
			Source src = new DOMSource(info.getToolTipStyledDocument());
			Result dest = new StreamResult(System.out);
			aTransformer.transform(src, dest);
		} catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, Messages.getText("An_exception_happened"), Messages.getText("Error"), JOptionPane.ERROR_MESSAGE);
		}
	}
}
