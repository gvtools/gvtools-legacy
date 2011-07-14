/* $Id: lti_translationFilter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_TRANSLATIONFILTER_H
#define LTI_TRANSLATIONFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"
#include "lti_geoCoord.h"

LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


/**
 * translates (moves) the geo coordinates of the image
 *
 * This class translates (moves) the geo coordinates of the image.
 */
class LTITranslationFilter : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * This class shifts the geographic coordinates of the image by the given
    * amount.
    *
    * @param  sourceImage   the base image
    * @param  xOffset       amount to shift in the X direction
    * @param  yOffset       amount to shift in the Y direction
    * @param takeOwnership  set to true to have the filter delete the \a sourceImage
    */
   LTITranslationFilter(LTIImageStage* sourceImage,
                        double xOffset,
                        double yOffset,
                        bool takeOwnership);

   LTITranslationFilter(LTIImageStage* sourceImage,
                        const LTIGeoCoord &geoCoord,
                        bool takeOwnership);

   virtual ~LTITranslationFilter();
   virtual LT_STATUS initialize();

protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeEnd();
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);

private:
   typedef LTIImageFilter Super;

   LTIGeoCoord m_newGeoCoord;

   // nope
   LTITranslationFilter(const LTITranslationFilter&);
   LTITranslationFilter& operator=(const LTITranslationFilter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_TRANSLATIONFILTER_H
