package org.gvsig.layer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.gvsig.GVSIGTestCase;
import org.gvsig.layer.filter.LayerFilter;
import org.gvsig.persistence.generated.LayerType;

import com.google.inject.Inject;

public class LayerTest extends GVSIGTestCase {
	@Inject
	private LayerFactory layerFactory;

	public void testContains() throws Exception {
		// Vectorial
		Layer layer = layerFactory.createLayer(mock(Source.class));
		assertFalse(layer.contains(mock(Layer.class)));
		assertTrue(layer.contains(layer));

		// Composite
		layer = layerFactory.createLayer();
		Layer l1 = mock(Layer.class);
		when(l1.contains(l1)).thenReturn(true);
		Layer l2 = mock(Layer.class);
		when(l2.contains(l2)).thenReturn(true);
		Layer l3 = mock(Layer.class);
		when(l3.contains(l3)).thenReturn(true);
		Layer composite = layerFactory.createLayer(l3);
		layer.addLayer(l1);
		layer.addLayer(composite);

		assertTrue(layer.contains(layer));
		assertTrue(layer.contains(l1));
		assertTrue(layer.contains(composite));
		assertTrue(layer.contains(l3));
		assertFalse(layer.contains(l2));
		assertFalse(layer.contains(null));
	}

	public void testGetAllLayers() throws Exception {
		// Vectorial
		Layer layer = layerFactory.createLayer(mock(Source.class));
		Layer[] layers = layer.getAllLayers();
		assertEquals(1, layers.length);
		assertEquals(layer, layers[0]);

		// Empty composite
		layer = layerFactory.createLayer();
		layers = layer.getAllLayers();
		assertEquals(1, layers.length);
		assertEquals(layer, layers[0]);

		// Non-empty composite
		Layer l1 = mock(Layer.class);
		Layer l2 = mock(Layer.class);
		layer = layerFactory.createLayer(l1, l2);
		layers = layer.getAllLayers();
		assertEquals(3, layers.length);
		assertEquals(layer, layers[0]);
		assertEquals(l1, layers[1]);
		assertEquals(l2, layers[2]);
	}

	public void testAddLayer() throws Exception {
		// Vectorial
		Layer child = mock(Layer.class);
		try {
			layerFactory.createLayer(mock(Source.class)).addLayer(child);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		// Composite
		Layer layer = layerFactory.createLayer();
		try {
			layer.addLayer(null);
			fail();
		} catch (IllegalArgumentException e) {
		}

		layer.addLayer(child);

		Layer[] layers = layer.getAllLayers();
		assertEquals(2, layers.length);
		assertEquals(child, layers[1]);
	}

	public void testFilter() throws Exception {
		// Vectorial
		Layer layer = layerFactory.createLayer(mock(Source.class));
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

		// Composite
		Layer l1 = mock(Layer.class);
		Layer l2 = mock(Layer.class);
		layer = layerFactory.createLayer(l1, l2);

		layers = layer.filter(new LayerFilter() {
			@Override
			public boolean accepts(Layer layer) {
				return false;
			}
		});
		assertEquals(0, layers.length);

		LayerFilter filter = mock(LayerFilter.class);
		// true, false, true == root and second child accepted
		when(filter.accepts(any(Layer.class))).thenReturn(true)
				.thenReturn(false).thenReturn(true);
		layers = layer.filter(filter);
		assertEquals(2, layers.length);
		assertEquals(layer, layers[0]);
		assertEquals(l2, layers[1]);
	}

	public void testSymbology() throws Exception {
		fail("Check default symbology for point layers, "
				+ "multipoint layers, line layers, etc. and heterogeneous layers");
	}

	public void testIsVectorial() throws Exception {
		assertTrue(layerFactory.createLayer(mock(Source.class)).isVectorial());
		assertFalse(layerFactory.createLayer().isVectorial());
	}

	public void testIsActive() throws Exception {
		assertFalse(layerFactory.createLayer(mock(Source.class)).isActive());
		assertFalse(layerFactory.createLayer().isActive());
	}

	public void testEditing() throws Exception {
		assertFalse(layerFactory.createLayer(mock(Source.class)).isEditing());
		assertFalse(layerFactory.createLayer().isEditing());
	}

	public void testGetXML() throws Exception {
		// Vectorial
		Layer layer = spy(layerFactory.createLayer(mock(Source.class)));
		when(layer.isActive()).thenReturn(true);
		when(layer.isEditing()).thenReturn(true);
		LayerType xml = layer.getXML();
		assertTrue(xml.isActive());
		assertTrue(xml.isEditing());
		assertEquals(0, xml.getLayers().size());

		layer = layerFactory.createLayer(mock(Source.class));
		xml = layer.getXML();
		assertFalse(xml.isActive());
		assertFalse(xml.isEditing());
		assertEquals(0, xml.getLayers().size());

		// Composite
		layer = layerFactory.createLayer();
		xml = layer.getXML();
		assertFalse(xml.isActive());
		assertFalse(xml.isEditing());
		assertFalse(xml.isVectorial());
		assertEquals(0, xml.getLayers().size());

		layer.addLayer(layerFactory.createLayer(mock(Source.class)));
		xml = layer.getXML();
		assertFalse(xml.isActive());
		assertFalse(xml.isEditing());
		assertFalse(xml.isVectorial());
		assertEquals(1, xml.getLayers().size());
		assertTrue(xml.getLayers().get(0).isVectorial());
	}

	public void testSetXML() throws Exception {
		LayerType xml = new LayerType();

		Layer layer = layerFactory.createLayer(mock(Source.class));
		try {
			// Not vectorial
			xml.setVectorial(false);
			layer.setXML(xml);
			fail();
		} catch (IllegalArgumentException e) {
		}

		xml.setVectorial(true);

		try {
			// With children layers
			xml.getLayers().add(
					layerFactory.createLayer(mock(Source.class)).getXML());
			layer.setXML(xml);
			fail();
		} catch (IllegalArgumentException e) {
		}

		xml.getLayers().clear();
		xml.setVectorial(true);
		xml.setActive(true);
		xml.setEditing(false);
		layer.setXML(xml);

		// Composite layer
		layer = layerFactory.createLayer();
		try {
			// Vectorial
			xml = new LayerType();
			xml.setVectorial(true);
			xml.setActive(false);
			xml.setEditing(false);
			layer.setXML(xml);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			// Editing
			xml = new LayerType();
			xml.setVectorial(false);
			xml.setActive(false);
			xml.setEditing(true);
			layer.setXML(xml);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			// Active
			xml = new LayerType();
			xml.setVectorial(true);
			xml.setActive(true);
			xml.setEditing(false);
			layer.setXML(xml);
			fail();
		} catch (IllegalArgumentException e) {
		}

		// With children layers
		xml = new LayerType();
		xml.setVectorial(false);
		xml.setActive(false);
		xml.setEditing(false);
		xml.getLayers().add(
				layerFactory.createLayer(mock(Source.class)).getXML());
		layer.setXML(xml);

		assertFalse(layer.isActive());
		assertFalse(layer.isEditing());
		assertFalse(layer.isVectorial());
		assertEquals(2, layer.getAllLayers().length);
		assertEquals(layer, layer.getAllLayers()[0]);
		assertTrue(layer.getAllLayers()[1].isVectorial());
	}
}
