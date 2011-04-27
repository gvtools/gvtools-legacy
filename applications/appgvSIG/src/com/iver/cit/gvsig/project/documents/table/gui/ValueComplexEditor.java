package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import org.gvsig.gui.beans.swing.JButton;

import com.hardcode.gdbms.engine.values.ComplexValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;


/**
 * ComplexValue Editor.
 *
 * @author Vicente Caballero Navarro
 */
public class ValueComplexEditor extends JButton implements TableCellEditor {
    private final Border grayBorder = BorderFactory.createLineBorder(Color.darkGray);
    private Value value;
    private ComplexValuePanel cvp=null;
    public ValueComplexEditor() {
        addMouseListener(new MouseListener() {
               	public void mouseClicked(MouseEvent e) {
					if (e.getClickCount()==2) {
						 cvp=new ComplexValuePanel(PluginServices.getText(this,"titulo"),(ComplexValue)getValue());
						 PluginServices.getMDIManager().addWindow(cvp);
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
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Value getValue() {
        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param isSelected DOCUMENT ME!
     * @param row DOCUMENT ME!
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        setValue((Value) value);
        setText(PluginServices.getText(this, "compleja"));
        setForeground(Color.darkGray);
        setOpaque(true);

        if (isSelected) {
            setBackground(Color.yellow);
        } else {
            setBackground(UIManager.getColor("Button.background"));
        }

        setBorder(grayBorder);

        return this;
    }

    /**
     * DOCUMENT ME!
     */
    public void cancelCellEditing() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean stopCellEditing() {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getCellEditorValue() {
        return this;
    }

    /**
     * DOCUMENT ME!
     *
     * @param anEvent DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param anEvent DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void addCellEditorListener(CellEditorListener l) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void removeCellEditorListener(CellEditorListener l) {
    }
}
