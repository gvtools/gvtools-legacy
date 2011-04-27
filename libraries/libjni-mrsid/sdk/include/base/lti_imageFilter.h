/* $Id: lti_imageFilter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_IMAGEFILTER_H
#define LTI_IMAGEFILTER_H

// lt_lib_mrsid_core
#include "lti_imageStage.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

/**
 * abstract class for implementing an image transform
 *
 * The LTIImageFilter abstract class extends the LTIImageStage so that it can
 * be used as an intermediate stage in an image pipeline.  This is the base
 * class for such classes as the colorspace transformer, image cropper, etc.
 */
class LTIImageFilter : public LTIImageStage
{
public:
   /**
    * constructor
    *
    * This is the constructor for a pipeline stage.  The previous image stage
    * in the pipeline is passed in to this stage.
    *
    * @param  previousStage  the previous image stage in the pipeline 
    * @param  takeOwnership  if true, this object will delete the parent stage
    *                        when required 
    */
   LTIImageFilter(LTIImageStage* previousStage, bool takeOwnership);

   /**
    * destructor
    */
   virtual ~LTIImageFilter();

   /**
    * initializer
    */
   virtual LT_STATUS initialize();

   virtual lt_uint32 getStripHeight() const;
   virtual LT_STATUS setStripHeight(lt_uint32 stripHeight);

   virtual lt_int64 getPhysicalFileSize() const;

   virtual lt_int64 getEncodingCost(const LTIScene& scene) const;

   virtual const lt_utf8* getName() const;

   virtual LT_STATUS projectPointAtMag(double upperLeft,
                                       double mag,
                                       double& newUpperLeft) const;

   virtual LT_STATUS projectDimAtMag(double dim,
                                     double mag,
                                     double& newDim) const;

   virtual bool isSelective() const;

   virtual LT_STATUS getDimsAtMag(double mag,
                                  lt_uint32& width,
                                  lt_uint32& height) const;

   virtual bool getReaderScene(const LTIScene &decodeScene,
                               LTIScene &readerScene) const;
protected:
   /** true iff this stage should delete the previous stage */
   bool m_takeOwnership;

   /**
    * get previous image in pipeline
    *
    * Returns the previous image in the pipeline.
    *
    * This function is virtual so that derived classes may override it if
    * needed.  This is useful for classes like BMPImageWriter which need to
    * silently insert their own transform stages into the pipeline.
    *
    * @return pointer to the previous stage (will not be NULL)
    */
   virtual LTIImageStage* getPreviousStage() const;

private:
   LTIImageStage* m_previousStage;

   // nope
   LTIImageFilter(LTIImageFilter&);
   LTIImageFilter& operator=(const LTIImageFilter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_IMAGEFILTER_H
