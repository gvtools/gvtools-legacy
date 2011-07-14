/* $Id: lti_imageWriter.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LTI_IMAGEWRITER_H
#define LTI_IMAGEWRITER_H

// lt_lib_base
#include "lt_base.h"

// lt_lib_mrsid_core
#include "lti_imageFilter.h"

LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTIReusableBSQBuffer;
class LTIProgressDelegate;
class LTIInterruptDelegate;


/**
 * abstract class for implementing an image writer
 *
 * The LTIImageWriter abstract class is used to output pixels from an image
 * pipeline to an actual image format.  The classes MG3ImageWriter,
 * TIFFImageWriter, etc, derive from this class.
 */
class LTIImageWriter
{
public:
   /**
    * constructor
    *
    * This constructor creates an abstract image writer for the given
    * image stage.
    *
    * Note that the pointer to the input image stage may not be NULL.
    * The writer does not take ownership of the image stage.
    *
    * @param  image  the source of image data to be written
    */
   LTIImageWriter(LTIImageStage* image);

   /**
    * destructor
    */
   virtual ~LTIImageWriter();

   /**
    * initialization function
    */
   virtual LT_STATUS initialize();

   /**
    * write (encode) a scene to the output format
    *
    * This function writes the given scene to the implemented image format.
    *
    * The write() function just calls writeBegin() for the given scene(),
    * then calls writeStrip() repeatedly for each strip in the scene(), then
    * calls writeEnd().
    *
    * A derived classes should not override this function, unless it has
    * special requirements for interacting with the rest of the image
    * pipeline.
    *
    * @param   scene  the scene to decode and output
    * @return  status code indicating success or failure
    */
   virtual LT_STATUS write(const LTIScene& scene);

   /**
    * begin an encode (write) operation
    *
    * This function implements the logic for beginning the encoding of the
    * given scene.
    *
    * The scene must be a valid scene for the underlying image pipeline.
    *
    * This function is called by write().  Derived classes must implement
    * this function.
    *
    * @param   scene  the scene to decode and output
    * @return  status code indicating success or failure
    */
   virtual LT_STATUS writeBegin(const LTIScene& scene) =0;

   /**
    * encode one strip of the scene
    *
    * This function implements the logic for actually encoding a given strip
    * of the given scene.
    *
    * This function is called by write().  Derived classes must implement
    * this function.
    *
    * @param   stripBuffer  the pixels for the current strip
    * @param   stripScene   the scene representing the strip being written
    * @return  status code indicating success or failure
    */
   virtual LT_STATUS writeStrip(LTISceneBuffer& stripBuffer, 
                                const LTIScene& stripScene) =0;

   /**
    * complete an encode (write) operation
    *
    * This function implements the logic for completing the encoding of the
    * given scene.
    *
    * This function is called by write().  Derived classes must implement
    * this function.
    *
    * @return  status code indicating success or failure
    */
   virtual LT_STATUS writeEnd() =0;

   /**
    * set the stripheight for the encode
    *
    * This function sets the stripheight to be used when encoding a scene,
    * i.e. the number of rows to process at one time.  This value is used
    * to control the decode requests from the image pipeline.
    *
    * @param  stripHeight  the number of rows to encode at one time
    * @return  status code indicating success or failure
    */
   virtual LT_STATUS setStripHeight(lt_uint32 stripHeight);

   /**
    * get the stripheight for the encode
    *
    * Returns the number of rows which will be encoded at a time.
    *
    * @return  the stripheight setting of the encoder
    */
   virtual lt_uint32 getStripHeight() const;

   /**
    * get the preferred stripheight for the encode
    *
    * Returns the preferred stripheight for the encoder.  By default, this is
    * just the stripheight of the underlying image pipeline.  A derived class
    * may override this to reflect specific needs of an encoder
    * implementation, however.
    *
    * @return  the preferred stripheight setting of the encoder
    */
   virtual lt_uint32 getDefaultStripHeight() const;

   /**
    * get the cost to encode the scene
    *
    * Returns the cost to encode this scene, for application using the usage
    * metering system.  See LTIImageStage::getEncodingCost() for details.
    *
    * @param   scene  the scene to determine the cost for
    * @return  the cost for the scene
    */
   virtual lt_int64 getEncodingCost(const LTIScene& scene) const;

   /**
    * set progress delegate
    *
    * This function sets the progress delegate, which is used in a callback-like
    * fashion to report percent-complete of a write() operation back to the
    * calling application.
    *
    * Passing NULL to this function should remove the LTIImageWriter's current delegate,
    * if any.
    *
    * Note this function does not take ownership of the delegate object.
    *
    * @param  delegate  a pointer to the delegate object to be used by the image writer
    */
   virtual void setProgressDelegate(LTIProgressDelegate* delegate);

   /**
    * set interrupt delegate
    *
    * This function sets the interrupt delegate, which is used in a callback-like
    * fashion by the calling application to asynchronously indicate that a write()
    * operation should be halted without completing.
    *
    * Passing NULL to this function should remove the LTIImageWriter's current delegate,
    * if any.
    *
    * Note this function does not take ownership of the delegate object.
    *
    * @param  delegate  a pointer to the delegate object to be used by the image writer
    */
   virtual void setInterruptDelegate(LTIInterruptDelegate* delegate);

protected:
   /**
    * get progress delegate
    *
    * This function returns the object's progress delegate.
    *
    * The function will return NULL if no delegate has been set.
    *
    * Derived classes should call this method from within their write()
    * methods so that they can inform the user of the progress of the write
    * operation.
    *
    * @return  a pointer to the delegate object (or NULL if no delegate has been set)
    */
   virtual LTIProgressDelegate* getProgressDelegate() const;

   /**
    * get interrupt delegate
    *
    * This function returns the object's interrupt delegate.
    *
    * The function will return NULL if no delegate has been set.
    *
    * Derived classes should call this method from within their write()
    * methods so that they can determine if the user has requested that the write
    * operation should be aborted.
    *
    * @return  a pointer to the delegate object (or NULL if no delegate has been set)
    */
   virtual LTIInterruptDelegate* getInterruptDelegate() const;

   LTIImageStage* m_image;

private:
   LT_STATUS checkDelegates(const LTIScene& fullScene,
                            const LTIScene* currScene,
                            bool atEnd);

   lt_uint32 m_stripHeight;
   LTIReusableBSQBuffer* m_buffer;

   LTIProgressDelegate* m_progressDelegate;
   LTIInterruptDelegate* m_interruptDelegate;

   // nope
   LTIImageWriter();
   LTIImageWriter(LTIImageWriter&);
   LTIImageWriter& operator=(const LTIImageWriter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LTI_IMAGEWRITER_H
