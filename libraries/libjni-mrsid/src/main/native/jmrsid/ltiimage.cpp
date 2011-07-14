 /**********************************************************************
 * $Id: ltiimage.cpp 8460 2006-10-31 16:26:34Z nacho $
 *
 * Name:     ltiimage.c
 * Project:  JMRSID. Interfaz java to MrSID (Lizardtech).
 * Purpose:  
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
*/


#include <jni.h>
#include <malloc.h>
#include "es_gva_cit_jmrsid_LTIImage.h"
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
//						 		initialize
/******************************************************************************/


JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_JNIBase_initializeNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	int res = -1;
  	  	
    img = *(LTIImage **)&cPtr;
    // rgaitan: API has changed between previous version 
	// and this version.. so this method currently is 
	// really a no-op, but I don't want to change all
	// raster libraries :).
    if(img!=NULL)
    	res = 0;
	

    return res;
  		
  }
  
/******************************************************************************/
//						 		getMetadata
/******************************************************************************/
  
 JNIEXPORT jlong JNICALL Java_es_gva_cit_jmrsid_LTIImage_getMetadataNat
  (JNIEnv *env, jobject obj, jlong cPtr){
	
  	jlong jresult = 0 ;
  	LTIImageReader *img  = (LTIImageReader *) 0 ;
  	LTIMetadataDatabase *metadata=NULL;
  
  	img = *(LTIImageReader **)&cPtr;
  	if(img!=NULL){
	  	metadata = new LTIMetadataDatabase( img->getMetadata() );
	  	*(LTIMetadataDatabase **)&jresult = metadata;
  	}
  
  	return jresult;
	  	
  } 
  
/******************************************************************************/
//						 		getWidth
/******************************************************************************/


JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_JNIBase_getWidthNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	int res = -1;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL)
    	res = img->getWidth();	

    return res;
  		
  }
  
  
/******************************************************************************/
//						 		getHeight
/******************************************************************************/


JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_JNIBase_getHeightNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	int res = -1;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL)
    	res = img->getHeight();	

    return res;
  		
  }
  
    
/******************************************************************************/
//						 		getDimsAtMagWidth
/******************************************************************************/


JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_LTIImage_getDimsAtMagWidthNat
  (JNIEnv *env, jobject obj, jlong cPtr, jdouble mag){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	int res = -1;
  	int height;
  	double mg=mag;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL)
    	img->getDimsAtMag((double)mg,(lt_uint32 &)res,(lt_uint32 &)height);	

    return res;
  		
  }  
  
/******************************************************************************/
//						 		getDimsAtMagHeight
/******************************************************************************/


JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_LTIImage_getDimsAtMagHeightNat
  (JNIEnv *env, jobject obj, jlong cPtr, jdouble mag){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	int res = -1;
  	int width;
  	double mg=mag;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL)
    	img->getDimsAtMag((double)mg,(lt_uint32 &)width,(lt_uint32 &)res);	

    return res;
  		
  }
  
/******************************************************************************/
//						 		getStripHeight
/******************************************************************************/


JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_JNIBase_getStripHeightNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImageReader *img  = (LTIImageReader *) 0 ;
  	int res = -1;
   	  	
    img = *(LTIImageReader **)&cPtr;
    
    if(img!=NULL)
    	res = img->getStripHeight();	

    return res;
  		
  } 
  
/******************************************************************************/
//						 		getNumBands
/******************************************************************************/


JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_JNIBase_getNumBandsNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	int res = -1;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL)
    	res = img->getNumBands();	

    return res;
  		
  } 
  
/******************************************************************************/
//						 		getColorSpace
/******************************************************************************/


JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_JNIBase_getColorSpaceNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	int res = -1;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL)
    	res = img->getColorSpace();	

    return res;
  		
  } 
  
/******************************************************************************/
//						 		getDataType
/******************************************************************************/


JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_JNIBase_getDataTypeNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	int res = -1;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL)
    	res = img->getDataType();	

    return res;
  		
  }  
  
/******************************************************************************/
//						 		getMinMagnification
/******************************************************************************/


JNIEXPORT jdouble JNICALL Java_es_gva_cit_jmrsid_LTIImage_getMinMagnificationNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	double res = -1;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL)
    	res = img->getMinMagnification();	

    return (jdouble)res;
  		
  }
  
/******************************************************************************/
//						 		getMaxMagnification
/******************************************************************************/


JNIEXPORT jdouble JNICALL Java_es_gva_cit_jmrsid_LTIImage_getMaxMagnificationNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	double res = -1;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL)
    	res = img->getMaxMagnification();	

    return (jdouble)res;
  		
  }
  
/******************************************************************************/
//						 		getPixelProps
/******************************************************************************/


/*JNIEXPORT jdouble JNICALL Java_es_gva_cit_jmrsid_LTIImage_getPixelPropsNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
	jlong jresult = 0 ;*/
  	/*LTIImage *img  = (LTIImage *) 0 ;
  	
  
  	img = *(LTIImage **)&cPtr;
  	if(img!=NULL){
	  	const LTIPixel& pixelprops = img->getPixelProps();
	  	*(LTIPixel **)&jresult = pixelprops;
  	
  
  	return (jlong)pixelprops;
  	}*/
  	/*return jresult;
  		
  } */
  
/******************************************************************************/
//						 		getGeoCoord
/******************************************************************************/

JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_LTIImage_getGeoCoordNat
  (JNIEnv *env, jobject obj, jlong cPtr, jobject geocoord){
	
	LTIImage *img  = (LTIImage *) 0 ;
  	jclass clase;
  	jfieldID id_campo; 
   	  	
    img = *(LTIImage **)&cPtr;
    if(img!=NULL){
    	const LTIGeoCoord& geo = img->getGeoCoord();
    	
    	clase = env->GetObjectClass(geocoord);
    	id_campo = env->GetFieldID(clase, "xUL", "D");
    	env->SetDoubleField(geocoord, id_campo, geo.getX());
    	
    	id_campo = env->GetFieldID(clase, "yUL", "D");
    	env->SetDoubleField(geocoord, id_campo, geo.getY());
    	
    	id_campo = env->GetFieldID(clase, "xRes", "D");
    	env->SetDoubleField(geocoord, id_campo, geo.getXRes());
    	
    	id_campo = env->GetFieldID(clase, "yRes", "D");
    	env->SetDoubleField(geocoord, id_campo, geo.getYRes());
    	
    	id_campo = env->GetFieldID(clase, "xRot", "D");
    	env->SetDoubleField(geocoord, id_campo, geo.getXRot());
    	
    	id_campo = env->GetFieldID(clase, "yRot", "D");
    	env->SetDoubleField(geocoord, id_campo, geo.getYRot());
    	return 1;
    }
    
    return -1;
  }
  
/******************************************************************************/
//						 		getBackgroundPixel
/******************************************************************************/


JNIEXPORT jlong JNICALL Java_es_gva_cit_jmrsid_LTIImage_getBackgroundPixelNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	jlong jresult = 0 ;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL){
    	const LTIPixel *pixel=img->getBackgroundPixel();
    	LTIPixel p=LTIPixel(*pixel);
    	*(LTIPixel **)&jresult = &p;
    }
    
    return jresult;
  		
  }  
  
  
/******************************************************************************/
//						 		getNoData
/******************************************************************************/


JNIEXPORT jlong JNICALL Java_es_gva_cit_jmrsid_LTIImage_getNoDataPixelNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
    LTIImage *img  = (LTIImage *) 0 ;
  	jlong jresult = 0 ;
   	  	
    img = *(LTIImage **)&cPtr;
    
    if(img!=NULL){
    	const LTIPixel *pixel=img->getNoDataPixel();
    	LTIPixel p=LTIPixel(*pixel);
    	*(LTIPixel **)&jresult = &p;
    }
    
    return jresult;
  		
  } 
  
/******************************************************************************/
//						 		~LTIImage
/******************************************************************************/
  
JNIEXPORT void JNICALL Java_es_gva_cit_jmrsid_LTIImage_FreeLTIImageNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIImage *img = (LTIImage *) 0 ;
  	
  	img = *(LTIImage **)&cPtr;
  	if(img!=NULL){
		img->release();
  	}
  	
  } 