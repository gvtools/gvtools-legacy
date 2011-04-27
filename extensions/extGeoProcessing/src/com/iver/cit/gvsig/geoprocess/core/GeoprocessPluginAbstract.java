package com.iver.cit.gvsig.geoprocess.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import com.iver.andami.PluginServices;

public abstract class GeoprocessPluginAbstract implements IGeoprocessPlugin {

	public URL getHtmlDescription() {
		Locale locale = Locale.getDefault();
		String localeStr = locale.getLanguage();
//		String urlStr = "resources/description_" +
//										localeStr +
//											".html";
//

		String urlBase =
			PluginServices.getPluginServices(this).getPluginDirectory() +
			"/docs/"+ this.getClass().getName() +
			"/description_%lang%.html";




		File htmlFile = new File(urlBase.replaceAll("%lang%", localeStr));
		if (!htmlFile.exists()){
			// for languages used in Spain, fallback to Spanish if their translation is not available
			if (localeStr.equals("ca")||localeStr.equals("gl")||localeStr.equals("eu")||localeStr.equals("va")) {
				localeStr = "es";
				htmlFile = new File(urlBase.replaceAll("%lang%", localeStr));

				if (htmlFile.exists())
					try {
						return htmlFile.toURL();
					} catch (MalformedURLException e) {
						return null;
					}
			}

			// as a last resort, fallback to English
			localeStr = "en";
			htmlFile = new File(urlBase.replaceAll("%lang%", localeStr));
		}
		try {
			return htmlFile.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

}
