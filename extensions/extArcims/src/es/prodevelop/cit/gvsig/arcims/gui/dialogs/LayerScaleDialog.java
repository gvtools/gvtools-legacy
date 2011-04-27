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
package es.prodevelop.cit.gvsig.arcims.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.project.documents.view.gui.View;

import es.prodevelop.cit.gvsig.arcims.fmap.layers.FRasterLyrArcIMS;
import es.prodevelop.cit.gvsig.arcims.fmap.listeners.FRasterLyrArcIMSListener;
import es.prodevelop.cit.gvsig.arcims.gui.panels.LayerScaleDrawPanel;


/**
 * This class shows the ArcIMS layer's scale limits status.
 *
 * @author jldominguez
 */
public class LayerScaleDialog extends JPanel implements ActionListener, IWindow,
    FRasterLyrArcIMSListener {
    private static final long serialVersionUID = 0;
    private JPanel button_p;
    private JButton b;
    private LayerScaleDrawPanel dp;
    private FRasterLyrArcIMS layer;
    private WindowInfo theViewInfo;
    private JScrollPane sp;
    private String vistaName;
    private JLabel southLabel;

    /**
     * Needs the layer as parameter.
     *
     * @param lyr the layer
     */
    public LayerScaleDialog(FRasterLyrArcIMS lyr, View v) {
        super();
        layer = lyr;
        southLabel = new JLabel();

        if (v != null) {
            vistaName = v.getModel().getName();
        }
        else {
            vistaName = "Unknown";
        }

        lyr.addActionlistener(this);
        lyr.addNameOrQueryListener(this);

        Vector infoV = layer.getLayerScaleInfoVector();
        int spHeight = 201 + (15 * infoV.size());

        if (spHeight > 500) {
            spHeight = 500;
        }

        setLayout(new BorderLayout());
        setSize(140 + 680, spHeight + 10);
        setMinimumSize(new Dimension(410, 226));

        b = new JButton(PluginServices.getText(this, "close"));
        b.setBounds(10, 10, 90, 25);
        b.addActionListener(this);

        button_p = new JPanel();
        button_p.setLayout(new BorderLayout());
        button_p.add(southLabel, BorderLayout.WEST); // setPreferredSize(new Dimension(10, 5));
                                                     // button_p.add(b);

        dp = new LayerScaleDrawPanel(infoV, this, southLabel);
        dp.setDpi(lyr.getArcimsStatus().getServiceInfo().getScreen_dpi());
        dp.setCurrentScale(1.0 * layer.getMapContext().getScaleView());

        southLabel.setText(PluginServices.getText(this, "Escala") + "  1 : " +
            getFormattedInteger(Math.round(dp.getCurrentScale())));

        dp.setPreferredSize(new Dimension(300, 200 + (15 * infoV.size())));

        sp = new JScrollPane(dp);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(BorderLayout.CENTER, sp);
        add(BorderLayout.SOUTH, button_p);
    }

    public void resetDrawingPanel() {
        Vector infoV = layer.getLayerScaleInfoVector();
        dp.resetInfo(infoV);
        dp.setPreferredSize(new Dimension(300, 200 + (15 * infoV.size())));
        dp.repaint();
    }

    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand()
                    .compareToIgnoreCase(FRasterLyrArcIMS.SCALE_CHANGED_COMMAND) == 0) {
            dp.setCurrentScale(1.0 * layer.getScale());
            dp.repaint();

            return;
        }

        if (arg0.getSource() == b) {
            this.close();
        }
    }

    public void close() {
        PluginServices.getMDIManager().closeWindow(this);
    }

    public WindowInfo getWindowInfo() {
        if (theViewInfo == null) {
            theViewInfo = new WindowInfo(0 + // palette
                    16 + // no modal
                    0 + // modal
                    4 + // iconifiable
                    2 + // maximizable
                    1 // resizable
                    );
            theViewInfo.setTitle(PluginServices.getText(this, "view") + ": " +
                vistaName + " - " + PluginServices.getText(this, "layer") +
                ": " + layer.getName() + " - " +
                PluginServices.getText(this, "layer_scale_status"));
            theViewInfo.setWidth(getInitialWidth());
            theViewInfo.setHeight(getInitialHeight());
        }

        return theViewInfo;
    }

    public FRasterLyrArcIMS getLayer() {
        return layer;
    }

    public void setLayer(FRasterLyrArcIMS layer) {
        this.layer = layer;
    }

    private int getInitialWidth() {
        return 365;
    }

    private int getInitialHeight() {
        return 290 - 35;
    }

    public void thingsHaveChanged(String query, String name) {
        resetDrawingPanel();
    }

    public static String getFormattedInteger(int n) {
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        df.setGroupingSize(3);

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        return df.format(n);
    }

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

}
