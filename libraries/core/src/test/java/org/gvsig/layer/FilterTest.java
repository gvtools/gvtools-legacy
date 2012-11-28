package org.gvsig.layer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.gvsig.layer.filter.CompositeLayerFilter;
import org.gvsig.layer.filter.LayerFilter;
import org.gvsig.layer.impl.CompositeLayer;
import org.gvsig.layer.impl.VectorialLayer;

public class FilterTest extends TestCase {

	public void testActive() throws Exception {
		CompositeLayer root = new CompositeLayer();
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

	public void testActiveSingle() throws Exception {
		// Active
		VectorialLayer l1 = new VectorialLayer(mock(Source.class));
		l1.activate();
		Layer[] layers = l1.filter(LayerFilter.ACTIVE);
		assertEquals(1, layers.length);
		assertEquals(l1, layers[0]);

		// Not active
		l1.deactivate();
		layers = l1.filter(LayerFilter.ACTIVE);
		assertEquals(0, layers.length);
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

		CompositeLayer root = new CompositeLayer();
		root.addLayer(l1);
		root.addLayer(l2);
		root.addLayer(l3);

		Layer[] layers = root.filter(filter);
		assertEquals(1, layers.length);
		assertEquals(l1, layers[0]);
	}

	public void testEditing() throws Exception {
		CompositeLayer root = new CompositeLayer();
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

	public void testEditingSingle() throws Exception {
		// Active
		VectorialLayer l1 = new VectorialLayer(mock(Source.class));
		l1.startEdition();
		Layer[] layers = l1.filter(LayerFilter.VECTORIAL_EDITING);
		assertEquals(1, layers.length);
		assertEquals(l1, layers[0]);

		// Not active
		l1.stopEdition();
		layers = l1.filter(LayerFilter.ACTIVE);
		assertEquals(0, layers.length);
	}

	public void testVectorial() throws Exception {
		CompositeLayer root = new CompositeLayer();
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
		VectorialLayer l1 = new VectorialLayer(mock(Source.class));
		Layer[] layers = l1.filter(LayerFilter.VECTORIAL);
		assertEquals(1, layers.length);
		assertEquals(l1, layers[0]);
	}
}
