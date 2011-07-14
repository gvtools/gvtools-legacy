/* $Id: lti_multiresFilter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_MULTIRESFILTER_H
#define LTI_MULTIRESFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


/**
 * add resolutions to the image
 *
 * Extends the magnification range of an image, to allow decodes at different
 * resolutions than the image stage would normally allow.
 *
 * Note that this class is not the same as at the LTIStaticZoomFilter class,
 * which scales the magnification statically for the pipeline when initially
 * constructed.  This class allows for the zoom level to be extended for an
 * individual decode operation.
 */
class LTIMultiResFilter : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Creates an image stage which can be decoded at arbitrary magnifications.
    *
    * Normally image stages will only support a limited set of magnification
    * values for the LTIScene passed to LTIImageStage::read() -- often, only
    * 1.0.  This class will perform any needed resampling on the fly so that
    * arbitrary (power-of-two) magnifications are supported.
    *
    * @param  sourceImage     the base image
    * @param takeOwnership    set to true to have the filter delete the \a sourceImage
    */
   LTIMultiResFilter(LTIImageStage* sourceImage, bool takeOwnership);
   LTIMultiResFilter(LTIImageStage* sourceImage,
                     double minMag,
                     double maxMag,
                     bool takeOwnership);

   virtual ~LTIMultiResFilter();
   virtual LT_STATUS initialize();

   LT_STATUS projectPointAtMag(double upperLeft,
                               double mag,
                               double& newUpperLeft) const;
   
   LT_STATUS projectDimAtMag(double dim,
                             double mag,
                             double& newDim) const;

   LT_STATUS getDimsAtMag(double mag,
                          lt_uint32& width,
                          lt_uint32& height) const;


   bool getReaderScene(const LTIScene &decodeScene,
                       LTIScene &readerScene) const;

protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer,
                         const LTIScene& stripScene);
   LT_STATUS decodeEnd();


   enum Mode
   {
      MODE_INVALID,
      MODE_PASSTHROUGH,
      MODE_DOWNSAMPLE,
      MODE_DOWNSAMPLE_FULLREAD,
      MODE_UPSAMPLE,
   };

   void getModeAndScale(const LTIScene &scene,
                        Mode &mode,
                        double &scale) const;

   bool getChildScene(const LTIScene &scene,
                      LTIScene &childScene) const;

   struct StripCache;

   enum
   {
      // The largest possible mag is based on the 2gb
      // scene limitation. Thus the largest scene we
      // should ever expect is approximately the square
      // root of (2gb / 3) pixels on a  side. If we
      // assume the smallest image we'll ever encounter
      // is 32x32 then the largest magnification can
      // be calculated. It's big, but we need a real number!
      kMaxMagnification = 512   // 51200% zoom!
   };

private:
   double m_mrMinMag;
   double m_mrMaxMag;

   Mode m_mode;
   double m_scale;
   double m_curX;
   double m_curY;
   lt_int32 m_curStrip;
   lt_int32 m_numStrips;

   StripCache *m_stripCache; 

   // nope
   LTIMultiResFilter(const LTIMultiResFilter&);
   LTIMultiResFilter& operator=(const LTIMultiResFilter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_MULTIRESFILTER_H
