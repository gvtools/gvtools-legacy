package es.unex.sextante.dataObjects;

import jaitools.tiledimage.DiskMemImage;

import java.awt.geom.Rectangle2D;
import java.awt.image.BandedSampleModel;
import java.awt.image.SampleModel;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.media.jai.JAI;

import com.sun.media.jai.codec.TIFFEncodeParam;

import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;

public class TiledRasterLayer
         extends
            AbstractRasterLayer {

   protected static final int TILE_SIZE = 512;

   protected DiskMemImage     m_Image;
   protected int              m_iDataType;
   protected AnalysisExtent   m_GridExtent;
   protected double           m_dNoDataValue;
   protected Object           m_CRS;
   protected String           m_sFilename;
   protected String           m_sName;


   public void create(final String sName,
                      final String sFilename,
                      final AnalysisExtent gridExtent,
                      final int iDataType,
                      final int iNumBands,
                      final Object crs) {

      m_GridExtent = gridExtent;
      m_iDataType = iDataType;
      m_CRS = crs;
      m_sFilename = sFilename;
      m_sName = sName;

      final SampleModel sm = new BandedSampleModel(iDataType, TILE_SIZE, TILE_SIZE, iNumBands);

      m_Image = new DiskMemImage(gridExtent.getNX(), gridExtent.getNY(), sm);


   }


   public int getBandsCount() {

      return m_Image.getNumBands();

   }


   public double getCellValueInLayerCoords(final int x,
                                           final int y,
                                           final int band) {

      return m_Image.getSampleFloat(x, y, band);

   }


   public int getDataType() {

      return m_iDataType;

   }


   public double getLayerCellSize() {

      return m_GridExtent.getCellSize();

   }


   public AnalysisExtent getLayerGridExtent() {

      return m_GridExtent;

   }


   public double getNoDataValue() {

      return m_dNoDataValue;

   }


   public void setCellValue(final int x,
                            final int y,
                            final int band,
                            final double value) {

      m_Image.setSample(x, y, band, value);

   }


   public void setNoDataValue(final double dNoDataValue) {

      m_dNoDataValue = dNoDataValue;

   }


   public Object getCRS() {

      return m_CRS;

   }


   public Rectangle2D getFullExtent() {

      return m_GridExtent.getAsRectangle2D();

   }


   public void close() {}


   public IOutputChannel getOutputChannel() {

      return new FileOutputChannel(m_sFilename);

   }


   public String getName() {

      return m_sName;

   }


   public void open() {}


   public void postProcess() throws Exception {

      final TIFFEncodeParam tep = new TIFFEncodeParam();
      tep.setWriteTiled(true);
      tep.setTileSize(TILE_SIZE, TILE_SIZE);
      JAI.create("filestore", m_Image, m_sFilename, "TIFF", tep);

      createWorldFile();

   }


   private void createWorldFile() throws IOException {

      final String sExtension = m_sFilename.substring(m_sFilename.lastIndexOf(".") + 1);
      final String sNewExtension = sExtension.substring(0, 1) + sExtension.substring(sExtension.length() - 1) + "w";
      final String sFilename = m_sFilename.substring(0, m_sFilename.lastIndexOf(".") + 1) + sNewExtension;

      final FileWriter f = new FileWriter(sFilename);
      final BufferedWriter fout = new BufferedWriter(f);
      fout.write(Double.toString(m_GridExtent.getCellSize()) + "\n");
      fout.write("0.0\n0.0\n");
      fout.write("-" + Double.toString(m_GridExtent.getCellSize()) + "\n");
      fout.write(Double.toString(m_GridExtent.getXMax() + m_GridExtent.getCellSize() / 2.) + "\n");
      fout.write(Double.toString(m_GridExtent.getYMin() - m_GridExtent.getCellSize() / 2.) + "\n");
      fout.close();
      f.close();

   }


   public void setName(final String sName) {

      m_sName = sName;

   }


   @Override
   public Object getBaseDataObject() {

      return m_Image;

   }


   @Override
   public void free() {

      m_Image = null;

   }

}
