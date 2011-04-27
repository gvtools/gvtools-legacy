/* $Id: lti_colorTransformer.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_COLORTRANSFORMER_H
#define LTI_COLORTRANSFORMER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTIReusableBSQBuffer;

/**
 * change the colorspace of the image
 *
 * This class changes the colorspace of the image.
 *
 * The supported color transforms are:
 * \li from RGB to CMYK, GRAYSCALE, or YIQ
 * \li from GRAYSCALE to RGB
 * \li from CMYK to RGB, RGBK, or YIQK
 * \li from YIQ to RGB
 * \li from YIQK to CMYK
 */
class LTIColorTransformer : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Creates an image stage with the given colorspace.  The sample values
    * will undergo the requisite color transform function to map from the
    * input colorspace to the output colorspace.
    *
    * @note The value of \a dstNumBands image is set according to the
    * \a dstColorSpace, as is in the constructor for the LTIPixel class.
    *
    * @param  sourceImage    the base image
    * @param  dstColorSpace  the colorspace of the new image
    * @param  dstNumBands    the number of bands in the new image
    * @param  takeOwnership  set to true to have the filter delete the \a sourceImage
    */
   LTIColorTransformer(LTIImageStage* sourceImage,
                       LTIColorSpace dstColorSpace,
                       lt_uint16 dstNumBands,
                       bool takeOwnership);
   virtual ~LTIColorTransformer();
   virtual LT_STATUS initialize();

protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeEnd();
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);

protected: // these are only protected for the temporary LTIColorTransformerMultiband class
   LTIPixel* remapPixel(const LTIPixel& pixel);

   const LTIColorSpace m_dstColorSpace;
   const lt_uint16 m_dstNumBands;
   LTIColorSpace m_srcColorSpace;
   lt_uint16 m_srcNumBands;
   bool m_isIdentity;

   LTIReusableBSQBuffer* m_buffer;

private:
   // nope
   LTIColorTransformer(const LTIColorTransformer&);
   LTIColorTransformer& operator=(const LTIColorTransformer&);
};



/**
 * Extends LTIColorTransformer to change the colorspace of the image and
 * also remap the bands.
 *
 * This class is typically used for mapping multispectral images to known
 * (and often displayable) colorspaces.  For example, a 6-banded
 * multispectral image might be changed to an RGB colorspace using only bands
 * 0, 2, and 3.
 *
 * Note that the allowed remapping of bands is different than in the
 * LTISampleMapTransformer class.
 *
 * @note This class is likely to change in a future release.
 */
class LTIColorTransformerMultiband : public LTIColorTransformer
{
public:
   /**
   * Constructor.
   *
   * Creates an image stage with the given colorspace using the given
   * remapping of bands.
   *
   * The \a dstBandMap parameter is an array of band numbers.  The array
   * elements must be within the range of the number of bands in the input
   * colorspace, and the number of elements in the array must be equal to
   * \a dstNumBands.  A given band may be specified more than once.
   *
   * @note The value of \a dstNumBands image is set according to the
   * \a dstColorSpace, as is in the constructor for the LTIPixel class.
   *
   * @param  sourceImage    the base image
   * @param  dstColorSpace  the colorspace of the new image
   * @param  dstBandMap     the array of bands to map to the new colorspace
   * @param  dstNumBands    the number of bands in the new image
   * @param  takeOwnership  set to true to have the filter delete the \a sourceImage
   */
   LTIColorTransformerMultiband(LTIImageStage* sourceImage,
                                LTIColorSpace dstColorSpace,
                                lt_uint16* dstBandMap,
                                lt_uint16 dstNumBands,
                                bool takeOwnership);
   virtual ~LTIColorTransformerMultiband();
   virtual LT_STATUS initialize();

   LT_STATUS decodeStrip(LTISceneBuffer &stripBuffer, const LTIScene &stripScene);

private:
   typedef LTIColorTransformer Super;

   const lt_uint16 *m_dstBandMapCtor;
   lt_uint16 *m_dstBandMap;
   LTIColorTransformerMultiband(const LTIColorTransformerMultiband&);
   LTIColorTransformerMultiband& operator=(const LTIColorTransformerMultiband&);
};

LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_COLORTRANSFORMER_H
