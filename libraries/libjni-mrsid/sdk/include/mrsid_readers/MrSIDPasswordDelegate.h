/* $Id: MrSIDPasswordDelegate.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef MRSIDPASSWORDDELEGATE_H
#define MRSIDPASSWORDDELEGATE_H

// lt_lib_base
#include "lt_base.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


class EncryptImp;
class MrSIDImageReaderBase;
class MG2ImageWriter;
class MG3ImageWriter;


/**
 * delegate for locked MrSID images
 *
 * This abstract class is used with MrSIDImageReaderBase::setPasswordDelegate()
 * to supply a user-callback mechanism for supplying text passwords to
 * the internal MrSID decoder logic.
 *
 * Users should derive their own class from this, supplying their own
 * reportIncorrectPassword() and getPassword() methods.
 */
class MrSIDPasswordDelegate
{
public:
   /**
    * constructor
    */
   MrSIDPasswordDelegate();

   /**
    * destructor
    */
   virtual ~MrSIDPasswordDelegate();

   /**
    * user function for user notification
    *
    * This function is called by the decoder if the password
    * entered was incorrect.  Derived classes must implement
    * this function, e.g. to pop up a message box, abort the
    * operation, etc.
    *
    * @return success or failure in reporting to user
    */
   virtual LT_STATUS reportIncorrectPassword() = 0;

   /**
    * user function for getting the password
    *
    * This function is called by the decoder to request a password
    * from the user.  Derived classes must implement
    * this function, e.g. to pop up a text-entry dialog box.
    *
    * The implementation of this function must copy the password
    * into the buffer pointed by getPasswordBuffer().
    *
    * @return success or failure in getting password from user
    */
   virtual LT_STATUS getPassword() = 0;

protected:
   /**
    * get password buffer
    *
    * This function returns a pointer to the allocated area for the
    * password obtained from the user.
    *
    * @return  pointer to the password buffer
    */
   char* getPasswordBuffer();

   /**
    * get password buffer length
    *
    * This function returns the length of the buffer returned from
    * getPasswordBuffer().
    *
    * @return  length of the password buffer
    */
   lt_uint32 getPasswordBufferLength();

private:
   friend class EncryptImp;
   EncryptImp* m_encryptImp;
   char* m_passwordBuffer;
   
   static const lt_uint32 s_passwordBufferLength;

   friend class MrSIDImageReaderBase;
   friend class MG2ImageWriter;
   friend class MG3ImageWriter;
   void registerProvider();

   // nope
   MrSIDPasswordDelegate(const MrSIDPasswordDelegate&);
   MrSIDPasswordDelegate& operator=(const MrSIDPasswordDelegate&);
};


/**
 * simple concrete delegate for locked MrSID images
 *
 * This class is a concrete password delegate class which just
 * takes a fixed string in its ctor.
 */
class MrSIDSimplePasswordDelegate : public MrSIDPasswordDelegate
{
public:
   /**
    * constructor
    * 
    * Create a password delegate, using the given string.
    *
    * @param password the password to use to unlock the image
    */
   MrSIDSimplePasswordDelegate(const char* password);

   /**
    * failure user notification
    *
    * This function just returns LT_STS_Failure.
    *
    * @return always LT_STS_Failure
    */
   LT_STATUS reportIncorrectPassword();

   /**
    * get the password
    *
    * This function does nothing, as the password is fixed (determined
    * by parameter to constructor).
    *
    * @return always LT_STS_Success
    */
   LT_STATUS getPassword();

private:
   // nope
   MrSIDSimplePasswordDelegate(const MrSIDSimplePasswordDelegate&);
   MrSIDSimplePasswordDelegate& operator=(const MrSIDSimplePasswordDelegate&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // MRSIDPASSWORDDELEGATE_H
