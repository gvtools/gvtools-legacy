package com.iver.cit.gvsig.fmap.layers;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

import org.geotools.data.ows.Layer;


public class GTLayerAdaptareToWMSLayerNode {


    public WMSLayerNode adapter(Layer layer, WMSLayerNode parent,
	    TreeMap<String, WMSLayerNode> layers) {
	WMSLayerNode node = new WMSLayerNode();
	node.setName(layer.getName());
	node.setTitle(layer.getTitle());
	// node.setTransparency(null); //TODO
	node.setSrs(new Vector(layer.getSrs()));
	node.setQueryable(layer.isQueryable());
	node.setParent(parent);
	node.setAbstract(layer.get_abstract());
	node.setScaleMax(layer.getScaleDenominatorMax());
	node.setScaleMin(layer.getScaleDenominatorMin());

	// TODO
	node.setFixedSize(0, 0);

	node.setLatLonBox("todo");

	if (layer.getKeywords() != null) {
	for (String keyword : layer.getKeywords()) {
	    node.addKeyword(keyword);
	}
	}

	// TODO: GT styles should be translated to WMStyles in an adapter class
	// like this one

	// TODO: Dimensions

	node.setChildren(new ArrayList<WMSLayerNode>());

	for (Layer children : layer.getChildren()) {
	    node.getChildren().add(adapter(children, node, layers));
	}

	if (node.getTitle() != null) {
	    layers.put(node.getTitle(), node);
	}

	return node;
    }


}
