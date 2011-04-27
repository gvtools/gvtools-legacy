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
package es.prodevelop.cit.gvsig.arcims.gui.panels;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicArrowButton;

import org.gvsig.remoteClient.arcims.ArcImsImageClient;
import org.gvsig.remoteClient.arcims.ArcImsStatus;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import com.iver.andami.PluginServices;

import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.ImageFormatSelector;
import es.prodevelop.cit.gvsig.arcims.gui.wizards.ArcImsWizard;


public class ImageServicePanel extends FeatureServicePanel {
    private static final long serialVersionUID = 0;
    private ImageFormatSelector formatSelector;

    // protected ImageFormatSelector imageFormatCombo;
    protected BasicArrowButton emergencyArrowButton;
    protected JPanel imageFormatPanel;
    private String imageFormat;

    public ImageServicePanel(ArcImsWizard parent, boolean prop) {
        super(parent, true, prop);
        imageFormat = ServiceInfoTags.vPNG8;
    }

    protected void addImageFormatPanel() {
        imageFormatPanel = new JPanel();
        imageFormatPanel.setBounds(180 - 6, 210 - 8, 287, 54); // hasta y = 264 
        imageFormatPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null, PluginServices.getText(this, "choose_image_format"),
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));

        formatSelector = new ImageFormatSelector();
        formatSelector.addListener(this);

        imageFormatPanel.setLayout(null);

        imgServiceTab_2.add(imageFormatPanel);
        imageFormatPanel.add(formatSelector); // setBounds(5, 15, 260, 20);
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);

        if (e.getSource() == formatSelector) {
            imageFormat = formatSelector.getSelected();

            if (imageFormat.length() == 0) {
                return;
            }

            parentWizard.setImageFormat(captionToFormatInImageFormatCombo(
                    imageFormat));
        }
    }

    protected void updateWizardLayerQuery() {
        super.updateWizardLayerQuery();
        parentWizard.setImageFormat(getArcIMSImageFormat());
    }

    public String getComboImageFormat() {
        return imageFormat;
    }

    public String getArcIMSImageFormat() {
        return captionToFormatInImageFormatCombo(imageFormat);
    }

    public void loadImageFormatCombo(ArcImsImageClient client,
        ArcImsStatus tmpStatus) {
        try {
            formatSelector.setAllEnabled(false);

            if (client.testFromat(tmpStatus, ServiceInfoTags.vPNG8)) {
                formatSelector.setThisEnabled("PNG8");
                formatSelector.setSelected("PNG8");
            }

            if (client.testFromat(tmpStatus, ServiceInfoTags.vPNG24)) {
                formatSelector.setThisEnabled("PNG24");
                formatSelector.setSelected("PNG24");
            }

            if (client.testFromat(tmpStatus, ServiceInfoTags.vJPEG)) {
                formatSelector.setThisEnabled("JPG");
                formatSelector.setSelected("JPG");
            }

            if (client.testFromat(tmpStatus, ServiceInfoTags.vGIF)) {
                formatSelector.setThisEnabled("GIF");
                formatSelector.setSelected("GIF");
            }

            parentWizard.setImageFormat(ServiceInfoTags.vPNG8);
        }
        catch (ArcImsException e) {
            logger.error("While loading image formats combo ", e);
        }
    }

    public void setInImageFormatCombo(String imgFormat) {
        String caption = formatToCaptionInImageFormatCombo(imgFormat);
        formatSelector.setSelected(caption);
    }

    public void emptyFormatsCombo() {
        formatSelector.setAllEnabled(false);
    }

    public ImageFormatSelector getImageFormatCombo() {
        return formatSelector;
    }

    private String captionToFormatInImageFormatCombo(String caption) {
        String resp = "Unknown";

        if (caption.compareToIgnoreCase("JPG") == 0) {
            return ServiceInfoTags.vJPEG;
        }

        if (caption.compareToIgnoreCase("PNG8") == 0) {
            return ServiceInfoTags.vPNG8;
        }

        if (caption.compareToIgnoreCase("PNG24") == 0) {
            return ServiceInfoTags.vPNG24;
        }

        if (caption.compareToIgnoreCase("GIF") == 0) {
            return ServiceInfoTags.vGIF;
        }

        return resp;
    }

    private String formatToCaptionInImageFormatCombo(String format) {
        String resp = "Unknown";

        if (format.compareToIgnoreCase(ServiceInfoTags.vJPEG) == 0) {
            return "JPG";
        }

        if (format.compareToIgnoreCase(ServiceInfoTags.vPNG8) == 0) {
            return "PNG8";
        }

        if (format.compareToIgnoreCase(ServiceInfoTags.vPNG24) == 0) {
            return "PNG24";
        }

        if (format.compareToIgnoreCase(ServiceInfoTags.vGIF) == 0) {
            return "GIF";
        }

        return resp;
    }
}
