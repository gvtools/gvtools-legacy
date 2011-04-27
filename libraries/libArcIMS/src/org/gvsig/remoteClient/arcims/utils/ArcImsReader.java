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

package org.gvsig.remoteClient.arcims.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ArcImsReader extends InputStreamReader {
    private char codigoB;
    private char codigoM;

    public ArcImsReader(InputStream in, char codigoB, char codigoM) {
        super(in);
        this.codigoB = codigoB;
        this.codigoM = codigoM;
    }

    /**
    * Read a single character.
    *
    * @return The character read, or -1 if the end of the stream has been
    *         reached
    *
    * @exception  IOException  If an I/O error occurs
    */
    public int read() throws IOException {
        int sup = super.read();

        if (sup == (int) codigoM) {
            return (int) codigoB;
        } else {
            return sup;
        }
    }

    /**
     * Read characters into a portion of an array.
     *
     * @param      cbuf     Destination buffer
     * @param      offset   Offset at which to start storing characters
     * @param      length   Maximum number of characters to read
     *
     * @return     The number of characters read, or -1 if the end of the
     *             stream has been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read(char[] cbuf, int off, int len) throws IOException {
        synchronized (lock) {
            int n = super.read(cbuf, off, len);

            for (int i = off; i < (off + n); i++) {
                char c = cbuf[i];

                if (c == codigoM) {
                    cbuf[i] = codigoB;
                }
            }

            return n;
        }
    }
}
