package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;

import com.prodevelop.cit.gvsig.vectorialdb.wizard.CBCellRenderer;
import com.prodevelop.cit.gvsig.vectorialdb.wizard.TablesListItemSimple;

public class AvailableTablesList extends JList {

	private static Logger logger = Logger.getLogger(AvailableTablesList.class
			.getName());
	private DataBaseOpenDialog parent = null;

	public AvailableTablesList(DataBaseOpenDialog pa) {
		super();
		parent = pa;

		setCellRenderer(new CBCellRenderer(this));

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());

				if (index == -1) {
					return;
				}

				TablesListItemSimple actingTable = (TablesListItemSimple) getModel()
						.getElementAt(index);

				if (actingTable == null) {
					return;
				}

				if ((e.getClickCount() == 2) || (e.getX() < 15)) {

					unselectAllButThis(getModel(), actingTable);

					if (!actingTable.isSelected()) {
						actingTable.setSelected(true);
					} else {
						actingTable.setSelected(false);
					}
				}

				if (!parent.setActingTable(actingTable)) {
					actingTable.setSelected(false);
					parent.clearFieldsList();
				}
				parent.checkFinishable();
				parent.repaint();
			}

			private void unselectAllButThis(ListModel lm,
					TablesListItemSimple ata) {

				int len = lm.getSize();
				JCheckBox item = null;
				for (int i = 0; i < len; i++) {
					if (lm.getElementAt(i) instanceof JCheckBox) {
						item = (JCheckBox) lm.getElementAt(i);
						if (item != ata) {
							item.setSelected(false);
						}
					}
				}
			}
		});

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

}

// [eiel-gestion-conexiones]
