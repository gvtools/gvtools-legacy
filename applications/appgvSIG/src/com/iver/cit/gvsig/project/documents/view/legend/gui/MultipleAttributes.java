package com.iver.cit.gvsig.project.documents.view.legend.gui;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public class MultipleAttributes extends AbstractParentPanel {

	public String getDescription() {
		return null;
	}

	public String getTitle() {
		return PluginServices.getText(this, "multiple_atributes");
	}

	public boolean isSuitableFor(FLayer layer) {
		return layer instanceof FLyrVect;
	}
}
