/* Generated By:JJTree: Do not edit this line. ASTSQLColRef.java */

package com.hardcode.gdbms.parser;

public class ASTSQLColRef extends SimpleNode {
	public ASTSQLColRef(int id) {
		super(id);
	}

	public ASTSQLColRef(SQLEngine p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
