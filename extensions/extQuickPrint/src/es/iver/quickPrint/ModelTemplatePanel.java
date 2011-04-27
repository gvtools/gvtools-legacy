package es.iver.quickPrint;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.net.URL;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.Print;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.exceptions.OpenException;
import com.iver.cit.gvsig.project.documents.layout.FLayoutDraw;
import com.iver.cit.gvsig.project.documents.layout.ProjectMap;
import com.iver.cit.gvsig.project.documents.layout.fframes.FFrameGrid;
import com.iver.cit.gvsig.project.documents.layout.fframes.FFrameLegend;
import com.iver.cit.gvsig.project.documents.layout.fframes.FFramePicture;
import com.iver.cit.gvsig.project.documents.layout.fframes.FFrameScaleBar;
import com.iver.cit.gvsig.project.documents.layout.fframes.FFrameText;
import com.iver.cit.gvsig.project.documents.layout.fframes.FFrameView;
import com.iver.cit.gvsig.project.documents.layout.fframes.IFFrame;
import com.iver.cit.gvsig.project.documents.layout.gui.Layout;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.xml.XMLEncodingUtils;
import com.iver.utiles.xmlEntity.generate.XmlTag;

public class ModelTemplatePanel {
	public static int DEFAULT=0;
	public static int WITHOUTLOGO=1;
	public static int IMAGE=2;

	public static final String[] formats=new String[]{"A4","A3","A2","A1","A0"};

	private String format="A4";
	private int copies=1;
	private String orientation=PluginServices.getText(ModelTemplatePanel.class,"horizontal");
	private String title="";
	private double scale=50000;
	private double grid=5000;
	private int legend=10;
	private int logo=DEFAULT;
	private String image="";
	private IView view;
	public static Integer[] sizeFont=new Integer[]{new Integer(4),new Integer(6),new Integer(8),new Integer(10),new Integer(12),new Integer(14),new Integer(16),new Integer(18),new Integer(20),new Integer(22),new Integer(24),new Integer(26),new Integer(28),new Integer(30)};
	private boolean isLegend;
	private boolean isGrid;
	private boolean forceScale=false;

	public ModelTemplatePanel(IView view) {
		this.view=view;
	}
	public int getCopies() {
		return copies;
	}
	public void setCopies(int copies) {
		this.copies = copies;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public double getGrid() {
		return grid;
	}
	public void setGrid(double grid) {
		this.grid = grid;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getLegend() {
		return legend;
	}
	public void setLegend(int legend) {
		this.legend = legend;
	}
	public int getLogo() {
		return logo;
	}
	public void setLogo(int logo) {
		this.logo = logo;
	}
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	public double getScale() {
		return scale;
	}
	public void setScale(double scale) {
		this.scale = scale;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	 /**
     * Método que abre una ventana nueva con el informe ya confeccionado.
     */
    public void openReport() {
//        Project p = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
        Layout layout = openTemplate();
        updateReport(layout);

        ProjectMap pmap = ProjectFactory.createMap(getTitle());
        pmap.setModel(layout);
        pmap.getModel().setProjectMap(pmap);
//        p.addDocument(pmap);
        PluginServices.getMDIManager().addWindow(layout);
    }

    /**
     * Método que exporta directamente el informe a PDF.
     */
    public void toPDFReport() {
        Layout layout = openTemplate();
        updateReport(layout);

        FLayoutDraw layoutDraw = new FLayoutDraw(layout);
        String generatePDF="";
        layoutDraw.toPDF(new File(generatePDF));
    }

    /**
     * Método que imprime directamente el informe.
     */
    public void printReport() {
        Layout layout = openTemplate();
        updateReport(layout);

        Print printExtension = (Print) PluginServices.getExtension(Print.class);
        int num=getCopies();
        for (int i = 0; i < num; i++) {
        	 printExtension.doPrint(layout);
		}

    }

	 /**
     * Carga la plantilla.
     *
     * @return Layout con el contenido de la plantilla ya cargado.
     */
    private Layout openTemplate() {
        Project p = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
        Layout layout = null;

        try {
        	String path=getPath();
            File xmlFile = new File(path);
            FileInputStream is = new FileInputStream(xmlFile);
            Reader reader = XMLEncodingUtils.getReader(is);
            XmlTag tag = (XmlTag) XmlTag.unmarshal(reader);

            try {
                XMLEntity xml = new XMLEntity(tag);
                layout = Layout.createLayout(xml, p);
            } catch (OpenException e) {
                e.showError();
            }
        } catch (FileNotFoundException e) {
            NotificationManager.addError(PluginServices.getText(this,
                    "Al_leer_la_leyenda"), e);
        } catch (MarshalException e) {
            NotificationManager.addError(PluginServices.getText(this,
                    "Al_leer_la_leyenda"), e);
        } catch (ValidationException e) {
            NotificationManager.addError(PluginServices.getText(this,
                    "Al_leer_la_leyenda"), e);
        }

        return layout;
    }

    private String getPath() {
    	return TemplateExtension.templatesDir + File.separator + getTemplateName();
	}

    private String getTemplateName() {
    	String format=getFormat();
		String orientation=getOrientation();
		String path="";
		if (orientation.equals(PluginServices.getText(ModelTemplatePanel.class,"horizontal"))){
			path=format+"H"+".gvt";
		}else{
			path=format+"V"+".gvt";
		}
		return path;
    }
	/**
     * Actualiza los datos del informe que queremos confeccionar.
     *
     * @param layout Layout sobre el que modificaremos. En nuestro caso es una plantilla.
     */
    private void updateReport(Layout layout) {
        IFFrame[] frames = layout.getLayoutContext().getFFrames();
        FFrameView fview = null;

        for (int i = 0; i < frames.length; i++) {
            IFFrame frame = frames[i];
            if (frame.getTag()==null)
            	continue;
            if (frame.getTag().equals("view")) {
                fview = (FFrameView) frame;

                fview.setView((ProjectView) ((View)view).getModel());
                if (isForceScale()){
                	fview.setScale(getScale());
                	fview.setTypeScale(FFrameView.MANUAL);
                	fview.refresh();
                }else{
                	fview.setTypeScale(FFrameView.AUTOMATICO);
                }
            }
        }

        if (isGrid()){
        	FFrameGrid fgrid=new FFrameGrid();
        	fgrid.setIsLine(true);
        	fgrid.setIntervalX(getGrid());
        	fgrid.setIntervalY(getGrid());
        	fgrid.setLayout(layout);
        	fgrid.setFFrameDependence(fview);
        	fview.setGrid(fgrid);
        	fview.showGrid(isGrid());
        }

        for (int i = 0; i < frames.length; i++) {
            IFFrame frame = frames[i];
            if (frame.getTag()==null)
            	continue;
            if (frame.getTag().equals("tittle")) {
                FFrameText ftext = (FFrameText) frame;
                ftext.clearText();
                String[] s=getTitle().split("\n");
                for (int j = 0; j < s.length; j++) {
					ftext.addText(s[j]);
				}
            } else if (frame.getTag().equals("scale")) {
                FFrameScaleBar fscale = (FFrameScaleBar) frame;
                fscale.setFFrameDependence(fview);
            } else if (frame.getTag().equals("legend")) {
            	FFrameLegend flegend = (FFrameLegend) frame;
            	if (isLegend()){
            		flegend.setFont(flegend.getFont().deriveFont(getLegend()));
            		flegend.setFFrameDependence(fview);
            	}else{
            		flegend.setBoundBox(new Rectangle2D.Double(0,0,0,0));
            	}
            } else if (frame.getTag().equals("image")) {
                FFramePicture fpicture = (FFramePicture) frame;
                if (getLogo()==DEFAULT){
                	fpicture.load(TemplateExtension.templatesDir+File.separator+"defaultLogo.png");
                }else if (getLogo()==IMAGE){
                	fpicture.load(getImage());
                }else{
                	fpicture.setBoundBox(new Rectangle2D.Double(0,0,0,0));
                }
            }
        }
    }
	public boolean isGrid() {
		return isGrid;
	}
	public void setGrid(boolean isGrid) {
		this.isGrid = isGrid;
	}
	public boolean isLegend() {
		return isLegend;
	}
	public void setLegend(boolean isLegend) {
		this.isLegend = isLegend;
	}
	public boolean isForceScale() {
		return forceScale;
	}
	public void forceScale(boolean b) {
		forceScale=b;
	}

}
