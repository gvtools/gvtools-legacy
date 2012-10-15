package com.iver.cit.gvsig.fmap.layers.order;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.Messages;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public class RasterPolLinePointOrderManager extends DefaultOrderManager {

	private static Logger logger =
		Logger.getLogger(RasterPolLinePointOrderManager.class.getName());
	
	private final String name = "Raster-Polygon-Line-Point order manager";
	private final String description =
		"Raster_to_bottom_then_polygons_lines_points";
	public static final String CODE =
		RasterPolLinePointOrderManager.class.getName();

	public int getPosition(FLayers target, FLayer newLayer) {
		
		int new_weight = 3;
		new_weight = getLayerWeight(newLayer);
		
		int len = target.getLayersCount();
		int item_w = 0;
		// from top to bottom,
		// look for a layer at
		// least as "heavy" as this one
		for (int i=(len-1); i>=0; i--) {
			item_w = getLayerWeight(target.getLayer(i));
			if (item_w >= new_weight) {
				return (i+1);
			}
		}
		// layer "falls" to bottom
		return 0;
	}

	public String getDescription() {
		return Messages.getString(description);
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return CODE;
	}
	
	public String getCode() {
		return CODE;
	}

	/**
	 * 
	 * @param lyr
	 * @return weight: point=0, line=1, polygon=2, other=3, raster=4
	 */
	private int getLayerWeight(FLayer lyr) {
		
		if (lyr.getClass().getName().indexOf("FLyrRasterSE") != -1) {
			return 4;
		} else {
			if (lyr instanceof FLyrVect) {
				FLyrVect lyrv = (FLyrVect) lyr;
				int type2d = FShape.POLYGON;
				try {
					type2d = simplifyType(lyrv.getShapeType());
				} catch (ReadDriverException e) {
					logger.error("While getting shape type from layer: " + lyrv.getName() + " (assumed polygon)");
				}
				switch (type2d) {
				case FShape.POLYGON:
					return 2;
				case FShape.LINE:
					return 1;
				case FShape.POINT:
					return 0;
				default:
					// should not reach this
					return 3;
				}
			} else {
				// other:
				return 3;
			}
		}
		
	}

	/**
	 * 
	 * @param t
	 * @return FShape.POLYGON, LINE or POINT
	 */
	private int simplifyType(int t) {

		int ty = t % FShape.Z;
		switch (ty) {
		case FShape.POLYGON:
		case FShape.MULTI:
		case FShape.NULL:
			return FShape.POLYGON;
		case FShape.LINE:
			return FShape.LINE;
		case FShape.POINT:
		case FShape.MULTIPOINT:
			return FShape.POINT;
		default:
			return FShape.POLYGON;
		}
		
	}
	


}
