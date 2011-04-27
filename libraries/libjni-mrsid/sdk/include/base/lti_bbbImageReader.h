/* $Id: lti_bbbImageReader.h 3539 2006-01-09 12:23:20Z nacho $ */
/* //////////////////////////////////////////////////////////////////////////
//                                                                         //
// This code is Copyright (c) 2004 LizardTech, Inc, 1008 Western Avenue,   //
// Suite 200, Seattle, WA 98104.  Unauthorized use or distribution         //
// prohibited.  Access to and use of this code is permitted only under     //
// license from LizardTech, Inc.  Portions of the code are protected by    //
// US and foreign patents and other filings. All Rights Reserved.          //
//                                                                         //
////////////////////////////////////////////////////////////////////////// */

#ifndef LTI_BBB_IMAGE_READER_H
#define LTI_BBB_IMAGE_READER_H

// lt_lib_mrsid_core
#include "lti_rawImageReader.h"

// system
#include <stdio.h>  // for FILE*

LT_BEGIN_NAMESPACE(LizardTech)

class LTIOStreamInf;
class LTIReusableBSQBuffer;

/**
 * read an image from a BBB file
 *
 * This class provides support for reading BBB files, i.e. a raw file with a
 * BIL/BIP/BSQ-style header.
 *
 */
class LTIBBBImageReader : public LTIRawImageReader
{
public:
   /**
    * constructor
    *
    * This function creates an image from a BBB file.
    *
    * @param fileSpec the image file to read from
    */
   LTIBBBImageReader(const LTFileSpec& fileSpec);

   /**
    * constructor
    *
    * This function creates an image from a BBB file.
    *
    * @param file the image file to read from
    */
   LTIBBBImageReader(const char* file);

   /**
    * destructor
    */
   ~LTIBBBImageReader();

   /**
    * intializer
    */
   LT_STATUS initialize();

private:
   LT_STATUS checkWorldFile(LTIGeoCoord*&);
   LT_STATUS doImagineMetadata();

   LT_STATUS readHeaderFile();
   LT_STATUS nextLine();
   LT_STATUS readInt(int&);
   LT_STATUS readDouble(double&);
   LT_STATUS readString(char*);

   class HeaderData;
   HeaderData* m_header;

   FILE* m_fp;

   LTIEndian m_endian;

   int m_bandgapbytes;
   int m_bandrowbytes;
   int m_totalrowbytes;
   int m_skipbytes;

   bool m_hasUlxmap;
   bool m_hasUlymap;
   bool m_hasXdim;
   bool m_hasYdim;

   double m_ulxmap;
   double m_ulymap;
   double m_xdim;
   double m_ydim;

   bool m_bFromImagine; //indicates whether to display Imagine Metadata

   //Imagine header data
   char* m_projname;
   char* m_sphereName;
   char* m_units;
   int m_proZone;
   double* m_projParams;
   double m_sphereMajor;
   double m_sphereMinor;
   double m_sphereEccentricitySquared;
   double m_sphereRadius;

   // nope
   LTIBBBImageReader();
   LTIBBBImageReader(LTIBBBImageReader&);
   LTIBBBImageReader& operator=(const LTIBBBImageReader&);
};

LT_END_NAMESPACE(LizardTech)

#endif // LTI_BBB_IMAGE_READER_H
