/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package org.gvsig.symbology.gui.layerproperties;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.gvsig.gui.beans.swing.JBlank;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.symbology.fmap.labeling.GeneralLabelingStrategy;
import org.gvsig.symbology.fmap.labeling.OnSelectionLabeled;
import org.gvsig.symbology.gui.styling.LayerPreview;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.parser.ParseException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.SelectionSupport;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.DefaultLabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IZoomConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelingFactory;

public class DefaultLabeling extends AbstractLabelingMethodPanel implements ActionListener {
	private static final long serialVersionUID = 7100208944546698724L;
	private LabelClass defaultLabel;
	private IPlacementConstraints placementConstraints;
	private LayerPreview layerPrev;
	private JCheckBox enableLayerPrev;
	private LabelClassPreview labelPrev;
	private JButton btnProperties;
	private String[] fieldNames;
	private int[] fieldTypes;
	private boolean allowOverlap;
	private IZoomConstraints zoomConstraints;
	private LabelClassProperties lcProp;
	@Override
	public Class<? extends ILabelingMethod> getLabelingMethodClass() {
		return DefaultLabelingMethod.class;
	}

	@Override
	public String getName() {
		return PluginServices.getText(this, "label_features_in_the_same_way")+".";
	}

	@Override
	public void fillPanel(ILabelingMethod method, SelectableDataSource dataSource) {
		try {
			if (enableLayerPrev.isSelected()) {
				layerPrev.setLayer(layer);
			}
			else {
				layerPrev.setLayer(null);
			}

			fieldNames = dataSource.getFieldNames();
			fieldTypes = new int[fieldNames.length];
			for (int i = 0; i < fieldTypes.length; i++) {
				fieldTypes[i] = dataSource.getFieldType(i);
			}
			ILabelingStrategy labeling = layer.getLabelingStrategy();
			if (!(labeling instanceof GeneralLabelingStrategy)) {
				labeling = new GeneralLabelingStrategy();
				// (!!)
				labeling.setLayer(layer);
				layer.setLabelingStrategy(labeling);
			}

		} catch (Exception e) {
			NotificationManager.addWarning(e.getMessage(), e);
		}

		LabelClass lc = null;
		if (method.getLabelClasses() != null && method.getLabelClasses().length > 0) {
			lc = method.getLabelClasses()[0];
		} else {
			lc = new LabelClass();
		}
		setLabel(lc);
		getLcProp();
	}

	private JButton getBtnProperties(){
		if (btnProperties == null){
			btnProperties = new JButton(PluginServices.getText(this, "properties"));
			btnProperties.addActionListener(this);
		}
		return btnProperties;
	}

	private LabelClassPreview getLabelPrev(){
		if (labelPrev == null){
			labelPrev = new LabelClassPreview();
		}
		return labelPrev;
	}

	private JCheckBox getEnableLayerPreview(){
		if(enableLayerPrev == null){
			enableLayerPrev = new JCheckBox(
					PluginServices.getText(this, "Enable_layer_preview"));
			enableLayerPrev.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					try {
						if (e.getStateChange()==ItemEvent.SELECTED) {
							layerPrev.setLayer(layer);
						}
						else if (e.getStateChange()==ItemEvent.DESELECTED) {
							layerPrev.setLayer(null);
						}
					} catch (ExpansionFileReadException e1) {
						PluginServices.getLogger().error(e1.getMessage(), e1);
					} catch (ReadDriverException e1) {
						PluginServices.getLogger().error(e1.getMessage(), e1);
					}
				}
			});
		}
		return enableLayerPrev;
	}

	private LayerPreview getLayerPreview(){
		if (layerPrev == null){
			layerPrev = new LayerPreview();
		}
		return layerPrev;
	}
	@Override
	protected void initializePanel() {
		setLayout(new BorderLayout());
		JSplitPane scrl = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		scrl.add(getLayerPreview(), JSplitPane.LEFT);

		labelPrev = getLabelPrev(); //new LabelClassPreview();
		JPanel aux = new JPanel(new BorderLayout());
		aux.add(new JBlank(10, 10), BorderLayout.NORTH );
		aux.add(new JBlank(10, 10), BorderLayout.WEST );
		aux.add(labelPrev, BorderLayout.CENTER);
		aux.add(new JBlank(10, 10), BorderLayout.EAST );
		JPanel aux2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		btnProperties = getBtnProperties();
		aux2.add(btnProperties);
//		btnProperties.addActionListener(this);
		aux.add(aux2, BorderLayout.SOUTH);
		scrl.add(aux, JSplitPane.RIGHT);
		add(scrl, BorderLayout.CENTER);
		scrl.setDividerLocation(500);

		getEnableLayerPreview().setSelected(false);
		add(enableLayerPrev, BorderLayout.SOUTH);
	}

	private LabelClassProperties getLcProp(){
		if(lcProp == null){
			lcProp = new LabelClassProperties(fieldNames, fieldTypes);
			lcProp.setLabelClass(method.getLabelClasses()[0]);
		}
		return lcProp;

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(getBtnProperties())) {
//			LabelClassProperties lcProp = new LabelClassProperties(fieldNames, fieldTypes);
//			lcProp.setLabelClass(method.getLabelClasses()[0]);
//			PluginServices.getMDIManager().addWindow(lcProp);
//			setLabel(lcProp.getLabelClass());
			LabelClassProperties lcProp = getLcProp();
			LabelClass lc = defaultLabel;
			lcProp.setLabelClass(lc);
			boolean eval = false;
			while (!eval){
				PluginServices.getMDIManager().addWindow(lcProp);
				if(!lcProp.isAccepted()){ break; };
				lc = lcProp.getLabelClass();
				eval = evaluateSQL(lc);
			}
			setLabel(lc);
		}
	}

	private boolean evaluateSQL(LabelClass lc){
		if(lc.isUseSqlQuery()){
			String sqlQuery = lc.getSQLQuery();
			if (sqlQuery.compareToIgnoreCase("")!=0){
				SelectableDataSource recordset = null;
				try {
					recordset = layer.getRecordset();
				} catch (ReadDriverException e1) {
					NotificationManager.addError(e1);
					return false;
				}
				if (recordset != null){
					String rName = recordset.getName();
					sqlQuery = "select * from '"+rName+ "' where "+sqlQuery+";";
					try {
						recordset.getDataSourceFactory().executeSQL(sqlQuery, 0);
					} catch (DriverLoadException e1) {
						NotificationManager.addError(e1);
						return false;
					} catch (ReadDriverException e1) {
						NotificationManager.addError(e1);
						return false;
					} catch (ParseException e1) {
						NotificationManager.showMessageError(PluginServices.getText(this, "error_coding_filter_query"), e1);
						return false;
					} catch (SemanticException e1) {
						NotificationManager.showMessageError(PluginServices.getText(this, "error_coding_filter_query"), e1);
						return false;
					} catch (EvaluationException e1) {
						NotificationManager.showMessageError(PluginServices.getText(this, "error_validating_filter_query"), e1);
						return false;
					} catch (Error e1) {
						NotificationManager.showMessageError(PluginServices.getText(this, "error_coding_filter_query"),new Exception(e1));
						return false;
					}
				}
			}
		}
		return true;
	}

	private void setLabel(LabelClass labelClass) {
		defaultLabel = LabelingFactory.createLabelClassFromXML(labelClass.getXMLEntity());
		labelPrev.setLabelClass(defaultLabel);
		method = newMethodForThePreview(defaultLabel);

		updatePreview();

	}

	protected ILabelingMethod newMethodForThePreview(LabelClass defaultLabel) {
		return new DefaultLabelingMethod(defaultLabel);
	}

	private void updatePreview() {
		GeneralLabelingStrategy s = (GeneralLabelingStrategy) layer.getLabelingStrategy();
		if (method == null){
			s.setLabelingMethod(newMethodForThePreview(defaultLabel));
		} else {
			s.setLabelingMethod(method);
		}
//		s.setPlacementConstraints(placementConstraints);
//		s.setAllowOverlapping(allowOverlap);
//		s.setZoomConstraints(zoomConstraints);

		layer.setIsLabeled(true);

		/* If the selected labeling method is OnSelectionLabeled we have to select some rows
		 * in the previous sample image*/
		if(s.getLabelingMethod() instanceof OnSelectionLabeled) {

			try {

				IFeatureIterator it = layer.getSource().getFeatureIterator();
				SelectionSupport selectionSupport = new SelectionSupport();
				FBitSet bitSet = new FBitSet();
				long rowCount = layer.getRecordset().getRowCount();

				for (int i = 0; i <= rowCount/4; i++) {
					bitSet.set(i);
				}

				selectionSupport.setSelection(bitSet);
				layer.getRecordset().setSelectionSupport(selectionSupport);

			} catch (ReadDriverException e) {
				NotificationManager.addError(PluginServices.getText(this, "accessing_file_structure"), e);
			}
		}


		try {
			Rectangle r = layerPrev.getBounds();
			r.setLocation(layerPrev.getLocationOnScreen());
			layerPrev.paintImmediately(r);
			layerPrev.doLayout();
		} catch (IllegalComponentStateException ex) {
			// this happens when the component is not showing in the
			// screen. If that is the case, then we don't need to do
			// anything.
		}

	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		GeneralLabelingStrategy s = (GeneralLabelingStrategy) layer.
		getLabelingStrategy();

		if (AbstractLabelingMethodPanel.PLACEMENT_CONSTRAINTS.equals(prop)) {
			placementConstraints = (IPlacementConstraints) evt.getNewValue();
			s.setPlacementConstraints(placementConstraints);
		} else if (AbstractLabelingMethodPanel.ALLOW_OVERLAP.equals(prop)) {
			allowOverlap = (Boolean) evt.getNewValue();
			s.setAllowOverlapping(allowOverlap);
		} else if (AbstractLabelingMethodPanel.ZOOM_CONSTRAINTS.equals(prop)) {
			zoomConstraints = (IZoomConstraints) evt.getNewValue();
			s.setZoomConstraints(zoomConstraints);
		}

		updatePreview();
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (layerPrev!=null) {
			layerPrev.setEnabled(enabled);
		};
		if (labelPrev!=null) {
			labelPrev.setEnabled(enabled);
		};
		if (btnProperties!=null) {
			btnProperties.setEnabled(enabled);
		};
	}
}
