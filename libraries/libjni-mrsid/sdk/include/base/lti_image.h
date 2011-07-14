/* $Id: lti_image.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_IMAGE_H
#define LTI_IMAGE_H

// lt_lib_mrsid_core
#include "lti_types.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


class LTIPixel;
class LTIMetadataDatabase;
class LTIGeoCoord;
class LTIPixelLookupTable;


/**
 * abstract class representing an image
 *
 * The LTIImage abstract class represents the basic properties of an image,
 * including dimensions, data type, background color, etc.   
 *
 * LTIImage does not support extraction of pixels (decoding); that
 * functionality is provided by the derived class LTIImageStage.
 */
class LTIImage
{
public:
   /**
    * constructor
    *
    * Constructs an LTIImage object with no data members (image properties)
    * set.
    *
    * The initialize() method should be called immediately after the object
    * is constructed.
    */
   LTIImage();

   /**
    * destructor
    */
   virtual ~LTIImage();

   /**
    * initialize the image object
    *
    * Derived classes should override this function.  This function must 
    * do the following (in order):
    *
    * \li call the initialize() function of the parent class
    * \li call the LTIImage::setPixelProps() function
    *     to set the required datatype, colorspace, and number of bands
    * \li call the LTIImage::setDimensions() function
    *     to set the required image width and height
    * \li call any optional "set" functions in LTIImage
    *     to set the additional image properties for the derived image type
    *
    * Status codes must be checked and handled appropriately after each of
    * these operations.
    *
    * This function is to be called by users immediately after the object is
    * constructed.
    *
   * @return status code indicating success or failure
    */
   virtual LT_STATUS initialize();

   /**
    * get image width
    *
    * This function returns the width of the image, in pixels.
    *
    * @return the image width
    */
   virtual lt_uint32 getWidth() const;

   /**
    * get image height
    *
    * This function returns the height of the image, in pixels.
    *
    * @return the image height
    */
   virtual lt_uint32 getHeight() const;
   
   /**
    * get image width and height at given magnification
    *
    * This function returns the width and height of the image, in pixels,
    * relative to the given magnification.
    *
    * @param  mag    magnification to determine width at 
    * @param  width  the image width at the magnification 
    * @param  height the image height at the magnification 
    * @return status code indicating success or failure
    */
   virtual LT_STATUS getDimsAtMag(double mag,
                                  lt_uint32& width,
                                  lt_uint32& height) const;

   /**
    * get number of bands
    *
    * This function returns the number of bands (colors) in the image.  This
    * value is closely related to the colorspace of the image.
    *
    * This function is equivalent to getPixelProps().getNumBands().
    *
    * @return the number of bands in the image
    */
   virtual lt_uint16 getNumBands() const;

   /**
    * get colorspace
    *
    * This function returns the colorspace of the image, e.g. RGB or greyscale.
    *
    * This function is equivalent to getPixelProps().getColorSpace().
    *
    * @return the colorspace of the image
    */
   virtual LTIColorSpace getColorSpace() const;

   /**
    * get data type
    *
    * This function returns the data type of the samples in the image, e.g.
    * unsigned byte or 32-bit float.
    *
    * This function is equivalent to getPixelProps().getDataType().
    *
    * @return the datatype of the image
    */
   virtual LTIDataType getDataType() const;

   /**
    * get the pixel properties of the image
    *
    * This function returns an object which describes the basic properties
    * of a pixel in the image.
    *
    * @return the pixel properties of the image
    */
   virtual const LTIPixel& getPixelProps() const;

   /**
    * get the values of the background pixel
    *
    * This function returns a pointer to an object containing the
    * values of the background pixel of the image.  If the pointer
    * is NULL, no background color has been set for the image.
    *
    * @return pointer to the background pixel 
    */
   virtual const LTIPixel* getBackgroundPixel() const;

   /**
    * get the values of the "no data" (transparency) pixel
    *
    * This function returns a pointer to an object containing the
    * values of the no data pixel of the image.  If the pointer
    * is NULL, no transparency color has been set for the image.
    *
    * @return pointer to the no data pixel 
    */
   virtual const LTIPixel* getNoDataPixel() const;

   /**
    * get the color lookup table, if any
    *
    * This function returns a pointer the color lookup table, used for
    * indexed or palletized images.  Will return NULL if no table
    * is used.
    *
    * @return pointer to the pixel lookup table
    */
   virtual const LTIPixelLookupTable* getPixelLookupTable() const;

   /**
    * get the minimum dynamic range value of image
    *
    * This function returns the pixels of minimum value of the 
    * dynamic range of the image.
    *
    * @return  pixel object with the minimum dynamic range values
    */
   virtual const LTIPixel& getMinDynamicRange() const;

   /**
    * get the maximum dynamic range value of image
    *
    * This function returns the pixels of maximum value of the 
    * dynamic range of the image.
    *
    * @return  pixel object with the maximum dynamic range values
    */
   virtual const LTIPixel& getMaxDynamicRange() const;

   /**
    * check if the range is "complete" or not
    *
    * This function returns true if the dynamic range min/max values for the
    * image correspond to the full range of the underlying datatype.
    *
    * @return  true if range spans the datatype's range
    */
   bool isNaturalDynamicRange() const;
   
   /**
    * get the geographic coordinates of the image
    *
    * This function returns the geographic coordinates of the image.
    *
    * Note that if the image has no explicit geographic coordinate information,
    * e.g. stored within the metadata of a file, the geographic coordinates
    * are implicitly set (upperleft is (0,-height), resolution is (1,-1))
    *
    * @return a pointer to an object containing the geographic coordinates
    */
   virtual const LTIGeoCoord& getGeoCoord() const;

   /**
    * is geo information "real" or not
    *
    * This function returns true if the geo information is implicit, i.e.
    * the source image did not have native geo coord info.
    *
    * @return true if and only if the geo information is not "real"
    */
   virtual bool isGeoCoordImplicit() const;

   /**
    * get the metadata associated with the image
    *
    * This function returns a reference to the object containing the
    * metadata associated with the image.
    *
    * @return a reference to the metadata database
    */
   virtual const LTIMetadataDatabase& getMetadata() const;

   /**
    * get a modifiable reference to the metadata associated with the image
    *
    * This function returns a reference to the object containing the
    * metadata associated with the image.  This function returns
    * a non-const reference, so it may be used by derived classes to
    * set metadata on the image.
    *
    * @return a reference to the metadata database
    */
   virtual LTIMetadataDatabase& getMetadataRef() const;

   /**
    * get the minimum magnification
    *
    * This function returns the minimum magnification
    * of the image.  Images that do not support "zooming out" will return
    * 1.0; images that contain "overviews", e.g. MrSID, will return a
    * value greater than 1.0.
    *
    * @return the minimum magnification
    */
   virtual double getMinMagnification() const;

   /**
    * get the maximum magnification
    *
    * This function returns the maximum magnification
    * of the image.  Images that do not support "zooming in" will return
    * 1.0; images that support "res-up", e.g. MrSID, will return a
    * value less than 1.0.
    *
    * @return the maximum magnification
    */
   virtual double getMaxMagnification() const;

   /**
    * get the physical (disk) size of the image
    *
    * Returns the number of bytes in the actual file (or stream) to the extent
    * possible and practical.  Note this value may be substantially different
    * than the "nominal" image size returned by getNominalImageSize(), e.g.
    * due to compression or sparse mosaics.
    *
    * This number should not be considered an exact representation:
    * \li if the actual value cannot be known, e.g. for streamed data, a value of 0
    *     will be returned
    * \li the returned value may or may not include bytes due to subsidiary files, e.g.
    *     BBB headers or World files
    * \li for "filter" image classes that perform some operation on an underlying image,
    *     the value returned is typically just the value of the underlying image
    * \li for "mosaic" image classes that wrap one or more underlying image
    *     classes, the value returned is typically the sum of the child images
    *
    * @return the physical size of the image, in bytes
    */
   virtual lt_int64 getPhysicalFileSize() const = 0;

   /**
    * get the nominal size of the image
    *
    * Returns number of bytes of actual data in the image, e.g. as if the image were
    * a raw file.  This value is simply the product of:
    * \li image width,
    * \li image height,
    * \li samples per pixel, and
    * \li bytes per sample.
    *
    * Note this value may be substantially different
    * than the "physical" image size returned by getPhysicalFileSize(), e.g.
    * due to compression or sparse mosaics.
    *
    * @return the nominal size of the image, in bytes
    */
   lt_int64 getNominalImageSize() const;

   /**
    * get the "name" of the image
    *
    * Returns a string representing the "name" of the image.  The name
    * corresponds roughly to the underlying file used, and should only
    * be used for debugging purposes.
    *
    * @return the name of the image
    */
   virtual const lt_utf8* getName() const;

   /**
    * get position of a named point
    *
    * Returns the (x,y) position of the given named point.
    *
    * @param  position  the position to be returned 
    * @param  x         the x-position of the point 
    * @param  y         the y-position of the point 
    */
   void getGeoPoint(LTIPosition position, double& x, double& y) const;

   /**
    * return new background pixel
    *
    * Creates and returns a new pixel of the correct background color for the
    * image.  If no background color has been set, a new black pixel will be
    * returned (unless the image is CMYK, in which case a white pixel will be
    * returned).
    *
    * The caller takes ownership of the returned pixel.
    *
    * @return a new background pixel
    */
   LTIPixel* createBackgroundPixel() const;

   /**
    * @name Projection functions
    */
   /*@{*/

  /**
    * project a value to a given magnification
    *
    * Given a value, such as a point, compute and return the value when
    * projected to the given magnification.
    *
    * @param  upperLeft     the value to be scaled  
    * @param  mag           the amount to scale by  
    * @param  newUpperLeft  the scaled value 
    * @return status indicating success or failure
    */
   virtual LT_STATUS projectPointAtMag(double upperLeft,
                                       double mag,
                                       double& newUpperLeft) const;

   /**
    * project a dimension to a given magnification
    *
    * Given a value, such as a width, compute and return the value when
    * projected to the given magnification.
    *
    * @param  dim     the value to be scaled  
    * @param  mag     the amount to scale by  
    * @param  newDim  the scaled value 
    * @return status indicating success or failure
    */
   virtual LT_STATUS projectDimAtMag(double dim,
                                     double mag,
                                     double& newDim) const;

   /*@}*/

   /**
    * check if image supports "random access" decoding
    *
    * Some formats, notably JPEG, do not support "selective" decoding.
    * That is, they require that scenes being decoding must march
    * in order down the scanlines of the image.  Formats like TIFF
    * and MrSID, however, are "selective": any scene can be requested
    * at any time.
    *
    * @return true if and only if the image supports arbitrary scene requests
    */
   virtual bool isSelective() const;
   
   /**
    * set standard metadata fields
    *
    * This function is used to populate the standard metadata fields for the
    * image, e.g. WIDTH, HEIGHT, COLORSPACE, etc.
    *
    * @return status code indicating success or failure
    */
   LT_STATUS setClassicalMetadata();

protected:
   /**
    * set pixel properties of the image
    *
    * This function is used to set the pixel properties of the image, namely
    * the datatype, colorspace, and number of bands.
    *
    * Derived classes are \em required to call this function from within their
    * initialize() method.
    *
    * @param pixelProps  the basic pixel properties of the image (number of
    *                    bands, colorspace, datatype)
    * @return status code indicating success or failure
    */
   LT_STATUS setPixelProps(const LTIPixel& pixelProps);

   /**
    * set width and height of the image
    *
    * This function is used to set the dimensions (width and height) of the image.
    *
    * Derived classes are \em required to call this function from within their
    * initialize() method.
    *
    * @param  width       the image width, in pixels
    * @param  height      the image height, in pixels
    * @return status code indicating success or failure
    */
   LT_STATUS setDimensions(lt_uint32 width,
                           lt_uint32 height);

   /**
    * set the background color of the image
    *
    * This function is used to set the background color of the image.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the background color is not set, or if NULL
    * is passed in, a background color of black (sample values of 0) will
    * be used.  (Exception: for CMYK images, the background is set to white.)
    *
    * @param backgroundPixel  the data for the background color
    * @return status code indicating success or failure
    */
   LT_STATUS setBackgroundPixel(const LTIPixel* backgroundPixel);

   /**
    * set the "no data" (transparency) color of the image
    *
    * This function is used to set the "no data" or transparency color of the
    * image.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the transparency color is not set, or if NULL
    * is passed in, the image will be assumed to have no transparent pixels.
    *
    * @param nodataPixel  the data for the transparency color
    * @return status code indicating success or failure
    */
   LT_STATUS setNoDataPixel(const LTIPixel* nodataPixel);

   /**
    * set the color lookup table
    *
    * This function is used to set the color used lookup table, used
    * for indexed color (palletized) images.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the lookup table is not set, or if NULL
    * is passed in, the image will be assumed to have no lookup table.
    *
    * The LTIImageReader makes a local copy of the lookup table.
    *
    * @param  pixelLookupTable  pointer to the lookup table (or NULL)
    * @return status code indicating success or failure
    */
   LT_STATUS setPixelLookupTable(const LTIPixelLookupTable* pixelLookupTable);

   /**
    * set the dynamic range of the image
    *
    * This function is used to set the dynamic range of the image.  The
    * dynamic range consists of the minimum and maximum value for a given
    * sample.
    *
    * If both the minimum and maximum are NULL, the natural
    * range of the sample datatype will be used.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the dynamic range is not set, the natural
    * minimum and maximum of the datatype of the sample is used.
    *
    * @param drmin       the minimum dynamic range value
    * @param drmax       the maximum dynamic range value
    * @return status code indicating success or failure
    */
   LT_STATUS setDynamicRange(const LTIPixel& drmin,
                             const LTIPixel& drmax);

   /**
    * set the dynamic range of the image
    *
    * This function is used to set the dynamic range of the image.  The
    * dynamic range consists of the minimum and maximum value for a given
    * sample.  This function sets these values to the natural
    * range of the sample datatype.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the dynamic range is not set, the natural
    * minimum and maximum of the datatype of the sample is used.
    *
    * @return status code indicating success or failure
    */
   LT_STATUS setDefaultDynamicRange();

   /**
    * set the minimum and maximum magnifications of the image properties
    *
    * This function is used to set the minimum and maximum magnifications
    * of the image.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the minimum and maximum magnifications are not
    * set, values of 1.0 are used (indicating the image may not be scaled
    * larger or smaller).
    *
    * @param minMag  the minimum magnification of the image
    * @param maxMag  the maximum magnification of the image
    * @return status code indicating success or failure
    */
   LT_STATUS setMagnification(double minMag,
                              double maxMag);

   /**
    * set the geographic coordinates of the image
    *
    * This function is used to set the geographic coordinates of the image.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the geographic coordinates are not set, the
    * default position is assumed.
    *
    * @param geoCoord    the geographic coordinate information
    * @return status code indicating success or failure
    */
   LT_STATUS setGeoCoord(const LTIGeoCoord& geoCoord);

   /**
    * set the geographic coordinates of the image
    *
    * This function is used to set the geographic coordinates of the image to
    * a reasonable default, when there are no other geographic coordinates to
    * use.
    *
    * The default coordinates used are:
    * \li upperleft: (0.0, height-1.0)
    * \li resolution: (1.0, -1.0)
    * \li rotation: (0.0, 0.0)
    *
    * Calling this function will cause isGeoCoordImplicit() to return true.
    *
    * @return status code indicating success or failure
    */
   LT_STATUS setDefaultGeoCoord();

   /**
    * set the properties of an image based on another image
    *
    * This function is used to set all the properties of an image
    * to be the same as those of another image.
    *
    * This function can be used by image filter classes, as a convenience
    * function instead of explicitly calling all the various "set" functions.
    *
    * @param  image  the source image properties to use for this image
    * @return status code indicating success or failure
    */
   LT_STATUS setAllProperties(const LTIImage& image);

   /**
    * set the "name" of the image
    *
    * Sets a string representing the "name" of the image.  The name
    * should correspond roughly to the underlying file used, and should only
    * be used for debugging purposes.
    *
    * @param  name  the name of this image
    * @return status code indicating success or failure
    */
   virtual LT_STATUS setName(const lt_utf8* name);

   /**
    * set image to support "random access"
    *
    * This function is used to indicate the image supports "selective"
    * decoding.  See isSelective() for details.
    *
    * By default, all images support selective decoding.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.
    *
    * @param  enable  set to true if and only if the image supports selective encoding
    */
   void setIsSelective(bool enable);

   LT_STATUS inferPrecisionFromMetadata(LTIDataType,lt_uint8&);

private:
   lt_uint32 m_width;
   lt_uint32 m_height;
   LTIPixel* m_pixelProps;
   LTIPixel* m_backgroundPixel;
   LTIPixel* m_nodataPixel;
   LTIPixel* m_drminPixel;
   LTIPixel* m_drmaxPixel;
   double m_minMag;
   double m_maxMag;
   LTIGeoCoord* m_geoCoord;
   LTIMetadataDatabase* m_metadata;
   LTIPixelLookupTable* m_pixelLookupTable;
   lt_utf8* m_name;
   bool m_isSelective;
   bool m_geoCoordImplicit;

   // nope
   LTIImage(LTIImage&);
   LTIImage& operator=(const LTIImage&);
};



LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_IMAGE_H
