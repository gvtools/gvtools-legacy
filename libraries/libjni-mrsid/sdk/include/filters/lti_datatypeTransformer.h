/* $Id: lti_datatypeTransformer.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_DATATYPETRANSFORMER_H
#define LTI_DATATYPETRANSFORMER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

// we support only float, uint8, uint16

class LTIReusableBSQBuffer;

/**
 * changes the datatype of the samples of the image
 *
 * This class changes the datatype of the samples of the image.
 *
 * The values of the samples are scaled as required to meet the range of the
 * new datatype.
 */
class LTIDataTypeTransformer : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Creates an image stage with the given datatype.  The sample values are
    * scaled as required to meet the range of the new datatype; that is, a
    * value of 65535 for a 16-bit datatype will map to a value of 255 for an
    * 8-bit datatype, and a value of 127 for an 8-bit datatype will map to
    * a value of 32767 for a 16-bit datatype.
    *
    * @note Only uint8, uint16, and float32 datatypes are supported.
    *
    * @param  sourceImage    the base image
    * @param  dstDataType    the datatype of the new image stage
    * @param  takeOwnership  set to true to have the filter delete the \a sourceImage
    */
   LTIDataTypeTransformer(LTIImageStage* sourceImage,
                          LTIDataType dstDataType,
                          bool takeOwnership);
   virtual ~LTIDataTypeTransformer();
   virtual LT_STATUS initialize();

protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeEnd();
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);

private:
   LTIPixel* remapPixel(const LTIPixel& pixel);

   const LTIDataType m_dstDataType;
   LTIDataType m_srcDataType;
   bool m_isIdentity;
   LTIReusableBSQBuffer* m_buffer;

   // nope
   LTIDataTypeTransformer(const LTIDataTypeTransformer&);
   LTIDataTypeTransformer& operator=(const LTIDataTypeTransformer&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_DATATYPETRANSFORMER_H
