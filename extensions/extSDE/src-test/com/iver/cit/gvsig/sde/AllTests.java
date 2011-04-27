/*
 * Created on 08-feb-2007
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
* $Id: AllTests.java 13496 2007-09-04 07:40:30Z cesar $
* $Log$
* Revision 1.1  2007-09-04 07:40:30  cesar
* Add a run-tests target; add an AllTest class to run all the TestCases
*
* Revision 1.4  2007/09/03 10:31:51  caballero
* prepare head
*
* Revision 1.1.2.3  2007/04/19 10:29:44  caballero
* sde funcionando
*
* Revision 1.2  2007/04/13 12:05:32  caballero
* funcionando
*
* Revision 1.1.2.1  2007/03/28 06:37:15  caballero
* arcSDE
*
* Revision 1.1.2.1  2007/02/28 07:35:10  jmvivo
* Actualizado desde el HEAD.
*
* Revision 1.1  2007/02/08 20:27:57  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.sde;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.iver.cit.gvsig.sde");
		//$JUnit-BEGIN$
		suite.addTestSuite(SDETest.class);
		//$JUnit-END$
		return suite;
	}

}

