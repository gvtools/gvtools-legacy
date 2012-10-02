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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.symbology.fmap.labeling.ExtendedLabelingFactory;
import org.gvsig.symbology.fmap.labeling.GeneralLabelingStrategy;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.DefaultLabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IZoomConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelingFactory;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ZoomConstraintsImpl;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ILabelingStrategyPanel;
import com.iver.utiles.swing.JComboBox;


public class GeneralLabeling extends JPanel implements ILabelingStrategyPanel, ActionListener {
	private static final long serialVersionUID = 8864709758980903351L;
	private static Comparator comparator=new Comparator<Class<? extends ILabelingMethod>>(){
		public int compare(Class<? extends ILabelingMethod> o1,
				Class<? extends ILabelingMethod> o2) {
			return o1.getName().compareTo(o2.getName());
		}};
	private static TreeMap<
			Class<? extends ILabelingMethod>,
			Class<? extends AbstractLabelingMethodPanel>
		> methods
		= new TreeMap<
			Class<? extends ILabelingMethod>,
			Class<? extends AbstractLabelingMethodPanel>
		>(comparator);
	private JButton btnVisualization;
	private JButton btnPlacement;
	private JComboBox cmbMethod;
	private JPanel methodPanel;
	private IPlacementConstraints placementConstraints;
	private IZoomConstraints zoomConstraints;
	private boolean noEvent;
	private FLyrVect targetLayer;
	private JCheckBox chkAllowLabelOverlapping;
	private FLyrVect auxLayer;
	private GeneralLabelingStrategy gStr;
	private AbstractLabelingMethodPanel previousMethodPanel = null;
	public GeneralLabeling() {
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		JPanel center = new JPanel(new BorderLayout(10, 10));
		center.setBorder(BorderFactory.createTitledBorder(
				null, PluginServices.getText(this, "classes")));
		JPanel aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5,0));
		aux.add(new JLabel(PluginServices.getText(this, "method")+":"));
		aux.add(getCmbMethod());
		aux.setPreferredSize(new Dimension(605, 40));
		center.add(aux, BorderLayout.NORTH);


		// el panell del mètode de moltes FeatureDependantLabelingMethod
		methodPanel = getMethodPanel();
		center.add(methodPanel, BorderLayout.CENTER);
		add(center, BorderLayout.CENTER);

		JPanel south = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
		south.setBorder(BorderFactory.createTitledBorder(
				null, PluginServices.getText(this, "options")));
		south.add(getBtnVisualization());
		south.add(getBtnPlacement());
		south.add(getChkAllowLabelOverlapping());
		south.setPreferredSize(new Dimension(612, 60));

		add(south, BorderLayout.SOUTH);
	}


	private JPanel getMethodPanel() {
		if (methodPanel == null){
			methodPanel = new JPanel(new BorderLayout(10, 0));
		}
		return methodPanel;
	}

	private JCheckBox getChkAllowLabelOverlapping() {
		if (chkAllowLabelOverlapping == null) {
			chkAllowLabelOverlapping = new JCheckBox(PluginServices.getText(this, "allow_label_overlapping"));
			chkAllowLabelOverlapping.addActionListener(this);
		}
		return chkAllowLabelOverlapping;
	}

	private void refreshControls() {
		// fires an event from the methods combo box
		actionPerformed(new ActionEvent(getCmbMethod(), 0, null));
	}

	private JButton getBtnVisualization() {
		if (btnVisualization == null) {
			btnVisualization = new JButton(
					PluginServices.getText(this, "visualization")+"...");
			btnVisualization.setName("BTNVISUALIZATION");
			btnVisualization.addActionListener(this);
		}
		return btnVisualization;
	}

	private JButton getBtnPlacement() {
		if (btnPlacement == null) {
			btnPlacement = new JButton(
					PluginServices.getText(this, "placement")+"...");
			btnPlacement.setName("BTNPLACEMENT");
			btnPlacement.addActionListener(this);
		}
		return btnPlacement;
	}

	private JComboBox getCmbMethod() {
		if (cmbMethod == null) {
			Iterator<Class<? extends AbstractLabelingMethodPanel>> it = methods.values().iterator();
			ArrayList<AbstractLabelingMethodPanel> panels = new ArrayList<AbstractLabelingMethodPanel>();
			while (it.hasNext()) {
				try {
					panels.add(it.next().newInstance());
				} catch (Exception e) {
					throw new Error(e);
				}
			}
			cmbMethod = new JComboBox(panels.toArray());
			cmbMethod.setSize(new Dimension(300, 22));
			cmbMethod.setName("CMBMETHOD");
			cmbMethod.addActionListener(this);
		}
		return cmbMethod;
	}

	public ILabelingStrategy getLabelingStrategy() {
		ILabelingStrategy st = ExtendedLabelingFactory.
						createStrategy((FLayer) targetLayer,
								getMethod(),
								getPlacementConstraints(),
								getZoomConstraints());
		if (st instanceof GeneralLabelingStrategy) {
			GeneralLabelingStrategy gStr = (GeneralLabelingStrategy) st;
			gStr.setAllowOverlapping(getChkAllowLabelOverlapping().isSelected());
			gStr.setZoomConstraints(getZoomConstraints());
		}
		return st;
	}

	public void setModel(FLayer layer, ILabelingStrategy str) {
		if (layer instanceof FLyrVect) {
			try {
				targetLayer = (FLyrVect) layer;//.cloneLayer();

				VectorialDriver vd = (VectorialDriver) ((FLyrVect) layer).getSource().getDriver();

				auxLayer = (FLyrVect) LayerFactory.createLayer(layer.getName(),vd, layer.getCrs());
				auxLayer.setParentLayer(layer.getParentLayer());
				auxLayer.setLegend((IVectorLegend)targetLayer.getLegend());

				
				if (auxLayer.getCrs() == null) {
					Logger.getLogger(getClass()).debug("Possible bug detected in " +
							"FLyrVect.cloneLayer() (missing projection in cloned layer)");
					// this line should be unnecessary (and included in cloneLayer) method);
					auxLayer.setCrs(targetLayer.getCrs());
				}
				//

				if (str instanceof GeneralLabelingStrategy) {
					gStr = (GeneralLabelingStrategy) LabelingFactory.
						createStrategyFromXML(str.getXMLEntity(), auxLayer/*layer.cloneLayer()*/);
					auxLayer.setLabelingStrategy(gStr);
					gStr.setLayer(auxLayer);
					setMethod(gStr.getLabelingMethod(), auxLayer);
					placementConstraints = gStr.getPlacementConstraints();
					zoomConstraints = gStr.getZoomConstraints();
					getChkAllowLabelOverlapping().setSelected(gStr.isAllowingOverlap());
				}
			} catch (ReadDriverException e) {
				NotificationManager.addError(PluginServices.getText(this, "accessing_file_structure"), e);
			} catch (Exception e) {
				NotificationManager.addError(PluginServices.getText(this, "accessing_file_structure"), e);
			}
			refreshControls();
		}
	}


	public static void addLabelingMethod(Class<? extends AbstractLabelingMethodPanel> iLabelingMethodClass) {
		try {
			methods.put(
					iLabelingMethodClass.newInstance().getLabelingMethodClass(),
					iLabelingMethodClass);
		} catch (Exception e) {
			NotificationManager.addError(
					PluginServices.getText(GeneralLabeling.class, "cannot_install_labeling_method"), e);
		}
	}

	private void setMethod(ILabelingMethod labelingMethod, FLyrVect srcLayer) {
		getMethodPanel().removeAll();
		AbstractLabelingMethodPanel p;
		try {
			p = methods.get(labelingMethod.getClass()).newInstance();
			System.out.println("last labeling method was"+p.getClass().getName());
			p.setModel(labelingMethod, srcLayer);
			cmbMethod.setSelectedItem(p);
		} catch (Exception e) {
			// should be impossible;
			NotificationManager.addWarning(e.getLocalizedMessage());
		}

	}

	private ILabelingMethod getMethod() {
		AbstractLabelingMethodPanel p = ((AbstractLabelingMethodPanel)cmbMethod.getSelectedItem());
		if(p != null){
			return p.getMethod();
		}

		return new DefaultLabelingMethod();
	}

	private IZoomConstraints getZoomConstraints() {
		if (zoomConstraints == null) {
			zoomConstraints = new ZoomConstraintsImpl();
		}
		return zoomConstraints;
	}

	private IPlacementConstraints getPlacementConstraints() {
		return placementConstraints;
	}

	public void actionPerformed(ActionEvent e) {
		System.err.println("GeneralLabeling.actionPerformed() "+((Component)e.getSource()).getName());
		if (noEvent) return;
		JComponent c = (JComponent)e.getSource();

		if (c.equals(btnPlacement)) {

			try {
				IPlacementConstraints oldValue = getPlacementConstraints();
				IPlacementProperties pp = PlacementProperties.createPlacementProperties(
						getPlacementConstraints(),
						((FLyrVect) auxLayer).getShapeType());
				PluginServices.getMDIManager().addWindow(pp);
				placementConstraints = pp.getPlacementConstraints();

				((AbstractLabelingMethodPanel) cmbMethod.getSelectedItem()).
					propertyChange(new PropertyChangeEvent(
							this,
							AbstractLabelingMethodPanel.PLACEMENT_CONSTRAINTS,
							oldValue,
							placementConstraints));

			} catch (ClassCastException ccEx) {
				NotificationManager.addError(
						"Placement constraints not prepared for:"
						+auxLayer.getClass().getName(),
						ccEx);
			} catch (ReadDriverException dEx) {
				NotificationManager.addWarning(
						"Should be unreachable code",
						dEx);
				NotificationManager.addError(
						PluginServices.getText(
								this,
								"usupported_layer_type"),
						dEx);
			}

		} else if (c.equals(btnVisualization)) {
			IZoomConstraints oldValue = getZoomConstraints();
			LabelScaleRange lsr = new LabelScaleRange(oldValue.getMinScale(), oldValue.getMaxScale());
			PluginServices.getMDIManager().addWindow(lsr);
			zoomConstraints = new ZoomConstraintsImpl();
			zoomConstraints.setMaxScale(lsr.getMaxScale());
			zoomConstraints.setMinScale(lsr.getMinScale());
			zoomConstraints.setMode(
					lsr.getMaxScale() ==-1 && lsr.getMinScale() == -1 ?
							IZoomConstraints.DEFINED_BY_THE_LAYER :
							IZoomConstraints.DEFINED_BY_THE_USER);

			((AbstractLabelingMethodPanel) cmbMethod.getSelectedItem()).
			propertyChange(new PropertyChangeEvent(
					this,
					AbstractLabelingMethodPanel.ZOOM_CONSTRAINTS,
					oldValue,
					getZoomConstraints()));

		} else if (c.equals(chkAllowLabelOverlapping)) {
			boolean newValue = chkAllowLabelOverlapping.isSelected();
			((AbstractLabelingMethodPanel) cmbMethod.getSelectedItem()).
			propertyChange(new PropertyChangeEvent(
					this,
					AbstractLabelingMethodPanel.ALLOW_OVERLAP,
					!newValue,
					newValue));

		} else if (c.equals(cmbMethod)) {
			AbstractLabelingMethodPanel p = (AbstractLabelingMethodPanel) cmbMethod.getSelectedItem();
			if(previousMethodPanel == null || previousMethodPanel != p ){
				Container cont = methodPanel.getParent();
				cont.remove(methodPanel);
				emptyContainer(methodPanel);

				try {
					if(gStr != null){
						if(gStr.getLabelingMethod() != null){
							p.setModel(gStr.getLabelingMethod(), auxLayer);
						}
					} else {
						p.setModel(getMethod(),auxLayer);
					}
					methodPanel.add(
							p,
							BorderLayout.CENTER);

					methodPanel.repaint();
					setVisible(false);
					setVisible(true);
				} catch (ReadDriverException e1) {
					NotificationManager.addInfo(new Date(System.currentTimeMillis()).toString(), e1);
				}
				cont.add(methodPanel, BorderLayout.CENTER);
			}
			previousMethodPanel = p;
		}
		System.out.println("GeneralLabeling.actionPerformed() exit "+((Component)e.getSource()).getName());
	}

	private void emptyContainer(Container c) {
		for (int i = 0; i < c.getComponentCount(); i++) {
			if (c.getComponent(i) instanceof Container) {
				emptyContainer((Container) c.getComponent(i));
			}
			c.remove(i);
		}
	}

	public String getLabelingStrategyName() {
		return PluginServices.getText(this, "user_defined_labels");
	}

	public Class<? extends ILabelingStrategy> getLabelingStrategyClass() {
		return GeneralLabelingStrategy.class;
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getBtnPlacement().setEnabled(enabled);
		getBtnVisualization().setEnabled(enabled);
		getChkAllowLabelOverlapping().setEnabled(enabled);
		getCmbMethod().setEnabled(enabled);
		JPanel mp = getMethodPanel();//.setEnabled(enabled);
		mp.setEnabled(enabled);
		for (int i=0; i<mp.getComponentCount(); i++){
			Component c = mp.getComponent(i);
			c.setEnabled(enabled);
		}

	}
}

/*
class _GeneralLabeling extends JPanel implements ILabelingStrategyPanel, ActionListener {
	private static final long serialVersionUID = 8864709758980903351L;
	private static Hashtable<String, Class<? extends ILabelingMethod>> methods
		= new Hashtable<String, Class<? extends ILabelingMethod>>();
	private static int newClassSuffix = 0;
	private JButton btnVisualization;
	private JButton btnRenameClass;
	private JButton btnSQLQuery;
	private JButton btnDelClass;
	private JButton btnAddClass;
	private JButton btnPlacement;
	private JComboBoxLabelingMethod cmbMethod;
	private GridBagLayoutPanel classesPanel;
	private JComboBox cmbClasses;
	private IPlacementConstraints placementConstraints;
	private IZoomConstraints zoomConstraints;
	private long minScaleView = -1, maxScaleView = -1;
	private boolean noEvent;
	private JCheckBox chkLabel;
	private JCheckBox chkTextOnly;
	private FLyrVect layer;
	private String[] fieldNames;
	private JDnDList lstClassPriorities;
	private JCheckBox chkDefinePriorities;
	private JScrollPane scrlPan;
	private LabelClassRenderingProperties labelClassRenderingProperties;
	private JCheckBox chkAllowLabelOverlapping;

	public _GeneralLabeling() {
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		GridBagLayoutPanel left = new GridBagLayoutPanel();
		JPanel aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5,0));
		aux.add(new JLabel(PluginServices.getText(this, "method")+":"));
		aux.add(getCmbMethod());
		aux.setPreferredSize(new Dimension(605, 40));
		left.addComponent(aux);


		classesPanel = new GridBagLayoutPanel();
		aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5,0));
		aux.add(new JLabel(PluginServices.getText(this, "class")+":"));
		aux.add(getCmbClasses());
		aux.add(getChkLabelFeatures());
		aux.setPreferredSize(new Dimension(602, 40));
		classesPanel.addComponent(aux);

		aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
		aux.add(getBtnAddClass());
		aux.add(getBtnDelClass());
		aux.add(getBtnRenameClass());
		aux.add(getBtnSQLQuery());
		classesPanel.addComponent(aux);
		classesPanel.setBorder(BorderFactory.createTitledBorder(
				null, PluginServices.getText(this, "classes")));
		left.addComponent(classesPanel);


		left.addComponent(labelClassRenderingProperties = new LabelClassRenderingProperties());
		aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
		aux.setBorder(BorderFactory.createTitledBorder(
				null, PluginServices.getText(this, "options")));
		aux.add(getBtnVisualization());

		aux.add(getBtnPlacement());
		aux.add(getChkAllowLabelOverlapping());
		aux.setPreferredSize(new Dimension(612, 60));

		left.addComponent(aux);

		add(left, BorderLayout.CENTER);

		JPanel right = new JPanel(new BorderLayout(15, 15));
		aux = new JPanel(new BorderLayout(15, 15));
		aux.add(getChkDefinePriorities(), BorderLayout.NORTH);
		aux.add(getScrlClassPriorities(), BorderLayout.CENTER);
		right.add(new JBlank(10, 10), BorderLayout.NORTH);
		right.add(aux, BorderLayout.CENTER);
		right.add(new JBlank(10, 10), BorderLayout.SOUTH);
		right.add(new JBlank(10, 10), BorderLayout.EAST);
		add(right, BorderLayout.EAST);
	}


	private JCheckBox getChkAllowLabelOverlapping() {
		if (chkAllowLabelOverlapping == null) {
			chkAllowLabelOverlapping = new JCheckBox(PluginServices.getText(this, "allow_label_overlapping"));

		}

		return chkAllowLabelOverlapping;
	}

	private Component getScrlClassPriorities() {
		if (scrlPan == null) {
			scrlPan = new JScrollPane();
			scrlPan.setViewportView(getLstClassPriorities());
			scrlPan.setPreferredSize(new Dimension(180, 300));
		}
		return scrlPan;
	}

	private JCheckBox getChkDefinePriorities() {
		if (chkDefinePriorities == null) {
			chkDefinePriorities = new JCheckBox(PluginServices.getText(this, "label_priority"));
			chkDefinePriorities.addActionListener(this);
			chkDefinePriorities.setName("CHK_DEFINE_PRIORITIES");
		}
		return chkDefinePriorities;
	}

	private JDnDList getLstClassPriorities() {
		if (lstClassPriorities == null) {
			lstClassPriorities = new JDnDList();
			lstClassPriorities.setName("CLASS_PRIORITY_LIST");
			lstClassPriorities.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					JDnDListModel m = (JDnDListModel) lstClassPriorities.getModel();
					for (int i = 0; i < m.getSize(); i++) {
						((LabelClass) m.getElementAt(i)).setPriority(i);
					}
				}
			});

		}
		return lstClassPriorities;
	}

	private void refreshControls() {
		// classes combo box
		refreshClasses();

		// fires an event from the methods combo box
		actionPerformed(new ActionEvent(getCmbMethod(), 0, null));
	}

	private JButton getBtnVisualization() {
		if (btnVisualization == null) {
			btnVisualization = new JButton(
					PluginServices.getText(this, "visualization")+"...");
			btnVisualization.setName("BTNVISUALIZATION");
			btnVisualization.addActionListener(this);
		}
		return btnVisualization;
	}

	private JButton getBtnPlacement() {
		if (btnPlacement == null) {
			btnPlacement = new JButton(
					PluginServices.getText(this, "placement")+"...");
			btnPlacement.setName("BTNPLACEMENT");
			btnPlacement.addActionListener(this);
		}
		return btnPlacement;
	}

	private JComboBox getCmbMethod() {
		if (cmbMethod == null) {
			Iterator<String> it = methods.keySet().iterator();
			ArrayList<MethodItem> aux = new ArrayList<MethodItem>();
			while (it.hasNext()) {
				String name = it.next();

				Class<? extends ILabelingMethod> methodClass = (Class<? extends ILabelingMethod>) methods.get(name);
				ILabelingMethod method;
				try {
					method = (ILabelingMethod) methodClass.newInstance();
					MethodItem newItem = new MethodItem(name, method);
					aux.add(newItem);

				} catch (InstantiationException e) {
					NotificationManager.addError("Trying to instantiate an interface" +
							" or abstract class + "+methodClass.getName(), e);
				} catch (IllegalAccessException e) {
					NotificationManager.addError("IllegalAccessException: does " +
							methodClass.getName()	+ " class have an anonymous" +
							" constructor?", e);
				}

			}
			cmbMethod = new JComboBoxLabelingMethod(aux.toArray(new MethodItem[0]));
			cmbMethod.setSize(new Dimension(300, 22));
			cmbMethod.setName("CMBMETHOD");
			cmbMethod.addActionListener(this);
		}
		return cmbMethod;
	}

	private JButton getBtnSQLQuery() {
		if (btnSQLQuery == null) {
			btnSQLQuery = new JButton(PluginServices.getText(this, "SQL_query"));
			btnSQLQuery.setName("BTNSQLQUERY");
			btnSQLQuery.addActionListener(this);
		}
		return btnSQLQuery;
	}

	private JButton getBtnRenameClass() {
		if (btnRenameClass == null) {
			btnRenameClass = new JButton(PluginServices.getText(this, "remane_class"));
			btnRenameClass.setName("BTNRENAMECLASS");
			btnRenameClass.addActionListener(this);
		}
		return btnRenameClass;
	}

	private JButton getBtnDelClass() {
		if (btnDelClass == null) {
			btnDelClass = new JButton(PluginServices.getText(this, "delete_class"));
			btnDelClass.setName("BTNDELCLASS");
			btnDelClass.addActionListener(this);
		}
		return btnDelClass;
	}

	private JButton getBtnAddClass() {
		if (btnAddClass == null) {
			btnAddClass = new JButton(PluginServices.getText(this, "add_class"));
			btnAddClass.setName("BTNADDCLASS");
			btnAddClass.addActionListener(this);
		}
		return btnAddClass;
	}

	private JCheckBox getChkLabelFeatures() {
		if (chkLabel == null) {
			chkLabel = new JCheckBox();
			chkLabel.setText(PluginServices.getText(this, "label_features_in_this_class"));
			chkLabel.setName("CHKLABEL");
			chkLabel.addActionListener(this);
		}
		return chkLabel;
	}

	private JComboBox getCmbClasses() {
		if (cmbClasses == null) {
			cmbClasses = new JComboBox();
			cmbClasses.setPreferredSize(new Dimension(150, 20));
			cmbClasses.setName("CMBCLASSES");
			cmbClasses.addActionListener(this);
		}
		return cmbClasses;
	}

	public ILabelingStrategy getLabelingStrategy() {
		ILabelingStrategy st = ExtendedLabelingFactory.
						createStrategy((FLayer) layer,
								getMethod(),
								getPlacementConstraints(),
								getZoomConstraints());
		if (st instanceof GeneralLabelingStrategy) {
			GeneralLabelingStrategy gStr = (GeneralLabelingStrategy) st;
			gStr.setAllowOverlapping(getChkAllowLabelOverlapping().isSelected());
			gStr.setMinScaleView(minScaleView);
			gStr.setMaxScaleView(maxScaleView);
		}
		return st;
	}

	public void setModel(FLayer layer, ILabelingStrategy str) {
		if (layer instanceof FLyrVect) {
			this.layer = (FLyrVect) layer;
			FLyrVect lv = (FLyrVect) layer;
			try {
				fieldNames = lv.getRecordset().getFieldNames();
				labelClassRenderingProperties.setFieldNames(fieldNames);
				if (str instanceof GeneralLabelingStrategy) {
					try {
						GeneralLabelingStrategy gStr = (GeneralLabelingStrategy) LabelingFactory.createStrategyFromXML(str.getXMLEntity(), layer.cloneLayer());
						setMethod(str.getLabelingMethod());
						placementConstraints = str.getPlacementConstraints();
						getChkAllowLabelOverlapping().setSelected(gStr.isAllowingOverlap());
						minScaleView = gStr.getMinScaleView();
						maxScaleView = gStr.getMaxScaleView();

					} catch (ReadDriverException e) {
						NotificationManager.addError(PluginServices.getText(this, "accessing_file_structure"), e);
					} catch (Exception e) {
						NotificationManager.addError(PluginServices.getText(this, "accessing_file_structure"), e);
					}
				}

				refreshControls();
				labelClassRenderingProperties.setModel(getActiveClass());

			} catch (ReadDriverException e) {
				NotificationManager.addError(PluginServices.getText(this, "accessing_file_structure"), e);
			}
		}
	}


	public static void addLabelingMethod(String name, Class<? extends ILabelingMethod> iLabelingMethodClass) {
		methods.put(name, iLabelingMethodClass);
	}

	private void setMethod(ILabelingMethod labelingMethod) {
		getCmbMethod().setSelectedItem(new MethodItem(null, labelingMethod));
	}

	private ILabelingMethod getMethod() {
		return ((MethodItem) getCmbMethod().getSelectedItem()).method;
	}

	private void refreshClasses() {
		// label classes
		getCmbClasses().removeAllItems();
		LabelClass[] lClasses = getMethod().getLabelClasses();
		for (int i = 0; i < lClasses.length; i++) {
			getCmbClasses().addItem(lClasses[i]);

		}

		labelClassRenderingProperties.setModel(
				(LabelClass) getCmbClasses().getSelectedItem());
		// expressions combo box
//		refreshCmbExpressions();

		// panel priorities
		refreshPnlPriorities();

	}

	private void refreshPnlPriorities() {
		TreeSet<LabelClass> ts = new TreeSet<LabelClass>(new LabelClassComparatorByPriority());

		LabelClass[] lClasses = getMethod().getLabelClasses();
		for (int i = 0; i < lClasses.length; i++) {
			ts.add(lClasses[i]);
		}

		// refresh label priority panel
		getChkDefinePriorities().setSelected(getMethod().definesPriorities());
		JDnDListModel m = new JDnDListModel();
		for (LabelClass labelClass : ts) {
			m.addElement(labelClass);
		}
		getLstClassPriorities().setModel(m);

	}

	private LabelClass getActiveClass() {
		return (LabelClass) getCmbClasses().getSelectedItem();
	}

	private IZoomConstraints getZoomConstraints() {
		return zoomConstraints;
	}

	private IPlacementConstraints getPlacementConstraints() {
		return placementConstraints;
	}

	public void actionPerformed(ActionEvent e) {
		if (noEvent) return;
		JComponent c = (JComponent)e.getSource();

		if (c.equals(btnAddClass)) {
			LabelClass newClass = new LabelClass();
			newClass.setName(PluginServices.getText(this, "labeling")+String.valueOf(++newClassSuffix));
			getMethod().addLabelClass(newClass);
			refreshClasses();
			getCmbClasses().setSelectedItem(newClass);
		} else if (c.equals(btnDelClass)) {
			LabelClass clazz = getActiveClass();
			getMethod().deleteLabelClass(clazz);
			refreshClasses();
		} else if (c.equals(btnRenameClass)) {
			LabelClass clazz = getActiveClass();
			String newName = JOptionPane.showInputDialog(
					PluginServices.getText(this, "enter_new_name"));
			if (newName != null)
				getMethod().renameLabelClass(clazz, newName);
			refreshClasses();
		} else if (c.equals(btnSQLQuery)) {
			LabelClass clazz = getActiveClass();
			String query = clazz.getSQLQuery();
			query = JOptionPane.showInputDialog(null,
					  "select from "+layer.getName()+" where ",
					  "SQL query",
					  JOptionPane.QUESTION_MESSAGE);
			if (!query.equals("")) {
				clazz.setSQLQuery(query);
			}

		} else  if (c.equals(chkLabel)) {
			LabelClass lc = (LabelClass) getCmbClasses().getSelectedItem();
			if (lc == null)
				lc = getMethod().getDefaultLabelClass();
			lc.setVisible(chkLabel.isSelected());
		} else if (c.equals(chkTextOnly)) {

		} else if (c.equals(cmbClasses)) {
			// refresh expressions
//			LabelClass lc = (LabelClass) cmbClasses.getSelectedItem();
//			if (lc != null && lc.getLabelExpression() != null && !getExpressions().contains(lc.getLabelExpression())) {
//				getExpressions().add(0, lc.getLabelExpression());
//			}
//			refreshCmbExpressions();
		} else if (c.equals(btnPlacement)) {

			try {
				IPlacementProperties pp = PlacementProperties.createPlacementProperties(
						getPlacementConstraints(),
						((FLyrVect) layer).getShapeType());
				PluginServices.getMDIManager().addWindow(pp);
				placementConstraints = pp.getPlacementConstraints();
			} catch (ClassCastException ccEx) {
				NotificationManager.addError("Placement constraints not prepared for:"
						+layer.getClass().getName(),
						ccEx);
			} catch (ReadDriverException dEx) {
				NotificationManager.addWarning("Should be unreachable code", dEx);
				NotificationManager.addError(PluginServices.getText(this, "usupported_layer_type"), dEx);
			}

		} else if (c.equals(btnVisualization)) {
			LabelScaleRange lsr = new LabelScaleRange(minScaleView, maxScaleView);
			PluginServices.getMDIManager().addWindow(lsr);
			minScaleView = lsr.getMinScale();
			maxScaleView = lsr.getMaxScale();
		} else if (c.equals(cmbMethod)) {
			// disable components in class panel
			// multiple class or not enables or disables the class panel
			setComponentEnabled(classesPanel, getMethod().allowsMultipleClass());
			refreshClasses();

		} else if (c.equals(chkDefinePriorities)) {
			getMethod().setDefinesPriorities(chkDefinePriorities.isSelected());
			refreshPnlPriorities();
		}
	}

	private void setComponentEnabled(Component c, boolean b) {
		if (c instanceof JComponent) {
			JComponent c1 = (JComponent) c;
			for (int i = 0; i < c1.getComponentCount(); i++) {
				setComponentEnabled(c1.getComponent(i), b);
			}
		}
		c.setEnabled(b);
	}

	public String getLabelingStrategyName() {
		return PluginServices.getText(this, "user_defined_labels");
	}

	public Class getLabelingStrategyClass() {
		return GeneralLabelingStrategy.class;
	}

	private class JComboBoxLabelingMethod extends JComboBox {
		private static final long serialVersionUID = 5935267402200698145L;

		public JComboBoxLabelingMethod(MethodItem[] items) {
			super(items);
		}

		@Override
		public void setSelectedItem(Object anObject) {
			if (anObject instanceof MethodItem) {
				MethodItem methodItem = (MethodItem) anObject;
				for (int i = 0; i < getItemCount(); i++) {
					MethodItem aux = (MethodItem) getItemAt(i);
					if (aux.equals(methodItem)) {
						aux.method=methodItem.method;
					}
				}
				super.setSelectedItem(methodItem);
//				((MethodItem) super.getSelectedItem()).method = methodItem.method;
			}
		}
	}
	private class MethodItem {
		private String name;
		private ILabelingMethod method;

		private MethodItem(String name, ILabelingMethod method) {
			this.name = name;
			this.method = method;
		}

		public String toString() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof MethodItem) {
				MethodItem methodItem = (MethodItem) obj;
				return methodItem.method.getClass().equals(this.method.getClass());
			}
			return false;
		}
	}

}*/