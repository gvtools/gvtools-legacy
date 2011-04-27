/* $Id: lti_utils.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_UTILS_H
#define LTI_UTILS_H

// lt_lib_base
#include "lt_base.h"

// lt_lib_mrsid_core
#include "lti_types.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

/**
 * utility functions
 *
 * The LTIUtils class contains a number of generally useful static functions, e.g.
 * functions which operate on the enums declared in lti_types.h
 *
 * @note This is a "static" class; do not instantiate.
 */
class LTIUtils
{
public:
   /**
    * returns number of samples per pixel
    *
    * This function returns the number of samples (bands) per pixel
    * for the given colorspace, e.g. 3 for RGB.  If the number of
    * bands is not known for a given colorspace, 0 will be returned.
    *
    * @param  colorspace  the colorspace to query 
    * @return the number of bands
    */
	static lt_uint8 getSamplesPerPixel(LTIColorSpace colorspace);

   /**
    * returns number of bytes for a given a datatype
    *
    * This function returns the number of bytes for a given datatype,
    * e.g. 1 for LTI_DATATYPE_UINT8 or 4 for LTI_DATATYPE_FLOAT32.
    *
    * @param  datatype  the datatype to query 
    * @return the number of bytes
    */
	static lt_uint8 getNumBytes(LTIDataType datatype);

   /**
    * @name Dynamic range conversion functions
    */
   /*@{*/

   /**
    * convert from window/level to min/max
    *
    * This function converts a "window and level" style of dynamic range
    * to a "minimum and maximum" style.
    *
    * @param window  the window value 
    * @param level   the level value 
    * @param drmin   the dynamic range minimum value 
    * @param drmax   the dynamic range maximum value 
    */
   static void convertWindowLevelToMinMax(double window, double level,
                                          double& drmin, double& drmax);

  /**
    * convert from min/max to window/level
    *
    * This function converts a "minimum and maximum" style of dynamic range
    * to a "window and level" style.
    *
    * The "window" is defined as the width of the range, i.e. maximum - minimum + 1.
    * The "level" is defined as the center of the window, i.e. minimum + 1/2 * window.
    *
    * @param drmin   the dynamic range minimum value 
    * @param drmax   the dynamic range maximum value 
    * @param window  the window value  
    * @param level   the level value 
    */
   static void convertMinMaxToWindowLevel(double drmin, double drmax,
                                          double& window, double& level);
   /*@}*/

   /**
    * @name Mag/level conversion functions
    */
   /*@{*/

  /**
    * convert mag to level
    *
    * This function converts a "magnification" value to a "level" value.
    *
    * Examples:
    * \li full resolution: mag of 1.0 equals level of 0
    * \li down-sampled: mag of 0.5 equals level of 1
    * \li up-sampled: mag of 2.0 equals level of -1
    *
    * @param   mag  the magnification value to convert 
    * @return  the magnification expressed as a level
    */
   static lt_int32 magToLevel(double mag);

  /**
    * convert level to mag
    *
    * This function converts a "level" value to a "magnification" value.
    *
    * See magToLevel() for examples.
    *
    * @param   level  the level value to convert 
    * @return  the level expressed as a magnification
    */
   static double levelToMag(lt_int32 level);
   /*@}*/

   /**
    * get SDK version information
    *
    * This function returns SDK version and build information.  The major,
    * minor, and revision numbers correspond to the public SDK version
    * number.  For this SDK series, \a major will be 4.  The revision number
    * is used to indicate the intermediate release point, e.g. "Technology
    * Preview 8".  The build number and branch name are for internal use by
    * LizardTech.
    *
    * @param  major  the major version number
    * @param  minor  the minor version number
    * @param  revision  the revision number
    * @param  build  the build number
    * @param  branch  the branch name
    */
   static void getVersionInfo(lt_uint32& major,
                              lt_uint32& minor,
                              lt_uint32& revision,
                              lt_uint32& build,
                              const char*& branch);

   /**
    * get SDK version information as a string
    *
    * This function returns SDK version and build information as a formatted
    * string.  The string will be of the form "SDK Version MAJOR.MINOR.REVISION.BUILD.BRANCH",
    * using the values returned from LTIUtils::getVersionInfo().
    *
    * @return  the version string
    */
   static const char* getVersionString();

private:
   // nope
   LTIUtils();
   LTIUtils(const LTIUtils&);
};


LT_END_NAMESPACE(LizardTech)


#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_UTILS_H
