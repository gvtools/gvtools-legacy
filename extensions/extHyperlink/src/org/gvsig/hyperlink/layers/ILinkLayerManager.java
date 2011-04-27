package org.gvsig.hyperlink.layers;

import java.awt.geom.Point2D;
import java.net.URI;

import com.iver.cit.gvsig.fmap.layers.FLayer;

public interface ILinkLayerManager  {
	public void setLayer(FLayer layer) throws IncompatibleLayerException;
	
	public FLayer getLayer();
	//public AbstractLinkProperties getLinkProperties();
	
	public URI[] getLink(Point2D point, double tolerance, String fieldName, String fileExtension);
	
	public URI[][] getLink(Point2D point, double tolerance, String[] fieldName, String fileExtension);

	public String[] getFieldCandidates();
}
