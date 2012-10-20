/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ib��ez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integraci�n de Tecnolog�as SL
 *   Conde Salvatierra de �lava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */

package org.gvsig.remoteClient.arcims.utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.ArcImsStatus;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;

/**
 * These download methods derive from those in
 * <tt>org.gvsig.remoteClient.arcims.utils.Utilities<tt>
 * and have been modified to prevent multiple downloads of the same image when
 * using an ArcIMS server, because the remote image's URL is not the
 * same when requesting the same map twice.
 *
 * @author jldominguez
 *
 */

/**
 * @author jsanz
 * 
 */
public class ArcImsDownloadUtils {
	private static Logger logger = Logger.getLogger(ArcImsDownloadUtils.class
			.getName());
	private static Hashtable downloadedFiles;
	private static final String tempDirectoryPath = System
			.getProperty("java.io.tmpdir") + "/tmp-andami";

	/**
	 * Returns the content of this URL as a file from the file system.<br>
	 * <p>
	 * If the URL has been already downloaded in this session and notified to
	 * the system using the static <b>Utilities.addDownloadedURL(URL)</b>
	 * method, it can be restored faster from the file system avoiding to
	 * download it again.
	 * </p>
	 * 
	 * @param virtualUrl
	 * @param override
	 *            If false, the file won't be downloaded
	 * @return File containing this URL's content or null if no file was found.
	 */
	private static File getPreviousDownloadedURL(URL virtualUrl,
			boolean override) {
		File f = null;

		if ((downloadedFiles != null)
				&& downloadedFiles.containsKey(virtualUrl) && !override) {
			String filePath = (String) downloadedFiles.get(virtualUrl);
			f = new File(filePath);
		}

		return f;
	}

	/**
	 * Returns the content of this URL as a file from the file system.<br>
	 * <p>
	 * If the URL has been already downloaded in this session and notified to
	 * the system using the static <b>Utilities.addDownloadedURL(URL)</b>
	 * method, it can be restored faster from the file system avoiding to
	 * download it again.
	 * </p>
	 * This overrided method doesn't require the boolean parameter
	 * 
	 * @see #getPreviousDownloadedURL(URL, boolean)
	 * @param virtualUrl
	 * @return File containing this URL's content or null if no file was found.
	 */
	private static File getPreviousDownloadedURL(URL virtualUrl) {
		return getPreviousDownloadedURL(virtualUrl, false);
	}

	/**
	 * Downloads an URL into a temporary file that is removed the next time the
	 * tempFileManager class is called, which means the next time gvSIG is
	 * launched.
	 * 
	 * @param url
	 * @param virtualURL
	 *            The virtual URL that identifies the file in the internal cache
	 * @param filePath
	 *            Where the file will be downloaded
	 * @return File object of the downloaded file
	 * @throws IOException
	 * @throws ServerErrorResponseException
	 * @throws ConnectException
	 * @throws UnknownHostException
	 */
	public static File downloadFile(URL url, URL virtualURL, String filePath)
			throws IOException, ConnectException, UnknownHostException {
		File f = null;
		long t1 = System.currentTimeMillis();

		try {
			if ((f = getPreviousDownloadedURL(virtualURL)) == null) {
				long t3 = System.currentTimeMillis();
				File tempDirectory = new File(tempDirectoryPath);

				if (!tempDirectory.exists()) {
					tempDirectory.mkdir();
				}

				// f = new File(tempDirectoryPath + "/" + filePath);
				String fName = normalizeFileName(filePath);
				f = new File(tempDirectoryPath + "/" + fName);

				logger.info("downloading '" + url.toString() + "' to: "
						+ f.getAbsolutePath());

				f.deleteOnExit();

				DataOutputStream dos = new DataOutputStream(
						new BufferedOutputStream(new FileOutputStream(f)));
				byte[] buffer = new byte[1024 * 256];
				InputStream is = url.openStream();
				long readed = 0;

				for (int i = is.read(buffer); i > 0; i = is.read(buffer)) {
					dos.write(buffer, 0, i);
					readed += i;
				}

				dos.close();
				addDownloadedURL(virtualURL, f.getAbsolutePath());

				long t4 = System.currentTimeMillis();
				logger.debug("Download time: " + (t4 - t3));
			}
		} catch (IOException io) {
			io.printStackTrace();
		}

		// Avoid possible conflits caused by multiple application instances.
		if (!f.exists()) {
			downloadedFiles.remove(virtualURL);
			f = downloadFile(url, virtualURL, filePath);
		}

		long t2 = System.currentTimeMillis();
		logger.debug("Total download method time: " + (t2 - t1));

		return f;
	}

	/**
	 * Downloads an URL into a temporary file that is removed the next time the
	 * tempFileManager class is called, which means the next time gvSIG is
	 * launched.
	 * 
	 * @param url
	 * @param filePath
	 *            Where the file will be downloaded
	 * @return File object of the downloaded file
	 * @throws IOException
	 * @throws ServerErrorResponseException
	 * @throws ConnectException
	 * @throws UnknownHostException
	 */
	public static File downloadFile(URL url, String filePath)
			throws IOException, ConnectException, UnknownHostException {
		File f = null;
		long t1 = System.currentTimeMillis();

		try {
			long t3 = System.currentTimeMillis();
			File tempDirectory = new File(tempDirectoryPath);

			if (!tempDirectory.exists()) {
				tempDirectory.mkdir();
			}

			// f = new File(tempDirectoryPath + "/" + filePath);
			String fName = normalizeFileName(filePath);
			f = new File(tempDirectoryPath + "/" + fName);

			logger.info("downloading '" + url.toString() + "' to: "
					+ f.getAbsolutePath());

			f.deleteOnExit();

			DataOutputStream dos = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(f)));
			byte[] buffer = new byte[1024 * 256];
			InputStream is = url.openStream();
			long readed = 0;

			for (int i = is.read(buffer); i > 0; i = is.read(buffer)) {
				dos.write(buffer, 0, i);
				readed += i;
			}

			dos.close();

			long t4 = System.currentTimeMillis();
			logger.debug("Download time: " + (t4 - t3));
		} catch (IOException io) {
			io.printStackTrace();
		}

		long t2 = System.currentTimeMillis();
		logger.debug("Total download method time: " + (t2 - t1));

		return f;
	}

	/**
	 * Adds an URL to the table of downloaded files for further uses. If the URL
	 * already exists in the table its filePath value is updated to the new one
	 * and the old file itself is removed from the file system.
	 * 
	 * @param virtualURL
	 * @param filePath
	 */
	private static void addDownloadedURL(URL virtualURL, String filePath) {
		if (downloadedFiles == null) {
			downloadedFiles = new Hashtable();
		}

		String fileName = (String) downloadedFiles.put(virtualURL, filePath);

		if (fileName != null) {
			File f = new File(fileName);

			if (f.exists()) {
				f.delete();
			}
		}
	}

	/**
	 * Sends a XML request to the server as a String and returns the server's
	 * response as a File object
	 * 
	 * @param url
	 *            server's URL
	 * @param req
	 *            XML request as a String
	 * @param fName
	 *            the name of the local file which will keep the server's
	 *            response
	 * @param override
	 *            this boolean sets if the download will be done (true)
	 * @return File object associated to the server's response (and it's local
	 *         name is fName)
	 * @throws ArcImsException
	 */
	public static File doRequestPost(URL url, String req, String fName,
			boolean override) throws ArcImsException {
		File f = null;
		URL virtualUrl = getVirtualRequestUrlFromUrlAndRequest(url, req);

		if ((f = getPreviousDownloadedURL(virtualUrl, override)) == null) {
			File tempDirectory = new File(tempDirectoryPath);

			if (!tempDirectory.exists()) {
				tempDirectory.mkdir();
			}

			String nfName = normalizeFileName(fName);

			f = new File(tempDirectoryPath + "/" + nfName);

			// f = new File(tempDirectoryPath + "/" + fName + "."
			// + System.currentTimeMillis());
			f.deleteOnExit();

			logger.info("downloading '" + url.toString() + "' to: "
					+ f.getAbsolutePath());

			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-type",
						"application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-length", "" + req.length());

				OutputStreamWriter wr = new OutputStreamWriter(
						conn.getOutputStream());
				wr.write(req);
				wr.flush();

				// Get the response
				DataOutputStream dos = new DataOutputStream(
						new BufferedOutputStream(new FileOutputStream(f)));
				byte[] buffer = new byte[1024 * 256];

				InputStream is = conn.getInputStream();

				long readed = 0;

				for (int i = is.read(buffer); i > 0; i = is.read(buffer)) {
					dos.write(buffer, 0, i);
					readed += i;
				}

				dos.close();
				is.close();
				wr.close();
				addDownloadedURL(virtualUrl, f.getAbsolutePath());
			} catch (ConnectException ce) {
				logger.error("Timed out error", ce);
				throw new ArcImsException("arcims_server_timeout");
			} catch (FileNotFoundException fe) {
				logger.error("FileNotFound Error", fe);
				throw new ArcImsException("arcims_server_error");
			} catch (IOException e) {
				logger.error("IO Error", e);
				throw new ArcImsException("arcims_server_error");
			}
		}

		// Avoid possible conflits caused by multiple application instances.
		if (!f.exists()) {
			downloadedFiles.remove(virtualUrl);
			f = doRequestPost(url, req, fName, override);
		}

		return f;
	}

	private static String normalizeFileName(String name) {
		String ret = new String();
		int indPoint = name.lastIndexOf(".");
		ret = name.substring(0, indPoint);
		ret += ("-" + System.currentTimeMillis());
		ret += ("." + name.substring(indPoint + 1));

		return ret;
	}

	/**
	 * Sends a XML request to the server as a String and returns the server's
	 * response as a File object
	 * 
	 * @param url
	 *            server's URL
	 * @param req
	 *            XML request as a String
	 * @param fName
	 *            the name of the local file which will keep the server's
	 *            response
	 * @return File object associated to the server's response (and it's local
	 *         name is fName)
	 * @throws ArcImsException
	 */
	public static File doRequestPost(URL url, String req, String fName)
			throws ArcImsException {
		return doRequestPost(url, req, fName, false);
	}

	/**
	 * Sends a POST request to a specified URL and gets a BufferedReader of the
	 * contents
	 * 
	 * @param url
	 * @param post
	 * @return BufferedReaded
	 * @throws ArcImsException
	 */
	public static InputStream getRemoteIS(URL url, String post)
			throws ArcImsException {
		InputStream lector = null;

		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-length", "" + post.length());

			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());

			// Do the POST request
			wr.write(post);
			wr.flush();

			// Get the response
			logger.info("downloading '" + url.toString());
			lector = conn.getInputStream();
		} catch (ConnectException e) {
			logger.error("Timed out error", e);
			throw new ArcImsException("arcims_server_timeout");
		} catch (ProtocolException e) {
			logger.error(e.getMessage(), e);
			throw new ArcImsException("arcims_server_error");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ArcImsException("arcims_server_error");
		}

		return lector;
	}

	/**
	 * Returns a virtual URL from status to emulate WMS requests to make unique
	 * requests for every image gvSIG downloads.
	 * 
	 * @param status
	 *            @see org.gvsig.remoteClient.arcims.utils.ArcImsStatus
	 * @return URL with the virtual link
	 */
	public static URL getVirtualUrlFromStatus(ArcImsStatus status) {
		URL u = null;
		String r = "http://arcims.image.virtual.url/";
		r = r + "?Server=";
		r = r + status.getServer();
		r = r + "&Service=";
		r = r + status.getService();
		r = r + "&LayerIds=";
		r = r + status.getLayerIds().toString();
		r = r + "&Extent=";
		r = r + status.getExtent().toString();
		r = r + "&Format=";
		r = r + status.getFormat();

		try {
			u = new URL(r);
		} catch (MalformedURLException e) {
			logger.error("Error in method getVirtualUrlFromStatus: "
					+ e.getMessage());
		}

		return u;
	}

	/**
	 * Returns a virtual URL from status to emulate WMS requests to make unique
	 * requests for every image gvSIG downloads.
	 * 
	 * @param url
	 * @param request
	 * @return URL with the virtual link
	 */
	public static URL getVirtualRequestUrlFromUrlAndRequest(URL url,
			String request) {
		URL u = null;
		String r = "http://arcims.request.virtual.url/";
		r = r + "Url=" + url.toString();
		r = r + "&Request=";
		r = r + request;

		try {
			u = new URL(r);
		} catch (MalformedURLException e) {
			logger.error("Error in methos getVirtualUrlFromStatus: "
					+ e.getMessage());
		}

		return u;
	}
}
