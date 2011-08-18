package com.prodevelop.cit.gvsig.vectorialdb.wizard;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class CBCellRenderer implements ListCellRenderer {
	
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private JList list = null;
	
	public CBCellRenderer(JList li) {
		list = li;
	}
	
	
        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
            TablesListItemSimple checkbox = (TablesListItemSimple) value;
            checkbox.setBackground(isSelected ? list.getSelectionBackground()
                                              : list.getBackground());
            checkbox.setForeground(isSelected ? list.getSelectionForeground()
                                              : list.getForeground());
            checkbox.setEnabled(list.isEnabled());
            checkbox.setFont(list.getFont());
            checkbox.setFocusPainted(false);
            checkbox.setBorderPainted(true);
            checkbox.setBorder(isSelected
                ? UIManager.getBorder("List.focusCellHighlightBorder")
                : noFocusBorder);

            return checkbox;
        }
}

// [eiel-gestion-conexiones]
