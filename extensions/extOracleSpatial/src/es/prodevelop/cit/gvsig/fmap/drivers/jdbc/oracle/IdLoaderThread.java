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
package es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;



/**
 * Utility class to perform IDs load. in a different thread to prevent gvSIG's
 * GUI from blocking.
 *
 * @author jldominguez
 *
 */
public class IdLoaderThread extends Thread {
	
	private static Logger logger = Logger.getLogger(IdLoaderThread.class.getName());
    /**
     * Pointer to the driver.
     */
    private OracleSpatialDriver drv;
    // private ReadDriverException errorHappened = null;

    public IdLoaderThread(OracleSpatialDriver _drv) {
        drv = _drv;
        
    }

    /**
     * This method loads the IDs and then notifies the end of the load.
     */
    public void run() {
        try {
			drv.getMetaDataInThisThread();
	        drv.setNotAvailableYet(false);
	        
	        // errorHappened = null;
		} catch (SQLException e) {
			drv.setNotAvailableYet(true);
			logger.error("Caught exception while executing IDs thread: " + e.getMessage());
			logger.warn("Layer will appear as empty.");
			// errorHappened = new ReadDriverException(OracleSpatialDriver.NAME, e);
			// throw new RuntimeException(e.getMessage());
		}
        drv.notifyDriverEndLoaded();
    }


}
