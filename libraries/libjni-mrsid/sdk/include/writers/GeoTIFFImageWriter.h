/* $Id: GeoTIFFImageWriter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef GEOTIFFIMAGEWRITER_H
#define GEOTIFFIMAGEWRITER_H

// lt_lib_mrsid_imageWriters
#include "TIFFImageWriter.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

LT_BEGIN_NAMESPACE(LizardTech)

class LTIMetadataRecord;


/**
 * writes an image stage to a GeoTIFF file
 *
 * This class writes an image stage to a GeoTIFF file.
 */
class GeoTIFFImageWriter : public TIFFImageWriter
{
public:
   /**
    * constructor
    *
    * Creates a writer for GeoTIFF images.
    *
    * @param  image  the image to write from
    */
   GeoTIFFImageWriter(LTIImageStage* image);
   virtual ~GeoTIFFImageWriter();
   LT_STATUS initialize();

   LT_STATUS writeBegin(const LTIScene& scene);
   LT_STATUS writeEnd();

   /**
    * Sets the PIXELSCALE tag.  This function can be used to manually
    * override the value set for the PIXELSCALE tag.
    *
    * @param  pixScale  the array of pixel scale values to use
    */
   void setPixelScale(double pixScale[]);

   /**
    * Sets the TIEPOINTS tag.  This function can be used to manually
    * override the value set for the TIEPOINTS tag.
    *
    * @param  tiePoints  the array of tie point values to use
    * @param  number     the number of tie points in the array
    */
   void setTiePoints(double *tiePoints, int number);

private:
   typedef TIFFImageWriter Super;

   LT_STATUS writeHeaders(lt_uint32 width, lt_uint32 height);

   // we use a uint for the key, to avoid exposing geokey_t
   bool setKey(lt_uint32 key, unsigned char byteVal);
   bool setKey(lt_uint32 key, unsigned short shortVal);
   bool setKey(lt_uint32 key, const char* asciiVal);
   bool setKey(lt_uint32 key, double doubleVal);
   bool setKey(lt_uint32 key, unsigned long longVal);

   char* getKeyName(lt_uint32 key);

   LT_STATUS setKeysFromMetadata();
   LT_STATUS setKeyFromMetadata(const LTIMetadataRecord&);
   LT_STATUS setKeyFromMetadata_geotiffnum(const LTIMetadataRecord& rec);
   LT_STATUS setKeyFromMetadata_geo(const LTIMetadataRecord& rec);
   int getKeyId(const char*);

   void *m_geoTiffPtr;

   double m_tiePoints[6];
   double m_res[3];
   double m_xval;
   double m_yval;
   double m_xres;
   double m_yres;

   // nope
   GeoTIFFImageWriter();
   GeoTIFFImageWriter(GeoTIFFImageWriter&);
   GeoTIFFImageWriter& operator=(const GeoTIFFImageWriter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // GEOTIFFIMAGEWRITER_H
