package org.gvsig.graph.core;

/**
 * @author fjp Used inside nodes to avoid initialization of the whole network
 * 
 */
public class GlobalCounter {
	static int numSolucGlobal = 0;

	public static int getGlobalSolutionNumber() {
		return numSolucGlobal;
	}

	public static void setGlobalSolutionNumber(int numSolucGlobal) {
		GlobalCounter.numSolucGlobal = numSolucGlobal;
	}

	/**
	 * @return true if you need to initalize all nodes (every 65000 solutions
	 *         only) if true, do: for (nodeNum = 0; nodeNum <
	 *         graph.numVertices(); nodeNum++) { node =
	 *         graph.getNodeByID(nodeNum); node.initialize(); }
	 */
	public static boolean increment() {
		if (numSolucGlobal > 65000) {
			numSolucGlobal = -1;
			return true;
		} // for nodeNum */
		numSolucGlobal++;
		return false;

	}

}
