package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.parser.Node;
import com.hardcode.gdbms.parser.SimpleNode;


/**
 * Clase base para todos los adaptadores de elementos del arbol sintáctico
 * generado por el parser a elementos descendientes de SelectInstruction
 *
 * @author Fernando González Cortés
 */
public class Adapter {
	private Adapter parent = null;
	private Adapter[] childs;
	private Node entity;
	private InstructionContext ic;

	/**
	 * set the context of the instruction being executed. Should be invoked on
	 * the root adapter to make all the adapter nodes have the same
	 * instruction context
	 *
	 * @param ic instruction context to set
	 */
	public void setInstructionContext(InstructionContext ic) {
		this.ic = ic;

		Adapter[] hijos = getChilds();

		for (int i = 0; i < hijos.length; i++) {
			hijos[i].setInstructionContext(ic);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public InstructionContext getInstructionContext() {
		return ic;
	}

	/**
	 * Establece la entidad del arbol sintáctico de la que es adaptador este
	 * objeto
	 *
	 * @param o Nodo de arbol sintáctico
	 */
	public void setEntity(Node o) {
		entity = o;
	}

	/**
	 * Obtiene la entidad del arbol sintáctico de la que es adaptador este
	 * objeto
	 *
	 * @return Nodo del arbol sintáctico
	 */
	public SimpleNode getEntity() {
		return (SimpleNode) entity;
	}

	/**
	 * Añade un hijo al adaptador
	 *
	 * @param a Adaptador hijo
	 */
	public void setChilds(Adapter[] a) {
		childs = a;
	}

	/**
	 * Obtiene el array de hijos del adaptador
	 *
	 * @return Array de hijos del adaptador. Si no existe ningún hijo se
	 * 		   retorna un array vacío
	 */
	public Adapter[] getChilds() {
		return childs;
	}

	/**
	 * Establece el padre del nodo en el arbol de adaptadores
	 *
	 * @param parent
	 */
	protected void setParent(Adapter parent) {
		this.parent = parent;
	}

	/**
	 * En los árboles de expresiones es común tener varios adaptadores que lo
	 * único que hacen es devolver el valor de su único hijo. Para evitar esto
	 * se pone al hijo en contacto directo con el padre invocando directamente
	 * este método
	 *
	 * @param child Hijo a sustituir
	 * @param newChild Hijo que reemplaza al anterior
	 */
	protected void replaceChild(Adapter child, Adapter newChild) {
		for (int i = 0; i < childs.length; i++) {
			if (childs[i] == child) {
				childs[i] = newChild;
			}
		}
	}

	/**
	 * Obtiene el padre de este adaptador en el arbol de adaptadores
	 *
	 * @return Returns the parent.
	 */
	public Adapter getParent() {
		return parent;
	}
}
