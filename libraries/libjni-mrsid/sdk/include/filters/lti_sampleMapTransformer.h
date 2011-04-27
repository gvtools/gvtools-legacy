/* $Id: lti_sampleMapTransformer.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_SAMPLEMAPTRANSFORMER_H
#define LTI_SAMPLEMAPTRANSFORMER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


class LTIReusableBSQBuffer;

/**
 * remaps the color bands of an image
 *
 * This class provides the ability to remap the color bands of an image, e.g.
 * from R-G-B to B-G-R.  Only proper permutations are supported; that is, the
 * number of bands may not be changed, and all bands must appear exactly once
 * in the remapped image.
 *
 * @note This class is \b deprecated.  A better way to remap the bands is to
 * manipulate the band pointers contained in the LTISceneBuffer object at the
 * end of the pipeline.
 */
class LTISampleMapTransformer : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Creates an image stage with the bands of the source image reordered,
    * e.g. to change an image from RGB to BGR format.
    *
    * The length of the permutation array is equal to the number of bands in
    * the image and the elements must be a proper permutation of the band
    * numbers.  That is, the default map "0,1,2" may be set to "2,1,0" or
    * "1,2,0" but not "0,1,1" or "2".
    *
    * @param  sourceImage   the base image
    * @param  dstSampleMap  the band permutation array (if NULL, the default
    *                       map of "0,1,2,..." will be used)
    * @param takeOwnership  set to true to have the filter delete the \a sourceImage
    */
   LTISampleMapTransformer(LTIImageStage* sourceImage,
                           const lt_uint16* dstSampleMap,
                           bool takeOwnership);
   virtual ~LTISampleMapTransformer();
   virtual LT_STATUS initialize();

protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeEnd();
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);

private:
   typedef LTIImageFilter Super;

   const lt_uint16* m_tmpSampleMap;
   lt_uint16* m_newMap;
   bool m_isIdentity;

   LTIReusableBSQBuffer* m_buffer;

   // nope
   LTISampleMapTransformer(const LTISampleMapTransformer&);
   LTISampleMapTransformer& operator=(const LTISampleMapTransformer&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_SAMPLEMAPTRANSFORMER_H
