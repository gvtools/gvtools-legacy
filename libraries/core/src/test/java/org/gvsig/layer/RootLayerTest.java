package org.gvsig.layer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.gvsig.layer.impl.RootLayer;

public class RootLayerTest extends TestCase {
	private RootLayer root;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		root = new RootLayer();
	}

	public void testAddLayer() throws Exception {
		Layer layer = mock(Layer.class);
		root.addLayer(layer);

		Layer[] layers = root.getAllLayers();
		assertEquals(1, layers.length);
		assertEquals(layer, layers[0]);
	}

	public void testAddNullLayer() throws Exception {
		try {
			root.addLayer(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testRemoveLayer() throws Exception {
		Layer layer = mock(Layer.class);
		root.addLayer(layer);

		assertTrue(root.removeLayer(layer));
		assertEquals(0, root.getAllLayers().length);

		assertFalse(root.removeLayer(layer));
		assertEquals(0, root.getAllLayers().length);
	}

	public void testGetAllLayers() throws Exception {
		Layer[] layers = root.getAllLayers();
		assertEquals(0, layers.length);

		Layer l1 = mock(Layer.class);
		Layer l2 = mock(Layer.class);
		root.addLayer(l1);
		root.addLayer(l2);
		layers = root.getAllLayers();
		assertEquals(2, layers.length);
		assertEquals(l1, layers[0]);
		assertEquals(l2, layers[1]);

		root.removeLayer(l1);
		layers = root.getAllLayers();
		assertEquals(1, layers.length);
		assertEquals(l2, layers[0]);
	}

	public void testContains() throws Exception {
		Layer l1 = mock(Layer.class);
		Layer l2 = mock(Layer.class);
		root.addLayer(l1);

		assertTrue(root.contains(l1));
		assertFalse(root.contains(l2));
		assertFalse(root.contains(null));
	}

	public void testName() throws Exception {
		assertNull(root.getName());
		root.setName("name");
		assertEquals("name", root.getName());
	}

	public void testEditing() throws Exception {
		assertFalse(root.isEditing());
	}

	public void testFilter() throws Exception {
		final String name = "1";
		Layer l1 = mock(Layer.class);
		when(l1.getName()).thenReturn(name);
		Layer l2 = mock(Layer.class);
		when(l2.getName()).thenReturn("2");
		root.addLayer(l1);
		root.addLayer(l2);

		Layer[] layers = root.filter(new LayerFilter() {
			@Override
			public boolean accepts(Layer layer) {
				return false;
			}
		});
		assertEquals(0, layers.length);

		layers = root.filter(new LayerFilter() {
			@Override
			public boolean accepts(Layer layer) {
				return layer.getName().equals(name);
			}
		});
		assertEquals(1, layers.length);
	}
}
