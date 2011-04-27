/* $Id: lti_colorCompositeFilter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_COLORCOMPOSITEFILTER_H
#define LTI_COLORCOMPOSITEFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

/**
 * combine N separate bands into one image
 *
 * This class creates a single N-banded image which is a composition of N single-banded
 * images.  That is, an RGB image can be created from three (nominally
 * grayscale) images which represent the red, green, and blue bands of a
 * dataset.
 */
class LTIColorCompositeFilter : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Creates an image stage which composes the bands of the input images.
    *
    * The \a sourceImages array contains the input images.  The composed image
    * is comprised of these images, in the given order.  A NULL element in
    * this array will result in samples of 0 for the corresponding band.
    *
    * @param  sourceImages    the base images; these must be single-banded images
    * @param  numImages       the number of input images
    * @param  colorSpace      the colorspace of the new image
    * @param  takeOwnership   set to true to have the filter delete the \a sourceImage
    */
   LTIColorCompositeFilter(LTIImageStage** sourceImages,
                           lt_uint16 numImages,
                           LTIColorSpace colorSpace,
                           bool takeOwnership);
   virtual ~LTIColorCompositeFilter();
   virtual LT_STATUS initialize();

   lt_int64 getPhysicalFileSize() const;

   bool isSelective() const;

protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeEnd();
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);

private:
   typedef LTIImageFilter Super;
   
   LT_STATUS updateMetadata();

   LTIImageStage** m_imageReaders;
   LTIImageStage* m_refReader;
   const lt_uint16 m_numImages;
   const LTIColorSpace m_colorSpace;

   // nope
   LTIColorCompositeFilter(const LTIColorCompositeFilter&);
   LTIColorCompositeFilter& operator=(const LTIColorCompositeFilter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_COLORCOMPOSITEFILTER_H
