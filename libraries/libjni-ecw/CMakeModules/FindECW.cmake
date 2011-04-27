# Locate ecw
# This module defines
# ECW_LIBRARY
# ECW_FOUND, if false, do not try to link to gdal 
# ECW_INCLUDE_DIR, where to find the headers
#
# $ECW_DIR is an environment variable that would
# correspond to the ./configure --prefix=$ECW_DIR
#
# Created by Robert Osfield. 

FIND_PATH(ECW_INCLUDE_DIR 
	NAMES ECW.h NCSError.h NCSErrors.h
	PATHS
    ${ECW_DIR}/include
    $ENV{ECW_DIR}/include
    $ENV{ECW_DIR}
    ~/Library/Frameworks
    /Library/Frameworks
    /usr/local/include
    /usr/include
    /usr/include/ecw
    /sw/include # Fink
    /opt/local/include # DarwinPorts
    /opt/csw/include # Blastwave
    /opt/include
    [HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session\ Manager\\Environment;OSG_ROOT]/include
    /usr/freeware/include
    #"C:/libecwj2-3.3/Source/include"
    "C:/Archivos de programa/Earth Resource Mapping/ECW SDK/include"
    "C:/Earth Resource Mapping/ECW SDK/include"
    "C:/ECW SDK/include"
    "C:/ecw/include"
)

FIND_LIBRARY(ECW_LIBRARY 
    NAMES NCSEcw
    PATHS
    ${ECW_DIR}/lib
    $ENV{ECW_DIR}/lib
    $ENV{ECW_DIR}
    ~/Library/Frameworks
    /Library/Frameworks
    /usr/local/lib
    /usr/lib
    /sw/lib
    /opt/local/lib
    /opt/csw/lib
    /opt/lib
    [HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session\ Manager\\Environment;OSG_ROOT]/lib
    /usr/freeware/lib64
    "C:/Archivos de programa/Earth Resource Mapping/ECW SDK/lib"
    "C:/Earth Resource Mapping/ECW SDK/lib"
    "C:/ECW SDK/lib/vc71"
    "C:/ECW SDK/lib"
    "C:/ecw/lib"
)

FIND_LIBRARY(ECW_LIBRARY2 
    NAMES NCSEcwC
    PATHS
    ${ECW_DIR}/lib
    $ENV{ECW_DIR}/lib
    $ENV{ECW_DIR}
    ~/Library/Frameworks
    /Library/Frameworks
    /usr/local/lib
    /usr/lib
    /sw/lib
    /opt/local/lib
    /opt/csw/lib
    /opt/lib
    [HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session\ Manager\\Environment;OSG_ROOT]/lib
    /usr/freeware/lib64
    "C:/Archivos de programa/Earth Resource Mapping/ECW SDK/lib"
    "C:/Earth Resource Mapping/ECW SDK/lib"
    "C:/ECW SDK/lib/vc71"
    "C:/ECW SDK/lib"
    "C:/ecw/lib"
)

FIND_LIBRARY(ECW_LIBRARY3 
    NAMES NCSUtil
    PATHS
    ${ECW_DIR}/lib
    $ENV{ECW_DIR}/lib
    $ENV{ECW_DIR}
    ~/Library/Frameworks
    /Library/Frameworks
    /usr/local/lib
    /usr/lib
    /sw/lib
    /opt/local/lib
    /opt/csw/lib
    /opt/lib
    [HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session\ Manager\\Environment;OSG_ROOT]/lib
    /usr/freeware/lib64
    "C:/Archivos de programa/Earth Resource Mapping/ECW SDK/lib"
    "C:/Earth Resource Mapping/ECW SDK/lib"
    "C:/ECW SDK/lib/vc71"
    "C:/ECW SDK/lib"
    "C:/ecw/lib"
)

FIND_LIBRARY(ECW_LIBRARY4 
    NAMES NCSCnet
    PATHS
    ${ECW_DIR}/lib
    $ENV{ECW_DIR}/lib
    $ENV{ECW_DIR}
    ~/Library/Frameworks
    /Library/Frameworks
    /usr/local/lib
    /usr/lib
    /sw/lib
    /opt/local/lib
    /opt/csw/lib
    /opt/lib
    [HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session\ Manager\\Environment;OSG_ROOT]/lib
    /usr/freeware/lib64
)

SET(ECW_FOUND "NO")
IF(ECW_LIBRARY AND ECW_INCLUDE_DIR)
    SET(ECW_FOUND "YES")
ENDIF(ECW_LIBRARY AND ECW_INCLUDE_DIR)

