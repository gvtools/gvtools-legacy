/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.arcims.gui.panels.utils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;


/**
 * This class allows nice multiline tool tips.
 *
 * @author Zafir Anjum
 * @author jldominguez
 */
public class MultilineToolTipUI extends BasicToolTipUI {
    static MultilineToolTipUI sharedInstance = new MultilineToolTipUI();

    // Dialog, SansSerif - similar to Arial
    // DialogInput, Monospaced - similar to Courier
    // Serif - similar to Times New Roman 
    static JToolTip tip;
    private static JTextArea textArea;
    Font smallFont = new Font("Dialog", Font.PLAIN, 12);
    protected CellRendererPane rendererPane;

    public MultilineToolTipUI() {
        super();
    }

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        tip = (JToolTip) c;
        rendererPane = new CellRendererPane();
        c.add(rendererPane);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        c.remove(rendererPane);
        rendererPane = null;
    }

    public void paint(Graphics g, JComponent c) {
        Dimension size = c.getSize();
        rendererPane.paintComponent(g, textArea, c, 1, 1, size.width - 1,
            size.height - 1, true);
    }

    /**
     * Overrides a method that is called by the system and,
     * at the same time, creates a nice textarea and displays the tool tip in it.
     */
    public Dimension getPreferredSize(JComponent c) {
        String tipText = ((JToolTip) c).getTipText();

        if (tipText == null) {
            return new Dimension(0, 0);
        }

        textArea = new JTextArea(tipText);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setBackground(tip.getBackground());
        rendererPane.removeAll();
        rendererPane.add(textArea);
        textArea.setWrapStyleWord(true);
        textArea.setFont(smallFont);

        int width = ((JMultilineToolTip) c).getFixedWidth();
        int columns = ((JMultilineToolTip) c).getColumns();

        if (columns > 0) {
            textArea.setColumns(columns);
            textArea.setSize(0, 0);
            textArea.setLineWrap(true);
            textArea.setSize(textArea.getPreferredSize());
        }
        else if (width > 0) {
            textArea.setLineWrap(true);

            Dimension d = textArea.getPreferredSize();
            d.width = width;
            d.height++;
            textArea.setSize(d);
        }
        else
        {
            textArea.setLineWrap(false);
        }

        Dimension dim = textArea.getPreferredSize();

        dim.height += 1;
        dim.width += 1;

        return dim;
    }

    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }
}
