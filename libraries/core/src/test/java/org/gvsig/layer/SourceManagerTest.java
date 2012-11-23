package org.gvsig.layer;

import static org.mockito.Mockito.mock;

import javax.inject.Inject;

import org.gvsig.GVSIGTestCase;

public class SourceManagerTest extends GVSIGTestCase {

	@Inject
	private SourceManager sourceManager;

	public void testAddSource() throws Exception {
		Source source = mock(Source.class);
		sourceManager.register("a", source);

		assertTrue(sourceManager.getSource("a") == source);
		assertTrue(sourceManager.getSources().length == 1);
		assertTrue(sourceManager.getSources()[0] == source);
	}

	public void testAddSameSourceTwice() throws Exception {
		fail();
	}

	public void testGetUnexistingSource() throws Exception {
		fail();
	}
}
