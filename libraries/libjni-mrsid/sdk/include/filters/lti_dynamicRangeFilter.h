/* $Id: lti_dynamicRangeFilter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_DYNAMICRANGEFILTER_H
#define LTI_DYNAMICRANGEFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


/**
 * change dynamic range of image
 *
 * Adjusts the sample values to fit the given dynamic range.
 */
class LTIDynamicRangeFilter : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Creates an image stage with the sample data adjusted according to the
    * given dynamic range values.
    *
    * @param  sourceImage    the base image
    * @param  window         the number of units or "width" of the desired range
    * @param  level          the midpoint of the window; this effectively defines
    *                        the min and max sample values
    * @param  takeOwnership  set to true to have the filter delete the \a sourceImage
    */
   LTIDynamicRangeFilter(LTIImageStage* sourceImage,
                         double window,
                         double level,
                         bool takeOwnership);

   /**
   * Constructor.
   *
   * Creates an image stage with the sample data adjusted according to the
   * dynamic range values inherit in the image, e.g. in the metadata.
   *
   * @param  sourceImage    the base image
   * @param  takeOwnership  set to true to have the filter delete the \a sourceImage
   */
   LTIDynamicRangeFilter(LTIImageStage* sourceImage,
                         bool takeOwnership);

   virtual ~LTIDynamicRangeFilter();
   virtual LT_STATUS initialize();

protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeEnd();
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);

private:
   typedef LTIImageFilter Super;

   double m_window;
   double m_level;
   const bool m_userInfo;
   double m_drmin;
   double m_drmax;


   // nope
   LTIDynamicRangeFilter(const LTIDynamicRangeFilter&);
   LTIDynamicRangeFilter& operator=(const LTIDynamicRangeFilter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_DYNAMICRANGEFILTER_H
