/* Generated By:JJTree: Do not edit this line. ASTSQLDelete.java */

package com.hardcode.gdbms.parser;

public class ASTSQLDelete extends SimpleNode {
	public ASTSQLDelete(int id) {
		super(id);
	}

	public ASTSQLDelete(SQLEngine p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
