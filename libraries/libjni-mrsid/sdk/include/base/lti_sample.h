/* $Id: lti_sample.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_SAMPLE_H
#define LTI_SAMPLE_H

// lt_lib_mrsid_core
#include "lti_types.h"


#if defined(LT_COMPILER_MS)
	#pragma warning(push,4)
#endif

LT_BEGIN_NAMESPACE(LizardTech)

/**
 * basic properties of a sample
 *
 * This class stores the basic properties of a sample: the color and the
 * datatype.  A set of one or more (possibly dissimilar) LTISample objects is
 * used in the representation of a pixel (LTIPixel).
 */
class LTISample
{
public:
   /**
    * default constructor
    *
    * This constructor creates an LTISample object with the given
    * properties.
    *
    * @param  color       the sample's color
    * @param  dataType    the datatype of the sample
    * @param  data        initial data for sample value (defaults to 0)
    */
   LTISample(LTIColor color,
             LTIDataType dataType,
             const void* data=0);

   LTISample();

   /**
    * copy constructor
    */
   LTISample(const LTISample&);

   /**
    * destructor
    */
   virtual ~LTISample();

   /**
    * assignment operator
    */
   virtual LTISample& operator=(const LTISample&);

   /**
    * equality operator
    */
   bool operator==(const LTISample&) const;

   /**
    * equality operator
    */
   bool operator!=(const LTISample&) const;

   /**
    * sets the precision of the sample
    *
    * Sets the precision of the sample.  By default, the number of bits of
    * precision is equal to the number of bits in the underlying datatype.
    *
    * @note The data is assumed to be justified to the least significant bit
    * of the word.
    *
    * @param  precision  the number of bits
    */
   void setPrecision(lt_uint32 precision);

   /**
    * returns the precision of the sample
    *
    * Returns the precision of the sample.
    *
    * @return  the number of bits used in the datatype
    */
   lt_uint32 getPrecision() const;

   /**
    * get the sample datatype
    *
    * This function returns the datatype of the sample.
    *
    * @return the datatype of the sample
    */
   LTIDataType getDataType() const;

   /**
    * get the color
    *
    * This function returns the color of the sample.
    *
    * @return the color of the sample
    */
   LTIColor getColor() const;

   /**
    * get the size of a sample
    *
    * This function returns the size of the sample, in bytes.
    *
    * This is equivalent to calling LTITypes::getNumBytes( getDataType() ).
    *
    * @return the number of bytes in the sample
    */
   lt_uint32 getNumBytes() const;

   /**
    * returns status code comparing two samples
    *
    * Returns status code comparing two samples.  This is just a different
    * version of operator==, which returns a status code instead of a bool.
    *
    * @param   sample  the sample to compare this sample to
    * @return  a specific code indicating if impedance matches
    */
   LT_STATUS checkImpedance(const LTISample& sample) const;

   /**
    * returns status code indicating if supported SDK type
    *
    * Returns status code indicating if this sample type is one of the
    * normally supported SDK types, e.g. uint8/uint16, gray/RGB/CMYK, etc.
    *
    * @return  a specific code indicating if sample type supported
    */
   LT_STATUS checkSupported() const;

   /**
    * sets the sample value maximum
    *
    * Sets the sample's value to the datatype's maximum.
    */
   void setValueToMin();

   /**
    * sets the sample value minimum
    *
    * Sets the sample's value to the datatype's minimum.
    */
   void setValueToMax();

   /**
    * @name Sample value functions
    */
   /*@{*/

   /**
    * sets the sample's value to the value given
    *
    * Sets the sample's value to the value given.  The \c addr parameter is
    * assumed to point to a value of the proper datatype after appropriate
    * casting.
    *
    * @param  addr  the address of the value to set the sample to
    */
   void setValueAddr(const void* addr);

   /**
    * sets the sample's value to the value given
    *
    * Sets the sample's value to the value given.  The sample is assumed to
    * be known to have the UINT8 datatype.
    *
    * @param  value  the value to set the sample to
    */
   void setValueUint8(lt_uint8 value);

   /**
    * sets the sample's value to the value given
    *
    * Sets the sample's value to the value given.  The sample is assumed to
    * be known to have the UINT16 datatype.
    *
    * @param  value  the value to set the sample to
    */
   void setValueUint16(lt_uint16 value);

   /**
    * sets the sample's value to the value given
    *
    * Sets the sample's value to the value given.  The sample is assumed to
    * be known to have the FLOAT32 datatype.
    *
    * @param  value  the value to set the sample to
    */
   void setValueFloat32(float value);
   
   /**
    * returns the address of the sample's value
    *
    * Returns the address of the sample's value.  The caller must cast the
    * pointer to the appropriate type before using.
    *
    * @return  the address of the sample's value
    */
   const void* getValueAddr() const;

   /**
    * returns the sample's value
    *
    * Returns the sample's value.  The sample is assumed to be known to have
    * the UINT8 datatype.
    *
    * @return  the sample's value
    */
   lt_uint8 getValueUint8() const;

   /**
    * returns the sample's value
    *
    * Returns the sample's value.  The sample is assumed to be known to have
    * the UINT16 datatype.
    *
    * @return  the sample's value
    */
   lt_uint16 getValueUint16() const;
   
   /**
    * returns the sample's value
    *
    * Returns the sample's value.  The sample is assumed to be known to have
    * the FLOAT32 datatype.
    *
    * @return  the sample's value
    */
   float getValueFloat32() const;

   /*@}*/

private:
   void* m_value;
   lt_uint32 m_numBytes;
   lt_uint32 m_precision;
   LTIDataType m_dataType;
   LTIColor m_color;
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_SAMPLE_H
