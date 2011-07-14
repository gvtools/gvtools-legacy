/* $Id: lti_embeddedImage.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_EMBEDDEDIMAGE_H
#define LTI_EMBEDDEDIMAGE_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"
#include "lti_scene.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTIReusableBSQBuffer;

/**
 * create a larger frame for the image
 *
 * Creates a new image stage of the given size, containing the input image
 * stage within it.
 *
 * This class is used to make an image stage "larger", e.g. to place an image
 * on a larger "canvas" for more flexible decoding.  This class is used
 * by the LTIMosaicFilter class to simplify certain computations by making
 * all the input images map to the same underlying grid shape and size.
 *
 * The embedding process honors the background and nodata pixel settings.
 *
 * The LTIGeoCoord information for the image stage is updated appropriately.
 */
class LTIEmbeddedImage : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Creates an image stage whose input image is placed within a much larger
    * empty canvas.
    *
    * @param  sourceImage      the base image
    * @param  newWidth         the width of the new image stage
    * @param  newHeight        the height of the new image stage
    * @param  xOffset          pixel x-position of the input image in the new image
    * @param  yOffset          pixel y-position of the input image in the new image
    * @param  backgroundPixel  background pixel color to use for the new image
    *                          (may be NULL, in which case the input image's
    *                          background is used)
    * @param  nodataPixel      nodata pixel color to use for the new image
    *                          (may be NULL, in which case the input image's
    *                          nodata is used)
    * @param takeOwnership    set to true to have the filter delete the \a sourceImage
    */
   LTIEmbeddedImage(LTIImageStage* sourceImage,
                    lt_int32 newWidth,
                    lt_int32 newHeight,
                    double xOffset,
                    double yOffset,
                    const LTIPixel* backgroundPixel,
                    const LTIPixel* nodataPixel,
                    bool takeOwnership);

   virtual ~LTIEmbeddedImage();
   virtual LT_STATUS initialize();

   virtual lt_int64 getEncodingCost(const LTIScene& scene) const;

   LT_STATUS setFillingBackground(bool fill);
   bool getFillingBackground(void) const;
   
   LT_STATUS setUsingFuzzyNoData(bool fuzzy);
   bool getUsingFuzzyNoData(void) const;

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

   double getChildXPosAtMag(double mag) const;
   double getChildYPosAtMag(double mag) const;
   bool getChildScene(const LTIScene &parentScene, LTIScene &childScene) const;

private:
   typedef LTIImageFilter Super;

   LTIGeoCoord* m_fullGeoCoord;
   lt_int32 m_fullWidth;
   lt_int32 m_fullHeight;
   double m_childXPos_P;
   double m_childYPos_P;
   
   LTIScene m_parentScene;
   LTIScene m_childScene;
   bool m_haveAnything;
   lt_int32 m_firstStrip;
   lt_int32 m_lastStrip;

   LTIPixel* m_userBackgroundPixel;
   LTIPixel* m_userNodataPixel;

   LTIReusableBSQBuffer* m_buffer;
   bool m_fillingBackground;
   bool m_usingFuzzyNoData;

   // nope
   LTIEmbeddedImage(const LTIEmbeddedImage&);
   LTIEmbeddedImage& operator=(const LTIEmbeddedImage&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_EMBEDDEDIMAGE_H
