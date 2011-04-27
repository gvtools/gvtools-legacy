 /**********************************************************************
 * $Id: ltiimagestage.cpp 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     ltiimagestage.c
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
#include "es_gva_cit_jmrsid_LTIImageStage.h"
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
//					 				read
/******************************************************************************/


JNIEXPORT jint JNICALL Java_es_gva_cit_jmrsid_LTIImageStage_readNat
  (JNIEnv *env, jobject obj, jlong cPtr, jlong cPtrscene, jlong cPtr_scenebuffer,jlong cPtrbuffer, jobject objbuf){
  	
  	LTIScene *scene;
  	LTISceneBuffer *buffer;
  	LTIImageStage *img;
  	t_buffer *str_buffer=NULL;
  	jclass class_mrsidbuffer;
  	jfieldID id_b1,id_b2,id_b3,fid;
  	jbyteArray jvect_byte1,jvect_byte2,jvect_byte3;
  	int size;
  	
  	img = *(LTIImageStage **)&cPtr;
  	scene = *(LTIScene **)&cPtrscene;
  	buffer = *(LTISceneBuffer **)&cPtr_scenebuffer;
  	
  	//Obtenemos el puntero al buffer. La llamada al constructor de SceneBuffer fue con el flag a 1.
  	
  	if(cPtrbuffer!=-1){
  		str_buffer = *(t_buffer **)&cPtrbuffer;
  	}
  	
  	//Llamamos a read
  	
  	int res=img->read(*scene,*buffer);

	if(str_buffer!=NULL){
		
	  	//Obtenemos el tamaño
  	
  		class_mrsidbuffer = env->GetObjectClass(objbuf);
		fid = env->GetFieldID( class_mrsidbuffer, "size", "I");
  		size = env->GetIntField( objbuf, fid);
	  	
	  	//Cargamos los datos en el buffer si la llamada fue con flag a 1
  	  	
  		id_b1 = env->GetFieldID( class_mrsidbuffer, "buf1", "[B");
	  	jvect_byte1 = env->NewByteArray(size);
		env->SetByteArrayRegion(jvect_byte1, 0, size,(jbyte *)str_buffer->bufs[0]); 
	  	env->SetObjectField( objbuf, id_b1, jvect_byte1);
  	
  		id_b2 = env->GetFieldID( class_mrsidbuffer, "buf2", "[B");
  		jvect_byte2 = env->NewByteArray(size);
  		env->SetByteArrayRegion(jvect_byte2, 0, size,(jbyte *)str_buffer->bufs[1]); 
	  	env->SetObjectField( objbuf, id_b2, jvect_byte2);
  	
  		id_b3 = env->GetFieldID( class_mrsidbuffer, "buf3", "[B");
  		jvect_byte3 = env->NewByteArray(size);
  		env->SetByteArrayRegion(jvect_byte3, 0, size,(jbyte *)str_buffer->bufs[2]); 
	  	env->SetObjectField( objbuf, id_b3, jvect_byte3);
  		
  		//Esto lo haremos en el destructor de LTISceneBuffer
  		//delete(str_buffer->membuf);
  		//free(str_buffer);
  	}		
	  	return res;

  }
  
/******************************************************************************/
//						 		~LTIImageStage
/******************************************************************************/
  
JNIEXPORT void JNICALL Java_es_gva_cit_jmrsid_LTIImageStage_FreeLTIImageStageNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	LTIImageStage *stage = (LTIImageStage *) 0 ;
  	
  	stage = *(LTIImageStage **)&cPtr;
  	if(stage!=NULL){
  		delete stage;
  	}
  	
  } 