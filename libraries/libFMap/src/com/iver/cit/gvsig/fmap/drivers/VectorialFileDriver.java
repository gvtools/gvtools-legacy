/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package com.iver.cit.gvsig.fmap.drivers;

import java.io.File;
import java.io.IOException;

import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;


/**
 * Interfaz a implementar por los drivers. El constructor no ha de tener
 * parámetros y ha de ser rápido, para las tareas de inicialización de la capa
 * se deberá de utilizar initialize.
 */
public interface VectorialFileDriver extends VectorialDriver {
    /**
     * Abre el fichero para una serie de operaciones.
     *
     * @param f Fichero sobre el que se va a operar
     *
     * @throws IOException Si se produce algún error
     */
    void open(File f) throws OpenDriverException;

    /**
     * Cuando se terminan las operaciones sobre el fichero se invoca éste
     * método para cerrar el descriptor que se abrió en f
     *
     * @throws IOException Si se produce algún error
     */
    void close() throws CloseDriverException;


    /**
     * Método invocado una sóla vez durante la ejecución justo antes
     * de visualizar una capa. En él se deben de hacer las inicializaciones
     * necesarias
     * @throws OpenDriverException
     *
     * @throws IOException Si se produce algún error
     */
    void initialize() throws ReadDriverException;

    /**
     * Define los tipos de fichero que puede leer el driver. Si devuelve true,
     * el fichero está aceptado (es de los que el driver puede leer), si
     * devuelve false es porque no lo puede leer.
     *
     * @param f Fichero
     *
     * @return boolean
     */
    boolean accept(File f);

    /**
     * Obtiene del fichero abierto en open la geometría index-ésima
     *
     * @param index Índice de la geometría que se quiere obtener
     *
     * @return IGeometry. Construida mediante llamadas a ShapeFactory
     *
     * @throws IOException Si se produce algún error
     */
    // IGeometry getShape(int index) throws IOException;

    /**
     * @return the original File that we are opening.
     */
    File getFile();


}
