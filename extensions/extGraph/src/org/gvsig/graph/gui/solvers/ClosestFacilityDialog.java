package org.gvsig.graph.gui.solvers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.gvsig.graph.ClosestFacilityController;
import org.gvsig.graph.IClosestFacilityListener;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.solvers.Route;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.GenericFileFilter;

public class ClosestFacilityDialog extends JPanel implements IWindow,
		ActionListener, MouseListener, IClosestFacilityListener, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8072170250049258985L;

	private JPanel panelFacilitiesWest;
	private JPanel panelFacilitiesCenter;
	private JPanel panelFacilitiesEast;
	private JPanel panelFacilities;
	private TitledBorder titledBorderFacilities;
	private JLabel labelFacilitiesName;
	private JComboBox comboFacilities;
	private JCheckBox checkFacilitiesSelection;
	private JLabel labelFacilitiesNumber;
	private JComboBox comboFacilitiesNumber; // Contendra tantos numeros como
												// puntos tenga la capa de
												// proveedores
	private JLabel labelFacilitiesMaxLimit;
	private JTextField textFieldFacilitiesMaxLimit;
	private JLabel labelFacilitiesMaxLimitUnits;

	private JPanel panelEvents;
	private TitledBorder titledBorderEvents;
	private JPanel panelEventsRadioButtons;
	private ButtonGroup buttonGroupEventsRouteToFrom;
	private JRadioButton radioButtonEventsTo;
	private JRadioButton radioButtonEventsFrom;
	private JPanel panelEventsTable;
	private JScrollPane scrollEventsTable;
	private JTable tableEvents;
	private JPanel panelEventsTableButtons;
	private JButton buttonEventsRemove;
	private JButton buttonEventsLoad;
	private JButton buttonEventsSave;

	private JPanel panelSolution;
	private TitledBorder titledBorderSolution;
	private JScrollPane scrollSolutionTable;
	private JTable tableSolution;
	private JPanel panelSolutionButtons;
	private JButton buttonSolutionProperties;
	private JButton buttonSolutionSolve;
	private JButton buttonSolutionInstructions;
	private JButton buttonSolutionZoomRoute;
	private JButton buttonSolutionDrawRoute;

	private WindowInfo wi;

	private Hashtable properties;

	private EventsDataModel events;
	private FacilitiesDataModel facilities;

	private ClosestFacilityController cfc;

	private GvFlag selectedEvent;

	private PluginServices ps = PluginServices.getPluginServices(this);

	public ClosestFacilityDialog(ClosestFacilityController cfc) {

		// FACILITIES
		this.panelFacilities = new JPanel(new GridLayout(1, 3, 5, 5));

		this.panelFacilitiesWest = new JPanel(new GridLayout(3, 1, 5, 7));
		this.panelFacilitiesCenter = new JPanel(new GridLayout(3, 1, 5, 7));
		this.panelFacilitiesEast = new JPanel(new GridLayout(3, 1, 5, 7));

		this.titledBorderFacilities = new TitledBorder(ps.getText("facilities"));
		this.panelFacilities.setBorder(this.titledBorderFacilities);
		this.labelFacilitiesName = new JLabel(ps.getText("facilities") + ":",
				JLabel.RIGHT);
		this.comboFacilities = new JComboBox();
		this.comboFacilities.addActionListener(this);
		this.checkFacilitiesSelection = new JCheckBox(
				ps.getText("only_use_the_selected_points"));
		this.checkFacilitiesSelection.addActionListener(this);

		this.labelFacilitiesNumber = new JLabel(
				ps.getText("number_of_facilities_to_search"), JLabel.RIGHT);
		this.comboFacilitiesNumber = new JComboBox();
		this.comboFacilitiesNumber.addActionListener(this);

		this.labelFacilitiesMaxLimit = new JLabel(ps.getText("max_cost_limit"),
				JLabel.RIGHT);
		this.textFieldFacilitiesMaxLimit = new JTextField();
		this.labelFacilitiesMaxLimitUnits = new JLabel(
				ps.getText("cost_facility_units"), JLabel.LEFT);

		this.panelFacilitiesWest.add(this.labelFacilitiesName);
		this.panelFacilitiesWest.add(this.labelFacilitiesNumber);
		this.panelFacilitiesWest.add(this.labelFacilitiesMaxLimit);

		this.panelFacilitiesCenter.add(this.comboFacilities);
		this.panelFacilitiesCenter.add(this.comboFacilitiesNumber);
		this.panelFacilitiesCenter.add(this.textFieldFacilitiesMaxLimit);

		this.panelFacilitiesEast.add(this.checkFacilitiesSelection);
		this.panelFacilitiesEast.add(new JPanel());
		this.panelFacilitiesEast.add(this.labelFacilitiesMaxLimitUnits);

		this.panelFacilities.add(this.panelFacilitiesWest);
		this.panelFacilities.add(this.panelFacilitiesCenter);
		this.panelFacilities.add(this.panelFacilitiesEast);

		// EVENTS

		this.panelEvents = new JPanel(new BorderLayout(5, 5));
		this.titledBorderEvents = new TitledBorder(ps.getText("events"));
		this.panelEvents.setBorder(this.titledBorderEvents);

		this.panelEventsRadioButtons = new JPanel(new GridLayout(2, 1, 3, 3));
		this.buttonGroupEventsRouteToFrom = new ButtonGroup();
		this.radioButtonEventsTo = new JRadioButton(
				ps.getText("route_to_the_event"));
		this.radioButtonEventsFrom = new JRadioButton(
				ps.getText("route_from_the_event"));
		this.buttonGroupEventsRouteToFrom.add(this.radioButtonEventsTo);
		this.buttonGroupEventsRouteToFrom.add(this.radioButtonEventsFrom);
		this.panelEventsRadioButtons.add(this.radioButtonEventsTo);
		this.panelEventsRadioButtons.add(this.radioButtonEventsFrom);

		this.panelEventsTable = new JPanel(new BorderLayout(5, 5));
		this.scrollEventsTable = new JScrollPane();
		this.scrollEventsTable
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollEventsTable
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.tableEvents = new JTable();
		this.tableEvents.setShowGrid(true);
		this.tableEvents.setRowSelectionAllowed(true);
		this.tableEvents.addMouseListener(this);
		this.scrollEventsTable.getViewport().add(this.tableEvents);

		this.panelEventsTableButtons = new JPanel(new GridLayout(1, 3, 3, 7));
		this.buttonEventsRemove = new JButton(ps.getText("remove_event"));
		this.buttonEventsRemove.addActionListener(this);
		this.buttonEventsLoad = new JButton(ps.getText("load_events"));
		this.buttonEventsLoad.addActionListener(this);
		this.buttonEventsSave = new JButton(ps.getText("save_events"));
		this.buttonEventsSave.addActionListener(this);
		this.panelEventsTableButtons.add(this.buttonEventsRemove);
		this.panelEventsTableButtons.add(this.buttonEventsLoad);
		this.panelEventsTableButtons.add(this.buttonEventsSave);
		this.panelEventsTable.add(this.scrollEventsTable, BorderLayout.CENTER);
		this.panelEventsTable.add(this.panelEventsTableButtons,
				BorderLayout.SOUTH);

		this.panelEvents.add(this.panelEventsRadioButtons, BorderLayout.WEST);
		this.panelEvents.add(this.panelEventsTable, BorderLayout.CENTER);

		// SOLUTION

		this.panelSolution = new JPanel(new BorderLayout(5, 5));
		this.titledBorderSolution = new TitledBorder(
				ps.getText("closest_facility_solution"));
		this.panelSolution.setBorder(this.titledBorderSolution);

		this.scrollSolutionTable = new JScrollPane();
		this.scrollSolutionTable
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollSolutionTable
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.tableSolution = new JTable();
		this.tableSolution.setShowGrid(true);
		this.tableSolution.setRowSelectionAllowed(true);
		this.tableSolution.addMouseListener(this);
		this.scrollSolutionTable.getViewport().add(this.tableSolution);

		// this.panelSolutionButtons=new JPanel(new GridLayout(1, 5, 3, 7));
		this.panelSolutionButtons = new JPanel(new GridLayout(1, 4, 3, 7));// De
																			// momento
																			// 4
																			// porque
																			// se
																			// ha
																			// eliminado
																			// el
																			// boton
																			// de
																			// propiedades
		// this.buttonSolutionProperties=new JButton("Propiedades");
		// this.buttonSolutionProperties.addActionListener(this);
		this.buttonSolutionInstructions = new JButton(
				ps.getText("closest_facility_instructions"));
		this.buttonSolutionInstructions.addActionListener(this);
		this.buttonSolutionDrawRoute = new JButton(ps.getText("draw_route"));
		this.buttonSolutionDrawRoute.addActionListener(this);
		this.buttonSolutionZoomRoute = new JButton(ps.getText("zoom_route"));
		this.buttonSolutionZoomRoute.addActionListener(this);
		this.buttonSolutionSolve = new JButton(
				ps.getText("closest_facility_solve"));
		this.buttonSolutionSolve.addActionListener(this);
		// this.panelSolutionButtons.add(this.buttonSolutionProperties);
		this.panelSolutionButtons.add(this.buttonSolutionInstructions);
		this.panelSolutionButtons.add(this.buttonSolutionDrawRoute);
		this.panelSolutionButtons.add(this.buttonSolutionZoomRoute);
		this.panelSolutionButtons.add(this.buttonSolutionSolve);

		this.panelSolution.add(this.scrollSolutionTable, BorderLayout.CENTER);
		this.panelSolution.add(this.panelSolutionButtons, BorderLayout.SOUTH);

		// GENERAL

		this.setLayout(new GridLayout(3, 1, 10, 10));
		this.add(this.panelFacilities);
		this.add(this.panelEvents);
		this.add(this.panelSolution);

		this.radioButtonEventsFrom.setSelected(true);

		this.properties = new Hashtable();
		this.properties.put("ROUND_COST", Integer.valueOf(5));

		this.cfc = cfc;
		this.facilities = new FacilitiesDataModel(this.cfc, this.tableSolution);
		this.cfc.addClosestFacilityListener(this);
		this.events = new EventsDataModel(this.cfc, this.tableEvents);
		this.selectedEvent = null;
	}

	public void addFacilitiesLayer(FLyrVect facilitiesLayer) {
		this.addFacilitiesLayerToCombo(facilitiesLayer);
	}

	public void removeFacilitiesLayer(FLyrVect facilitiesLayer) {
		this.comboFacilities.removeItem(facilitiesLayer.getName());
		this.comboFacilities.updateUI();
	}

	private boolean addFacilitiesLayerToCombo(FLyrVect facilitiesLayer) {
		this.comboFacilities.addItem(facilitiesLayer.getName());
		this.comboFacilities.updateUI();
		return true;
	}

	private void fillMaxFacilitiesCombo(int maxFacilities) {
		int realMaxFacilities = (maxFacilities < this
				.getToolMaxFacilitiesNumber()) ? maxFacilities : this
				.getToolMaxFacilitiesNumber();
		this.comboFacilitiesNumber.removeAllItems();
		for (int i = 1; i <= realMaxFacilities; i++) {
			this.comboFacilitiesNumber.addItem(Integer.valueOf(i));
		}
	}

	public boolean onlySelectedFacilities() {
		return this.checkFacilitiesSelection.isSelected();
	}

	public void setMaxFacilitiesNumber(int maxFacilitiesNumber) {
		this.cfc.setMaxFacilitiesNumber(maxFacilitiesNumber);
	}

	public int getMaxFacilitiesNumber() {
		return this.cfc.getMaxFacilitiesNumber();
	}

	public void setToolMaxFacilitiesNumber(int toolMaxFacilitiesNumber) {
		this.cfc.setToolMaxFacilitiesNumber(toolMaxFacilitiesNumber);
	}

	public int getToolMaxFacilitiesNumber() {
		return this.cfc.getToolMaxFacilitiesNumber();
	}

	public int getSelectedMaxFacilitiesNumber() {
		return ((Integer) this.comboFacilitiesNumber.getSelectedItem())
				.intValue();
	}

	public double getFacilitiesMaxLimit() throws NumberFormatException {
		return Double.parseDouble(this.textFieldFacilitiesMaxLimit.getText()
				.trim());
	}

	public boolean isToEventSelected() {
		return this.radioButtonEventsTo.isSelected();
	}

	public boolean isFromEventSelected() {
		return this.radioButtonEventsFrom.isSelected();
	}

	public GvFlag getSelectedEvent() {
		if (this.tableEvents.getSelectedRowCount() > 0
				&& this.tableEvents.getRowCount() > 0) {
			Object o = this.events.getValueAt(
					this.tableEvents.getSelectedRow(), 0);
			if (o != null)
				return this.cfc.getEvent(((Integer) o).intValue());
		}
		return null;
	}

	public WindowInfo getWindowInfo() {
		if (this.wi == null) {
			this.wi = new WindowInfo(WindowInfo.PALETTE | WindowInfo.RESIZABLE);
			this.wi.setTitle(ps.getText("closest_facility"));
			this.wi.setWidth(510);
			this.wi.setHeight(400);
		}

		return wi;
	}

	public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source.getClass() == JButton.class) {
			if (source == this.buttonEventsLoad) {
				try {
					String path = "";
					Preferences prefs = Preferences.userRoot().node(
							"gvsig.foldering");
					path = prefs.get("DataFolder", null);
					JFileChooser fileChooser = new JFileChooser(path);
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.addChoosableFileFilter(new GenericFileFilter(
							"shp", ps.getText("shp_files")));
					if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

						this.cfc.loadEventsFromFile(fileChooser
								.getSelectedFile());
					}
				} catch (ReadDriverException except) {
					JOptionPane.showMessageDialog(this, except.getMessage(),
							ps.getText("error_message"),
							JOptionPane.ERROR_MESSAGE);
				} catch (FileNotFoundException except) {
					JOptionPane.showMessageDialog(this, except.getMessage(),
							ps.getText("error_message"),
							JOptionPane.ERROR_MESSAGE);
				} catch (NullPointerException except) {
					JOptionPane.showMessageDialog(this, except.getMessage(),
							ps.getText("error_message"),
							JOptionPane.ERROR_MESSAGE);
				} catch (GraphException except) {
					JOptionPane.showMessageDialog(this, except.getMessage(),
							ps.getText("error_message"),
							JOptionPane.ERROR_MESSAGE);
				} catch (DriverLoadException except) {
					JOptionPane.showMessageDialog(this, except.getMessage(),
							ps.getText("error_message"),
							JOptionPane.ERROR_MESSAGE);
				} catch (NoSuchTableException except) {
					JOptionPane.showMessageDialog(this, except.getMessage(),
							ps.getText("error_message"),
							JOptionPane.ERROR_MESSAGE);
				} catch (Exception except) {
					JOptionPane.showMessageDialog(this, except.getMessage(),
							ps.getText("error_message"),
							JOptionPane.ERROR_MESSAGE);
				}
			} else if (source == this.buttonEventsRemove) {
				if (this.tableEvents.getSelectedRowCount() > 0
						&& this.tableEvents.getRowCount() > 0) {
					GvFlag flag = this.getSelectedEvent();
					if (flag != null) {
						this.cfc.removeEvent(flag);
					}
				}
			} else if (source == this.buttonEventsSave) {
				if (this.tableEvents.getRowCount() > 0) {
					String path = "";
					Preferences prefs = Preferences.userRoot().node(
							"gvsig.foldering");
					path = prefs.get("DataFolder", null);
					JFileChooser fileChooser = new JFileChooser(path);
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.addChoosableFileFilter(new GenericFileFilter(
							"shp", ps.getText("shp_files")));
					if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
						File shpFile = fileChooser.getSelectedFile();
						if (!shpFile.getPath().toLowerCase().endsWith(".shp"))
							shpFile = new File(shpFile.getPath() + ".shp");
						if (shpFile != null) {
							try {
								if (shpFile.exists()) {
									if (JOptionPane
											.showConfirmDialog(
													this,
													ps.getText("overwrite_selected_file_confirmation"),
													ps.getText("confirmation_overwrite"),
													JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION) {
										this.cfc.createSHPEvents(shpFile);
									}
								} else {
									this.cfc.createSHPEvents(shpFile);
								}
							} catch (StartWriterVisitorException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (StopWriterVisitorException e1) {
								e1.printStackTrace();
							} catch (InitializeWriterException e1) {
								e1.printStackTrace();
							} catch (ProcessWriterVisitorException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				} else {
					JOptionPane.showMessageDialog(this,
							ps.getText("no_events_network"),
							ps.getText("warning_message"),
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else if (source == this.buttonSolutionInstructions) {
				if (this.tableSolution.getRowCount() > 0
						&& this.tableSolution.getSelectedRowCount() > 0) {
					try {
						Route route = this.cfc
								.getSolvedRoute(this.tableSolution
										.getSelectedRow());
						if (route != null) {
							this.cfc.showRouteReport(route);
						}
					} catch (GraphException except) {
						JOptionPane.showMessageDialog(this,
								except.getMessage(),
								ps.getText("error_message"),
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(this,
							ps.getText("select_route_show_instructions"),
							ps.getText("closest_facility_instructions"),
							JOptionPane.WARNING_MESSAGE);
				}
			} else if (source == this.buttonSolutionDrawRoute) {
				if (this.tableSolution.getRowCount() > 0
						&& this.tableSolution.getSelectedRowCount() > 0) {
					try {
						Route route = this.cfc
								.getSolvedRoute(this.tableSolution
										.getSelectedRow());
						if (route != null) {
							GvFlag facility = this.cfc
									.getSolvedFacility(this.tableSolution
											.getSelectedRow());

							this.cfc.drawRouteOnGraphics(route);
							if (this.cfc.getSolvedEvent() != null)
								this.cfc.flashFlag(this.cfc.getSolvedEvent(),
										Color.RED, 5);
							if (facility != null)
								this.cfc.flashFlag(facility, Color.BLUE, 5);
						}
					} catch (GraphException except) {
						JOptionPane.showMessageDialog(this,
								except.getMessage(),
								ps.getText("error_message"),
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(this,
							ps.getText("select_route_to_draw"),
							ps.getText("select_route_show_instructions"),
							JOptionPane.WARNING_MESSAGE);
				}
			} else if (source == this.buttonSolutionZoomRoute) {
				if (this.tableSolution.getRowCount() > 0
						&& this.tableSolution.getSelectedRowCount() > 0) {
					try {
						Route route = this.cfc
								.getSolvedRoute(this.tableSolution
										.getSelectedRow());
						if (route != null) {
							GvFlag facility = this.cfc
									.getSolvedFacility(this.tableSolution
											.getSelectedRow());
							GvFlag[] flags = { this.cfc.getSolvedEvent(),
									facility };

							this.cfc.drawRouteOnGraphics(route);
							this.cfc.centerGraphicsOnFlags(flags);
							if (this.cfc.getSolvedEvent() != null)
								this.cfc.flashFlag(this.cfc.getSolvedEvent(),
										Color.RED, 5);
							if (facility != null)
								this.cfc.flashFlag(facility, Color.BLUE, 5);
						}
					} catch (GraphException except) {
						JOptionPane.showMessageDialog(this,
								except.getMessage(),
								ps.getText("warning_message"),
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(this,
							ps.getText("select_route_to_zoom"),
							ps.getText("warning_message"),
							JOptionPane.WARNING_MESSAGE);
				}
			} else if (source == this.buttonSolutionProperties) {
				ClosestFacilityProperties cfp = new ClosestFacilityProperties(
						this.properties);
				PluginServices.getMDIManager().addWindow(cfp);
			} else if (source == this.buttonSolutionSolve) {
				FLyrVect layerFacilities = this.getSelectedFacilitiesLayer();
				double costFacilitiesLimit = -1;
				try {
					costFacilitiesLimit = this.getFacilitiesMaxLimit();
				} catch (NumberFormatException except) {
					costFacilitiesLimit = -1;
				}

				GvFlag sourceFlag = this.getSelectedEvent();

				if (layerFacilities != null) {
					if (this.cfc.getMaxFacilitiesNumber() > 0) {
						if (sourceFlag != null) {
							try {
								solve(sourceFlag, layerFacilities,
										this.isFromEventSelected(),
										this.onlySelectedFacilities(),
										costFacilitiesLimit);
							} catch (ReadDriverException except) {
								JOptionPane.showMessageDialog(this,
										except.getMessage(),
										ps.getText("error_message"),
										JOptionPane.ERROR_MESSAGE);
							} catch (GraphException except) {
								JOptionPane.showMessageDialog(this,
										except.getMessage(),
										ps.getText("error_message"),
										JOptionPane.ERROR_MESSAGE);
							}
						} else {
							JOptionPane.showMessageDialog(this,
									ps.getText("no_event_selected"),
									ps.getText("warning_message"),
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane
								.showMessageDialog(
										this,
										ps.getText("max_facilities_number_higher_zero"),
										ps.getText("warning_message"),
										JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(this,
							ps.getText("no_facilities_layer_selected"),
							ps.getText("error_message"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (source == this.comboFacilities) {
			Thread thread = new Thread(this);
			thread.start();
		} else if (source == this.comboFacilitiesNumber) {
			try {
				if (this.comboFacilitiesNumber.getSelectedIndex() != -1)
					this.setMaxFacilitiesNumber(((Integer) this.comboFacilitiesNumber
							.getSelectedItem()).intValue());
			} catch (ClassCastException except) {

			}
		} else if (source == this.checkFacilitiesSelection) {
			Thread thread = new Thread(this);
			thread.start();
		}
	}

	public void run() {
		Color foregroundButtonSolve = this.buttonSolutionSolve.getForeground();
		String tooltipButtonSolve = this.buttonSolutionSolve.getToolTipText();
		try {
			this.buttonSolutionSolve.setToolTipText(ps
					.getText("loading_facilities"));
			this.buttonSolutionSolve.setEnabled(false);
			this.buttonSolutionSolve.setForeground(Color.gray);
			FLyrVect selectedFacilitiesLayer = this
					.getSelectedFacilitiesLayer();
			this.properties.put("LINES_LAYER_NAME",
					selectedFacilitiesLayer.getName());
			String[] fieldNames = selectedFacilitiesLayer.getRecordset()
					.getFieldNames();
			ArrayList fieldNamesArray = new ArrayList();
			for (int i = 0; i < fieldNames.length; i++) {
				fieldNamesArray.add(fieldNames[i]);
			}
			this.properties.put("LAYER_FIELDS", fieldNamesArray);

			if (selectedFacilitiesLayer != null) {
				this.loadFacilitiesFromLayer(selectedFacilitiesLayer);
			}

			this.comboFacilitiesNumber
					.setSelectedIndex(this.comboFacilitiesNumber.getItemCount() - 1);
		} catch (ReadDriverException except) {
		} catch (GraphException except) {
		} finally {
			this.buttonSolutionSolve.setEnabled(true);
			this.buttonSolutionSolve.setForeground(foregroundButtonSolve);
			this.buttonSolutionSolve.setToolTipText(tooltipButtonSolve);
		}
	}

	private void loadFacilitiesFromLayer(FLyrVect layerFacilities)
			throws ReadDriverException, GraphException {
		this.cfc.removeAllFacilities();
		int rejectedFacilities = this.cfc.loadFacilitiesFromLayer(
				layerFacilities, this.checkFacilitiesSelection.isSelected());
		// this.cfc.flashFacilitiesOnGraphics();
		if (rejectedFacilities > 0)
			if (rejectedFacilities == 1)
				JOptionPane.showMessageDialog(this, rejectedFacilities + " "
						+ ps.getText("rejected_facility_out_of_network"),
						ps.getText("warning_message"),
						JOptionPane.WARNING_MESSAGE);
			else
				JOptionPane.showMessageDialog(this, rejectedFacilities + " "
						+ ps.getText("rejected_facilities_out_of_network"),
						ps.getText("warning_message"),
						JOptionPane.WARNING_MESSAGE);
		int maxFacilities = this.cfc.getFacilitiesCount();
		this.fillMaxFacilitiesCombo(maxFacilities);
		this.setMaxFacilitiesNumber(maxFacilities);
	}

	// Metodos interfaz MouseListener
	public void mouseClicked(MouseEvent e) {
		if (e.getSource().equals(this.tableEvents)) {
			if (this.tableEvents.getSelectedRow() > -1) {
				if (this.tableEvents.getRowCount() > 0) {
					if (this.selectedEvent != null) {
						this.cfc.removeEventFromGraphics(this.selectedEvent);
					}
					this.selectedEvent = this.cfc
							.getEvent(((Integer) this.tableEvents.getValueAt(
									this.tableEvents.getSelectedRow(), 0))
									.intValue());
					if (this.selectedEvent != null) {
						this.cfc.addEventToGraphics(this.selectedEvent);
						// this.cfc.centerGraphicsOnFlag(this.selectedEvent);
						this.cfc.flashFlag(this.selectedEvent, Color.RED, 5);
					}
				}
			} else {
				if (this.selectedEvent != null) {
					this.cfc.removeEventFromGraphics(this.selectedEvent);
				}
			}
		} else if (e.getSource().equals(this.tableSolution)) {
			if (this.tableSolution.getSelectedRow() > -1) {
				if (this.tableEvents.getRowCount() > 0) {
					GvFlag facility = this.cfc
							.getSolvedFacility(this.tableSolution
									.getSelectedRow());
					if (facility != null) {
						this.cfc.clearFlashes();
						this.cfc.flashFlag(facility, Color.BLUE, 5);
					}
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	// Fin metodos interfaz MouseListener

	private FLyrVect getSelectedFacilitiesLayer() {
		String layerName = String.valueOf(this.comboFacilities
				.getSelectedItem());
		FLyrVect lyrVect = null;
		if (layerName != null || layerName.equalsIgnoreCase("null")) {
			IWindow wi = PluginServices.getMDIManager().getActiveWindow();
			if (wi instanceof View) {
				FLayer layer = ((View) wi).getMapControl().getMapContext()
						.getLayers().getLayer(layerName);
				if (layer instanceof FLyrVect) {
					lyrVect = (FLyrVect) layer;
				}
			}
		}

		return lyrVect;
	}

	public void addedSolvedFacility(GvFlag solvedFacility) {
		// this.tableSolution.revalidate();
		this.tableSolution.updateUI();
	}

	public void allEventsRemoved() {
		this.tableEvents.revalidate();
	}

	public void allFacilitiesRemoved() {

	}

	public void eventAdded(GvFlag event) {
		this.tableEvents.revalidate();
	}

	public void eventModified(GvFlag oldEvent, GvFlag modifiedEvent) {
		this.tableEvents.revalidate();
	}

	public void eventRemoved(GvFlag event) {
		this.tableEvents.revalidate();
	}

	public void facilityAdded(GvFlag facility) {

	}

	public void facilityModified(GvFlag oldFacility, GvFlag modifiedFacility) {

	}

	public void facilityRemoved(GvFlag facility) {

	}

	public void removedSolvedFacilities() {
		// this.tableSolution.revalidate();
		this.tableSolution.updateUI();
	}

	private void solve(GvFlag sourceFlag, FLyrVect layerFacilities,
			boolean fromEvent, boolean onlySelectedFacilities,
			double costFacilitiesLimit) throws ReadDriverException,
			GraphException {
		Network network = this.cfc.getNetwork();

		this.cfc.setOnlySelectedFacilities(this.onlySelectedFacilities());
		this.cfc.setLayerFacilities(layerFacilities);
		this.cfc.setSourceEvent(sourceFlag);
		if (fromEvent)
			this.cfc.setFromEvent();
		else
			this.cfc.setToEvent();
		this.cfc.setFacilitiesMaxLimit(costFacilitiesLimit);
		this.cfc.solve();
	}

	class EventsDataModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1064456858672232579L;

		private PluginServices ps = PluginServices.getPluginServices(this);

		private ClosestFacilityController cfc;
		private JTable eventsTable;

		public EventsDataModel(ClosestFacilityController cfc, JTable table) {
			this.cfc = cfc;
			this.eventsTable = table;
			this.eventsTable.setModel(this);

			this.eventsTable
					.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			TableColumnModel cm = this.eventsTable.getColumnModel();

			int tablePreferredWidth = (int) this.eventsTable.getPreferredSize()
					.getWidth();
			int colSize = this.eventsTable.getFontMetrics(
					this.eventsTable.getFont()).stringWidth(
					this.eventsTable.getModel().getColumnName(0)) * 4;
			cm.getColumn(0).setPreferredWidth((int) (colSize));
			cm.getColumn(0).setMinWidth((int) (colSize));
			cm.getColumn(0).setMaxWidth((int) (colSize));

			tablePreferredWidth -= colSize;
			// cm.getColumn(1).setPreferredWidth((int) (tablePreferredWidth *
			// 0.8));
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return this.cfc.getEventCount();
		}

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
				// GvFlag flag =
				// this.cfc.getEvent(((Integer)this.eventsTable.getValueAt(this.eventsTable.getSelectedRow(),
				// 0)).intValue());
				// System.out.println("Events:getValueAt:rowIndex="+rowIndex+", colIndex="+columnIndex+", idFlag="+flag.getIdFlag());
				switch (columnIndex) {
				case 0:
					return Integer.valueOf(this.cfc.getEventByIndex(rowIndex)
							.getIdFlag());
				case 1:
					return this.cfc.getEventByIndex(rowIndex).getDescription();
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
				return ps.getText("table_events_column_description");
			default:
				return String.valueOf((char) (65 + column)); // ASCII
			}
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return false;
			case 1:
				return true;
			default:
				return false;
			}
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				break;

			case 1:
				this.cfc.getEvent(
						Integer.parseInt(String.valueOf(this.getValueAt(
								rowIndex, 0)))).setDescription(
						String.valueOf(aValue));
				break;
			}
		}
	}

	class FacilitiesDataModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6329862517799391098L;

		private PluginServices ps = PluginServices.getPluginServices(this);

		private ClosestFacilityController cfc;

		private JTable solutionTable;

		public FacilitiesDataModel(ClosestFacilityController cfc, JTable table) {
			this.cfc = cfc;
			this.solutionTable = table;
			this.solutionTable.setModel(this);

			this.solutionTable
					.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			TableColumnModel cm = this.solutionTable.getColumnModel();

			int tablePreferredWidth = (int) this.solutionTable
					.getPreferredSize().getWidth();
			int colSize = Double
					.valueOf(
							this.solutionTable.getFontMetrics(
									this.solutionTable.getFont()).stringWidth(
									this.solutionTable.getModel()
											.getColumnName(0)) * 1.5)
					.intValue();
			cm.getColumn(0).setPreferredWidth((int) (colSize));
			cm.getColumn(0).setMinWidth((int) (colSize));
			cm.getColumn(0).setMaxWidth((int) (colSize));
			tablePreferredWidth -= colSize;
			cm.getColumn(1)
					.setPreferredWidth((int) (tablePreferredWidth * 0.9));
			cm.getColumn(2)
					.setPreferredWidth((int) (tablePreferredWidth * 0.1));
		}

		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return this.cfc.getSolvedFacilitiesCount();
		}

		public Class getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Integer.class;
			case 1:
				return String.class;
			case 2:
				return Double.class;
			default:
				return String.class;
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			try {
				switch (columnIndex) {
				case 0:
					// GvFlag flag1 = this.cfc.getSolvedFacility(rowIndex);
					// return Integer.valueOf(flag1.getIdFlag());
					return rowIndex + 1;
				case 1:
					GvFlag flag2 = this.cfc.getSolvedFacility(rowIndex);
					return flag2.getDescription();
				case 2:
					// Route route=this.cfc.getSolvedRoute(rowIndex);
					GvFlag flag = this.cfc.getSolvedFacility(rowIndex);
					return Double.valueOf(flag.getCost());
				default:
					return null;
				}
			} catch (IndexOutOfBoundsException except) {
				return null;
			}
		}

		public String getColumnName(int column) {
			// TODO Auto-generated method stub
			switch (column) {
			case 0:
				return ps.getText("table_solution_column_facilities_position");
			case 1:
				return ps.getText("table_solution_column_description");
			case 2:
				return ps.getText("table_solution_column_cost");
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

}