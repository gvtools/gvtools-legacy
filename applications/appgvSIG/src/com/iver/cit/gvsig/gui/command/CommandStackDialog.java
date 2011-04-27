package com.iver.cit.gvsig.gui.command;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.gvsig.gui.beans.DefaultBean;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindowListener;
import com.iver.andami.ui.mdiManager.SingletonWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.AddLayer;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.fmap.edition.commands.AddRowCommand;
import com.iver.cit.gvsig.fmap.edition.commands.CommandListener;
import com.iver.cit.gvsig.fmap.edition.commands.CommandRecord;
import com.iver.cit.gvsig.fmap.edition.commands.RemoveRowCommand;

public class CommandStackDialog extends DefaultBean implements SingletonWindow, IWindowListener,CommandListener{

	private JTable jTable = null;
	private JPanel jPanel = null;
	private CommandRecord cr;
	private JSlider jSlider = null;
	//private int itemCount;
	private int lowLimit;
	private int currentValue=-1;
	private JPanel jPanel1 = null;
	protected boolean refreshing;
	private JPanel pCenter = null;
	private JScrollPane jScrollPane = null;
	private static final ImageIcon imodify=PluginServices.getIconTheme()
		.get("edition-modify-command");
	private static final ImageIcon iadd= PluginServices.getIconTheme()
		.get("edition-add-command");

	private static final ImageIcon idel = PluginServices.getIconTheme()
		.get("edition-del-command");
	
	/**
	 * This is the default constructor
	 */
	public CommandStackDialog() {
		super();
		//this.cr=cr;
		//cr.addCommandListener(this);
		initialize();
		//System.err.println("Identificaci�n del objeto en constructor = "+ cr.toString());
	}
	public void setModel(CommandRecord cr1){
//		System.err.println("Identificaci�n del objeto en setModel = "+ cr1.toString());
		if (this.cr!= null && this.cr.equals(cr1))
			return;
		this.cr=cr1;
		this.cr.addCommandListener(this);
		initTable();
		initSlider();
		currentValue=cr.getCommandCount()-cr.getUndoCommands().length;
    	refreshControls();
		//refreshSlider(cr.getCommandCount());
		refreshScroll();

		//
	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(328, 229);
		this.add(getJPanel(), java.awt.BorderLayout.NORTH);
		this.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		//refreshScroll();
	}

	/**
	 * This method initializes jList
	 *
	 * @return javax.swing.JList
	 */
	private JTable getTable() {
		if (jTable == null) {
			jTable = new JTable();
		}
		return jTable;
	}
	private void initTable(){
		MyModel mymodel=new MyModel(cr);
		jTable.setModel(mymodel);
		jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTable.setSelectionBackground(Color.orange);
		jTable.setSelectionForeground(Color.black);
		jTable.setShowGrid(false);
		jTable.getTableHeader().setBackground(Color.white);
		TableColumn tc = jTable.getColumnModel().getColumn(0);
		tc.setCellRenderer(new DefaultTableCellRenderer() {
			   public Component getTableCellRendererComponent(JTable table,
			                                               Object value,
			                                               boolean isSelected,
			                                               boolean hasFocus,
			                                               int row,
			                                               int column)
			      {
			         JLabel label = (JLabel)
			            super.getTableCellRendererComponent
			               (table, value, isSelected, hasFocus, row, column);
			            if (value instanceof AddRowCommand){
			            	label.setIcon(iadd);
			            }else if (value instanceof RemoveRowCommand){
			            	label.setIcon(idel);
			            }else{
			            	label.setIcon(imodify);
			            }
			         if (CommandStackDialog.this.cr.getPos()<row){
			        	 label.setBackground(Color.lightGray);
			         }else {
			        	/* if (cr.getPos()==row){
			        		 label.setBackground(Color.orange);
			        	 }else{*/
			        		 label.setBackground(Color.orange);
			        	 //}
			         }
			            return label;
			      }
			});

		jTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				int newpos=jTable.getSelectedRow();
				try {
					CommandStackDialog.this.cr.setPos(newpos);
					PluginServices.getMainFrame().enableControls();
				} catch (EditionCommandException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
		}
		return jPanel;
	}

	public WindowInfo getWindowInfo() {
		WindowInfo m_viewinfo = new WindowInfo(WindowInfo.ICONIFIABLE |
				WindowInfo.MODELESSDIALOG | WindowInfo.RESIZABLE | WindowInfo.PALETTE);
		m_viewinfo.setTitle(PluginServices.getText(this,
				"pila_de_comandos"));
		return m_viewinfo;
	}

	public Object getWindowModel() {
		return "CommandStack";
	}

	public void windowActivated() {
		this.validateTree();
	}

	public void windowClosed() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.commands.CommandListener#executeCommand(com.iver.cit.gvsig.fmap.edition.commands.CommandEvent)
	 */
	public void commandRepaint() {
		setValue(cr.getCommandCount()-1-cr.getPos(),true);
		refreshScroll();
	}
	private void refreshScroll(){
		Dimension size=new Dimension(jSlider.getPreferredSize().width,((getTable().getRowCount())*getTable().getRowHeight()));
		JScrollBar verticalScrollBar=getJScrollPane().getVerticalScrollBar();//ove(size.width,size.height);
		verticalScrollBar.setValue(cr.getPos()*getTable().getRowHeight());
		jSlider.setPreferredSize(size);
		jSlider.setSize(size);
		validateTree();
	}
	/**
	 * This method initializes jSlider
	 *
	 * @return javax.swing.JSlider
	 */
	private JSlider getJSlider() {
		if (jSlider == null) {
			jSlider = new JSlider();
		}
		return jSlider;
	}
	private void initSlider(){
		jSlider.setOrientation(SwingConstants.VERTICAL);
		jSlider.setPreferredSize(new Dimension(jSlider.getPreferredSize().width,((getTable().getRowCount())*getTable().getRowHeight())));
		jSlider.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseReleased(java.awt.event.MouseEvent e) {

            	}
            });
    	jSlider.addChangeListener(new javax.swing.event.ChangeListener() {


				public void stateChanged(javax.swing.event.ChangeEvent e) {
					int value = (int) (getJSlider().getValue() * cr.getCommandCount() * 0.01);
                	try {
                    	if (!refreshing)
                    	cr.setPos(cr.getCommandCount()-1-value);
                    	//System.out.println("setPos = "+(cr.getCommandCount()-1-value));
					} catch (EditionCommandException e1) {
						e1.printStackTrace();
					}

                }
    		});
    	setValue(cr.getCommandCount()-1-cr.getPos(),true);
	}
    public void setValue(int number, boolean fireEvent) {
        if (number < lowLimit)
            number = lowLimit;
        if (number > cr.getCommandCount())
            number = cr.getCommandCount();
        if (number != currentValue) {
        	currentValue = number;
        	refreshControls();
        	if (fireEvent)
        		callValueChanged(new Integer(currentValue));
        }
        int selpos=cr.getCommandCount()-1-number;
        if (selpos>=0){
       	 getTable().setRowSelectionInterval(selpos,selpos);
        } else
       		 getTable().clearSelection();
    }
    /**
     * Refreshes all the mutable controls in this component.
     */
    private void refreshControls() {
    	int normalizedValue = (int) ((currentValue / (float) cr.getCommandCount())*100);
		refreshSlider(normalizedValue);
	}
    /**
	 * Sets the slider to the correct (scaled) position.
     * @param normalizedValue
     */
    private void refreshSlider(int normalizedValue) {
    	refreshing = true;
        getJSlider().setValue(normalizedValue);
        refreshing = false;
    }

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.add(getJSlider());
		}
		return jPanel1;
	}



	/**
	 * This method initializes pCenter
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPCenter() {
		if (pCenter == null) {
			pCenter = new JPanel();
			pCenter.setLayout(new BorderLayout());
			pCenter.add(getTable(), java.awt.BorderLayout.CENTER);
			pCenter.add(getJPanel1(), java.awt.BorderLayout.WEST);
		}
		return pCenter;
	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getPCenter());
		}
		return jScrollPane;
	}

	public void commandRefresh() {
		commandRepaint();

	}
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
