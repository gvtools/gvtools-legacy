/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.gui;

import com.hardcode.gdbms.engine.instruction.Utilities;
import com.hardcode.gdbms.parser.SimpleNode;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class GDBMSParseTreeModel extends DefaultTreeModel {
	/**
	 * DOCUMENT ME!
	 *
	 * @param arg0
	 * @param arg1
	 */
	public GDBMSParseTreeModel(TreeNode arg0, boolean arg1) {
		super(arg0, arg1);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param arg0
	 */
	public GDBMSParseTreeModel(TreeNode arg0) {
		super(arg0);
	}

	/**
	 * Crea un nuevo GDBMSParseTreeModel.
	 *
	 * @param root DOCUMENT ME!
	 */
	public GDBMSParseTreeModel(SimpleNode root) {
		super(null);
		this.setRoot(new ParseNode(root));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param root DOCUMENT ME!
	 */
	public void setTree(SimpleNode root) {
		this.setRoot(new ParseNode(root));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @author Fernando González Cortés
	 */
	public class ParseNode implements TreeNode {
		private SimpleNode node;

		/**
		 * Crea un nuevo ParseNode.
		 *
		 * @param n DOCUMENT ME!
		 */
		public ParseNode(SimpleNode n) {
			node = n;
		}

		/**
		 * @see javax.swing.tree.TreeNode#getChildCount()
		 */
		public int getChildCount() {
			return node.jjtGetNumChildren();
		}

		/**
		 * @see javax.swing.tree.TreeNode#getAllowsChildren()
		 */
		public boolean getAllowsChildren() {
			return true;
		}

		/**
		 * @see javax.swing.tree.TreeNode#isLeaf()
		 */
		public boolean isLeaf() {
			return node.jjtGetNumChildren() == 0;
		}

		/**
		 * @see javax.swing.tree.TreeNode#children()
		 */
		public Enumeration children() {
			Hashtable foo = new Hashtable();

			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				foo.put(new ParseNode((SimpleNode) node.jjtGetChild(i)), "");
			}

			return foo.keys();
		}

		/**
		 * @see javax.swing.tree.TreeNode#getParent()
		 */
		public TreeNode getParent() {
			return new ParseNode((SimpleNode) node.jjtGetParent());
		}

		/**
		 * @see javax.swing.tree.TreeNode#getChildAt(int)
		 */
		public TreeNode getChildAt(int arg0) {
			return new ParseNode((SimpleNode) node.jjtGetChild(arg0));
		}

		/**
		 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
		 */
		public int getIndex(TreeNode arg0) {
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				if (((ParseNode) arg0).node == node) {
					return i;
				}
			}

			return -1;
		}

		/**
		 * DOCUMENT ME!
		 *
		 * @return DOCUMENT ME!
		 */
		public String toString() {
			String className = node.getClass().getName();
			className = className.substring(className.lastIndexOf("."));

			return className + "(" + Utilities.getText(node) + ")";
		}
	}
}
