
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

import java.awt.FileDialog;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.iver.cit.gvsig.fmap.MapControl;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.raster.JimiRasterImage;

/**
 * @author VCN
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

public class Export
{
	private MapControl m_MapControl;
	private FileDialog fd;
	private Image tempImage;
	Export(MapControl fm)
	{
		m_MapControl=fm;
		JFileChooser tempChooser = new JFileChooser();

		tempChooser.addChoosableFileFilter(new FileFilter() {
				public boolean accept(File f) {
					String extension = "";
					if (f.isDirectory()) return true;
					int i = f.getName().lastIndexOf('.');
					if (i > 0) extension = f.getName().substring(i + 1).toLowerCase();
					if (extension.equals("bmp")) return true;
					else return false;
				}
				public String getDescription() {
					return "Achivos BMP (*.bmp)";
				}
			});
			
	tempChooser.addChoosableFileFilter(new FileFilter() {
			   public boolean accept(File f) {
				   String extension = "";
				   if (f.isDirectory()) return true;
				   int i = f.getName().lastIndexOf('.');
				   if (i > 0) extension = f.getName().substring(i + 1).toLowerCase();
				   if (extension.equals("jpg") || extension.equals("jpeg")) return true;
				   else return false;
			   }
			   public String getDescription() {
				   return "Archivos JPEG (*.jpg; *.jpeg)";
			   }
		   });
		   
	tempChooser.addChoosableFileFilter(new FileFilter() {
				public boolean accept(File f) {
					String extension = "";
					if (f.isDirectory()) return true;
					int i = f.getName().lastIndexOf('.');
					if (i > 0) extension = f.getName().substring(i + 1).toLowerCase();
					if (extension.equals("png")) return true;
					else return false;
				}
				public String getDescription() {
					return "Archivos PNG (*.png)";
				}
			});

	tempChooser.showSaveDialog(m_MapControl);
	
	File	tempFile = tempChooser.getSelectedFile();
	if (tempFile != null){
		try{
		
				tempImage = null;
 				tempImage = m_MapControl.getImage();
				JimiRasterImage jrf = Jimi.createRasterImage(tempImage.getSource());
				String tempName = tempFile.getName().toUpperCase().trim();
				String extension="";
		
				if ((tempName.endsWith(".JPG")) || (tempName.endsWith(".JPEG")) || (tempChooser.getFileFilter().getDescription().toString()=="Archivos JPEG (*.jpg; *.jpeg)")){
					if (!(tempName.endsWith(".JPG")) || (tempName.endsWith(".JPEG"))) extension=".jpg";	
					
					File f1=new File(tempFile.getParent(), tempFile.getName() + extension);
					tempFile.renameTo(f1);
					System.out.println(tempFile.getName() + ".jpg");
					FileOutputStream fout = new FileOutputStream(f1);
					Jimi.putImage("image/jpg",jrf,fout);
					fout.close();
				}
				else if ((tempName.endsWith(".PNG")) || (tempChooser.getFileFilter().getDescription().toString()=="Archivos PNG (*.png)")){
					if (!(tempName.endsWith(".PNG"))) extension=".png";
					File f2=new File(tempFile.getParent(), tempFile.getName() + extension);
					tempFile.renameTo(f2);
					System.out.println(tempFile.getName() + ".png");
					FileOutputStream fout = new FileOutputStream(f2);
					Jimi.putImage("image/png",jrf,fout);
					fout.close();
				}
				else if ((tempName.endsWith(".BMP")) || (tempChooser.getFileFilter().getDescription().toString()=="Achivos BMP (*.bmp)")){
					if (!(tempName.endsWith(".BMP"))) extension=".bmp";
					File f3=new File(tempFile.getParent(), tempFile.getName() + extension);
					tempFile.renameTo(f3);
					System.out.println(tempFile.getName() + ".bmp");
					FileOutputStream fout = new FileOutputStream(f3);
					Jimi.putImage("image/bmp",jrf,fout);
					fout.close();
				}
		}catch (Exception e){System.out.println("Exception   "+e);}
	}
  }
//>>>>>>> 1.2
}
