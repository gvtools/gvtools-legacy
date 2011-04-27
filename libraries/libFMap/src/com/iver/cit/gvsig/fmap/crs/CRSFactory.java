package com.iver.cit.gvsig.fmap.crs;

import org.cresques.cts.ICRSFactory;
import org.cresques.cts.IProjection;
import org.cresques.cts.ProjectionPool;

/**
 * Fabrica de CRS.
 * Centraliza las peticiones de creación de objetos CRS de todo fmap.
 * @author luisw
 *
 */
public class CRSFactory {
	public static ICRSFactory cp = new ProjectionPool();
	
	public static IProjection getCRS(String code) {
		return cp.get(code);
	}
}
