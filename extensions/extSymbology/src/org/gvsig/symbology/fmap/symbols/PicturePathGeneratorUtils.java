package org.gvsig.symbology.fmap.symbols;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

import com.iver.cit.gvsig.fmap.core.SymbologyFactory;

public class PicturePathGeneratorUtils {

	public static String getAbsoluteURLPath(String path) {
		try {
			return new URL(path).toExternalForm();
		} catch (MalformedURLException e) {
			File file = new File(path);
			if (!file.isAbsolute()) {
				file = new File(SymbologyFactory.SymbolLibraryPath, path);
			}
			try {
				return new URL("file", null, file.getAbsolutePath())
						.toExternalForm();
			} catch (MalformedURLException e1) {
				// Should never fail
				return path;
			}
		}
	}

	public static String getURLPath(String imagePath) {
		URL url;
		try {
			url = new URL(imagePath);
			if (url.getProtocol().equals("file")) {
				File file = new File(url.getFile());
				assert file.isAbsolute(); // Internally all paths are still
											// absolute
				URI relativeURI = new File(SymbologyFactory.SymbolLibraryPath)
						.toURI().relativize(file.toURI());
				try {
					return URLDecoder.decode(relativeURI.getPath(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// UTF-8 not supported?
					throw new AssertionError();
				}
			} else {
				return imagePath;
			}
		} catch (MalformedURLException e) {
			// PictureXXXSymbol.xxxImagePath is always created with
			// url.toExternalForm
			throw new AssertionError();
		}
	}
}
