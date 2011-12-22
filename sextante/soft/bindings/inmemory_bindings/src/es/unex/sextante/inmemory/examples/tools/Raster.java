

package es.unex.sextante.inmemory.examples.tools;

import java.util.Arrays;


/**
 * Represents my best guess at the ESRI ASCII raster format. I couldn't find any sensible documentation, so it supports the
 * following features:
 * <ul>
 * <li>cellsize, xll and xll are stored as doubles, and largely ignored
 * <li>NDATA has a string representation (as it is typically read in from an ascii string) and is internally represented as
 * Double.NaN, as this is safer and easier to deal with than the -9999 found in most rasters.
 * </ul>
 * 
 * @author dmrust
 * 
 */
public class Raster {
   protected double[][]       data;
   protected double           xll;
   protected double           yll;
   protected double           cellsize;
   protected int              cols;
   protected int              rows;
   protected String           NDATA;
   public static final String DEFAULT_NODATA = "-9999";


   public void print() {
      System.out.println("Rows: " + rows + " cols: " + cols + " cellsize " + cellsize);
      for (final double[] row : data) {
         for (final double val : row) {
            System.out.print(val + " ");
         }
         System.out.println("");
      }

   }


   /**
    * Creates an empty raster
    */
   public Raster() {

   }


   /**
    * Creates a raster from the given data
    * 
    * @param cellsize
    * @param xll
    * @param yll
    */
   public Raster(final double cellsize,
                 final double xll,
                 final double yll) {
      this();
      setCellsize(cellsize);
      setXll(xll);
      setYll(yll);
   }


   /**
    * Creates a raster from the given data
    * 
    * @param data
    * @param cellsize
    * @param xll
    * @param yll
    */
   public Raster(final double[][] data,
                 final double cellsize,
                 final double xll,
                 final double yll) {
      this(cellsize, xll, yll);
      setData(data);
   }


   /**
    * Creates a raster from the given data
    * 
    * @param data
    * @param cellsize
    * @param xll
    * @param yll
    */
   public Raster(final int[][] data,
                 final double cellsize,
                 final double xll,
                 final double yll) {
      this(cellsize, xll, yll);
      setData(data);
   }


   public static Raster getTempRaster(final double[][] data,
                                      final double xll,
                                      final double yll,
                                      final double size) {
      return getTempRaster(data, xll, yll, size, DEFAULT_NODATA);
   }


   public static Raster getTempRaster(final double[][] data,
                                      final double xll,
                                      final double yll,
                                      final double size,
                                      final String ndata) {
      final Raster a = new Raster();
      a.data = data;
      a.xll = xll;
      a.yll = yll;
      a.cellsize = size;
      a.NDATA = ndata;
      a.rows = data.length;
      a.cols = data[0].length;
      return a;
   }


   /**
    * Sets the parameters of this raster (rows, columns, corner, cellsize, NDATA etc) to be the same as the other raster. This
    * includes initialising the data array with NDATAs
    * 
    * @param other
    */
   public void init(final Raster other) {
      xll = other.xll;
      yll = other.yll;
      cellsize = other.cellsize;
      NDATA = other.NDATA;
      setSize(other.getRows(), other.getCols());
   }


   /**
    * Initialises the Raster to Double.NaN (i.e. NDATA)
    */
   public void initData() {
      initData(Double.NaN);
   }


   /**
    * Initialises the raster so the entire data array contains 'value'
    * 
    * @param value
    */
   public void initData(final double value) {
      data = new double[rows][];
      for (int i = 0; i < rows; i++) {
         data[i] = new double[cols];

         Arrays.fill(data[i], value);
      }
   }


   /**
    * Returns the underlying data array - NOTE: this is *NOT* a copy, if you change it, you change the data
    * 
    * @return
    */
   public double[][] getData() {
      return data;
   }


   public void setValue(final int row,
                        final int column,
                        final double value) {
      if ((row < rows) && (column < cols)) {
         data[row][column] = value;
      }
   }


   public double getValue(final int row,
                          final int column) {
      if ((row < rows) && (column < cols)) {
         return data[row][column];
      }
      return Double.NaN;
   }


   /**
    * Copies the given data into the underlying data array. Also updates the number of rows and columns.
    * 
    * @param data
    */
   public void setData(final double[][] data) {
      rows = data.length;
      cols = data[0].length;
      initData();
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < cols; j++) {
            this.data[i][j] = data[i][j];
         }
      }
   }


   /**
    * Copies the given data into the underlying data array. Also updates the number of rows and columns.
    * 
    * @param data
    */
   public void setData(final int[][] data) {
      rows = data.length;
      cols = data[0].length;
      initData();
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < cols; j++) {
            this.data[i][j] = data[i][j];
         }
      }
   }


   public double getXll() {
      return xll;
   }


   public void setXll(final double xll) {
      this.xll = xll;
   }


   public double getYll() {
      return yll;
   }


   public void setYll(final double yll) {
      this.yll = yll;
   }


   public double getCellsize() {
      return cellsize;
   }


   public void setCellsize(final double cellsize) {
      this.cellsize = cellsize;
   }


   public int getCols() {
      return cols;
   }


   public int getRows() {
      return rows;
   }


   /**
    * Sets the size of the raster, and also initialises the array with NDATA
    * 
    * @param nrows
    * @param columns
    */
   public void setSize(final int nrows,
                       final int columns) {
      this.rows = nrows;
      this.cols = columns;
      initData();
   }


   public String getNDATA() {
      return NDATA;
   }


   public void setNDATA(final String nDATA) {
      NDATA = nDATA;
   }
}
