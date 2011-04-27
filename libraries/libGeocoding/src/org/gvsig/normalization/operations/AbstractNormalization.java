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

import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gvsig.normalization.patterns.Datevalue;
import org.gvsig.normalization.patterns.Decimalvalue;
import org.gvsig.normalization.patterns.Element;
import org.gvsig.normalization.patterns.Fieldtype;
import org.gvsig.normalization.patterns.Integervalue;
import org.gvsig.normalization.patterns.NormalizationPattern;
import org.gvsig.normalization.patterns.Stringvalue;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;

/**
 * Abstract Normalization Class
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 */

public abstract class AbstractNormalization implements Normalization,
		ChangeListener {

	private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
	protected NormAlgorithm nAlgorithm = null;
	protected NormalizationPattern pattern = null;
	protected String[] nameNewFields = null;
	protected int[] posNameNewFields = null;

	/**
	 * Constructor
	 * 
	 * @param pat
	 *            pattern
	 */
	public AbstractNormalization(NormalizationPattern pat) {
		this.pattern = pat;
		this.nAlgorithm = new NormAlgorithm(pat);
		this.nAlgorithm.registerListener(this);
		loadNamesNewFields();
	}

	/**
	 * Get the pattern
	 * 
	 * @return pattern
	 */
	public NormalizationPattern getPattern() {
		return this.pattern;
	}

	/**
	 * Get the number of rows
	 * 
	 * @return number of rows
	 */
	public abstract long getRowCount();

	/**
	 * This method formats the date from a date pattern
	 * 
	 * @see http 
	 *      ://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html
	 * 
	 * @param date
	 * @param pattern
	 * @return Value
	 * @throws ParseException
	 */
	public Value formatDates(String date, String pattern) throws ParseException {

		Value result = ValueFactory.createNullValue();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date fecha = sdf.parse(date);
		result = ValueFactory.createValue(fecha);
		return result;
	}

	/**
	 * This method in its base implementations returns the same value as the
	 * rowCount method
	 * 
	 * @return number of rows estimated
	 */
	public long getEstimatedRowCount() {
		return getRowCount();
	}

	/**
	 * Tasks necessary before normalize
	 * 
	 * @return process ok
	 */
	public boolean preProcess() {
		return false;
	}

	/**
	 * Tasks necessary after normalize
	 * 
	 * @return process ok
	 */
	public boolean postProcess() {
		return false;
	}

	/**
	 * Get the number of the new imported fields
	 * 
	 * @return number of imported fields
	 */
	public int numberNewImportedFields() {

		int contImported = 0;
		int contTotal = this.pattern.getElements().size();

		for (int i = 0; i < contTotal; i++) {
			if (((Element) this.pattern.getElements().get(i)).getImportfield()) {
				contImported++;
			}
		}
		return contImported;
	}

	/**
	 * Register a listeners
	 * 
	 * @param l
	 *            listener
	 */
	public void registerListener(ChangeListener l) {
		this.listeners.add(l);
	}

	/**
	 * Remove a listener
	 * 
	 * @param l
	 *            listener
	 */
	public void removeListener(ChangeListener l) {
		this.listeners.remove(l);
	}

	/**
	 * Remove all listeners
	 */
	public void removeAllListeners() {
		this.nAlgorithm.removeAllListeners();
		this.listeners.clear();
	}

	/**
	 * Update the list listeners
	 * 
	 * @param evt
	 */
	public void update(ChangeEvent evt) {

		for (int i = 0; i < listeners.size(); i++) {
			((ChangeListener) listeners.get(i)).stateChanged(evt);
		}
	}

	/**
	 * Add a new message
	 * 
	 * @param message
	 */
	public void update(String message) {
		ChangeEvent evt = new ChangeEvent(message);
		update(evt);
	}

	/**
	 * Add new event
	 * 
	 * @param e
	 */
	public void stateChanged(ChangeEvent e) {
		update(e);
	}

	/**
	 * Delete all listeners
	 */
	public void clearConsoleInfo() {
		listeners.clear();
	}

	/**
	 * This method loads the names of the new fields and his position
	 */
	protected void loadNamesNewFields() {

		int co = this.pattern.getElements().size();
		List<String> elems = new ArrayList<String>();
		List<Integer> pos = new ArrayList<Integer>();
		for (int i = 0; i < co; i++) {
			Element elem = (Element) this.pattern.getElements().get(i);
			if (elem.getImportfield()) {
				elems.add(elem.getFieldname());
				pos.add(new Integer(i));
			}
		}
		int cou = elems.size();

		this.nameNewFields = new String[cou];
		this.posNameNewFields = new int[cou];
		String aux;
		int auxint;
		for (int i = 0; i < cou; i++) {
			aux = (String) elems.get(i);
			auxint = ((Integer) pos.get(i)).intValue();
			this.nameNewFields[i] = aux;
			this.posNameNewFields[i] = auxint;
		}
	}

	/**
	 * This method gets the Date format of a field from pattern
	 * 
	 * @param posi
	 * @return date formatted
	 */
	protected String getDateFormat(int posi) {
		String format = "";
		Element adres = (Element) this.pattern.getElements().get(posi);
		format = adres.getFieldtype().getDatevalue().getDatevalueformat();
		return format;
	}

	/**
	 * This method gets the field type from the pattern
	 * 
	 * @param adr
	 * @return field description
	 */
	protected FieldDescription createFieldDescFromPattern(Element adr) {

		FieldDescription fd = new FieldDescription();
		// Field Name and Field Alias
		fd.setFieldName(adr.getFieldname());
		fd.setFieldAlias(adr.getFieldname());

		// Field Type and particular width, precision
		Fieldtype nft = adr.getFieldtype();
		// Field type (STRING)
		if (((Stringvalue) nft.getStringvalue()) != null) {
			fd.setFieldType(Types.VARCHAR);
			Stringvalue strVal = ((Stringvalue) nft.getStringvalue());
			fd.setFieldLength(strVal.getStringvaluewidth());
		}
		// Field type (DATE)
		if (((Datevalue) nft.getDatevalue()) != null) {
			fd.setFieldType(Types.DATE);
			Datevalue dateVal = ((Datevalue) nft.getDatevalue());
			dateVal.getDatevalueformat().trim().length();
			fd.setFieldLength(dateVal.getDatevalueformat().trim().length());
		}
		// Field type (INTEGER)
		if (((Integervalue) nft.getIntegervalue()) != null) {
			fd.setFieldType(Types.INTEGER);
			Integervalue intVal = ((Integervalue) nft.getIntegervalue());
			fd.setFieldLength(intVal.getIntegervaluewidth());
		}
		// Field type (DOUBLE)
		if (((Decimalvalue) nft.getDecimalvalue()) != null) {
			fd.setFieldType(Types.DOUBLE);
			Decimalvalue decVal = ((Decimalvalue) nft.getDecimalvalue());
			fd.setFieldLength(decVal.getDecimalvalueint());
			fd.setFieldDecimalCount(decVal.getDecimalvaluedec());
		}
		return fd;
	}

	/**
	 * Create a new value
	 * 
	 * @param row
	 * @param tipoCampo
	 * @param posi
	 * @param cadena
	 * @return
	 */
	protected Value createValue(int row, int tipoCampo, int posi, String cadena) {
		Value val = ValueFactory.createNullValue();
		// DATE
		if (tipoCampo == Types.DATE) {
			try {
				String format = getDateFormat(posi);
				val = formatDates(cadena, format);
				return val;
			} catch (Exception e) {
				val = ValueFactory.createNullValue();
				update("ERROR.errorformattingdaterow." + (row + 1));
			}
		}
		// Integer
		if (tipoCampo == Types.INTEGER) {
			try {
				val = ValueFactory.createValueByType(cadena, tipoCampo);
				return val;
			} catch (Exception e) {
				val = ValueFactory.createNullValue();
				update("ERROR.errorformattingintegerrow." + (row + 1));
			}
		}
		// Decimal
		if (tipoCampo == Types.DOUBLE) {
			try {
				val = ValueFactory.createValueByType(cadena, tipoCampo);
				return val;
			} catch (Exception e) {
				val = ValueFactory.createNullValue();
				update("ERROR.errorformattingdecimalrow." + (row + 1));
			}
		}
		// String
		if (tipoCampo == Types.VARCHAR) {
			try {
				val = ValueFactory.createValueByType(cadena, tipoCampo);
				return val;
			} catch (Exception e) {
				val = ValueFactory.createNullValue();
				update("ERROR.errorformattingstringrow." + (row + 1));
			}
		}
		return val;
	}

}
