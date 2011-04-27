/* $Id: lti_imageReader.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_IMAGEREADER_H
#define LTI_IMAGEREADER_H

// lt_lib_mrsid_core
#include "lti_imageStage.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


/**
 * abstract class for implementing an image reader
 *
 * The LTIImageReader abstract class extends the LTIImageStage so that it can
 * be used as a decoder for an image format, i.e. the "end" of an image
 * pipeline.  This is the base class for such classes as the TIFFImageReader,
 * the MrSIDImageReader, etc.
 */
class LTIImageReader : public LTIImageStage
{
public:
   /**
    * constructor
    */
   LTIImageReader();

   /**
    * destructor
    */
   virtual ~LTIImageReader();

	/**
    * clone the image reader
    *
    * This function creates a new image reader of the same type with the same
    * initialization parameters.  The caller must call initialize() on the new
    * reader prior to using it.
    *
    * The default implementation returns NULL, indicating cloning is not
    * supported.  Derived classes may choose to override this.
	 *
	 *	@return  a pointer to the new reader, or NULL if not image can not be cloned
	*/
	virtual LTIImageReader* duplicate();

   virtual lt_uint32 getStripHeight() const;
   virtual LT_STATUS setStripHeight(lt_uint32 stripHeight);

   virtual lt_int64 getPhysicalFileSize() const = 0;

   virtual lt_int64 getEncodingCost(const LTIScene& scene) const;

   virtual bool getReaderScene(const LTIScene &decodeScene,
                               LTIScene &readerScene) const;

private:
   lt_uint32 m_stripHeight;


   // nope
   LTIImageReader(LTIImageReader&);
   LTIImageReader& operator=(const LTIImageReader&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_IMAGEREADER_H
