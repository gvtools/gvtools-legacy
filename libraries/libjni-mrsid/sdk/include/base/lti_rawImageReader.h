/* $Id: lti_rawImageReader.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTIRAWIMAGEREADER_H
#define LTIRAWIMAGEREADER_H

// lt_lib_mrsid_core
#include "lti_imageReader.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTIPixel;
class LTIOStreamInf;
class LTFileSpec;
class LTIGeoCoord;
class LTIPixel;


/**
 * class for reading RAW files
 *
 * This class reads a RAW image.
 *
 * The RAW format used is simple packed BIP form.
 */
class LTIRawImageReader : public LTIImageReader
{
public:
   /**
    * constructor (stream)
    *
    * This function constructs an LTIImageReader using the data in the
    * stream with the image properties specified.  The input taken from a
    * stream.
    *
    * @param stream      stream containing the RAW image data (may not be NULL) 
    * @param pixelProps  the pixel properties of the image (colorspace, datatype, etc) 
    * @param width       the width of the image 
    * @param height      the height of the image 
    */
   LTIRawImageReader(LTIOStreamInf* stream,
                     const LTIPixel& pixelProps,
                     lt_uint32 width,
                     lt_uint32 height);

   /**
    * constructor (FileSpec)
    *
    * This function constructs an LTIImageReader using the data in the
    * stream with the image properties specified. The input taken from a
    * stream, and the background and nodata pixels may be specified.
    *
    * @param fileSpec    file containing the RAW image data 
    * @param pixelProps  the pixel properties of the image (colorspace, datatype, etc) 
    * @param width       the width of the image 
    * @param height      the height of the image 
    */
   LTIRawImageReader(const LTFileSpec& fileSpec,
                     const LTIPixel& pixelProps,
                     lt_uint32 width,
                     lt_uint32 height);

   /**
    * constructor (char*)
    *
    * This function constructs an LTIImageReader using the data in the
    * stream with the image properties specified.  The input taken from a
    * stream, and the background and nodata pixels may be specified.
    *
    * @param file        file containing the RAW image data (may not be NULL) 
    * @param pixelProps  the pixel properties of the image (colorspace, datatype, etc) 
    * @param width       the width of the image 
    * @param height      the height of the image 
    */
   LTIRawImageReader(const char* file,
                     const LTIPixel& pixelProps,
                     lt_uint32 width,
                     lt_uint32 height);

   /**
    * destructor
    */
   virtual ~LTIRawImageReader();

   /**
    * initializer
    */
   virtual LT_STATUS initialize();


   /**
    * @name Properties of the RAW image
    *
    * These must be called prior to calling initialize(), unlike most other SDK
    * objects.
    */
   /*@{*/

   /**
    * set geo coordinates
    *
    * Sets the geographic coordinates for the image.  If not set, the default
    * is the normal default of (0,h-1) with resolutions (1.0,-1.0).
    *
    * @note This must be called prior to calling initialize().
    *
    * @param  geo  the geo coordinates for the image 
    */
   void setGeoCoordinate(const LTIGeoCoord& geo);

   /**
    * set stream ownership
    *
    * Sets the ownership of the stream, to indicate responsibility for
    * deleting the stream when done. This only pertains to objects which were
    * passed a stream in the ctor.
    *
    * If not set, the default is for the object to not take ownership of the
    * stream.
    *
    * @note This must be called prior to calling initialize().
    *
    * @param  takeOwnership  set to true to have the reader delete the stream 
    */
   void setStreamOwnership(bool takeOwnership);

    /**
    * add classical metadata
    *
    * Directs the reader to populate the standard "classical" metadata tags.
    *
    * If not set, the default is to not add the metadata tags.
    *
    * @note This must be called prior to calling initialize().
    *
    * @param  withMetadata  set to true to have the reader add metadata 
    */
   void setMetadata(bool withMetadata);

    /**
    * set background color
    *
    * Sets the background pixel of the image.
    *
    * If not set, the default is to leave the background pixel unset.
    *
    * @note This must be called prior to calling initialize().
    *
    * @param  background  the new background color of the image 
    */
   void setBackground(const LTIPixel* background);

    /**
    * set nodata color
    *
    * Sets the NoData pixel of the image.
    *
    * If not set, the default is to leave the NoData pixel unset.
    *
    * @note This must be called prior to calling initialize().
    *
    * @param  nodata  the new nodata color of the image 
    */
   void setNoData(const LTIPixel* nodata);

   /**
    * sets the number of bytes in each row
    *
    * Sets the number of bytes in each row.  This allows for alignment
    * padding, required by certain RAW formats.
    *
    * If not set, the bytes per row is simply the number of data items per
    * row times the size of a data item.  (Note that a "data item" in this
    * case may be either a pixel or a sample, depending on the layout being
    * used.)
    *
    * @note This must be called prior to calling initialize().
    *
    * @param  rowBytes  the number of bytes per row 
    */
   void setRowBytes(lt_uint32 rowBytes);

    /**
    * sets the layout or organization of the raw data
    *
    * Sets the layout or organization of the raw data.
    *
    * If not set, the default is BIP format.
    *
    * @note This must be called prior to calling initialize().
    *
    * @param  layout  the layout of the raw data 
    */
   void setLayout(LTILayout layout);

    /**
    * sets the number of bytes to skip at the beginning of the file
    *
    * Sets the number of bytes to skip at the beginning of the file.  If not
    * set, the default is zero (no leading bytes are skipped).
    *
    * @note This must be called prior to calling initialize().
    *
    * @param  skipBytes  the number of bytes to skip 
    */
   void setSkipBytes(lt_uint32 skipBytes);

    /**
    * sets endianness of output file
    *
    * Sets the byte order or endianness of the output file.
    *
    * @note This must be called prior to calling initialize().
    *
    * @param  byteOrder  the endian setting to use 
    */
   void setByteOrder(LTIEndian byteOrder);

   /*@}*/

   virtual LTIImageReader* duplicate();

   virtual lt_int64 getPhysicalFileSize() const;

protected:
   LTIRawImageReader(const LTFileSpec& fileSpec);
   void setCtorPixelProps(const LTIPixel& pixelProps);
   void setCtorDimensions(lt_uint32 width, lt_uint32 height);

   virtual LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);
   virtual LT_STATUS decodeBegin(const LTIScene& scene);
   virtual LT_STATUS decodeEnd();

   LTFileSpec* m_fileSpec;

private:
   LTIOStreamInf* m_stream;
   bool m_ownsStream;
   LTIPixel* m_pixel;
   lt_uint32 m_width;
   lt_uint32 m_height;
   const LTIGeoCoord* m_geoCoordArg;
   lt_int64 m_fileSize;
   bool m_withMetadata;
   lt_uint32 m_rowBytes;
   LTILayout m_layout;
   lt_uint32 m_skipBytes;
   LTIEndian m_byteOrder;

   lt_uint8* m_rowBuffer;

   const LTIPixel* m_backgroundPixel;
   const LTIPixel* m_nodataPixel;
   
   // nope
   LTIRawImageReader(LTIRawImageReader&);
   LTIRawImageReader& operator=(const LTIRawImageReader&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTIRAWIMAGEREADER_H
