/*
 * Created on 16-feb-2005
 */
package com.iver.cit.gvsig.fmap.layers;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

import com.iver.cit.gvsig.fmap.ViewPort;

/**
 * Cálculo de Partes (Tiles) en las que se divide un raster grande.
 * Se usa para imprimir rasters y capas raste remotas (WMS).
 * 
 * Para no pedir imagenes demasiado grandes, vamos
 * a hacer lo mismo que hace EcwFile: chunkear.
 * Llamamos a drawView con cuadraditos más pequeños
 * del BufferedImage ni caso, cuando se imprime viene con null
 * código original de Fran Peñarrubia
 * @author Luis W. Sevilla (sevilla_lui@gva.es)
 */

public class Tiling {
	private static final int		MIN_SIZE = 50; //Tamaño mínimo en pixeles del tile
	private boolean 				debug = true;
	private int 					tileMaxWidth, tileMaxHeight;
	private int 					numRows, numCols;
	private double[][] 				srcPts;
	private Rectangle[] 			tile;
	private double 					width = 500, height = 500;	
	private AffineTransform 		mat;
	private ViewPort 				vp;
		
	public Tiling(){}
	
	public Tiling(int tileW, int tileH, Rectangle r) {
		int[] size = this.calcMaxTileSize(tileW, tileH, r);
		tileMaxWidth = size[0];
		tileMaxHeight = size[1];
		
        int stepX, stepY;
        int xProv, yProv;
        int altoAux, anchoAux;
        
        //Vamos a hacerlo en trozos de AxH
        numCols = 1+(int) (r.width) / tileMaxWidth;
        numRows = 1+(int) (r.height) / tileMaxHeight;
        
        srcPts = new double[numCols*numRows][8];
        tile = new Rectangle[numCols*numRows];
        
    	yProv = (int) r.y;
        for (stepY=0; stepY < numRows; stepY++) {
    		if ((yProv + tileMaxHeight) > r.getMaxY()) 
    			altoAux = (int) r.getMaxY() - yProv;
    		else
    			altoAux = tileMaxHeight;
        		        	
    		xProv = (int) r.x;
        	for (stepX=0; stepX < numCols; stepX++) {        		
	    		if ((xProv + tileMaxWidth) > r.getMaxX()) 
	    			anchoAux = (int) r.getMaxX() - xProv;
	    		else
	    			anchoAux = tileMaxWidth;
        		
        		//Rectangle newRect = new Rectangle(xProv, yProv, anchoAux, altoAux);
        		int tileCnt = stepY*numCols+stepX;
        		// Parte que dibuja
        		srcPts[tileCnt][0] = xProv;
        		srcPts[tileCnt][1] = yProv;
        		srcPts[tileCnt][2] = xProv + anchoAux+1;
        		srcPts[tileCnt][3] = yProv;
        		srcPts[tileCnt][4] = xProv + anchoAux+1;
        		srcPts[tileCnt][5] = yProv + altoAux+1;
        		srcPts[tileCnt][6] = xProv;
        		srcPts[tileCnt][7] = yProv + altoAux+1;
        		
        		tile[tileCnt] = new Rectangle(xProv, yProv, anchoAux+1, altoAux+1);
        		
				xProv += tileMaxWidth;	
        	}	        	
        	yProv += tileMaxHeight;
        }  		
	}
	
	/**
	 * Calcula el tamaño máximo de tile controlando que ningún tile tenga menos de MIN_SIZE
	 * pixeles
	 * @param tileW Ancho del tile
	 * @param tileH	Alto del tile
	 * @param r Rectangulo que define el area de la imagen
	 */
	public int[] calcMaxTileSize(int tileW, int tileH, Rectangle r){
		if(r.width < tileW || r.height < tileH){
			int[] sizeTiles = {tileW, tileH};
			return sizeTiles;
		}
			
        int wLastCol = 0;
        tileW += MIN_SIZE;
		do{
			tileW -= MIN_SIZE;
	        int numCols = (int) (r.width / tileW);
	        int w = 0;
	        for(int i = 0; i < numCols; i++)
	        	w += tileW;
	        wLastCol = r.width - w;
		}while(wLastCol < MIN_SIZE && tileW > (MIN_SIZE * 2));
        	
		int hLastRow = 0;
        tileH += MIN_SIZE;
		do{
			tileH -= MIN_SIZE;
	        int numRows = (int) (r.height / tileH);
	        int h = 0;
	        for(int i = 0; i < numRows; i++)
	        	h += tileH;
	        hLastRow = r.height - h;
		}while(hLastRow < MIN_SIZE && tileH > (MIN_SIZE * 2));
		
		tileMaxWidth = tileW;
		tileMaxHeight = tileH;
		int[] sizeTiles = {tileMaxWidth, tileMaxHeight};
		return sizeTiles;
	}
	
	public double [] getTilePts(int colNr, int rowNr) {
		return srcPts[rowNr*numCols+colNr];
	}
	
	public double [] getTilePts(int num) {
		return srcPts[num];
	}
	
	public Rectangle getTileSz(int colNr, int rowNr) {
		return tile[rowNr*numCols+colNr];
	}
	
	public Rectangle getTile(int num) {
		return tile[num];
	}
	
	/**
	 * @return Returns the numCols.
	 */
	public int getNumCols() {
		return numCols;
	}
	/**
	 * @return Returns the numRows.
	 */
	public int getNumRows() {
		return numRows;
	}
	
	public int getNumTiles() { return numRows*numCols; }
	/**
	 * @return Returns the tileHeight.
	 */
	public int getMaxTileHeight() {
		return tileMaxHeight;
	}
	/**
	 * @return Returns the tileWidth.
	 */
	public int getMaxTileWidth() {
		return tileMaxWidth;
	}
	
	ViewPort[] viewPortList = null;
	private void calcViewPort(ViewPort viewPort)throws NoninvertibleTransformException{
		viewPortList = new ViewPort[numCols*numRows];
		
		/*if(viewPort.getImageWidth() < width && viewPort.getImageHeight() < height){
			viewPortList[0] = viewPort;
			return;
		}*/
		
	    int vpCnt = 0;

	    double imgPxX = viewPort.getImageWidth();
	    double dWcX = viewPort.getAdjustedExtent().getWidth();
	    double tileWcW = (getTile(vpCnt).getSize().getWidth() * dWcX) / imgPxX;
	    
	    double imgPxY = viewPort.getImageHeight();
	    double dWcY = viewPort.getAdjustedExtent().getHeight();
	    double tileWcH = (getTile(vpCnt).getSize().getHeight() * dWcY) / imgPxY;
	   
	    viewPortList[0] = viewPort.cloneViewPort();
	    viewPortList[0].setImageSize(getTile(vpCnt).getSize());
	    viewPortList[0].setExtent(new Rectangle2D.Double(viewPort.getAdjustedExtent().getMinX(), viewPort.getAdjustedExtent().getMaxY() - tileWcH, tileWcW, tileWcH));
	    viewPortList[0].setAffineTransform(mat);

	    double wt = tileWcW;
	    double ht = tileWcH;
	    double xt = viewPort.getAdjustedExtent().getMinX();
	    double yt = viewPort.getAdjustedExtent().getMaxY() - tileWcH;
	    
	    for (int stepY=0; stepY < numRows; stepY++) {
	    	wt = tileWcW;
	    	xt = viewPort.getAdjustedExtent().getMinX();
	    	for (int stepX=0; stepX < numCols; stepX++) {
	    		vpCnt = stepY*numCols+stepX;
	    		if(vpCnt > 0){
	    			if(stepX > 0)
	    				xt += wt;
	    			if((xt + wt) > viewPort.getAdjustedExtent().getMaxX())
	    				wt = Math.abs(viewPort.getAdjustedExtent().getMaxX() - xt);

	    			viewPortList[vpCnt] = viewPort.cloneViewPort();
	    			viewPortList[vpCnt].setImageSize(getTile(vpCnt).getSize());
	    			viewPortList[vpCnt].setExtent(new Rectangle2D.Double(xt, yt, wt, ht));
	    			viewPortList[vpCnt].setAffineTransform(mat);

	    		}
	    		//System.out.println("ViewPort: "+vpCnt+" "+viewPortList[vpCnt].getAdjustedExtent()+" "+getTile(vpCnt).getSize());
	    	}
	    	if((yt - ht) < viewPort.getAdjustedExtent().getMinY()){
	    		ht = Math.abs(yt - viewPort.getAdjustedExtent().getMinY());
	    		yt = viewPort.getAdjustedExtent().getMinY();
	    	}else
	    		yt -= ht;
	    }
	}
	
	public ViewPort getTileViewPort(ViewPort viewPort, int tileNr) throws NoninvertibleTransformException {
		/*if(viewPortList == null)
			this.calcViewPort(viewPort);
		return viewPortList[tileNr];*/
		
		if(tile.length == 1)
			return viewPort;
		
		double [] dstPts = new double[8];
		double [] srcPts = getTilePts(tileNr);
		Rectangle tile = getTile(tileNr);
		//Rectangle newRect = new Rectangle((int)srcPts[0], (int)srcPts[1], tileSz[0], tileSz[1]);
		
		mat.inverseTransform(srcPts, 0, dstPts, 0, 4);
		double x = dstPts[0], w = dstPts[2] - dstPts[0];
		double y = dstPts[1], h = dstPts[5] - dstPts[3];
		if (w < 0) { x = dstPts[2]; w = dstPts[0] - dstPts[2]; }
		if (h < 0) { y = dstPts[5]; h = dstPts[3] - dstPts[5]; }
		Rectangle2D.Double rectCuadricula = new Rectangle2D.Double(x, y, w, h); 
		//Extent extent = new Extent(rectCuadricula);
		
		ViewPort vp = viewPort.cloneViewPort();
		vp.setImageSize(tile.getSize());
		//vp.setOffset(tile.getLocation());
		vp.setExtent(rectCuadricula);
		vp.setAffineTransform(mat);
		
		if (debug)
    		System.out.println("Tiling.print(): tile "+tileNr+" de "
    		        + getNumTiles() + 
    		        "\n, Extent = "+vp.getAdjustedExtent() + " tile: "
    		        + tile);

		return vp;
	}
	/**
	 * @return Returns the mat.
	 */
	public AffineTransform getAffineTransform() {
		return mat;
	}
	/**
	 * @param mat The mat to set.
	 */
	public void setAffineTransform(AffineTransform mat) {
		this.mat = mat;
	}
	/**
	 * @return Returns the debug.
	 */
	public boolean isDebug() {
		return debug;
	}
	/**
	 * @param debug The debug to set.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}


