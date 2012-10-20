package com.iver.cit.gvsig.project.documents.layout.fframes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.project.documents.exceptions.SaveException;
import com.iver.cit.gvsig.project.documents.layout.fframes.gui.dialogs.FFrameOverViewDialog;
import com.iver.cit.gvsig.project.documents.layout.fframes.gui.dialogs.IFFrameDialog;
import com.iver.cit.gvsig.project.documents.layout.gui.Layout;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.ProjectViewFactory;
import com.iver.utiles.XMLEntity;

/**
 * FFrame to draw the locator map of a view in the Layout
 * 
 * @author Vicente Caballero Navarro
 */
public class FFrameOverView extends FFrameView implements IFFrameViewDependence {
	private MapContext assoc_map;
	private Rectangle2D extent;
	private FFrameView fframeview;
	private int dependenceIndex = -1;
	private boolean showCross = false;

	public void setShowCross(boolean showCross) {
		this.showCross = showCross;
	}

	public boolean getShowCross() {
		return showCross;
	}

	public void draw(Graphics2D g, AffineTransform at, Rectangle2D rv,
			BufferedImage imgBase) {

		g.setStroke(new BasicStroke());

		if (getMapContext() != null)
			try {
				getMapContext().getViewPort().setExtent(
						getMapContext().getFullExtent());
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
		refresh();
		super.draw(g, at, rv, imgBase);
		Rectangle2D r = getBoundingBox(at);

		if ((assoc_map != null)
				&& (assoc_map.getViewPort().getExtent() != null)
				&& (getMapContext().getViewPort().getExtent() != null)) {

			ViewPort vp = getMapContext().getViewPort();
			ViewPort vpOrig = assoc_map.getViewPort();

			if (extent == null) {
				extent = vpOrig.getAdjustedExtent();
			}
			vp.refreshExtent();
			Rectangle2D extentPixels = vp.fromMapRectangle(extent);

			extentPixels.setFrame(extentPixels.getX(), extentPixels.getY(),
					extentPixels.getWidth(), extentPixels.getHeight());
			Rectangle2D clip = g.getClipBounds();

			g.setClip((int) r.getX(), (int) r.getY(), (int) r.getWidth(),
					(int) r.getHeight());
			g.setColor(Color.red);
			g.draw(extentPixels);

			g.setColor(new Color(100, 100, 100, 100));
			g.fill(extentPixels);

			// Draw the cross if the user active this option
			if (showCross) {
				double pRightUp = (int) (r.getWidth() + r.getX());
				Line2D.Double linVert = new Line2D.Double(
						extentPixels.getCenterX(), r.getY(),
						extentPixels.getCenterX(), r.getMaxY());
				Line2D.Double linHoriz = new Line2D.Double(r.getX(),
						extentPixels.getCenterY(), pRightUp,
						extentPixels.getCenterY());
				g.setColor(Color.darkGray);
				g.draw(linVert);
				g.draw(linHoriz);
			}

			g.setClip((int) clip.getX(), (int) clip.getY(),
					(int) clip.getWidth(), (int) clip.getHeight());
			extent = null;
		}

	}

	public void print(Graphics2D g, AffineTransform at)
			throws ReadDriverException {
		draw(g, at, null, null);
	}

	/**
	 * Set the ProjectView from where it gets the properties of the view to show
	 * 
	 * @param v
	 *            Model of the view
	 */
	public void setView(ProjectView v) {
		view = v;

		ViewPort vp = null;

		if (getMapContext() != null) {
			vp = getMapContext().getViewPort();
		} else {
			vp = v.getMapContext().getViewPort().cloneViewPort();
		}

		vp.setImageSize(new Dimension((int) getBoundingBox(null).width,
				(int) getBoundingBox(null).height));
		if (fframeview == null)
			return;
		assoc_map = fframeview.getMapContext();

		if (fframeview.getLinked()) {
			m_fmap = fframeview
					.getView()
					.getMapOverViewContext()
					.createNewFMap(
							fframeview.getView().getMapOverViewContext()
									.getViewPort().cloneViewPort());
			m_fmap.getViewPort().setImageSize(
					new Dimension((int) getBoundingBox(null).width,
							(int) getBoundingBox(null).height));
			fframeview.getMapContext().getViewPort().addViewPortListener(this);
			v.getMapContext().getViewPort().addViewPortListener(this);
			fframeview.getView().getMapOverViewContext().addLayerListener(this);
		} else if (!fframeview.getLinked()) {
			try {
				m_fmap = fframeview.getView().getMapOverViewContext()
						.cloneFMap(); // (v.getMapContext().getViewPort().cloneViewPort());
				m_fmap.setViewPort(fframeview.getView().getMapOverViewContext()
						.getViewPort().cloneViewPort());
				m_fmap.getViewPort().setImageSize(
						new Dimension((int) getBoundingBox(null).width,
								(int) getBoundingBox(null).height));
				fframeview.getMapContext().getViewPort()
						.addViewPortListener(this);
				v.getMapContext().getViewPort().addViewPortListener(this);
				fframeview.getView().getMapOverViewContext()
						.addLayerListener(this);
			} catch (XMLException e1) {
				NotificationManager.addError("when_a_view_add_to_layout", e1);
			}
		}
	}

	/**
	 * @see com.iver.cit.gvsig.project.documents.layout.fframes.IFFrame#getNameFFrame()
	 */
	public String getNameFFrame() {
		return PluginServices.getText(this, "Localizador") + num;
	}

	public IFFrame cloneFFrame(Layout layout) {
		FFrameOverView frame = new FFrameOverView();
		frame.setLevel(this.getLevel());
		frame.setNum(this.num);
		frame.setName(this.getName());
		frame.setBoundBox(this.getBoundBox());
		frame.setTag(this.getTag());
		frame.m_Mode = this.m_Mode;
		frame.m_typeScale = this.m_typeScale;
		frame.m_extension = this.m_extension;
		frame.m_quality = this.m_quality;
		frame.m_viewing = this.m_viewing;
		frame.m_bLinked = this.m_bLinked;
		frame.m_mapUnits = this.m_mapUnits;
		frame.setRotation(this.getRotation());

		frame.m_Scale = this.m_Scale;
		frame.view = this.getView();
		frame.m_fmap = this.getMapContext();
		frame.setSelected(this.getSelected() != IFFrame.NOSELECT);
		frame.setLayout(layout);
		frame.assoc_map = this.assoc_map;
		frame.extent = this.extent;
		frame.dependenceIndex = dependenceIndex;
		frame.showCross = this.showCross;
		frame.fframeview = fframeview;
		frame.initDependence(layout.getLayoutContext().getAllFFrames());
		frame.setFrameLayoutFactory(factory);
		cloneActions(frame);
		return frame;
	}

	public IFFrameDialog getPropertyDialog() {
		return new FFrameOverViewDialog(getLayout(), this);
	}

	public void setFFrameDependence(IFFrame f) {
		fframeview = (FFrameView) f;
	}

	public IFFrame[] getFFrameDependence() {
		return new IFFrame[] { fframeview };
	}

	public void initDependence(IFFrame[] fframes) {
		if ((dependenceIndex != -1)
				&& fframes[dependenceIndex] instanceof FFrameView) {
			fframeview = (FFrameView) fframes[dependenceIndex];
			assoc_map = fframeview.getMapContext();

			if (fframeview.getLinked()) {
				m_fmap = fframeview
						.getView()
						.getMapOverViewContext()
						.createNewFMap(
								fframeview.getView().getMapOverViewContext()
										.getViewPort().cloneViewPort());
				m_fmap.getViewPort().setImageSize(
						new Dimension((int) getBoundingBox(null).width,
								(int) getBoundingBox(null).height));
				fframeview.getMapContext().getViewPort()
						.addViewPortListener(this);
				getView().getMapContext().getViewPort()
						.addViewPortListener(this);
				fframeview.getView().getMapOverViewContext()
						.addLayerListener(this);
			} else if (!fframeview.getLinked()) {
				try {
					m_fmap = fframeview.getView().getMapOverViewContext()
							.cloneFMap(); // (v.getMapContext().getViewPort().cloneViewPort());
					m_fmap.setViewPort(fframeview.getView()
							.getMapOverViewContext().getViewPort()
							.cloneViewPort());
					m_fmap.getViewPort().setImageSize(
							new Dimension((int) getBoundingBox(null).width,
									(int) getBoundingBox(null).height));
					fframeview.getMapContext().getViewPort()
							.addViewPortListener(this);
					getView().getMapContext().getViewPort()
							.addViewPortListener(this);
					fframeview.getView().getMapOverViewContext()
							.addLayerListener(this);
				} catch (XMLException e1) {
					NotificationManager.addError("when_a_view_add_to_layout",
							e1);
				}
			}
		}
	}

	/**
	 * @see com.iver.cit.gvsig.project.documents.layout.fframes.IFFrame#getXMLEntity()
	 */
	public XMLEntity getXMLEntity() throws SaveException {
		XMLEntity xml = super.getXMLEntity();
		try {
			if (fframeview != null) {
				Layout layout = fframeview.getLayout();
				IFFrame[] fframes = layout.getLayoutContext().getAllFFrames();

				for (int i = 0; i < fframes.length; i++) {
					if (fframeview.equals(fframes[i])) {
						xml.putProperty("index", i);
						break;
					}
				}
			}
		} catch (Exception e) {
			throw new SaveException(e, this.getClass().getName());
		}
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		if (xml.getIntProperty("m_Selected") != 0) {
			this.setSelected(true);
		} else {
			this.setSelected(false);
		}

		this.setName(xml.getStringProperty("m_name"));
		this.setBoundBox(new Rectangle2D.Double(xml.getDoubleProperty("x"), xml
				.getDoubleProperty("y"), xml.getDoubleProperty("w"), xml
				.getDoubleProperty("h")));

		this.m_Mode = xml.getIntProperty("m_Mode");
		this.m_typeScale = xml.getIntProperty("m_typeScale");
		this.m_extension = xml.getIntProperty("m_extension");
		this.m_quality = xml.getIntProperty("m_quality");
		this.m_viewing = xml.getIntProperty("m_viewing");
		this.m_bLinked = xml.getBooleanProperty("m_bLinked");
		this.m_mapUnits = xml.getIntProperty("m_mapUnits");
		setRotation(xml.getDoubleProperty("m_rotation"));

		this.m_Scale = xml.getDoubleProperty("m_Scale");

		int indice = xml.getIntProperty("indice");

		ProjectView view = null;

		if (xml.contains("viewName")) {
			view = (ProjectView) project.getProjectDocumentByName(
					xml.getStringProperty("viewName"),
					ProjectViewFactory.registerName);
		} else {
			if (indice != -1) {
				try {
					ArrayList views = project
							.getDocumentsByType(ProjectViewFactory.registerName);

					view = (ProjectView) views.get(indice);
				} catch (IndexOutOfBoundsException e) {
					NotificationManager.addError("index_not_found" + indice, e);
				}
			}
		}

		if (view != null) {
			this.setView(view);
		}
		if (xml.contains("index")) {
			dependenceIndex = xml.getIntProperty("index");
		}
	}

	public void refreshDependence(IFFrame fant, IFFrame fnew) {
		if ((fframeview != null) && fframeview.equals(fant)) {
			fframeview = (FFrameView) fnew;
			assoc_map = fframeview.getMapContext();

			if (fframeview.getLinked()) {
				m_fmap = fframeview
						.getView()
						.getMapOverViewContext()
						.createNewFMap(
								fframeview.getView().getMapOverViewContext()
										.getViewPort().cloneViewPort());
				m_fmap.getViewPort().setImageSize(
						new Dimension((int) getBoundingBox(null).width,
								(int) getBoundingBox(null).height));
				fframeview.getMapContext().getViewPort()
						.addViewPortListener(this);
				getView().getMapContext().getViewPort()
						.addViewPortListener(this);
				fframeview.getView().getMapOverViewContext()
						.addLayerListener(this);
			} else if (!fframeview.getLinked()) {
				try {
					m_fmap = fframeview.getView().getMapOverViewContext()
							.cloneFMap(); // (v.getMapContext().getViewPort().cloneViewPort());
					m_fmap.setViewPort(fframeview.getView()
							.getMapOverViewContext().getViewPort()
							.cloneViewPort());
					m_fmap.getViewPort().setImageSize(
							new Dimension((int) getBoundingBox(null).width,
									(int) getBoundingBox(null).height));
					fframeview.getMapContext().getViewPort()
							.addViewPortListener(this);
					getView().getMapContext().getViewPort()
							.addViewPortListener(this);
					fframeview.getView().getMapOverViewContext()
							.addLayerListener(this);
				} catch (XMLException e1) {
					NotificationManager.addError("when_a_view_add_to_layout",
							e1);
				}
			}
		}

	}
}
