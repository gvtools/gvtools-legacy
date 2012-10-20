/*
 * Created on 13.04.2005 for PIROL
 *
 * SVN header information:
 *  $Author: LBST-PF-3\orahn $
 *  $Rev: 2446 $
 *  $Date: 2006-09-12 14:57:25 +0200 (Di, 12 Sep 2006) $
 *  $Id: CollectionsTools.java 2446 2006-09-12 12:57:25Z LBST-PF-3\orahn $
 */
package org.gvsig.fmap.algorithm.triangulation.pirol.apiTools;

import java.util.List;

/**
 * Class for more convenient use of Lists and Arrays.
 * 
 * @author Ole Rahn
 * 
 *         FH Osnabrück - University of Applied Sciences Osnabrück Project PIROL
 *         2005 Daten- und Wissensmanagement
 * 
 */
public class CollectionsTools {

	public static boolean addArrayToList(List toAddTo, Object[] arrayToBeAdded) {

		for (int i = 0; i < arrayToBeAdded.length; i++) {
			toAddTo.add(arrayToBeAdded[i]);
		}

		return true;
	}

}
