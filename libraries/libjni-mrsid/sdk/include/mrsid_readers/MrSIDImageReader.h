/* $Id: MrSIDImageReader.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef MRSIDIMAGEREADER_H
#define MRSIDIMAGEREADER_H

// lt_lib_mrsid_mrsidReader
#include "MrSIDImageReaderBase.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTFileSpec;
class LTIOStreamInf;
class MG2ImageReader;
class MG3SingleImageReader;
class MG3CompositeImageReader;
class MG3Container;


/**
 * reader for MrSID images (MG2 and MG3)
 *
 * This class supports reading MrSID/MG2 and MrSID/MG3 images, including
 * composite MG3 images.
 */
class MrSIDImageReader : public MrSIDImageReaderBase
{
public:
   /**
    * constructor
    *
    * Construct a MrSID reader from the given file.
    *
    * @param  fileSpec      file containing MrSID image
    * @param  useWorldFile  incorporate world file data when reading image
    * @param  memoryUsage   control memory resource usage
    * @param  streamUsage   control stream resource usage
    */
   MrSIDImageReader(const LTFileSpec& fileSpec,
                    bool useWorldFile = false,
                    MrSIDMemoryUsage memoryUsage=MRSID_MEMORY_USAGE_DEFAULT,
                    MrSIDStreamUsage streamUsage=MRSID_STREAM_USAGE_DEFAULT);

   /**
    * constructor
    *
    * Construct a MrSID reader from the given stream.
    *
    * @param  stream        stream containing MrSID image (may not be NULL)
    * @param  worldFileStream  stream containing world file data (may be NULL)
    * @param  memoryUsage   control memory resource usage
    * @param  streamUsage   control stream resource usage
    */
   MrSIDImageReader(LTIOStreamInf* stream,
                    LTIOStreamInf* worldFileStream = 0,
                    MrSIDMemoryUsage memoryUsage=MRSID_MEMORY_USAGE_DEFAULT,
                    MrSIDStreamUsage streamUsage=MRSID_STREAM_USAGE_DEFAULT);

   virtual ~MrSIDImageReader();

   virtual LT_STATUS initialize();

   /**
    * number of levels
    * 
    * Return the number of resolution levels in the image.  (This is similar to querying
    * getMinMagnificaton(), but expressed as an integer.)
    *
    * @return the number of levels in the image
    */
   virtual lt_uint8 getNumLevels() const;

   /**
    * query for image encryption
    * 
    * Return true if the image is password-protected.
    *
    * @return true, if and only if the image is encrypted
    */
   virtual bool isLocked() const;

   /**
    * query for file format version
    * 
    * This functions returns detailed version information for the
    * MG2 or MG3 file.
    *
    * @param major the major version number
    * @param minor the minor version number
    * @param tweak the reversion number
    * @param letter the format letter (MG3 only)
    */
   virtual void getVersion(lt_uint8& major, lt_uint8& minor, lt_uint8& tweak, char& letter) const;

   virtual lt_int64 getPhysicalFileSize() const;

   virtual LT_STATUS setStripHeight(lt_uint32 stripHeight);
   virtual lt_uint32 getStripHeight() const;

   // not for the faint of heart
   MG2ImageReader* getMG2Reader() const;
   MG3SingleImageReader* getMG3SingleReader() const;
   MG3CompositeImageReader* getMG3CompositeReader() const;

   virtual LT_STATUS projectPointAtMag(double upperLeft,
                                       double mag,
                                       double& newUpperLeft) const;
   virtual LT_STATUS projectDimAtMag(double dim,
                                     double mag,
                                     double& newDim) const;

   virtual LT_STATUS getDimsAtMag(double mag,
                                  lt_uint32& width,
                                  lt_uint32& height) const;

   void setProgressDelegate(LTIProgressDelegate* delegate);
   void setInterruptDelegate(LTIInterruptDelegate* delegate);

protected:
   virtual LT_STATUS decodeBegin(const LTIScene& scene);
   virtual LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer,
                                 const LTIScene& stripScene);
   virtual LT_STATUS decodeEnd();

   virtual LT_STATUS createMG2Reader(void);
   virtual LT_STATUS createMG3Reader(void);

#ifndef DOXYGEN_EXCLUDE
   struct Data
   {
      ~Data(void);
      Data(const LTFileSpec *fileSpec,
           LTIOStreamInf *stream,
           MG3Container* container,
           LTIOStreamInf* worldFileStream);
   
      LTIOStreamInf* m_stream;
      LTFileSpec* m_fileSpec;
      bool m_ownStream;
      LTIOStreamInf* m_worldFileStream;
      bool m_worldFileStreamOwned;
      MG3Container* m_container;
      MrSIDImageReaderBase *m_mrsidReader;
      MG3CompositeImageReader* m_mg3CompositeReader;
      MG3SingleImageReader* m_mg3SingleReader;
      MG2ImageReader* m_mg2Reader;
   };
#endif
   Data *m_dat;

private:
   typedef MrSIDImageReaderBase Super;

   // nope
   MrSIDImageReader(const MrSIDImageReader&);
   MrSIDImageReader& operator=(const MrSIDImageReader&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // MRSIDIMAGEREADER_H
