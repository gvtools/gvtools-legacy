package com.hardcode.gdbms.engine.instruction;

import java.util.HashMap;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.parser.ASTSQLAndExpr;
import com.hardcode.gdbms.parser.ASTSQLBetweenClause;
import com.hardcode.gdbms.parser.ASTSQLColRef;
import com.hardcode.gdbms.parser.ASTSQLCompareExpr;
import com.hardcode.gdbms.parser.ASTSQLCompareExprRight;
import com.hardcode.gdbms.parser.ASTSQLCompareOp;
import com.hardcode.gdbms.parser.ASTSQLCustom;
import com.hardcode.gdbms.parser.ASTSQLExistsClause;
import com.hardcode.gdbms.parser.ASTSQLFunction;
import com.hardcode.gdbms.parser.ASTSQLFunctionArgs;
import com.hardcode.gdbms.parser.ASTSQLInClause;
import com.hardcode.gdbms.parser.ASTSQLIsClause;
import com.hardcode.gdbms.parser.ASTSQLLValueElement;
import com.hardcode.gdbms.parser.ASTSQLLValueList;
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
import com.hardcode.gdbms.parser.ASTSQLProductExpr;
import com.hardcode.gdbms.parser.ASTSQLSelect;
import com.hardcode.gdbms.parser.ASTSQLSelectCols;
import com.hardcode.gdbms.parser.ASTSQLSelectList;
import com.hardcode.gdbms.parser.ASTSQLSumExpr;
import com.hardcode.gdbms.parser.ASTSQLTableList;
import com.hardcode.gdbms.parser.ASTSQLTableRef;
import com.hardcode.gdbms.parser.ASTSQLTerm;
import com.hardcode.gdbms.parser.ASTSQLUnaryExpr;
import com.hardcode.gdbms.parser.ASTSQLUnion;
import com.hardcode.gdbms.parser.ASTSQLWhere;
import com.hardcode.gdbms.parser.Node;
import com.hardcode.gdbms.parser.SimpleNode;
import com.hardcode.gdbms.parser.Token;


/**
 * Clase con distintos métodos de utilidad
 *
 * @author Fernando González Cortés
 */
public class Utilities {
	private static HashMap adapters = new HashMap();

	static {
		adapters.put(ASTSQLAndExpr.class, AndExprAdapter.class);
		adapters.put(ASTSQLCompareExpr.class, CompareExprAdapter.class);
		adapters.put(ASTSQLNotExpr.class, NotExprAdapter.class);
		adapters.put(ASTSQLOrExpr.class, OrExprAdapter.class);
		adapters.put(ASTSQLProductExpr.class, ProductExprAdapter.class);
		adapters.put(ASTSQLSelect.class, SelectAdapter.class);
		adapters.put(ASTSQLSumExpr.class, SumExprAdapter.class);
		adapters.put(ASTSQLTerm.class, TermAdapter.class);
		adapters.put(ASTSQLUnaryExpr.class, UnaryExprAdapter.class);
		adapters.put(ASTSQLUnion.class, UnionAdapter.class);
		adapters.put(ASTSQLCompareExprRight.class, CompareExprRigthAdapter.class);
		adapters.put(ASTSQLCompareOp.class, CompareOpAdapter.class);
		adapters.put(ASTSQLCustom.class, CustomAdapter.class);
		adapters.put(ASTSQLLikeClause.class, LikeClauseAdapter.class);
		adapters.put(ASTSQLInClause.class, InClauseAdapter.class);
		adapters.put(ASTSQLBetweenClause.class, BetweenClauseAdapter.class);
		adapters.put(ASTSQLIsClause.class, IsClauseAdapter.class);
		adapters.put(ASTSQLExistsClause.class, ExistsClauseAdapter.class);
		adapters.put(ASTSQLFunction.class, FunctionAdapter.class);
		adapters.put(ASTSQLFunctionArgs.class, FunctionArgsAdapter.class);
		adapters.put(ASTSQLLiteral.class, LiteralAdapter.class);
		adapters.put(ASTSQLColRef.class, ColRefAdapter.class);
		adapters.put(ASTSQLLvalue.class, LValueAdapter.class);
		adapters.put(ASTSQLLValueElement.class, LValueElementAdapter.class);
		adapters.put(ASTSQLLValueList.class, LValueListAdapter.class);
		adapters.put(ASTSQLLvalueTerm.class, LValueTermAdapter.class);
		adapters.put(ASTSQLOrderBy.class, OrderByAdapter.class);
		adapters.put(ASTSQLOrderByList.class, OrderByListAdapter.class);
		adapters.put(ASTSQLOrderByElem.class, OrderByElemAdapter.class);
		adapters.put(ASTSQLOrderDirection.class, OrderDirectionAdapter.class);
		adapters.put(ASTSQLSelectCols.class, SelectColsAdapter.class);
		adapters.put(ASTSQLSelectList.class, SelectListAdapter.class);
		adapters.put(ASTSQLTableList.class, TableListAdapter.class);
		adapters.put(ASTSQLTableRef.class, TableRefAdapter.class);
		adapters.put(ASTSQLWhere.class, WhereAdapter.class);
	}

	/**
	 * Obtienen el tipo de un nodo del arbol sintáctico de entrada en caso de
	 * que dicho nodo tenga un solo token. Si el nodo tiene varios token's se
	 * devuelve un -1
	 *
	 * @param n Nodo cuyo tipo se quiere conocer
	 *
	 * @return Tipo del token del nodo. Una constante de la interfaz
	 * 		   SQLEngineConstants
	 */
	public static int getType(Node n) {
		SimpleNode node = (SimpleNode) n;

		if (node.first_token == node.last_token) {
			return node.first_token.kind;
		}

		return -1;
	}

	/**
	 * Obtiene el texto de un nodo
	 *
	 * @param n Nodo del cual se quiere obtener el texto
	 *
	 * @return Texto del nodo
	 */
	public static String getText(Node n) {
		return getText((SimpleNode) n);
	}

	/**
	 * Obtiene el texto de un nodo
	 *
	 * @param s Nodo del cual se quiere obtener el texto
	 *
	 * @return Texto del nodo
	 */
	public static String getText(SimpleNode s) {
		String ret = "";

		for (Token tok = s.first_token; tok != s.last_token.next;
				tok = tok.next) {
			ret += (" " + tok.image);
		}

		return ret.trim();
	}

	/**
	 * Construye un arbol de adaptadores correspondiente al arbol sintáctico
	 * cuya raiz es el nodo que se pasa como parámetro. El árbol se construirá
	 * mientras se encuentren clases adaptadoras. En el momento que no se
	 * encuentre la clase adaptadora de un nodo no se seguirá profundizando
	 * por esa rama.   Despues de la construcción del arbol se invoca el
	 * método  calculateLiteralCondition de todos los adaptadores del arbol
	 * que sean instancias de Expression
	 *
	 * @param root Nodo raiz
	 * @param sql DOCUMENT ME!
	 * @param ds DOCUMENT ME!
	 *
	 * @return Adaptador raiz
	 */
	public static Adapter buildTree(Node root, String sql, DataSourceFactory ds) {
		Adapter rootAdapter = recursiveBuildTree(root);
		rootAdapter.setInstructionContext(new InstructionContext());
		rootAdapter.getInstructionContext().setSql(sql);
		rootAdapter.getInstructionContext().setDSFActory(ds);

		return rootAdapter;
	}

	/**
	 * Método recursivo para la creación del arbol de adaptadores
	 *
	 * @param root raiz del subárbol
	 *
	 * @return raiz del arbol creado o null si no se encuentra la clase
	 * 		   adaptadora
	 */
	private static Adapter recursiveBuildTree(Node root) {
		Adapter a;

		try {
			a = getAdapter(root);
		} catch (Exception e) {
			//e.printStackTrace();
			//No se encontró la clase adaptadora
			return null;
		}

		a.setEntity(root);

		Adapter[] childs = new Adapter[root.jjtGetNumChildren()];
		int index = 0;
		for (int i = 0; i < root.jjtGetNumChildren(); i++) {
			Adapter child = recursiveBuildTree(root.jjtGetChild(i));

			if (child != null) {
				child.setParent(a);

				//Se encontró la clase adaptadora
				childs[index] = child;
				index++;
			}
		}
		Adapter[] trueChilds = new Adapter[index];
		if (index != root.jjtGetNumChildren()) {
			System.arraycopy(childs, 0, trueChilds, 0, index);
			a.setChilds(trueChilds);
		} else {
		    a.setChilds(childs);
		}

		return a;
	}

	/**
	 * Obtiene una instancia nueva de la clase adaptadora de un nodo
	 *
	 * @param node nodo de cual se quiere obtener la clase adaptadora
	 *
	 * @return instancia de la clase adaptadora
	 *
	 * @throws InstantiationException Si no se puede instanciar la clase
	 * @throws IllegalAccessException Si no se puede acceder a la clase
	 */
	private static Adapter getAdapter(Node node)
		throws InstantiationException, IllegalAccessException {
		return (Adapter) ((Class) adapters.get(node.getClass())).newInstance();
	}

	/**
	 * Dada una clase devuelve el nombre de dicha clase sin el texto
	 * correspondiente al paquete
	 *
	 * @param clase Clase cuyo nombre se quiere conocer
	 *
	 * @return nombre de la clase
	 */
	private static String getClassName(Class clase) {
		String nombre = clase.getName();

		return nombre.substring(nombre.lastIndexOf('.') + 1);
	}

	/**
	 * Devuelve true si todas las expresiones que se pasan en el array son
	 * literales
	 *
	 * @param childs conjunto de adaptadores
	 *
	 * @return true si se cumple que para cada elemento del array childs que es
	 * 		   Expresion, es literal
	 */
	public static boolean checkExpressions(Adapter[] childs) {
		for (int i = 0; i < childs.length; i++) {
			if (!(childs[i] instanceof Expression)) {
				continue;
			}

			if (!((Expression) childs[i]).isLiteral()) {
				return false;
			}
		}

		return true;
	}

	/*
	 * Establece las tablas de la instrucción y la fuente de datos resultante
	 * de la cláusula from
	 *
	 * @param root raiz del arbol de adaptadores donde se aplicará el método
	 * @param tables tablas de la clausula from
	 * @param source fuente de datos de la que obtiene los valores los objetos
	 *                   field, resultado de la clausula from
	 *
	   public static void setTablesAndSource(Adapter root, DataSource[] tables,
	           DataSource source) {
	           if (root instanceof FieldSupport) {
	                   FieldSupport fs = (FieldSupport) root;
	                   fs.setDataSource(source);
	                   fs.setTables(tables);
	           }
	           Adapter[] hijos = root.getChilds();
	           for (int i = 0; i < hijos.length; i++) {
	                   setTablesAndSource(hijos[i], tables, source);
	           }
	   }*
	   public static void setTablesAndSource(Adapter root, DataSource table, DataSource source){
	           setTablesAndSource(root, new DataSource[]{table}, source);
	   }
	 */

	/**
	 * Simplifica las expresiones del árbol de adaptadores
	 *
	 * @param root raiz del arbol que se simplifica
	 */
	public static void simplify(Adapter root) {
		if (root instanceof Expression) {
			Expression ex = (Expression) root;
			ex.simplify();
		}

		Adapter[] hijos = root.getChilds();

		for (int i = 0; i < hijos.length; i++) {
			simplify(hijos[i]);
		}
	}
}
