package com.iver.cit.gvsig.fmap.layers;

import java.awt.Image;

import javax.swing.ImageIcon;

import com.hardcode.driverManager.DriverEvent;
import com.hardcode.driverManager.DriverEventListener;
import com.iver.cit.gvsig.fmap.MapControl;

public class DefaultDelayedDriverListener implements DriverEventListener {
	private final Image driverLoading = new ImageIcon(
			MapControl.class.getResource("images/waiting_ovr.gif")).getImage();
	private FLyrVect _lyr;

	public DefaultDelayedDriverListener(FLyrVect lyr) {
		_lyr = lyr;
		// _lyr.setCachingDrawnLayers(true);
		_lyr.getFLayerStatus().setDriverLoaded(false);
		_lyr.setTocStatusImage(driverLoading);

	}

	public void driverNotification(DriverEvent event) {
		_lyr.getFLayerStatus().setDriverLoaded(true);
		_lyr.setTocStatusImage(null);
		// _lyr.setCachingDrawnLayers(false);
		_lyr.getMapContext().invalidate();

	}

}
