/* Generated By:JJTree: Do not edit this line. ASTSQLInsert.java */

package com.hardcode.gdbms.parser;

public class ASTSQLInsert extends SimpleNode {
	public ASTSQLInsert(int id) {
		super(id);
	}

	public ASTSQLInsert(SQLEngine p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
