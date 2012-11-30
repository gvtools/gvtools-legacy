package org.gvsig.layer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.gvsig.GVSIGTestCase;
import org.gvsig.layer.filter.AndLayerFilter;
import org.gvsig.layer.filter.LayerFilter;

import com.google.inject.Inject;

public class FilterTest extends GVSIGTestCase {
	@Inject
	private LayerFactory layerFactory;

	public void testActive() throws Exception {
		// Vectorial active
		Layer layer = spy(layerFactory.createLayer(mock(Source.class)));
		when(layer.isActive()).thenReturn(true);

		Layer[] layers = layer.filter(LayerFilter.ACTIVE);
		assertEquals(1, layers.length);
		assertEquals(layer, layers[0]);

		// Vectorial not active
		layer = spy(layerFactory.createLayer(mock(Source.class)));
		when(layer.isActive()).thenReturn(false);
		layers = layer.filter(LayerFilter.ACTIVE);
		assertEquals(0, layers.length);

		// Composite
		Layer l1 = mock(Layer.class);
		Layer l2 = mock(Layer.class);
		when(l1.isActive()).thenReturn(false);
		when(l2.isActive()).thenReturn(true);
		layer = layerFactory.createLayer(l1, l2);

		layers = layer.filter(LayerFilter.ACTIVE);
		assertEquals(1, layers.length);
		assertEquals(l2, layers[0]);
	}

	public void testComposite() throws Exception {
		// Active and vectorial filter
		AndLayerFilter filter = new AndLayerFilter(
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

		Layer layer = layerFactory.createLayer(l1, l2, l3);

		Layer[] layers = layer.filter(filter);
		assertEquals(1, layers.length);
		assertEquals(l1, layers[0]);
	}

	public void testEditing() throws Exception {
		// Composite layer
		Layer l1 = mock(Layer.class);
		when(l1.isVectorial()).thenReturn(true);
		when(l1.isEditing()).thenReturn(true);
		Layer l2 = mock(Layer.class);
		when(l2.isVectorial()).thenReturn(false);
		when(l2.isEditing()).thenReturn(true);
		Layer l3 = mock(Layer.class);
		when(l3.isVectorial()).thenReturn(true);
		when(l3.isEditing()).thenReturn(false);

		Layer layer = layerFactory.createLayer(l1, l2, l3);
		Layer[] layers = layer.filter(LayerFilter.VECTORIAL_EDITING);
		assertEquals(1, layers.length);
		assertEquals(l1, layers[0]);

		// Vectorial layer
		layer = spy(layerFactory.createLayer(mock(Source.class)));
		when(layer.isEditing()).thenReturn(true);
		layers = layer.filter(LayerFilter.VECTORIAL_EDITING);
		assertEquals(1, layers.length);
		assertEquals(layer, layers[0]);

		layer = spy(layerFactory.createLayer(mock(Source.class)));
		when(layer.isEditing()).thenReturn(false);
		layers = layer.filter(LayerFilter.VECTORIAL_EDITING);
		assertEquals(0, layers.length);
	}

	public void testVectorial() throws Exception {
		// Vectorial layer
		Layer layer = layerFactory.createLayer(mock(Source.class));
		Layer[] layers = layer.filter(LayerFilter.VECTORIAL);
		assertEquals(1, layers.length);
		assertEquals(layer, layers[0]);

		// Composite layer
		Layer l1 = mock(Layer.class);
		Layer l2 = mock(Layer.class);
		when(l1.isVectorial()).thenReturn(false);
		when(l2.isVectorial()).thenReturn(true);
		layer = layerFactory.createLayer(l1, l2);

		layers = layer.filter(LayerFilter.VECTORIAL);
		assertEquals(1, layers.length);
		assertEquals(l2, layers[0]);
	}
}
