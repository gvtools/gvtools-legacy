package org.gvsig.quickInfo;

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

import java.text.DecimalFormat;

import org.gvsig.quickInfo.tools.QuickInfoListener;

/**
 * <p>Class that has a method to format decimal numbers with the criterions
 *  to display them as calculated fields in the {@link QuickInfoListener QuickInfoListener} tool.</p>
 *
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class FormatDecimalNumber {
    /**
     * <p>Formats a decimal number according some criterions:
     *  <ul>
     *   <li>If number has more than 10 digits: uses scientific notation.</li>
     *   <li>If number has more than 5 0's before the first non-zero after the comma: uses scientific notation.</li>
     *   <li>Otherwise uses normal notation.</li>
     *   <li>Uses 6 decimal digits as much.</li>
     *  </ul>
     * </p>
     * 
     * @param number the decimal number to be formatted
     * @return the decimal number formatted
     */
    public static String formatDecimal(double number) {
    	DecimalFormat dFormat = null; 

    	double abs_number = Math.abs(number);

    	if ((abs_number >= 10E9) || (abs_number <= 10E-6)) {
        	dFormat = new DecimalFormat("0.######E0");
        	dFormat.setGroupingUsed(false);
    		return dFormat.format(number);
    	}

    	dFormat = new DecimalFormat("0.######");
    	dFormat.setGroupingUsed(false);
		return dFormat.format(number);
    }

    public static String formatDecimal(double number, String base, boolean superIndex) {
    	String f_number = formatDecimal(number);
    	
    	String[] parts = f_number.split("E");
    	
    	if (parts.length == 2) {
    		String exp = parts[1];
    		
    		if (superIndex)
    			exp = "<sup>" + exp + "</sup>";
    		
    		f_number = parts[0] + base + exp;
    	}
    	System.out.println(f_number);
    	return f_number;
    }
}
