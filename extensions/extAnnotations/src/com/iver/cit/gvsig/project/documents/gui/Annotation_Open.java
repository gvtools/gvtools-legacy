
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

package com.iver.cit.gvsig.project.documents.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.cresques.cts.ProjectionUtils;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.AddLayer;
import com.iver.cit.gvsig.addlayer.AddLayerDialog;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.Annotation_LayerFactory;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.GTLayerFactory;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.VectorialFileAdapter;
import com.iver.cit.gvsig.gui.JComboBoxUnits;
import com.iver.cit.gvsig.gui.WizardPanel;
import com.iver.cit.gvsig.gui.panels.CRSSelectPanel;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.view.gui.View;


/**
 * Dialog to open an annotation layer.
 *
 * @author Vicente Caballero Navarro
 */
public class Annotation_Open extends WizardPanel {
    private static String lastPath = null;
    private JPanel pGeneral = null;
    private JButton bSelectFile = null;
    private String fName;
    private JTextField tFile;
    private CRSSelectPanel pProyection = null;
    private JPanel pFileSelection;
    private JPanel pControls;
    private JPanel pInPixels;
    private JComboBoxUnits cmbUnits;

    public Annotation_Open() {
        super();
        initialize();
    }

    /**
     * DOCUMENT ME!
     */
    private void initialize() {
        this.setPreferredSize(new java.awt.Dimension(750, 320));
        this.setSize(new java.awt.Dimension(510, 311));
        this.setLocation(new java.awt.Point(0, 0));
        this.add(getPGeneral(), null);
        this.getBSelectFile().addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    acceptButtonActionPerformed(evt);
                }
            });
    }

    /**
     * Evento de pulsado del botón de seleccionar fichero
     *
     * @param e
     */
    private void acceptButtonActionPerformed(ActionEvent e) {
        // Selector de Fichero que se quiere georeferenciar
        if (e.getSource().equals(this.getBSelectFile())) {
            JFileChooser chooser = new JFileChooser(lastPath);
            chooser.setDialogTitle(PluginServices.getText(this,
                    "seleccionar_fichero"));

            chooser.addChoosableFileFilter(new FileFilter() {
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        }

                        if (isPointShapeType(f)) {
                            return f.getAbsolutePath().toLowerCase().endsWith(".shp");
                        }

                        return false;
                    }

                    public String getDescription() {
                        return PluginServices.getText(this, "Ficheros_SHP") + " [" + PluginServices.getText(this, "points") + "]";
                    }
                });
            chooser.addChoosableFileFilter(new FileFilter() {
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        }

                        String[] files = f.getParentFile().list();

                        for (int i = 0; i < files.length; i++) {
                            if (!files[i].equals(f.getName()) &&
                                    files[i].replaceAll("gva", "shp")
                                                .toLowerCase().equals(f.getName()
                                                                           .toLowerCase())) {
                                return (isPointShapeType(f));
                            }
                        }

                        return false;
                    }

                    public String getDescription() {
                    	return PluginServices.getText(this, "Annotation_layers");
                    }
                });

            int returnVal = chooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.fName = chooser.getSelectedFile().toString();

                // FileFilter filter = chooser.getFileFilter();
                this.getTFile().setText(fName);
                lastPath = chooser.getCurrentDirectory().getAbsolutePath();

                if (PluginServices.getMainFrame() == null) {
                    ((JDialog) (getParent().getParent().getParent().getParent())).dispose();
                } else {
                    callStateChanged(true);
                }
            }
        }
    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTFile() {
        if (tFile == null) {
            tFile = new JTextField();
            tFile.setPreferredSize(new java.awt.Dimension(350, 25));
        }

        return tFile;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPGeneral() {
        if (pGeneral == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(5, 0, 0, 0);
            gridBagConstraints1.gridy = 3;
            gridBagConstraints1.gridx = 0;

            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints.gridx = 0;

            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = new java.awt.Insets(0, 0, 2, 0);
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.gridx = 0;

            pGeneral = new JPanel();
            pGeneral.setLayout(new GridBagLayout());
            pGeneral.setPreferredSize(new java.awt.Dimension(750, 350));
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTH;
            pGeneral.add(getPFileSelection(), gridBagConstraints);
            pGeneral.add(getPControls(), gridBagConstraints1);
            pGeneral.add(getPInPixels(), gridBagConstraints2);
        }

        return pGeneral;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private JPanel getPControls() {
        if (pControls == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 1;
            pControls = new JPanel();
            pControls.setLayout(new GridBagLayout());
            pControls.setPreferredSize(new java.awt.Dimension(475, 200));
            pControls.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, "",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.gridy = 0;

            pControls.add(getPProyection(), gridBagConstraints2);
        }

        return pControls;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPFileSelection() {
        if (pFileSelection == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setVgap(10);
            pFileSelection = new JPanel();
            pFileSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this, "cargar"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            pFileSelection.setLayout(flowLayout);
            pFileSelection.setPreferredSize(new java.awt.Dimension(475, 70));
            pFileSelection.add(getTFile(), null);
            pFileSelection.add(getBSelectFile(), null);
        }

        return pFileSelection;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private JButton getBSelectFile() {
        if (bSelectFile == null) {
            bSelectFile = new JButton();
            bSelectFile.setText(PluginServices.getText(this, "cargar"));
        }

        return bSelectFile;
    }

    /**
     * DOCUMENT ME!
     */
    public void initWizard() {
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
		// Creamos la capa y la cargamos
		boolean ok = false;
		View theView = null;

		try {
			theView = (View) PluginServices.getMDIManager().getActiveWindow();
		} catch (ClassCastException exc) {
			return;
		}

		File fich = new File(fName);
		ok = loadFileAnnotationLayer(theView.getMapControl(), fich);
		if (!ok)
			JOptionPane.showMessageDialog((Component) PluginServices
					.getMainFrame(), PluginServices.getText(this,
					"incorrect_annotation_format"));
	}

    /**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
    public FLayer getLayer() {
        return null;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private CRSSelectPanel getPProyection() {
        if (pProyection == null) {
            pProyection = CRSSelectPanel.getPanel(AddLayerDialog.getLastCrs());
            pProyection.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        if (pProyection.isOkPressed()) {
                        	AddLayerDialog.setLastCrs(pProyection.getCurrentCrs());
                        }
                    }
                });
        }

        return pProyection;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPInPixels() {
        if (pInPixels == null) {
            pInPixels = new JPanel(); // CRSSelectPanel.getPanel(FOpenDialog.getLastProjection());

            JLabel lbl = new JLabel(PluginServices.getText(this,
                        "units_of_annotations"));
            pInPixels.add(lbl);
            pInPixels.add(getCmbUnits());
        }

        return pInPixels;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JComboBoxUnits getCmbUnits() {
		if (cmbUnits == null) {
			cmbUnits = new JComboBoxUnits();
			cmbUnits.setSelectedItem(PluginServices.getText(this, "pixels"));
			cmbUnits.setName("CMBUNITS");
		}

		return cmbUnits;
	}

    /*
     * (non-Javadoc)
     *
     * @see com.iver.cit.gvsig.gui.WizardPanel#getTabName()
     */
    public String getTabName() {
        return PluginServices.getText(this, "annotation");
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean isPointShapeType(File file) {
        if (!file.getAbsolutePath().toLowerCase().endsWith(".shp")) {
            return false;
        }

        FLyrVect capa;
		try {
			capa = GTLayerFactory.createVectorLayer("", file, null);
		} catch (IOException e1) {
			return false;
		}

		try {
			return capa.getShapeType() == FShape.POINT;
        } catch (OpenDriverException e) {
			return false;
		} catch (InitializeDriverException e) {
			return false;
		} catch (ReadDriverException e) {
			return false;
		}

    }

    /**
     * Adds to mapcontrol all the file based layers selected by user in
     * fileOpenDialog instance.
     *
     * @param mapControl MapControl where we want to add the selected layers
     * @param file FileOpenDialog where user selected file based layers
     *
     * @return boolean flag to report sucess of the operation
     */
    private boolean loadFileAnnotationLayer(MapControl mapControl, File file) {
    	try {
			Annotation_Layer al = Annotation_LayerFactory.createLayer(file
					.getName(), file, AddLayerDialog.getLastCrs(),
					getCmbUnits().getSelectedUnitIndex(), mapControl
							.getViewPort().getBackColor());

            // Añadir capas al mapControl se trata como una transaccion
            mapControl.getMapContext().beginAtomicEvent();

            if (al != null) {
                al.setVisible(true);
                AddLayer.checkProjection(al, mapControl.getViewPort());
                mapControl.getMapContext().getLayers().addLayer(al);
            } // if

            mapControl.getMapContext().endAtomicEvent();
            return true;
    	}
    	catch (Exception ex) {
    		PluginServices.getLogger().error("", ex);
    		return false;
    	}
    }
}