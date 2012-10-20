package com.iver.cit.gvsig.addlayer.fileopen.solve;

import java.io.File;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.FileNotFoundDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.addlayer.fileopen.solve.gui.FileNotFoundSolvePanel;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ISolveErrorListener;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

/**
 * Class to solve file not found error.
 * 
 * @author Vicente Caballero Navarro
 */
public class FileNotFoundSolve implements ISolveErrorListener {
	private FLayer layer = null;
	private Driver driver = null;
	private boolean solved = false;

	public String getException() {
		return FileNotFoundDriverException.class.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.layers.ISolveErrorListener#solve(com.iver.cit
	 * .gvsig.fmap.layers.FLayer, com.hardcode.driverManager.Driver)
	 */
	public FLayer solve(FLayer obj, Driver driver) {
		layer = (FLayer) obj;
		this.driver = driver;
		FileNotFoundSolvePanel fnfs = new FileNotFoundSolvePanel(this);
		PluginServices.getMDIManager().addWindow(fnfs);
		return layer;
	}

	/**
	 * @param file
	 */
	public void createLayer(File file) {
		layer = (FLyrVect) LayerFactory.createLayer(file.getName(),
				(VectorialFileDriver) driver, file, layer.getCrs());
		solved = true;
	}

	/**
	 * @return
	 */
	public Driver getDriver() {
		return driver;
	}

	/**
	 * @return
	 */
	public FLayer getLayer() {
		return layer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.layers.ISolveErrorListener#isSolved()
	 */
	public boolean isSolved() {
		return solved;
	}

}
