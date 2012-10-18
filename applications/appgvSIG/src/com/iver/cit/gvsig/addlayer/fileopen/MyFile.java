package com.iver.cit.gvsig.addlayer.fileopen;

import java.awt.geom.Rectangle2D;
import java.io.File;

import com.iver.cit.gvsig.addlayer.AddLayerDialog;
import com.iver.cit.gvsig.fmap.MapControl;

/**
 * @version 05/09/2007
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
	public class MyFile {
		private File      f;
		private String    driverName;
		private IFileOpen fileOpen;

		public MyFile(File f, String driverName, IFileOpen fileOpen) {
			this.f = f;
			this.driverName = driverName;
			this.fileOpen = fileOpen;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return f.getName();
		}

		public String getDriverName() {
			return driverName;
		}

		public IFileOpen getFileOpen() {
			return fileOpen;
		}

		public Rectangle2D createLayer(MapControl mapControl) {
			return fileOpen.createLayer(f, mapControl, AddLayerDialog.getLastCrs());
		}
		public File getFile(){
			return f;
		}
	}
