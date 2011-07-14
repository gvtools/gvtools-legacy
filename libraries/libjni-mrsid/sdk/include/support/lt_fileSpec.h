/* $Id: lt_fileSpec.h 3539 2006-01-09 12:23:20Z nacho $ */
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

#ifndef LT_FILESPEC_H
#define LT_FILESPEC_H

// lt_lib_base
#include "lt_base.h"

#if defined(LT_OS_DARWIN)
	#if defined(LT_OS_DARWIN6)
  		#include <wcstring.h>
  	#endif
#elif defined (LT_OS_WIN32)
  #include <wchar.h>
#endif

#include <stddef.h>  // for NULL

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


LT_BEGIN_NAMESPACE(LizardTech)


/**
 * Max length of a path
 */
#ifdef LT_OS_WIN
#define LT_UTIL_MAX_PATH _MAX_PATH
#else
#define LT_UTIL_MAX_PATH 2048
#endif

/**
 * Represents a file or directory path
 */
class LTFileSpec
{
public:
   enum
   {
      fSlash = '/',
      bSlash = '\\',
#ifdef LT_OS_WIN
      platformSlash = '\\'
#else
      platformSlash = '/'
#endif
   };


public:
   /**
    * default constructor
    */
   LTFileSpec(void);

   /**
    * destructor
    */
   ~LTFileSpec(void);

   /**
    * native constructor
    *
    * @param   p1    first part of the path could be directory or filename
    * @param   p2    second part of the path could be directory or filename or NULL
    * @param   p3    last part of the path could be a filename or NULL
    *
    * @note On Win32 the file system treats a (char *) as a multibyte string
    *       On Unix/Linux the file system treats a (char *) as a utf8 string
    */
   explicit LTFileSpec(const char *p1, const char *p2 = NULL, const char *p3 = NULL);

   /**
    * wchar constructor
    * @note See native constructor for arguments
    * @note On Win32 wchar_t is a UTF16 string. On unix it is UTF32
    */
   explicit LTFileSpec(const wchar_t *p1, const wchar_t *p2 = NULL, const wchar_t *p3 = NULL);

   /*@{*/
   /**
    * LTFileSpec constructor
    * @note See native constructor for arguments
    * @note See above for notes on (char *) and (wchar_t *) types
    */
   LTFileSpec(const LTFileSpec &copy);
   LTFileSpec(const LTFileSpec &p1, const char *p2, const char *p3 = NULL);
   LTFileSpec(const LTFileSpec &p1, const wchar_t *p2, const wchar_t *p3 = NULL);

   LTFileSpec(const LTFileSpec &p1, const LTFileSpec &p2, const char *p3 = NULL);
   LTFileSpec(const LTFileSpec &p1, const LTFileSpec &p2, const wchar_t *p3);

   LTFileSpec(const LTFileSpec &p1, const LTFileSpec &p2, const LTFileSpec &p3);
   /*@}*/

   /**
    * assignment operator
    */
   LTFileSpec& operator=(const LTFileSpec& copy);

   /**
    * inequality operator
    */
   bool operator!=(const LTFileSpec& fs) const;

   /**
    * equality operator
    */
   bool operator==(const LTFileSpec& fs) const;

   /**
    * Initialization from Native strings
    * 
    * See Native contructor for arguments
    */
   void set(const char *p1, const char *p2 = NULL, const char *p3 = NULL);

   /**
    * Initialization from UTF8 strings
    * 
    * See Native contructor for arguments
    */
   void setUTF8(const lt_utf8 *p1, const lt_utf8 *p2 = NULL, const lt_utf8 *p3 = NULL);

   /**
    * Initialization from Wide strings
    * 
    * See Native contructor for arguments
    */
   void setWide(const wchar_t *p1, const wchar_t *p2 = NULL, const wchar_t *p3 = NULL);

   /**
    * Return the parent directory
    *
    * The semantics are similar to the standard Unix dirname.
    *
    * Examples:
    * - "/usr/lib" -> "/usr"
    * - "/usr/"    -> "/"
    * - "usr"      -> "."
    * - "/"        -> "/"
    * - "C:/"      -> "C:/"
    * - "."        -> "."
    * - ".."       -> "."
    */
   LTFileSpec dirname(void) const;

   /**
    * Return the base filename
    *
    * The semantics are similar to the standard Unix basename.
    *
    * Examples:
    * - "/usr/lib" -> "lib"
    * - "/usr/"    -> "usr/"  (not the same as the unix basename)
    * - "usr"      -> "usr"
    * - "/"        -> "/"
    * - "C:/"      -> "C:/"
    * - "."        -> "."
    * - ".."       -> ".."
    */
   const lt_utf8 *basename(void) const;

   /**
    * Function to convert the path to native format.
    *
    * @note On Win32 this returns a multi-byte string.
    * @note On Unix this returns a UTF8 string.
    */
   const char *n_str(void) const;

   /* DEPRECATED */
   const char *c_str(void) const;
   
   /**
    * Function to convert the path to Wide format.
    *
    * @note On Win32 this returns a 16bit UTF16 string. 
    * @note On Unix this returns a 32bit UTF32 string.
    */
   const wchar_t *w_str(void) const;

   /**
    * Function to convert the path to a UTF8 format.
    *
    * @note On Win32 this returns a 16bit UTF16 string. 
    * @note On Unix this returns a 32bit UTF32 string.
    */
   const lt_utf8* utf8(void) const { return m_path8; }

   /**
    * returns suffix (in utf8)
    *
    * Examples:
    * - "foo.bar" -> "bar"
    * - "foo." -> ""
    * - "foo" -> ""
    */
   const lt_utf8* getSuffix() const;

   /**
    * replaces suffix (extension)
    *
    * Note that \a ext should not include the ".".
    * If \a ext is null, the suffix is removed.
    *
    * Examples:
    * - "foo.bar" with "baz" -> "foo.baz"
    * - "foo.bar" with ".baz" -> "foo..baz"
    * - "foo.bar" with "" -> "foo."
    * - "foo" with "baz" -> "foo.baz"
    */
   void replaceSuffix(const lt_utf8* ext);

   /**
    * remove the suffix (extension)
    *
    * Note this also removes the ".".
    *
    * Examples:
    * - "foo.bar" -> "foo"
    * - "foo." -> "foo"
    * - "foo" -> "foo"
    */
   void removeSuffix();

   /**
    * returns true if path is absolute, false if relative
    */
   bool absolute() const;

protected:

private:
   // using a utf8 string to hold the path because it is the easiest
   // to play with (we can look for bSlashs and not have to worry 
   // about lead btyes.
   lt_utf8 *m_path8;

   mutable char *m_pathA;     // this will be updated in n_str()
   mutable wchar_t *m_pathW;  // this will be updated in w_str()
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif // LT_FILESPEC_H
