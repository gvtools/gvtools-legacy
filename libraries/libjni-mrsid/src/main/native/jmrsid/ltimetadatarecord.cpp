/**********************************************************************
 * $Id: ltimetadatarecord.cpp 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     ltimetadatarecord.c
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
#include <string.h>
#include "es_gva_cit_jmrsid_LTIMetadataRecord.h"
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
//				 			getTagName
/******************************************************************************/

JNIEXPORT jstring JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_getTagNameNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIMetadataRecord *record  = (LTIMetadataRecord *) 0 ;
  	jstring res=NULL;
  	const char *tagname;
  	  	
    record = *(LTIMetadataRecord **)&cPtr;
    
    if(record!=NULL){
		tagname=record->getTagName();
		res = env->NewStringUTF(tagname); 
    }

    return res;
  	
  }
  
/******************************************************************************/
//				 			isScalar
/******************************************************************************/

  JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_isScalarNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIMetadataRecord *record  = (LTIMetadataRecord *) 0 ;
  	
  	record = *(LTIMetadataRecord **)&cPtr;
  	
  	if(record!=NULL){
  		if (record->isScalar())return 1;
  		else return 0;
  	}else return -1; 
  }


/******************************************************************************/
//				 			isVector
/******************************************************************************/

JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_isVectorNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIMetadataRecord *record  = (LTIMetadataRecord *) 0 ;
  	
  	record = *(LTIMetadataRecord **)&cPtr;
  	
  	if(record!=NULL){
  		if (record->isVector())return 1;
  		else return 0;
  	}else return -1;
  }

/******************************************************************************/
//				 			isArray
/******************************************************************************/

JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_isArrayNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIMetadataRecord *record  = (LTIMetadataRecord *) 0 ;
  	
  	record = *(LTIMetadataRecord **)&cPtr;
  	
  	if(record!=NULL){
  		if (record->isArray())return 1;
  		else return 0;
  	}else return -1;
  }

/******************************************************************************/
//				 			getDataType
/******************************************************************************/

JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_getDataTypeNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIMetadataRecord *record  = (LTIMetadataRecord *) 0 ;
  	
  	record = *(LTIMetadataRecord **)&cPtr;
  	
  	if(record!=NULL){
  		switch (record->getDataType())
   		{
   			case LTI_METADATA_DATATYPE_UINT8:
			case LTI_METADATA_DATATYPE_SINT8:
		    case LTI_METADATA_DATATYPE_UINT16:
	   		case LTI_METADATA_DATATYPE_SINT16:
		    case LTI_METADATA_DATATYPE_UINT32:
		    case LTI_METADATA_DATATYPE_SINT32:
		    case LTI_METADATA_DATATYPE_UINT64:
		    case LTI_METADATA_DATATYPE_SINT64:return 0;break;
		    case LTI_METADATA_DATATYPE_FLOAT32:
		    case LTI_METADATA_DATATYPE_FLOAT64:return 1;break;
    		case LTI_METADATA_DATATYPE_ASCII:return 2;break;
	   }
  	}
  	
  	return -1;
  }

/******************************************************************************/
//				 			getScalarData
/******************************************************************************/

JNIEXPORT jstring JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_getScalarDataNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIMetadataRecord *record  = (LTIMetadataRecord *) 0 ;
  	record = *(LTIMetadataRecord **)&cPtr;
  	
  	if(record!=NULL){
  		char dato[8192];
  		const void* data = record->getScalarData();
  		switch (record->getDataType())
	   		{
				case LTI_METADATA_DATATYPE_UINT8:
			      sprintf(dato, "%d", (lt_uint32)((lt_uint8*)data)[0]);
			      break;
			    case LTI_METADATA_DATATYPE_SINT8:
			      sprintf(dato, "%d", (lt_int32)((lt_int8*)data)[0]);
			      break;
			    case LTI_METADATA_DATATYPE_UINT16:
			      sprintf(dato, "%d", (lt_uint32)((lt_uint16*)data)[0]);
			      break;
			    case LTI_METADATA_DATATYPE_SINT16:
			      sprintf(dato, "%d", (lt_int32)((lt_int16*)data)[0]);
			      break;
			    case LTI_METADATA_DATATYPE_UINT32:
			      sprintf(dato, "%d", ((lt_uint32*)data)[0]);
			      break;
			    case LTI_METADATA_DATATYPE_SINT32:
			      sprintf(dato, "%d", ((lt_int32*)data)[0]);
			      break;
			    case LTI_METADATA_DATATYPE_UINT64:
			      sprintf(dato, "%I64u", ((lt_uint64*)data)[0]);
			      break;
			    case LTI_METADATA_DATATYPE_SINT64:
			      sprintf(dato, "%I64d", ((lt_int64*)data)[0]);
			      break;
			    case LTI_METADATA_DATATYPE_FLOAT32:
			      sprintf(dato, "%f", ((float*)data)[0]);
			      break;
			    case LTI_METADATA_DATATYPE_FLOAT64:
			      sprintf(dato, "%f", ((double*)data)[0]);
			      break;
			    case LTI_METADATA_DATATYPE_ASCII:
			      {
			      	
			         const char* p = ((const char**)data)[0];
			         if(strlen(p)>8192)
			         	strncpy(dato,p,8192);
			         else
			        	 sprintf(dato,"%s", p);
			      }
			      break;
	   		}
	   
	   return env->NewStringUTF(dato); 
	   
  		
  	}else return NULL;
  }




/******************************************************************************/
//				 			getVectorData
/******************************************************************************/

JNIEXPORT jobject JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_getVectorDataNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIMetadataRecord *record  = (LTIMetadataRecord *) 0 ;
   	char dato[255];
 	jclass clase;
 	jmethodID metodo;
 	jobject obj_stringarray;
 	jfieldID id_campo;
   	jobjectArray arr_string;
   	   	
  	record = *(LTIMetadataRecord **)&cPtr;
  	
  	if(record!=NULL){
  		lt_uint32 len=0;
    	const void* data = record->getVectorData(len);
    	
    	clase = env->FindClass ("es/gva/cit/jmrsid/StringArray");
	   	metodo = env->GetMethodID(clase, "reserva", "(I)V");
   		obj_stringarray = env->NewObject (clase,metodo,len);
   		id_campo = env->GetFieldID(clase, "array", "[Ljava/lang/String;");
   		arr_string =(jobjectArray)env->GetObjectField(obj_stringarray, id_campo);
   		
      	for (lt_uint32 i=0; i<len; i++)
  	    {
	  		switch (record->getDataType())
	   		{
				case LTI_METADATA_DATATYPE_UINT8:
			      sprintf(dato, "%d", (lt_uint32)((lt_uint8*)data)[i]);
			      break;
			    case LTI_METADATA_DATATYPE_SINT8:
			      sprintf(dato, "%d", (lt_int32)((lt_int8*)data)[i]);
			      break;
			    case LTI_METADATA_DATATYPE_UINT16:
			      sprintf(dato, "%d", (lt_uint32)((lt_uint16*)data)[i]);
			      break;
			    case LTI_METADATA_DATATYPE_SINT16:
			      sprintf(dato, "%d", (lt_int32)((lt_int16*)data)[i]);
			      break;
			    case LTI_METADATA_DATATYPE_UINT32:
			      sprintf(dato, "%d", ((lt_uint32*)data)[i]);
			      break;
			    case LTI_METADATA_DATATYPE_SINT32:
			      sprintf(dato, "%d", ((lt_int32*)data)[i]);
			      break;
			    case LTI_METADATA_DATATYPE_UINT64:
			      sprintf(dato, "%I64u", ((lt_uint64*)data)[i]);
			      break;
			    case LTI_METADATA_DATATYPE_SINT64:
			      sprintf(dato, "%I64d", ((lt_int64*)data)[i]);
			      break;
			    case LTI_METADATA_DATATYPE_FLOAT32:
			      sprintf(dato, "%f", ((float*)data)[i]);
			      break;
			    case LTI_METADATA_DATATYPE_FLOAT64:
			      sprintf(dato, "%f", ((double*)data)[i]);
			      break;
			    case LTI_METADATA_DATATYPE_ASCII:
			      {
			         const char* p = ((const char**)data)[i];
			         sprintf(dato,"%s", p);
			      }
			      break;
		   }

		   env->SetObjectArrayElement(arr_string,i,env->NewStringUTF(dato));
   	 	
    	}
    	
  	}
  	
  	return obj_stringarray;
  	
  }

/******************************************************************************/
//				 			getArrayData
/******************************************************************************/

JNIEXPORT jobject JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_getArrayDataNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIMetadataRecord *record  = (LTIMetadataRecord *) 0 ;
   	char dato[255];
 	jclass clase;
 	jmethodID metodo;
 	jobject obj_stringarray;
 	jfieldID id_campo;
   	jobjectArray arr_string;
   	   	
  	record = *(LTIMetadataRecord **)&cPtr;
  	
  	if(record!=NULL){
  		
  		lt_uint32 numDims=0;
      	const lt_uint32* dims=NULL;
        const void* data = record->getArrayData(numDims, dims);
      	lt_uint32 i=0;
      	lt_uint32 d=0;
              
    	
    	clase = env->FindClass ("es/gva/cit/jmrsid/StringArray");
	   	metodo = env->GetMethodID(clase, "reserva", "(I)V");
   		obj_stringarray = env->NewObject (clase,metodo,numDims*(*dims));
   		id_campo = env->GetFieldID(clase, "array", "[Ljava/lang/String;");
   		arr_string =(jobjectArray)env->GetObjectField(obj_stringarray, id_campo);
   		
      	for (d=0; d<numDims; d++)
      	{

         	lt_uint32 nd=0;
 			for (nd=0; nd<dims[d]; nd++)
         	{
		  		switch (record->getDataType())
		   		{
					case LTI_METADATA_DATATYPE_UINT8:
				      sprintf(dato, "%d", (lt_uint32)((lt_uint8*)data)[i]);
				      break;
				    case LTI_METADATA_DATATYPE_SINT8:
				      sprintf(dato, "%d", (lt_int32)((lt_int8*)data)[i]);
				      break;
				    case LTI_METADATA_DATATYPE_UINT16:
				      sprintf(dato, "%d", (lt_uint32)((lt_uint16*)data)[i]);
				      break;
				    case LTI_METADATA_DATATYPE_SINT16:
				      sprintf(dato, "%d", (lt_int32)((lt_int16*)data)[i]);
				      break;
				    case LTI_METADATA_DATATYPE_UINT32:
				      sprintf(dato, "%d", ((lt_uint32*)data)[i]);
				      break;
				    case LTI_METADATA_DATATYPE_SINT32:
				      sprintf(dato, "%d", ((lt_int32*)data)[i]);
				      break;
				    case LTI_METADATA_DATATYPE_UINT64:
				      sprintf(dato, "%I64u", ((lt_uint64*)data)[i]);
				      break;
				    case LTI_METADATA_DATATYPE_SINT64:
				      sprintf(dato, "%I64d", ((lt_int64*)data)[i]);
				      break;
				    case LTI_METADATA_DATATYPE_FLOAT32:
				      sprintf(dato, "%f", ((float*)data)[i]);
				      break;
				    case LTI_METADATA_DATATYPE_FLOAT64:
				      sprintf(dato, "%f", ((double*)data)[i]);
				      break;
				    case LTI_METADATA_DATATYPE_ASCII:
				      {
				         const char* p = ((const char**)data)[i];
				         sprintf(dato,"%s", p);
				      }
				      break;
			   }
	
			   env->SetObjectArrayElement(arr_string,i,env->NewStringUTF(dato));
	   	 	
	    	   ++i;
         	}
      	}
    	
  	}
  	
  	return obj_stringarray;
  }
  
/******************************************************************************/
//				 			getNumDims
/******************************************************************************/

  JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_getNumDimsNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	jint dims=-1;
  	LTIMetadataRecord *record  = (LTIMetadataRecord *) 0 ;
  	record = *(LTIMetadataRecord **)&cPtr;
  	if(record!=NULL){
  		dims = (jint)record->getNumDims();
  	}
  	return dims;
  }

/******************************************************************************/
//				 			getDims
/******************************************************************************/

JNIEXPORT jintArray JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_getDimsNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	jintArray longitudes=NULL;
  	LTIMetadataRecord *record  = (LTIMetadataRecord *) 0 ;
  	record = *(LTIMetadataRecord **)&cPtr;
  	if(record!=NULL){
  		const lt_uint32* dims = record->getDims();
  		const lt_uint32 numdims = record->getNumDims();
  		longitudes = env->NewIntArray((int)numdims);
  		env->SetIntArrayRegion( longitudes, 0, (int)numdims,(jint *)dims); 

  	}
  	return longitudes;
  }
  
  
/******************************************************************************/
//						 		~LTIMetadataRecord
/******************************************************************************/
  
JNIEXPORT void JNICALL Java_es_gva_cit_jmrsid_LTIMetadataRecord_FreeLTIMetadataRecordNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIMetadataRecord *record = (LTIMetadataRecord *) 0 ;
  	
  	record = *(LTIMetadataRecord **)&cPtr;
  	if(record!=NULL){
  		delete record;
  	}
  	
  } 