package org.gvsig.layer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.gvsig.GVSIGTestCase;
import org.gvsig.layer.filter.LayerFilter;

public class CompositeLayerTest extends GVSIGTestCase {
	private Layer root;

	@Inject
	private LayerFactory layerFactory;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		root = layerFactory.createCompositeLayer();
	}

	public void testReminder() throws Exception {
		fail("We should not do tests on concrete instances only. "
				+ "First on the interface and, if necessary, on the implementation");
	}

	public void testAddLayer() throws Exception {
		Layer layer = mock(Layer.class);
		root.addLayer(layer);

		Layer[] layers = root.getAllLayers();
		assertEquals(2, layers.length);
		assertEquals(layer, layers[1]);
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
		assertEquals(1, root.getAllLayers().length);
		assertEquals(root, root.getAllLayers()[0]);

		assertFalse(root.removeLayer(layer));
		assertEquals(1, root.getAllLayers().length);
		assertEquals(root, root.getAllLayers()[0]);
	}

	public void testGetAllLayers() throws Exception {
		Layer[] layers = root.getAllLayers();
		assertEquals(1, layers.length);
		assertEquals(root, layers[0]);

		Layer l1 = mock(Layer.class);
		Layer l2 = mock(Layer.class);
		root.addLayer(l1);
		root.addLayer(l2);
		layers = root.getAllLayers();
		assertEquals(3, layers.length);
		assertEquals(root, layers[0]);
		assertEquals(l1, layers[1]);
		assertEquals(l2, layers[2]);

		root.removeLayer(l1);
		layers = root.getAllLayers();
		assertEquals(2, layers.length);
		assertEquals(l2, layers[1]);
	}

	public void testContains() throws Exception {
		Layer l1 = mock(Layer.class);
		when(l1.contains(l1)).thenReturn(true);
		Layer l2 = mock(Layer.class);
		when(l2.contains(l2)).thenReturn(true);
		Layer l3 = mock(Layer.class);
		when(l3.contains(l3)).thenReturn(true);
		Layer composite = layerFactory.createCompositeLayer();
		composite.addLayer(l3);
		root.addLayer(l1);
		root.addLayer(composite);

		assertTrue(root.contains(root));
		assertTrue(root.contains(l1));
		assertTrue(root.contains(composite));
		assertTrue(root.contains(l3));
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

	public void testIsVectorial() throws Exception {
		assertFalse(root.isVectorial());
	}

	public void testEvents() throws Exception {
		fail("Should test that all layer events are "
				+ "raised. Not only in Componsite but also in Vectorial");
	}
}
