# Locate mrsid
# This module defines
# MRSID_LIBRARY
# MRSID_FOUND, if false, do not try to link to gdal 
# MRSID_INCLUDE_DIR, where to find the headers
#
# $MRSID_DIR is an environment variable that would
# correspond to the ./configure --prefix=$MRSID_DIR
#
# Created by gvSIG Raster/3D Team. 

FIND_PATH(MRSID_INCLUDE_DIR lti_image.h
	$ENV{MRSID_DIR}/include
	${CMAKE_SOURCE_DIR}/sdk/include
)

FIND_PATH(MRSID_INCLUDE_DIR2 ltic_api.h
	$ENV{MRSID_DIR}/include
	${CMAKE_SOURCE_DIR}/sdk/include
)

FIND_PATH(MRSID_INCLUDE_DIR3 lti_cropFilter.h
	$ENV{MRSID_DIR}/include
    	${CMAKE_SOURCE_DIR}/sdk/include
)

FIND_PATH(MRSID_INCLUDE_DIR4 j2k_types.h
	$ENV{MRSID_DIR}/include
    	${CMAKE_SOURCE_DIR}/sdk/include
)

FIND_PATH(MRSID_INCLUDE_DIR5 lti_metadataUtils.h
	$ENV{MRSID_DIR}/include
    	${CMAKE_SOURCE_DIR}/sdk/include
)

FIND_PATH(MRSID_INCLUDE_DIR6 lti_mrsidReadersStatus.h
	$ENV{MRSID_DIR}/include
	${CMAKE_SOURCE_DIR}/sdk/include
)

FIND_PATH(MRSID_INCLUDE_DIR7 lt_base.h
	$ENV{MRSID_DIR}/include
    	${CMAKE_SOURCE_DIR}/sdk/include
)

FIND_PATH(MRSID_INCLUDE_DIR8 lti_writersStatus.h
    	$ENV{MRSID_DIR}/include
    	${CMAKE_SOURCE_DIR}/sdk/include
)

FIND_LIBRARY(MRSID_LIBRARY 
    	NAMES ltidsdk lti_dsdk
    	PATHS
		$ENV{MRSID_DIR}/lib/Release
    		${CMAKE_SOURCE_DIR}/sdk/lib
)

	
FIND_LIBRARY(MRSID_LIBRARY_W1
	NAMES lti_dsdk_dll
	PATHS
		$ENV{MRSID_DIR}/lib/Release
		${CMAKE_SOURCE_DIR}/sdk/lib
)

FIND_LIBRARY(MRSID_LIBRARY_W2
NAMES lti_dsdk_cdll
	PATHS
    		$ENV{MRSID_DIR}/lib/Release
		${CMAKE_SOURCE_DIR}/sdk/lib
)

SET(MRSID_FOUND "NO")
IF(MRSID_LIBRARY AND MRSID_INCLUDE_DIR)
    SET(MRSID_FOUND "YES")
ENDIF(MRSID_LIBRARY AND MRSID_INCLUDE_DIR)

