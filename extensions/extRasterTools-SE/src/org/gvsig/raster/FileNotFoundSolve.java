package org.gvsig.raster;

import java.io.File;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.fmap.raster.layers.ISolveErrorListener;
import org.gvsig.raster.util.RasterToolsUtil;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.FileNotFoundDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.layers.FLayer;

/**
 * Implementación del gestor de errores en la carga de capas raster
 * 
 * 18/03/2009
 * @author Nacho Brodin nachobrodin@gmail.com
 */
public class FileNotFoundSolve implements ISolveErrorListener {
	private FLayer    layer   = null;
	private Driver    driver  = null;
	private boolean   solved  = false;
	//private File      file    = null;
		
	public String getException() {
		return FileNotFoundDriverException.class.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ISolveErrorListener#solve(com.iver.cit.gvsig.fmap.layers.FLayer, com.hardcode.driverManager.Driver)
	 */
	public FLayer solve(FLayer lyr, Driver driver) {
		layer = lyr;
		this.driver = driver;
		FileNotFoundSolvePanel fnfs = new FileNotFoundSolvePanel(this);
		PluginServices.getMDIManager().addWindow(fnfs);
		return getLayer();
	}
	/**
	 * @param file
	 */
	public void createLayer(File file){
		try {
			String lyr_name = RasterToolsUtil.getLayerNameFromFile(file);
			layer = FLyrRasterSE.createLayer(lyr_name, file.getAbsolutePath(), null);
		} catch (LoadLayerException e) {
			solved = false;
		}
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
	
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ISolveErrorListener#isSolved()
	 */
	public boolean isSolved() {
		return solved;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.fmap.raster.layers.ISolveErrorListener#getLayerName()
	 */
	public String getLayerName() {
		return layer.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.fmap.raster.layers.ISolveErrorListener#getPath()
	 */
	public String getPath() {
		if(((FLyrRasterSE)layer).getFile() != null)
			return ((FLyrRasterSE)layer).getFile().getAbsolutePath();
		return null;
	}

}
