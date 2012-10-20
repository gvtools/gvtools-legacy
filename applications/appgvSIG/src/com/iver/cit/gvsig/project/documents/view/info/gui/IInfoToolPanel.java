package com.iver.cit.gvsig.project.documents.view.info.gui;

import com.iver.cit.gvsig.fmap.layers.layerOperations.XMLItem;

/**
 * Interface that must implement the panels provided from a layer to display its
 * Feature Information
 * 
 * @author laura
 * 
 */
public interface IInfoToolPanel {

	public void show(String s);

	public void refreshSize();

	public void show(XMLItem item);
}
