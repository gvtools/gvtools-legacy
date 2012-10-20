package org.gvsig.graph.gui.wizard.servicearea;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import jwizardcomponent.JWizardPanel;

import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.gui.wizard.servicearea.AbstractPointsModel.InvalidCostFieldException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.IExtension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.AddLayer;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class ServiceAreaPage0 extends JWizardPanel implements ActionListener,
		LayerCollectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5424586431273891364L;

	private Logger logger;
	private MapContext mc;
	private ServiceAreaWizard owner;
	private PreviewFLyerVectPointsModel layerModel;
	private PreviewNetworkPointsModel networkModel;
	private Network network;

	private JPanel contentPane;

	private GridBagLayout gridbagSelection;
	private GridBagConstraints cSelection;
	private JPanel panelSourcePointsSelection;
	private TitledBorder borderSourcePointsSelection;
	private JLabel labelSourcePointsSelection;
	private JRadioButton radioSourcePointsSelectionFromLayer;
	private JRadioButton radioSourcePointsSelectionFromNetwork;
	private ButtonGroup groupSourcePointsSelection;

	private JPanel panelSelection;
	private JTabbedPane tabbedSelection;

	private JPanel panelSourcePointsSelectionFromLayer;
	private JPanel panelSourcePointsSelectionFromLayerCombos;
	private JPanel panelSourcePointsSelectionFromLayerPreview;
	private TitledBorder borderSourcePointsSelecionFromLayer;
	private JLabel labelSourcePointsSelectionFromLayerName;
	private JComboBox comboSourcePointsSelectionFromLayerName;
	private JButton buttonSourcePointsSelectionFromLayerAdd;
	private JLabel labelSourcePointsSelectionFromLayerMainCostField;
	private JComboBox comboSourcePointsSelectionFromLayerMainCostField;
	private JLabel labelSourcePointsSelectionFromLayerSecondaryCostField;
	private JComboBox comboSourcePointsSelectionFromLayerSecondaryCostField;
	private JLabel labelSourcePointsSelectionFromLayerPreview;
	private JTable tableSourcePointsSelectionFromLayerPreview;
	private JScrollPane scrollSourcePointsSelectionFromLayerPreview;

	private JPanel panelSourcePointsSelectionFromNetwork;
	private TitledBorder borderSourcePointsSelectionFromNetwork;
	private JLabel labelSourcePointsSelectionFromNetworkList;
	private JScrollPane scrollSourcePointsSelectionFromNetworkList;
	private JTable tableSourcePointsSelectionFromNetworkList;
	private JLabel labelSourcePointsSelectionFromNetworkNote;

	private static final int BORDER_HGAP = 7;
	private static final int BORDER_VGAP = 7;
	private static final int COMPONENT_HGAP = 4;
	private static final int COMPONENT_VGAP = 4;
	private static final int PREFERRED_HEIGHT = 23;
	private static final int PREFERRED_WIDTH = 120;

	public ServiceAreaPage0(ServiceAreaWizard wizard) {
		super(wizard.getWizardComponents());

		this.logger = Logger.getLogger(this.getClass().getName());

		this.owner = wizard;

		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		this.mc = null;
		if (window instanceof View) {
			this.mc = ((View) window).getMapControl().getMapContext();
		}

		this.layerModel = null;
		this.networkModel = null;

		this.initialize();

		if (this.mc != null) {
			int layersCount = this.mc.getLayers().getLayersCount();
			this.comboSourcePointsSelectionFromLayerName.removeAllItems();
			FLayer layer;
			for (int i = 0; i < layersCount; i++) {
				this.mc.getLayers().addLayerCollectionListener(this);
				layer = this.mc.getLayers().getLayer(i);
				if (layer instanceof FLyrVect) {
					try {
						if (((FLyrVect) layer).getShapeType() == FShape.POINT) {
							this.comboSourcePointsSelectionFromLayerName
									.addItem(layer.getName());
						}
					} catch (ReadDriverException except) {
						this.logger.warning(except.getMessage());
					}
				}
			}
		}

		this.comboSourcePointsSelectionFromLayerName.addActionListener(this);
		if (this.comboSourcePointsSelectionFromLayerName.getItemCount() > 0)
			this.comboSourcePointsSelectionFromLayerName.setSelectedIndex(0);
	}

	private void initialize() {
		this.contentPane = new JPanel();

		this.gridbagSelection = new GridBagLayout();
		this.cSelection = new GridBagConstraints();
		this.panelSourcePointsSelection = new JPanel();
		this.borderSourcePointsSelection = new TitledBorder(
				"Fuente de los puntos");
		this.panelSourcePointsSelection
				.setBorder(this.borderSourcePointsSelection);
		this.labelSourcePointsSelection = new JLabel(
				"Obtener puntos a partir de:");
		this.groupSourcePointsSelection = new ButtonGroup();
		this.radioSourcePointsSelectionFromLayer = new JRadioButton(
				"Capa de puntos");
		this.radioSourcePointsSelectionFromNetwork = new JRadioButton(
				"Gestor de paradas");
		this.groupSourcePointsSelection
				.add(this.radioSourcePointsSelectionFromLayer);
		this.groupSourcePointsSelection
				.add(this.radioSourcePointsSelectionFromNetwork);
		this.radioSourcePointsSelectionFromLayer.addActionListener(this);
		this.radioSourcePointsSelectionFromNetwork.addActionListener(this);
		this.radioSourcePointsSelectionFromLayer.setSelected(true);

		this.panelSelection = new JPanel();
		this.panelSelection.setLayout(new BorderLayout(7, 7));
		this.tabbedSelection = new JTabbedPane();
		this.panelSelection.add(this.tabbedSelection, BorderLayout.CENTER);

		this.panelSourcePointsSelectionFromLayer = new JPanel();
		this.panelSourcePointsSelectionFromLayerCombos = new JPanel();
		this.panelSourcePointsSelectionFromLayerPreview = new JPanel();
		this.borderSourcePointsSelecionFromLayer = new TitledBorder(
				"Desde una capa");
		this.panelSourcePointsSelectionFromLayer
				.setBorder(this.borderSourcePointsSelecionFromLayer);
		this.labelSourcePointsSelectionFromLayerName = new JLabel("Capa:",
				JLabel.RIGHT);
		this.comboSourcePointsSelectionFromLayerName = new JComboBox();
		this.comboSourcePointsSelectionFromLayerName
				.setPreferredSize(new Dimension(30, 23));
		this.buttonSourcePointsSelectionFromLayerAdd = new JButton(
				PluginServices.getIconTheme().get("layer-add"));
		this.buttonSourcePointsSelectionFromLayerAdd.addActionListener(this);
		this.labelSourcePointsSelectionFromLayerMainCostField = new JLabel(
				"Coste primario:", JLabel.RIGHT);
		this.comboSourcePointsSelectionFromLayerMainCostField = new JComboBox();
		this.labelSourcePointsSelectionFromLayerSecondaryCostField = new JLabel(
				"Coste secundario:", JLabel.RIGHT);
		this.comboSourcePointsSelectionFromLayerSecondaryCostField = new JComboBox();
		this.comboSourcePointsSelectionFromLayerMainCostField
				.setPreferredSize(new Dimension(30, 23));
		this.labelSourcePointsSelectionFromLayerPreview = new JLabel(
				"Vista previa", JLabel.LEFT);
		this.scrollSourcePointsSelectionFromLayerPreview = new JScrollPane();
		this.tableSourcePointsSelectionFromLayerPreview = new JTable();
		this.scrollSourcePointsSelectionFromLayerPreview.getViewport().add(
				this.tableSourcePointsSelectionFromLayerPreview);
		this.scrollSourcePointsSelectionFromLayerPreview
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollSourcePointsSelectionFromLayerPreview
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		this.panelSourcePointsSelectionFromNetwork = new JPanel();
		this.panelSourcePointsSelectionFromNetwork.setLayout(new BorderLayout(
				BORDER_HGAP, BORDER_VGAP));
		this.borderSourcePointsSelectionFromNetwork = new TitledBorder(
				"Desde el gestor de paradas");
		this.panelSourcePointsSelectionFromNetwork
				.setBorder(this.borderSourcePointsSelectionFromNetwork);
		this.labelSourcePointsSelectionFromNetworkList = new JLabel(
				"Lista de paradas", JLabel.CENTER);
		this.scrollSourcePointsSelectionFromNetworkList = new JScrollPane();
		this.tableSourcePointsSelectionFromNetworkList = new JTable();
		this.scrollSourcePointsSelectionFromNetworkList.getViewport().add(
				this.tableSourcePointsSelectionFromNetworkList);
		this.scrollSourcePointsSelectionFromNetworkList
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollSourcePointsSelectionFromNetworkList
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.labelSourcePointsSelectionFromNetworkNote = new JLabel(
				"<html><p>NOTA: Las paradas se podrán gestionar en el siguiente paso</p></html>");

		this.contentPane.setLayout(new BorderLayout(7, 7));

		this.panelSourcePointsSelection.setLayout(this.gridbagSelection);

		this.cSelection.fill = GridBagConstraints.NONE;
		this.cSelection.weightx = 1.0;
		this.cSelection.gridwidth = GridBagConstraints.REMAINDER;

		this.gridbagSelection.setConstraints(this.labelSourcePointsSelection,
				this.cSelection);
		this.cSelection.gridwidth = GridBagConstraints.RELATIVE;
		this.gridbagSelection.setConstraints(
				this.radioSourcePointsSelectionFromLayer, this.cSelection);
		this.cSelection.gridwidth = GridBagConstraints.REMAINDER;
		this.gridbagSelection.setConstraints(
				this.radioSourcePointsSelectionFromLayer, this.cSelection);

		this.panelSourcePointsSelection.add(this.labelSourcePointsSelection);
		this.panelSourcePointsSelection
				.add(this.radioSourcePointsSelectionFromLayer);
		this.panelSourcePointsSelection
				.add(this.radioSourcePointsSelectionFromNetwork);

		this.contentPane.add(this.panelSourcePointsSelection,
				BorderLayout.NORTH);
		this.contentPane.add(this.panelSelection, BorderLayout.CENTER);

		this.panelSourcePointsSelectionFromLayerCombos.setLayout(null);
		this.panelSourcePointsSelectionFromLayerCombos.setSize(0, 30 * 3);
		this.panelSourcePointsSelectionFromLayerCombos
				.setPreferredSize(new Dimension(0, 30 * 3));

		this.labelSourcePointsSelectionFromLayerName.setSize(
				this.labelSourcePointsSelectionFromLayerName.getFontMetrics(
						this.labelSourcePointsSelectionFromLayerName.getFont())
						.stringWidth(
								this.labelSourcePointsSelectionFromLayerName
										.getText()), PREFERRED_HEIGHT);
		this.labelSourcePointsSelectionFromLayerMainCostField
				.setSize(
						this.labelSourcePointsSelectionFromLayerMainCostField
								.getFontMetrics(
										this.labelSourcePointsSelectionFromLayerMainCostField
												.getFont())
								.stringWidth(
										this.labelSourcePointsSelectionFromLayerMainCostField
												.getText()), PREFERRED_HEIGHT);
		this.labelSourcePointsSelectionFromLayerSecondaryCostField
				.setSize(
						this.labelSourcePointsSelectionFromLayerSecondaryCostField
								.getFontMetrics(
										this.labelSourcePointsSelectionFromLayerSecondaryCostField
												.getFont())
								.stringWidth(
										this.labelSourcePointsSelectionFromLayerSecondaryCostField
												.getText()), PREFERRED_HEIGHT);

		if (this.labelSourcePointsSelectionFromLayerName.getWidth() > this.labelSourcePointsSelectionFromLayerMainCostField
				.getWidth()) {
			if (this.labelSourcePointsSelectionFromLayerName.getWidth() > this.labelSourcePointsSelectionFromLayerSecondaryCostField
					.getWidth()) {
				// Label Name has the biggest width

				this.labelSourcePointsSelectionFromLayerName.setLocation(
						BORDER_HGAP, BORDER_VGAP);
				this.labelSourcePointsSelectionFromLayerMainCostField
						.setLocation(
								(this.labelSourcePointsSelectionFromLayerName
										.getWidth() - this.labelSourcePointsSelectionFromLayerMainCostField
										.getWidth())
										+ BORDER_HGAP,
								this.labelSourcePointsSelectionFromLayerName
										.getY()
										+ this.labelSourcePointsSelectionFromLayerName
												.getHeight() + COMPONENT_VGAP);
				this.labelSourcePointsSelectionFromLayerSecondaryCostField
						.setLocation(
								this.labelSourcePointsSelectionFromLayerName
										.getWidth()
										- this.labelSourcePointsSelectionFromLayerSecondaryCostField
												.getWidth() + BORDER_HGAP,
								this.labelSourcePointsSelectionFromLayerMainCostField
										.getY()
										+ this.labelSourcePointsSelectionFromLayerMainCostField
												.getHeight() + COMPONENT_VGAP);
			} else {
				// Label Secondary Cost has the biggest width

				this.labelSourcePointsSelectionFromLayerName
						.setLocation(
								this.labelSourcePointsSelectionFromLayerSecondaryCostField
										.getWidth()
										- this.labelSourcePointsSelectionFromLayerName
												.getWidth() + BORDER_HGAP,
								BORDER_VGAP);
				this.labelSourcePointsSelectionFromLayerMainCostField
						.setLocation(
								this.labelSourcePointsSelectionFromLayerSecondaryCostField
										.getWidth()
										- this.labelSourcePointsSelectionFromLayerMainCostField
												.getWidth() + BORDER_HGAP,
								this.labelSourcePointsSelectionFromLayerSecondaryCostField
										.getY()
										+ this.labelSourcePointsSelectionFromLayerSecondaryCostField
												.getHeight() + COMPONENT_VGAP);
				this.labelSourcePointsSelectionFromLayerSecondaryCostField
						.setLocation(
								BORDER_HGAP,
								this.labelSourcePointsSelectionFromLayerName
										.getY()
										+ this.labelSourcePointsSelectionFromLayerName
												.getHeight() + COMPONENT_VGAP);
			}
		} else {
			if (this.labelSourcePointsSelectionFromLayerMainCostField
					.getWidth() > this.labelSourcePointsSelectionFromLayerSecondaryCostField
					.getWidth()) {
				// Label Main Cost has the biggest width

				this.labelSourcePointsSelectionFromLayerName.setLocation(
						this.labelSourcePointsSelectionFromLayerMainCostField
								.getWidth()
								- this.labelSourcePointsSelectionFromLayerName
										.getWidth() + BORDER_HGAP, BORDER_VGAP);
				this.labelSourcePointsSelectionFromLayerMainCostField
						.setLocation(
								BORDER_HGAP,
								this.labelSourcePointsSelectionFromLayerName
										.getY()
										+ this.labelSourcePointsSelectionFromLayerName
												.getHeight() + COMPONENT_VGAP);
				this.labelSourcePointsSelectionFromLayerSecondaryCostField
						.setLocation(
								this.labelSourcePointsSelectionFromLayerMainCostField
										.getWidth()
										- this.labelSourcePointsSelectionFromLayerSecondaryCostField
												.getWidth() + BORDER_HGAP,
								this.labelSourcePointsSelectionFromLayerMainCostField
										.getY()
										+ this.labelSourcePointsSelectionFromLayerMainCostField
												.getHeight() + COMPONENT_VGAP);
			} else {
				// Label Secondary Cost has the biggest width

				this.labelSourcePointsSelectionFromLayerName
						.setLocation(
								this.labelSourcePointsSelectionFromLayerSecondaryCostField
										.getWidth()
										- this.labelSourcePointsSelectionFromLayerName
												.getWidth() + BORDER_HGAP,
								BORDER_VGAP);
				this.labelSourcePointsSelectionFromLayerMainCostField
						.setLocation(
								this.labelSourcePointsSelectionFromLayerSecondaryCostField
										.getWidth()
										- this.labelSourcePointsSelectionFromLayerMainCostField
												.getWidth() + BORDER_HGAP,
								this.labelSourcePointsSelectionFromLayerName
										.getY()
										+ this.labelSourcePointsSelectionFromLayerName
												.getHeight() + COMPONENT_VGAP);
				this.labelSourcePointsSelectionFromLayerSecondaryCostField
						.setLocation(
								BORDER_HGAP,
								this.labelSourcePointsSelectionFromLayerMainCostField
										.getY()
										+ this.labelSourcePointsSelectionFromLayerMainCostField
												.getHeight() + COMPONENT_VGAP);
			}
		}

		this.comboSourcePointsSelectionFromLayerName.setBounds(
				this.labelSourcePointsSelectionFromLayerName.getX()
						+ this.labelSourcePointsSelectionFromLayerName
								.getWidth() + COMPONENT_HGAP,
				this.labelSourcePointsSelectionFromLayerName.getY(),
				PREFERRED_WIDTH, PREFERRED_HEIGHT);
		this.comboSourcePointsSelectionFromLayerMainCostField.setBounds(
				this.labelSourcePointsSelectionFromLayerMainCostField.getX()
						+ this.labelSourcePointsSelectionFromLayerMainCostField
								.getWidth() + 4,
				this.labelSourcePointsSelectionFromLayerMainCostField.getY(),
				PREFERRED_WIDTH, PREFERRED_HEIGHT);
		this.comboSourcePointsSelectionFromLayerSecondaryCostField
				.setBounds(
						this.labelSourcePointsSelectionFromLayerSecondaryCostField
								.getX()
								+ this.labelSourcePointsSelectionFromLayerSecondaryCostField
										.getWidth() + COMPONENT_HGAP,
						this.labelSourcePointsSelectionFromLayerSecondaryCostField
								.getY(), PREFERRED_WIDTH, PREFERRED_HEIGHT);
		this.buttonSourcePointsSelectionFromLayerAdd.setBounds(
				this.comboSourcePointsSelectionFromLayerName.getX()
						+ this.comboSourcePointsSelectionFromLayerName
								.getWidth() + 4,
				this.labelSourcePointsSelectionFromLayerName.getY(), 23,
				PREFERRED_HEIGHT);
		this.panelSourcePointsSelectionFromLayerCombos
				.add(this.labelSourcePointsSelectionFromLayerName);
		this.panelSourcePointsSelectionFromLayerCombos
				.add(this.comboSourcePointsSelectionFromLayerName);
		this.panelSourcePointsSelectionFromLayerCombos
				.add(this.labelSourcePointsSelectionFromLayerMainCostField);
		this.panelSourcePointsSelectionFromLayerCombos
				.add(this.comboSourcePointsSelectionFromLayerMainCostField);
		this.panelSourcePointsSelectionFromLayerCombos
				.add(this.labelSourcePointsSelectionFromLayerSecondaryCostField);
		this.panelSourcePointsSelectionFromLayerCombos
				.add(this.comboSourcePointsSelectionFromLayerSecondaryCostField);
		this.panelSourcePointsSelectionFromLayerCombos
				.add(this.buttonSourcePointsSelectionFromLayerAdd);

		this.panelSourcePointsSelectionFromLayerPreview
				.setLayout(new BorderLayout(BORDER_HGAP, BORDER_VGAP));
		this.panelSourcePointsSelectionFromLayerPreview.add(
				this.labelSourcePointsSelectionFromLayerPreview,
				BorderLayout.NORTH);
		this.panelSourcePointsSelectionFromLayerPreview.add(
				this.scrollSourcePointsSelectionFromLayerPreview,
				BorderLayout.CENTER);

		this.panelSourcePointsSelectionFromLayer.setLayout(new BorderLayout(
				BORDER_HGAP, BORDER_VGAP));
		this.panelSourcePointsSelectionFromLayer.add(
				this.panelSourcePointsSelectionFromLayerCombos,
				BorderLayout.NORTH);
		this.panelSourcePointsSelectionFromLayer.add(
				this.panelSourcePointsSelectionFromLayerPreview,
				BorderLayout.CENTER);

		this.panelSourcePointsSelectionFromNetwork.add(
				this.labelSourcePointsSelectionFromNetworkList,
				BorderLayout.NORTH);
		this.panelSourcePointsSelectionFromNetwork.add(
				this.scrollSourcePointsSelectionFromNetworkList,
				BorderLayout.CENTER);
		this.panelSourcePointsSelectionFromNetwork.add(
				this.labelSourcePointsSelectionFromNetworkNote,
				BorderLayout.SOUTH);

		this.setLayout(new BorderLayout(BORDER_HGAP, BORDER_VGAP));
		this.add(this.contentPane, BorderLayout.CENTER);

		this.tabbedSelection.addTab("Desde una capa",
				this.panelSourcePointsSelectionFromLayer);
	}

	public void next() {
		AbstractPointsModel model = this.owner.getController().getModel();
		if (this.radioSourcePointsSelectionFromLayer.isSelected()) {
			try {
				FLayer layer;
				IWindow window = PluginServices.getMDIManager()
						.getActiveWindow();
				if (window instanceof View) {
					layer = ((View) window)
							.getMapControl()
							.getMapContext()
							.getLayers()
							.getLayer(
									String.valueOf(this.comboSourcePointsSelectionFromLayerName
											.getSelectedItem()));
					if (layer != null && layer instanceof FLyrVect) {
						if (model == null
								|| !(model instanceof FLyrVectPointsModel)
								|| (model instanceof FLyrVectPointsModel && ((FLyrVectPointsModel) model)
										.getLayer() != ((FLyrVect) layer))) {
							model = new FLyrVectPointsModel((FLyrVect) layer);
							((FLyrVectPointsModel) model)
									.setCostFields(
											String.valueOf(this.comboSourcePointsSelectionFromLayerMainCostField
													.getSelectedItem()),
											String.valueOf(this.comboSourcePointsSelectionFromLayerSecondaryCostField
													.getSelectedItem()));
							this.owner.getController().setModel(model);
						}
						super.next();
					} else {
						JOptionPane
								.showMessageDialog(
										this,
										"No se ha podido obtener una capa de puntos válida",
										"Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane
							.showMessageDialog(
									this,
									"No se ha podido obtener una capa de puntos válida",
									"Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (InvalidCostFieldException except) {
				JOptionPane.showMessageDialog(this, except.getMessage(),
						"Campo no válido", JOptionPane.ERROR_MESSAGE);
			} catch (ReadDriverException except) {
				JOptionPane
						.showMessageDialog(
								this,
								"Se ha producido un error en la lectura de la información de la capa",
								"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (this.radioSourcePointsSelectionFromNetwork.isSelected()) {
			// OJO: SI SE QUITA EL ACTIONLISTENER AL RADIOBUTTON
			// QUE DICE QUE LOS PUNTOS VIENEN DE UNA RED ESTO VA
			// A PETAR PORQUE LA RED SE OBTIENE EN ESE EVENTO
			model = new NetworkPointsModel(this.network);
			this.owner.getController().setModel(model);
			super.next();
		} else {
			JOptionPane.showMessageDialog(this,
					"No se ha encontrado un modelo de datos válido", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.radioSourcePointsSelectionFromLayer) {
			this.tabbedSelection.removeAll();
			this.tabbedSelection.addTab("Desde una capa",
					this.panelSourcePointsSelectionFromLayer);
		} else if (e.getSource() == this.radioSourcePointsSelectionFromNetwork) {
			this.tabbedSelection.removeAll();

			if (this.networkModel == null) {
				if (this.network == null) {
					IWindow win = PluginServices.getMDIManager()
							.getActiveWindow();
					boolean flagNetworkLayer = false;
					if (win instanceof View) {
						View view = (View) win;
						FLayers fLayers = view.getMapControl().getMapContext()
								.getLayers();
						FLayer[] layers = fLayers.getVisibles();
						for (int i = 0; i < layers.length; i++) {
							if (!flagNetworkLayer
									&& layers[i].isActive()
									&& (this.network = (Network) layers[i]
											.getProperty("network")) != null) {
								flagNetworkLayer = true;
							}
						}
					}
					if (this.network != null) {
						this.networkModel = new PreviewNetworkPointsModel(
								this.tableSourcePointsSelectionFromNetworkList,
								this.network);
					} else {
						JOptionPane.showMessageDialog(this,
								"No hay ninguna capa que contenta una red",
								"Advertencia", JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			this.tabbedSelection.addTab("Desde el gestor de paradas",
					this.panelSourcePointsSelectionFromNetwork);
		} else if (e.getSource() == this.comboSourcePointsSelectionFromLayerName) {
			FLayer layer = this.mc.getLayers().getLayer(
					String.valueOf(this.comboSourcePointsSelectionFromLayerName
							.getSelectedItem()));
			if (layer instanceof FLyrVect) {
				try {
					String[] fieldsName = ((FLyrVect) layer).getRecordset()
							.getFieldNames();
					this.comboSourcePointsSelectionFromLayerMainCostField
							.removeAllItems();
					this.comboSourcePointsSelectionFromLayerSecondaryCostField
							.removeAllItems();
					for (int i = 0; i < fieldsName.length; i++) {
						this.comboSourcePointsSelectionFromLayerMainCostField
								.addItem(fieldsName[i]);
						this.comboSourcePointsSelectionFromLayerSecondaryCostField
								.addItem(fieldsName[i]);
					}
					if (this.comboSourcePointsSelectionFromLayerSecondaryCostField
							.getItemCount() > 1)
						this.comboSourcePointsSelectionFromLayerSecondaryCostField
								.setSelectedIndex(1);

					if (this.layerModel == null) {
						this.layerModel = new PreviewFLyerVectPointsModel(
								this.tableSourcePointsSelectionFromLayerPreview,
								(FLyrVect) layer);
					} else {
						this.layerModel.setFLyrVect((FLyrVect) layer);
					}
				} catch (ReadDriverException except) {
					JOptionPane.showMessageDialog(this,
							"Ha sido imposible obtener los campos de la capa "
									+ layer.getName(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == this.buttonSourcePointsSelectionFromLayerAdd) {
			IExtension ext = PluginServices.getExtension(AddLayer.class);
			if (ext != null) {
				ext.execute("");
			} else {
				JOptionPane.showMessageDialog(this, "Imposible añadir capas",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void layerAdded(LayerCollectionEvent e) {
		this.comboSourcePointsSelectionFromLayerName.addItem(e
				.getAffectedLayer().getName());
		this.comboSourcePointsSelectionFromLayerName
				.setSelectedIndex(this.comboSourcePointsSelectionFromLayerName
						.getItemCount() - 1);
	}

	public void layerAdding(LayerCollectionEvent e) throws CancelationException {

	}

	public void layerMoved(LayerPositionEvent e) {

	}

	public void layerMoving(LayerPositionEvent e) throws CancelationException {

	}

	public void layerRemoved(LayerCollectionEvent e) {
		this.comboSourcePointsSelectionFromLayerName.removeItem(e
				.getAffectedLayer().getName());
	}

	public void layerRemoving(LayerCollectionEvent e)
			throws CancelationException {

	}

	public void visibilityChanged(LayerCollectionEvent e)
			throws CancelationException {
		if (e.getAffectedLayer().isVisible()) {
			this.comboSourcePointsSelectionFromLayerName.addItem(e
					.getAffectedLayer().getName());
		} else {
			this.comboSourcePointsSelectionFromLayerName.removeItem(e
					.getAffectedLayer().getName());
		}
	}

	class PreviewNetworkPointsModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9021109614225441795L;
		private JTable solutionTable;
		private Network network;

		public PreviewNetworkPointsModel(JTable table, Network network) {
			this.solutionTable = table;
			this.solutionTable.setModel(this);
			this.network = network;

			// this.solutionTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			TableColumnModel cm = this.solutionTable.getColumnModel();

			int tablePreferredWidth = (int) this.solutionTable
					.getPreferredSize().getWidth();
			int colSize = this.solutionTable.getFontMetrics(
					this.solutionTable.getFont()).stringWidth(
					this.solutionTable.getModel().getColumnName(0)) * 2;
			cm.getColumn(0).setPreferredWidth((int) (colSize));
			cm.getColumn(0).setMinWidth((int) (colSize));
			cm.getColumn(0).setMaxWidth((int) (colSize));
			tablePreferredWidth -= colSize;
			// cm.getColumn(1).setPreferredWidth((int)(tablePreferredWidth *
			// 0.7));
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return this.network.getFlagsCount();
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Integer.class;
			case 1:
				return String.class;
			default:
				return String.class;
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			try {
				switch (columnIndex) {
				case 0:
					return ((GvFlag) this.network.getOriginaFlags().get(
							rowIndex)).getIdFlag();
				case 1:
					return ((GvFlag) this.network.getOriginaFlags().get(
							rowIndex)).getDescription();
				default:
					return null;
				}
			} catch (IndexOutOfBoundsException except) {
				return null;
			}
		}

		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Id";
			case 1:
				return "Descripción";
			default:
				return String.valueOf((char) (65 + column)); // ASCII
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// Ninguna celda es editable
		}
	}

	class PreviewFLyerVectPointsModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6363515496824845366L;
		private JTable solutionTable;
		private FLyrVect layer;
		private SelectableDataSource recordset;

		private int tempRowIndex;
		private Value[] tempRow;

		private Logger logger;

		@SuppressWarnings("static-access")
		public PreviewFLyerVectPointsModel(JTable table, FLyrVect layer)
				throws ReadDriverException {
			this.logger.getLogger(this.getClass().getName());

			this.solutionTable = table;
			this.setFLyrVect(layer);
			this.solutionTable.setAutoCreateColumnsFromModel(true);
			this.solutionTable.setModel(this);

			this.tempRowIndex = -1;
			this.tempRow = null;

			/*
			 * this.solutionTable.setAutoResizeMode(javax.swing.JTable.
			 * AUTO_RESIZE_SUBSEQUENT_COLUMNS); TableColumnModel cm =
			 * this.solutionTable.getColumnModel();
			 * 
			 * int tablePreferredWidth = (int)
			 * this.solutionTable.getPreferredSize() .getWidth(); int colSize =
			 * Double
			 * .valueOf(this.solutionTable.getFontMetrics(this.solutionTable
			 * .getFont
			 * ()).stringWidth(this.solutionTable.getModel().getColumnName(0)) *
			 * 1.5).intValue(); cm.getColumn(0).setPreferredWidth((int)
			 * (colSize)); cm.getColumn(0).setMinWidth((int) (colSize));
			 * cm.getColumn(0).setMaxWidth((int) (colSize)); tablePreferredWidth
			 * -= colSize; cm.getColumn(1) .setPreferredWidth((int)
			 * (tablePreferredWidth * 0.9));
			 */
		}

		public void setFLyrVect(FLyrVect layer) throws ReadDriverException {
			this.tempRow = null;
			this.tempRowIndex = -1;
			this.layer = layer;
			this.recordset = this.layer.getRecordset();
			this.fireTableStructureChanged();
		}

		public int getColumnCount() {
			try {
				return this.recordset.getFieldCount();
			} catch (ReadDriverException except) {
				this.logger.warning(except.getMessage());
				return 0;
			}
		}

		public int getRowCount() {
			try {
				return (int) this.recordset.getRowCount();
			} catch (ReadDriverException except) {
				this.logger.warning(except.getMessage());
				return 0;
			}
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int columnIndex) {
			return String.class;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			try {
				if (tempRowIndex == rowIndex) {
					if (columnIndex > tempRow.length)
						return null;
					return tempRow[columnIndex].toString();
				} else {
					Value[] values = this.recordset.getRow(rowIndex);
					this.tempRow = values;
					this.tempRowIndex = rowIndex;
					if (columnIndex > values.length)
						return null;
					return values[columnIndex].toString();
				}
			} catch (ReadDriverException except) {
				this.logger.warning(except.getMessage());
			}

			return null;
		}

		public String getColumnName(int column) {
			try {
				return this.recordset.getFieldName(column);
			} catch (ReadDriverException except) {
				this.logger.warning(except.getMessage());
			} catch (Exception except) {
				this.logger.warning(except.getMessage());
			}

			return String.valueOf((char) (65 + column)); // ASCII
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// Ninguna celda es editable
		}
	}
}