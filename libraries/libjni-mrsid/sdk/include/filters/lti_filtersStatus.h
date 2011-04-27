/* $Id: lti_filtersStatus.h 3539 2006-01-09 12:23:20Z nacho $ */
/* //////////////////////////////////////////////////////////////////////////
//                                                                         //
// This code is Copyright (c) 2004 LizardTech, Inc, 1008 Western Avenue,   //
// Suite 200, Seattle, WA 98104.  Unauthorized use or distribution         //
// prohibited.  Access to and use of this code is permitted only under     //
// license from LizardTech, Inc.  Portions of the code are protected by    //
// US and foreign patents and other filings. All Rights Reserved.          //
//                                                                         //
////////////////////////////////////////////////////////////////////////// */
/* PUBLIC */

#ifndef LTI_FILTERSSTATUS_H
#define LTI_FILTERSSTATUS_H

#include "lt_base.h"

#define LTI_STS_Filters_Base                          50200
LT_STATUSSTRING_ADD(LTI_STS_Filters_Base, "lt_lib_mrsid_imageFilters base")

#define LTI_STS_Filters_UnsupDataType                 50201
LT_STATUSSTRING_ADD(LTI_STS_Filters_UnsupDataType, "unsupported datatype")

#define LTI_STS_Filters_UnsupColorTransform           50202
LT_STATUSSTRING_ADD(LTI_STS_Filters_UnsupColorTransform, "unsupported colorspace transform")

#define LTI_STS_Filters_InvalidArgument               50203
LT_STATUSSTRING_ADD(LTI_STS_Filters_InvalidArgument, "invalid argument")

#define LTI_STS_Filters_UnsupDataTypeTransform        50204
LT_STATUSSTRING_ADD(LTI_STS_Filters_UnsupDataTypeTransform, "unsupported datatype transform")

#define LTI_STS_Filters_MosaicIncompatPixelProps      50205
LT_STATUSSTRING_ADD(LTI_STS_Filters_MosaicIncompatPixelProps, "incompatible pixel properties - bit-depth, colorspace, etc")

#define LTI_STS_Filters_MosaicIncompatXRes            50206
LT_STATUSSTRING_ADD(LTI_STS_Filters_MosaicIncompatXRes, "incompatible X resolutions")

#define LTI_STS_Filters_MosaicIncompatYRes            50207
LT_STATUSSTRING_ADD(LTI_STS_Filters_MosaicIncompatYRes, "incompatible Y resolutions")

#define LTI_STS_Filters_WatermarkTooBig               50208
LT_STATUSSTRING_ADD(LTI_STS_Filters_WatermarkTooBig, "watermark too big")

#define LTI_STS_Filters_InvalidSampleMap              50209
LT_STATUSSTRING_ADD(LTI_STS_Filters_InvalidSampleMap, "invalid sample map")

#define LTI_STS_Filters_InvalidEmbedding              50210
LT_STATUSSTRING_ADD(LTI_STS_Filters_InvalidEmbedding, "invalid embedding")

#define LTI_STS_Filters_InvalidComposition            50211
LT_STATUSSTRING_ADD(LTI_STS_Filters_InvalidComposition, "invalid composition inputs")

#define LTI_STS_Filters_MosaicIncompatXResMulti       50212
LT_STATUSSTRING_ADD(LTI_STS_Filters_MosaicIncompatXResMulti, "incompatible X resolutions - not scalable")

#define LTI_STS_Filters_MosaicIncompatYResMulti       50213
LT_STATUSSTRING_ADD(LTI_STS_Filters_MosaicIncompatYResMulti, "incompatible Y resolutions - not scalable")

#define LTI_STS_Filters_MosaicIncompatXYResMulti      50214
LT_STATUSSTRING_ADD(LTI_STS_Filters_MosaicIncompatXYResMulti, "incompatible X/Y resolutions - not scalable")

#define LTI_STS_Filters_Max                           50299
LT_STATUSSTRING_ADD(LTI_STS_Filters_Max, "lt_lib_mrsid_imageFilters max")

#endif // LTI_FILTERSSTATUS_H
