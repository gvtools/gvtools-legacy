

package es.unex.sextante.inmemory.examples.tools;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A class which reads an ESRI ASCII raster file into a Raster
 * 
 * @author dmrust
 * 
 */
public class RasterReader {
   String  noData = Raster.DEFAULT_NODATA;
   Pattern header = Pattern.compile("^(\\w+)\\s+(-?\\d+(.\\d+)?)");


   public Raster readRaster(final String filename) throws IOException {
      final Raster raster = new Raster();
      final BufferedReader input = new BufferedReader(new FileReader(filename));
      while (input.ready()) {
         String line = input.readLine();
         final Matcher headMatch = header.matcher(line);
         //Match all the heads
         if (headMatch.matches()) {
            final String head = headMatch.group(1);
            final String value = headMatch.group(2);
            if (head.equalsIgnoreCase("nrows")) {
               raster.rows = Integer.parseInt(value);
            }
            else if (head.equalsIgnoreCase("ncols")) {
               raster.cols = Integer.parseInt(value);
            }
            else if (head.equalsIgnoreCase("xllcorner")) {
               raster.xll = Double.parseDouble(value);
            }
            else if (head.equalsIgnoreCase("yllcorner")) {
               raster.yll = Double.parseDouble(value);
            }
            else if (head.equalsIgnoreCase("NODATA_value")) {
               raster.NDATA = value;
            }
            else if (head.equals("cellsize")) {
               raster.cellsize = Double.parseDouble(value);
            }
            else {
               System.out.println("Unknown setting: " + line);
            }
         }
         else if (line.matches("^-?\\d+.*")) {
            //System.out.println( "Processing data section");
            //Check that data is set up!
            //Start processing numbers!
            int row = 0;
            final double[][] data = new double[raster.rows][];
            while (true) {
               //System.out.println( "Got data row: " + line );
               final String[] inData = line.split("\\s+");
               final double[] numData = new double[raster.cols];
               if (inData.length != numData.length) {
                  throw new RuntimeException("Wrong number of columns: Expected " + raster.cols + " got " + inData.length
                                             + " for line \n" + line);
               }
               for (int col = 0; col < raster.cols; col++) {
                  if (inData[col].equals(noData)) {
                     numData[col] = Double.NaN;
                  }
                  else {
                     numData[col] = Double.parseDouble(inData[col]);
                  }
               }
               data[row] = numData;
               //Ugly backward input structure...
               if (input.ready()) {
                  line = input.readLine();
               }
               else {
                  break;
               }
               row++;
            }
            if (row != raster.rows - 1) {
               throw new RuntimeException("Wrong number of rows: expected " + raster.rows + " got " + (row + 1));
            }
            raster.data = data;
         }
         else {
            if ((line.length() >= 0) && !line.matches("^\\s*$")) {
               System.out.println("Unknown line: " + line);
            }
         }
      }
      return raster;
   }
}
