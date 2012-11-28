package org.gvsig.layer;

import static org.mockito.Mockito.mock;

import javax.inject.Inject;

import org.gvsig.GVSIGTestCase;
import org.gvsig.layer.filter.LayerFilter;

public class VectorialLayerTest extends GVSIGTestCase {

	@Inject
	private LayerFactory layerFactory;

	private Layer layer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		layer = layerFactory.createLayer(mock(Source.class));
	}

	public void testGetAllLayers() throws Exception {
		Layer[] layers = layer.getAllLayers();
		assertEquals(1, layers.length);
		assertEquals(layer, layers[0]);
	}

	public void testContains() throws Exception {
		assertFalse(layer.contains(mock(Layer.class)));
		assertTrue(layer.contains(layer));
	}

	public void testName() throws Exception {
		assertNull(layer.getName());
		layer.setName("name");
		assertEquals("name", layer.getName());
	}

	public void testSymbology() throws Exception {
		fail("Check default symbology for point layers, "
				+ "multipoint layers, line layers, etc. and heterogeneous layers");
	}

	public void testFilter() throws Exception {
		Layer[] layers = layer.filter(new LayerFilter() {
			@Override
			public boolean accepts(Layer layer) {
				return false;
			}
		});
		assertEquals(0, layers.length);

		layers = layer.filter(new LayerFilter() {
			@Override
			public boolean accepts(Layer layer) {
				return true;
			}
		});
		assertEquals(1, layers.length);
		assertEquals(layer, layers[0]);
	}

	public void testIsVectorial() throws Exception {
		assertTrue(layer.isVectorial());
	}
}
