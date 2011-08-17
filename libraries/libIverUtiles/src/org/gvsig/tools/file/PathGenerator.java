package org.gvsig.tools.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Generator of path absolute or relative.
 * 
 * Vicente Caballero Navarro
 */
public class PathGenerator {
	private String basePath = null;
	private boolean isAbsolutePath = true;
	private static PathGenerator instance = null;
	private static String pathSeparator=File.separator;
	/**
	 * Return the singleton instance of this object.
	 */
	public static PathGenerator getInstance(){
		if (instance == null){
			instance=new PathGenerator();
		}
		return instance;
	}
	
	/**
	 * Return the path relative or absolute depends if the option 
	 * isAbsolutePath is true or false. 
	 * @param targetPath Absolute path of file
	 * @param pathSeparator separator path of system
	 * @return the path of file.
	 */
	 public String getURLPath(String targetPath) {
		 try {
			URL url=new URL(targetPath);
			File fileIn = new File(url.toURI());
			File file = getFile(getPath(fileIn.getAbsolutePath()));
			return file.toURI().toURL().toString();
		 } catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		 return targetPath;
		 
	 }
	
	/**
	 * Return the path relative or absolute depends if the option 
	 * isAbsolutePath is true or false. 
	 * @param targetPath Absolute path of file
	 * @param pathSeparator separator path of system
	 * @return the path of file.
	 */
	 public String getPath(String targetPath) {
		 if (isAbsolutePath || basePath==null){
			 return targetPath;
		 }
		 if (targetPath == null) {
			 return null;
		 }
		 boolean isDir = false;
		 {
		   File f = getFile(targetPath);
		   isDir = f.isDirectory();
		 }
		 //  We need the -1 argument to split to make sure we get a trailing 
		 //  "" token if the base ends in the path separator and is therefore
		 //  a directory. We require directory paths to end in the path
		 //  separator -- otherwise they are indistinguishable from files.
		 String[] base = basePath.split(Pattern.quote(pathSeparator), -1);
		 String[] target = targetPath.split(Pattern.quote(pathSeparator), 0);

		 //  First get all the common elements. Store them as a string,
		 //  and also count how many of them there are. 
		 String common = "";
		 int commonIndex = 0;
		 for (int i = 0; i < target.length && i < base.length; i++) {
		     if (target[i].equals(base[i])) {
		         common += target[i] + pathSeparator;
		         commonIndex++;
		     }
		     else break;
		 }

		 if (commonIndex == 0)
		 {
		     //  Whoops -- not even a single common path element. This most
		     //  likely indicates differing drive letters, like C: and D:. 
		     //  These paths cannot be relativized. Return the target path.
		     return targetPath;
		     //  This should never happen when all absolute paths
		     //  begin with / as in *nix. 
		 }

		 String relative = "";
		 if (base.length == commonIndex) {
		     //  Comment this out if you prefer that a relative path not start with ./
		     relative = "." + pathSeparator;
		 }
		 else {
		     int numDirsUp = base.length - commonIndex - (isDir?0:1); /* only subtract 1 if it  is a file. */
		     //  The number of directories we have to backtrack is the length of 
		     //  the base path MINUS the number of common path elements, minus
		     //  one because the last element in the path isn't a directory.
		     for (int i = 1; i <= (numDirsUp); i++) {
		         relative += ".." + pathSeparator;
		     }
		 }
		 //if we are comparing directories then we 
		 if (targetPath.length() > common.length()) {
		  //it's OK, it isn't a directory
		  relative += targetPath.substring(common.length());
		 }

		 return relative;
	 }
	
	 /**
	  * Set the base path of project (.gvp)
	  * @param path of .GVP
	  */
	public void setBasePath(String path){
		if(path!=null){
			basePath = getFile(path).getAbsolutePath();
		} else {
			basePath = null;
		}
	}

	/**
	 * Returns a file from a path.
	 * @param path relative path.
	 * @return
	 */
	private File getFile(String path){
		if (path==null)
			return null;
		File filePath;
		try {
			URI uri = new URI(path.replace(" ", "%20"));
			filePath = new File(uri);
		} catch (Exception e) {
			filePath=new File(path);
		}
		return filePath;
	}
	/**
	 * Returns absolute path from a relative.
	 * @param path relative path.
	 * @return
	 */
	public String getAbsoluteURLPath(String path){
		try {
			URL url=new URL(path);
			File fileIn=new File(url.toURI());
			File file = getFile(getAbsolutePath(fileIn.getAbsolutePath()));
			return file.toURI().toURL().toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
		
	}
	
	/**
	 * Returns absolute path from a relative.
	 * @param path relative path.
	 * @return
	 */
	public String getAbsolutePath(String path){
		if (path==null)
			return null;
		File filePath = getFile(path);
		if (isAbsolutePath && filePath.exists())
			return path;
		filePath=new File(basePath, path);
		if (filePath.exists())
			return filePath.getAbsolutePath();
		return path;
	}
	
	/**
	 * Set if the path of project works in absolute path or relative path.
	 * @param b true if is absolute path.
	 */
	public void setIsAbsolutePath(boolean b){
		isAbsolutePath=b;
	}
	
	
	public static void main(String[] args) {
		getInstance().setBasePath("C:\\Documents and Settings\\vcn\\Escritorio\\kk.gvp");
		String s=getInstance().getPath("C:\\CONSTRU.SHP");
		System.err.println("ruta resultado: "+ s);
	}
	
}
