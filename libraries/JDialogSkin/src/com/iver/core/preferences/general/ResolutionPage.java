package com.iver.core.preferences.general;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.prefs.Preferences;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.gvsig.gui.beans.swing.JButton;


/**
 * Page to calculate correctly all the scales of screen.  In it we introduce the values of our screen just as we see it.
 *
 * @author Vicente Caballero Navarro
 */
public class ResolutionPage extends AbstractPreferencePage {
	private static Preferences prefs = Preferences.userRoot().node( "gvsig.configuration.screen" );
	private ImageIcon icon;
	private JPanel pTestMeasure;
	private JTextField txtResolution;
	private JTextField txtMeasure;
	private JComboBox cmbUnits;
	private JButton btnRefresh;
	/**
     * This is the default constructor
     */
    public ResolutionPage() {
        super();
        setParentID(GeneralPage.id);
        icon = new ImageIcon(this.getClass().getClassLoader().getResource("images/resolution.png"));
        addComponent(PluginServices.getText(this, "resolution") + ":",
    			txtResolution = new JTextField("", 15));

        pTestMeasure = new TestMeasurePanel();
        addComponent(pTestMeasure);
        addComponent(new JLabel(PluginServices.getText(this,"the_length_of_the_line_above_is")+":"));
        cmbUnits=new JComboBox();
        cmbUnits.addItem(PluginServices.getText(this,"centimeters"));
        cmbUnits.addItem(PluginServices.getText(this,"inches"));
        cmbUnits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*double d=Double.parseDouble(txtMeasure.getText().replace(',','.'));
				if (cmbUnits.getSelectedIndex()==0) {
					txtResolution.setText(String.valueOf((int)((210*2.45)/d)));
				}else {
					txtResolution.setText(String.valueOf((int)(210/d)));
				}
*/
			}
        });
        txtMeasure=new JTextField();
        addComponent(txtMeasure,cmbUnits);
        btnRefresh=new JButton(PluginServices.getText(this,"button.resolution.calculate"));
        btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				double d=Double.parseDouble(txtMeasure.getText().replace(',','.'));
				if (cmbUnits.getSelectedIndex()==0) {
					txtResolution.setText(String.valueOf((int)((210*2.54)/d)));
				}else {
					txtResolution.setText(String.valueOf((int)(210/d)));
				}


			}

        });
        addComponent(btnRefresh);

        initialize();
    }
    class TestMeasurePanel extends JPanel{

    	public TestMeasurePanel() {
    		setPreferredSize(new Dimension(250,60));
            Border border=BorderFactory.createTitledBorder(PluginServices.getText(this, "test_measure"));
            setBorder(border);
    	}
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D)g).setStroke(new BasicStroke(2));
			g.setColor(Color.black);
			g.drawLine(20,30,230,30);
			g.drawLine(20,20,20,40);
			g.drawLine(230,20,230,40);
		}

    }
    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(394, 248);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws StoreException DOCUMENT ME!
     */
    public void storeValues() throws StoreException {
       int dpi=Integer.parseInt(txtResolution.getText());
       prefs.putInt("dpi",dpi);
    }

    /**
     * DOCUMENT ME!
     */
    public void setChangesApplied() {
    	setChanged(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getID() {
    	return this.getClass().getName();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTitle() {
    	return PluginServices.getText(this, "options.configuration.screen");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JPanel getPanel() {
       return this;
    }

    /**
     * DOCUMENT ME!
     */
    public void initializeValues() {
    	Toolkit kit = Toolkit.getDefaultToolkit();
    	double dpi = kit.getScreenResolution();
    	int resDPI=prefs.getInt("dpi",(int)dpi);

		txtResolution.setText(String.valueOf(resDPI));
		txtMeasure.setText(String.valueOf(format(210*2.54/((double)resDPI))));
		cmbUnits.setSelectedIndex(0);
	}

    /**
     * DOCUMENT ME!
     */
    public void initializeDefaults() {
    	Toolkit kit = Toolkit.getDefaultToolkit();
		int dpi = kit.getScreenResolution();
		txtResolution.setText(String.valueOf(dpi));
		txtMeasure.setText(String.valueOf(format(210*2.45/dpi)));
		cmbUnits.setSelectedIndex(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isValueChanged() {
    	return super.hasChanged();
    }
    /**
     * DOCUMENT ME!
     *
     * @param d DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String format(double d) {
        NumberFormat nf = NumberFormat.getInstance();

        if ((d % (long) d) != 0) {
            nf.setMaximumFractionDigits(2);
        } else {
            nf.setMaximumFractionDigits(0);
        }

        return nf.format(d); //(Double.valueOf(s).doubleValue());
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
