/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
 */
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "potrace_raster.h"

/**
 * Parte de jni para vectorizar un buffer
 */
JNIEXPORT jdoubleArray JNICALL Java_org_gvsig_jpotrace_Potrace_vectorizeBufferRasterNat(JNIEnv *env, jclass clase, jintArray bufferIn, jint width, jint height, jobjectArray array) {
	int i;
	jint *cbufferIn;
	double *values;
	jsize size;
	jdoubleArray jvalues;
	jint length;
	char **list;

	cbufferIn = (*env)->GetIntArrayElements(env, bufferIn, NULL);

	length = (*env)->GetArrayLength(env, array);
	list = malloc(length * sizeof(char *));
	for (i = 0; i < length; i++) {
		jobject string = (*env)->GetObjectArrayElement(env, array, i);
		list[i] = (char *) (*env)->GetStringUTFChars(env, string, NULL);
	}

	values = vectorizarBuffer((long *) cbufferIn, width, height, length, list);
	// we release the used memory here
	(*env)->ReleaseIntArrayElements(env, bufferIn, cbufferIn, 0);
	size = (jsize) values[0];
	jvalues = (*env)->NewDoubleArray(env, size);
	(*env)->SetDoubleArrayRegion(env, jvalues, 0, size, values);

	free(values);

	for (i = 0; i < length; i++) {
		jobject string = (*env)->GetObjectArrayElement(env, array, i);
		(*env)->ReleaseStringUTFChars(env, string, list[i]);
	}
	free (list);

	return jvalues;
}
