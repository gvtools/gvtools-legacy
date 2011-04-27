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
package org.gvsig.remoteClient.arcims.styling.renderers;

import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * This renderer is used to group any other type of renderer, so
 * it's unique variable is an ArrayList of renderers and its behavior is
 * more or less as a Collection.
 *
 * This type of legend is used to group other legends and at this time
 * is not supported by gvSIG. Thus, it will be necessary to select one
 * of the inner renderers to create a valid gvSIG legend.
 * @author jsanz
 *
 */
public class GroupRenderer extends BasicRenderer {
    public static final String TAG = ServiceInfoTags.tGROUPRENDERER;
    protected ArrayList renderers;

    public GroupRenderer() {
        this.renderers = new ArrayList();
    }

    /**
     * Generates an XML representation of the Renderer
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator it = renderers.iterator();

        while (it.hasNext()) {
            sb.append(((Renderer) it.next()).toString());
        }

        return "<" + GroupRenderer.TAG + ">\r\n" + sb.toString() + "</" +
        GroupRenderer.TAG + ">\r\n";
    }

    /**
     * @return Returns the renderer.
     */
    public Renderer getRenderer(int index) {
        return (Renderer) renderers.get(index);
    }

    /**
     * @param renderer The renderer to set.
     */
    public boolean addRender(Renderer renderer) {
        return this.renderers.add(renderer);
    }

    public int size() {
        return renderers.size();
    }

    public void clear() {
        this.renderers.clear();
    }

    public boolean isEmpty() {
        if (this.renderers != null) {
            return this.renderers.isEmpty();
        } else {
            return false;
        }
    }

    public Iterator iterator() {
        return this.renderers.iterator();
    }

    public Object[] toArray(Object[] a) {
        return this.renderers.toArray();
    }
}
