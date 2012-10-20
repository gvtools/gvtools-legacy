package com.iver.cit.gvsig.project.documents.layout.tools.listener;

import com.iver.cit.gvsig.fmap.core.adapter.GeometryAdapter;

/**
 * Iterfaz de los listeners con gráficos.
 * 
 * @author Vicente Caballero Navarro
 */
public interface ILayoutGraphicListener {
	public void endGraphic();

	public GeometryAdapter createGeometryAdapter();
}
