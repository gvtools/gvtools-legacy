/* $Id: MG3CompositeImageReader.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef MG3COMPOSITEIMAGEREADER_H
#define MG3COMPOSITEIMAGEREADER_H

// lt_lib_mrsid_mrsidReaders
#include "MrSIDImageReaderBase.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class MG3Container;
class LTIMosaicFilter;
class MG3SingleImageReader;
class MG2ImageReader;
class J2KImageReader;

/**
 * reader for MrSID/MG3 images
 *
 * This class supports reading MrSID/MG3 images.
 *
 * @note MrSID/MG2 images are not supported with this class.
 */
class MG3CompositeImageReader : public MrSIDImageReaderBase
{
public:
   /**
    * constructor
    *
    * Create an MG3 reader from the given file.
    *
    * The \a imageNumber array allows for control over
    * which tiles in the image should be opened to form
    * the composite (mosaic) image.  If NULL is passed,
    * all tiles will be used.
    *
    * To determine the tiles available in the image,
    * you can use the static getCompositeImageInfo()
    * member function.
    *
    * @param fileSpec      file containing MrSID image
    * @param imageNumber   array of image tile numbers
    * @param numImages     size of \a imageNumber array
    * @param useWorldFile  incorporate world file data when reading image
    * @param memoryUsage   control memory resource usage
    * @param streamUsage   control stream resource usage
    */
   MG3CompositeImageReader(const LTFileSpec& fileSpec,
                           const lt_uint32* imageNumber,
                           lt_uint32 numImages,
                           bool useWorldFile,
                           MrSIDMemoryUsage memoryUsage,
                           MrSIDStreamUsage streamUsage);

   /**
    * constructor
    *
    * Construct an MG3 image from a stream.  (See file-based
    * constructor for details.)
    *
    * @param stream           stream containing MrSID image (may not be NULL)
    * @param imageNumber      array of image tile numbers
    * @param numImages        size of \a imageNumber array
    * @param worldFileStream  stream containing world file data (may be NULL)
    * @param memoryUsage      control memory resource usage
    * @param streamUsage      control stream resource usage
    */
   MG3CompositeImageReader(LTIOStreamInf* stream,
                           const lt_uint32* imageNumber,
                           lt_uint32 numImages,
                           LTIOStreamInf* worldFileStream,
                           MrSIDMemoryUsage memoryUsage,
                           MrSIDStreamUsage streamUsage);

   MG3CompositeImageReader(MG3Container* container,
                           const lt_uint32* imageNumber,
                           lt_uint32 numImages,
                           LTIOStreamInf* worldFileStream,
                           MrSIDMemoryUsage memoryUsage,
                           MrSIDStreamUsage streamUsage);

   virtual ~MG3CompositeImageReader();
   LT_STATUS initialize();

   LT_STATUS setStripHeight(lt_uint32 stripHeight);
   lt_uint32 getStripHeight() const;

   lt_uint8 getNumLevels() const;
   bool isLocked() const;

   void getVersion(lt_uint8& major, lt_uint8& minor, lt_uint8& tweak, char& letter) const;

   /**
    * query if is optimizable
    *
    * Returns true if and only if the image can be further compressed.
    *
    * @return true, if and only if the image may be compressed
    */
   bool isOptimizable() const;

   /**
    * tile type queries
    *
    * These functions are used to determine whether any of the tiles
    * in the image are MG2 or JPEG 2000.
    */
   /*@{*/
   bool hasMG2Data() const;
   bool hasJP2Data() const;
   /*@}*/

   void setInterruptDelegate(LTIInterruptDelegate* delegate);

   lt_int64 getPhysicalFileSize() const;

   /**
    * file format type of image tile
    */
   enum CompositeImageType
   {
      COMPOSITETYPE_MG2 = 1,
      COMPOSITETYPE_MG3 = 2,
      COMPOSITETYPE_JP2 = 3
   };

   /**
    * structure representing tiled image
    */
   struct CompositeImageInfo
   {
      /** image tile number */
      lt_uint32 imageId;

      /** x-position of tile in the mosaic */
      double xPos;

      /** y-position of tile in the mosaic */
      double yPos;

      /** type of image tile */
      CompositeImageType imageType;
   };

   /**
    * query tile information
    *
    * This function is used to collect information about the tiles in the
    * image.
    *
    * The caller takes ownership of the returned \a tileInfo array.
    *
    * @param  tileInfo  array of tile information structures to be set
    * @param  numTiles  number of tiles
    * @return status code indicating success or failure
    */
   LT_STATUS getTileInfo(CompositeImageInfo *&tileInfo, lt_uint32 &numTiles) const;

   /**
    * query tile information
    *
    * This function is used to collect information about the tiles in
    * the image contained in the given file.
    *
    * The caller takes ownership of the returned \a info array.
    *
    * @param  fileSpec  name of image to query
    * @param  tileInfo  array of tile information structures to be set
    * @param  numTiles  number of tiles
    * @return status code indicating success or failure
    */
   static LT_STATUS getCompositeImageInfo(const LTFileSpec &fileSpec,
                                          CompositeImageInfo *&tileInfo,
                                          lt_uint32 &numTiles);

   /**
    * query tile information
    *
    * This function is used to collect information about the tiles in
    * the image contained in the given stream.
    *
    * The caller takes ownership of the returned \a info array.
    *
    * @param  stream    stream containing image to query
    * @param  tileInfo  array of tile information structures to be set
    * @param  numTiles  number of tiles
    * @return status code indicating success or failure
    */
   static LT_STATUS getCompositeImageInfo(LTIOStreamInf &stream,
                                          CompositeImageInfo *&tileInfo,
                                          lt_uint32 &numTiles);

   static LT_STATUS getCompositeImageInfo(const MG3Container &container,
                                          CompositeImageInfo *&tileInfo,
                                          lt_uint32 &numTiles);


//protected: // only used be MG3Optimizer
   bool getReaderScene(lt_uint32 imageIndex,
                       const LTIScene &scene,
                       LTIScene &readerScene) const;

protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer,
                         const LTIScene& stripScene);
   LT_STATUS decodeEnd();

   LT_STATUS init(void);

   virtual LT_STATUS createMG2Reader(lt_uint32 imageNumber,
                                       MG2ImageReader *&mg2Reader);
   virtual LT_STATUS createMG3Reader(lt_uint32 imageNumber,
                                       MG3SingleImageReader *&mg3Reader);
   virtual LT_STATUS createJP2Reader(lt_uint32 imageNumber,
                                       J2KImageReader *&jp2Reader);
                              
   virtual LT_STATUS updateMemoryModel();

#ifndef DOXYGEN_EXCLUDE
   struct Data
   {
      ~Data(void);
      Data(const LTFileSpec *fileSpec,
           LTIOStreamInf *stream,
           MG3Container* container,
           lt_uint32 numImages,
           const lt_uint32* imageNumbers,
           LTIOStreamInf* worldFileStream);
 
      MG3Container* m_container;
      bool m_ownContainer;   

      lt_uint32 m_numImages;
      lt_uint32* m_imageNumbers;
      LTIImageReader** m_readers;
      LTIImageStage** m_images;
      CompositeImageType *m_tileType;
      LTIMosaicFilter* m_mosaic;

      LTFileSpec* m_fileSpec;
      LTIOStreamInf* m_stream;
      bool m_ownStream;
      LTIOStreamInf* m_worldFileStream;

      bool m_isLockedComposite;
      lt_uint8 m_numLevelsComposite;
      bool m_isOptimizableComposite;
      bool m_hasMG2Composite;
      bool m_hasJP2Composite;
   };
#endif
   Data *m_dat;

private:
   typedef MrSIDImageReaderBase Super;

   // nope
   MG3CompositeImageReader(const MG3CompositeImageReader&);
   MG3CompositeImageReader& operator=(const MG3CompositeImageReader&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // MG3COMPOSITEIMAGEREADER_H
