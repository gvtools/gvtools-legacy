project(jecw)

# Looking for arch extension!
SET(LIB_POSTFIX "32")
IF(UNIX)
	IF(NOT APPLE)
		IF(CMAKE_SIZEOF_VOID_P MATCHES "8")
      		SET(LIB_POSTFIX "64")
  		ENDIF(CMAKE_SIZEOF_VOID_P MATCHES "8")
	ENDIF(NOT APPLE)
ELSE(UNIX)
	IF(CMAKE_SYSTEM_NAME MATCHES Windows)
		IF(CMAKE_CL_64)
      		SET(LIB_POSTFIX "64")
		ENDIF(CMAKE_CL_64)	
	ENDIF(CMAKE_SYSTEM_NAME MATCHES Windows)
ENDIF(UNIX)

SET(platform_dir)
IF(WIN32)
	IF(NOT CYGWIN)
		SET(platform_dir "w${LIB_POSTFIX}")
	ENDIF(NOT CYGWIN)
ENDIF(WIN32)
IF(UNIX)
	IF(APPLE)
		SET(platform_dir "mac")
	ELSE(APPLE)
		SET(platform_dir "linux")
	ENDIF(APPLE)
ENDIF(UNIX)

IF(UNIX)
	SET(CMAKE_CXX_FLAGS_RELEASE "-O -DNDEBUG")
	SET(CMAKE_C_FLAGS_RELEASE "-O -DNDEBUG")
ENDIF(UNIX)

SET(GVSIG_BINARIES_PATH "${CMAKE_SOURCE_DIR}/../binaries/${platform_dir}/raster/ecw")

SET(CMAKE_MODULE_PATH "${CMAKE_SOURCE_DIR}/CMakeModules;${CMAKE_MODULE_PATH}")
FILE(MAKE_DIRECTORY ${CMAKE_SOURCE_DIR}/lib-dist)
#FILE(MAKE_DIRECTORY ${CMAKE_SOURCE_DIR}/bin-dist)

set(LIBRARY_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/lib-dist)
#set(EXECUTABLE_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/bin-dist)
#set(CMAKE_SKIP_RPATH TRUE)
IF(APPLE)
	SET(CMAKE_INSTALL_NAME_DIR @executable_path/../Resources/NativeLibs CACHE STRING "Executable Path for MacOS X" FORCE)
  	MARK_AS_ADVANCED(CMAKE_INSTALL_NAME_DIR)
ENDIF(APPLE)


SET(CMAKE_INSTALL_PREFIX ${GVSIG_BINARIES_PATH} CACHE STRING "Forcing install path" FORCE)
MARK_AS_ADVANCED(CMAKE_INSTALL_PREFIX)

# This is for an advanced option to give aggressive warnings
# under different compilers. If yours is not implemented, this option
# will not be made available.
IF(CMAKE_COMPILER_IS_GNUCXX)
    # To be complete, we might also do GNUCC flags,
    # but everything here is C++ code.
    # -Wshadow and -Woverloaded-virtual are also interesting flags, but OSG
    # returns too many hits.
    # FYI, if we do implement GNUCC, then -Wmissing-prototypes in another
    # interesting C-specific flag.
    # Also, there is a bug in gcc 4.0. Under C++, -pedantic will create
    # errors instead of warnings for certain issues, including superfluous
    # semicolons and commas, and the use of long long. -fpermissive seems
    # to be the workaround.
    SET(AGGRESSIVE_WARNING_FLAGS "-Wall -Wparentheses -Wformat=2 -Wno-long-long -Wno-import -pedantic -Wreturn-type -Wmissing-braces -Wunknown-pragmas -Wunused -fpermissive")
ELSE(CMAKE_COMPILER_IS_GNUCXX)
    IF(MSVC)
        # FIXME: What are good aggressive warning flags for Visual Studio?
        # And do we need to further subcase this for different versions of VS?
        # CMake variables: MSVC60, MSVC70, MSVC71, MSVC80, CMAKE_COMPILER_2005
        SET(AGGRESSIVE_WARNING_FLAGS "/Wall /W4")


    ELSE(MSVC)
        # CMake lacks an elseif, so other non-gcc, non-VS compilers need
        # to be listed below. If unhandled, OSG_AGGRESSIVE_WARNING_FLAGS should
        # remain unset.
    ENDIF(MSVC)
ENDIF(CMAKE_COMPILER_IS_GNUCXX)

# This part is for the CMake menu option to toggle the warnings on/off.
# This will only be made available if we set values for OSG_AGGRESSIVE_WARNING_FLAGS.
IF(AGGRESSIVE_WARNING_FLAGS)
    OPTION(USE_AGGRESSIVE_WARNINGS "Enable to activate aggressive warnings" OFF)
    MARK_AS_ADVANCED(USE_AGGRESSIVE_WARNINGS)

    IF(USE_AGGRESSIVE_WARNINGS)
        IF(NOT "${OLD_CMAKE_CXX_FLAGS_WAS_SET}")
            SET(OLD_CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}" CACHE INTERNAL "Old CXX flags")
            SET(OLD_CMAKE_CXX_FLAGS_WAS_SET 1 CACHE INTERNAL "Old CXX flags was set")
            SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${AGGRESSIVE_WARNING_FLAGS}" CACHE STRING "Flags used by the compiler during all build types." FORCE)
        ENDIF(NOT "${OLD_CMAKE_CXX_FLAGS_WAS_SET}")
    ELSE(USE_AGGRESSIVE_WARNINGS)
        # FIXME: This will lose any changes made after OLD_CMAKE_CXX_FLAGS was
        # set. The better way would be to parse the string and remove each
        # option explicitly.
        IF("${OLD_CMAKE_CXX_FLAGS_WAS_SET}")
            SET(CMAKE_CXX_FLAGS "${OLD_CMAKE_CXX_FLAGS}" CACHE STRING "Flags used by the compiler during all build types." FORCE)
            SET(OLD_CMAKE_CXX_FLAGS_WAS_SET 0 CACHE INTERNAL "Old CXX flags was set")
        ENDIF("${OLD_CMAKE_CXX_FLAGS_WAS_SET}")
    ENDIF(USE_AGGRESSIVE_WARNINGS)
ENDIF(AGGRESSIVE_WARNING_FLAGS)

# Set defaults for Universal Binaries. We want 32-bit Intel/PPC on 10.4
# and 32/64-bit Intel/PPC on >= 10.5. Anything <= 10.3 doesn't support.
IF(APPLE)
    # These are just defaults/recommendations, but how we want to build
    # out of the box. But the user needs to be able to change these options.
    # So we must only set the values the first time CMake is run, or we
    # will overwrite any changes the user sets.
    # FORCE is used because the options are not reflected in the UI otherwise.
    # Seems like a good place to add version specific compiler flags too.
    IF(NOT CONFIG_HAS_BEEN_RUN_BEFORE)
        # This is really fragile, but CMake doesn't provide the OS system
        # version information we need. (Darwin versions can be changed
        # independently of OS X versions.)
        # It does look like CMake handles the CMAKE_OSX_SYSROOT automatically.
        IF(EXISTS /Developer/SDKs/10.5.sdk)
            SET(CMAKE_OSX_ARCHITECTURES "ppc;i386;ppc64;x86_64" CACHE STRING "Build architectures for OSX" FORCE)
            SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -mmacosx-version-min=10.5 -ftree-vectorize -fvisibility-inlines-hidden" CACHE STRING "Flags used by the compiler during all build types." FORCE)
        ELSE(EXISTS /Developer/SDKs/10.5.sdk)
            IF(EXISTS /Developer/SDKs/MacOSX10.4u.sdk)
                SET(CMAKE_OSX_ARCHITECTURES "ppc;i386" CACHE STRING "Build architectures for OSX" FORCE)
                SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -mmacosx-version-min=10.4 -ftree-vectorize -fvisibility-inlines-hidden" CACHE STRING "Flags used by the compiler during all build types." FORCE)
            ELSE(EXISTS /Developer/SDKs/MacOSX10.4u.sdk)
                # No Universal Binary support
                # Should break down further to set the -mmacosx-version-min,
                # but the SDK detection is too unreliable here.
            ENDIF(EXISTS /Developer/SDKs/MacOSX10.4u.sdk)
        ENDIF(EXISTS /Developer/SDKs/10.5.sdk)
    ENDIF(NOT CONFIG_HAS_BEEN_RUN_BEFORE)
ENDIF(APPLE)


SET(JECW_VERSION "0.0.5" CACHE STRING "JECW_VERSION")

find_package(JNI) 
find_package(ECW) 

link_directories(${LIBRARY_OUTPUT_PATH})

add_subdirectory(src/main/native/jecw)

# This needs to be run very last so other parts of the scripts can take
# advantage of this.
IF(NOT CONFIG_HAS_BEEN_RUN_BEFORE)
    SET(CONFIG_HAS_BEEN_RUN_BEFORE 1 CACHE INTERNAL "Flag to track whether this is the first time running CMake or if CMake has been configured before")
ENDIF(NOT CONFIG_HAS_BEEN_RUN_BEFORE)


