/* $Id: TIFFImageWriter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef TIFFIMAGEWRITER_H
#define TIFFIMAGEWRITER_H

// lt_lib_mrsid_core
#include "lti_geoFileImageWriter.h"

struct tiff;


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTIPixelLookupTable;
class LTReusableBuffer;


/**
 * writes an image stage to a TIFF file
 *
 * This class writes an image stage to a TIFF file.
 */
class TIFFImageWriter : public LTIGeoFileImageWriter
{
public:
   /**
    * constructor
    *
    * Creates a writer for TIFF images.
    *
    * @param  image  the image to write from
    */
   TIFFImageWriter(LTIImageStage *image);

   virtual ~TIFFImageWriter();
   virtual LT_STATUS initialize();

   /**
    * Output resolution information.  Set this to false to prevent writing
    * normal TIFF resolution information.
    *
    * The default is to write this information.
    *
    * @param enable set to true to write resolution information
    */
   void setWriteResolution(bool enable);

   LT_STATUS writeBegin(const LTIScene& scene);
   LT_STATUS writeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);
   LT_STATUS writeEnd();

protected:
   virtual LT_STATUS writeHeaders(lt_uint32 width, lt_uint32 height);
   static LT_STATUS getLibtiffError(void);

   // GeoTIFFImageWriter sets this prior to calling Super::initialize()
   struct tiff *m_tiff;
   double m_x_resolution;
   double m_y_resolution;

private:
   LT_STATUS checkImpedance() const;

   typedef LTIGeoFileImageWriter Super;

   LT_STATUS initMetadata();
   LT_STATUS setColorMap(const LTIPixelLookupTable&);
   LT_STATUS writeFieldInformation(lt_uint32 width, lt_uint32 height);
   
   bool m_writeResolution;

   void init( const LTFileSpec& file );
   char* m_tiff_file_name;
   long m_currentRow;
   int m_compression;
   LTReusableBuffer* m_stripBuffer;

   // nope
   TIFFImageWriter();
   TIFFImageWriter(TIFFImageWriter&);
   TIFFImageWriter& operator=(const TIFFImageWriter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // TIFFIMAGEWRITER_H
