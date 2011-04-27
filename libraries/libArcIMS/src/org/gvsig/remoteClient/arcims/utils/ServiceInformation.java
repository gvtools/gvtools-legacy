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
 *  Generalitat Valenciana
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

/**
 *
 */
package org.gvsig.remoteClient.arcims.utils;

import org.gvsig.remoteClient.utils.BoundaryBox;

import java.awt.geom.Rectangle2D;

import java.util.Iterator;
import java.util.Vector;


/**
 * Class that stores the general and layers information of an ArcIMS Server to
 * be retrieved by different classes (ArcImsStatus, ArcImsProtocolHandler,...)
 *
 * @author jsanz
 *
 */
public class ServiceInformation implements Cloneable {
    private String type;
    private ServiceInformationLocale locale;
    private ServiceInformationUIFont uifont;
    private ServiceInformationSeparators separators;
    private int screen_dpi;
    private String imagelimit_pixelcount;
    private String featurecoordsys;
    private boolean srsAssumed;
    private boolean dpiAssumed;

    /**
     * Envelope with name equal to "Initial_Extent", it will be the default
     * Envelope.
     */
    private BoundaryBox envelope;

    /**
     * Envelope with name equal to "Extent_Limit", it stores the maximum zoomout
     * extension defined in the configuration file. However, this information is
     * ignored by the ArcIMS Spatial Server.
     */
    private BoundaryBox envelopeEL;

    /**
     * Units of the map (metes, decimal_degrees, etc.
     */
    private String mapunits;

    /**
     * Vector of layers,
     *
     * @see ServiceInformationLayer
     */
    private Vector layers;

    /**
     * Constructor.
     *
     */
    public ServiceInformation() {
        type = new String();
        locale = new ServiceInformationLocale();
        uifont = new ServiceInformationUIFont();
        separators = new ServiceInformationSeparators();
        screen_dpi = 96;
        imagelimit_pixelcount = new String();
        envelope = new BoundaryBox();
        mapunits = new String();
        featurecoordsys = new String();
        layers = new Vector();
    }

    public Object clone() {
        ServiceInformation si = new ServiceInformation();
        si.setType(this.type);

        ServiceInformationLocale locale = this.locale;

        if (locale != null) {
            si.setLocale((ServiceInformationLocale) this.locale.clone());
        }

        return si;
    }

    public boolean isQueryable() {
        // if (operations.keySet().contains( CapabilitiesTags.GETFEATUREINFO ))
        return true;

        // else
        // return false;
        // }
    }

    public BoundaryBox getEnvelope() {
        return envelope;
    }

    public void setEnvelope(BoundaryBox envelope) {
        this.envelope = envelope;
    }

    public BoundaryBox getEnvelopeEL() {
        return envelopeEL;
    }

    public void setEnvelopeEL(BoundaryBox envelope) {
        this.envelopeEL = envelope;
    }

    public Rectangle2D getEnvelopeR2D() {
        Rectangle2D.Double r = new Rectangle2D.Double();
        r.setFrameFromDiagonal(envelope.getXmin(), envelope.getYmin(),
            envelope.getXmax(), envelope.getYmax());

        return r;
    }

    /**
     * Add a layer to the ServiceInformation @see ServiceInformationLayer.
     *
     * @param sil
     */
    public void addLayer(ServiceInformationLayer sil) {
        if (!layers.contains(sil)) {
            layers.add(sil);
        }
    }

    /**
     * Get a layer of the ServiceInformation.
     *
     * @param i Index of the vector of layers
     * @return @see ServiceInformationLayer
     */
    public ServiceInformationLayer getLayer(int i) {
        ServiceInformationLayer sil = (ServiceInformationLayer) this.layers.get(i);

        return sil;
    }

    /**
     * Get a layer of the ServiceInformation.
     *
     * @param i Index of the vector of layers
     * @return @see ServiceInformationLayer
     */
    public ServiceInformationLayer getLayerById(String name) {
        Iterator it = layers.iterator();
        ServiceInformationLayer sil = null;

        while (it.hasNext()) {
            sil = (ServiceInformationLayer) it.next();

            if (sil.getId().equals(name)) {
                return sil;
            }
        }

        return null;
    }

    /**
     * Get the type.
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type.
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Returns the imagelimit_pixelcount.
     */
    public String getImagelimit_pixelcount() {
        return imagelimit_pixelcount;
    }

    /**
     * @param imagelimit_pixelcount The imagelimit_pixelcount to set.
     */
    public void setImagelimit_pixelcount(String imagelimit_pixelcount) {
        this.imagelimit_pixelcount = imagelimit_pixelcount;
    }

    /**
     * @return Returns the layers.
     */
    public Vector getLayers() {
        return layers;
    }

    /**
     * @param layers The layers to set.
     */
    public void setLayers(Vector layers) {
        this.layers = layers;
    }

    /**
     * @return Returns the locale.
     */
    public ServiceInformationLocale getLocale() {
        return locale;
    }

    /**
     * @param locale The locale to set.
     */
    public void setLocale(ServiceInformationLocale locale) {
        this.locale = locale;
    }

    /**
     * @return Returns the mapunits.
     */
    public String getMapunits() {
        return mapunits;
    }

    /**
     * @param mapunits The mapunits to set.
     */
    public void setMapunits(String mapunits) {
        this.mapunits = mapunits;
    }

    /**
     * @return Returns the separators.
     */
    public ServiceInformationSeparators getSeparators() {
        return separators;
    }

    /**
     * @param separators The separators to set.
     */
    public void setSeparators(ServiceInformationSeparators separators) {
        this.separators = separators;
    }

    /**
     * @return Returns the uifont.
     */
    public ServiceInformationUIFont getUifont() {
        return uifont;
    }

    /**
     * @param uifont The uifont to set.
     */
    public void setUifont(ServiceInformationUIFont uifont) {
        this.uifont = uifont;
    }

    /**
     * @return Returns the screen_dpi.
     */
    public int getScreen_dpi() {
        return screen_dpi;
    }

    /**
     *
     * @param screen_dpi the screen_dpi to set
     */
    public void setScreen_dpi(int screen_dpi) {
        this.screen_dpi = screen_dpi;
    }

    /**
     * @return Returns the featurecoordsys.
     */
    public String getFeaturecoordsys() {
        return featurecoordsys;
    }

    /**
     * @param featurecoordsys The featurecoordsys to set.
     */
    public void setFeaturecoordsys(String featurecoordsys) {
        this.featurecoordsys = featurecoordsys;
    }

    /**
     * @return Returns the dpiAssumed.
     */
    public boolean isDpiAssumed() {
        return dpiAssumed;
    }

    /**
     * @return Returns the srsAssumed.
     */
    public boolean isSrsAssumed() {
        return srsAssumed;
    }

    /**
     * @param dpiAssumed The dpiAssumed to set.
     */
    public void setDpiAssumed(boolean dpiAssumed) {
        this.dpiAssumed = dpiAssumed;
    }

    /**
     * @param srsAssumed The srsAssumed to set.
     */
    public void setSrsAssumed(boolean srsAssumed) {
        this.srsAssumed = srsAssumed;
    }

    /**
     * Inner class that represents the description of the ArcIms metadata. The
     * first part of the capabilities will return the service information from
     * the ArcIms, this class will hold this information.
     *
     * @author jcarrasco
     */
    public class ServiceInformationLocale implements Cloneable {
        private String language;
        private String country;

        public ServiceInformationLocale() {
            language = new String();
            country = new String();
        }

        public Object clone() {
            ServiceInformationLocale loc = new ServiceInformationLocale();
            loc.setCountry(this.country);
            loc.setLanguage(this.language);

            return loc;
        }

        /**
         * @return Returns the country.
         */
        public String getCountry() {
            return country;
        }

        /**
         * @param country The country to set.
         */
        public void setCountry(String country) {
            this.country = country;
        }

        /**
         * @return Returns the language.
         */
        public String getLanguage() {
            return language;
        }

        /**
         * @param language The language to set.
         */
        public void setLanguage(String language) {
            this.language = language;
        }
    }

    /**
     * Inner class that stores the UIFont information of a ServicInfo request.
     *
     * @author jcarrasco
     *
     */
    public class ServiceInformationUIFont implements Cloneable {
        private String name;
        private String color;
        private String size;
        private String style;

        public ServiceInformationUIFont() {
            name = new String();
            color = new String();
            size = new String();
            style = new String();
        }

        public Object clone() {
            ServiceInformationUIFont siuf = new ServiceInformationUIFont();
            siuf.setColor(this.color);
            siuf.setName(this.name);
            siuf.setSize(this.size);
            siuf.setStyle(this.style);

            return siuf;
        }

        /**
         * @return Returns the color.
         */
        public String getColor() {
            return color;
        }

        /**
         * @param color The color to set.
         */
        public void setColor(String color) {
            this.color = color;
        }

        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name to set.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return Returns the size.
         */
        public String getSize() {
            return size;
        }

        /**
         * @param size The size to set.
         */
        public void setSize(String size) {
            this.size = size;
        }

        /**
         * @return Returns the style.
         */
        public String getStyle() {
            return style;
        }

        /**
         * @param style The style to set.
         */
        public void setStyle(String style) {
            this.style = style;
        }
    }

    /**
     * Inner class that stores the Separators information of a ServicInfo
     * request.
     *
     * @author jcarrasco
     *
     */
    public class ServiceInformationSeparators implements Cloneable {
        /**
         * Coordinate separator is used to separate an x-coordinate from a y-coordinate.
         */
        private String cs;

        /**
         * Tuple separator is used to separate coordinate pairs and string lists.
         */
        private String ts;

        /**
         * Decimal separator is used to separate integer part from the decimal part
         * of a float/double number. It will be determined from the language/country
         * information of the service. The default value is '.'.
         */
        private char ds;

        public ServiceInformationSeparators() {
            cs = new String();
            ts = new String();
            ds = 'c';
        }

        public Object clone() {
            ServiceInformationSeparators sis = new ServiceInformationSeparators();
            sis.setCs(this.getCs());
            sis.setDs(this.getDs());
            sis.setTs(this.getTs());

            return sis;
        }

        /**
         * @return Returns the Coordinate Separator.
         */
        public String getCs() {
            return cs;
        }

        /**
         * @param cs The cs to set.
         */
        public void setCs(String cs) {
            this.cs = cs;
        }

        /**
         * @return Returns the Decimal Separator.
         */
        public char getDs() {
            return ds;
        }

        /**
         * @param ds The ds to set.
         */
        public void setDs(char ds) {
            this.ds = ds;
        }

        /**
         * @return Returns the Tuple Separator.
         */
        public String getTs() {
            return ts;
        }

        /**
         * @param ts The ts to set.
         */
        public void setTs(String ts) {
            this.ts = ts;
        }
    }
}
