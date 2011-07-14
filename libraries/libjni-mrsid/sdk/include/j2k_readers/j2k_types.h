/* $Id: j2k_types.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef J2K_TYPES_H
#define J2K_TYPES_H

// lt_lib_base
#include "lt_base.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

LT_BEGIN_NAMESPACE(LizardTech)


/**
 * constants used to define progression orders
 */
enum J2KProgressionOrder
{
   J2K_ORDER_INVALID = 0,
   J2K_ORDER_LRCP    = 1,
   J2K_ORDER_RLCP    = 2,
   J2K_ORDER_RPCL    = 3,
   J2K_ORDER_PCRL    = 4,
   J2K_ORDER_CPRL    = 5
};


/**
 * constants used to define tile part styles
 */
enum J2KTilePartFlags
{
   J2K_TILEPART_FLAG_NONE  = 0,
   J2K_TILEPART_FLAG_R     = 1,
   J2K_TILEPART_FLAG_L     = 2,
   J2K_TILEPART_FLAG_C     = 4,
   J2K_TILEPART_FLAG_MAX   = (J2K_TILEPART_FLAG_R | 
                              J2K_TILEPART_FLAG_L |
                              J2K_TILEPART_FLAG_C)
};


/**
 * constants used to define encoding profiles
 */
enum J2KProfile
{
   J2K_PROFILE_NONE     = 0,
   J2K_PROFILE_NPJE     = 1,
   J2K_PROFILE_LOWMEM   = 2
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // J2K_TYPES_H
