	# Locate mrsid
# This module defines
# MRSID_LIBRARY
# MRSID_FOUND, if false, do not try to link to gdal 
# MRSID_INCLUDE_DIR, where to find the headers
#
# $MRSID_DIR is an environment variable that would
# correspond to the ./configure --prefix=$MRSID_DIR
#
# Created by Robert Osfield. 

FIND_PATH(MRSID_INCLUDE_DIR lti_image.h
	C:/Geo_DSDK-6.0.7.1408/include/base
	${CMAKE_SOURCE_DIR}/sdk/include/base
    ${MRSID_DIR}/include/base
    $ENV{MRSID_DIR}/include/base
)

FIND_PATH(MRSID_INCLUDE_DIR2 ltic_api.h
	C:/Geo_DSDK-6.0.7.1408/include/c_api
	${CMAKE_SOURCE_DIR}/sdk/include/c_api
    ${MRSID_DIR}/include/c_api
    $ENV{MRSID_DIR}/include/c_api
)

FIND_PATH(MRSID_INCLUDE_DIR3 lti_cropFilter.h
	C:/Geo_DSDK-6.0.7.1408/include/filters
    ${CMAKE_SOURCE_DIR}/sdk/include/filters
    #${MRSID_DIR}/include/filters
    #$ENV{MRSID_DIR}/include/filters
)

FIND_PATH(MRSID_INCLUDE_DIR4 j2k_types.h
    C:/Geo_DSDK-6.0.7.1408/include/j2k_readers
    ${CMAKE_SOURCE_DIR}/sdk/include/j2k_readers
    ${MRSID_DIR}/include/j2k_readers
    $ENV{MRSID_DIR}/include/j2k_readers
)

FIND_PATH(MRSID_INCLUDE_DIR5 lti_metadataUtils.h
    C:/Geo_DSDK-6.0.7.1408/include/metadata
    ${CMAKE_SOURCE_DIR}/sdk/include/metadata
    ${MRSID_DIR}/include/metadata
    $ENV{MRSID_DIR}/include/metadata
)

FIND_PATH(MRSID_INCLUDE_DIR6 lti_mrsidReadersStatus.h
	C:/Geo_DSDK-6.0.7.1408/include/mrsid_readers
    ${CMAKE_SOURCE_DIR}/sdk/include/mrsid_readers
    ${MRSID_DIR}/include/mrsid_readers
    $ENV{MRSID_DIR}/include/mrsid_readers
)

FIND_PATH(MRSID_INCLUDE_DIR7 lt_base.h
	C:/Geo_DSDK-6.0.7.1408/include/support
    ${CMAKE_SOURCE_DIR}/sdk/include/support
    ${MRSID_DIR}/include/support
    $ENV{MRSID_DIR}/include/support
)

FIND_PATH(MRSID_INCLUDE_DIR8 lti_writersStatus.h
	C:/Geo_DSDK-6.0.7.1408/include/writers
    ${CMAKE_SOURCE_DIR}/sdk/include/writers
    ${MRSID_DIR}/include/writers
    $ENV{MRSID_DIR}/include/writers
)


FIND_LIBRARY(MRSID_LIBRARY 
    NAMES ltidsdk
    PATHS
    ${CMAKE_SOURCE_DIR}/sdk/lib
    ${MRSID_DIR}/lib/Release
    $ENV{MRSID_DIR}/lib/Release
)

	
FIND_LIBRARY(MRSID_LIBRARY_W1
	NAMES lti_dsdk_dll
	PATHS
	C:/Geo_DSDK-6.0.7.1408/lib/Release
	${CMAKE_SOURCE_DIR}/sdk/lib
    ${MRSID_DIR}/lib/Release
    $ENV{MRSID_DIR}/lib/Release
)

FIND_LIBRARY(MRSID_LIBRARY_W2
NAMES lti_dsdk_	cdll
	PATHS
	C:/Geo_DSDK-6.0.7.1408/lib/Release
	${CMAKE_SOURCE_DIR}/sdk/lib
    ${MRSID_DIR}/lib/Release
    $ENV{MRSID_DIR}/lib/Release
)

SET(MRSID_FOUND "NO")
IF(MRSID_LIBRARY AND MRSID_INCLUDE_DIR)
    SET(MRSID_FOUND "YES")
ENDIF(MRSID_LIBRARY AND MRSID_INCLUDE_DIR)

