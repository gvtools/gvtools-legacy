/* Generated By:JJTree: Do not edit this line. ASTSQLNotExpr.java */

package com.hardcode.gdbms.parser;

public class ASTSQLNotExpr extends SimpleNode {
	public ASTSQLNotExpr(int id) {
		super(id);
	}

	public ASTSQLNotExpr(SQLEngine p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
