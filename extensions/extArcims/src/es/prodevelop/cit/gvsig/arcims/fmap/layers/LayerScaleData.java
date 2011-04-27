/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.arcims.fmap.layers;

import org.apache.log4j.Logger;

import org.gvsig.remoteClient.arcims.ArcImsProtocolHandler;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import java.awt.Color;


/**
 * This class keeps a simple layer scale limits data.
 *
 * @author jldominguez
 *
 */
public class LayerScaleData {
    private static Logger logger = Logger.getLogger(LayerScaleData.class.getName());
    public static Color imagYesColor = new Color(43, 185, 35);
    public static Color featYesColor = new Color(1, 51, 236);
    public static Color imagNoColor = new Color(175, 215, 123);
    public static Color featNoColor = new Color(164, 182, 249);
    public static Color unknownColor = new Color(210, 255, 210);
    private final long Min_Allowed_Scale = 1;
    private final long Max_Allowed_Scale = 1000000000;
    private String name;
    private String id;
    private double maxSc;
    private double minSc;
    private String type; // 	LAYERTYPE_F, LAYERTYPE_I

    public LayerScaleData(String layerName, String layerId, long minScale,
        long maxScale, String theType) {
        name = layerName;
        id = layerId;
        maxSc = maxScale;
        minSc = minScale;

        if ((maxSc < 0.0) || (maxSc > Max_Allowed_Scale)) {
            maxSc = 1.0 * Max_Allowed_Scale;
        }

        if ((minSc < 0.0) || (minSc < Min_Allowed_Scale)) {
            minSc = 1.0 * Min_Allowed_Scale;
        }

        type = theType;
    }

    public String getId() {
        return id;
    }

    public double getMaxSc() {
        return maxSc;
    }

    public double getMinSc() {
        return minSc;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the color depending on the type of layer and whether
     * the current scale is inside the limits or not.
     *
     * @param current current scale
     * @return the color to be used when painting the limits
     */
    public Color getColor(double current) {
        Color resp = unknownColor;

        // LAYERTYPE_F, LAYERTYPE_I
        if (type.compareTo(ServiceInfoTags.vLAYERTYPE_F) == 0) {
            if (between(minSc, current, maxSc)) {
                resp = featYesColor;
            }
            else {
                resp = featNoColor;
            }
        }

        if (type.compareTo(ServiceInfoTags.vLAYERTYPE_I) == 0) {
            if (between(minSc, current, maxSc)) {
                resp = imagYesColor;
            }
            else {
                resp = imagNoColor;
            }
        }

        return resp;
    }

    public static Color darker(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        float f = (float) 0.7;
        Color newcolor = new Color(Math.round(r * f), Math.round(g * f),
                Math.round(b * f));

        return newcolor;
    }

    public String getType() {
        return type;
    }

    private boolean between(double a, double d, double b) {
        if ((a > b) && (d < b)) {
            return false;
        }

        if ((a > b) && (d > a)) {
            return false;
        }

        if ((a < b) && (d < a)) {
            return false;
        }

        if ((a < b) && (d > b)) {
            return false;
        }

        return true;
    }

    /**
     * Computes real scale from unit-dependant scale and the dpi.
     *
     * @param rs unit-dependant scale
     * @param units units used
     * @param theDpi
     * @return the real scale (the <i>x</i> in "1:<i>x</i>")
     */
    public static long getTrueScaleFromRelativeScaleAndMapUnits(double rs,
        String units, int theDpi) {
        // decimal_degrees | feet | meters
        double fromMetersPerPixelToTrueScale = theDpi / ArcImsProtocolHandler.INCHES;
        double fromDegreesPerPixelToTrueScale = (fromMetersPerPixelToTrueScale * 40000000.0) / 360.0;
        double fromFeetPerPixelToTrueScale = fromMetersPerPixelToTrueScale * 0.3048;

        if (units.compareToIgnoreCase(ServiceInfoTags.vMAP_UNITS_FEET) == 0) {
            return Math.round(fromFeetPerPixelToTrueScale * rs);
        }

        if (units.compareToIgnoreCase(ServiceInfoTags.vMAP_UNITS_METERS) == 0) {
            return Math.round(fromMetersPerPixelToTrueScale * rs);
        }

        if (units.compareToIgnoreCase(
                    ServiceInfoTags.vMAP_UNITS_DECIMAL_DEGREES) == 0) {
            return Math.round(fromDegreesPerPixelToTrueScale * rs);
        }

        logger.error(
            "Unable to compute true scale. Returned value: scale = 1:1");

        return 1;
    }
}
