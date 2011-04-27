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
package es.prodevelop.cit.gvsig.arcims.fmap.drivers;

import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;


public interface ArcImsAlphanumericDataSource extends DataSource {
    /**
     * Deletes the ith row of the DataSource
     *
     * @param rowId index of the row to be deleted
     *
     * @throws DriverException if the row could not be deleted
     */
    public void deleteRow(long rowId) throws DriverException;

    /**
     * Inserts a row at the end of the dataware with the specified values
     *
     * @param values Values of the inserted row fields in the field order
     * @param index index where the row will be inserted. No data is lost.
     *
     * @throws DriverException if the row could not be inserted
     */
    public void insertFilledRowAt(long index, Value[] values)
        throws DriverException;

    /**
     * Inserts a row at the end of the dataware
     *
     * @param index index where the row will be inserted. No data is lost.
     *
     * @throws DriverException if the row could not be inserted
     */
    public void insertEmptyRowAt(long index) throws DriverException;
}
