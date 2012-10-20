/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 * 2008 Prodevelop S.L. main development
 */

package org.gvsig.normalization.operations;

import java.io.IOException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gvsig.normalization.patterns.NormalizationPattern;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.parser.ParseException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;

/**
 * Interface of Normalization
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicente Sanjaime Calvet</a>
 * 
 */
public interface Normalization {

	/**
	 * This method returns the Normalization pattern
	 * 
	 * @return Normalization pattern
	 */
	public NormalizationPattern getPattern();

	/**
	 * This method returns the number of row to normalize
	 * 
	 * @return rows number
	 */
	public long getRowCount();

	/**
	 * This method returns the estimated number of row to normalize for the GUI
	 * progress bar
	 * 
	 * @return estimated rows number for progress bar
	 */
	public long getEstimatedRowCount();

	/**
	 * This method fills one row normalized
	 * 
	 * @param row
	 * @throws DriverIOException
	 * @throws IOException
	 * @throws ParseException
	 * @throws DriverException
	 * @throws ExpansionFileReadException
	 * @throws ReadDriverException
	 * @throws ValidateRowException
	 */
	public void fillRow(int row) throws DriverIOException, IOException,
			ParseException, DriverException, ExpansionFileReadException,
			ReadDriverException, ValidateRowException;

	/**
	 * Tasks necessary before normalize
	 * 
	 * @return process ok
	 */
	public boolean preProcess();

	/**
	 * Tasks necessary after normalize
	 * 
	 * @return process ok
	 */
	public boolean postProcess();

	/**
	 * Information on whether it will create a new table
	 * 
	 * @return
	 */
	public boolean createNewTable();

	/**
	 * Register listener
	 * 
	 * @param l
	 */
	public void registerListener(ChangeListener l);

	/**
	 * Remove listener
	 * 
	 * @param l
	 */
	public void removeListener(ChangeListener l);

	/**
	 * 
	 * @param evt
	 */
	public void update(ChangeEvent evt);

	/**
	 * Add new messages
	 * 
	 * @param message
	 */
	public void update(String message);

	/**
	 * Remove all listeners
	 */
	public void removeAllListeners();
}
