package org.gvsig.gpe.kml.utils;

import java.util.regex.Pattern;

public class StringUtils {
	//----------------------------------------------------------------------
	// PROBLEM WITH COMPATIBILITY OF "split()" WITH IBM J9 JAVA MICROEDITION
	//----------------------------------------------------------------------
	public static String[] splitString(String input, String sep) {
		return Pattern.compile(sep).split(input, 0);
	}

	public static String[] charSplit(String origin, String expresion){
		String ns[];
		String aux = origin;
		int indexFin;
		int indexStart=0;
		int i=0;
			//take the string in couples of words separates by :
		indexFin = origin.indexOf(expresion);
		if(indexFin!=-1){
			while (indexFin!=-1){
				aux = aux.substring(indexFin+1);
				indexFin = aux.indexOf(expresion);
				i++;
			}
			ns = new String[i+1];
			aux = origin;
			i=0;
			indexFin = aux.indexOf(expresion);
			while (indexFin!=-1){
				ns[i]= aux.substring(0,indexFin);
				indexStart=indexFin+1;
				aux = aux.substring(indexStart);
				indexFin = aux.indexOf(expresion);
				i++;
			}
			ns[i]=aux;	
		}
		else
		{
			ns = new String[1];
			ns[0] = origin;
		}
		return ns;
	}
	//----------------------------------
	// end of the split() compatibility
	//----------------------------------
	public static String replaceAllString(String original, String match_old,
			String match_new) {
		return Pattern.compile(match_old).matcher(original).replaceAll(
				match_new);
	}
}
