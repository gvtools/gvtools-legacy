/* $Id: lti_cropFilter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_CROPFILTER_H
#define LTI_CROPFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


/**
 * crops the image stage to a smaller width and height
 *
 * This class crops the image stage to a smaller width and height.
 */
class LTICropFilter : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Creates an image stage which corresponds to a cropped subsection of the
    * input image, according to the given offset, width, and height.
    *
    * @param  sourceImage    the base image
    * @param  xOffset        x-position of the origin of the new image
    *                        (specified relative to the input image)
    * @param  yOffset        y-position of the origin of the new image
    *                        (specified relative to the input image)
    * @param  newWidth       width of the new image
    * @param  newHeight      height of the new image
    * @param  takeOwnership  set to true to have the filter delete the \a sourceImage
    */
   LTICropFilter(LTIImageStage* sourceImage,
                 lt_int32 xOffset,
                 lt_int32 yOffset,
                 lt_int32 newWidth,
                 lt_int32 newHeight,
                 bool takeOwnership);
   virtual ~LTICropFilter();
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

   bool getReaderScene(const LTIScene &decodeScene, LTIScene &readerScene) const;
   
protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeEnd();
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);

   LT_STATUS getChildScene(const LTIScene &decodeScene, LTIScene &childScene) const;

private:
   typedef LTIImageFilter Super;

   const lt_int32 m_xOffset;
   const lt_int32 m_yOffset;
   const lt_int32 m_newWidth;
   const lt_int32 m_newHeight;

   // nope
   LTICropFilter(const LTICropFilter&);
   LTICropFilter& operator=(const LTICropFilter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_CROPFILTER_H
