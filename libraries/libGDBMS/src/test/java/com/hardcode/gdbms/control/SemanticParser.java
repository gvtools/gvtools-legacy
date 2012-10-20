package com.hardcode.gdbms.control;

import java.util.Stack;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.hardcode.gdbms.parser.ASTSQLAndExpr;
import com.hardcode.gdbms.parser.ASTSQLBetweenClause;
import com.hardcode.gdbms.parser.ASTSQLColRef;
import com.hardcode.gdbms.parser.ASTSQLCompareExpr;
import com.hardcode.gdbms.parser.ASTSQLCompareExprRight;
import com.hardcode.gdbms.parser.ASTSQLCompareOp;
import com.hardcode.gdbms.parser.ASTSQLCustom;
import com.hardcode.gdbms.parser.ASTSQLDelete;
import com.hardcode.gdbms.parser.ASTSQLExistsClause;
import com.hardcode.gdbms.parser.ASTSQLFunction;
import com.hardcode.gdbms.parser.ASTSQLFunctionArgs;
import com.hardcode.gdbms.parser.ASTSQLGroupBy;
import com.hardcode.gdbms.parser.ASTSQLInClause;
import com.hardcode.gdbms.parser.ASTSQLInsert;
import com.hardcode.gdbms.parser.ASTSQLIsClause;
import com.hardcode.gdbms.parser.ASTSQLLValueElement;
import com.hardcode.gdbms.parser.ASTSQLLValueList;
import com.hardcode.gdbms.parser.ASTSQLLeftJoinClause;
import com.hardcode.gdbms.parser.ASTSQLLikeClause;
import com.hardcode.gdbms.parser.ASTSQLLiteral;
import com.hardcode.gdbms.parser.ASTSQLLvalue;
import com.hardcode.gdbms.parser.ASTSQLLvalueTerm;
import com.hardcode.gdbms.parser.ASTSQLNotExpr;
import com.hardcode.gdbms.parser.ASTSQLOrExpr;
import com.hardcode.gdbms.parser.ASTSQLOrderBy;
import com.hardcode.gdbms.parser.ASTSQLOrderByElem;
import com.hardcode.gdbms.parser.ASTSQLOrderByList;
import com.hardcode.gdbms.parser.ASTSQLOrderDirection;
import com.hardcode.gdbms.parser.ASTSQLPattern;
import com.hardcode.gdbms.parser.ASTSQLProductExpr;
import com.hardcode.gdbms.parser.ASTSQLRightJoinClause;
import com.hardcode.gdbms.parser.ASTSQLSelect;
import com.hardcode.gdbms.parser.ASTSQLSelectCols;
import com.hardcode.gdbms.parser.ASTSQLSelectList;
import com.hardcode.gdbms.parser.ASTSQLStatement;
import com.hardcode.gdbms.parser.ASTSQLSumExpr;
import com.hardcode.gdbms.parser.ASTSQLTableList;
import com.hardcode.gdbms.parser.ASTSQLTableRef;
import com.hardcode.gdbms.parser.ASTSQLTerm;
import com.hardcode.gdbms.parser.ASTSQLUnaryExpr;
import com.hardcode.gdbms.parser.ASTSQLUnion;
import com.hardcode.gdbms.parser.ASTSQLUpdate;
import com.hardcode.gdbms.parser.ASTSQLUpdateAssignment;
import com.hardcode.gdbms.parser.ASTSQLWhere;
import com.hardcode.gdbms.parser.Node;
import com.hardcode.gdbms.parser.SQLEngineVisitor;
import com.hardcode.gdbms.parser.SimpleNode;
import com.hardcode.gdbms.parser.Token;

/**
 * Se encarga de obtener una estructura de datos que defina la instrucción SQL
 * en términos de DataSources y demás estructuras internas a partir del árbol
 * obtenido en el análisis sintáctico
 * 
 * @author Fernando González Cortés
 */
public class SemanticParser implements SQLEngineVisitor {
	private Stack childs = new Stack();
	private int current = -1;
	private Stack infos = new Stack();
	private ContentHandler handler;

	/**
	 * Obtiene el texto del nodo
	 * 
	 * @param s
	 *            Nodo del cual se quiere obtener el texto
	 * 
	 * @return String
	 */
	private String getText(SimpleNode s) {
		String ret = "";

		for (Token tok = s.first_token; tok != s.last_token.next; tok = tok.next) {
			ret += (" " + tok.image);
		}

		return ret;
	}

	/**
	 * Obtiene el nombre de la clase a partir de dicha clase. El nombre no
	 * cualificado, es decir, sin el paquete
	 * 
	 * @param clase
	 *            Clase de la cual se quiere obtener el nombre
	 * 
	 * @return String con el nombre
	 */
	private String getClassName(Class clase) {
		String name = clase.getName();

		return name.substring(name.lastIndexOf('.') + 1);
	}

	/**
	 * Lanza un evento SAX de startElement con la información del nodo que se
	 * pasa como parametro
	 * 
	 * @param node
	 *            nodo del arbol sintáctico
	 */
	private void start(SimpleNode node) {

		int numChilds = node.jjtGetNumChildren();

		childs.push(new Integer(current));
		infos.push(node);

		current = numChilds;

		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "image", "image", "CDATA", getText(node));

		if (node.first_token == node.last_token) {
			atts.addAttribute("", "type", "type", "CDATA", ""
					+ node.first_token.kind);
		}

		try {
			handler.startElement("", getClassName(node.getClass()),
					getClassName(node.getClass()), atts);
		} catch (SAXException e) {
		}

		if (numChilds == 0) {
			end();
		}
	}

	/**
	 * metodo que procesa un nodo
	 * 
	 * @param node
	 *            nodo a procesar
	 */
	private void process(SimpleNode node) {
		start(node);
	}

	/**
	 * Lanza un evento SAX de endElement con la información del nodo que se
	 * encuentra encima de la pila
	 */
	private void end() {
		current--;

		if (current <= 0) {
			current = ((Integer) childs.pop()).intValue();

			Node node = (Node) infos.pop();

			try {
				handler.endElement("", getClassName(node.getClass()),
						getClassName(node.getClass()));
			} catch (SAXException e) {
			}

			if (!childs.isEmpty()) {
				end();
			}
		}
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.SimpleNode,
	 *      java.lang.Object)
	 */
	public Object visit(SimpleNode node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLAndExpr,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLAndExpr node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLBetweenClause,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLBetweenClause node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLColRef,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLColRef node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLCompareExpr,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLCompareExpr node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLCompareExprRight,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLCompareExprRight node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLCompareOp,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLCompareOp node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLDelete,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLDelete node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLExistsClause,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLExistsClause node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLFunction,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLFunction node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLFunctionArgs,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLFunctionArgs node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLGroupBy,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLGroupBy node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLInClause,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLInClause node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLInsert,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLInsert node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLIsClause,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLIsClause node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLLeftJoinClause,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLLeftJoinClause node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLLikeClause,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLLikeClause node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLLiteral,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLLiteral node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLLvalue,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLLvalue node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLLvalueTerm,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLLvalueTerm node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLNotExpr,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLNotExpr node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLOrderBy,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLOrderBy node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLOrderByElem,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLOrderByElem node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLOrderByList,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLOrderByList node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLOrderDirection,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLOrderDirection node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLOrExpr,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLOrExpr node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLPattern,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLPattern node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLProductExpr,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLProductExpr node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLRightJoinClause,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLRightJoinClause node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLSelect,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLSelect node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLSelectCols,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLSelectCols node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLSelectList,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLSelectList node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLStatement,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLStatement node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLSumExpr,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLSumExpr node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLTableList,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLTableList node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLTableRef,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLTableRef node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLTerm,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLTerm node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLUnaryExpr,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLUnaryExpr node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLUpdate,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLUpdate node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLUpdateAssignment,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLUpdateAssignment node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLLValueElement,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLLValueElement node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLLValueList,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLLValueList node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLWhere,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLWhere node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see org.xml.sax.XMLReader#getContentHandler()
	 */
	public ContentHandler getContentHandler() {
		return handler;
	}

	/**
	 * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
	 */
	public void setContentHandler(ContentHandler arg0) {
		handler = arg0;
	}

	/**
	 * Hace que este visitor sp visite el nodo que se pasa como parámetro y cada
	 * uno de sus hijos
	 * 
	 * @param root
	 *            nodo raiz que se va a visitar
	 * 
	 * @throws SAXException
	 *             DOCUMENT ME!
	 */
	public void parse(Node root) throws SAXException {
		handler.startDocument();
		parsing(root);
		handler.endDocument();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param root
	 *            DOCUMENT ME!
	 * 
	 * @throws SAXException
	 *             DOCUMENT ME!
	 */
	private void parsing(Node root) throws SAXException {
		root.jjtAccept(this, null);

		for (int i = 0; i < root.jjtGetNumChildren(); i++) {
			parsing(root.jjtGetChild(i));
		}
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLUnion,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLUnion node, Object data) {
		process(node);

		return null;
	}

	/**
	 * @see com.hardcode.gdbms.parser.SQLEngineVisitor#visit(com.hardcode.gdbms.parser.ASTSQLCustom,
	 *      java.lang.Object)
	 */
	public Object visit(ASTSQLCustom node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}
}
