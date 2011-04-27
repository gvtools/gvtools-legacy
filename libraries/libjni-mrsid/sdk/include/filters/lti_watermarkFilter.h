/* $Id: lti_watermarkFilter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_WATERMARKFILTER_H
#define LTI_WATERMARKFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTIEmbeddedImage;

/**
 * insert a watermark image onto an image
 *
 * Inserts a watermark image onto the current image stage.
 */
class LTIWatermarkFilter : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Creates an image stage with the \a watermarkImage overlaid over the \a
    * sourceImage at the position specified.
    *
    * The \a edgePadding argument is used to specify the distance (in pixels)
    * between the watermark and the edge of the base image specified by the \a
    * position argument.  (This argument is ignored if LTI_POSITION_CENTER is
    * used.)
    *
    * @param  sourceImage     the base image
    * @param  watermarkImage  the watermark to be overlaid
    * @param  position        where to insert the watermark
    * @param  edgePadding     distance (in pixels) between the watermark and
    *                         the base image
    * @param  takeOwnershipWM  set to true to have the filter delete the \a watermarkImage
    * @param  takeOwnership   set to true to have the filter delete the \a sourceImage
    */
   LTIWatermarkFilter(LTIImageStage* sourceImage,
                      LTIImageStage* watermarkImage,
                      LTIPosition position,
                      lt_uint32 edgePadding,
                      bool takeOwnershipWM,
                      bool takeOwnership);

   /**
    * destructor
    */
   virtual ~LTIWatermarkFilter();

   virtual LT_STATUS initialize();

protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeEnd();
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);

private:
   typedef LTIImageFilter Super;

   LT_STATUS checkImpedance() const;

   LTIImageStage* m_rawWatermark;
   LTIEmbeddedImage* m_embeddedWatermark;
   lt_uint32 m_edgePadding;
   LTIPosition m_position;
   bool m_takeOwnershipWM;

   // nope
   LTIWatermarkFilter(const LTIWatermarkFilter&);
   LTIWatermarkFilter& operator=(const LTIWatermarkFilter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_WATERMARKFILTER_H
