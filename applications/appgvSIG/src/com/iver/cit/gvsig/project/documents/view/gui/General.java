/*
 * Created on 30-mar-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
package com.iver.cit.gvsig.project.documents.view.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.layerOperations.ClassifiableVectorial;


/**
 * DOCUMENT ME!
 *
 * @author vcn To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class General extends JPanel {
    private javax.swing.JPanel general = null;
    private javax.swing.JLabel jLabel = null;
    private javax.swing.JTextField jTextField = null;
    private javax.swing.JPanel nombre = null;
    private javax.swing.JPanel jPanel2 = null;
    private javax.swing.JLabel jLabel1 = null;
    private javax.swing.JRadioButton jRadioButton = null;
    private javax.swing.JRadioButton jRadioButton1 = null;
    private javax.swing.JPanel jPanel3 = null;
    private javax.swing.JLabel jLabel2 = null;
    private javax.swing.JTextField jTextField1 = null;
    private javax.swing.JLabel jLabel3 = null;
    private javax.swing.JPanel jPanel4 = null;
    private javax.swing.JPanel jPanel5 = null;
    private javax.swing.JLabel jLabel4 = null;
    private javax.swing.JTextField jTextField2 = null;
    private javax.swing.JLabel jLabel5 = null;
    private javax.swing.JPanel central = null;
    private javax.swing.JPanel srango = null;
    private javax.swing.JPanel crango = null;
    private javax.swing.JPanel propiedades = null;
    private javax.swing.JPanel rango = null;
    private javax.swing.JPanel jPanel11 = null;
    private javax.swing.JLabel jLabel6 = null;
    private javax.swing.JLabel jLabel7 = null;
    private javax.swing.JPanel npropiedades = null;
    private javax.swing.JPanel cpropiedades = null;
    private javax.swing.JLabel jLabel8 = null;
    private javax.swing.JLabel jLabel9 = null;
    private javax.swing.JLabel jLabel10 = null;
    private javax.swing.JLabel jLabel11 = null;
    private javax.swing.JLabel jLabel12 = null;
    private javax.swing.JLabel jLabel13 = null;
    private javax.swing.JLabel jLabel14 = null;
    private javax.swing.JLabel jLabel15 = null;
    private javax.swing.JPanel spropiedades = null;
    private javax.swing.JLabel jLabel16 = null;
    private javax.swing.JLabel jLabel17 = null;

    //private FRenderer m_render;
    private ClassifiableVectorial m_layer = null;
    private java.awt.Label label = null;

    /**
     * This is the default constructor
     *
     * @param lyr DOCUMENT ME!
     */
    public General(FLayer lyr) {
        super();

        //m_render=render;
        m_layer = (ClassifiableVectorial) lyr;
        initialize();
    }

    /**
     * DOCUMENT ME!
     */
    public void rellenar() {
        try {
			jLabel9.setText(String.valueOf(((FLayer)m_layer).getFullExtent().getMaxY()));
	        jLabel11.setText(String.valueOf(((FLayer)m_layer).getFullExtent().getMinY()));
	        jLabel13.setText(String.valueOf(((FLayer)m_layer).getFullExtent().getMinX()));
	        jLabel15.setText(String.valueOf(((FLayer)m_layer).getFullExtent().getMaxX()));
		} catch (ReadDriverException e) {
			e.printStackTrace();
		} 
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.add(getpgeneral());

        //jTextField.setText("");
        this.setEnable(false);
        this.setSize(566, 449);
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    private void setEnable(boolean b) {
        jLabel2.setEnabled(b);
        jTextField1.setEditable(b);
        jLabel3.setEnabled(b);
        jLabel4.setEnabled(b);
        jTextField2.setEditable(b);
        jLabel5.setEnabled(b);
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getpgeneral() {
        if (general == null) {
            general = new javax.swing.JPanel();
            general.setLayout(new java.awt.BorderLayout());
            general.add(getpnombre(), java.awt.BorderLayout.NORTH);
            general.add(getpcentral(), java.awt.BorderLayout.CENTER);

            //general.add(getJPanel2(), java.awt.BorderLayout.WEST);
        }

        return general;
    }

    /**
     * This method initializes jLabel
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel() {
        if (jLabel == null) {
            jLabel = new javax.swing.JLabel();
            jLabel.setText(PluginServices.getText(this,"nombre"));
        }

        return jLabel;
    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getJTextField() {
        if (jTextField == null) {
            jTextField = new javax.swing.JTextField();
            jTextField.setText(((FLayer)m_layer).getName());
            jTextField.setPreferredSize(new Dimension(140, 22));
        }

        return jTextField;
    }

    /**
     * This method initializes jPanel1
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getpnombre() {
        if (nombre == null) {
            nombre = new javax.swing.JPanel();
            nombre.add(getJLabel(), null);
            nombre.add(getJTextField(), null);
        }

        return nombre;
    }

    /**
     * This method initializes jPanel2
     *
     * @return javax.swing.JPanel
     */

    /*        private javax.swing.JPanel getJPanel2() {
                    if(jPanel2 == null) {
                            jPanel2 = new javax.swing.JPanel();
                            jPanel2.setLayout(new java.awt.BorderLayout());
                            jPanel2.add(getJPanel3(), java.awt.BorderLayout.NORTH);
                    }
                    return jPanel2;
            }
    */

    /**
     * This method initializes jLabel1
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel1() {
        if (jLabel1 == null) {
            jLabel1 = new javax.swing.JLabel();
            jLabel1.setText(PluginServices.getText(this,"Rango_de_escalas"));
            jLabel1.setForeground(Color.blue);
            jLabel1.setName("jLabel1");
        }

        return jLabel1;
    }

    /**
     * This method initializes jRadioButton
     *
     * @return javax.swing.JRadioButton
     */
    private javax.swing.JRadioButton getJRadioButton() {
        if (jRadioButton == null) {
            jRadioButton = new javax.swing.JRadioButton();
            jRadioButton.setText(PluginServices.getText(this,"Mostrar_siempre"));
            jRadioButton.setName("jRadioButton");
            jRadioButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        System.out.println("mostrar"); // TODO Auto-generated Event stub actionPerformed()
                        setEnable(false);
                    }
                });
        }

        return jRadioButton;
    }

    /**
     * This method initializes jRadioButton1
     *
     * @return javax.swing.JRadioButton
     */
    private javax.swing.JRadioButton getJRadioButton1() {
        if (jRadioButton1 == null) {
            jRadioButton1 = new javax.swing.JRadioButton();
            jRadioButton1.setText(PluginServices.getText(this,"No_mostrar"));
            jRadioButton1.setName("jRadioButton1");
            jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        System.out.println("no mostrar"); // TODO Auto-generated Event stub actionPerformed()
                        setEnable(true);
                    }
                });
        }

        return jRadioButton1;
    }

    /**
     * This method initializes jPanel3
     *
     * @return javax.swing.JPanel
     */

    /*        private javax.swing.JPanel getJPanel3() {
                    if(jPanel3 == null) {
                            jPanel3 = new javax.swing.JPanel();
                            jPanel3.setLayout(new java.awt.BorderLayout());
                            jPanel3.add(getJPanel4(), java.awt.BorderLayout.EAST);
                    }
                    return jPanel3;
            }
    */

    /**
     * This method initializes jLabel2
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel2() {
        if (jLabel2 == null) {
            jLabel2 = new javax.swing.JLabel();
            jLabel2.setText(PluginServices.getText(this,"por_debajo_de"));
        }

        return jLabel2;
    }

    /**
     * This method initializes jTextField1
     *
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getJTextField1() {
        if (jTextField1 == null) {
            jTextField1 = new javax.swing.JTextField();
            jTextField1.setSize(100, 30);
            jTextField1.setText("100,30");
        }

        return jTextField1;
    }

    /**
     * This method initializes jLabel3
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel3() {
        if (jLabel3 == null) {
            jLabel3 = new javax.swing.JLabel();
            jLabel3.setText(PluginServices.getText(this,"(escala_maxima)"));
        }

        return jLabel3;
    }

    /**
     * This method initializes jPanel4
     *
     * @return javax.swing.JPanel
     */

    /*        private javax.swing.JPanel getJPanel4() {
                    if(jPanel4 == null) {
                            jPanel4 = new javax.swing.JPanel();
                            java.awt.GridLayout layGridLayout6 = new java.awt.GridLayout();
                            layGridLayout6.setRows(1);
                            layGridLayout6.setColumns(3);
                            jPanel4.setLayout(layGridLayout6);
                            jPanel4.add(getJPanel5(), null);
                    }
                    return jPanel4;
            }
    */

    /**
     * This method initializes jPanel5
     *
     * @return javax.swing.JPanel
     */

    /*private javax.swing.JPanel getJPanel5() {
            if(jPanel5 == null) {
                    jPanel5 = new javax.swing.JPanel();
                    java.awt.GridLayout layGridLayout7 = new java.awt.GridLayout();
                    layGridLayout7.setRows(2);
                    jPanel5.setLayout(layGridLayout7);
            }
            return jPanel5;
    }*/

    /**
     * This method initializes jLabel4
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel4() {
        if (jLabel4 == null) {
            jLabel4 = new javax.swing.JLabel();
            jLabel4.setText(PluginServices.getText(this,"por_encima_de"));
        }

        return jLabel4;
    }

    /**
     * This method initializes jTextField2
     *
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getJTextField2() {
        if (jTextField2 == null) {
            jTextField2 = new javax.swing.JTextField();
            jTextField2.setSize(100, 30);
            jTextField2.setText("100,30");
        }

        return jTextField2;
    }

    /**
     * This method initializes jLabel5
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel5() {
        if (jLabel5 == null) {
            jLabel5 = new javax.swing.JLabel();
            jLabel5.setText(PluginServices.getText(this,"(escala_minima)"));
        }

        return jLabel5;
    }

    /**
     * This method initializes jPanel6
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getpcentral() {
        if (central == null) {
            central = new javax.swing.JPanel();
            central.setLayout(new java.awt.BorderLayout());
            central.add(getppropiedades(), java.awt.BorderLayout.SOUTH);
            central.add(getprango(), java.awt.BorderLayout.WEST);
            central.setBorder(BorderFactory.createLoweredBevelBorder()); //setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        }

        return central;
    }

    /**
     * This method initializes jPanel7
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getpsrango() {
        if (srango == null) {
            srango = new javax.swing.JPanel();

            java.awt.GridLayout layGridLayout10 = new java.awt.GridLayout();
            layGridLayout10.setRows(2);
            layGridLayout10.setColumns(3);
            srango.setLayout(layGridLayout10);
            srango.add(getJLabel2(), null);
            srango.add(getJTextField1(), null);
            srango.add(getJLabel3(), null);
            srango.add(getJLabel4(), null);
            srango.add(getJTextField2(), null);
            srango.add(getJLabel5(), null);
            srango.setName("jPanel7");
        }

        return srango;
    }

    /**
     * This method initializes jPanel8
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getpcrango() {
        if (crango == null) {
            crango = new javax.swing.JPanel();
            crango.setLayout(new java.awt.BorderLayout());
            crango.add(getJRadioButton(), java.awt.BorderLayout.NORTH);
            crango.add(getJRadioButton1(),
                java.awt.BorderLayout.BEFORE_LINE_BEGINS);
            crango.add(getpsrango(), java.awt.BorderLayout.SOUTH);

            ButtonGroup group = new ButtonGroup();
            group.add(getJRadioButton());
            group.add(getJRadioButton1());
            getJRadioButton().setSelected(true);
        }

        return crango;
    }

    /**
     * This method initializes jPanel9
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getppropiedades() {
        if (propiedades == null) {
            propiedades = new javax.swing.JPanel();
            propiedades.setLayout(new java.awt.BorderLayout());
            propiedades.add(getpnpropiedades(), java.awt.BorderLayout.NORTH);
            propiedades.add(getpcpropiedades(), java.awt.BorderLayout.CENTER);
            propiedades.add(getpspropiedades(), java.awt.BorderLayout.SOUTH);
            propiedades.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this,"propiedades"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));

            /*jPanel9.setLayout(new java.awt.BorderLayout());
            jPanel9.add(getJPanel11(), java.awt.BorderLayout.NORTH);
            jPanel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

            */
        }

        return propiedades;
    }

    /**
     * This method initializes jPanel10
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getprango() {
        if (rango == null) {
            rango = new javax.swing.JPanel();
            rango.setLayout(new java.awt.BorderLayout());

            //rango.add(getJLabel1(), java.awt.BorderLayout.NORTH);
            rango.add(getpcrango(), java.awt.BorderLayout.EAST);
            rango.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                    PluginServices.getText(this,"Rango_de_escalas"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
        }

        return rango;
    }

    /**
     * This method initializes jPanel11
     *
     * @return javax.swing.JPanel
     */

    /*        private javax.swing.JPanel getJPanel11() {
                    if(jPanel11 == null) {
                            jPanel11 = new javax.swing.JPanel();
                            jPanel11.setLayout(new java.awt.BorderLayout());
                            jPanel11.add(getJPanel12(), java.awt.BorderLayout.NORTH);
                            jPanel11.add(getJPanel13(), java.awt.BorderLayout.CENTER);
                            jPanel11.add(getJPanel14(), java.awt.BorderLayout.SOUTH);
                    }
                    return jPanel11;
            }
    */

    /**
     * This method initializes jLabel6
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel6() {
        if (jLabel6 == null) {
            jLabel6 = new javax.swing.JLabel();
            jLabel6.setForeground(Color.blue);
            jLabel6.setText(PluginServices.getText(this,"propiedades"));
        }

        return jLabel6;
    }

    /**
     * This method initializes jLabel7
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel7() {
        if (jLabel7 == null) {
            jLabel7 = new javax.swing.JLabel();
            jLabel7.setText("Extent");
        }

        return jLabel7;
    }

    /**
     * This method initializes jPanel12
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getpnpropiedades() {
        if (npropiedades == null) {
            npropiedades = new javax.swing.JPanel();
            npropiedades.setLayout(new java.awt.BorderLayout());

            //npropiedades.add(getJLabel6(), java.awt.BorderLayout.CENTER);
            npropiedades.add(getJLabel7(), java.awt.BorderLayout.SOUTH);
            npropiedades.add(getLabel(), java.awt.BorderLayout.NORTH);
        }

        return npropiedades;
    }

    /**
     * This method initializes jPanel13
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getpcpropiedades() {
        if (cpropiedades == null) {
            cpropiedades = new javax.swing.JPanel();

            java.awt.GridLayout layGridLayout19 = new java.awt.GridLayout();
            layGridLayout19.setRows(4);
            layGridLayout19.setColumns(2);
            cpropiedades.setLayout(layGridLayout19);
            cpropiedades.add(getJLabel8(), null);
            cpropiedades.add(getJLabel9(), null);
            cpropiedades.add(getJLabel10(), null);
            cpropiedades.add(getJLabel11(), null);
            cpropiedades.add(getJLabel12(), null);
            cpropiedades.add(getJLabel13(), null);
            cpropiedades.add(getJLabel14(), null);
            cpropiedades.add(getJLabel15(), null);
        }

        return cpropiedades;
    }

    /**
     * This method initializes jLabel8
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel8() {
        if (jLabel8 == null) {
            jLabel8 = new javax.swing.JLabel();
            jLabel8.setText(PluginServices.getText(this,"Superior"));
        }

        return jLabel8;
    }

    /**
     * This method initializes jLabel9
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel9() {
        if (jLabel9 == null) {
            jLabel9 = new javax.swing.JLabel();

            //jLabel9.setText(String.valueOf(m_render.m_layer.getLayerExtent().getMaxY()));
        }

        return jLabel9;
    }

    /**
     * This method initializes jLabel10
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel10() {
        if (jLabel10 == null) {
            jLabel10 = new javax.swing.JLabel();
            jLabel10.setText(PluginServices.getText(this,"Inferior"));
        }

        return jLabel10;
    }

    /**
     * This method initializes jLabel11
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel11() {
        if (jLabel11 == null) {
            jLabel11 = new javax.swing.JLabel();

            //jLabel11.setText(String.valueOf(m_render.m_layer.getLayerExtent().getMinY()));
        }

        return jLabel11;
    }

    /**
     * This method initializes jLabel12
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel12() {
        if (jLabel12 == null) {
            jLabel12 = new javax.swing.JLabel();
            jLabel12.setText(PluginServices.getText(this,"Izquierda"));
        }

        return jLabel12;
    }

    /**
     * This method initializes jLabel13
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel13() {
        if (jLabel13 == null) {
            jLabel13 = new javax.swing.JLabel();

            //jLabel13.setText(String.valueOf(m_render.m_layer.getLayerExtent().getMinX()));
        }

        return jLabel13;
    }

    /**
     * This method initializes jLabel14
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel14() {
        if (jLabel14 == null) {
            jLabel14 = new javax.swing.JLabel();
            jLabel14.setText(PluginServices.getText(this,"Derecha"));
        }

        return jLabel14;
    }

    /**
     * This method initializes jLabel15
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel15() {
        if (jLabel15 == null) {
            jLabel15 = new javax.swing.JLabel();

            //jLabel15.setText(String.valueOf(m_render.m_layer.getLayerExtent().getMaxX()));
        }

        return jLabel15;
    }

    /**
     * This method initializes jPanel14
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getpspropiedades() {
        if (spropiedades == null) {
            spropiedades = new javax.swing.JPanel();
            spropiedades.add(getJLabel16(), null);
            spropiedades.add(getJLabel17(), null);
        }

        return spropiedades;
    }

    /**
     * This method initializes jLabel16
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel16() {
        if (jLabel16 == null) {
            jLabel16 = new javax.swing.JLabel();
            jLabel16.setText(PluginServices.getText(this,"Origen_de_Datos"));
        }

        return jLabel16;
    }

    /**
     * This method initializes jLabel17
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel17() {
        if (jLabel17 == null) {
            jLabel17 = new javax.swing.JLabel();
            // TODO: REVISAR. AQUÍ DEBERÍA PONER EL ORIGEN DE DATOS DE ESTA CAPA
            jLabel17.setText(((FLyrVect)m_layer).getSource().getDriver().toString());
        }

        return jLabel17;
    }

    /**
     * This method initializes label
     *
     * @return java.awt.Label
     */
    private java.awt.Label getLabel() {
        if (label == null) {
            label = new java.awt.Label();
            label.setText("Label");
        }

        return label;
    }
}
 //  @jve:visual-info  decl-index=0 visual-constraint="247,3"
