/*
 * Created on 25-jun-2004
 *
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
package com.iver.cit.gvsig.project.documents.layout;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.Sides;

import org.apache.log4j.Logger;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.printutils.UserMediaSizeName;
import com.iver.utiles.XMLEntity;


/**
 * Attributes of Layout.
 *
 * @author Vicente Caballero Navarro
 */
public class Attributes {
	
	private static Logger logger = Logger.getLogger(Attributes.class);
	/** Array of doubles containg the change factro from <b>CENTIMETERS</b> to KILOMETERS, METERS, CENTIMETERS, MILLIMETERS, MILES, YARDS, FEET, INCHES, DECIMAL DEGREES*/
	 /* Do not alter the order and the values of this array, if you need append values.*/
	public static final double[] CHANGE = {
			100000, 100, 1, 0.1, 160934.4, 91.44, 30.48, 2.54, 8.983152841195214E-6
		};
	public static final String DEGREES="Grados";
	/* Do not alter the order and the values of this array, if you need append values.*/
	public static final double[] UNIT = {
			0.0000025, 0.0025, 0.25, 2.5, 0.0000025, 0.025, 0.025, 0.25, 8.8E-7
		};
	public static final int HIGH = 0;
	public static final int NORMAL = 1;
	public static final int DRAFT = 2;

	//	Para impresión
	public final static double PULGADA = 2.54;

	public final static Size NA_LETTER_PAPER_SIZE = new Size(PULGADA * 11.0, PULGADA * 8.5);
	public final static Size NA_LEGAL_PAPER_SIZE = new Size(PULGADA * 14.0, PULGADA * 8.5);
	public final static Size NA_TABLOID_PAPER_SIZE = new Size(PULGADA * 17.0, PULGADA * 11.0);
	// public final static Size FOLIO_PAPER_SIZE = new Size(PULGADA * 13.0, PULGADA * 8.5);
	
	public final static Size METRIC_A0_PAPER_SIZE = new Size(118.9, 84.1);
	public final static Size METRIC_A1_PAPER_SIZE = new Size(84.1, 59.4);
	public final static Size METRIC_A2_PAPER_SIZE = new Size(59.4, 42.0);
	public final static Size METRIC_A3_PAPER_SIZE = new Size(42.0, 29.7);
	public final static Size METRIC_A4_PAPER_SIZE = new Size(29.7, 21.0);
	public final static Size METRIC_A5_PAPER_SIZE = new Size(21.0, 14.8);
	// public final static Size ANSI_ENG_A_PAPER_SIZE = new Size(11.0, 8.5);
	// public final static Size ANSI_ENG_B_PAPER_SIZE = new Size(17.0, 11.0);
	// public final static Size ANSI_ENG_C_PAPER_SIZE = new Size(22.0, 17.0);
	// public final static Size ANSI_ENG_D_PAPER_SIZE = new Size(34.0, 22.0);
	// public final static Size ANSI_ENG_E_PAPER_SIZE = new Size(44.0, 34.0);
	// public final static Size ANSI_ARCH_A_PAPER_SIZE = new Size(12.0, 9.0);
	// public final static Size ANSI_ARCH_B_PAPER_SIZE = new Size(18.0, 12.0);
	// public final static Size ANSI_ARCH_C_PAPER_SIZE = new Size(24.0, 18.0);
	// public final static Size ANSI_ARCH_D_PAPER_SIZE = new Size(36.0, 24.0);
	// public final static Size ANSI_ARCH_E_PAPER_SIZE = new Size(42.0, 30.0);
	
	public static Size CUSTOM_PAPER_SIZE = new Size(30.0, 20.0);
	
	// ===============================================
	public final static int PREPARE_PAGE_ID_PRINT = 0;
	public final static int PREPARE_PAGE_ID_A4 = 1;
	public final static int PREPARE_PAGE_ID_A3 = 2;
	public final static int PREPARE_PAGE_ID_A2 = 3;
	public final static int PREPARE_PAGE_ID_A1 = 4;
	public final static int PREPARE_PAGE_ID_A0 = 5;

	// added gvSIG EIEL march 2011 - start
	public final static int PREPARE_PAGE_ID_NA_LETTER = 6;
	public final static int PREPARE_PAGE_ID_NA_LEGAL = 7;
	public final static int PREPARE_PAGE_ID_EXEC_MONARCH = 8;
	public final static int PREPARE_PAGE_ID_B5_JIS = 9;
	public final static int PREPARE_PAGE_ID_TABLOID = 10;
	public final static int PREPARE_PAGE_ID_ENV_MONARCH = 11;
	public final static int PREPARE_PAGE_ID_ENV_N10 = 12;
	public final static int PREPARE_PAGE_ID_ENV_C5 = 13;
	public final static int PREPARE_PAGE_ID_ENV_DL = 14;
	public final static int PREPARE_PAGE_ID_ENV_B5 = 15;
	// ----------------------------------------------
	public final static int PREPARE_PAGE_ID_CUSTOM = 16;
	// ======================================
	// public final static Size US_LETTER_PAPER_SIZE = new Size(PULGADA * 8.5, PULGADA * 11);
	// public final static Size NA_LEGAL_PAPER_SIZE = new Size(PULGADA * 8.5, PULGADA * 14);
	public final static Size EXEC_MONARCH_PAPER_SIZE = new Size(PULGADA * 10.5, PULGADA * 7.25);
	public final static Size B5_JIS_PAPER_SIZE = new Size(25.7, 18.2);
	// public final static Size ENV_11X17_PAPER_SIZE = new Size(11, 17);
	public final static Size ENV_MONARCH_PAPER_SIZE = new Size(PULGADA * 7.5, PULGADA * 3.875);
	public final static Size ENV_N10_PAPER_SIZE = new Size(PULGADA * 9.5, PULGADA * 4.125);
	public final static Size ENV_C5_PAPER_SIZE = new Size(22.9, 16.2);
	public final static Size ENV_DL_PAPER_SIZE = new Size(22,11);
	public final static Size ENV_B5_PAPER_SIZE = new Size(25,17.6);
	
	
	// ==========================================
	// public static UserMediaSizeName USR_MEDIA_EXEC_MONARCH = new UserMediaSizeName("Executive");
	// public static UserMediaSizeName USR_MEDIA_B5_JIS = new UserMediaSizeName("B5 (JIS)");
	public static UserMediaSizeName USR_MEDIA_ENV_MONARCH = new UserMediaSizeName("Envelope_Monarch");
	public static UserMediaSizeName USR_MEDIA_ENV_N10 = new UserMediaSizeName("Envelope_no10");
	public static UserMediaSizeName USR_MEDIA_ENV_C5 = new UserMediaSizeName("Envelope_C5");
	public static UserMediaSizeName USR_MEDIA_ENV_DL = new UserMediaSizeName("Envelope_DL");
	public static UserMediaSizeName USR_MEDIA_ENV_B5 = new UserMediaSizeName("Envelope_B5");
	// added gvSIG EIEL march 2011 - end

	
	public static int DPI = 300;
	public static int DPISCREEN = 72;
	public static double STANDARD_SCREEN_DOTS_PER_CM = 72 / 2.54;
	
	public static Rectangle clipRect = new Rectangle();
	private static Point2D defaultGridGap;

//	static {
//		new Attributes();
//	}

	private PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
	private MediaSizeName m_type = MediaSizeName.ISO_A4;
	private boolean m_isLandSel;
	private OrientationRequested m_Orientation;
	private double m_TypeUnit = CHANGE[2]; //CENTIMETROS;
	private String m_NameUnit;
	private Double m_numX=null;
	private Double m_numY=null;
	private double m_unitX = 0;
	private double m_unitY = 0;
	private boolean hasmargin;
	private int m_resolutionSel = NORMAL;
	private int m_typeSel = PREPARE_PAGE_ID_PRINT;
	private int m_selTypeUnit = 2;
	public Size m_sizePaper = new Size(METRIC_A4_PAPER_SIZE.getAlto(),
			METRIC_A4_PAPER_SIZE.getAncho());
	public double[] m_area = { PULGADA, PULGADA, PULGADA, PULGADA };
	private double anchoXalto = m_sizePaper.getAlto() / m_sizePaper.getAncho();
	private Size m_sizeinUnits = m_sizePaper;
	private PrintQuality m_resolution=PrintQuality.NORMAL;
	/**
	 * Create a new object of Attributes.
	 *
	 */
	public Attributes() {
		m_NameUnit = PluginServices.getText(this,"Centimetros");
		m_type = MediaSizeName.ISO_A4;
		m_isLandSel = true;
		setSizeinUnits(m_isLandSel);

		hasmargin = false;
		m_Orientation = OrientationRequested.LANDSCAPE;
		attributes.add(new Copies(1));
		attributes.add(MediaSizeName.ISO_A4);
		attributes.add(Sides.ONE_SIDED);
		attributes.add(Fidelity.FIDELITY_FALSE);
		attributes.add(PrintQuality.NORMAL);

		setType(m_typeSel);

		attributes.add(new MediaPrintableArea(0, 0,
				(float) ((m_sizePaper.getAlto() * DPISCREEN) / PULGADA),
				(float) ((m_sizePaper.getAncho() * DPISCREEN) / PULGADA),
				MediaPrintableArea.MM));
	}

	/**
	 * Inserts the Layout's properties to print.
	 *
	 * @param typeSel Type of sheet.
	 * @param units Units.
	 * @param isLand True if the sheet is horizontal.
	 * @param margin True if the sheet has margin.
	 * @param resolution Type of quality of resolution.
	 * @param area Printing area.
	 */
	public void setSelectedOptions(int typeSel, int units, boolean isLand,
		boolean margin, int resolution, double[] area) {
		setType(typeSel);
		setUnit(units);
		setIsLandScape(isLand);
		hasmargin = margin;
		setResolution(resolution);
		m_area = area;
	}

	/**
	 * Returns a PageFormat with the properties of printing.
	 *
	 * @return PageFormat
	 */
	public PageFormat getPageFormat() {
		PageFormat pf1 = new PageFormat();
		Paper paper = pf1.getPaper();

		if (isLandSpace()) {
			pf1.setOrientation(0);
			paper.setSize((m_sizePaper.getAncho() * DPISCREEN) / PULGADA,
				(m_sizePaper.getAlto() * DPISCREEN) / PULGADA);

			double aux = m_area[0];
			m_area[0] = m_area[3];
			m_area[3] = m_area[1];
			m_area[1] = m_area[2];
			m_area[2] = aux;
			paper.setImageableArea(getInPixels(m_area[2]),
				getInPixels(m_area[0]),
				getInPixels(m_sizeinUnits.getAncho() - m_area[2] - m_area[3]),
				getInPixels(m_sizeinUnits.getAlto() - m_area[0] - m_area[1]));
		} else {
			pf1.setOrientation(1);
			paper.setSize((m_sizePaper.getAlto() * DPISCREEN) / PULGADA,
				(m_sizePaper.getAncho() * DPISCREEN) / PULGADA);
			paper.setImageableArea(getInPixels(m_area[2]),
				getInPixels(m_area[0]),
				getInPixels(m_sizeinUnits.getAlto() - m_area[2] - m_area[3]),
				getInPixels(m_sizeinUnits.getAncho() - m_area[0] - m_area[1]));
		}

		pf1.setPaper(paper);

		return pf1;
	}

	/**
	 * Changes centimeters to pixels.
	 *
	 * @param d Centimeters.
	 *
	 * @return Pixels.
	 */
	private double getInPixels(double d) {
		return d * (DPISCREEN / PULGADA);
	}

	/**
	 * Changes pixels to centimeters.
	 *
	 * @param d Pixel.
	 *
	 * @return Centimeters.
	 */
	private double getInCM(double d) {
		return d / (DPISCREEN / PULGADA);
	}

	/**
	 * Returns the printing area.
	 *
	 * @return Rectangle Area.
	 */
	public Rectangle2D getArea() {
		Rectangle2D.Double rect = new Rectangle2D.Double();
		rect.setRect(m_area[0], m_area[1],
			m_sizePaper.getAncho() - m_area[0] - m_area[2],
			m_sizePaper.getAlto() - m_area[1] - m_area[3]);

		return rect;
	}

	/**
	 * Get current 4 margin values as an array[0..3] [top, bottom, left, right]
	 * @return 4 margin values as an array[0..3] [top, bottom, left, right]
	 */
	public double[] getMargins() {
		return m_area;
	}

	/**
	 * Inserts the attributes with a PageFormat.
	 *
	 * @param pf PageFormat.
	 */
	public void setPageFormat(PageFormat pf) {
		Size size = null;

		if (pf.getOrientation() == 0) {
			setIsLandScape(true);
			m_Orientation = OrientationRequested.LANDSCAPE;
			size = new Size(pf.getHeight(), pf.getWidth());
		} else {
			setIsLandScape(false);
			m_Orientation = OrientationRequested.PORTRAIT;
			size = new Size(pf.getHeight(), pf.getWidth());
		}

		attributes.add(m_Orientation);

		//area
		m_area[0] = getInCM(pf.getImageableY());
		m_area[2] = getInCM(pf.getImageableX());
		m_area[1] = m_sizePaper.getAlto() - getInCM(pf.getImageableHeight()) -
			m_area[0];
		m_area[3] = m_sizePaper.getAncho() - getInCM(pf.getImageableWidth()) -
			m_area[2];

		//	tipo formato
		if (isLandSpace()) {
			//double aux = m_area[0];
			//m_area[0] = m_area[3];
			//m_area[3] = m_area[1];
			//m_area[1] = m_area[2];
			//m_area[2] = aux;
			attributes.add(new MediaPrintableArea((float) (m_area[2] * 10),
					(float) (m_area[0] * 10),
					(float) (m_sizeinUnits.getAlto() - m_area[0] - m_area[1]) * 10,
					(float) (m_sizeinUnits.getAncho() - m_area[2] - m_area[3]) * 10,
					MediaPrintableArea.MM));
		} else {
			attributes.add(new MediaPrintableArea((float) (m_area[0] * 10),
					(float) (m_area[1] * 10),
					(float) (getInCM(pf.getImageableWidth()) * 10),
					(float) (getInCM(pf.getImageableHeight()) * 10),
					MediaPrintableArea.MM));
		}

		setType(getTypePaper(size));
	}

	/**
	 * Returns a PrintRequestAttributeSet with all properties to the PrintDialog.
	 *
	 * @return PrintRequestAttributesSet.
	 */
	public PrintRequestAttributeSet toPrintAttributes() {
		HashPrintRequestAttributeSet resul = new HashPrintRequestAttributeSet();

		setType(m_typeSel);

		resul.add(m_type);

		//units, no hace falta añadirlo a attributes
		resul.add(m_Orientation);
		setArea(m_area);
		resul.add(new MediaPrintableArea(0, 0,
				(float) ((m_sizePaper.getAlto() * DPISCREEN) / PULGADA),
				(float) ((m_sizePaper.getAncho() * DPISCREEN) / PULGADA),
				MediaPrintableArea.MM));

		resul.add(m_resolution);
		//resul.add(new PrinterResolution(DPI,DPI,PrinterResolution.DPI));
		return resul;
	}

	/**
	 * Inserts printing area.
	 *
	 * @param area Printing area.
	 */
	public void setArea(double[] area) {
		if (!isLandSpace()) {
			attributes.add(new MediaPrintableArea((float) (area[2] * 10),
					(float) (area[0] * 10),
					(float) ((m_sizePaper.getAncho() - area[2] - area[3]) * 10),
					(float) ((m_sizePaper.getAlto() - area[0] - area[1]) * 10),
					MediaPrintableArea.MM));

			clipRect.setRect((area[2] / PULGADA * DPI),
				area[0] / PULGADA * DPI,
				(m_sizePaper.getAncho() - area[2] - area[3]) / PULGADA * DPI,
				(m_sizePaper.getAlto() - area[0] - area[1]) / PULGADA * DPI);
		} else {
			attributes.add(new MediaPrintableArea((float) (area[0] * 10),
					(float) (area[3] * 10),
					(float) ((m_sizePaper.getAlto() - area[0] - area[1]) * 10),
					(float) ((m_sizePaper.getAncho() - area[3] - area[2]) * 10),
					MediaPrintableArea.MM));

			clipRect.setRect((area[1] / PULGADA * DPI),
				area[2] / PULGADA * DPI,
				(m_sizePaper.getAncho() - area[1] - area[0]) / PULGADA * DPI,
				(m_sizePaper.getAlto() - area[2] - area[3]) / PULGADA * DPI);
		}
	}

	/**
	 * Returns true if the margin should be used.
	 *
	 * @return True if margin should be used.
	 */
	public boolean isMargin() {
		return hasmargin;
	}

	/**
	 * Returns the resolution of properties to print.
	 *
	 * @return Resolution.
	 */
	public int getResolution() {
		return m_resolutionSel;
	}

	/**
	 * Inserts the resolution to apply at printing.
	 *
	 * @param i Type of resolution.
	 *
	 * The type of resolution can be:
	 *
	 *  DRAFT: 	27 dpi.
	 *  NORMAL: 300 dpi.
	 *  HIGH: 	600 dpi.
	 */
	public void setResolution(int i) {
		m_resolutionSel = i;

		switch (i) {
			case (HIGH):

				m_resolution=PrintQuality.HIGH;
				DPI = 600;

				break;

			case (NORMAL):

				m_resolution=PrintQuality.NORMAL;
				DPI = 300;

				break;

			case (DRAFT):

				m_resolution=PrintQuality.DRAFT;
				DPI = 72;

				break;
		}
		attributes.add(m_resolution);
	}

	/**
	 * Returns attributes of PrintDialog.
	 *
	 * @return Attributes of printing.
	 */
	public PrintRequestAttributeSet getAttributes() {
		return attributes;
	}

	/**
	 * Returns True if the position of sheet is horizontal or vertical.
	 *
	 * @return True if position is horizontal.
	 */
	public boolean isLandSpace() {
		return m_isLandSel;
	}

	/**
	 * Returns the format of sheet.
	 *
	 * @return Format of sheet.
	 */
	public int getType() {
		return m_typeSel;
	}

	/**
	 * Inserts the number of units to define the coords X grid.
	 *
	 * @param d number of units to define the grid.
	 */
	public void setNumUnitsX(double d) {
		m_numX = new Double(d);
	}

	/**
	 * Returns the number of pixels that represents a unit of coords X.
	 *
	 * @return Number of pixels coords X.
	 */

	public double getNumUnitsX() {
		if (m_numX == null)
			m_numX = new Double(getDefaultGridGap().getX());
		return m_numX.doubleValue();
	}

	/**
	 * Returns the number of pixels that represents a unit of coords Y.
	 *
	 * @return Number of pixels coords Y.
	 */

	public double getNumUnitsY() {
		if (m_numY == null)
			m_numY = new Double(getDefaultGridGap().getY());
		return m_numY.doubleValue();
	}

	/**
	 * Inserts the number of units to define the coords X grid.
	 *
	 * @param d number of units to define the grid.
	 */
	public void setNumUnitsY(double d) {
		m_numY = new Double(d);
	}

	/**
	 * Inserts true if the sheet is horizontal or false if is vertical.
	 *
	 * @param b True if sheet is horizontal.
	 */
	public void setIsLandScape(boolean b) {
		m_isLandSel = b;
		setType(m_typeSel);

		if (m_isLandSel) {
			m_Orientation = OrientationRequested.LANDSCAPE;
		} else {
			m_Orientation = OrientationRequested.PORTRAIT;
		}
	}

	/**
	 * Inserts the type of sheet to show.
	 *
	 * @param t Type of sheet.
	 */
	public void setType(int t) {
		m_typeSel = t;

		//m_sizePaper=getSizePaper(t);
		switch (t) {
			case (PREPARE_PAGE_ID_PRINT):
				m_type = ((MediaSizeName) attributes.get(Media.class));

				if (isLandSpace()) {
					m_sizePaper = new Size(METRIC_A4_PAPER_SIZE.getAncho(),
							METRIC_A4_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = METRIC_A4_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_A4):
				m_type = MediaSizeName.ISO_A4;

				if (isLandSpace()) {
					m_sizePaper = new Size(METRIC_A4_PAPER_SIZE.getAncho(),
							METRIC_A4_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = METRIC_A4_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_A3):
				m_type = MediaSizeName.ISO_A3;

				if (isLandSpace()) {
					m_sizePaper = new Size(METRIC_A3_PAPER_SIZE.getAncho(),
							METRIC_A3_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = METRIC_A3_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_A2):
				m_type = MediaSizeName.ISO_A2;

				if (isLandSpace()) {
					m_sizePaper = new Size(METRIC_A2_PAPER_SIZE.getAncho(),
							METRIC_A2_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = METRIC_A2_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_A1):
				m_type = MediaSizeName.ISO_A1;

				if (isLandSpace()) {
					m_sizePaper = new Size(METRIC_A1_PAPER_SIZE.getAncho(),
							METRIC_A1_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = METRIC_A1_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_A0):
				m_type = MediaSizeName.ISO_A0;

				if (isLandSpace()) {
					m_sizePaper = new Size(METRIC_A0_PAPER_SIZE.getAncho(),
							METRIC_A0_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = METRIC_A0_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_CUSTOM):
				m_type = MediaSizeName.PERSONAL_ENVELOPE;
//			if (isLandSpace()) {
//			m_sizePaper = new Size(CUSTOM_PAPER_SIZE.getAncho()*m_TypeUnit,
//					CUSTOM_PAPER_SIZE.getAlto()*m_TypeUnit);
//		} else {
//			m_sizePaper = new Size(CUSTOM_PAPER_SIZE.getAlto()*m_TypeUnit,
//					CUSTOM_PAPER_SIZE.getAncho()*m_TypeUnit);
//		}
				break;
				
				

				// public final static int PREPARE_PAGE_ID_US_LETTER = 7;
			case (PREPARE_PAGE_ID_NA_LETTER):
				m_type = MediaSizeName.NA_LETTER;
				if (isLandSpace()) {
					m_sizePaper = new Size(
							NA_LETTER_PAPER_SIZE.getAncho(),
							NA_LETTER_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = NA_LETTER_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_US_LEGAL = 8;
			case (PREPARE_PAGE_ID_NA_LEGAL):
				m_type = MediaSizeName.NA_LEGAL;
				if (isLandSpace()) {
					m_sizePaper = new Size(
							NA_LEGAL_PAPER_SIZE.getAncho(),
							NA_LEGAL_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = NA_LEGAL_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_EXEC_MONARCH = 9;
			case (PREPARE_PAGE_ID_EXEC_MONARCH):
				m_type = MediaSizeName.EXECUTIVE;
				if (isLandSpace()) {
					m_sizePaper = new Size(
							EXEC_MONARCH_PAPER_SIZE.getAncho(),
							EXEC_MONARCH_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = EXEC_MONARCH_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_B5_JIS = 10;
			case (PREPARE_PAGE_ID_B5_JIS):
				m_type = MediaSizeName.JIS_B5;
				if (isLandSpace()) {
					m_sizePaper = new Size(
							B5_JIS_PAPER_SIZE.getAncho(),
							B5_JIS_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = B5_JIS_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_11X17 = 11;
			case (PREPARE_PAGE_ID_TABLOID):
				m_type = MediaSizeName.TABLOID;
				if (isLandSpace()) {
					m_sizePaper = new Size(
							NA_TABLOID_PAPER_SIZE.getAncho(),
							NA_TABLOID_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = NA_TABLOID_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_MONARCH = 12;
			case (PREPARE_PAGE_ID_ENV_MONARCH):
				m_type = MediaSizeName.MONARCH_ENVELOPE;
				if (isLandSpace()) {
					m_sizePaper = new Size(
							ENV_MONARCH_PAPER_SIZE.getAncho(),
							ENV_MONARCH_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = ENV_MONARCH_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_N10 = 13;
			case (PREPARE_PAGE_ID_ENV_N10):
				m_type = MediaSizeName.NA_NUMBER_10_ENVELOPE;
				if (isLandSpace()) {
					m_sizePaper = new Size(
							ENV_N10_PAPER_SIZE.getAncho(),
							ENV_N10_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = ENV_N10_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_C5 = 14;
			case (PREPARE_PAGE_ID_ENV_C5):
				m_type = USR_MEDIA_ENV_C5;
				if (isLandSpace()) {
					m_sizePaper = new Size(
							ENV_C5_PAPER_SIZE.getAncho(),
							ENV_C5_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = ENV_C5_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_DL = 15;
			case (PREPARE_PAGE_ID_ENV_DL):
				m_type = USR_MEDIA_ENV_DL;
			// MediaSizeName.NA_LETTER.
			
				if (isLandSpace()) {
					m_sizePaper = new Size(
							ENV_DL_PAPER_SIZE.getAncho(),
							ENV_DL_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = ENV_DL_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_B5 = 16;
			case (PREPARE_PAGE_ID_ENV_B5):
				m_type = USR_MEDIA_ENV_B5;
				if (isLandSpace()) {
					m_sizePaper = new Size(
							ENV_B5_PAPER_SIZE.getAncho(),
							ENV_B5_PAPER_SIZE.getAlto());
				} else {
					m_sizePaper = ENV_B5_PAPER_SIZE;
				}
				break;

		}
		attributes.add(m_type);
		setSizeinUnits(isLandSpace());
		m_sizeinUnits = getSizeInUnits();
	}

	/**
	 * Returns the size of sheet.
	 *
	 * @param isLand True if is horizontal and false if is vertical.
	 * @param type Type of sheet.
	 *
	 * @return Size of sheet with these properties.
	 */
	public Size getSizeinUnits(boolean isLand, int type) {
		Size size = null;

		switch (type) {
			case (PREPARE_PAGE_ID_PRINT):

				if (isLand) {
					size = new Size(METRIC_A4_PAPER_SIZE.getAncho(),
							METRIC_A4_PAPER_SIZE.getAlto());
				} else {
					size = METRIC_A4_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_A4):

				if (isLand) {
					size = new Size(METRIC_A4_PAPER_SIZE.getAncho(),
							METRIC_A4_PAPER_SIZE.getAlto());
				} else {
					size = METRIC_A4_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_A3):

				if (isLand) {
					size = new Size(METRIC_A3_PAPER_SIZE.getAncho(),
							METRIC_A3_PAPER_SIZE.getAlto());
				} else {
					size = METRIC_A3_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_A2):

				if (isLand) {
					size = new Size(METRIC_A2_PAPER_SIZE.getAncho(),
							METRIC_A2_PAPER_SIZE.getAlto());
				} else {
					size = METRIC_A2_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_A1):

				if (isLand) {
					size = new Size(METRIC_A1_PAPER_SIZE.getAncho(),
							METRIC_A1_PAPER_SIZE.getAlto());
				} else {
					size = METRIC_A1_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_A0):

				if (isLand) {
					size = new Size(METRIC_A0_PAPER_SIZE.getAncho(),
							METRIC_A0_PAPER_SIZE.getAlto());
				} else {
					size = METRIC_A0_PAPER_SIZE;
				}

				break;

			case (PREPARE_PAGE_ID_CUSTOM):

				size = new Size(CUSTOM_PAPER_SIZE.getAncho(),
						CUSTOM_PAPER_SIZE.getAlto());
				break;
				
				// =============================
				// =============================
				
			case (PREPARE_PAGE_ID_NA_LETTER):
				if (isLand) {
					size = new Size(
							NA_LETTER_PAPER_SIZE.getAncho(),
							NA_LETTER_PAPER_SIZE.getAlto());
				} else {
					size = NA_LETTER_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_US_LEGAL = 8;
			case (PREPARE_PAGE_ID_NA_LEGAL):
				if (isLand) {
					size = new Size(
							NA_LEGAL_PAPER_SIZE.getAncho(),
							NA_LEGAL_PAPER_SIZE.getAlto());
				} else {
					size = NA_LEGAL_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_EXEC_MONARCH = 9;
			case (PREPARE_PAGE_ID_EXEC_MONARCH):
				if (isLand) {
					size = new Size(
							EXEC_MONARCH_PAPER_SIZE.getAncho(),
							EXEC_MONARCH_PAPER_SIZE.getAlto());
				} else {
					size = EXEC_MONARCH_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_B5_JIS = 10;
			case (PREPARE_PAGE_ID_B5_JIS):
				if (isLand) {
					size = new Size(
							B5_JIS_PAPER_SIZE.getAncho(),
							B5_JIS_PAPER_SIZE.getAlto());
				} else {
					size = B5_JIS_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_11X17 = 11;
			case (PREPARE_PAGE_ID_TABLOID):
				if (isLand) {
					size = new Size(
							NA_TABLOID_PAPER_SIZE.getAncho(),
							NA_TABLOID_PAPER_SIZE.getAlto());
				} else {
					size = NA_TABLOID_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_MONARCH = 12;
			case (PREPARE_PAGE_ID_ENV_MONARCH):
				if (isLand) {
					size = new Size(
							ENV_MONARCH_PAPER_SIZE.getAncho(),
							ENV_MONARCH_PAPER_SIZE.getAlto());
				} else {
					size = ENV_MONARCH_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_N10 = 13;
			case (PREPARE_PAGE_ID_ENV_N10):
				if (isLand) {
					size = new Size(
							ENV_N10_PAPER_SIZE.getAncho(),
							ENV_N10_PAPER_SIZE.getAlto());
				} else {
					size = ENV_N10_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_C5 = 14;
			case (PREPARE_PAGE_ID_ENV_C5):
				if (isLand) {
					size = new Size(
							ENV_C5_PAPER_SIZE.getAncho(),
							ENV_C5_PAPER_SIZE.getAlto());
				} else {
					size = ENV_C5_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_DL = 15;
			case (PREPARE_PAGE_ID_ENV_DL):
				if (isLand) {
					size = new Size(
							ENV_DL_PAPER_SIZE.getAncho(),
							ENV_DL_PAPER_SIZE.getAlto());
				} else {
					size = ENV_DL_PAPER_SIZE;
				}
				break;
				// public final static int PREPARE_PAGE_ID_ENV_B5 = 16;
			case (PREPARE_PAGE_ID_ENV_B5):
				if (isLand) {
					size = new Size(
							ENV_B5_PAPER_SIZE.getAncho(),
							ENV_B5_PAPER_SIZE.getAlto());
				} else {
					size = ENV_B5_PAPER_SIZE;
				}
				break;

		}

		m_sizeinUnits = new Size(size.getAlto() / m_TypeUnit,
				size.getAncho() / m_TypeUnit);

		return m_sizeinUnits;
	}

	/**
	 * Retunrs the type of units is in use.
	 *
	 * @return Type of units.
	 */
	public int getSelTypeUnit() {
		return m_selTypeUnit;
	}

	/**
	 * Returns the name of units.
	 *
	 * @return String Name of units.
	 */
	public String getNameUnit() {
		return m_NameUnit;
	}

	/**
	 * Selecting the type of unit that is utilized in the Layout, by default is in centimetres.
	 *
	 * @param sel Type of unit.
	 */
	public void setUnit(int sel) {
		m_selTypeUnit = sel;
		m_TypeUnit = CHANGE[sel];
		m_NameUnit = PluginServices.getText(this,MapContext.getDistanceNames()[sel]);
		m_numX = m_numY = new Double(UNIT[sel]);
	}

	/**
	 * Actualize to date m_sizeinUnits with the height and width of the sheet
	 * in the units of mediated selected.
	 *
	 * @param b True if is horizontal.
	 */
	public void setSizeinUnits(boolean b) {
		if (b) {
			m_sizeinUnits = new Size(m_sizePaper.getAlto() / m_TypeUnit,
					m_sizePaper.getAncho() / m_TypeUnit);
		} else {
			m_sizeinUnits = new Size(m_sizePaper.getAncho() / m_TypeUnit,
					m_sizePaper.getAlto() / m_TypeUnit);
		}
	}

	/**
	 * Returns a double in the unit of measure selected from the double in centimeters.
	 *
	 * @param d Distance in centimeters.
	 *
	 * @return Distance in the unit measure selected.
	 */
	public double toUnits(double d) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);

		String s = String.valueOf(nf.format(d));
		s = s.replace(',', '.');

		return (Double.valueOf(s).doubleValue() / m_TypeUnit);
	}

	/**
	 * Returns a double in centimeters from the the unit of measure selected.
	 *
	 * @param d Distance in the unit measure selected.
	 *
	 * @return Distance in centimeters.
	 */
	public double fromUnits(double d) {
		return (d * m_TypeUnit);
	}

	/**
	 * Returns one centimeter in pixels on screen in this moment.
	 *
	 * @param rect Rectangle of sheet.
	 *
	 * @return Pixels from centimeter.
	 */
	public double getPixXCm(Rectangle2D rect) {
		double value = m_sizePaper.getAncho();
		double cm = CHANGE[2];
		double unidades = 0;
		unidades = ((rect.getMaxX() - rect.getMinX()) /((value / cm)));
		return unidades;
	}

	/**
	 * Inserts the number of pixels of one centimeter of X.
	 *
	 * @param rect Rectangle of sheet.
	 */
	public void setDistanceUnitX(Rectangle2D rect) {
		double value = m_sizePaper.getAncho();
		double unidades = ((rect.getMaxX() - rect.getMinX()) / ((value / m_TypeUnit) / getNumUnitsX()));
		m_unitX = unidades;
	}

	/**
	 * Inserts the number of pixels of one centimeter of Y.
	 *
	 * @param rect Rectangle of sheet.
	 */
	public void setDistanceUnitY(Rectangle2D rect) {
		double value = m_sizePaper.getAncho();
		double unidades = ((rect.getMaxX() - rect.getMinX()) / ((value / m_TypeUnit) / getNumUnitsY()));
		m_unitY = unidades;
	}

	/**
	 * Obtains the rectangle that represents the sheet with the characteristics
	 * that contains attributes
	 *
	 * @param rect rectangle.
	 *
	 */
	public void obtainRect(boolean isPrint, Rectangle2D rect, int w, int h) {
		double value1 = 0;
		double value2 = 0;

		if (!isPrint) {
			if (isLandSpace()) {
				anchoXalto = m_sizePaper.getAncho() / m_sizePaper.getAlto();
				rect = getRectangleLandscape(rect, w, h);
			} else {
				anchoXalto = m_sizePaper.getAlto() / m_sizePaper.getAncho();
				rect = getRectanglePortrait(rect, w, h);
			}
		} else {
			value1 = m_sizePaper.getAncho();
			value2 = m_sizePaper.getAlto();
			rect.setRect(0, 0, ((value1 / PULGADA) * DPI),
				((value2 / PULGADA) * DPI));
		}

		setDistanceUnitX(rect);
		setDistanceUnitY(rect);
	}

	/**
	 * It obtains the rect that is adjusted to the size of the window,
	 * to see the full extent of horizontal layout.
	 *
	 * @param rect Rectangle sheet.
	 * @param w Width of Layout.
	 * @param h Height of Layout.
	 *
	 * @return Rectangle modified.
	 */
	public Rectangle2D.Double getRectangleLandscape(Rectangle2D rect,
		int w, int h) {
		Rectangle2D.Double rectaux = new Rectangle2D.Double();
		int x0 = (int) rect.getMinX();
		int y0 = (int) rect.getMinY();
		int y1;
		int x1;
		y1 = (h - (2 * y0));
		x1 = (int) (y1 * anchoXalto);

		if (((int) (((h) - (2 * y0)) * anchoXalto)) > ((w) - (2 * x0))) {
			x1 = ((w) - (2 * x0));
			y1 = (int) (x1 / anchoXalto);
		}

		rectaux.setRect(x0, y0, x1, y1);

		return rectaux;
	}

	/**
	 * Returns the size of sheet in the units of measure selected.
	 *
	 * @return Size of sheet.
	 */
	public Size getSizeInUnits() {
		return m_sizeinUnits;
	}

	/**
	 * It obtains the rect that is adjusted to the size of the window,
	 * to see the full extent of vertical layout.
	 *
	 * @param rect Rectangle sheet.
	 * @param w Width of Layout.
	 * @param h Height of Layout.
	 *
	 * @return Rectangle modified.
	 */
	public Rectangle2D.Double getRectanglePortrait(Rectangle2D rect,
		int w, int h) {
		Rectangle2D.Double rectaux = new Rectangle2D.Double();
		int x0 = (int) rect.getMinX();
		int y0 = (int) rect.getMinY();
		int y1;
		int x1;
		x1 = (w - (2 * x0));
		y1 = (int) (x1 * anchoXalto);

		if (((int) (((w) - (2 * x0)) * anchoXalto)) > ((h) - (2 * y0))) {
			y1 = (h - (2 * y0));
			x1 = (int) (y1 / anchoXalto);
		}

		rectaux.setRect(x0, y0, x1, y1);

		return rectaux;
	}

	/**
	 * It obtains the type of format from the size.
	 *
	 * @param size Size of sheet.
	 *
	 * @return Type of sheet.
	 */
	private int getTypePaper(Size size) {

		float tol = 0.1f; //  1mm
		Size auxSize = null;

		if (isLandSpace()) {
			auxSize = new Size(size.getAncho() - tol, size.getAlto() - tol);
		} else {
			auxSize = new Size(size.getAlto() - tol, size.getAncho() - tol);
		}
		
		Size small = getSmallestFor(auxSize.getAncho(), auxSize.getAlto());
		if (small == null) {
			return PREPARE_PAGE_ID_A4;
		} else {
			Integer resp = idForSize(small);
			if (resp == null) {
				return PREPARE_PAGE_ID_A4;
			} else {
				return resp.intValue();
			}
		}
		

//		if ((((auxSize.getAncho() * PULGADA) / DPISCREEN) < (tol +
//				METRIC_A4_PAPER_SIZE.getAncho())) &&
//				(((auxSize.getAlto() * PULGADA) / DPISCREEN) < (tol +
//				METRIC_A4_PAPER_SIZE.getAlto()))) {
//			return PREPARE_PAGE_ID_A4;
//		} else if ((((auxSize.getAncho() * PULGADA) / DPISCREEN) < (tol +
//				METRIC_A3_PAPER_SIZE.getAncho())) &&
//				(((auxSize.getAlto() * PULGADA) / DPISCREEN) < (tol +
//				METRIC_A3_PAPER_SIZE.getAlto()))) {
//			return PREPARE_PAGE_ID_A3;
//		} else if ((((auxSize.getAncho() * PULGADA) / DPISCREEN) < (tol +
//				METRIC_A2_PAPER_SIZE.getAncho())) &&
//				(((auxSize.getAlto() * PULGADA) / DPISCREEN) < (tol +
//				METRIC_A2_PAPER_SIZE.getAlto()))) {
//			return PREPARE_PAGE_ID_A2;
//		} else if ((((auxSize.getAncho() * PULGADA) / DPISCREEN) < (tol +
//				METRIC_A1_PAPER_SIZE.getAncho())) &&
//				(((auxSize.getAlto() * PULGADA) / DPISCREEN) < (tol +
//				METRIC_A1_PAPER_SIZE.getAlto()))) {
//			return PREPARE_PAGE_ID_A1;
//		} else if ((((auxSize.getAncho() * PULGADA) / DPISCREEN) < (tol +
//				METRIC_A0_PAPER_SIZE.getAncho())) &&
//				(((auxSize.getAlto() * PULGADA) / DPISCREEN) < (tol +
//				METRIC_A0_PAPER_SIZE.getAlto()))) {
//			return PREPARE_PAGE_ID_A0;
//		}


	}

	/**
	 * It returns an Object XMLEntity with the information the necessary attributes
	 * to be able later to create again the original object.
	 *
	 * @return XMLEntity.
	 *
	 */
	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className",this.getClass().getName());
		xml.putProperty("DPI", DPI);
		xml.putProperty("DPISCREEN", DPISCREEN);
		xml.putProperty("m_TypeUnit", m_TypeUnit);
		xml.putProperty("m_numX", m_numX);
		xml.putProperty("m_numY", m_numY);
		xml.putProperty("m_unitX", m_unitX);
		xml.putProperty("m_unitY", m_unitY);
		xml.putProperty("hasmargin", hasmargin);
		xml.putProperty("m_resolutionSel", m_resolutionSel);
		xml.putProperty("m_typeSel", m_typeSel);
		xml.putProperty("m_selTypeUnit", m_selTypeUnit);
		xml.addChild(m_sizePaper.getXMLEntity());
		xml.putProperty("m_area", m_area);
		xml.putProperty("anchoXalto", anchoXalto);
		xml.addChild(m_sizeinUnits.getXMLEntity());

		// Landscape
		xml.putProperty("m_isLandSel", m_isLandSel);

		return xml;
	}

	/**
	 * It returns an Object of this class from a XMLEntity.
	 *
	 *
	 * @param xml XMLEntity
	 *
	 * @return Object of Attributes.
	 */
	public static Attributes createAtributes(XMLEntity xml) {
		Attributes atri = new Attributes();
		DPI = xml.getIntProperty("DPI");
		if (DPI==300){
			atri.m_resolution=PrintQuality.NORMAL;
		}else if (DPI==600){
			atri.m_resolution=PrintQuality.HIGH;
		}else if (DPI==72){
			atri.m_resolution=PrintQuality.DRAFT;
		}
		DPISCREEN = xml.getIntProperty("DPISCREEN");
		atri.m_TypeUnit = xml.getDoubleProperty("m_TypeUnit");
		atri.setNumUnitsX(xml.getDoubleProperty("m_numX"));
		atri.setNumUnitsY(xml.getDoubleProperty("m_numY"));
		atri.m_unitX = xml.getDoubleProperty("m_unitX");
		atri.m_unitY = xml.getDoubleProperty("m_unitY");
		atri.hasmargin = xml.getBooleanProperty("hasmargin");
		atri.m_resolutionSel = xml.getIntProperty("m_resolutionSel");
		atri.m_typeSel = xml.getIntProperty("m_typeSel");
		atri.m_selTypeUnit = xml.getIntProperty("m_selTypeUnit");
		atri.m_sizePaper = Size.createSize(xml.getChild(0));

		if (atri.hasmargin) {
			atri.m_area = xml.getDoubleArrayProperty("m_area");
		}

		atri.anchoXalto = xml.getDoubleProperty("anchoXalto");
		atri.m_sizeinUnits = Size.createSize(xml.getChild(1));
		atri.m_isLandSel = xml.getBooleanProperty("m_isLandSel");
		atri.setIsLandScape(atri.m_isLandSel);
		return atri;
	}

	/**
	 * It returns an Object of this class from a XMLEntity.
	 *
	 *
	 * @param xml XMLEntity
	 *
	 * @return Object of Attributes.
	 */
	public static Attributes createAtributes03(XMLEntity xml) {
		Attributes atri = new Attributes();
		DPI = xml.getIntProperty("DPI");
		if (DPI==300){
			atri.m_resolution=PrintQuality.NORMAL;
		}else if (DPI==600){
			atri.m_resolution=PrintQuality.HIGH;
		}else if (DPI==72){
			atri.m_resolution=PrintQuality.DRAFT;
		}
		DPISCREEN = xml.getIntProperty("DPISCREEN");
		atri.m_TypeUnit = xml.getDoubleProperty("m_TypeUnit");
		atri.setNumUnitsX(xml.getDoubleProperty("m_numX"));
		atri.setNumUnitsY(xml.getDoubleProperty("m_numY"));
		atri.m_unitX = xml.getDoubleProperty("m_unitX");
		atri.m_unitY = xml.getDoubleProperty("m_unitY");
		atri.hasmargin = xml.getBooleanProperty("hasmargin");
		atri.m_resolutionSel = xml.getIntProperty("m_resolutionSel");
		atri.m_typeSel = xml.getIntProperty("m_typeSel");
		atri.m_selTypeUnit = xml.getIntProperty("m_selTypeUnit");
		atri.m_sizePaper = Size.createSize(xml.getChild(0));

		if (atri.hasmargin) {
			atri.m_area = xml.getDoubleArrayProperty("m_area");
		}

		atri.anchoXalto = xml.getDoubleProperty("anchoXalto");
		atri.m_sizeinUnits = Size.createSize(xml.getChild(1));
		atri.m_isLandSel = xml.getBooleanProperty("m_isLandSel");
		atri.setIsLandScape(atri.m_isLandSel);
		return atri;
	}
	/**
	 * Inserts the default number of pixels that represents a unit of grid.
	 *
	 * @param hGap Horizontal distance.
	 * @param vGap Vertical distance.
	 */
	public static void setDefaultGridGap(double hGap, double vGap) {
		defaultGridGap = new Point2D.Double(hGap, vGap);
	}
	/**
	 * Returns the default number of pixels that represents a unit of grid.
	 *
	 * @return Number of pixels of grid.
	 */
	public static Point2D getDefaultGridGap() {
		if (defaultGridGap == null){
			XMLEntity xml = PluginServices.getPluginServices("com.iver.cit.gvsig").getPersistentXML();
			double hGap = xml.contains("DefaultLayoutGridHorizontalGapX") ? xml.getDoubleProperty("DefaultLayoutGridHorizontalGapX"): 0.25;
			double vGap = xml.contains("DefaultLayoutGridHorizontalGapY") ? xml.getDoubleProperty("DefaultLayoutGridHorizontalGapY"): 0.25;
			defaultGridGap = new Point2D.Double(hGap, vGap);
		}
		return defaultGridGap;
	}

	/**
	 * Devuelve el número de pixels de longitud en el eje Y que representa a la unidad  del mapa que está
	 * seleccionada, suelen ser centímetros.
	 *
	 * @return número de pixels que representa la unidad seleccionada.
	 */
	public double getUnitInPixelsY() {
		return m_unitY;
	}

	/**
	 * Devuelve el número de pixels de longitud en el eje X que representa a la unidad del mapa que está
	 * seleccionada, suelen ser centímetros.
	 *
	 * @return número de pixels que representa la unidad seleccionada.
	 */
	public double getUnitInPixelsX() {
		return m_unitX;
	}

	
	public static Size getSmallestFor(double ancho, double alto) {
		
		ArrayList cand = new ArrayList();
		Iterator kiter = PAPER_SIZES_LIST.keySet().iterator();
		Size aux = null;
		Integer kitem = null;
		while (kiter.hasNext()) {
			kitem = (Integer) kiter.next(); 
			aux = (Size) PAPER_SIZES_LIST.get(kitem);
			if (fitsIn(ancho, alto, aux) && isRealFormat(kitem)) {
				cand.add(aux);
			}
		}
		return smallest(cand);
	}
	
	private static boolean isRealFormat(Integer k) {
		return
		(k.intValue() != PREPARE_PAGE_ID_CUSTOM)
		&& (k.intValue() != PREPARE_PAGE_ID_PRINT);
	}

	public static HashMap PAPER_SIZES_LIST = new HashMap(); 
	static {
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_CUSTOM), CUSTOM_PAPER_SIZE);
		// as printer
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_PRINT), CUSTOM_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_A0), METRIC_A0_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_A1), METRIC_A1_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_A2), METRIC_A2_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_A3), METRIC_A3_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_A4), METRIC_A4_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_NA_LETTER), NA_LETTER_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_NA_LEGAL), NA_LEGAL_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_TABLOID), NA_TABLOID_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_EXEC_MONARCH), EXEC_MONARCH_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_B5_JIS), B5_JIS_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_ENV_MONARCH), ENV_MONARCH_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_ENV_N10), ENV_N10_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_ENV_C5), ENV_C5_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_ENV_DL), ENV_DL_PAPER_SIZE);
		PAPER_SIZES_LIST.put(new Integer(PREPARE_PAGE_ID_ENV_B5), ENV_B5_PAPER_SIZE);
	}
	
	public static HashMap PAPER_NAMES_LIST = new HashMap(); 
	static {
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_CUSTOM), new UserMediaSizeName(
				"Personalizado"));
		// as printer
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_PRINT), new UserMediaSizeName(
				"Igual_que_la_impresora"));
		
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_A0), MediaSizeName.ISO_A0);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_A1), MediaSizeName.ISO_A1);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_A2), MediaSizeName.ISO_A2);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_A3), MediaSizeName.ISO_A3);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_A4), MediaSizeName.ISO_A4);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_NA_LETTER), MediaSizeName.NA_LETTER);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_NA_LEGAL), MediaSizeName.NA_LEGAL);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_TABLOID), MediaSizeName.TABLOID);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_EXEC_MONARCH), MediaSizeName.EXECUTIVE);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_B5_JIS), MediaSizeName.JIS_B5);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_ENV_MONARCH), USR_MEDIA_ENV_MONARCH);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_ENV_N10), USR_MEDIA_ENV_N10);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_ENV_C5), USR_MEDIA_ENV_C5);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_ENV_DL), USR_MEDIA_ENV_DL);
		PAPER_NAMES_LIST.put(new Integer(PREPARE_PAGE_ID_ENV_B5), USR_MEDIA_ENV_B5);
	}

	
	public static Integer idForSize(Size sz) {
		
		Iterator kiter = PAPER_SIZES_LIST.keySet().iterator();
		Size aux = null;
		Integer kitem = null;
		while (kiter.hasNext()) {
			kitem = (Integer) kiter.next();
			aux = (Size) PAPER_SIZES_LIST.get(kitem);
			if (aux == sz) {
				return kitem;
			}
		}
		logger.error("Did not find paper size [h,w] : [" + sz.getAlto() + " , " + sz.getAncho() + "] (returned id a4)");
		return PREPARE_PAGE_ID_A4;
	}
	
	public static Size sizeForId(int id) {
		
		Iterator kiter = PAPER_SIZES_LIST.keySet().iterator();
		Integer kitem = null;
		while (kiter.hasNext()) {
			kitem = (Integer) kiter.next();
			if (kitem.intValue() == id) {
				return (Size) PAPER_SIZES_LIST.get(kitem);
			}
		}
		
		logger.error("Did not find paper type id: " + id + " (returned a4)");
		return METRIC_A4_PAPER_SIZE;
	}

	
	public static Size smallest(ArrayList sszs) {
		
		int len = sszs.size();
		
		if (len == 0) {
			return null;
		}
		
		if (len == 1) {
			return (Size) sszs.get(0);
		}
		
		int resp = 0;
		Size aux = (Size) sszs.get(0);
		double min = aux.getAlto() * aux.getAncho();
		double auxmin = 0;
		for (int i=1; i<len; i++) {
			aux = (Size) sszs.get(i);
			auxmin = aux.getAlto() * aux.getAncho();
			if (auxmin < min) {
				min = auxmin;
				resp = i;
			}
		}
		return (Size) sszs.get(resp);
	}
	
	public static boolean fitsIn(double anchoin, double altoin, Size outer) {
		return (anchoin <= outer.getAncho()) && (altoin <= outer.getAlto()); 
	}

	public static MediaSizeName getMediaSizeNameForId(int type) {
		
		Iterator iter = PAPER_NAMES_LIST.keySet().iterator();
		Integer kitem = null;
		while (iter.hasNext()) {
			
			kitem = (Integer) iter.next();
			if (kitem.intValue() == type) {
				return (MediaSizeName) PAPER_NAMES_LIST.get(kitem);
			}
		}
		logger.error("Did not find paper type id: " + type + " (returned a4)");
		return MediaSizeName.ISO_A4;
	}

	public static int getIdForMediaSizeName(MediaSizeName msn) {
		
		Iterator iter = PAPER_NAMES_LIST.keySet().iterator();
		Integer kitem = null;
		MediaSizeName item_msn = null;
		while (iter.hasNext()) {
			kitem = (Integer) iter.next();
			item_msn = (MediaSizeName) PAPER_NAMES_LIST.get(kitem);
			if (item_msn == msn) {
				return kitem.intValue();
			}
		}
		logger.error("Did not find msn: " + msn.toString() + " (returned id for a4)");
		return PREPARE_PAGE_ID_A4;
	}

	public Size getPaperSize() {
		return m_sizePaper;
	}

}

// [eiel-add-print-formats]
