 /**********************************************************************
 * $Id: ltipixel.cpp 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     ltipixel.c
 * Project:  JMRSID. Interfaz java to MrSID (Lizardtech).
 * Purpose:  
 * Author:   Nacho Brodin, brodin_ign@gva.es
 *
 **********************************************************************/
/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
*   Av. Blasco Ib��ez, 50
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


#include <jni.h>
#include "es_gva_cit_jmrsid_LTIPixel.h"
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
//					 		Constructor LTIPixel
/******************************************************************************/


JNIEXPORT jlong JNICALL Java_es_gva_cit_jmrsid_LTIPixel_LTIPixelNat
  (JNIEnv *env, jobject obj, jint eColorSpace, jint nBands, jint eSampleType){
  	
	LTIPixel *poPixel;
	jlong jresult = 0 ;
	
	poPixel = new LTIPixel((LTIColorSpace)eColorSpace, (lt_uint16)nBands, (LTIDataType)eSampleType );
  		
  	*(LTIPixel **)&jresult = poPixel;
  	
  	return jresult;
  }
  
/******************************************************************************/
//						 		~LTIPixel
/******************************************************************************/
  
JNIEXPORT void JNICALL Java_es_gva_cit_jmrsid_LTIPixel_FreeLTIPixelNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIPixel *pixel = (LTIPixel *) 0 ;
  	
  	pixel = *(LTIPixel **)&cPtr;
  	if(pixel!=NULL){
  		delete pixel;
  	}
  	
  }
