/* Generated By:JJTree: Do not edit this line. ASTSQLProductExpr.java */

package com.hardcode.gdbms.parser;

public class ASTSQLProductExpr extends SimpleNode {
  public ASTSQLProductExpr(int id) {
    super(id);
  }

  public ASTSQLProductExpr(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}