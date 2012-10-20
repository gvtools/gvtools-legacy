/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 * 2009 Iver T.I.  {{Task}}
 */

package es.gva.cit.gazetteer.geonames;

import java.awt.geom.Point2D;
import java.net.URI;
import java.util.List;

import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import com.iver.utiles.swing.jcomboServer.ServerData;

import es.gva.cit.catalog.drivers.DiscoveryServiceCapabilities;
import es.gva.cit.catalog.exceptions.NotSupportedVersionException;
import es.gva.cit.gazetteer.drivers.AbstractGazetteerServiceDriver;
import es.gva.cit.gazetteer.drivers.GazetteerCapabilities;
import es.gva.cit.gazetteer.querys.Feature;
import es.gva.cit.gazetteer.querys.GazetteerQuery;

public class GeonamesServiceDriver extends AbstractGazetteerServiceDriver {

	public GeonamesServiceDriver() {
		super();
		setProjection("EPSG:4326");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.gva.cit.gazetteer.drivers.IGazetteerServiceDriver#getFeature(java.
	 * net.URI, es.gva.cit.gazetteer.querys.GazetteerQuery)
	 */
	public Feature[] getFeature(URI uri, GazetteerQuery query) throws Exception {
		ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
		searchCriteria.setQ(query.getName());
		ToponymSearchResult searchResult = WebService.search(searchCriteria);
		List toponims = searchResult.getToponyms();
		Feature[] features = new Feature[toponims.size()];
		for (int i = 0; i < toponims.size(); i++) {
			features[i] = converToponym((Toponym) toponims.get(i));
		}
		return features;
	}

	private Feature converToponym(Toponym toponym) {
		Feature feature = new Feature(toponym.getFeatureCode(),
				toponym.getName(), toponym.getName(), new Point2D.Double(
						toponym.getLongitude(), toponym.getLatitude()));

		return feature;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.gva.cit.catalog.drivers.IDiscoveryServiceDriver#getCapabilities(java
	 * .net.URI)
	 */
	public DiscoveryServiceCapabilities getCapabilities(URI uri)
			throws NotSupportedVersionException {
		GazetteerCapabilities capabilities = new GazetteerCapabilities();
		return capabilities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.gva.cit.catalog.drivers.IDiscoveryServiceDriver#getServiceName()
	 */
	public String getServiceName() {
		return "Geonames";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.gva.cit.catalog.drivers.AbstractDiscoveryServiceDriver#getOneServer()
	 */
	public ServerData getOneServer() {
		return new ServerData("www.geonames.org", "GEONAMES");
	}

}
