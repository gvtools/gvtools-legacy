/**********************************************************************
 * $Id: ncsecwcompressclient_wrapper.c 4274 2006-03-06 07:32:00Z nacho $
 *
 * Name:     bsb_interfaz.c
 * Project:  JGDAL. Interface java to gdal (Frank Warmerdam).
 * Purpose:  Interface functions to manage bsb files. This include gdal
 * 			code but it doesn't use the gdal API.
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
#ifdef __APPLE__
#include <stdlib.h>
#else
#include <malloc.h>
#endif
#include "NCSECWCompressClient.h"

typedef struct ReadInfo {
	//NCSFileView 	*pNCSFileView;
	UINT8 			**ppInputBandBufferArray;
	UINT32 			nPercentComplete;
	jclass			clase;
	jobject			*jclient;
	jobject			*jreadcall;
	JNIEnv 			*env;
	char 			pErrorBuffer[1024];
} ReadInfo;

static BOOLEAN 	callBackOn = TRUE;

/******************************************************************************/
//								ReadCallback
/******************************************************************************/


static BOOLEAN ReadCallback(NCSEcwCompressClient *pClient,
							UINT32 nNextLine,
							IEEE4 **ppOutputBandBufferArray)
{
	ReadInfo *pReadInfo = (ReadInfo *)pClient->pClientData;
	UINT32 nBand;
	jfieldID fid;
	jbyteArray jarray;
	JNIEnv *env=pReadInfo->env;
	int longitud;
	jclass clase_client;
	jclass clase_readcall;
	jmethodID metodo;

    if(callBackOn){
		clase_client = (*env)->GetObjectClass(env, *(pReadInfo->jclient));
		clase_readcall = (*env)->GetObjectClass(env, *(pReadInfo->jreadcall));
		
		//Ponemos el valor de la banda un número de línea en el objeto cliente		
	
	  	fid = (*env)->GetFieldID(env, clase_readcall, "nNextLine", "I");
	  	(*env)->SetIntField(env, *(pReadInfo->jreadcall), fid, nNextLine); 
		   	
	   	//Ejecutamos la función de java que carga el buffer
			
		metodo = (*env)->GetMethodID(env, clase_readcall, "loadBuffer", "()V");
		(*env)->CallVoidMethod(env, *(pReadInfo->jreadcall), metodo);
		
		//Obtenemos el array de bytes desde java
		
		fid = (*env)->GetFieldID(env, clase_client, "buffer", "[B");
	    jarray =(jbyteArray)(*env)->GetObjectField(env, *(pReadInfo->jclient), fid);  
	    longitud = (*env)->GetArrayLength(env, jarray);
	    
	    (*env)->GetByteArrayRegion(env,jarray,0,longitud,(jbyte *)pReadInfo->ppInputBandBufferArray[0]);
	        
		for(nBand = 0; nBand < pClient->nInputBands; nBand++) {
			UINT32 nCell;		
			UINT8 *pInputValue = pReadInfo->ppInputBandBufferArray[nBand];
			IEEE4 *pOutputValue = ppOutputBandBufferArray[nBand];
	    	
			// Compression needs input to be IEEE4
			for (nCell = 0; nCell < pClient->nInOutSizeX; nCell++) {
				*pOutputValue++ = (IEEE4)*pInputValue++;
			}
		
		}

		return(TRUE);	
    }else {
	callBackOn = TRUE;
    	return(FALSE);
    }
}

/******************************************************************************/
//								StatusCallback
/******************************************************************************/
//Asigna el tanto por cien de imagen que lleva comprimido en una variable de java
//Ejecuta la función updatePercent de java despues de actualizar la varible.

static void StatusCallback(NCSEcwCompressClient *pClient,
						   UINT32 nCurrentLine)
{
	ReadInfo 	*pReadInfo = (ReadInfo *)pClient->pClientData;
	UINT32 		nPercentComplete = (nCurrentLine * 100) / (pClient->nInOutSizeY - 1);
	jclass 		clase_client;
	jclass 		clase_readcall;
	JNIEnv 		*env=pReadInfo->env;
	jfieldID 	fid;
	jmethodID 	metodo;
	
	clase_client = (*env)->GetObjectClass(env, *(pReadInfo->jclient));
	clase_readcall = (*env)->GetObjectClass(env, *(pReadInfo->jreadcall));
	
	if (nPercentComplete != pReadInfo->nPercentComplete) {
		fid = (*env)->GetFieldID(env, clase_client, "porcentaje", "I");
	  	(*env)->SetIntField(env, *(pReadInfo->jclient), fid, nPercentComplete);
  		metodo = (*env)->GetMethodID(env, clase_readcall, "updatePercent", "()V");
		(*env)->CallVoidMethod(env, *(pReadInfo->jreadcall), metodo); 
		pReadInfo->nPercentComplete = nPercentComplete;
	}
}

/******************************************************************************/
//								NCSEcwCompressClient
/******************************************************************************/

JNIEXPORT jlong JNICALL Java_es_gva_cit_jecwcompress_NCSEcwCompressClient_NCSEcwCompressClientNat
  (JNIEnv *env, jobject obj){
  	
  	NCSEcwCompressClient 	*pClient;
  	jlong 					jresult = 0 ;
  	
  	if(pClient = NCSEcwCompressAllocClient()){
  		*(NCSEcwCompressClient **)&jresult = pClient;
  		return jresult;
  	}else
  		return -1;
  	
  }

/******************************************************************************/
//								NCSEcwCompressOpen
/******************************************************************************/

JNIEXPORT jint JNICALL Java_es_gva_cit_jecwcompress_NCSEcwCompressClient_NCSEcwCompressOpenNat
  (JNIEnv *env, jobject obj, jlong cPtr, jboolean bCalculateSizesOnly){
  	
  	NCSEcwCompressClient 	*pClient = (NCSEcwCompressClient *) 0 ;
  	NCSError 				eError;
  	jfieldID 				fid;
    jobject					obj_str;
    const char				*str;
    ReadInfo 				*compress_readInfo;
    jmethodID 				metodo;

    UINT8 *pReadBuffer,nBand;
    
  	pClient = *(NCSEcwCompressClient **)&cPtr;
  	if(pClient!=NULL){
  		//Asignamos los valores de los campos de NCSEcwCompress
  		
  		jclass clase = (*env)->GetObjectClass(env, obj);
  		  		
  		fid = (*env)->GetFieldID(env, clase, "inputFilename", "Ljava/lang/String;");
  		obj_str = (*env)->GetObjectField(env, obj, fid);
  		if(obj_str!=NULL){
			str = (*env)->GetStringUTFChars(env,obj_str,0);
	  		strcpy(pClient->szInputFilename,str);
	  		str=NULL;
	  		(*env)->ReleaseStringUTFChars(env, obj_str, str);
	  		//printf("inputFilename=%s\n",pClient->szInputFilename);
  		}
  		
  		fid = (*env)->GetFieldID(env, clase, "outputFilename", "Ljava/lang/String;");
  		obj_str = (*env)->GetObjectField(env, obj, fid);
  		if(obj_str!=NULL){
			str = (*env)->GetStringUTFChars(env,obj_str,0);
	  		strcpy(pClient->szOutputFilename,str);
	  		str=NULL;
	  		(*env)->ReleaseStringUTFChars(env, obj_str, str);
	  		//printf("outputFilename=%s\n",pClient->szOutputFilename);
  		}
  		  		
  		fid = (*env)->GetFieldID(env, clase, "targetCompression", "D");
  		pClient->fTargetCompression = (IEEE4)(*env)->GetDoubleField(env, obj, fid);
  		//printf("targetCompression=%f\n",pClient->fTargetCompression);
  		
  		fid = (*env)->GetFieldID(env, clase, "eCompressFormat", "I");
  		pClient->eCompressFormat = (*env)->GetIntField(env, obj, fid);
  		//printf("eCompressFormat=%d\n",pClient->eCompressFormat);
  		  		
  		fid = (*env)->GetFieldID(env, clase, "eCompressHint", "I");
  		pClient->eCompressHint = (*env)->GetIntField(env, obj, fid);
  		//printf("eCompressHint=%d\n",pClient->eCompressHint);
  		
  		fid = (*env)->GetFieldID(env, clase, "nBlockSizeX", "I");
  		pClient->nBlockSizeX = (*env)->GetIntField(env, obj, fid);
  		//printf("nBlockSizeX=%d\n",pClient->nBlockSizeX);
  		
  		fid = (*env)->GetFieldID(env, clase, "nBlockSizeY", "I");
  		pClient->nBlockSizeY = (*env)->GetIntField(env, obj, fid);
  		//printf("nBlockSizeY=%d\n",pClient->nBlockSizeY);
  		
  		fid = (*env)->GetFieldID(env, clase, "nInOutSizeX", "I");
  		pClient->nInOutSizeX = (*env)->GetIntField(env, obj, fid);
  		//printf("nInOutSizeX=%d\n",pClient->nInOutSizeX);
  		
  		fid = (*env)->GetFieldID(env, clase, "nInOutSizeY", "I");
  		pClient->nInOutSizeY = (*env)->GetIntField(env, obj, fid);
  		//printf("nInOutSizeY=%d\n",pClient->nInOutSizeY);
  		
  		fid = (*env)->GetFieldID(env, clase, "nInputBands", "I");
  		pClient->nInputBands = (*env)->GetIntField(env, obj, fid);
  		//printf("nInputBands=%d\n",pClient->nInputBands);
  		
  		fid = (*env)->GetFieldID(env, clase, "nOutputBands", "I");
  		pClient->nOutputBands = (*env)->GetIntField(env, obj, fid);
		//printf("nOutputBands=%d\n",pClient->nOutputBands);
		
  		fid = (*env)->GetFieldID(env, clase, "nInputSize", "J");
  		pClient->nInputSize = (*env)->GetLongField(env, obj, fid);
  		//printf("nInputSize=%ld\n",pClient->nInputSize);
  		
  		fid = (*env)->GetFieldID(env, clase, "fCellIncrementX", "D");
  		pClient->fCellIncrementX = (*env)->GetDoubleField(env, obj, fid);
  		//printf("fCellIncrementX=%f\n",pClient->fCellIncrementX);
  		
  		fid = (*env)->GetFieldID(env, clase, "fCellIncrementY", "D");
  		pClient->fCellIncrementY = (*env)->GetDoubleField(env, obj, fid);
  		//printf("fCellIncrementY=%f\n",pClient->fCellIncrementY);
  		
  		fid = (*env)->GetFieldID(env, clase, "fOriginX", "D");
  		pClient->fOriginX = (*env)->GetDoubleField(env, obj, fid);
  		//printf("fOriginX=%f\n",pClient->fOriginX);
  		
  		fid = (*env)->GetFieldID(env, clase, "fOriginY", "D");
  		pClient->fOriginY = (*env)->GetDoubleField(env, obj, fid);
  		//printf("fOriginY=%f\n",pClient->fOriginY);
  		
  		fid = (*env)->GetFieldID(env, clase, "eCellSizeUnits", "I");
  		pClient->fActualCompression = (IEEE4)(*env)->GetIntField(env, obj, fid);
  		//printf("eCellSizeUnits=%d\n",pClient->fActualCompression);
  		
  		fid = (*env)->GetFieldID(env, clase, "szDatum", "Ljava/lang/String;");
  		obj_str = (*env)->GetObjectField(env, obj, fid);
  		if(obj_str!=NULL){
			str = (*env)->GetStringUTFChars(env,obj_str,0);
	  		strcpy(pClient->szDatum,str);
	  		str=NULL;
	  		(*env)->ReleaseStringUTFChars(env, obj_str, str);
	  		//printf("szDatum=%s\n",pClient->szDatum);
  		}
  		
  		fid = (*env)->GetFieldID(env, clase, "szProjection", "Ljava/lang/String;");
  		obj_str = (*env)->GetObjectField(env, obj, fid);
  		if(obj_str!=NULL){
			str = (*env)->GetStringUTFChars(env,obj_str,0);
	  		strcpy(pClient->szProjection,str);
	  		str=NULL;
	  		(*env)->ReleaseStringUTFChars(env, obj_str, str);
	  		//printf("szProjection=%s\n",pClient->szProjection);
  		}
  		
  		fid = (*env)->GetFieldID(env, clase, "fActualCompression", "D");
  		pClient->fActualCompression = (IEEE4)(*env)->GetDoubleField(env, obj, fid);
  		//printf("fActualCompression=%f\n",pClient->fActualCompression);
  		
  		fid = (*env)->GetFieldID(env, clase, "fCompressionSeconds", "D");
  		pClient->fCompressionSeconds = (*env)->GetDoubleField(env, obj, fid);
  		//printf("fCompressionSeconds=%f\n",pClient->fCompressionSeconds);
  		
  		fid = (*env)->GetFieldID(env, clase, "fCompressionMBSec", "D");
  		pClient->fCompressionMBSec = (*env)->GetDoubleField(env, obj, fid);
  		//printf("fCompressionMBSec=%f\n",pClient->fCompressionMBSec);
  		
  		fid = (*env)->GetFieldID(env, clase, "nOutputSize", "J");
  		pClient->nOutputSize = (*env)->GetLongField(env, obj, fid);
  		//printf("nOutputSize=%ld\n",pClient->nOutputSize);
  		
  		pClient->pReadCallback = ReadCallback;
		pClient->pStatusCallback = StatusCallback;
				
		//Inicializar el buffer que tendrá una línea de entrada x el núm de bandas
		
		metodo = (*env)->GetMethodID(env, clase, "initialize", "()V");
		(*env)->CallIntMethod(env,obj,metodo);
			
		compress_readInfo = (ReadInfo *)malloc(sizeof(ReadInfo));
		
		pReadBuffer = (UINT8 *)malloc(sizeof(UINT8) *
								  pClient->nInOutSizeX *
								  pClient->nInputBands);
		compress_readInfo->ppInputBandBufferArray = (UINT8 **)malloc(sizeof(UINT8 *) *
								   pClient->nInputBands);
		for (nBand = 0; nBand < pClient->nInputBands; nBand++) {
			compress_readInfo->ppInputBandBufferArray[nBand] = pReadBuffer + 
			(nBand * pClient->nInOutSizeX * sizeof(UINT8));
		}
								  
		if (compress_readInfo->ppInputBandBufferArray == NULL) return 46;
		compress_readInfo->nPercentComplete = 0;
				
		pClient->pClientData = (void *)compress_readInfo;
		
	  	eError = NCSEcwCompressOpen(pClient, ((bCalculateSizesOnly==0)? FALSE : TRUE));
	  	
		return eError;
		
  	}
  	return -1;
  }
  
/******************************************************************************/
//								NCSEcwCompress
/******************************************************************************/
  
JNIEXPORT jint JNICALL Java_es_gva_cit_jecwcompress_NCSEcwCompressClient_NCSEcwCompressNat
  (JNIEnv *env, jobject obj, jlong cPtr, jobject obj_read){
  	
  	NCSEcwCompressClient 	*pClient = (NCSEcwCompressClient *) 0 ;
  	ReadInfo 				*compress_readInfo = (ReadInfo *) 0 ;
  	NCSError 				eError;
   
	pClient = *(NCSEcwCompressClient **)&cPtr;
	compress_readInfo = (ReadInfo *)pClient->pClientData;
	compress_readInfo->jclient=&obj;
	compress_readInfo->jreadcall=&obj_read;
	compress_readInfo->env=env;	
	
	eError = NCSEcwCompress(pClient);
	return eError;  	
  }
  
/******************************************************************************/
//								NCSEcwCompressClose
/******************************************************************************/
  
JNIEXPORT jint JNICALL Java_es_gva_cit_jecwcompress_NCSEcwCompressClient_NCSEcwCompressCloseNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	NCSEcwCompressClient 	*pClient = (NCSEcwCompressClient *) 0 ;
  	NCSError 				eError;
    	
	pClient = *(NCSEcwCompressClient **)&cPtr;	
	eError = NCSEcwCompressClose(pClient);
	return eError;  	
  }  
  
/******************************************************************************/
//								NCSEcwCompressCancel
/******************************************************************************/
  
JNIEXPORT void JNICALL Java_es_gva_cit_jecwcompress_NCSEcwCompressClient_NCSEcwCompressCancelNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	callBackOn = FALSE;
  }
  
/******************************************************************************/
//								finalize
/******************************************************************************/
  
JNIEXPORT void JNICALL Java_es_gva_cit_jecwcompress_NCSEcwCompressClient_finalizeNat
  (JNIEnv *env, jobject obj, jlong cPtr){
  	
  	ReadInfo 				*compress_readInfo = (ReadInfo *) 0 ;
  	NCSEcwCompressClient 	*pClient = (NCSEcwCompressClient *) 0 ;
    int 					nBand;	
    
	pClient = *(NCSEcwCompressClient **)&cPtr;	
   	
  	//Liberamos la memoria
  	
  	compress_readInfo = pClient->pClientData;
  	for (nBand = 0; nBand < pClient->nInputBands; nBand++) {
			free(compress_readInfo->ppInputBandBufferArray[nBand]);
	}
  	free(compress_readInfo->ppInputBandBufferArray);
  	free(pClient->pClientData);
  	pClient->pClientData = NULL;
  	NCSEcwCompressFreeClient(pClient);
  	
  }
  
