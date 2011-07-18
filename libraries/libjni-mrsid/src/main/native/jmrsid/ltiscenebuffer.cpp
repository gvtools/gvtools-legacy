 /**********************************************************************
 * $Id: ltiscenebuffer.cpp 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     ltiscenebuffer.c
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
#include "es_gva_cit_jmrsid_LTISceneBuffer.h"
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
#include "../include/jmrsid.h"

LT_USE_NAMESPACE(LizardTech);



/******************************************************************************/
//				 		Constructor LTISceneBuffer
/******************************************************************************/


JNIEXPORT jlong JNICALL Java_es_gva_cit_jmrsid_LTISceneBuffer_LTISceneBufferNat
  (JNIEnv *env, jobject obj, jlong cPtr, jint tamx, jint tamy, jint flag){
  	
  	LTISceneBuffer *buffer=NULL;
  	LTIPixel *pixel  = (LTIPixel *) 0 ;
  	jclass class_mrsidbuffer;
  	jfieldID id_ptrbuffer;
  	t_buffer *datos_ventana = (t_buffer*)malloc(sizeof(t_buffer));
	jlong jbuf = 0 ;
	jlong jstructbuf = 0 ;
	
  	
  	pixel = *(LTIPixel **)&cPtr;
  	
  	//Si el flag es 1 necesitamos un buffer.
  	
  	if(pixel!=NULL){
	  	if(flag>=1){
	  		
	  		//Creamos el buffer
	  	
  			datos_ventana->siz = tamx * tamy;
	    	datos_ventana->membuf = new lt_uint8[datos_ventana->siz*3];
	   		datos_ventana->bufs[0] = datos_ventana->membuf;
	   		datos_ventana->bufs[1] = datos_ventana->membuf+datos_ventana->siz;
	   		datos_ventana->bufs[2] = datos_ventana->membuf+datos_ventana->siz*2;
  	  	
  	  		//Obtenemos el id del campo que tiene el buffer de la clase LTISceneBuffer de java
  	  	
  			class_mrsidbuffer = env->GetObjectClass(obj);
  			id_ptrbuffer = env->GetFieldID( class_mrsidbuffer, "cPtrbuffer", "J");

  			
  			//Llamamos al constructor pasandole el buffer creado
  			
  			buffer=new LTISceneBuffer(*pixel,tamx,tamy,datos_ventana->bufs);
  			
  			//Obtenemos los punteros a objeto buffer y a la estructura con el buffer de memoria
  			
  			*(t_buffer **)&jstructbuf = datos_ventana;
  			
  			//Guardamos el id del buffer en la parte java
  			
  			env->SetLongField(obj, id_ptrbuffer, jstructbuf); 
  			
  		}else
		  	buffer=new LTISceneBuffer(*pixel,tamx,tamy,NULL);	
	  	
  	
		*(LTISceneBuffer **)&jbuf = buffer;
  	
	
  	}else return -1;
  	
  	
  	
  	return jbuf;
  }
  

/******************************************************************************/
//						 		LTISceneBuffer
/******************************************************************************/

JNIEXPORT jlong JNICALL Java_es_gva_cit_jmrsid_LTISceneBuffer_LTISceneBuffer1Nat__JIIIIIII
  (JNIEnv *env, jobject obj, jlong cPtr, jint totalNumCols, jint totalNumRows, jint colOffset, jint rowOffset, jint windowNumCols, jint windowNumRows, jint flag){
  	
  	LTISceneBuffer *buffer=NULL;
  	LTIPixel *pixel  = (LTIPixel *) 0 ;
  	jclass class_mrsidbuffer;
  	jfieldID id_ptrbuffer;
  	t_buffer *datos_ventana = (t_buffer*)malloc(sizeof(t_buffer));
	jlong jbuf = 0 ;
	jlong jstructbuf = 0 ;
	
  	
  	pixel = *(LTIPixel **)&cPtr;
  	
  	//Si el flag es 1 necesitamos un buffer.
  	
  	if(pixel!=NULL){
	  	if(flag>=1){
	  		
	  		//Creamos el buffer
	  	
  			datos_ventana->siz = totalNumCols * totalNumRows;
	    	datos_ventana->membuf = new lt_uint8[datos_ventana->siz*3];
	   		datos_ventana->bufs[0] = datos_ventana->membuf;
	   		datos_ventana->bufs[1] = datos_ventana->membuf+datos_ventana->siz;
	   		datos_ventana->bufs[2] = datos_ventana->membuf+datos_ventana->siz*2;
  	  	
  	  		//Obtenemos el id del campo que tiene el buffer de la clase LTISceneBuffer de java
  	  	
  			class_mrsidbuffer = env->GetObjectClass(obj);
  			id_ptrbuffer = env->GetFieldID( class_mrsidbuffer, "cPtrbuffer", "J");

  			
  			//Llamamos al constructor pasandole el buffer creado
  			
  			buffer=new LTISceneBuffer(*pixel, totalNumCols, totalNumRows, colOffset, rowOffset, windowNumCols, windowNumRows, datos_ventana->bufs);
  			
  			//Obtenemos los punteros a objeto buffer y a la estructura con el buffer de memoria
  			
  			*(t_buffer **)&jstructbuf = datos_ventana;
  			
  			//Guardamos el id del buffer en la parte java
  			
  			env->SetLongField(obj, id_ptrbuffer, jstructbuf); 
  		}else
		  	buffer=new LTISceneBuffer(*pixel, totalNumCols, totalNumRows, colOffset, rowOffset, windowNumCols, windowNumRows, NULL);	
	  	
  	
		*(LTISceneBuffer **)&jbuf = buffer;
  	
	
  	}else return -1;
  	
  	
  	
  	return jbuf;
  }
  
/******************************************************************************/
//						 		~LTISceneBuffer
/******************************************************************************/
  
JNIEXPORT void JNICALL Java_es_gva_cit_jmrsid_LTISceneBuffer_FreeLTISceneBufferNat
  (JNIEnv *env, jobject obj, jlong cPtr_LTISceneBuffer, jlong cPtr_tbuffer){
  	
  	LTISceneBuffer *scene = (LTISceneBuffer *) 0 ;
  	t_buffer *datos_ventana;
  	
  	scene = *(LTISceneBuffer **)&cPtr_LTISceneBuffer;
  	datos_ventana = *(t_buffer **)&cPtr_tbuffer;
  	
  	//Nos cepillamos el buffer y el objeto scenebuffer para que quede todo aseado
  	
  	if(datos_ventana!=NULL){
	  	if(datos_ventana->membuf!=NULL)delete datos_ventana->membuf;
  		free(datos_ventana);
  	}
  	if(scene!=NULL){
  		delete scene;
  	}
  	
  }
