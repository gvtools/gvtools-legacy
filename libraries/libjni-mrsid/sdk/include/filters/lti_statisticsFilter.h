/* $Id: lti_statisticsFilter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_STATISTICSFILTER_H
#define LTI_STATISTICSFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

/**
 * collects basic numeric statistics about the image's samples
 *
 * This class collects basic numeric statistics about the image's samples.  The data
 * collected includes the minimum, maximum, mean, and standard deviation of
 * the samples for each band.
 *
 * This filter does not change the image or its properties in any way.
 */
class LTIStatisticsFilter : public LTIImageFilter
{
public:
   /**
    * constructor
    *
    * Creates a pass-through image stage 
    *
    * The \a edgePadding argument is used to specify the distance (in pixels)
    * between the watermark and the edge of the base image specified by the \a
    * position argument.  (This argument is ignored if LTI_POSITION_CENTER is
    * used.)
    *
    * @param  sourceImage     the base image
    * @param takeOwnership    set to true to have the filter delete the \a sourceImage
    */
   LTIStatisticsFilter(LTIImageStage* sourceImage,
                       bool takeOwnership);
   virtual ~LTIStatisticsFilter();
   virtual LT_STATUS initialize();

   void getStats(const double*& minPixel,
                 const double*& maxPixel,
                 const double*& meanPixel,
                 const double*& stdDeviation,
                 lt_uint32& numPixels) const;

   void clearStats();

protected:
   LT_STATUS decodeBegin(const LTIScene& scene);
   LT_STATUS decodeEnd();
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);

private:
   typedef LTIImageFilter Super;

   void updateStats();

   lt_uint32 m_numPixels;
   double* m_minPixel;
   double* m_maxPixel;
   double* m_sumPixel;
   double* m_sumSquaredPixel;
   double* m_meanPixel;
   double* m_variancePixel;
   double* m_stdDeviationPixel;
   bool m_firstTime;

   // nope
   LTIStatisticsFilter(const LTIStatisticsFilter&);
   LTIStatisticsFilter& operator=(const LTIStatisticsFilter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_STATISTICSFILTER_H
