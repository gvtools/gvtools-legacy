/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
package org.gvsig.symbology.gui.layerproperties;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gvsig.gui.beans.comboboxconfigurablelookup.DefaultComboBoxConfigurableLookUpModel;
import org.gvsig.gui.beans.comboboxconfigurablelookup.ILookUp;
import org.gvsig.gui.beans.comboboxconfigurablelookup.JComboBoxConfigurableLookUp;
import org.gvsig.gui.beans.comboboxconfigurablelookup.StringComparator;
import org.gvsig.symbology.fmap.labeling.parse.LabelExpressionParserConstants;

public class TestLabelFieldCellEditor /*extends TestCase*/ {




	public static void main(String[] args) {
		JFrame j = new JFrame("test field cell editor");
		JPanel content = new JPanel(new BorderLayout(10, 10));

		JComboBoxConfigurableLookUp combo = new JComboBoxConfigurableLookUp();
		final DefaultComboBoxConfigurableLookUpModel m = (DefaultComboBoxConfigurableLookUpModel)combo.getModel();

		ILookUp agent = new ILookUp() {
			ArrayList<Object> allElements = new ArrayList<Object>();
			{
				for (int i = 0; i < LabelExpressionParserConstants.tokenImage.length; i++) {
					allElements.add(LabelExpressionParserConstants.tokenImage[i].replaceAll("\"",	""));
				}
			}
			public List<Object> doLookUpConsideringCaseSensitive(String text,
					Vector<Object> sortOrderedItems, StringComparator comp) {
				Vector<Object> suggested = new Vector<Object>();

				String t_aux;
				m.setEventNotificationEnabled(false);
				m.removeAllElements();

//				LabelExpressionParser p = new LabelExpressionParser(new StringReader(text));
				try {
					if (text.compareTo("Hola") != 0) {
						throw new Exception();
					}

					t_aux = text;
//					p.FieldExpression();
//					m.disableEvents();
//					m.removeAllElements();
//					m.addElement(text);
//					m.enableEvents();
//					} catch (ParseException e) {
//					for (int i = 0; i < e.expectedTokenSequences.length; i++) {
//					suggested.add(LabelExpressionParserConstants.tokenImage[e.expectedTokenSequences[i][0]].replaceAll("\"",	""));
//					}
				}
				catch (Exception e) {
					t_aux = "";
					///System.out.println(e.getMessage());
//					return (List) suggested;
				}
//				System.out.println("gaste tots els elements");

				for (Object string : allElements) {
					m.addElement(t_aux + " " + string);
					suggested.add(t_aux + " " + string);
				}
//				return suggested;

				m.setEventNotificationEnabled(true);
				return suggested;
			}

			public List<Object> doLookUpIgnoringCaseSensitive(String text,
					Vector<Object> sortOrderedItems, StringComparator comp) {

				return null;
			}

		};
		m.setCaseSensitive(true);
		m.setLookUpAgent(agent);
		for (int i = 0; i < LabelExpressionParserConstants.tokenImage.length; i++) {
			m.addElement(LabelExpressionParserConstants.tokenImage[i]);
		}

		content.add(combo, BorderLayout.CENTER);
		j.setContentPane(content);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.pack();
		j.setVisible(true);


	}
}
