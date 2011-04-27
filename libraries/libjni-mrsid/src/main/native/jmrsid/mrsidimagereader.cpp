 /**********************************************************************
 * $Id: mrsidimagereader.cpp 8460 2006-10-31 16:26:34Z nacho $
 *
 * Name:     mrsid_interfaz.c
 * Project:  JMRSID. Interfaz java to MrSID (Lizardtech).
 * Purpose:  dataset's Basic Funcions.
 * Author:   Nacho Brodin, brodin_ign@gva.es
 *
 **********************************************************************/
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
* 
* [01] 01-Oct-2005 nbt MrSIDImageReaderArrayNat function to support name parameter in char array
*/


#include <jni.h>
#include <stdlib.h>
#include "es_gva_cit_jmrsid_MrSIDImageReader.h"
#include "es_gva_cit_jmrsid_JNIBase.h"

#include "lt_types.h"
#include "lt_base.h"
#include "lt_fileSpec.h"
#include "lti_geoCoord.h"
#include "lti_pixel.h"
#include "lti_navigator.h"
#include "lti_sceneBuffer.h"
#include "lti_metadataDatabase.h"
#include "lti_metadataRecord.h"
#include "lti_utils.h"
#include "MrSIDImageReader.h"
#include "J2KImageReader.h"
#include "lti_imageReader.h"
#include "lti_sceneBuffer.h"
#include "lti_scene.h"
//#include "cpl_conv.h"
#include "TIFFImageWriter.h"

LT_USE_NAMESPACE(LizardTech);

/******************************************************************************/
//						 Constructor MrSIDImageReader
/******************************************************************************/


JNIEXPORT jlong JNICALL Java_es_gva_cit_jmrsid_MrSIDImageReader_MrSIDImageReaderNat
  (JNIEnv *env, jobject obj, jstring pszF){
  	
  	const char *pszFilename;
  	LTIImageReader     *poImageReader;
  	jlong jresult = 0 ;
  	FILE *fich;
  	
  	
  	pszFilename = env->GetStringUTFChars(pszF, 0);

	fich=fopen( pszFilename, "r" );
	if( fich )
		fclose(fich);
	else
   	{
      	fclose(fich);
      	return -1;
   	}
  	
  	const LTFileSpec fileSpec(pszFilename);
  	poImageReader = new MrSIDImageReader( fileSpec );
  	*(LTIImageReader **)&jresult = poImageReader;
    env->ReleaseStringUTFChars(pszF, pszFilename);
  	
  
  	if(poImageReader==NULL)return -1; 
  	else return jresult; 
  		
  }
  
/******************************************************************************/
//						 Constructor MrSIDImageArrayReader
/******************************************************************************/


JNIEXPORT jlong JNICALL Java_es_gva_cit_jmrsid_MrSIDImageReader_MrSIDImageReaderArrayNat
  (JNIEnv *env, jobject obj, jbyteArray pszF){
  	
  	char *pszFilename;
  	LTIImageReader     *poImageReader;
  	jlong jresult = 0 ;
  	FILE *fich;
  	int longitud = 0;
  	
  	longitud = env->GetArrayLength(pszF); 
  	pszFilename = (char *)env->GetByteArrayElements(pszF, 0); 
  	pszFilename = (char *)realloc(pszFilename, longitud + 1);
	pszFilename[longitud] = '\0';
	  	
	fich=fopen( (char *)pszFilename, "r" );
	if( fich )
		fclose(fich);
	else
      	return -1;
   	
  	const LTFileSpec fileSpec(pszFilename);
  	poImageReader = new MrSIDImageReader( fileSpec );

  	*(LTIImageReader **)&jresult = poImageReader;
    
    env->ReleaseByteArrayElements(pszF, (jbyte *)pszFilename, 0);
    
  	if(poImageReader==NULL)
  		return -1; 
  	else 
  		return jresult; 
  		
  }
  
/******************************************************************************/
//						 		getNumLevels
/******************************************************************************/
  
JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_JNIBase_getNumLevelsNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	MrSIDImageReader *img  = (MrSIDImageReader *) 0 ;
  	int res = -1;
  	  	
    img = *(MrSIDImageReader **)&cPtr;
    
    if(img!=NULL)
		res = img->getNumLevels();

    return res;
    
  }
  
/******************************************************************************/
//						 		~MrSIDImageReader
/******************************************************************************/
  
JNIEXPORT void JNICALL Java_es_gva_cit_jmrsid_MrSIDImageReader_FreeMrSIDImageReaderNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	MrSIDImageReader *reader = (MrSIDImageReader *) 0 ;
  	
  	reader = *(MrSIDImageReader **)&cPtr;
  	if(reader!=NULL){
  		delete reader;
  	}
  	
  }
  

 
  
  
