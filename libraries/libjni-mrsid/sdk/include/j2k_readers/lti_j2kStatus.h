/* $Id: lti_j2kStatus.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_J2KSTATUS_H
#define LTI_J2KSTATUS_H

#include "lt_base.h"

#define LTI_STS_J2K_Base                                          50900
LT_STATUSSTRING_ADD(LTI_STS_J2K_Base, "lt_lib_mrsid_j2k base")

#define LTI_STS_J2K_CannotWriteFile                               50901
LT_STATUSSTRING_ADD(LTI_STS_J2K_CannotWriteFile, "JP2: cannot write to file %F")

#define LTI_STS_J2K_UnsupColorSpace                               50902
LT_STATUSSTRING_ADD(LTI_STS_J2K_UnsupColorSpace, "JP2: unsupported colorspace")

#define LTI_STS_J2K_UnsupDataType                                 50903
LT_STATUSSTRING_ADD(LTI_STS_J2K_UnsupDataType, "JP2: unsupported datatype")

#define LTI_STS_J2K_LibraryError                                  50904
LT_STATUSSTRING_ADD(LTI_STS_J2K_LibraryError, "JP2: internal error")

#define LTI_STS_J2K_HandledError                                  50905
LT_STATUSSTRING_ADD(LTI_STS_J2K_HandledError, "JP2: %s")

#define LTI_STS_J2K_InvalidRegion                                 50906
LT_STATUSSTRING_ADD(LTI_STS_J2K_InvalidRegion, "JP2: invalid region")

#define LTI_STS_J2K_InvalidDims                                   50907
LT_STATUSSTRING_ADD(LTI_STS_J2K_InvalidDims, "JP2: invalid/unsupported dimensions")

#define LTI_STS_J2K_InvalidDecodeScene                            50908
LT_STATUSSTRING_ADD(LTI_STS_J2K_InvalidDecodeScene, "JP2: invalid scene for decode")

#define LTI_STS_J2K_BadDecodeParam                                50909
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadDecodeParam, "JP2: invalid decoder parameter setting")

#define LTI_STS_J2K_MetadataReadError                             50910
LT_STATUSSTRING_ADD(LTI_STS_J2K_MetadataReadError, "JP2: error reading metadata")

#define LTI_STS_J2K_MetadataUuidNotFound                          50911
LT_STATUSSTRING_ADD(LTI_STS_J2K_MetadataUuidNotFound, "JP2: uuid not found")

#define LTI_STS_J2K_BadPrecisionParam                             50912
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadPrecisionParam, "JP2: bad precision value")

#define LTI_STS_J2K_BadLayersParam                                50915
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadLayersParam, "JP2: bad quality layers value")

#define LTI_STS_J2K_CannotReadFile                                50916
LT_STATUSSTRING_ADD(LTI_STS_J2K_CannotReadFile, "JP2: cannot read file")

#define LTI_STS_J2K_BadTileParam                                  50917
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadTileParam, "JP2: bad tile size value")

#define LTI_STS_J2K_BadFlushParam                                 50918
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadFlushParam, "JP2: bad flush period value")

#define LTI_STS_J2K_BadPrecinctsParam                             50919
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadPrecinctsParam, "JP2: bad precincts values")

#define LTI_STS_J2K_BadProgressionParam                           50920
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadProgressionParam, "JP2: bad progression order")

#define LTI_STS_J2K_BadCodeblockParam                             50921
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadCodeblockParam, "JP2: bad codeblock value")

#define LTI_STS_J2K_BadTilePartParam                             50922
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadTilePartParam, "JP2: bad tile part value")

#define LTI_STS_J2K_BadProfile                                    50923
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadProfile, "JP2: bad profile")

#define LTI_STS_J2K_UnsupSigned                                   50924
LT_STATUSSTRING_ADD(LTI_STS_J2K_UnsupSigned, "JP2: unsupported datatype - signed")


#define LTI_STS_J2K_MSIChunkError1                                50980
LT_STATUSSTRING_ADD(LTI_STS_J2K_MSIChunkError1, "JP2: internal error 1")

#define LTI_STS_J2K_MSIChunkError2                                50981
LT_STATUSSTRING_ADD(LTI_STS_J2K_MSIChunkError2, "JP2: internal error 2")

#define LTI_STS_J2K_MSIChunkError3                                50982
LT_STATUSSTRING_ADD(LTI_STS_J2K_MSIChunkError3, "JP2: internal error 3")

#define LTI_STS_J2K_MSIChunkError4                                50983
LT_STATUSSTRING_ADD(LTI_STS_J2K_MSIChunkError4, "JP2: internal error 4")

#define LTI_STS_J2K_Max                                           50999
LT_STATUSSTRING_ADD(LTI_STS_J2K_Base, "lt_lib_mrsid_j2k max")

#endif // LTI_J2KSTATUS_H
