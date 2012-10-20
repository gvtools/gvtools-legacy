package com.iver.gvsig.addeventtheme;

import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.edition.AfterFieldEditEvent;
import com.iver.cit.gvsig.fmap.edition.AfterRowEditEvent;
import com.iver.cit.gvsig.fmap.edition.BeforeFieldEditEvent;
import com.iver.cit.gvsig.fmap.edition.BeforeRowEditEvent;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IEditionListener;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerEvent;
import com.iver.cit.gvsig.fmap.layers.LayerListener;
import com.iver.utiles.DoubleUtilities;

public class AddEventThemListener implements LayerListener, IEditionListener {

	public void visibilityChanged(LayerEvent e) {

	}

	public void activationChanged(LayerEvent e) {

	}

	public void nameChanged(LayerEvent e) {

	}

	public void editionChanged(LayerEvent e) {
		if (e.getSource().isEditing()) {
			VectorialEditableAdapter vea = (VectorialEditableAdapter) ((FLyrVect) e
					.getSource()).getSource();
			vea.addEditionListener(this);
		}
	}

	public void processEvent(EditionEvent e) {
		// TODO Auto-generated method stub

	}

	public void beforeRowEditEvent(IRow feat, BeforeRowEditEvent e) {
		if (EditionEvent.CHANGE_TYPE_MODIFY == e.getChangeType()) {
			VectorialEditableAdapter vea = (VectorialEditableAdapter) e
					.getSource();
			// int numRow = (int) e.getNumRow();
			// DefaultFeature df = null;
			// df = (DefaultFeature) vea.getFeature(numRow);
			if (EditionEvent.GRAPHIC == e.getSourceType()) {
				double[] d = new double[4];
				IGeometry g = ((DefaultFeature) feat).getGeometry();
				g.getPathIterator(null).currentSegment(d);
				int[] xy = ((AddEventThemeDriver) vea.getDriver())
						.getFieldsIndex();
				Value[] values = ((DefaultFeature) feat).getAttributes();
				values[xy[0]] = ValueFactory.createValue(DoubleUtilities
						.format(d[0], ".".charAt(0), 6));
				values[xy[1]] = ValueFactory.createValue(DoubleUtilities
						.format(d[1], ".".charAt(0), 6));
				// feat = new DefaultFeature(g, values);
				// Value value0=vea.getRecordset().getFieldValue(numRow,xy[0]);
				// ((DoubleValue)value0).setValue(d[0]);
				// Value value1=vea.getRecordset().getFieldValue(numRow,xy[1]);
				// ((DoubleValue)value1).setValue(d[1]);
				// vea.setRow(numRow, df);
			} else if (EditionEvent.ALPHANUMERIC == e.getSourceType()) {
				IGeometry g = ((DefaultFeature) feat).getGeometry();
				int[] xy = ((AddEventThemeDriver) vea.getDriver())
						.getFieldsIndex();
				NumericValue x = (NumericValue) ((DefaultFeature) feat)
						.getAttribute(xy[0]);
				NumericValue y = (NumericValue) ((DefaultFeature) feat)
						.getAttribute(xy[1]);
				g = ShapeFactory
						.createPoint2D(x.doubleValue(), y.doubleValue());
				((DefaultFeature) feat).setGeometry(g);
				// feat = new DefaultFeature(g, ((DefaultFeature) feat)
				// .getAttributes());
				// vea.setRow(numRow, df);
			}
		}
	}

	public void afterRowEditEvent(IRow feat, AfterRowEditEvent e) {
		VectorialEditableAdapter vea = (VectorialEditableAdapter) e.getSource();
		if (EditionEvent.CHANGE_TYPE_ADD == e.getChangeType()) {
			if (EditionEvent.GRAPHIC == e.getSourceType()) {
				double[] d = new double[4];
				if (feat == null)
					return;
				IGeometry g = ((DefaultFeature) feat).getGeometry();
				g.getPathIterator(null).currentSegment(d);
				int[] xy = ((AddEventThemeDriver) vea.getDriver())
						.getFieldsIndex();
				Value[] values = ((DefaultFeature) feat).getAttributes();
				values[xy[0]] = ValueFactory.createValue(DoubleUtilities
						.format(d[0], ".".charAt(0), 6));
				values[xy[1]] = ValueFactory.createValue(DoubleUtilities
						.format(d[1], ".".charAt(0), 6));
			}
		}
	}

	public void beforeFieldEditEvent(BeforeFieldEditEvent e) {
		// TODO Auto-generated method stub

	}

	public void afterFieldEditEvent(AfterFieldEditEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.layers.LayerListener#drawValueChanged(com.iver
	 * .cit.gvsig.fmap.layers.LayerEvent)
	 */
	public void drawValueChanged(LayerEvent e) {
		// TODO Auto-generated method stub

	}

}
