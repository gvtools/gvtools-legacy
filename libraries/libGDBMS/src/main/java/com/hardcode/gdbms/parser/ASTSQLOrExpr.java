/* Generated By:JJTree: Do not edit this line. ASTSQLOrExpr.java */

package com.hardcode.gdbms.parser;

public class ASTSQLOrExpr extends SimpleNode {
  public ASTSQLOrExpr(int id) {
    super(id);
  }

  public ASTSQLOrExpr(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
