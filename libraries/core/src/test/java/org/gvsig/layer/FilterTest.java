package org.gvsig.layer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.gvsig.GVSIGTestCase;
import org.gvsig.layer.filter.CompositeLayerFilter;
import org.gvsig.layer.filter.LayerFilter;

public class FilterTest extends GVSIGTestCase {

	@Inject
	private LayerFactory layerFactory;

	public void testActive() throws Exception {
		Layer root = layerFactory.createCompositeLayer();
		Layer l1 = mock(Layer.class);
		Layer l2 = mock(Layer.class);
		when(l1.isActive()).thenReturn(false);
		when(l2.isActive()).thenReturn(true);
		root.addLayer(l1);
		root.addLayer(l2);

		Layer[] layers = root.filter(LayerFilter.ACTIVE);
		assertEquals(1, layers.length);
		assertEquals(l2, layers[0]);
	}

	public void testComposite() throws Exception {
		// Active and vectorial filter
		CompositeLayerFilter filter = new CompositeLayerFilter(
				LayerFilter.ACTIVE, LayerFilter.VECTORIAL);

		// Vectorial and active
		Layer l1 = mock(Layer.class);
		when(l1.isVectorial()).thenReturn(true);
		when(l1.isActive()).thenReturn(true);
		// Not Vectorial and active
		Layer l2 = mock(Layer.class);
		when(l2.isVectorial()).thenReturn(false);
		when(l2.isActive()).thenReturn(true);
		// Vectorial and not active
		Layer l3 = mock(Layer.class);
		when(l3.isVectorial()).thenReturn(true);
		when(l3.isActive()).thenReturn(false);

		Layer root = layerFactory.createCompositeLayer();
		root.addLayer(l1);
		root.addLayer(l2);
		root.addLayer(l3);

		Layer[] layers = root.filter(filter);
		assertEquals(1, layers.length);
		assertEquals(l1, layers[0]);
	}

	public void testEditing() throws Exception {
		Layer root = layerFactory.createCompositeLayer();
		// Vectorial and editing
		Layer l1 = mock(Layer.class);
		when(l1.isVectorial()).thenReturn(true);
		when(l1.isEditing()).thenReturn(true);
		// Not Vectorial and editing
		Layer l2 = mock(Layer.class);
		when(l2.isVectorial()).thenReturn(false);
		when(l2.isEditing()).thenReturn(true);
		// Not editing
		Layer l3 = mock(Layer.class);
		when(l3.isVectorial()).thenReturn(true);
		when(l3.isEditing()).thenReturn(false);

		root.addLayer(l1);
		root.addLayer(l2);
		root.addLayer(l3);

		Layer[] layers = root.filter(LayerFilter.VECTORIAL_EDITING);
		assertEquals(1, layers.length);
		assertEquals(l1, layers[0]);
	}

	public void testVectorial() throws Exception {
		Layer root = layerFactory.createCompositeLayer();
		Layer l1 = mock(Layer.class);
		Layer l2 = mock(Layer.class);
		when(l1.isVectorial()).thenReturn(false);
		when(l2.isVectorial()).thenReturn(true);
		root.addLayer(l1);
		root.addLayer(l2);

		Layer[] layers = root.filter(LayerFilter.VECTORIAL);
		assertEquals(1, layers.length);
		assertEquals(l2, layers[0]);
	}

	public void testVectorialSingle() throws Exception {
		// Active
		Layer l1 = layerFactory.createLayer(mock(Source.class));
		Layer[] layers = l1.filter(LayerFilter.VECTORIAL);
		assertEquals(1, layers.length);
		assertEquals(l1, layers[0]);
	}
}
