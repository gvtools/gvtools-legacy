/* $Id: NITFImageWriter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef NITFIMAGEWRITER_H
#define NITFIMAGEWRITER_H

// lt_lib_mrsid_core
#include "lti_geoFileImageWriter.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class GDALDataset;

LT_BEGIN_NAMESPACE(LizardTech)

class LTFileSpec;

/**
 * writes an image stage to NITF file
 *
 * This class writes an image stage to NITF file.
 *
 * Only very limited NITF support is provided.  Specifically, only simple
 * 8-bit and 16-bit data, and no special metadata support.
 */
class NITFImageWriter : public LTIGeoFileImageWriter
{
public:
   /**
    * constructor
    *
    * Creates a writer for NITF images.
    *
    * @param  image  the image to write from
    */
   NITFImageWriter(LTIImageStage* image);
   virtual ~NITFImageWriter();
   LT_STATUS initialize();

   LT_STATUS writeBegin(const LTIScene& scene);
   LT_STATUS writeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);
   LT_STATUS writeEnd();

private:
   typedef LTIGeoFileImageWriter Super;

   LT_STATUS checkImpedance() const;

   void allocTempBuffer(lt_uint32);
   lt_uint8* copyIntoBuffer(lt_uint32 w, lt_uint32 h, lt_uint8* bipData);

   double m_xUL;
   double m_yUL;
   double m_xDim;
   double m_yDim;
   double m_xRot;
   double m_yRot;
   bool m_hasWorldInfo;

   lt_uint32 m_myBytesPerSample;
   lt_uint32 m_myNumBands;
   lt_uint32 m_myWidth;
   lt_uint32 m_myHeight;
   int m_gdalDataType; // (cast to GDALDataType)

   GDALDataset* m_gdalDataset;
   lt_uint8* m_tempBuffer;
   lt_uint32 m_tempBufferSize;

   // nope
   NITFImageWriter();
   NITFImageWriter(NITFImageWriter&);
   NITFImageWriter& operator=(const NITFImageWriter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // NITFIMAGEWRITER_H
