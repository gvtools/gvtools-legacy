/* $Id: lt_ioFile64Stream.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LT_IOFILE64STREAM_H
#define LT_IOFILE64STREAM_H

#include "lt_ioStreamInf.h"
#include "lt_fileSpec.h"

#if defined(LT_OS_WIN)
   #include <Windows.h>
#elif defined(LT_OS_SUNOS) || defined(LT_OS_LINUX) || defined(LT_OS_DARWIN)
   #include <stdio.h>
#else
   #error Platform not yet supported.
#endif


LT_BEGIN_NAMESPACE( LizardTech )

/**
 * File stream for large files
 *
 * This class implements a file-based stream.  It works correctly for large
 * files (greater than 2GB), although on some systems performance may be less
 * than the normal file stream (LTFileStream).
 */
class LTIOFile64Stream : public LTIOStreamInf
{
public:
   /** 
    * @name Construction, destruction, initialization
    */
   //@{

   /**   
    * Default Constructor
    */
   LTIOFile64Stream();

   /**   
    * Destructor
    */
   virtual ~LTIOFile64Stream();

   /**
    * Initializes the stream
    *
    * @param   path  A LTFileSpec to the file
    * @param   mode  mode (see stdio.h)
    */
   virtual LT_STATUS initialize( const LTFileSpec &path, const char* mode );
   virtual LT_STATUS initialize( const char *path, const char* mode );

   //@}

   /** 
    * @name Status accessors
    */
   //@{

	/**
    * Indicates whether the stream is at the end of its data or not.
    *
	 *	@return  true     the stream is valid and at the end of its data.  
    * @retval false     otherwise
	 */
	virtual bool isEOF();


   /** 
    * @name Opening and closing
    */
   //@{

	/**
    * Is the stream open?
    *
	 *	@retval  true  the stream is valid and in a state that allows data access.
    * @retval  false otherwise
	 */
	virtual bool isOpen();

	/**
	 *	Opens the stream.
    *
    * Opening a stream puts it in a state that allows data access based on cached
	 *	initialization parameters.
    *
	 * @retval  LT_STS_IOStreamUninitialized  The stream has not been initialized with enough
	 *		                                    information to open the stream
	 *	@retval  LT_STS_IOStreamInvalidState   The the stream is already open
	 *	@retval  LT_STS_Success                On success.
	 *	@retval  LT_STS_Failure                Otherwise.
	 */
	virtual LT_STATUS open();
	
	/**
	 *	Closes the stream.
    *
	 *	Puts the stream in a state that does not allow data access.  May
	 *	free up resources, but only in such a way that doesn't inhibit
	 *	successful future calls to open()
    *
	 *	@retval  LT_STS_Success                On success, or if the stream is already closed.
	 *	@retval  LT_STS_Failure                Otherwise.
	 */
	virtual LT_STATUS close();

   //@}


   /** 
    * @name Data access
    */
   //@{

	/**
	 * Retrieve the specified number of bytes from the data source and
	 *	place them in pDest.
	 *	
	 *	@param   pDest         buffer in which to store read data
	 *	@param   numBytes      number of bytes to read from stream
    *
	 *	@retval numBytes        The number of bytes actually read
	 */
   virtual lt_uint32 read( lt_uint8 *pDest, lt_uint32 numBytes );
	
	/**
	 * Store the specified number of bytes in the data source.  
    *
	 *	@param   pSrc        buffer from which to store data
	 *	@param   numBytes    number of bytes to write to stream
    *
	 *	@retval  numBytes    number of bytes actually written
	 */
   virtual lt_uint32 write( const lt_uint8 *pSrc, lt_uint32 numBytes );

   //@}

   /** 
    * @name Positioning
    */
   //@{

	/**
	 *	Moves the the data access position to origin + offset
    * 
	 *	@param   offset   number of bytes from origin at which to the next read or write will take place
	 *	@param   origin   place in stream from which to seek
    *
	 *	@retval  LT_STS_IOStreamUnsupported    The stream is not seekable
	 *	@retval  LT_STS_IOStreamInvalidArgs    The offset and origin do not specify a valid location in the stream
	 *	@retval  LT_STS_Success                On success
	 *	@retval  LT_STS_Failure                Otherwise
	*/
   virtual LT_STATUS seek( lt_int64 offset, LTIOSeekDir origin );

	/**
	 *	Returns the current data access position as an offset from the start of the data
    *
	 *	@retval  postion     Number of bytes from the start of the data  
	 *	@retval  -1          On error.  
	 */
   virtual lt_int64 tell();

   //@}

   /** 
    * @name Other operations
    */
   //@{

	/**
    * @brief   Clone the stream
    *
    * Create new stream of the same type with the same initialization parameters.  
	 *	The transmission of these parameters is the responsibility of the derived type.
	 *	The new stream should initially return false for isOpen().
	 *	
	 *	@retval  NULL  the stream could not be duplicated; valid LTIOStreamInf* otherwise.
	*/
	virtual LTIOStreamInf* duplicate();

	virtual LT_STATUS getLastError() const;

	virtual const char* getID() const;

   //@}
private:

   /**   cleanup method */
   void cleanup();
   /**   sets url id */
   void setID();

protected:

   /**   pointer to open file */
#if defined(LT_OS_WIN)
   HANDLE m_file;
   // The win32 I/O primitives have different semantics than the Unix file
   // primitives, which are our reference model -- so we need to track the
   // EOF condition ourselves.
   bool m_isEOF;
#elif defined(LT_OS_SUNOS) || defined(LT_OS_LINUX) || defined(LT_OS_DARWIN)
   FILE * m_file;
#else
   #error Platform not yet supported.
#endif

   /**   path  */
   LTFileSpec m_path;

   /**   mode  */
   char *m_mode;
   char *m_uri;

   // (state code taken copied from LTIOFileStream)
   enum
   {
      unknown_state = 1,
      reading_state = 2,
      writing_state = 3
   } m_state;
};



LT_END_NAMESPACE( LizardTech )


#endif	// LT_IOFILE64STREAM_H
