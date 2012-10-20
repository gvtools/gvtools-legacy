/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
/* CVS MESSAGES:
 *
 * $Id: 
 * $Log: 
 */
package com.iver.cit.gvsig.geoprocess.impl.referencing.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.vecmath.MismatchedSizeException;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.operation.builder.MappedPosition;
import org.geotools.referencing.operation.builder.MathTransformBuilder;
import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.tools.VectorListenerImpl;
import org.gvsig.fmap.tools.behavior.VectorBehavior;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.referencing.DisactivableMappedPosition;
import org.gvsig.referencing.MappedPositionContainer;
import org.gvsig.referencing.ReferencingUtil;
import org.gvsig.topology.ui.LayerJComboBox;
import org.gvsig.topology.ui.LayerJComboBox.LayerFilter;
import org.gvsig.topology.ui.util.BoxLayoutPanel;
import org.gvsig.topology.ui.util.GUIUtil;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.drivers.VectorErrorMemoryDriver;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayerGenericVectorial;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.fmap.tools.Events.MoveEvent;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;
import com.iver.cit.gvsig.referencing.DisactivableMappedPositionContainerImpl;
import com.iver.cit.gvsig.referencing.MappedPositionContainerLayerBased;
import com.iver.cit.gvsig.referencing.TransformationsRegistry.TransformationRegistryEntry;

/**
 * Component to show digitized vector errors for an spatial adjust in a table.
 * 
 * @author Alvaro Zabala
 * 
 */
public class VectorErrorTable extends BoxLayoutPanel {

	private static final long serialVersionUID = -6506747314549738246L;

	private static boolean registeredKeyStrokes = false;

	/**
	 * Current active view where user could digitize new vectorial errors.
	 */
	MapControl currentView;

	/**
	 * Provides the digitized vectorial errors to create the transformation
	 */
	MappedPositionContainer verrorContainer;

	/**
	 * Current transformation provider
	 */
	TransformationRegistryEntry transformBuilderProvider;

	/**
	 * Current transformation built from control points in vector error
	 * container with the current transformBuilderProvider
	 */
	MathTransform mathTransform;
	/**
	 * Lyr we are spatially adjusting
	 */
	FLyrVect adjustingLyr;

	/**
	 * Table model to show control points and associated errors in a JTable
	 * 
	 * @author Alvaro Zabala
	 * 
	 */
	private final class VErrorTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -3431188780005613099L;
		private static final int COLUMN_COUNT = 7;

		private final DirectPosition buffer = new DirectPosition2D();

		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		public int getRowCount() {
			return verrorContainer.getCount();
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			if ((col == 0) || (col == 5)) {
				return false;
			} else {
				return true;
			}
		}

		public void setValueAt(Object value, int row, int col) {
			if (col == 6) {
				DisactivableMappedPosition mappedPosition = (DisactivableMappedPosition) verrorContainer
						.getMappedPosition(row);
				mappedPosition.setActive(((Boolean) value).booleanValue());
				fireTableCellUpdated(row, col);

				updateVErrorTable();
				updateRmsText();
			} else if ((col == 1) || (col == 2) || (col == 3) || (col == 4)) {
				double newValue = 0.0;
				try {
					newValue = Double.parseDouble(value.toString());
				} catch (Exception e) {
					return;
				}
				MappedPosition mappedPosition = verrorContainer
						.getMappedPosition(row);
				if (col == 1) {
					mappedPosition.getSource().setOrdinate(0, newValue);
				} else if (col == 2) {
					mappedPosition.getSource().setOrdinate(1, newValue);
				} else if (col == 3) {
					mappedPosition.getTarget().setOrdinate(0, newValue);
				} else if (col == 4) {
					mappedPosition.getTarget().setOrdinate(1, newValue);
				}
			}
		}

		public Object getValueAt(int row, int col) {// FIXME USAR NUMBERFORMAT
			// PARA LAS COORDENADAS
			Object solution = null;
			MappedPosition mappedPosition = verrorContainer
					.getMappedPosition(row);

			DirectPosition position = null;
			switch (col) {
			case 0:// Verror id
				solution = row + "";
				break;
			case 1:// x0
				position = mappedPosition.getSource();
				solution = position.getCoordinate()[0] + "";
				break;
			case 2:// y0
				position = mappedPosition.getSource();
				solution = position.getCoordinate()[1] + "";
				break;
			case 3:// x1
				position = mappedPosition.getTarget();
				solution = position.getCoordinate()[0] + "";
				break;
			case 4:// y1
				position = mappedPosition.getTarget();
				solution = position.getCoordinate()[1] + "";
				break;
			case 5:// RMS
				try {
					if (mathTransform == null) {
						// mathTransform at begininng will allways be null,
						// because there arent
						// enought control points to build a transformation
						MathTransformBuilder transformBuilder = transformBuilderProvider
								.createTransformBuilder(verrorContainer
										.getAsList());
						if (transformBuilder != null)
							mathTransform = transformBuilder.getMathTransform();
						else {
							solution = "";
							getRmsLabel()
									.setText(
											PluginServices
													.getText(this,
															"More_control_points_needed_for_the_current_transform"));
							break;
						}
					}
					// TODO gt: error
					solution = "mappedPosition.getError(mathTransform, buffer)";
					// solution =
					// "mappedPosition.getError(mathTransform, buffer)"
					// + "";
				} catch (MismatchedSizeException e) {
					solution = "";
					getRmsLabel()
							.setText(
									PluginServices
											.getText(this,
													"More_control_points_needed_for_the_current_transform"));
				} catch (MismatchedDimensionException e) {
					solution = "";
					getRmsLabel()
							.setText(
									PluginServices
											.getText(
													this,
													"Control_points_coordinates_dimension_are_insufficient_for_the_current_transform"));
				} catch (MismatchedReferenceSystemException e) {
					solution = "";
					getRmsLabel()
							.setText(
									PluginServices
											.getText(this,
													"Control_points_have_inconsistent_reference_system"));
				} catch (FactoryException e) {
					solution = "";
					getRmsLabel().setText(
							PluginServices.getText(this,
									"Error_creating_mathTransform"));
				}
				break;

			case 6:// active or disactive
				if (mappedPosition instanceof DisactivableMappedPosition) {
					DisactivableMappedPosition pos = (DisactivableMappedPosition) mappedPosition;
					solution = new Boolean(pos.isActive());
				} else {
					solution = new Boolean(true);
				}
				break;
			}
			return solution;
		}
	}// TableModel

	private JTable verrorTable;

	private VErrorTableModel verrorTableModel;

	private JLabel totalRmsLabel;

	private JButton deleteLinkBtn;

	private JButton addLinkBtn;

	private JButton loadLinksBtn;

	/**
	 * Listener for mouse events in MapControl
	 */
	private ExtendedVectorListener vl;
	/**
	 * Behavior to digitize vector errors
	 */
	private VectorBehavior vb;

	/**
	 * Constructor.
	 * 
	 * @param currentView
	 * @param adjustingLyr
	 * @param transformBuilderProvider
	 */
	public VectorErrorTable(MapControl currentView, FLyrVect adjustingLyr,
			TransformationRegistryEntry transformBuilderProvider) {
		this.currentView = currentView;
		this.verrorContainer = new DisactivableMappedPositionContainerImpl();
		this.transformBuilderProvider = transformBuilderProvider;
		this.adjustingLyr = adjustingLyr;
		try {
			this.mathTransform = transformBuilderProvider
					.createTransformBuilder(verrorContainer.getAsList())
					.getMathTransform();
		} catch (Exception e) {
			e.printStackTrace();
		}
		initialize();

		ReferencingUtil.getInstance().incrementAdjustSessions();
	}

	public void updateVErrorTable() {
		getVErrorTable().revalidate();
		updateRmsText();
		this.repaint();
	}

	private void initialize() {
		this.addRow(new JComponent[] { new JLabel(PluginServices.getText(this,
				"VERROR_TITLE")) });

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(getVErrorTable());
		scrollPane.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED));
		// scrollPane.setPreferredSize(new Dimension(600, 160));
		// scrollPane.setMinimumSize(new Dimension(600, 120));
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.addRow(new JComponent[] { scrollPane }, 600, 300);

		this.addRow(new JComponent[] { getAddVErrorButton(),
				getDeleteVErrorButton(), getLoadLinksButton() }, 600,
				DEFAULT_HEIGHT);

		this.addRow(new JComponent[] { getRmsLabel() });

		initializeKeyEventsListening();

	}

	private void initializeKeyEventsListening() {
		if (!registeredKeyStrokes) {
			KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

			PluginServices.registerKeyStroke(key, new KeyEventDispatcher() {
				public boolean dispatchKeyEvent(KeyEvent e) {
					if (e.getID() != KeyEvent.KEY_RELEASED)
						return false;

					IWindow v = PluginServices.getMDIManager()
							.getActiveWindow();
					if (!(v instanceof View))
						return false;

					View view = (View) v;
					MapControl mapControl = view.getMapControl();
					if (mapControl.getCurrentTool().equals(
							"digitizeVectorError")) {
						if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
							mapControl.setPrevTool();
							PluginServices.backgroundExecution(new Runnable() {
								public void run() {
									vl.showContainer();
								}
							});
							return true;
						}
					}
					return false;
				}
			});

			// KeyboardFocusManager kfm =
			// KeyboardFocusManager.getCurrentKeyboardFocusManager();
			// kfm.addKeyEventPostProcessor(new KeyEventPostProcessor(){
			// public boolean postProcessKeyEvent(KeyEvent arg0) {
			// return false;
			// }});
			registeredKeyStrokes = true;
		}

	}

	private JTable getVErrorTable() {
		if (verrorTable == null) {
			verrorTable = new JTable();
			verrorTableModel = new VErrorTableModel();
			verrorTable.setModel(verrorTableModel);
			verrorTable.getColumnModel().getColumn(0)
					.setHeaderValue(PluginServices.getText(this, "VError_ID"));

			verrorTable.getColumnModel().getColumn(1)
					.setHeaderValue(PluginServices.getText(this, "Source_x"));

			verrorTable.getColumnModel().getColumn(2)
					.setHeaderValue(PluginServices.getText(this, "Source_y"));

			verrorTable.getColumnModel().getColumn(3)
					.setHeaderValue(PluginServices.getText(this, "Target_x"));

			verrorTable.getColumnModel().getColumn(4)
					.setHeaderValue(PluginServices.getText(this, "Target_y"));

			verrorTable.getColumnModel().getColumn(5)
					.setHeaderValue(PluginServices.getText(this, "RMS"));

			verrorTable.getColumnModel().getColumn(6)
					.setHeaderValue(PluginServices.getText(this, "Active"));

			verrorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return verrorTable;

	}

	private JButton getDeleteVErrorButton() {
		if (deleteLinkBtn == null) {
			deleteLinkBtn = new JButton(PluginServices.getText(this,
					"Delete_VError"));
			deleteLinkBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					deleteLink();
				}
			});
		}
		return deleteLinkBtn;
	}

	private JButton getAddVErrorButton() {
		if (addLinkBtn == null) {
			addLinkBtn = new JButton(PluginServices.getText(this, "Add_VError"));
			addLinkBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					addLink();
				}
			});
		}
		return addLinkBtn;
	}

	private JButton getLoadLinksButton() {
		if (loadLinksBtn == null) {
			loadLinksBtn = new JButton(PluginServices.getText(this,
					"Load_links"));
			loadLinksBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					loadLinks();
				}
			});
		}
		return loadLinksBtn;
	}

	private JLabel getRmsLabel() {
		if (totalRmsLabel == null) {
			totalRmsLabel = new JLabel();
			updateRmsText();
		}
		return totalRmsLabel;
	}

	private void updateRmsText() {
		try {
			getRmsLabel().setText(
					PluginServices.getText(this, "RMS")
							+ " : "
							+ transformBuilderProvider
									.createTransformBuilder(
											verrorContainer.getAsList())
									.getErrorStatistics().rms());
		} catch (FactoryException e) {
			getRmsLabel().setText(
					PluginServices.getText(this, "Error_al_calcular_RMS"));
		} catch (RuntimeException re) {
			getRmsLabel().setText("");
		}

		// MismatchedSizeException, MismatchedDimensionException,
		// MismatchedReferenceSystemException
	}

	class ExtendedVectorListener extends VectorListenerImpl {
		JComponent container;

		public ExtendedVectorListener(MapControl mapCtrl,
				MappedPositionContainer linksList, JComponent container) {
			super(mapCtrl, linksList);
			this.container = container;
		}

		public void hideContainer() {
			if (container != null) {
				GUIUtil.getInstance().getParentOfType(container, JDialog.class)
						.setVisible(false);
			}
		}

		public void showContainer() {
			if (container != null) {
				if (!GUIUtil.getInstance()
						.getParentOfType(container, JDialog.class).isVisible()) {
					PluginServices.backgroundExecution(new Runnable() {
						public void run() {
							GUIUtil.getInstance()
									.getParentOfType(container, JDialog.class)
									.setVisible(true);
						}
					});

				}// if
			}// thisContainer
		}

		public void vector(MoveEvent event) throws BehaviorException {
			super.vector(event);
			if (isZooming)
				return;
			mapCtrl.setPrevTool();

			if (!verrorContainer.existsLinksLyr()) {
				FLyrVect linkLyr = verrorContainer.getLinkLyr(currentView
						.getCrs());
				MapContext mapContext = currentView.getMapContext();

				// each time we create a new link layer, we are creating a new
				// spatial adjusting session
				FLayers solution = new FLayers();
				solution.setMapContext(mapContext);
				solution.setParentLayer(mapContext.getLayers());
				solution.setName(PluginServices.getText(this,
						"SPATIAL_ADJUST_SESSION")
						+ " "
						+ ReferencingUtil.getInstance()
								.getNumberOfSpatialAdjustSessions());
				solution.addLayer(linkLyr);
				mapContext.beginAtomicEvent();
				mapContext.getLayers().addLayer(solution);
				mapContext.endAtomicEvent();
			}
			mapCtrl.commandRepaint();

			updateVErrorTable();
			updateRmsText();

			showContainer();
		}
	}// class ExtendedVectorListener

	private void addLink() {
		final MapControl mapCtrl = currentView;
		if (mapCtrl != null) {
			String sTool = "digitizeVectorError";

			if (vb == null) {
				JComponent container = (JComponent) GUIUtil.getInstance()
						.getParentOfType(this, IWindow.class);

				vl = new ExtendedVectorListener(mapCtrl, verrorContainer,
						container);

				mapCtrl.setVisible(true);

				vb = new VectorBehavior(vl, this.adjustingLyr);

				StatusBarListener sbl = new StatusBarListener(mapCtrl);
				mapCtrl.addMapTool(sTool, new Behavior[] { vb,
						new MouseMovementBehavior(sbl) });
			}// if vl == null

			mapCtrl.setTool(sTool);

			vl.hideContainer();
		}// if mapCtrl != null
	}

	private void deleteLink() {
		int toDelete = getVErrorTable().getSelectedRow();
		this.verrorContainer.delete(toDelete);
		updateVErrorTable();
		updateRmsText();
	}

	/**
	 * Creates de MappedPositionContainer from one of the TOC's layer
	 */
	private void loadLinks() {
		LayerJComboBox lyrComboBox = new LayerJComboBox(currentView
				.getMapContext().getLayers(), new LayerFilter() {
			public boolean filter(FLayer layer) {
				if (layer instanceof FLyrVect) {
					FLyrVect aux = (FLyrVect) layer;
					try {
						if (aux.getShapeType() == FShape.LINE
								|| aux.getShapeType() == FShape.MULTI)
							return true;
					} catch (ReadDriverException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		});

		class LayerComboPanel extends JPanel implements IWindow {

			private boolean okPressed = false;

			public WindowInfo getWindowInfo() {
				WindowInfo solution = new WindowInfo(WindowInfo.MODALDIALOG
						| WindowInfo.PALETTE | WindowInfo.ICONIFIABLE
						| WindowInfo.RESIZABLE | WindowInfo.MAXIMIZABLE);
				solution.setTitle(PluginServices.getText(this,
						"Link_layer_Selection"));
				solution.setWidth(300);
				solution.setHeight(50);
				return solution;
			}

			public Object getWindowProfile() {
				return WindowInfo.DIALOG_PROFILE;
			}

			public void setOkPressed(boolean okPressed) {
				this.okPressed = okPressed;
			}

			public boolean isOkPressed() {
				return this.okPressed;
			}
		}// class

		final LayerComboPanel panel = new LayerComboPanel();
		panel.setLayout(new BorderLayout());

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		JPanel caux = new JPanel();
		caux.add(new JLabel(PluginServices.getText(this, "Select_link_layer")),
				BorderLayout.WEST);
		caux.add(lyrComboBox, BorderLayout.EAST);
		centerPanel.add(caux, BorderLayout.EAST);
		panel.add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		JButton okButton = new JButton(PluginServices.getText(this, "OK"));

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Window parentWindow = GUIUtil.getInstance().getParentWindow(
						panel);
				parentWindow.setVisible(false);
				parentWindow.dispose();
				panel.setOkPressed(true);
			}
		});

		southPanel.setLayout(new BorderLayout());
		JPanel aux = new JPanel();
		aux.add(okButton, BorderLayout.EAST);
		southPanel.add(aux, BorderLayout.EAST);
		panel.add(southPanel, BorderLayout.SOUTH);

		panel.setSize(new Dimension(300, 50));
		PluginServices.getMDIManager().addWindow(panel);

		// Now we check if user presssed ok
		FLyrVect selectedLyr = null;
		if (panel.isOkPressed()) {
			selectedLyr = (FLyrVect) lyrComboBox.getSelectedLayer();
			// clone the layer to ensure in TOC we wont duplicate a layer
			FLyrVect clonedSelectedLyr = null;
			MappedPositionContainer auxContainer = null;
			String name = PluginServices.getText(this, "LINKS_SPATIAL_ADJUST")
					+ " "
					+ ReferencingUtil.getInstance()
							.getNumberOfSpatialAdjustSessions();
			if (selectedLyr instanceof FLayerGenericVectorial) {
				FLayerGenericVectorial genericLyr = (FLayerGenericVectorial) selectedLyr;
				VectorialDriver driver = genericLyr.getDriver();
				CoordinateReferenceSystem crs = genericLyr.getCrs();
				IVectorLegend legend = (IVectorLegend) genericLyr.getLegend();
				if (driver instanceof VectorErrorMemoryDriver) {
					VectorErrorMemoryDriver errorDriver = (VectorErrorMemoryDriver) driver;
					auxContainer = errorDriver.getMappedPositionContainer();
				}
				clonedSelectedLyr = new FLayerGenericVectorial();
				((FLayerGenericVectorial) clonedSelectedLyr).setName(name);
				((FLayerGenericVectorial) clonedSelectedLyr).setDriver(driver);
				((FLayerGenericVectorial) clonedSelectedLyr).setCrs(crs);
				try {
					((FLayerGenericVectorial) clonedSelectedLyr).load();
					((FLayerGenericVectorial) clonedSelectedLyr)
							.setLegend(legend);
				} catch (LegendLayerException e) {
					e.printStackTrace();
					return;
				} catch (LoadLayerException e) {
					e.printStackTrace();
					return;
				}

			} else {
				try {
					clonedSelectedLyr = (FLyrVect) selectedLyr.cloneLayer();
					auxContainer = new MappedPositionContainerLayerBased(
							selectedLyr);
				} catch (BaseException e) {
					e.printStackTrace();
					GUIUtil.getInstance()
							.messageBox(
									PluginServices.getText(this,
											"Error_cargando_links"),
									PluginServices.getText(this,
											"Error_cargando_links"));
					return;
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			boolean overwriteCurrentLinks = true;
			if (this.verrorContainer.getCount() > 0) {
				overwriteCurrentLinks = GUIUtil.getInstance().optionMessage(
						PluginServices.getText(this,
								"overwrite_current_vector_error"),
						PluginServices.getText(this, "spatial_adjust_warning"));

			}
			if (overwriteCurrentLinks) {
				this.verrorContainer = auxContainer;
				updateVErrorTable();
				String folderName = PluginServices.getText(this,
						"SPATIAL_ADJUST_SESSION")
						+ " "
						+ ReferencingUtil.getInstance()
								.getNumberOfSpatialAdjustSessions();
				FLayers rootLyrs = currentView.getMapContext().getLayers();
				FLayers adjustSessionLyrs = (FLayers) rootLyrs
						.getLayer(folderName);
				if (adjustSessionLyrs == null) {
					adjustSessionLyrs = new FLayers();
					adjustSessionLyrs
							.setMapContext(currentView.getMapContext());
					adjustSessionLyrs.setParentLayer(rootLyrs);
					adjustSessionLyrs.setName(folderName);
					rootLyrs.addLayer(adjustSessionLyrs);
				}// if
					// rootLyrs.removeLayer(selectedLyr);
					// adjustSessionLyrs.addLayer(selectedLyr);
				adjustSessionLyrs.addLayer(clonedSelectedLyr);

			}
		}// if ok pressed
	}

	public TransformationRegistryEntry getTransformBuilderProvider() {
		return transformBuilderProvider;
	}

	public void setTransformBuilderProvider(
			TransformationRegistryEntry transformBuilderProvider)
			throws FactoryException {
		this.transformBuilderProvider = transformBuilderProvider;
		this.mathTransform = transformBuilderProvider.createTransformBuilder(
				verrorContainer.getAsList()).getMathTransform();
		updateVErrorTable();
		updateRmsText();
	}

	public MappedPositionContainer getVerrorContainer() {
		return verrorContainer;
	}

	public void setVerrorContainer(MappedPositionContainer verrorContainer) {
		this.verrorContainer = verrorContainer;
	}

	public FLyrVect getAdjustingLyr() {
		return adjustingLyr;
	}

	public void setAdjustingLyr(FLyrVect adjustingLyr) {
		this.adjustingLyr = adjustingLyr;
		if (vb != null)
			vb.setSnappingLyr(adjustingLyr);
	}
}
