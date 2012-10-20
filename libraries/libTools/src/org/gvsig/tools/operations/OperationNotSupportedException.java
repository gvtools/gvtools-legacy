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
 * 2008 IVER T.I. S.A.   {{Task}}
 */

package org.gvsig.tools.operations;

import java.util.HashMap;
import java.util.Map;

import org.gvsig.tools.exception.BaseException;

public class OperationNotSupportedException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Generated serial version UID
	 */
	private static final String MESSAGE = "Operation Exception";
	private static final String MESSAGE_KEY = "_Exception_executing_the_operation_XoperationNameX_with_code_XoperationCodeX";
	private static final String FORMAT_STRING = "Operation %(operationName) with code %(operationCode) not supported.";

	private int operationCode = -1;
	private String operationName = "unknow";

	public OperationNotSupportedException(int operationCode,
			String operationName) {
		super(MESSAGE, MESSAGE_KEY, serialVersionUID);
		this.formatString = FORMAT_STRING;
		this.operationCode = operationCode;
		this.operationName = operationName;
	}

	public OperationNotSupportedException(int operationCode,
			String operationName, Throwable e) {
		super(MESSAGE, e, MESSAGE_KEY, serialVersionUID);
		this.formatString = FORMAT_STRING;
		this.operationCode = operationCode;
		this.operationName = operationName;
	}

	protected Map values() {
		Map params = new HashMap();
		params.put("operationCode", new Integer(this.operationCode));
		params.put("operationName", this.operationName);
		return params;
	}

}
