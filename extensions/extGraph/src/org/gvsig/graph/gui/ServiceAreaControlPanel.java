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

/* CVS MESSAGES:
 *
 * $Id: RouteControlPanel.java 29800 2009-07-06 22:47:34Z fpenarrubia $
 * $Log$
 * Revision 1.26  2007-09-07 11:29:47  fjp
 * Casi compila. Falta arreglar lo de FArrowSymbol y retocar el graphiclist de FMap.
 *
 * Revision 1.25.2.5  2007/08/06 16:54:34  fjp
 * Versión en desarrollo con velocidades y un esbozo de áreas de influencia
 *
 * Revision 1.25.2.4  2007/06/14 10:02:25  fjp
 * Pliego de redes a publicar SIN el cuadro de diálogo setVelocities (bueno, con , pero invisible)
 *
 * Revision 1.25.2.3  2007/05/24 11:33:36  fjp
 * Para que puedas añadir los puntos que estén cerca de la red. Los que no lo estén, lanzan un error informando de qué punto no está cerca de la red.
 *
 * Revision 1.25.2.2  2007/05/15 07:08:21  fjp
 * Para calcular matrices de distancias
 *
 * Revision 1.25  2006/11/14 18:32:32  fjp
 * *** empty log message ***
 *
 * Revision 1.24  2006/11/14 16:12:01  fjp
 * *** empty log message ***
 *
 * Revision 1.23  2006/11/14 09:23:30  fjp
 * cargar paradas desde cualquier tema de puntos
 *
 * Revision 1.22  2006/11/10 13:57:04  fjp
 * *** empty log message ***
 *
 * Revision 1.21  2006/11/09 21:08:32  azabala
 * *** empty log message ***
 *
 * Revision 1.20  2006/11/09 12:51:12  jaume
 * *** empty log message ***
 *
 * Revision 1.19  2006/11/09 11:00:43  jaume
 * *** empty log message ***
 *
 * Revision 1.18  2006/11/09 10:59:53  jaume
 * *** empty log message ***
 *
 * Revision 1.17  2006/11/09 10:27:50  fjp
 * *** empty log message ***
 *
 * Revision 1.16  2006/11/09 10:24:11  fjp
 * *** empty log message ***
 *
 * Revision 1.15  2006/11/09 09:16:35  fjp
 * Ya va!!
 *
 * Revision 1.14  2006/11/08 20:14:52  azabala
 * *** empty log message ***
 *
 * Revision 1.13  2006/11/08 19:32:22  azabala
 * saveroute and saveflags modifications
 *
 * Revision 1.12  2006/11/08 18:16:28  fjp
 * *** empty log message ***
 *
 * Revision 1.11  2006/11/08 16:48:19  fjp
 * *** empty log message ***
 *
 * Revision 1.10  2006/11/08 16:00:39  fjp
 * Por terminar el enlace flags-cuadro de diálogo
 *
 * Revision 1.9  2006/11/08 13:18:46  fjp
 * Por terminar el enlace flags-cuadro de diálogo
 *
 * Revision 1.8  2006/11/07 19:49:38  azabala
 * *** empty log message ***
 *
 * Revision 1.7  2006/11/06 13:13:53  azabala
 * *** empty log message ***
 *
 * Revision 1.6  2006/11/06 10:29:32  jaume
 * *** empty log message ***
 *
 * Revision 1.5  2006/11/03 19:39:29  azabala
 * *** empty log message ***
 *
 * Revision 1.4  2006/10/27 18:26:22  azabala
 * added implementation of load stages method
 *
 * Revision 1.3  2006/10/27 12:41:09  jaume
 * GUI
 *
 * Revision 1.2  2006/10/26 16:31:21  jaume
 * GUI
 *
 * Revision 1.1  2006/10/25 10:50:41  jaume
 * movement of classes and gui stuff
 *
 * Revision 1.4  2006/10/24 08:04:41  jaume
 * *** empty log message ***
 *
 * Revision 1.3  2006/10/23 16:00:20  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2006/10/23 08:05:39  jaume
 * GUI
 *
 * Revision 1.1  2006/10/20 12:02:50  jaume
 * GUI
 *
 *
 */
package org.gvsig.graph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Random;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.solvers.OneToManySolver;
import org.gvsig.graph.solvers.ServiceAreaExtractor2;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;

import com.hardcode.gdbms.engine.values.DoubleValue;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.rendering.IVectorialUniqueValueLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;

public class ServiceAreaControlPanel extends RouteControlPanel {
	private static Logger logger = Logger
			.getLogger(ServiceAreaControlPanel.class.getName());

	WindowInfo wi;

	private MyTableModel tableModel2 = new MyTableModel();

	private JCheckBox chkCompactArea;

	private JButton btnCalculateServiceArea;

	private class MyTableModel extends AbstractTableModel {
		private String[] colName2;

		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return _getFlags().size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			GvFlag flag = (GvFlag) _getFlags().get(rowIndex);
			switch (columnIndex) {
			case 0:
				return new Boolean(flag.isEnabled());
			case 1:
				return flag.getDescription();
			case 2:
				return flag.getProperties().get("service_area_costs");
			}

			return null;
		}

		public Class getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Boolean.class;
			case 1:
				return String.class;
			case 2:
				return String.class;
			}
			return super.getColumnClass(columnIndex);
		}

		public String getColumnName(int column) {
			if (colName2 == null)
				colName2 = new String[] {
						PluginServices.getText(this, "enable"),
						PluginServices.getText(this, "stage"),
						PluginServices.getText(this, "costs"), };
			return colName2[column];
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;

		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			GvFlag flag = (GvFlag) _getFlags().get(rowIndex);
			switch (columnIndex) {
			case 0:
				Boolean bAux = (Boolean) aValue;
				flag.setEnabled(bAux.booleanValue());
				refresh();
				PluginServices.getMainFrame().enableControls();
				return;
			case 1:
				String strAux = (String) aValue;
				flag.setDescription(strAux);
				return;
			case 2:
				flag.getProperties().setProperty("service_area_costs",
						(String) aValue);
				return;

			}

		}

	}

	/**
	 * This method initializes
	 * 
	 */
	public ServiceAreaControlPanel(Network network) {
		super(network);
		// initialize();
	}

	@Override
	protected void initialize() {
		btnCalculateServiceArea = new JButton(PluginServices.getText(null,
				"Calculate_Service_Areas"));
		btnCalculateServiceArea.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					calculateServiceAreas(network);
				} catch (BaseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		btnCalculateServiceArea.setEnabled(false);
		panelButtonsEast.add(btnCalculateServiceArea);
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(10);
		borderLayout.setVgap(10);
		JPanel cont = new JPanel(borderLayout);
		// cont.setPreferredSize(new Dimension(490, 320));
		this.setPreferredSize(new Dimension(460, 280));
		cont.add(getWestPanel(), BorderLayout.CENTER);
		cont.add(getEastPanel(), BorderLayout.EAST);
		cont.add(getSouthPanel(), BorderLayout.SOUTH);
		this.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10));
		this.add(cont);
		updateFlags();
		refresh();
		getChkReturnToOrigin().setVisible(false);
		getChkTSP().setVisible(false);
		lblCost.setVisible(false);

	}

	@Override
	public void refresh() {
		super.refresh();
		int numActiveFlags = 0;
		for (int i = 0; i < _getFlags().size(); i++) {
			GvFlag f = (GvFlag) _getFlags().get(i);
			if (f.isEnabled())
				numActiveFlags++;
		}
		if (numActiveFlags > 0) {
			btnCalculateServiceArea.setEnabled(true);
		} else
			btnCalculateServiceArea.setEnabled(false);
	}

	/**
	 * This method initializes eastPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getEastPanel() {
		if (eastPanel == null) {
			GridLayout layout = new GridLayout();
			layout.setColumns(1);
			layout.setVgap(5);

			panelButtonsEast.add(getBtnLoadStage());
			panelButtonsEast.add(getBtnSaveStage());
			// panelButtonsEast.add(getBtnSaveRoute());
			panelButtonsEast.add(getBtnCenterOnFlag());

			panelButtonsEast.add(getChkCompactArea());

			// panelButtonsEast.add(getBtnSetVelocities());
			// panelButtonsEast.add(getChkTSP());
			// panelButtonsEast.add(getChkReturnToOrigin());
			panelButtonsEast.add(new JLabel(PluginServices.getText(this,
					"tolerance") + ":"));
			panelButtonsEast.add(getTxtTolerance());

			layout.setRows(panelButtonsEast.getComponentCount());
			panelButtonsEast.setLayout(layout);
			eastPanel = new GridBagLayoutPanel();
			eastPanel.addComponent(panelButtonsEast);
		}
		return eastPanel;
	}

	public JCheckBox getChkCompactArea() {
		if (chkCompactArea == null) {
			chkCompactArea = new JCheckBox();
			chkCompactArea
					.setText(PluginServices.getText(this, "compact_area"));
		}
		return chkCompactArea;

	}

	public void calculateServiceAreas(Network net) throws BaseException {
		if (getTblStages().getCellEditor() != null) {
			DefaultCellEditor cellEditor = (DefaultCellEditor) getTblStages()
					.getCellEditor();
			cellEditor.stopCellEditing();
			// getTblStages().setValueAt(cellEditor.getCellEditorValue(),
			// getTblStages().getEditingRow(),
			// getTblStages().getEditingColumn());
		}
		OneToManySolver solver = new OneToManySolver();
		solver.setNetwork(net);
		GvFlag[] flags = net.getFlags();
		solver.putDestinationsOnNetwork(flags);

		ServiceAreaExtractor2 extractor = new ServiceAreaExtractor2(net);
		MapContext map = mapCtrl.getMapContext();
		solver.addListener(extractor);
		for (int i = 0; i < flags.length; i++) {
			String aux = (String) getTableModel().getValueAt(i, 2);
			if (aux == null) {
				JOptionPane.showMessageDialog(
						(Component) PluginServices.getMainFrame(),
						PluginServices.getText(null,
								"please_introduce_some_costs")
								+ ":"
								+ PluginServices.getText(null, "Line")
								+ " "
								+ (i + 1));
				return;
			}
			double[] costs = null;
			try {
				costs = NetworkUtils.string2doubleArray(aux, ",");
				Arrays.sort(costs);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog((Component) PluginServices
						.getMainFrame(), PluginServices.getText(null,
						"please_introduce_some_costs"));
				return;
			}

			solver.setSourceFlag(flags[i]);
			long t1 = System.currentTimeMillis();
			solver.setExploreAllNetwork(true);
			for (int j = costs.length - 1; j >= 0; j--) {
				solver.setMaxCost(costs[j] * 1.2);
				extractor.setIdFlag(i);
				double[] oneOnly = new double[1];
				oneOnly[0] = costs[j];
				extractor.setCosts(oneOnly);
				extractor.setDoCompactArea(getChkCompactArea().isSelected());
				solver.calculate();
				extractor.writeServiceArea();
				// FLyrVect lyrPoints= extractor.getBorderPoints();
				// lyrPoints.setProjection(map.getProjection());
				// map.getLayers().addLayer(lyrPoints);
				extractor.reset();
			} // j
			long t2 = System.currentTimeMillis();
			System.out.println("Punto " + i + " de " + flags.length + ". "
					+ (t2 - t1) + " msecs.");

		}
		extractor.closeFiles();

		FLyrVect lyrPol = extractor.getPolygonLayer();
		lyrPol.setCrs(map.getCrs());
		IVectorialUniqueValueLegend defaultLegend = LegendFactory
				.createVectorialUniqueValueLegend(FShape.POLYGON);
		defaultLegend.setClassifyingFieldNames(new String[] { "COST" });
		ISymbol myDefaultSymbol = SymbologyFactory
				.createDefaultSymbolByShapeType(FShape.POLYGON);

		defaultLegend.setDefaultSymbol(myDefaultSymbol);

		DoubleValue clave;
		IFillSymbol theSymbol = null;
		Random rnd = new Random(System.currentTimeMillis());
		int iField = lyrPol.getRecordset().getFieldIndexByName("COST");

		for (int j = 0; j < lyrPol.getRecordset().getRowCount(); j++) {
			clave = (DoubleValue) lyrPol.getRecordset()
					.getFieldValue(j, iField);
			if (defaultLegend.getSymbolByValue(clave) == null) {
				theSymbol = (IFillSymbol) SymbologyFactory
						.createDefaultSymbolByShapeType(FShape.POLYGON);
				theSymbol.setDescription(clave.toString());
				Color newColor = new Color(rnd.nextFloat(), rnd.nextFloat(),
						rnd.nextFloat(), 0.7f);
				theSymbol.setFillColor(newColor);

				defaultLegend.addSymbol(clave, theSymbol);
			}

		} // for
		lyrPol.setLegend(defaultLegend);

		FLyrVect lyrLine = extractor.getLineLayer();
		lyrLine.setCrs(map.getCrs());

		solver.removeDestinationsFromNetwork(net.getFlags());
		map.beginAtomicEvent();
		map.getLayers().addLayer(lyrPol);
		map.getLayers().addLayer(lyrLine);

		map.endAtomicEvent();

	}

	public Object getWindowModel() {
		return this.getClass();
	}

	public WindowInfo getWindowInfo() {
		if (wi == null) {
			wi = new WindowInfo(WindowInfo.MODELESSDIALOG
					| WindowInfo.MAXIMIZABLE | WindowInfo.ICONIFIABLE
					| WindowInfo.PALETTE);
			wi.setWidth(600);
			wi.setHeight((int) this.getPreferredSize().getHeight());
			wi.setTitle(PluginServices.getText(this,
					"service_area_control_panel"));
		}
		return wi;
	}

	public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}

	protected JPanel getWestPanel() {
		if (westPanel == null) {
			westPanel = new JPanel(new BorderLayout(5, 5));
			lblCost = new JLabel();
			lblCost.setFont(lblCost.getFont().deriveFont(Font.BOLD));
			GridBagLayoutPanel aux = new GridBagLayoutPanel();
			// aux.addComponent(PluginServices.getText(this, "total_route_cost")
			// + ":", lblCost);
			aux.addComponent(getScrlStages());

			westPanel.add(aux);
		}
		return westPanel;
	}

	protected JScrollPane getScrlStages() {
		if (scrlStages == null) {
			scrlStages = new JScrollPane();
			scrlStages.setViewportView(getTblStages());
			scrlStages.setPreferredSize(new Dimension(400, 200));
		}
		return scrlStages;
	}

	/**
	 * This method initializes tblStages
	 * 
	 * @return javax.swing.JTable
	 */
	protected JTable getTblStages() {
		if (tblStages == null) {
			tblStages = new JTable();
			tblStages
					.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			tblStages.setModel(getTableModel());
			TableColumnModel cm = tblStages.getColumnModel();

			int tablePreferredWidth = 400;
			int colSize = (int) (1.2 * tblStages.getFontMetrics(
					tblStages.getFont()).stringWidth(
					tblStages.getModel().getColumnName(0)));
			cm.getColumn(0).setPreferredWidth((int) (colSize));
			cm.getColumn(0).setMinWidth((int) (colSize));
			cm.getColumn(0).setMaxWidth((int) (colSize));
			tablePreferredWidth -= colSize;
			cm.getColumn(1)
					.setPreferredWidth((int) (tablePreferredWidth * 0.5));
			cm.getColumn(2)
					.setPreferredWidth((int) (tablePreferredWidth * 0.5));

			// Ask to be notified of selection changes.
			ListSelectionModel rowSM = tblStages.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					// Ignore extra messages.
					if (e.getValueIsAdjusting())
						return;

					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					getBtnCenterOnFlag().setEnabled(!lsm.isSelectionEmpty());
					int[] selected = tblStages.getSelectedRows();
					if (selected.length == 0)
						return;
					GvFlag flag = (GvFlag) _getFlags().get(selected[0]);

					Point2D p = flag.getOriginalPoint();
					mapCtrl.repaint(); // borramos el de antes
					NetworkUtils.flashPoint(mapCtrl, p.getX(), p.getY());

				}
			});

			tblStages.getModel().addTableModelListener(
					new TableModelListener() {

						public void tableChanged(TableModelEvent e) {
							System.out.println("Table model changed");
							// getBtnCenterOnFlag().setEnabled(false);
						}

					});

		}
		return tblStages;
	}

	@Override
	protected TableModel getTableModel() {
		if (tableModel2 == null)
			tableModel2 = new MyTableModel();
		return tableModel2;
	}

} // @jve:decl-index=0:visual-constraint="17,9"
