# Locate ecw
# This module defines
# ECW_LIBRARIES
# ECW_FOUND, if false, do not try to link to gdal 
# ECW_INCLUDE_DIR, where to find the headers
#
# $ECW_DIR is an environment variable that would
# correspond to the ./configure --prefix=$ECW_DIR
#
# Created by gvSIG project

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
)

FIND_LIBRARY(ECW_LIBRARY 
	NAMES libecwj2 ecwj2
	PATHS
	${ECW_DIR}/lib
    $ENV{ECW_DIR}/lib
    $ENV{ECW_DIR}
)

IF(ECW_LIBRARY)
	SET(ECW_LIBRARIES ${ECW_LIBRARY})
ENDIF(ECW_LIBRARY)

IF(UNIX)
	FIND_LIBRARY(NECW_LIBRARY 
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
	)

	IF(NECW_LIBRARY)
		SET(ECW_LIBRARIES ${NECW_LIBRARY})
	ENDIF(NECW_LIBRARY)

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
	)

	IF(ECW_LIBRARY2)
		SET(ECW_LIBRARIES ${ECW_LIBRARIES} ${ECW_LIBRARY2})
	ENDIF(ECW_LIBRARY2)

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
	)

	IF(ECW_LIBRARY3)
		SET(ECW_LIBRARIES ${ECW_LIBRARIES} ${ECW_LIBRARY3})
	ENDIF(ECW_LIBRARY3)

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

	IF(ECW_LIBRARY4)
		SET(ECW_LIBRARIES ${ECW_LIBRARIES} ${ECW_LIBRARY4})
	ENDIF(ECW_LIBRARY4)
ENDIF(UNIX)

SET(ECW_FOUND "NO")
IF(ECW_LIBRARIES AND ECW_INCLUDE_DIR)
    SET(ECW_FOUND "YES")
ENDIF(ECW_LIBRARIES AND ECW_INCLUDE_DIR)

