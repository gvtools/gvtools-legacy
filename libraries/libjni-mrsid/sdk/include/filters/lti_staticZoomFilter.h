/* $Id: lti_staticZoomFilter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_STATICZOOMFILTER_H
#define LTI_STATICZOOMFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTIMultiResFilter;


/**
 * magnifies the image by a fixed amount
 *
 * This class magnifies the image by a fixed amount.  In effect this simply changes the
 * width and height of the image statically, i.e. for the life of the
 * pipeline.  The resampling is performed internally by the LTIMultiresFilter
 * class.
 */
class LTIStaticZoomFilter : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Magnifies the image by the scale factor given.  The image width, height,
    * geographic resolution, etc, are all updated accordingly.
    *
    * A positive scale factor performs a "res-up" operation, while a negative
    * scale factor will reduce the image.  That is, a scale factor of 2 will
    * double the image size, e.g. from a magnification of 1.0 to 2.0, while a
    * scale factor of -2 will halve the image size, e.g. from a magnification
    * of 1.0 to 0.5.
    * 
    * @param  sourceImage   the base image
    * @param  scaleFactor    the integer scale factor
    * @param takeOwnership  set to true to have the filter delete the \a sourceImage
    */
   LTIStaticZoomFilter(LTIImageStage* sourceImage,
                       lt_int8 scaleFactor,
                       bool takeOwnership);
   virtual ~LTIStaticZoomFilter();
   virtual LT_STATUS initialize();

   virtual lt_int64 getEncodingCost(const LTIScene& scene) const;

   LT_STATUS projectPointAtMag(double upperLeft,
                               double mag,
                               double& newUpperLeft) const;
   
   LT_STATUS projectDimAtMag(double dim,
                             double mag,
                             double& newDim) const;

   LT_STATUS getDimsAtMag(double mag,
                          lt_uint32& width,
                          lt_uint32& height) const;

   bool getReaderScene(const LTIScene &decodeScene, LTIScene &readerScene) const;
   
protected:
   virtual LT_STATUS decodeBegin(const LTIScene& scene);
   virtual LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer,
                                 const LTIScene& stripScene);
   virtual LT_STATUS decodeEnd();

   void getChildScene(const LTIScene &decodeScene, LTIScene &childScene) const;

private:
   typedef LTIImageFilter Super;

   LTIMultiResFilter* m_multiresFilter;
   double m_deltaMag;

   // nope
   LTIStaticZoomFilter(const LTIStaticZoomFilter&);
   LTIStaticZoomFilter& operator=(const LTIStaticZoomFilter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_STATICZOOMFILTER_H
