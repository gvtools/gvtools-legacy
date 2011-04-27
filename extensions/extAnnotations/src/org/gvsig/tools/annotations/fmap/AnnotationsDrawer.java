package org.gvsig.tools.annotations.fmap;

import com.iver.cit.gvsig.fmap.DefaultMapContextDrawer;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.FLayer;

public class AnnotationsDrawer extends DefaultMapContextDrawer {
	private Annotation_Layer targetLayer = null;

	@Override
	protected boolean isLayerCacheable(FLayer layer) {
		if (targetLayer!=null && layer==targetLayer) {	
			return true;
		}
		else {
			return false;
		}
	}

	public void setTargetLayer(Annotation_Layer targetLayer) {
		this.targetLayer = targetLayer;
	}

	public Annotation_Layer getTargetLayer() {
		return targetLayer;
	}
}
