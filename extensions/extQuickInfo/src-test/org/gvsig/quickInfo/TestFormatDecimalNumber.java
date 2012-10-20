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

import javax.swing.JOptionPane;

import org.gvsig.quickInfo.i18n.Messages;

/**
 * <p>
 * Tests the method {@link FormatDecimalNumber FormatDecimalNumber}
 * </p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class TestFormatDecimalNumber {
	/**
	 * Tests the method {@link FormatDecimalNumbe#formatDecimal(double)
	 * FormatDecimalNumbe#formatDecimal(double)}
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			double num = 123412341234.123412351235123451235123;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");

			num = 0.00000123412341234123412351235123451235123;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");

			num = 123412341234123412351235123451235123.0;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");

			num = -123;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");

			num = 0.00123;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");

			num = -0.923;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");

			num = 0.0000225;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");

			num = 0.00000225;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");

			num = 1234567890;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");

			num = 1223456789;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");

			num = 12.2345678901E9;
			System.out.println(num);
			System.out.println(FormatDecimalNumber.formatDecimal(num));
			System.out.println("----------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					Messages.getText("An_exception_happened"),
					Messages.getText("Error"), JOptionPane.ERROR_MESSAGE);
		}

		return;
	}
}
