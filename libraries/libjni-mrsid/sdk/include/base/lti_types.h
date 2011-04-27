/* $Id: lti_types.h 3539 2006-01-09 12:23:20Z nacho $ */
/* //////////////////////////////////////////////////////////////////////////
//                                                                         //
// This code is Copyright (c) 2004 LizardTech, Inc, 1008 Western Avenue,   //
// Suite 200, Seattle, WA 98104.  Unauthorized use or distribution         //
// prohibited.  Access to and use of this code is permitted only under     //
// license from LizardTech, Inc.  Portions of the code are protected by    //
// US and foreign patents and other filings. All Rights Reserved.          //
//                                                                         //
////////////////////////////////////////////////////////////////////////// */
/* PUBLIC - C*/

#ifndef LTI_TYPES_H
#define LTI_TYPES_H

#include "lt_base.h"


#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

#ifdef LT_CPLUSPLUS
extern "C" {
#endif

/**
 * @file
 *
 * This file contains a number of enums, typedefs, etc, that are used
 * throughout the MrSID SDK.
 */


/**
 * colorspaces
 *
 * This enum is used to represent colorspaces.
 */
typedef enum LTIColorSpace
{
  LTI_COLORSPACE_INVALID        = 0,
  LTI_COLORSPACE_RGB            = 1,
  LTI_COLORSPACE_RGBK           = 2,
  LTI_COLORSPACE_CMYK           = 3,
  LTI_COLORSPACE_GRAYSCALE      = 4,
  LTI_COLORSPACE_PALETTE        = 5,
  LTI_COLORSPACE_YIQ            = 6,
  LTI_COLORSPACE_YIQK           = 7,
  LTI_COLORSPACE_MULTISPECTRAL  = 8
} LTIColorSpace;


/**
 * colors
 *
 * This enum is used to represent colors.
 */
typedef enum LTIColor
{
  LTI_COLOR_INVALID   = 0,
  LTI_COLOR_RED       = 1,
  LTI_COLOR_GREEN     = 2,
  LTI_COLOR_BLUE      = 3,
  LTI_COLOR_CYAN      = 4,
  LTI_COLOR_MAGENTA   = 5,
  LTI_COLOR_YELLOW    = 6,
  LTI_COLOR_BLACK     = 7,
  LTI_COLOR_GRAY      = 8,
  LTI_COLOR_UNKNOWN   = 9
} LTIColor;


/**
 * datatypes
 *
 * This enum is used to represent datatypes.
 */
typedef enum LTIDataType
{
  LTI_DATATYPE_INVALID  = 0,
  LTI_DATATYPE_UINT8    = 1,
  LTI_DATATYPE_SINT8    = 2,
  LTI_DATATYPE_UINT16   = 3,
  LTI_DATATYPE_SINT16   = 4,
  LTI_DATATYPE_UINT32   = 5,
  LTI_DATATYPE_SINT32   = 6,
  LTI_DATATYPE_FLOAT32  = 7,
  LTI_DATATYPE_FLOAT64  = 8
} LTIDataType;



/**
 * well-known points
 *
 * This enum is used to represent the well-known points on a rectangle.
 *
 * @note The constant name can be mentally read as "LTI_POSITION_y_x".
 */
typedef enum LTIPosition
{
   LTI_POSITION_UPPER_LEFT     = 1,
   LTI_POSITION_UPPER_CENTER   = 2,
   LTI_POSITION_UPPER_RIGHT    = 3,
   LTI_POSITION_LOWER_LEFT     = 4,
   LTI_POSITION_LOWER_CENTER   = 5,
   LTI_POSITION_LOWER_RIGHT    = 6,
   LTI_POSITION_CENTER_LEFT    = 7,
   LTI_POSITION_CENTER         = 8,
   LTI_POSITION_CENTER_RIGHT   = 9
} LTIPosition;


/**
 * constants representing data layout
 *
 * These are used by classes like
 * LTIRawImageReader and LTIRawImageWriter.
 */
typedef enum LTILayout
{
   LTI_LAYOUT_INVALID  = 0,
   LTI_LAYOUT_BIP      = 1,   /**< band interleaved by pixel */
   LTI_LAYOUT_BSQ      = 2,   /**< band sequential */
   LTI_LAYOUT_BIL      = 3    /**< band interleaved by line */
} LTILayout;


/**
 * constants representing endianness (byte order)
 */
typedef enum LTIEndian
{
   LTI_ENDIAN_INVALID = 0,
   LTI_ENDIAN_HOST    = 1,
   LTI_ENDIAN_LITTLE  = 2,
   LTI_ENDIAN_BIG     = 3
} LTIEndian;


#ifdef LT_CPLUSPLUS
}
#endif

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif
