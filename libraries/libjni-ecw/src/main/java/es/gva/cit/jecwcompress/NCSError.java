/**********************************************************************
 * $Id: NCSError.java 3538 2006-01-09 11:56:54Z nacho $
 *
 * Name:     NCSError.java
 * Project:  
 * Purpose:  
 * Author:   Nacho Brodin, brodin_ign@gva.es
 *
 **********************************************************************/
/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */

package es.gva.cit.jecwcompress;

/**
 * Clase que realiza la conversión de los códigos de error que devuelve las
 * funciones C en cadenas para que pueda ser mostrado el significado del error
 * al usuario.
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR>
 *         Equipo de desarrollo gvSIG.<BR>
 *         http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class NCSError {

	/**
	 * Conversión de un código de error pasado por parámetro a cadena
	 * 
	 * @return cadena que representa el error producido
	 */
	public static String ErrorToString(int error) {

		switch (error) {
		case 0:
			return new String("NCS_SUCCESS");
		case 1:
			return new String("NCS_QUEUE_NODE_CREATE_FAILED");
			/** < Queue node creation failed */
		case 2:
			return new String("NCS_FILE_OPEN_FAILED");
			/** < File open failed */
		case 3:
			return new String("NCS_FILE_LIMIT_REACHED");
			/** < The Image Web Server's licensed file limit has been reached */
		case 4:
			return new String("NCS_FILE_SIZE_LIMIT_REACHED");
			/**
			 * < The requested file is larger than is permitted by the license
			 * on this Image Web Server
			 */
		case 5:
			return new String("NCS_FILE_NO_MEMORY");
			/** < Not enough memory for new file */
		case 6:
			return new String("NCS_CLIENT_LIMIT_REACHED");
			/** < The Image Web Server's licensed client limit has been reached */
		case 7:
			return new String("NCS_DUPLICATE_OPEN");
			/** < Detected duplicate open from net layer */
		case 8:
			return new String("NCS_PACKET_REQUEST_NYI");
			/** < Packet request type not yet implemented */
		case 9:
			return new String("NCS_PACKET_TYPE_ILLEGAL");
			/** < Packet type is illegal */
		case 10:
			return new String("NCS_DESTROY_CLIENT_DANGLING_REQUESTS");
			/** < Client closed while requests outstanding */
		case 11:
			return new String("NCS_UNKNOWN_CLIENT_UID");
			/** < Client UID unknown */
		case 12:
			return new String("NCS_COULDNT_CREATE_CLIENT");
			/** < Could not create new client */
		case 13:
			return new String("NCS_NET_COULDNT_RESOLVE_HOST");
			/** < Could not resolve address of Image Web Server */
		case 14:
			return new String("NCS_NET_COULDNT_CONNECT");
			/** < Could not connect to host */
		case 15:
			return new String("NCS_NET_RECV_TIMEOUT");
			/** < Receive timeout */
		case 16:
			return new String("NCS_NET_HEADER_SEND_FAILURE");
			/** < Error sending header */
		case 17:
			return new String("NCS_NET_HEADER_RECV_FAILURE");
			/** < Error receiving header */
		case 18:
			return new String("NCS_NET_PACKET_SEND_FAILURE");
			/** < Error sending packet */
		case 19:
			return new String("NCS_NET_PACKET_RECV_FAILURE");
			/** < Error receiving packet */
		case 20:
			return new String("NCS_NET_401_UNAUTHORISED");
			/**
			 * < 401 Unauthorised: SDK doesn't do authentication so this
			 * suggests a misconfigured server
			 */
		case 21:
			return new String("NCS_NET_403_FORBIDDEN");
			/**
			 * < 403 Forbidden: could be a 403.9 from IIS or PWS meaning that
			 * the maximum simultaneous request limit has been reached
			 */
		case 22:
			return new String("NCS_NET_404_NOT_FOUND");
			/**
			 * < 404 Not Found: this error suggests that the server hasn't got
			 * Image Web Server installed
			 */
		case 23:
			return new String("NCS_NET_407_PROXYAUTH");
			/**
			 * < 407 Proxy Authentication: the SDK doesn't do proxy
			 * authentication yet either, so this also suggests misconfiguration
			 */
		case 24:
			return new String("NCS_NET_UNEXPECTED_RESPONSE");
			/** < Unexpected HTTP response could not be handled */
		case 25:
			return new String("NCS_NET_BAD_RESPONSE");
			/** < HTTP response received outside specification */
		case 26:
			return new String("NCS_NET_ALREADY_CONNECTED");
			/** < Already connected */
		case 27:
			return new String("NCS_INVALID_CONNECTION");
			/** < Connection is invalid */
		case 28:
			return new String("NCS_WINSOCK_FAILURE");
			/** < A Windows sockets failure occurred */
		case 29:
			return new String("NCS_SYMBOL_ERROR");
			/** < Symbology error */
		case 30:
			return new String("NCS_OPEN_DB_ERROR");
			/** < Could not open database */
		case 31:
			return new String("NCS_DB_QUERY_FAILED");
			/** < Could not execute the requested query on database */
		case 32:
			return new String("NCS_DB_SQL_ERROR");
			/** < SQL statement could not be executed */
		case 33:
			return new String("NCS_GET_LAYER_FAILED");
			/** < Open symbol layer failed */
		case 34:
			return new String("NCS_DB_NOT_OPEN");
			/** < The database is not open */
		case 35:
			return new String("NCS_QT_TYPE_UNSUPPORTED");
			/** < This type of quadtree is not supported */
		case 36:
			return new String("NCS_PREF_INVALID_USER_KEY");
			/** < Invalid local user key name specified */
		case 37:
			return new String("NCS_PREF_INVALID_MACHINE_KEY");
			/** < Invalid local machine key name specified */
		case 38:
			return new String("NCS_REGKEY_OPENEX_FAILED");
			/** < Failed to open registry key */
		case 39:
			return new String("NCS_REGQUERY_VALUE_FAILED");
			/** < Registry query failed */
		case 40:
			return new String("NCS_INVALID_REG_TYPE");
			/** < Type mismatch in registry variable */
		case 41:
			return new String("NCS_INVALID_ARGUMENTS");
			/** < Invalid arguments passed to function */
		case 42:
			return new String("NCS_ECW_ERROR");
			/** < ECW error */
		case 43:
			return new String("NCS_SERVER_ERROR");
			/** < Server error */
		case 44:
			return new String("NCS_UNKNOWN_ERROR");
			/** < Unknown error */
		case 45:
			return new String("NCS_EXTENT_ERROR");
			/** < Extent conversion failed */
		case 46:
			return new String("NCS_COULDNT_ALLOC_MEMORY");
			/** < Could not allocate enough memory */
		case 47:
			return new String("NCS_INVALID_PARAMETER");
			/** < An invalid parameter was used */
		case 48:
			return new String("NCS_FILEIO_ERROR");
			/** < Error reading or writing file */
		case 49:
			return new String("NCS_COULDNT_OPEN_COMPRESSION");
			/** < Compression task could not be initialised */
		case 50:
			return new String("NCS_COULDNT_PERFORM_COMPRESSION");
			/** < Compression task could not be processed */
		case 51:
			return new String("NCS_GENERATED_TOO_MANY_OUTPUT_LINES");
			/** < Trying to generate too many output lines */
		case 52:
			return new String("NCS_USER_CANCELLED_COMPRESSION");
			/** < Compression task was cancelled by client application */
		case 53:
			return new String("NCS_COULDNT_READ_INPUT_LINE");
			/** < Could not read line from input data */
		case 54:
			return new String("NCS_INPUT_SIZE_EXCEEDED");
			/** < Input image size was exceeded for this version of the SDK */
		case 55:
			return new String("NCS_REGION_OUTSIDE_FILE");
			/** < Specified image region is outside image extents */
		case 56:
			return new String("NCS_NO_SUPERSAMPLE");
			/** < Supersampling is not supported by the SDK functions */
		case 57:
			return new String("NCS_ZERO_SIZE");
			/** < Specified image region has a zero width or height */
		case 58:
			return new String("NCS_TOO_MANY_BANDS");
			/** < More bands specified than exist in the input file */
		case 59:
			return new String("NCS_INVALID_BAND_NR");
			/** < An invalid band number has been specified */
		case 60:
			return new String("NCS_INPUT_SIZE_TOO_SMALL");
			/**
			 * < Input image size is too small to compress - for ECW compression
			 * there is a minimum output file size
			 */
		case 61:
			return new String("NCS_INCOMPATIBLE_PROTOCOL_VERSION");
			/** < The ECWP client version is incompatible with this server */
		case 62:
			return new String("NCS_WININET_FAILURE");
			/** < Windows Internet Client error */
		case 63:
			return new String("NCS_COULDNT_LOAD_WININET");
			/**
			 * < wininet.dll could not be loaded - usually indicates Internet
			 * Explorer should be upgraded
			 */
		case 64:
			return new String("NCS_FILE_INVALID_SETVIEW");
			/**
			 * < The parameters specified for setting a file view were invalid,
			 * or the view was not set
			 */
		case 65:
			return new String("NCS_FILE_NOT_OPEN");
			/** < No file is open */
		case 66:
			return new String("NCS_JNI_REFRESH_NOT_IMPLEMENTED");
			/** < Class does not implement ECWProgressiveDisplay interface */
		case 67:
			return new String("NCS_INCOMPATIBLE_COORDINATE_SYSTEMS");
			/** < Incompatible coordinate systems */
		case 68:
			return new String("NCS_INCOMPATIBLE_COORDINATE_DATUM");
			/** < Incompatible coordinate datum types */
		case 69:
			return new String("NCS_INCOMPATIBLE_COORDINATE_PROJECTION");
			/** < Incompatible coordinate projection types */
		case 70:
			return new String("NCS_INCOMPATIBLE_COORDINATE_UNITS");
			/** < Incompatible coordinate units types */
		case 71:
			return new String("NCS_COORDINATE_CANNOT_BE_TRANSFORMED");
			/** < Non-linear coordinate systems not supported */
		case 72:
			return new String("NCS_GDT_ERROR");
			/** < Error involving the GDT database */
		case 73:
			return new String("NCS_NET_PACKET_RECV_ZERO_LENGTH");
			/** < Zero length packet received */
		case 74:
			return new String("NCS_UNSUPPORTEDLANGUAGE");
			/** < Must use Japanese version of the ECW SDK */
		case 75:
			return new String("NCS_CONNECTION_LOST");
			/** < Connection to server was lost */
		case 76:
			return new String("NCS_COORD_CONVERT_ERROR");
			/** < NCSGDT coordinate conversion failed */
		case 77:
			return new String("NCS_METABASE_OPEN_FAILED");
			/** < Failed to open metabase */
		case 78:
			return new String("NCS_METABASE_GET_FAILED");
			/** < Failed to get value from metabase */
		case 79:
			return new String("NCS_NET_HEADER_SEND_TIMEOUT");
			/** < Timeout sending header */
		case 80:
			return new String("NCS_JNI_ERROR");
			/** < Java JNI error */
		case 81:
			return new String("NCS_DB_INVALID_NAME");
			/** < No data source passed */
		case 82:
			return new String("NCS_SYMBOL_COULDNT_RESOLVE_HOST");
			/**
			 * < Could not resolve address of Image Web Server Symbol Server
			 * Extension
			 */
		case 83:
			return new String("NCS_INVALID_ERROR_ENUM");
			/** < The value of an NCSError error number was invalid! */
		case 84:
			return new String("NCS_FILE_EOF");
			/** < End of file reached */
		case 85:
			return new String("NCS_FILE_NOT_FOUND");
			/** < File not found */
		case 86:
			return new String("NCS_FILE_INVALID");
			/** < File was invalid or corrupt */
		case 87:
			return new String("NCS_FILE_SEEK_ERROR");
			/** < Attempted to read, write or seek past file limits */
		case 88:
			return new String("NCS_FILE_NO_PERMISSIONS");
			/** < Permissions not available to access file */
		case 89:
			return new String("NCS_FILE_OPEN_ERROR");
			/** < Error opengin file */
		case 90:
			return new String("NCS_FILE_CLOSE_ERROR");
			/** < Error closing file */
		case 91:
			return new String("NCS_FILE_IO_ERROR");
			/** < Miscellaneous error involving file input or output */
		case 92:
			return new String("NCS_SET_EXTENTS_ERROR");
			/** < Illegal or invalid world coordinates supplied */
		case 93:
			return new String("NCS_FILE_PROJECTION_MISMATCH");
			/** < Image projection does not match that of the controlling layer */
		case 94:
			return new String("NCS_GDT_UNKNOWN_PROJECTION");
			/** < Unknown map projection */
		case 95:
			return new String("NCS_GDT_UNKNOWN_DATUM");
			/** < Unknown geodetic datum */
		case 96:
			return new String("NCS_GDT_USER_SERVER_FAILED");
			/**
			 * < User specified Geographic Projection Database data server
			 * failed
			 */
		case 97:
			return new String("NCS_GDT_REMOTE_PATH_DISABLED");
			/**
			 * < Remote Geographic Projection Database file downloading has been
			 * disabled and no local GDT data is available
			 */
		case 98:
			return new String("NCS_GDT_BAD_TRANSFORM_MODE");
			/** < Invalid mode of transform */
		case 99:
			return new String("NCS_GDT_TRANSFORM_OUT_OF_BOUNDS");
			/** < Coordinate to be transformed is out of bounds */
		case 100:
			return new String("NCS_LAYER_DUPLICATE_LAYER_NAME");
			/** < A layer already exists with the specified name */
		case 101:
			return new String("NCS_LAYER_INVALID_PARAMETER");
			/** < The specified layer does not contain the specified parameter */
		case 102:
			return new String("NCS_PIPE_CREATE_FAILED");
			/** < Failed to create pipe */
		case 103:
			return new String("NCS_FILE_MKDIR_EXISTS");
			/** < Directory to be created already exists */
			/* [20] */
		case 104:
			return new String("NCS_FILE_MKDIR_PATH_NOT_FOUND");
			/** < The path specified for directory creation does not exist */
			/* [20] */
		case 105:
			return new String("NCS_ECW_READ_CANCELLED");
			/** < File read was cancelled */
		case 106:
			return new String("NCS_JP2_GEODATA_READ_ERROR");
			/** < Error reading geodata from a JPEG 2000 file */
			/* [21] */
		case 107:
			return new String("NCS_JP2_GEODATA_WRITE_ERROR");
			/** < Error writing geodata to a JPEG 2000 file */
			/* [21] */
		case 108:
			return new String("NCS_JP2_GEODATA_NOT_GEOREFERENCED");
			/** < JPEG 2000 file not georeferenced */
			/* [21] */
		case 109:
			return new String("NCS_MAX_ERROR_NUMBER");
			/** < The maximum error value in this enumerated typ */
		}
		return new String("");
	}
}