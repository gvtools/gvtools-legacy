/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.parser.Node;
import com.hardcode.gdbms.parser.SQLEngineConstants;
import com.hardcode.gdbms.parser.SimpleNode;


/**
 * Adaptador
 *
 * @author Fernando González Cortés
 */
public class TableRefAdapter extends Adapter {
	private String name;
	private String alias;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Adapter#setEntity(com.hardcode.gdbms.parser.Node)
	 */
	public void setEntity(Node o) {
		super.setEntity(o);

		SimpleNode sn = (SimpleNode) o;

		if (sn.first_token.kind == SQLEngineConstants.STRING_LITERAL) {
			name = sn.first_token.image.substring(1);
			name = name.substring(0, name.length() - 1);
		} else {
			name = sn.first_token.image;
		}

		if (sn.last_token != sn.first_token) {
			alias = sn.last_token.image;
		}
	}

	/**
	 * Obtiene el alias de la tabla
	 *
	 * @return Returns the alias.
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Obtiene el nombre de la tabla
	 *
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
}
