package com.iver.cit.gvsig.project.documents.table.gui.tablemodel;

import java.beans.PropertyChangeEvent;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;

public class ColumnModel extends DefaultTableColumnModel {
	private ProjectTable pt;

	public ColumnModel(ProjectTable pt) {
		this.pt = pt;
	}

	public int getTotalColumnWidth() {
		// int w=0;
		// for (int i=0;i<getColumnCount();i++){
		// w+=pt.getColumn(i).getWidth();
		// }
		// System.out.println("Total width ="+super.getTotalColumnWidth());
		return super.getTotalColumnWidth();
	}

	public void moveColumn(int arg0, int arg1) {
		// super.moveColumn(arg0, arg1);
		if (arg0 == arg1) {
			super.moveColumn(arg0, arg1);
		}
	}

	public void propertyChange(PropertyChangeEvent arg0) {

		try {

			super.propertyChange(arg0);

			if (!(arg0.getNewValue() instanceof Integer)) {
				// sometimes it's not Integer
				return;
			}

			int w = ((Integer) arg0.getNewValue()).intValue();
			if (arg0.getSource() instanceof TableColumn && (w != 75)) {
				TableColumn tc = (TableColumn) arg0.getSource();
				Column column = pt.getColumn(tc.getModelIndex());
				column.setWidth(w);
				System.out.println("Index guardar = " + tc.getModelIndex()
						+ " , " + "Anchura = " + w);
			}
		} catch (Exception e) {
			NotificationManager.showMessageError("propertyChange, Table.java",
					e);
		}
	}

	public TableColumn getColumn(int columnIndex) {
		TableColumn column = super.getColumn(columnIndex);
		int widht = pt.getColumn(columnIndex).getWidth();
		// System.err.println("width= "+widht);
		column.setWidth(widht);
		return column;
	}

	public int getColumnCount() {
		if (super.getColumnCount() < pt.getMapping().length)
			return super.getColumnCount();
		return pt.getMapping().length;
	}
}

// [eiel-error-postgis]
