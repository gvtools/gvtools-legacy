/*
 * Created on 21.06.2005 for PIROL
 *
 * SVN header information:
 *  $Author: LBST-PF-3\orahn $
 *  $Rev: 2446 $
 *  $Date: 2006-09-12 14:57:25 +0200 (Di, 12 Sep 2006) $
 *  $Id: DelaunayInterimMapObject.java 2446 2006-09-12 12:57:25Z LBST-PF-3\orahn $
 */
package org.gvsig.fmap.algorithm.triangulation.pirol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Class that allows to store a map of Envelopes and DelaunayCalculators for a layer.  
 *
 * @author Ole Rahn
 * <br>
 * <br>FH Osnabr&uuml;ck - University of Applied Sciences Osnabr&uuml;ck,
 * <br>Project: PIROL (2005),
 * <br>Subproject: Daten- und Wissensmanagement
 * 
 * @version $Rev: 2446 $
 * 
 */
public class DelaunayInterimMapObject {
    
    private Map Evelope2Delaunay;

    
    public DelaunayInterimMapObject() {
        super();
        this.Evelope2Delaunay = Collections.synchronizedMap(new HashMap());
    }
    
    

    public boolean containsKey(Envelope arg0) {
        return Evelope2Delaunay.containsKey(arg0);
    }
    public DelaunayCalculator get(Envelope arg0) {
        return (DelaunayCalculator)Evelope2Delaunay.get(arg0);
    }
    public Object put(Envelope arg0, DelaunayCalculator arg1) {
        return Evelope2Delaunay.put(arg0, arg1);
    }
    public Object remove(Envelope arg0) {
        return Evelope2Delaunay.remove(arg0);
    }
}
