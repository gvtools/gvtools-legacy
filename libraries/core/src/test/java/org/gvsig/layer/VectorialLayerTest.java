package org.gvsig.layer;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.gvsig.layer.filter.LayerFilter;
import org.gvsig.layer.impl.VectorialLayer;

public class VectorialLayerTest extends TestCase {
	private VectorialLayer layer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		layer = new VectorialLayer();
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

	public void testEditing() throws Exception {
		assertFalse(layer.isEditing());

		layer.startEdition();
		assertTrue(layer.isEditing());
		layer.startEdition();
		assertTrue(layer.isEditing());
		layer.stopEdition();
		assertFalse(layer.isEditing());
		layer.stopEdition();
		assertFalse(layer.isEditing());
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

	public void testActivation() throws Exception {
		assertFalse(layer.isActive());

		layer.activate();
		assertTrue(layer.isActive());
		layer.activate();
		assertTrue(layer.isActive());

		layer.deactivate();
		assertFalse(layer.isActive());
		layer.deactivate();
		assertFalse(layer.isActive());
	}
}
