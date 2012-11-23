package org.gvsig.layer;

import java.util.HashMap;

import javax.inject.Inject;

import org.gvsig.GVSIGTestCase;

public class SourceTest extends GVSIGTestCase {

	@Inject
	private SourceManager sourceManager;

	@Inject
	private SourceFactory sourceFactory;

	public void testSourceReceivesId() throws Exception {
		Source source = sourceFactory
				.createSource(new HashMap<String, String>());
		sourceManager.register("a", source);

		assertTrue(source.getId().equals("a"));
	}
}
