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
package com.iver.cit.gvsig.fmap.core.rendering.styling.labeling;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.CartographicSupport;
import com.iver.cit.gvsig.fmap.core.CartographicSupportToolkit;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.ILabelStyle;
import com.iver.cit.gvsig.fmap.core.symbols.TestIMarkerSymbol;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelLocationMetrics;

public class TestLabelClass extends TestCase {
	/*
	private ITextSymbol textSymbol;
	private String labelExpression;
	private boolean isVisible = true;
	private String[] texts;
	private int priority;
	private double scale = 1;
	private String sqlQuery;
	*/
	
	private static final String[] names = new String [] {
		"aName",
		"otherName",
		"30949Mock",
	};
	
	private static final Dimension[] sizes = new Dimension[] {
		new Dimension(200, 150),
		new Dimension(100, 100),
		new Dimension(300, 100), // he detectat que esta falla en svg, pero crec que es per batik
		new Dimension(300, 180),
		new Dimension(300, 600),
		new Dimension(100, 200),
		new Dimension(10, 10),
	};
	
	private static final float INNER_TOLERANCE = TestIMarkerSymbol.INNER_TOLERANCE;
	private static final float OUTTER_TOLERANCE = TestIMarkerSymbol.OUTTER_TOLERANCE;

	private static final String TMP_DIR = System.getProperty("java.io.tmpdir")+"/test-files/";

	
	protected static ArrayList<ILabelStyle> testLabelStyles = new ArrayList<ILabelStyle>();

	
	@Override
	protected void setUp() throws Exception {
		File f = new File(TMP_DIR);
		if (!f.exists()) {
			f.mkdir();
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		deleteRecursively(new File(TMP_DIR));
	}
	
	private void deleteRecursively(File f) {
		if (f.isDirectory()) {
			for (int i = f.list().length-1; i >= 0; i--) {
				deleteRecursively(new File(f.getAbsolutePath()+File.separator+f.list()[i]));
			}
		}
		f.delete();
	}
	
	public void testSetName() throws Exception {
		for (int i = 0; i < names.length; i++) {
			LabelClass testClass = new LabelClass();
			testClass.setName(names[i]);
			assertTrue("LabelClass.setName() fails.", 
					names[i].equals(testClass.getName()));
		}
	}
	
	public static void addLabelStyleToTest(ILabelStyle labelSty){
		if (labelSty != null && !testLabelStyles.contains(labelSty))
			testLabelStyles.add(labelSty);
	}
	
	/**
	 * Tests if LabelClass always return a text symbol, since it is who
	 * actually draws the texts.
	 */
	public void testTextSymbol() {
		LabelClass lc = new LabelClass();
		assertNotNull("LabelClass.getTextSymbol() returns null when doing only new LabelClass()",
				lc.getTextSymbol());
		
		lc.setTextSymbol(null);
		assertNotNull("LabelClass.getTextSymbol() returns null after calling " +
				"LabelClass.setTextSymbol(null). The label class must provide always " +
				"a ITextSymbol.",
				lc.getTextSymbol());
	}
	
	public void testDefaultUnit() throws Exception {
		int u = CartographicSupportToolkit.DefaultMeasureUnit;
		for (int i = -1 /* -1 == pixel */; i < MapContext.getDistanceTrans2Meter().length; i++) {
			CartographicSupportToolkit.DefaultMeasureUnit = i;
			LabelClass lc = new LabelClass();
			assertTrue("fails creating with system's context's default measure unit",
					lc.getUnit() == CartographicSupportToolkit.DefaultMeasureUnit);
			assertTrue("LabelClass's XMLEntity's unit attribute is not correctly persisted",
					lc.getXMLEntity().getIntProperty("unit") == lc.getUnit());
		}
		
		// restore settings
		CartographicSupportToolkit.DefaultMeasureUnit = u;
		
		u = CartographicSupportToolkit.DefaultReferenceSystem;
		
		{
			CartographicSupportToolkit.DefaultReferenceSystem = CartographicSupport.PAPER;
			LabelClass lc = new LabelClass();
			assertTrue("fails creating with system's context's default measure reference system",
					lc.getReferenceSystem() == CartographicSupportToolkit.DefaultReferenceSystem);
			
			assertTrue("LabelClass's XMLEntity referenceSystem attribute is not correctly persisted",
					lc.getXMLEntity().getIntProperty("referenceSystem") == lc.getReferenceSystem());
		}

		{
			CartographicSupportToolkit.DefaultReferenceSystem = CartographicSupport.WORLD;
			LabelClass lc = new LabelClass();
			assertTrue("fails creating with system's context's default measure reference system",
					lc.getReferenceSystem() == CartographicSupportToolkit.DefaultReferenceSystem);
			
			assertTrue("LabelClass's XMLEntity referenceSystem attribute is not correctly persisted",
					lc.getXMLEntity().getIntProperty("referenceSystem") == lc.getReferenceSystem());
		}
		
		// restore settings
		CartographicSupportToolkit.DefaultReferenceSystem = u;
	}
	
	/**
	 * Ensures that sizing the label style causes the LabelClass exactly
	 * fits into its premises. It iterates over all the sizes defined in 
	 * the field <b>sizes</b>, and all the backgrounds defined in 
	 * <b>backgrounds</b> field.
	 */
	public void testDrawLabelClassWithBackgroundLabelStyle() {
		LabelClass lc = new LabelClass();
		if (testLabelStyles.size()==0) {
			fail("the test has no label styles to test, it is testing NOTHING!");
		}
		
		for (ILabelStyle aux : testLabelStyles) {
			for (int i = 0; i < sizes.length; i++) {
				ILabelStyle sty = (ILabelStyle) SymbologyFactory.
					createStyleFromXML(aux.getXMLEntity(), null);
				Dimension sz = sizes[i];
				sty.setSize(sz.getWidth(), sz.getHeight());
				
				LabelLocationMetrics llm = new LabelLocationMetrics(
						new Point2D.Double(sz.getWidth()/4, sz.getHeight()/4),
						0,
						true);
				
				// new blank buffered image
				BufferedImage bi = new BufferedImage(sz.width*2, sz.height*2, BufferedImage.TYPE_INT_ARGB);

				// the graphics for the image, so we can draw onto the buffered image
				Graphics2D graphics = bi.createGraphics();
				
				lc.setLabelStyle(sty);
				lc.draw(graphics, llm, new FPoint2D(llm.getAnchor()) /* Although for the test it is not necessary */);
				
				Rectangle wrappingRect = new Rectangle(
						(int) Math.round(sz.getWidth()/4),
						(int) Math.round(sz.getHeight()/4),
						(int) Math.round(sz.getWidth()),
						(int) Math.round(sz.getHeight()));

//				assertFalse("fails sizing LabelClass, too big ("+sz.width+"x"+sz.height+"px)", isOutsideRect(bi, wrappingRect, (int) OUTTER_TOLERANCE ));
//				assertTrue("fails sizing marker, too small ("+sz.width+"x"+sz.height+"px) \n" +
//						"\t - forgot to enable ANTIALIASING?", fitsInsideRect(bi, wrappingRect, INNER_TOLERANCE));
				try {
					fitsInOutBounds(bi, wrappingRect, INNER_TOLERANCE, OUTTER_TOLERANCE);
					ImageIO.write(bi, "png", new File(TMP_DIR+"LabelClass_"+sz.width+"x"+sz.height+"_"+sty.getDescription()+".png"));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (AssertionError e) {
					fail(sz.getWidth()+"x"+sz.getHeight()+"px, fails: "+e.getMessage());
				}

			}

		}
	}

	public void testDrawWithLabelLocationMetrics() {
		
	}
	
	private void fitsInOutBounds(BufferedImage bi, Rectangle bounds, float innerTolerance, float outterTolerance) {
		Rectangle2D bigBounds = new Rectangle2D.Double(
				bounds.getX()-outterTolerance,
				bounds.getY()-outterTolerance,
				bounds.getWidth()+(outterTolerance+outterTolerance),
				bounds.getHeight()+(outterTolerance+outterTolerance));
		
		Rectangle2D smallBounds = new Rectangle2D.Double(
				bounds.getX()+outterTolerance,
				bounds.getY()+outterTolerance,
				bounds.getWidth()-(outterTolerance+outterTolerance),
				bounds.getHeight()-(outterTolerance+outterTolerance));

		// ENSURE IT DOES NOT DRAW OUTSIDE THE BIG TOLERANCE
		
			// this is the outside the bigger tolerance check, they all must be 0
//			for (int j = 0; j < bi.getHeight(); j++) {
//				for (int i = 0; i < bi.getWidth(); i++) {
//					if (!bigBounds.contains(i, j)) {
//						if (bi.getRGB(i, j)!=0) {
//							throw new AssertionError("too big");
//						}
//						
//					}
//					
//				}
//			}

		// ENSURE IT DRAWS BETWEEN THE BIG AND THE SMALL TOLERANCE
			boolean exceedsTop    = false;
			boolean exceedsBottom = false;
			boolean exceedsLeft   = false;
			boolean exceedsRight  = false;

			// this is the exact top fit check, topFit will contain if
			// the label adjusts correctly to the top
			//
			// ---------------------------------------  <- Outter tolerance
			// |       ^                             |
			// |      /|\                            |
			// |  -----+--------------------------   |
			// |  |    |                         |   |
			// |  |    |                         |<--+---- Inner tolerance
			// |  |    label                     |   |
			// |  --------------------------------   |
			// |                                     |
			// |                                     |
			// ---------------------------------------
			boolean topFit = false;
			for (int j = 0; j < bi.getHeight(); j++) {
				for (int i = 0; !topFit && i < bi.getWidth(); i++) {
					if (j<bigBounds.getMinY() && !bigBounds.contains(i, j)) {
						if (bi.getRGB(i, j) != 0) {
							if (!exceedsTop && j<bigBounds.getY()) {
								exceedsTop = true;
							}
						}
					}
					
					if (bigBounds.contains(i, j) && !smallBounds.contains(i, j)) {
						// this is the area that at least must have 1 non-zero pixel
						if (bi.getRGB(i, j) != 0) {
							topFit = true;
						}
					}
				}
			}
			
			
			// this is the exact bottom fit check, bottomFit will contain
			// if the label adjusts correctly to the bottom
			// ---------------------------------------  <- Outter tolerance
			// |                                     |
			// |                                     |
			// |  --------------------------------   |
			// |  |    label                     |   |
			// |  |    |                         |<--+---- Inner tolerance
			// |  |    |                         |   |
			// |  -----+--------------------------   |
			// |      \|/                            |
			// |       `                             |
			// ---------------------------------------
			boolean bottomFit = false;
			for (int j = bi.getHeight()-1; j >= 0; j--) {
				for (int i = 0; !bottomFit && i < bi.getWidth(); i++) {
					if (j>bigBounds.getMaxY() && !bigBounds.contains(i, j)) {
						if (bi.getRGB(i, j) != 0) {
							if (!exceedsBottom) {
								exceedsBottom = true;
							}
						}
					}
					
					if (bigBounds.contains(i, j) && !smallBounds.contains(i, j)) {
						// this is the area that at least must have 1 non-zero pixel
						if (bi.getRGB(i, j) != 0) {
							bottomFit = true;
						}
					}
				}
			}
			
			// this is the exact top fit check, left will contain if
			// the label adjusts correctly to the left
			// ---------------------------------------  <- Outter tolerance
			// |                                     |
			// |                                     |
			// |  --------------------------------   |
			// |  |                              |   |
			// |<-+-------label                  |<--+---- Inner tolerance
			// |  |                              |   |
			// |  --------------------------------   |
			// |                                     |
			// |                                     |
			// ---------------------------------------

			boolean leftFit = false;
			for (int i = 0; !leftFit && i <= smallBounds.getX(); i++) {
				for (int j = 0; j < bi.getHeight(); j++) {
					if (i<bigBounds.getMinX() && !bigBounds.contains(i, j)) {
						if (bi.getRGB(i, j) != 0) {
							if (!exceedsLeft) {
								exceedsLeft = true;
							}
						}
					}
					
					if (bigBounds.contains(i, j) && !smallBounds.contains(i, j)) {
						// this is the area that at least must have 1 non-zero pixel
						if (bi.getRGB(i, j) != 0) {
							leftFit = true;
						}
					}
				}
			}
			
			// this is the exact bottom fit check, bottomFit will contain
			// if the label adjusts correctly to the bottom
			// this is the exact top fit check, left will contain if
			// the label adjusts correctly to the left
			// ---------------------------------------  <- Outter tolerance
			// |                                     |
			// |                                     |
			// |  --------------------------------   |
			// |  |                   label -----+-> |
			// |  |                              |<--+---- Inner tolerance
			// |  |                              |   |
			// |  --------------------------------   |
			// |                                     |
			// |                                     |
			// ---------------------------------------
			boolean rightFit = false;
			for (int i = bi.getWidth()-1; !rightFit && i >= smallBounds.getMaxX(); i--) {
				for (int j = 0; j < bi.getHeight(); j++) {
					if (i>bigBounds.getMaxX() && !bigBounds.contains(i, j)) {
						if (bi.getRGB(i, j) != 0) {
							if (!exceedsRight) {
								exceedsRight = true;
							}
						}
					}
					if (bigBounds.contains(i, j) && !smallBounds.contains(i, j)) {
						// this is the area that at least must have 1 non-zero pixel
						if (bi.getRGB(i, j) != 0) {
							rightFit = true;
						}
					}
				}
			}
			
			Graphics2D g = bi.createGraphics();
			g.setColor(Color.RED);
			g.draw(bigBounds);
			g.draw(smallBounds);
			if ((exceedsTop && exceedsBottom) || (exceedsLeft && exceedsRight)) {
				String message = "Label does not fit in the bounding box. ";
				if (exceedsTop || exceedsBottom || exceedsLeft || exceedsRight) {
					message += "However it fits in:";
					
					if (exceedsTop)   message += " top,";
					if (exceedsBottom)message += " bottom,";
					if (exceedsLeft)  message += " left,";
					if (exceedsRight) message += " right,";
				}
				
				throw new AssertionError(message);
			}
	}
	
}
