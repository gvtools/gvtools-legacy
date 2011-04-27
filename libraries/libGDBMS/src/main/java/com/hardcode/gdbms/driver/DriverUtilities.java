/*
 * Created on 15-sep-2005
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, 
 USA.
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
 * $Id: DriverUtilities.java 3556 2006-01-09 18:18:20Z fjp $
 * $Log$
 * Revision 1.2  2006-01-09 18:15:06  fjp
 * Solucionar un fallo detectado por Jaume que hacía que no se escribieran archivos de más de 100 KBytes.
 *
 */
package com.hardcode.gdbms.driver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Utility method for the drivers
 */
public class DriverUtilities {
    /**
     * Copies the contents of the fcin FileChannel to the fcout FileChannel
     * 
     * @param fcin
     * @param fcout
     * 
     * @throws IOException
     */
    public static void copy(FileChannel fcin, FileChannel fcout)
            throws IOException {
        
        // Esto antes tenía un problema de que solo copiaba
        // los primeros 100 KBytes. Ahora le hemos puesto
        // un while y hemos comprobado que copia todo el fichero.
        // Ole tus huevos Luis, que tenga que comentar esto
        // y ver cómo está el resto del código.
        ByteBuffer buffer = ByteBuffer.allocate(102400);
        buffer.clear();
        int r = 0;
        int position = 0;
        while ((r = fcin.read(buffer, position)) != -1)
        {
            buffer.flip();
            int bytesWritten = fcout.write(buffer, position);
            position += bytesWritten;
            buffer.clear();
        }
    }
    
}
