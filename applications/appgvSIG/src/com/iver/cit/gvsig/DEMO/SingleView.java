/*
 * Created on 19-may-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
package com.iver.cit.gvsig.DEMO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.iver.andami.messages.Messages;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.tools.PanListenerImpl;
import com.iver.cit.gvsig.fmap.tools.RectangleSelectionListener;
import com.iver.cit.gvsig.fmap.tools.ZoomOutListenerImpl;
import com.iver.cit.gvsig.fmap.tools.ZoomOutRightButtonListener;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MoveBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.PolygonBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.PolylineBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.RectangleBehavior;
import com.iver.cit.gvsig.gui.panels.FPanelAbout;
import com.iver.cit.gvsig.project.documents.view.MapOverview;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.cit.gvsig.project.documents.view.info.gui.FInfoDialog;
import com.iver.cit.gvsig.project.documents.view.toc.actions.ZoomAlTemaTocMenuEntry;
import com.iver.cit.gvsig.project.documents.view.toc.gui.TOC;
import com.iver.cit.gvsig.project.documents.view.toolListeners.AreaListener;
import com.iver.cit.gvsig.project.documents.view.toolListeners.InfoListener;
import com.iver.cit.gvsig.project.documents.view.toolListeners.MeasureListener;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;
import com.iver.cit.gvsig.project.documents.view.toolListeners.ZoomInListener;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;


/* import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension; */

/**
 * @author Administrador
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SingleView extends JFrame implements IView, WindowListener {
	public static ImageObserver m_ImageObserver = null;
	private static Logger logger = Logger.getLogger(SingleView.class.getName());

	public JPanel panelvista = null;

	//public FLegend jLeyenda;
	//private TOC vTOC;
	public MapControl m_MapControl;
	public FInfoDialog m_dlgInfo;
// 	public FProjection m_Projection;
	private JScrollPane jscrollTOC;
	private TOC m_TOC;
	private MapOverview m_MapLoc;
	private String m_Name = null;
	// private com.iver.cit.opensig.project.View modelo;

    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton btn_FullExtent;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToolBar jToolBar1;
    public Export jExport;
    private ComandosListener m_acL;

    private javax.swing.JMenuBar menuBar;

    private class myKeyAdapter extends KeyAdapter {
    		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		public void keyReleased(KeyEvent arg0) {
			System.out.println("Hola colega");
			super.keyReleased(arg0);
		}

	}
	private myKeyAdapter m_OrejaTeclado = new myKeyAdapter();
    /** Button group for pan, zoom, and all exclusive tools */
    private ButtonGroup myButtonGroup = new ButtonGroup();
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenu CapaMenu;
    private javax.swing.JMenuItem verTabla;
    private javax.swing.JMenu VistaMenu;
    private JTextArea m_txt;
    private JTextField m_txtCoords;


    SingleView() {
    	m_ImageObserver = this;
        setSize(640, 480);
        initComponents();

        // setExtendedState(MAXIMIZED_BOTH);
        addWindowListener((WindowListener) this);
        // m_Map.addMouseListener(this);
        m_dlgInfo = new FInfoDialog();
        // addMouseListener(this);
        // addKeyListener((KeyListener) this);
        addKeyListener(m_OrejaTeclado);

		LayerFactory.setDriversPath("D:/eclipse/workspace/_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");
		
		ExtensionPointsSingleton.getInstance().add("View_TocActions","ZoomAlTema",new ZoomAlTemaTocMenuEntry());
        // LayerFactory.setDriversPath("C:/Workspace-HEAD/_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");
        // m_MapControl.addTool(MapControl.ZOOM_MAS);
        // m_MapControl.addCoordsListener(this);

    }


	public MapControl getMapControl()
	{
		return m_MapControl;
	}
	public MapControl getMapOverview()
	{
		return m_MapLoc;
	}

    public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		PropertyConfigurator.configure(SingleView.class.getClassLoader()
														 .getResource("log4j.properties"));

		Locale loc = null;

		if (args.length == 0) {
			loc = Locale.getDefault();
		} else {
			loc = new Locale(args[0]);
		}

		Messages.init(loc);


        SingleView testApp = new SingleView();

        // testApp.getContentPane().add(elMapa);
        testApp.show();

    }

    /**
     * Add the tools to the tools panel.
     */
    private void addTools(JToolBar inPanel, MapControl inDisplay) {
        /* inPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = c.HORIZONTAL;
        c.insets = new Insets(1,1,1,1);
        c.weightx = 1;

        // set the width of the panel.
        inPanel.setPreferredSize(new Dimension(40,40));
        inPanel.setMinimumSize(new Dimension(40,10));



        c.gridy = 0;
        c.gridx = 0; */

    	inPanel.setLayout(new BorderLayout());

    	JToolBar tempPanel = new JToolBar();

        JButton  btnSymbolManager = new JButton();
        btnSymbolManager.setIcon(new javax.swing.ImageIcon(
		        "images/Link.png"));
        btnSymbolManager.setActionCommand("SYMBOL_MANAGER");
        btnSymbolManager.addActionListener(m_acL);
        tempPanel.add(btnSymbolManager);


        btn_FullExtent.setIcon(new javax.swing.ImageIcon(
                "images/MapContents.png"));
        btn_FullExtent.setActionCommand("ZOOM_TODO");
        btn_FullExtent.addActionListener(m_acL);
        tempPanel.add(btn_FullExtent);

        JButton  btnZoomPrevio = new JButton();
        btnZoomPrevio.setIcon(new javax.swing.ImageIcon(
		        "images/ZoomPrevio.png"));
        btnZoomPrevio.setActionCommand("ZOOM_PREVIO");
        btnZoomPrevio.addActionListener(m_acL);
        tempPanel.add(btnZoomPrevio);

        tempPanel.add(new JToolBar.Separator(new java.awt.Dimension(16,16)));
		// inPanel.add(new JToolBar.Separator());

        // the select tool not that it does anything
        // the zoom in tool
        // c.gridy++;
        // JRadioButton tempGISButton = new JRadioButton("Zoom In", ï¿½ new ZoomInCommand(this));
        // JRadioButton tempGISButton = new JRadioButton("Zoom In");
        JToggleButton tempGISButton = new JToggleButton("Zoom por rectángulo");
        tempGISButton.setBorder(null);
        tempGISButton.setIcon(new javax.swing.ImageIcon("images/zoomIn.png"));

        // tempGISButton.setSelectedIcon(new javax.swing.ImageIcon("images/zoomPrev.gif"));
        // tempGISButton.setRolloverIcon(tempGISButton.getSelectedIcon());
        tempGISButton.setRolloverEnabled(true);
        tempGISButton.setToolTipText(tempGISButton.getText());
        tempGISButton.setText("");
        tempGISButton.setSelected(true);
        tempGISButton.setActionCommand("ZOOM_MAS");
        tempGISButton.addActionListener(m_acL);

        //Listener de eventos de movimiento que pone las coordenadas del ratón en la barra de estado
        StatusBarListener sbl = new StatusBarListener(m_MapControl);

        // Zoom por rectángulo
        ZoomOutRightButtonListener zoil = new ZoomOutRightButtonListener(m_MapControl);
        ZoomInListener zil = new ZoomInListener(m_MapControl);
        m_MapControl.addMapTool("zoomIn", new Behavior[]{new RectangleBehavior(zil),
        				new PointBehavior(zoil), new MouseMovementBehavior(sbl)});

        m_MapControl.setTool("zoomIn"); // Por defecto

        //TODO: Antes funcionaba con esto
       /*      ZoomInListenerImpl zili = new ZoomInListenerImpl(m_MapControl);
            m_MapControl.setCursor(zili.getCursor());
            m_MapControl.addMapTool("zoomrect",
                    new RectangleBehavior(zili));
            m_MapControl.setTool("zoomrect");
            */
        myButtonGroup.add(tempGISButton);

        // inPanel.add(tempGISButton, c);
        tempPanel.add(tempGISButton);

        // the zoom out tool
        // c.gridy++;
        tempGISButton = new JToggleButton("Alejar"); //, new ZoomOutCommand(this));
        tempGISButton.setBorder(null);
        tempGISButton.setIcon(new javax.swing.ImageIcon("images/zoomOut.png"));
        // Zoom out (pinchas y el mapa se centra y te muestra más).
        // No es dibujando un rectángulo, es solo pinchando.

        ZoomOutListenerImpl zoli = new ZoomOutListenerImpl(m_MapControl);
        m_MapControl.addMapTool("zoomout", new PointBehavior(zoli));
        m_MapControl.setCursor(zoli.getCursor());

        // pan

        PanListenerImpl pli = new PanListenerImpl(m_MapControl);
        m_MapControl.setCursor(pli.getCursor());
        m_MapControl.addMapTool("pan", new MoveBehavior(pli));

        // info
        //Info por punto
        InfoListener il = new InfoListener(m_MapControl);
        //il.setModel(modelo);
        m_MapControl.addMapTool("info", new PointBehavior(il));

        //Selección por rectángulo
        RectangleSelectionListener rsl = new RectangleSelectionListener(m_MapControl);
        m_MapControl.addMapTool("rectSelection",
                new RectangleBehavior(rsl));

        // Medir

        MeasureListener mli = new MeasureListener(m_MapControl);
        m_MapControl.addMapTool("medicion", new Behavior[]{new PolylineBehavior(mli), new MouseMovementBehavior(sbl)});

        // Area

        AreaListener ali = new AreaListener(m_MapControl);
        m_MapControl.addMapTool("area", new Behavior[]{new PolygonBehavior(ali), new MouseMovementBehavior(sbl)});


        // tempGISButton.setSelectedIcon(getIcon("ZoomOutActive.png"));
        // tempGISButton.setRolloverIcon(tempGISButton.getSelectedIcon());
        // tempGISButton.setRolloverEnabled(true);
        tempGISButton.setToolTipText(tempGISButton.getText());
        tempGISButton.setText("");
        tempGISButton.setActionCommand("ZOOM_MENOS");
        tempGISButton.addActionListener(m_acL);

        myButtonGroup.add(tempGISButton);

        // inPanel.add(tempGISButton, c);
        tempPanel.add(tempGISButton);

        // The Pan Tool
        // c.gridy++;
        tempGISButton = new JToggleButton("Pan Around"); // , new PanCommand(this));
        tempGISButton.setBorder(null);
        tempGISButton.setIcon(new javax.swing.ImageIcon("images/pan.png")); // getIcon("PanInactive.png"));

        tempGISButton.setToolTipText(tempGISButton.getText());
        tempGISButton.setText("");
        tempGISButton.setActionCommand("PAN");
        tempGISButton.addActionListener(m_acL);

        myButtonGroup.add(tempGISButton);
        tempPanel.add(tempGISButton);

        tempGISButton = new JToggleButton("Info"); // , new PanCommand(this));
        tempGISButton.setBorder(null);
        tempGISButton.setIcon(new javax.swing.ImageIcon("images/identify.png")); // getIcon("PanInactive.png"));
        tempGISButton.setToolTipText(tempGISButton.getText());
        tempGISButton.setText("");
        tempGISButton.setActionCommand("INFO");
        tempGISButton.addActionListener(m_acL);

        myButtonGroup.add(tempGISButton);
        tempPanel.add(tempGISButton);

        tempGISButton = new JToggleButton("Selección por rectángulo");
        tempGISButton.setBorder(null);
        tempGISButton.setIcon(new javax.swing.ImageIcon("images/Select.png"));
        tempGISButton.setToolTipText(tempGISButton.getText());
        tempGISButton.setText("");
        tempGISButton.setActionCommand("SELRECT");
        tempGISButton.addActionListener(m_acL);

        myButtonGroup.add(tempGISButton);
        tempPanel.add(tempGISButton);


        tempGISButton = new JToggleButton("Medir distancia"); // , new PanCommand(this));
        tempGISButton.setBorder(null);
        tempGISButton.setIcon(new javax.swing.ImageIcon("images/medir.png")); // getIcon("PanInactive.png"));
        tempGISButton.setToolTipText(tempGISButton.getText());
        tempGISButton.setText("");
        tempGISButton.setActionCommand("MEDICION");
        tempGISButton.addActionListener(m_acL);

        myButtonGroup.add(tempGISButton);
        tempPanel.add(tempGISButton);

        tempGISButton = new JToggleButton("Medir area"); // , new PanCommand(this));
        tempGISButton.setBorder(null);
        tempGISButton.setIcon(new javax.swing.ImageIcon("images/poligono16.png")); // getIcon("PanInactive.png"));
        tempGISButton.setToolTipText(tempGISButton.getText());
        tempGISButton.setText("");
        tempGISButton.setActionCommand("MEDIRAREA");
        tempGISButton.addActionListener(m_acL);

        myButtonGroup.add(tempGISButton);
        tempPanel.add(tempGISButton);




        inPanel.setRollover(true);

        /* tempMenuButton = new GISMenuButton(tempEditNodesCommand, tempRadioButton);
        tempEditNodesCommand.setButton(tempMenuButton);
        tempMenuButton.initPanel();
        inPanel.add(tempMenuButton, c); */

        // Add a panel for the additional buttons.
        m_txtCoords = new JTextField();
        m_txtCoords.setPreferredSize(new Dimension(300,20));

        inPanel.add(tempPanel,BorderLayout.WEST);
        inPanel.add(m_txtCoords, BorderLayout.EAST);

        /* c.gridy++;
        c.anchor = c.NORTH;
        c.weighty = 1;
        c.fill = c.HORIZONTAL;
        inPanel.add(new JPanel(), c); */

        //        inPanel.add(myAuxillaryButtonPanel, c);

    }

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) { //GEN-FIRST:event_exitForm
        System.exit(0);
    }

    //GEN-LAST:event_exitForm
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }

	private void info(Point pScreen){
		ViewPort vp = m_MapControl.getMapContext().getViewPort();
		Point2D pReal = vp.toMapPoint(pScreen);

		FLayer[] sel = m_MapControl.getMapContext().getLayers().getActives();

		// Denro de queryByPoint tenemos que separar los registros que vienen asociados a una
		// capa o a otra. Supongo que lo correcto sería que montaramos un XML y usar el visor
		// de XML que ha empezado Fernando.
		// String strResul = m_MapControl.queryByPoint(pScreen,m_MapControl.toMapDistance(3),"");

		JDialog dlg = new JDialog();
		m_dlgInfo.setPreferredSize(m_dlgInfo.getSize());
		//m_dlgInfo.clearAll();

		// if (strResul == "" ) return;
		String strResul;

		for (int i=0; i< sel.length; i++)
		{
			FLayer laCapa = sel[i];

			/* if (laCapa instanceof FLyrWMS)
			{
				strResul = laCapa.QueryByPoint(pScreen,m_MapControl.toMapDistance(3),"" );
				// System.out.println("LWS: strResul="+strResul);
				m_dlgInfo.addLayerInfo(laCapa.getName(), strResul);
			}
			else
			{
				strResul = laCapa.QueryByPoint(pReal,m_MapControl.toMapDistance(3),"" );

		        String[] arraySplit = null;
		        arraySplit = strResul.split("\n");

				if (arraySplit.length > 1)
				{
					// System.out.println("LWS: strResul="+strResul);
					String descrip = "";
					if (laCapa.getParentLayer() != null)
						descrip = laCapa.getParentLayer().getName() + "_";
					m_dlgInfo.addLayerInfo(descrip + laCapa.getName(), strResul);
				}

			} */
		}
		//m_dlgInfo.refresh();
		dlg.getContentPane().add(m_dlgInfo);
		dlg.setModal(true);
		dlg.pack();
		dlg.show();

	}

    //GEN-LAST:event_exitMenuItemActionPerformed
    private void initComponents() { //GEN-BEGIN:initComponents
        jToolBar1 = new javax.swing.JToolBar();
        btn_FullExtent = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        jPanel1 = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exportMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        VistaMenu = new JMenu();
        CapaMenu = new JMenu();
        verTabla = new JMenuItem();

        m_txt = new JTextArea(0,0);
		// jToolBar1.add(m_txt);
		// m_txt.addKeyListener(m_OrejaTeclado);

        addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    exitForm(evt);
                }
            });

        jLabel1.setText("Escala:");

        // jToolBar1.add(jLabel1);
        jTextField1.setText("jTextField1");

        // jToolBar1.add(jTextField1);

		m_MapControl = new MapControl();

		m_MapControl.getMapContext().getViewPort()
				.setBackColor(new Color(220,220,255));

		m_TOC = new TOC();
		m_TOC.setMapContext(m_MapControl.getMapContext());
		m_MapLoc = new MapOverview(m_MapControl);
		m_MapLoc.setPreferredSize(new Dimension(150,200));
		JSplitPane tempMainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		tempMainSplit.setPreferredSize(new Dimension(500, 300));
		// jscrollTOC = new JScrollPane(m_TOC);

		//jscrollTOC.setPreferredSize(new Dimension(200, 1000));
		// jscrollTOC.setSize(300, 600);

		//jscrollTOC.setAutoscrolls(true);
		//jscrollTOC.setVisible(true);
		// tempMainSplit.setLeftComponent(jscrollTOC);

		// Ponemos el localizador
		JSplitPane tempSplitToc = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		tempSplitToc.setTopComponent(m_TOC);
		tempSplitToc.setBottomComponent(m_MapLoc);
		tempSplitToc.setResizeWeight(0.7);
		tempMainSplit.setLeftComponent(tempSplitToc);

		m_TOC.setVisible(true);

		tempMainSplit.setRightComponent(m_MapControl);

		this.getContentPane().setLayout(new BorderLayout());

		getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

		// getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);


		this.getContentPane().add(tempMainSplit, BorderLayout.CENTER);


        m_acL = new ComandosListener(m_MapControl, this);

        fileMenu.setText("Fichero");
        openMenuItem.setText("Añadir capa...");
        openMenuItem.setActionCommand("ADD_LAYER");
        openMenuItem.addActionListener(m_acL);
        fileMenu.add(openMenuItem);

        JMenuItem generateRedMenu = new JMenuItem("Generar red");
        generateRedMenu.setActionCommand("NETWORK_GENERATEREDFILE");
        generateRedMenu.addActionListener(m_acL);
        fileMenu.add(generateRedMenu);



		JMenuItem abrirEnMemoria = new JMenuItem();
		abrirEnMemoria.setText("Añadir capa en memoria...");
		abrirEnMemoria.setActionCommand("ADD_MEMORY_LAYER");
		abrirEnMemoria.addActionListener(m_acL);
		fileMenu.add(abrirEnMemoria);


//		a submenu
		JMenu abrirGT2 = new JMenu();
		abrirGT2.setText("Abrir capa GT2");

		JMenuItem abrirGT2_Shp = new JMenuItem();
		abrirGT2_Shp.setText("Shp");
		abrirGT2_Shp.setActionCommand("ADD_GT2_SHP");
		abrirGT2_Shp.addActionListener(m_acL);
		abrirGT2.add(abrirGT2_Shp);

		JMenuItem abrirGT2_PostGIS_propio = new JMenuItem();
		abrirGT2_PostGIS_propio.setText("PostGIS propio");
		abrirGT2_PostGIS_propio.setActionCommand("ADD_GT2_POSTGIS_PROPIO");
		abrirGT2_PostGIS_propio.addActionListener(m_acL);
		abrirGT2.add(abrirGT2_PostGIS_propio);

		// ADD_GT2_MYSQL_PROPIO
		JMenuItem abrirGT2_MySQL_propio = new JMenuItem();
		abrirGT2_MySQL_propio.setText("mySQL propio");
		abrirGT2_MySQL_propio.setActionCommand("ADD_GT2_MYSQL_PROPIO");
		abrirGT2_MySQL_propio.addActionListener(m_acL);
		abrirGT2.add(abrirGT2_MySQL_propio);

        // ADD_GT2_ARCSDE_PROPIO
        JMenuItem abrirGT2_ArcSDE_propio = new JMenuItem();
        abrirGT2_ArcSDE_propio.setText("ArcSDE propio");
        abrirGT2_ArcSDE_propio.setActionCommand("ADD_GT2_ARCSDE_PROPIO");
        abrirGT2_ArcSDE_propio.addActionListener(m_acL);
        abrirGT2.add(abrirGT2_ArcSDE_propio);

		JMenuItem abrirGT2_PostGIS = new JMenuItem();
		abrirGT2_PostGIS.setText("PostGIS");
		abrirGT2_PostGIS.setActionCommand("ADD_GT2_POSTGIS");
		abrirGT2_PostGIS.addActionListener(m_acL);
		abrirGT2.add(abrirGT2_PostGIS);

		JMenuItem abrirGT2_ArcSDE = new JMenuItem();
		abrirGT2_ArcSDE.setText("ArcSDE");
		abrirGT2_ArcSDE.setActionCommand("ADD_GT2_ARCSDE");
		abrirGT2_ArcSDE.addActionListener(m_acL);
		abrirGT2.add(abrirGT2_ArcSDE);

		fileMenu.add(abrirGT2);

		fileMenu.addSeparator();

		JMenuItem prueba = new JMenuItem();
		prueba.setText("Probar breaks");
		prueba.setActionCommand("PRUEBA");
		prueba.addActionListener(m_acL);
		fileMenu.add(prueba);

        /* saveMenuItem.setText("Guardar");
         fileMenu.add(saveMenuItem);
         saveAsMenuItem.setText("Guardar como ...");
         fileMenu.add(saveAsMenuItem);
         */
        exportMenuItem.setText("Exportar vista a...");
        exportMenuItem.setActionCommand("EXPORT");
        exportMenuItem.addActionListener(m_acL);
        fileMenu.add(exportMenuItem);

        exitMenuItem.setText("Salir");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    exitMenuItemActionPerformed(evt);
                }
            });

        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        /*editMenu.setText("Editar");
         cutMenuItem.setText("Cortar");
         editMenu.add(cutMenuItem);
         copyMenuItem.setText("Copiar");
         editMenu.add(copyMenuItem);
         pasteMenuItem.setText("Pegar");
         editMenu.add(pasteMenuItem);
         deleteMenuItem.setText("Borrar");
         editMenu.add(deleteMenuItem);
         menuBar.add(editMenu);
        */
        VistaMenu.setText("Vista");

		JMenuItem mnuOpenLocator = new JMenuItem();
		mnuOpenLocator.setText("Abrir localizador");
		mnuOpenLocator.setActionCommand("OPEN_LOCATOR");
		mnuOpenLocator.addActionListener(m_acL);
		VistaMenu.add(mnuOpenLocator);

        menuBar.add(VistaMenu);

        CapaMenu.setText("Capaa");
        verTabla.setText("Ver tabla de la capa activa...");
        verTabla.setActionCommand("VIEW_TABLE");
        verTabla.addActionListener(m_acL);

        CapaMenu.add(verTabla);
        menuBar.add(CapaMenu);

        helpMenu.setText("Ayuda");
              contentsMenuItem.setText("Temas de Ayuda");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setText("Acerca de...");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JDialog dlg = new JDialog();
                FPanelAbout panelAbout = new FPanelAbout();
                panelAbout.setPreferredSize(panelAbout.getSize());
                dlg.getContentPane().add(panelAbout);
                dlg.pack();
                dlg.setResizable(false);
                dlg.setModal(true);
                dlg.show();
            }
        });

        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);


		// m_TOC.refresh();


        addTools(jToolBar1, m_MapControl);
    }

    //GEN-END:initComponents

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    /* public void mouseClicked(MouseEvent E) {
        if (m_Map.m_Estado == FMap.INFO) {
            if (m_Map.m_Layers.size() == 0) {
                return;
            }

            Point pScreen = E.getPoint();
            Point2D.Double pReal = m_Map.ToMapPoint(pScreen);
            FLyrVect laCapa = m_TOC.getFirstLayerSelected(); // m_Mapa.GetLayer(0);
            String strResul = laCapa.QueryByPoint(pReal, 10);

            // System.out.println(pScreen);
            // System.out.println(pReal);
            System.out.println("strResul = " + strResul);

            // DlgInfo dlgInfo = new DlgInfo(this, true);
            m_dlgInfo.clearAll();
            m_dlgInfo.addLayerInfo(laCapa.m_LayerName, strResul);
            m_dlgInfo.refresh();
            m_dlgInfo.show();
        }
    } */

	public void repaintMap() {
		m_MapControl.drawMap(false);
		repaint();
		// m_Map.refresh();
	}



    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
        System.out.println("Salgo");
    }

    /**
     * class for handling messages from the window expecially the window closing event to end
     * the process when the window closes.
     */
    public void windowClosing(WindowEvent e) {
        System.out.println("Salgo");
        System.exit(0);
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }


	/* (non-Javadoc)
	 * @see com.iver.cit.opensig.gui.IView#getTOC()
	 */
	/*public IToc getTOC() {
		return m_TOC;
	} */

	public void showCoords(double x, double y)
	{
		m_txtCoords.setText("x= " + x + " y= " + y);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.opensig.gui.IView#getViewName()
	 */
	public String getViewName() {
		return "Ejemplo de Visor sencillo";
	}


	/* (non-Javadoc)
	 * @see com.iver.cit.opensig.gui.IView#setViewName(java.lang.String)
	 */
	public void setViewName(String viewName) {
		m_Name = viewName;

	}


	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.ICoordsListener#coordsChanged(double, double)
	 */
	public void coordsChanged(double x, double y) {
		showCoords(x, y);

	}



	public TOC getTOC() {
		return m_TOC;
	}
}
