package com.iver.cit.gvsig.fmap.layers.name;

import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;

/**
 * Utility class with methods to deal with layer naming. These methods are used
 * to prevent repeated layer names Indexes are used with the '-' character.
 * 
 * Apart from this te user can choose whether or not the file extension (for
 * example ".shp") is part of the layer name.
 * 
 * @author jldominguez
 * 
 */
public class LayerNameUtils {

	/**
	 * Find highest index among layer names starting with a given name (example:
	 * countries, countries-2, countries-1, result = 2)
	 * 
	 * @param layers
	 * @param name
	 *            base name
	 * @return
	 */
	public static long findHighestIndex(FLayers layers, String name) {

		ArrayList indexed = getIndexedLayers(layers);
		long max = 0;
		int len = indexed.size();
		Long long_aux = null;
		FLayer lyr = null;

		String it_name = null;
		String re_name = null;

		for (int i = 0; i < len; i++) {
			lyr = (FLayer) indexed.get(i);
			it_name = lyr.getName();
			long_aux = getIndexFromName(it_name);
			if (long_aux != null && long_aux.longValue() > max) {
				re_name = name + "-" + long_aux.longValue();
				if (re_name.compareToIgnoreCase(it_name) == 0) {
					max = long_aux.longValue();
				}

			}
		}
		return max;
	}

	/**
	 * Returns layers whose names are "*-<number>"
	 * 
	 * @param lyrs
	 * @return
	 */
	public static ArrayList getIndexedLayers(FLayers lyrs) {
		ArrayList resp = new ArrayList();

		if (lyrs.getName() != null && getIndexFromName(lyrs.getName()) != null) {
			resp.add(lyrs);
		}

		FLayer lyr = null;
		int len = lyrs.getLayersCount();
		for (int i = 0; i < len; i++) {
			lyr = lyrs.getLayer(i);
			if (lyr instanceof FLayers) {
				ArrayList al = getIndexedLayers((FLayers) lyr);
				resp.addAll(al);
			} else {
				if (getIndexFromName(lyr.getName()) != null) {
					resp.add(lyr);
				}
			}
		}
		return resp;
	}

	/**
	 * Remove index and '-' from layer name.
	 * 
	 * @param name
	 * @return
	 */
	public static String removeIndex(String name) {
		int last = name.lastIndexOf("-");

		if (name.length() == (last + 1)) {
			// ends with - not indexed
			return name;
		}

		String indx = name.substring(last + 1);
		try {
			// try to parse string after "-" as integer
			Long.parseLong(indx);
			// ok, remove index
			return name.substring(0, last);
		} catch (Exception ex) {
			// not valid, not index
			return name;
		}

	}

	public static Long getIndexFromName(String name) {
		int last = name.lastIndexOf("-");
		String aux = name.substring(last + 1);
		long r = 0;
		try {
			r = Long.parseLong(aux);
			return new Long(r);
		} catch (Exception ex) {
			return null;
		}
	}

	public static boolean nameExists(FLayers layers, String name) {

		if (layers == null) {
			return false;
		}

		if (layers.getName() != null
				&& layers.getName().compareToIgnoreCase(name) == 0) {
			return true;
		} else {

			int cnt = layers.getLayersCount();
			FLayer aux = null;
			for (int i = 0; i < cnt; i++) {
				aux = layers.getLayer(i);
				if (aux instanceof FLayers) {
					if (nameExists((FLayers) aux, name)) {
						return true;
					}
				} else {
					if (aux.getName().compareToIgnoreCase(name) == 0) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public static String normalizeName(String name) {

		String resp = name.trim();
		while (resp.indexOf("  ") != -1) {
			resp = resp.replace("  ", " ");
		}
		return resp;
	}

	public static String composeWithIndex(String name, long ind) {
		return name + "-" + ind;
	}

	// ======================================================
	// ======================================================
	// ======================================================

}
