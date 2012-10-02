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
*/
package org.gvsig.rastertools.vectorizacion.process;

import java.io.File;
import java.sql.Types;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.RasterProcess;
import org.gvsig.raster.buffer.RasterBufferInvalidException;
import org.gvsig.raster.datastruct.Extent;
import org.gvsig.raster.grid.Grid;
import org.gvsig.raster.grid.GridException;
import org.gvsig.raster.grid.GridExtent;
import org.gvsig.raster.process.RasterTask;
import org.gvsig.raster.process.RasterTaskQueue;
import org.gvsig.raster.util.RasterToolsUtil;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DXFLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfFieldsMapping;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
/**
 * Este proceso vectoriza la capa de entrada que debe estar ya preprocesada.
 * 03/07/2008
 * @author Victor Olaya (volaya@ya.com)
 * @author Nacho Brodin nachobrodin@gmail.com
 */
public class ContourLinesProcess extends RasterProcess {
	private FLyrRasterSE                  lyr                 = null;
	private double                        min                 = 0;
	private double                        max                 = 0;
	private double                        distance            = 0;
	private Extent                        extent              = null;
	private String                        fileName            = null;
	
	private char[][]                      m_Row               = null;
	private char[][]                      m_Col               = null; 
	private Grid                          grid                = null;
	private IWriter                       writer              = null;
	private int                           m_iGeometry         = 0;
	private double                        percent             = 0;
	
	private class NextContourInfo {
		public int iDir;
		public int x;
		public int y;
		public boolean doRow;
	}
	
	/**
	 * Parámetros obligatorios al proceso:
	 * <UL>
	 * <LI></LI>
	 * <LI>filename: Nombre del fichero de salida</LI>
	 * <LI></LI>
	 * <LI></LI>
	 * <LI></LI>
	 * <LI></LI>
	 * </UL> 
	 */
	public void init() {
		lyr = getLayerParam("layer");
		fileName = getStringParam("filename");
		min = getDoubleParam("min");
		max = getDoubleParam("max");
		distance = getDoubleParam("distance");
		extent = getExtentParam("extent");
		
		String sFields[] = new String[2];
		sFields[0] = "ID";
		sFields[1] = lyr.getName();
		
		LayerDefinition tableDef = null;
		if (fileName.endsWith(".dxf")) {
			writer = new DxfWriter();
			((DxfWriter) writer).setFile(new File(fileName));
			((DxfWriter) writer).setCrs(lyr.getCrs());
			tableDef = new DXFLayerDefinition();

			DxfFieldsMapping fieldsMapping = new DxfFieldsMapping();
			((DxfWriter) writer).setFieldMapping(fieldsMapping);
		}
		if(fileName.endsWith(".shp")) {
			writer = new ShpWriter();
			((ShpWriter)writer).setFile(new File(fileName));
			tableDef = new SHPLayerDefinition();
		}
		tableDef.setShapeType(FShape.LINE);
		
		int types[] = {Types.DOUBLE, Types.DOUBLE};
		FieldDescription[] fields = new FieldDescription[sFields.length];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new FieldDescription();
			fields[i].setFieldName(sFields[i]);
			fields[i].setFieldType(types[i]);
			fields[i].setFieldLength(getDataTypeLength(types[i]));
			fields[i].setFieldDecimalCount(5);
		}
		tableDef.setFieldsDesc(fields);
		tableDef.setName(fileName);

		try {
			writer.initialize(tableDef);
			writer.preProcess();	
		} catch (InitializeWriterException e) {
			RasterToolsUtil.messageBoxError("error_init_vectlayer", null, e);
		} catch (StartWriterVisitorException e) {
			RasterToolsUtil.messageBoxError("error_init_vectlayer", null, e);
		}
	}

	/**
	 * Tarea de recorte
	 */
	public void process() throws InterruptedException {
		GridExtent ge = null;
		insertLineLog("Contour Lines");
		if(extent != null)
			ge = new GridExtent(extent, lyr.getCellSize());
		
		try {
			grid = new Grid(lyr.getDataSource(), new int[]{0}, ge);
			if( min <= max && distance > 0 ) {
				if( min < grid.getMinValue() )
					min += distance * (int)((grid.getMinValue() - min) / distance);
				
				if( max > grid.getMaxValue() )
					max = grid.getMaxValue();

				createContours(min, max, distance);
				writer.postProcess();
			}
		} catch (RasterBufferInvalidException e) {
			RasterToolsUtil.messageBoxError("error_loading_grid", this, e);
		} catch (GridException e) {
			RasterToolsUtil.messageBoxError("error_loading_grid", this, e);
		} catch (StopWriterVisitorException e) {
			RasterToolsUtil.messageBoxError("error_closing_grid", this, e);
		} catch (VisitorException e) {
			RasterToolsUtil.messageBoxError("", this, e);
		} finally {
			if (incrementableTask != null) {
				incrementableTask.processFinalize();
				incrementableTask = null;
			}
		}
		if(externalActions != null)
			externalActions.end(fileName);
	}
	
	private void createContours(double dMin, double dMax, double dDistance) throws InterruptedException, GridException, VisitorException {
		int x, y;
		int i;
		int ID;
		int iNX,iNY;
		double dZ;
		double dValue = 0;
		RasterTask task = RasterTaskQueue.get(Thread.currentThread().toString());

		iNX = grid.getNX();
		iNY = grid.getNY();

		m_Row = new char[iNY][iNX];
		m_Col = new char[iNY][iNX];

		if( dDistance <= 0 )
			dDistance= 1;
		
		percent = 0;
		for(dZ = dMin, ID = 0; dZ <= dMax; dZ += dDistance) {
			double increment = (25 / (double)(iNY * (((dMax - dMin) / dDistance) + 1)));
			for(y = 0; y < iNY - 1; y++) {
				percent += increment;
				for(x = 0; x < iNX - 1; x++) {
					dValue = grid.getCellValue(y, x);
					if( dValue >= dZ ) {
						m_Row[y][x]	= (char) (grid.getCellValue(y, x + 1) <  dZ ? 1 : 0);
						m_Col[y][x]	= (char) (grid.getCellValue(y + 1, x) <  dZ ? 1 : 0);
					}
					else {
						m_Row[y][x]	= (char) (grid.getCellValue(y, x + 1) >= dZ ? 1 : 0);
						m_Col[y][x]	= (char) (grid.getCellValue(y + 1, x) >= dZ ? 1 : 0);
					}
				}
				if(task.getEvent() != null)
					task.manageEvent(task.getEvent());
			}

			increment = (75 / (double)(iNY * (((dMax - dMin) / dDistance) + 1)));
			for(y = 0; y < iNY - 1; y++) {
				percent += increment;
				for(x = 0; x < iNX - 1; x++) {
					if(m_Row[y][x] != 0) {
						for(i = 0; i < 2; i++) 
							findContour(x, y, dZ, true, ID++);
						m_Row[y][x]	= 0;
					}

					if(m_Col[y][x] != 0) {
						for(i=0; i<2; i++)
							findContour(x, y, dZ, false, ID++);
						m_Col[y][x]	= 0;
					}
				}
				if(task.getEvent() != null)
					task.manageEvent(task.getEvent());
			}
		}
	}

	private void findContour(int x, int y, double z, boolean doRow, int ID) throws GridException, InterruptedException, VisitorException {
		boolean doContinue = true;
		boolean bIsFirstPoint = true;
		int	zx	= doRow ? x + 1 : x;
		int zy	= doRow ? y : y + 1;
		double d = 0;
		double	xPos, yPos;
		double 	xMin = grid.getGridExtent().getMin().getX();
		double 	yMax = grid.getGridExtent().getMax().getY();
		FPolyline2D line;
		Value values[] = new Value[2];
		NextContourInfo info = new NextContourInfo();
		GeneralPathX genPathX = new GeneralPathX();

		info.x = x;
		info.y = y;
		info.iDir = 0;
		info.doRow = doRow;

		do{
			d = grid.getCellValue(info.y, info.x);
			d = (d - z) / (d - grid.getCellValue(zy, zx));

			xPos = xMin + grid.getCellSize() * (info.x + d * (zx - info.x) + 0.5);
			yPos = yMax - grid.getCellSize() * (info.y + d * (zy - info.y) + 0.5);

			if (bIsFirstPoint) {
				genPathX.moveTo(xPos, yPos);
				bIsFirstPoint = false;
			} else
				genPathX.lineTo(xPos, yPos);

			if( !findNextContour(info) )
				doContinue	= findNextContour(info);

			info.iDir = (info.iDir + 5) % 8;

			if(info.doRow) {
				m_Row[info.y][info.x]	= 0;
				zx = info.x + 1;
				zy = info.y;
			} else {
				m_Col[info.y][info.x]	= 0;
				zx = info.x;
				zy = info.y + 1;
			}

		}
		while(doContinue);

		values[0] = ValueFactory.createValue(ID);
		values[1] = ValueFactory.createValue(z);

		line =  new FPolyline2D(genPathX);


		addShape(line, values);
	}

	private boolean findNextContour(NextContourInfo info) {
		boolean	doContinue;

		if(info.doRow) {
			switch(info.iDir) {
			case 0:
				if(m_Row[info.y + 1][info.x] != 0) {
					info.y++;
					info.iDir = 0;
					doContinue = true;
					break;
				}
			case 1:
				if(m_Col[info.y][info.x + 1] != 0) {
					info.x++;
					info.iDir = 1;
					info.doRow = false;
					doContinue = true;
					break;
				}
			case 2:
			case 3:
				if(info.y-1 >= 0) {
					if(m_Col[info.y - 1][info.x + 1] != 0) {
						info.x++;
						info.y--;
						info.doRow = false;
						info.iDir = 3;
						doContinue = true;
						break;
					}
				}
			case 4:
				if(info.y-1>=0) {
					if(m_Row[info.y - 1][info.x]!= 0) {
						info.y--;
						info.iDir = 4;
						doContinue = true;
						break;
					}
				}
			case 5:
				if(info.y-1>=0) {
					if(m_Col[info.y - 1][info.x] != 0) {
						info.y--;
						info.doRow = false;
						info.iDir = 5 ;
						doContinue = true;
						break;
					}
				}
			case 6:
			case 7:
				if(m_Col[info.y][info.x]!= 0) {
					info.doRow = false;
					info.iDir = 7;
					doContinue = true;
					break;
				}
			default:
				info.iDir = 0;
				doContinue = false;
			}
		}
		else{
			switch(info.iDir) {
			case 0:
			case 1:
				if(m_Row[info.y + 1][info.x] != 0) {
					info.y++;
					info.doRow = true;
					info.iDir = 1;
					doContinue = true;
					break;
				}
			case 2:
				if(m_Col[info.y][info.x + 1] != 0) {
					info.x++;
					info.iDir = 2;
					doContinue = true;
					break;
				}
			case 3:
				if(m_Row[info.y][info.x] != 0) {
					info.doRow = true;
					info.iDir = 3;
					doContinue = true;
					break;
				}
			case 4:
			case 5:
				if(info.x-1>=0) {
					if(m_Row[info.y][info.x - 1] != 0) {
						info.x--;
						info.doRow = true;
						info.iDir = 5;
						doContinue = true;
						break;
					}
				}
			case 6:
				if(info.x-1>=0) {
					if(m_Col[info.y][info.x - 1] != 0) {
						info.x--;
						info.iDir = 6;
						doContinue = true;
						break;
					}
				}
			case 7:
				if(info.x-1 >= 0) {
					if(m_Row[info.y + 1][info.x - 1] != 0) {
						info.x--;
						info.y++;
						info.doRow = true;
						info.iDir = 7;
						doContinue = true;
						break;
					}
				}
			default:
				info.iDir = 0;
				doContinue = false;
			}
		}

		return(doContinue);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#getPercent()
	 */
	public int getPercent() {
		return (int)percent;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#getTitle()
	 */
	public String getTitle() {
		return RasterToolsUtil.getText(this, "vectorization");
	}
	
	public void addShape(FShape shape, Value[] value) throws VisitorException  {
		if (shape == null) 
			return;
		IGeometry geom = ShapeFactory.createGeometry(shape);
		addGeometry(geom, value);
	}

	public void addGeometry(IGeometry geom, Value[] value) throws VisitorException {
		DefaultFeature feat = new DefaultFeature(geom, value, Integer.toString(m_iGeometry));
		IRowEdited editFeat = new DefaultRowEdited(feat, IRowEdited.STATUS_MODIFIED, m_iGeometry);
		m_iGeometry++;
		writer.process(editFeat);
	}
	
	/**
		 * Returns the length of field
		 * @param dataType
		 * @return length of field
		 */
	public int getDataTypeLength(int dataType) {
		switch (dataType) {
		case Types.NUMERIC:
		case Types.DOUBLE:
		case Types.REAL:
		case Types.FLOAT:
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.DECIMAL:
			return 20;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return 254;
		case Types.DATE:
			return 8;
		case Types.BOOLEAN:
		case Types.BIT:
			return 1;
		}
		return 0;
	}
}