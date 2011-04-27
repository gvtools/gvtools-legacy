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
package es.prodevelop.cit.gvsig.arcims.gui.panels.utils;

import com.iver.andami.PluginServices;

import es.prodevelop.cit.gvsig.arcims.fmap.layers.LayerScaleData;

import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayer;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerFeatures;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerImage;
import org.gvsig.remoteClient.utils.BoundaryBox;

import java.util.Vector;

import javax.swing.JComponent;


/**
 * Implements the class used as a list element in most lists
 * in this plugin.
 *
 * @author jldominguez
 *
 */
public class LayersListElement extends JComponent {
    private static final long serialVersionUID = 0;
    private String name;
    private String type;
    private String id;
    private String theFclasstype;
    private String visible;
    private String mapUnits;
    private double maxscale;
    private double minscale;
    private BoundaryBox envelope;
    private boolean showIds = false;
    private boolean isAdded = false;
    private int theDPI;

    public LayersListElement(ServiceInformationLayer sil, String mapunits,
        int dpi) {
        if (sil instanceof ServiceInformationLayerImage) {
            ServiceInformationLayerImage sili = (ServiceInformationLayerImage) sil;
            id = sili.getId();

            if (id == null) {
                id = "";
            }

            name = sili.getName();

            if (name == null) {
                name = "";
            }

            type = sili.getType();

            if (type == null) {
                type = "";
            }

            theFclasstype = "";
            maxscale = sili.getMaxscale();
            minscale = sili.getMinscale();
            visible = sili.getVisible();

            if (visible == null) {
                visible = "";
            }

            envelope = sili.getEnvelope();
        }

        if (sil instanceof ServiceInformationLayerFeatures) {
            ServiceInformationLayerFeatures silf = (ServiceInformationLayerFeatures) sil;
            id = silf.getId();

            if (id == null) {
                id = "";
            }

            name = silf.getName();

            if (name == null) {
                name = "";
            }

            type = silf.getType();

            if (type == null) {
                type = "";
            }

            theFclasstype = silf.getFclasstype();

            if (theFclasstype == null) {
                theFclasstype = "";
            }

            maxscale = silf.getMaxscale();
            minscale = silf.getMinscale();
            visible = silf.getVisible();

            if (visible == null) {
                visible = "";
            }

            envelope = silf.getEnvelope();
        }

        mapUnits = mapunits;
        theDPI = dpi;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }

    public void setShowIds(boolean show) {
        this.showIds = show;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    private String boundaryBoxXRangeToString(BoundaryBox bb) {
        String r = "[ " + ServicesTableModel.leaveNDigits(bb.getXmin(), 10) +
            " , ";
        r = r + ServicesTableModel.leaveNDigits(bb.getXmax(), 10) + " ]";

        return r;
    }

    private String boundaryBoxYRangeToString(BoundaryBox bb) {
        String r = "[ " + ServicesTableModel.leaveNDigits(bb.getYmin(), 10) +
            " , ";
        r = r + ServicesTableModel.leaveNDigits(bb.getYmax(), 10) + " ]";

        return r;
    }

    /**
     * Gets a description of the layer to be shown on one of the
     * wizard's panes.
     *
     * @return a description of the layer
     */
    public Vector getDataVector() {
        long true_scale;
        Vector data = new Vector();
        Vector item = new Vector();

        item.add("ID");
        item.add(id);
        data.add(item.clone());
        item.removeAllElements();

        item.add(PluginServices.getText(this, "name"));
        item.add(name);
        data.add(item.clone());
        item.removeAllElements();

        item.add(PluginServices.getText(this, "type"));
        item.add(PluginServices.getText(this, type));
        data.add(item.clone());
        item.removeAllElements();

        item.add(PluginServices.getText(this, "vector_type"));

        if (theFclasstype.length() != 0) {
            item.add(PluginServices.getText(this, theFclasstype));
        }
        else {
            item.add(PluginServices.getText(this, "not_available"));
        }

        data.add(item.clone());
        item.removeAllElements();

        item.add(PluginServices.getText(this, "max_scale"));
        true_scale = LayerScaleData.getTrueScaleFromRelativeScaleAndMapUnits(maxscale,
                mapUnits, theDPI);

        if (maxscale != -1.0) {
            item.add("1 : " + String.valueOf(true_scale));
        }
        else {
            item.add(PluginServices.getText(this, "not_available"));
        }

        data.add(item.clone());
        item.removeAllElements();

        item.add(PluginServices.getText(this, "min_scale"));
        true_scale = LayerScaleData.getTrueScaleFromRelativeScaleAndMapUnits(minscale,
                mapUnits, theDPI);

        if (minscale != -1.0) {
            item.add("1 : " + String.valueOf(true_scale));
        }
        else {
            item.add(PluginServices.getText(this, "not_available"));
        }

        data.add(item.clone());
        item.removeAllElements();

        item.add(PluginServices.getText(this, "visible"));

        if (visible.length() != 0) {
            item.add(visible);
        }
        else {
            item.add(PluginServices.getText(this, "not_available"));
        }

        data.add(item.clone());
        item.removeAllElements();

        if (envelope == null) {
            item.add(PluginServices.getText(this, "envelope"));
            item.add(PluginServices.getText(this, "not_available"));
            data.add(item.clone());
            item.removeAllElements();
        }
        else {
            item.add(PluginServices.getText(this, "envelope") + " (" +
                PluginServices.getText(this, "xrange") + ")");
            item.add(boundaryBoxXRangeToString(envelope));
            data.add(item.clone());
            item.removeAllElements();
            item.add(PluginServices.getText(this, "envelope") + " (" +
                PluginServices.getText(this, "yrange") + ")");
            item.add(boundaryBoxYRangeToString(envelope));
            data.add(item.clone());
            item.removeAllElements();
        }

        return data;
    }

    /**
     * Implements the toString() method automatically invoqued by the system
     * to paint a list.
     *
     * @return a String that identifies the layer on the list
     */
    public String toString() {
        if (showIds) {
            return "[" + id + "] " + name;
        }
        else {
            // String tail = repeatString(" ", id.length() + 5);
            // return  name + tail;
            return name;
        }
    }

    public String toolTipText() {
        Vector v = getDataVector();
        Vector vv = null;
        String r = "";

        for (int i = 0; i < v.size(); i++) {
            vv = (Vector) v.get(i);
            r = r + "\n " + ((String) vv.get(0)) + ": ";
            r = r + ((String) vv.get(1)) + " ";
        }

        r = r.substring(1);

        return r;
    }

    /**
     * Utility method.
     * @param array
     * @param s
     * @return index of the first occurrence of <tt>s</tt> in <tt>array</tt>.
     */
    public static int getFirstIndexInIntArray(int[] array, int s) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == s) {
                return i;
            }
        }

        return -1;
    }

    public static String repeatString(String s, int n) {
        String r = "";

        for (int i = 0; i < n; i++)
            r = r + s;

        return r;
    }
}
