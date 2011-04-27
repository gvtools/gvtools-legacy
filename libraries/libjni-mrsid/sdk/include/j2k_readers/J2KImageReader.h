/* $Id: J2KImageReader.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef J2KIMAGEREADER_H
#define J2KIMAGEREADER_H

// lt_lib_mrsid_core
#include "lti_imageReader.h"

// lt_lib_mrsid_j2k
#include "j2k_types.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


LT_BEGIN_NAMESPACE(LizardTech)

class LTFileSpec;
class LTIOStreamInf;
class J2KImageReaderImp;

/**
 * image reader for JPEG 2000 images
 *
 * This class provides support for reading JPEG 2000 images.
 *
 * @note Not all JP2/Part 1 images are supported yet.  See the release
 * notes for details.
 */
class J2KImageReader : public LTIImageReader
{
public:
   /**
    * constructor
    *
    * Create a JPEG 2000 image reader from a file.
    *
    * The \a isPersistent parameter is used as a performance hint
    * to the underlying codec.  If you intend to do only one decode
    * request from the image, you may set this value to false; this
    * may result in improved memory usage and performance.  If multiple
    * decode requests are to be made, the value must be set to true.
    *
    * The \a maxBpp parameter controls the number of bits per sample
    * to decode from.  Values smaller than the number of bits per
    * sample will produce lossy images, but generally the decode
    * operations will be faster.  A value of -1.0 indicates all
    * available bits are to be used.
    * 
    * @param  fileSpec      file to read from
    * @param  isPersistent  set to true, unless only doing one decode request
    * @param  maxBpp        set to number of bits per sample desired (or -1.0f for all bits)
    */
   J2KImageReader(const LTFileSpec& fileSpec,
                  bool isPersistent = true,
                  float maxBpp = -1.0f);

   /**
    * constructor
    *
    * Create a JPEG 2000 image reader from a stream.
    *
    * See file constructor for parameter descriptions.
    *
    * @param  stream        stream to read from
    * @param  isPersistent  set to true, unless only doing one decode request
    * @param  maxBpp        set to number of bits per sample desired (or -1.0f for all bits)
    */
   J2KImageReader(LTIOStreamInf &stream,
                  bool isPersistent = true,
                  float maxBpp = -1.0f);

   /**
    * destructor
    */
   virtual ~J2KImageReader();
   
   LT_STATUS initialize();

   /**
    * @name precision control
    */
   /*@{*/

   /**
    * control decoder use of bits of precision
    *
    * These functions allow the caller to control the number of bits of
    * precision (or bits per sample) used in satisfying the decode request.
    * 
    * The default is to use all available bits.
    *
    * Note this allows for precision control on a per-decode basis.  The
    * \a maxBpp constructor parameter performs a similar function, but
    * sets the value for all decode operations on the image.
    */
   LT_STATUS setParameter_Precision(lt_uint32 precision);
   lt_uint32 getParameter_Precision() const;
   lt_uint32 getParameter_MinPrecision() const;
   lt_uint32 getParameter_MaxPrecision() const;
   lt_uint32 getParameter_DefaultPrecision() const;
   /*@}*/

   /**
    * @name quality layer control
    */
   /*@{*/

   /**
    * control decoder use of quality layers 
    *
    * These functions allow the caller to control the number of quality
    * layers used in satisfying the decode request.  Use of quality
    * layers allow for "preset" compression ratios to be encoded into
    * the image.
    * 
    * The default is to use all available layers).
    */
   LT_STATUS setParameter_Layers(lt_uint32 numLayers);
   lt_uint32 getParameter_Layers() const;
   lt_uint32 getParameter_MinLayers() const;
   lt_uint32 getParameter_MaxLayers() const;
   lt_uint32 getParameter_DefaultLayers() const;
   /*@}*/

   /**
    * get image tile size
    *
    * Returns the size of the tile(s) used to encode the image, in pixels.
    * An image that is "not tiled" is considered to be encoded as one
    * large tile, the size of the entire image.
    *
    * @param width  tile width, in pixels
    * @param height  tile height, in pixels
    */
   void getParameter_TileSize(int& width, int& height) const;
   
   /**
    * get image precinct sizes
    *
    * Returns the size of the precinct(s) used to encode the image, in pixels.
    * The data is returned as pointers to arrays, one width/height value for
    * each of the \a numPrecincts precincts.
    *
    * @param widths   returned array of precincts widths, in pixels
    * @param heights  returned array of precincts heights, in pixels
    * @param numPrecincts  number of precincts in image (and length of \a widths / \a heights arrays)
    */
   void getParameter_Precincts(const int*& widths, const int*& heights, int& numPrecincts) const;

   /**
    * get image progression order
    *
    * Returns the progression order of the image.
    *
    * @return the progression order
    */
   J2KProgressionOrder getParameter_ProgressionOrder() const;
   
   /**
    * get image codeblock size
    *
    * Returns the size of the codeblock used to encode the image, in pixels.
    *
    * @param width    codeblock width, in pixels
    * @param height   codeblock width, in pixels
    */
   void getParameter_CodeblockSize(int& width, int& height) const;

   /**
    * query if 9-7 wavelet used
    *
    * Returns true if the image was encoded with the 9-7 wavelet.  The 9-7
    * wavelet may give better image quality as compared to the 5-3 wavelet
    * for a given compression ratio, however the 9-7 wavelet does not support
    * lossless encoding.
    *
    * @return  true, if the 9-7 wavelet was used
    */
   bool getParameter_Wavelet97() const;

   /**
    * get number of resolution levels
    *
    * Returns the number of wavelet resolution levels encoded in the image.
    * This is equivalent to the minimum magnification value, expressed
    * as a power of two.
    *
    * @return  the number of resolution levels
    */
   lt_uint8 getNumLevels() const;

   /**
    * get metadata chunk
    *
    * This function is used to access the contents of a metadata box
    * in the JPEG 2000 image, given a UUID.  The box contents are
    * copied into a user-supplied stream.
    *
    * @param   uuid    the UUID of the box to read
    * @param   stream  stream to receive the metadata
    * @return  success or failure status code
    */
   LT_STATUS readMetadataBox(const lt_uint8* uuid, LTIOStreamInf& stream);

   /**
    * control ownership of underlying stream
    *
    * This function is used to change the ownership of the image stream.
    *
    * @param  takeOwnership    set to true to have object own the stream
    */
   void setStreamOwnership(bool takeOwnership);


   // overrides
   lt_int64 getPhysicalFileSize() const;
   const LTFileSpec& getFileSpec() const;

protected:
   friend class J2KImageReaderImp;

   // decode functions
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer,
                         const LTIScene& stripScene);
   LT_STATUS decodeEnd();

private:

   const LTFileSpec* m_ctor_fileSpec;
   LTIOStreamInf* m_ctor_stream;
   const bool m_ctor_isPersistent;
   const float m_ctor_maxBpp;

   J2KImageReaderImp* m_imp;

   // nope
   J2KImageReader(J2KImageReader&);
   J2KImageReader& operator=(const J2KImageReader&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // J2KIMAGEREADER_H
