/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.gui;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.hardcode.gdbms.engine.instruction.Adapter;
import com.hardcode.gdbms.engine.instruction.Utilities;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando González Cortés
 */
public class GDBMSAdapterTreeModel extends DefaultTreeModel {
	/**
	 * DOCUMENT ME!
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public GDBMSAdapterTreeModel(TreeNode arg0, boolean arg1) {
		super(arg0, arg1);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param arg0
	 */
	public GDBMSAdapterTreeModel(TreeNode arg0) {
		super(arg0);
	}

	/**
	 * Crea un nuevo GDBMSAdapterTreeModel.
	 * 
	 * @param root
	 *            DOCUMENT ME!
	 */
	public GDBMSAdapterTreeModel(Adapter root) {
		super(null);
		this.setRoot(new AdapterNode(root));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param root
	 *            DOCUMENT ME!
	 */
	public void setTree(Adapter root) {
		this.setRoot(new AdapterNode(root));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @author Fernando González Cortés
	 */
	public class AdapterNode implements TreeNode {
		private Adapter node;

		/**
		 * Crea un nuevo AdapterNode.
		 * 
		 * @param n
		 *            DOCUMENT ME!
		 */
		public AdapterNode(Adapter n) {
			node = n;
		}

		/**
		 * @see javax.swing.tree.TreeNode#getChildCount()
		 */
		public int getChildCount() {
			return node.getChilds().length;
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
			return node.getChilds().length == 0;
		}

		/**
		 * @see javax.swing.tree.TreeNode#children()
		 */
		public Enumeration children() {
			Hashtable foo = new Hashtable();

			for (int i = 0; i < node.getChilds().length; i++) {
				foo.put(new AdapterNode(node.getChilds()[i]), "");
			}

			return foo.keys();
		}

		/**
		 * @see javax.swing.tree.TreeNode#getParent()
		 */
		public TreeNode getParent() {
			return new AdapterNode(node.getParent());
		}

		/**
		 * @see javax.swing.tree.TreeNode#getChildAt(int)
		 */
		public TreeNode getChildAt(int arg0) {
			return new AdapterNode(node.getChilds()[arg0]);
		}

		/**
		 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
		 */
		public int getIndex(TreeNode arg0) {
			for (int i = 0; i < node.getChilds().length; i++) {
				if (((AdapterNode) arg0).node == node) {
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

			return className + "(" + Utilities.getText(node.getEntity()) + ")";
		}
	}
}
