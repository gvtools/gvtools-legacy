# Locate gdal
# This module defines
# GDAL_LIBRARY
# GDAL_FOUND, if false, do not try to link to gdal 
# GDAL_INCLUDE_DIR, where to find the headers
#
# $GDAL_DIR is an environment variable that would
# correspond to the ./configure --prefix=$GDAL_DIR
#
# Created by Robert Osfield. 

FIND_PATH(GDAL_INCLUDE_DIR gdal.h
    /home/nacho/software/libs/gdal-1.5.2/gcore
	${GDAL_DIR}/include
	$ENV{GDAL_DIR}/include
	$ENV{GDAL_DIR}
	$ENV{OSGDIR}/include
	$ENV{OSGDIR}
	$ENV{OSG_ROOT}/include
	~/Library/Frameworks
	/Library/Frameworks
	/usr/local/include
	/usr/include
	/usr/include/gdal
	/sw/include # Fink
	/opt/local/include # DarwinPorts
	/opt/csw/include # Blastwave
	/opt/include
	[HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session\ Manager\\Environment;OSG_ROOT]/include
	/usr/freeware/include
	C:/gdal/gdal-1.5.1/gcore
	C:/gdal/gdal-1.5.0/gcore
	C:/gdal/gdal-1.4.4/gcore
	C:/gdal/gdal-1.4.2/gcore
	C:/gdal/gdal-1.4.1/gcore
	C:/gdal-1.5.1/gcore
	C:/gdal-1.5.0/gcore
	C:/gdal-1.4.4/gcore
	C:/gdal-1.4.2/gcore
	C:/gdal-1.4.1/gcore
)

FIND_PATH(GDAL_INCLUDE_DIR2 cpl_port.h
    /home/nacho/software/libs/gdal-1.5.2/port
	C:/gdal/gdal-1.5.1/port
	C:/gdal/gdal-1.5.0/port
	C:/gdal/gdal-1.4.4/port
	C:/gdal/gdal-1.4.2/port
	C:/gdal/gdal-1.4.1/port
	C:/gdal-1.5.1/port
	C:/gdal-1.5.0/port
	C:/gdal-1.4.4/port
	C:/gdal-1.4.2/port
	C:/gdal-1.4.1/port
)

FIND_PATH(GDAL_INCLUDE_DIR3 ogr_api.h
    /home/nacho/software/libs/gdal-1.5.2/ogr
	C:/gdal/gdal-1.5.1/ogr
	C:/gdal/gdal-1.5.0/ogr
	C:/gdal/gdal-1.4.4/ogr
	C:/gdal/gdal-1.4.2/ogr
	C:/gdal/gdal-1.4.1/ogr
	C:/gdal-1.5.1/ogr
	C:/gdal-1.5.0/ogr
	C:/gdal-1.4.4/ogr
	C:/gdal-1.4.2/ogr
	C:/gdal-1.4.1/ogr
)

FIND_PATH(GDAL_INCLUDE_DIR4 ogrsf_frmts.h
	/home/nacho/software/libs/gdal-1.5.2/ogrsf_frmts
	C:/gdal/gdal-1.5.1/ogr/ogrsf_frmts
	C:/gdal/gdal-1.5.0/ogr/ogrsf_frmts
	C:/gdal/gdal-1.4.4/ogr/ogrsf_frmts
	C:/gdal/gdal-1.4.2/ogr/ogrsf_frmts
	C:/gdal/gdal-1.4.1/ogr/ogrsf_frmts
	C:/gdal-1.5.1/ogr/ogrsf_frmts
	C:/gdal-1.5.0/ogr/ogrsf_frmts
	C:/gdal-1.4.4/ogr/ogrsf_frmts
	C:/gdal-1.4.2/ogr/ogrsf_frmts
	C:/gdal-1.4.1/ogr/ogrsf_frmts
)


FIND_PATH(GDAL_INCLUDE_DIR5 gdalwarper.h
    /home/nacho/software/libs/gdal-1.5.2/alg
	C:/gdal/gdal-1.5.1/alg
	C:/gdal/gdal-1.5.0/alg
	C:/gdal/gdal-1.4.4/alg
	C:/gdal/gdal-1.4.2/alg
	C:/gdal/gdal-1.4.1/alg
	C:/gdal-1.5.1/alg
	C:/gdal-1.5.0/alg
	C:/gdal-1.4.4/alg
	C:/gdal-1.4.2/alg
	C:/gdal-1.4.1/alg
)


FIND_LIBRARY(GDAL_LIBRARY 
	NAMES gdal gdal1.5.2 gdal1.5.1 gdal1.5.0 gdal1.4.0 gdal1.3.2 gdal_i GDAL
	PATHS
        /home/nacho/software/libs/gdal-1.5.2/.libs
	${GDAL_DIR}/lib
	$ENV{GDAL_DIR}/lib
	$ENV{GDAL_DIR}
	$ENV{OSGDIR}/lib
	$ENV{OSGDIR}
	$ENV{OSG_ROOT}/lib
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
	C:/gdal/gdal-1.5.1
	C:/gdal/gdal-1.5.0
	C:/gdal/gdal-1.4.4
	C:/gdal/gdal-1.4.2
	C:/gdal/gdal-1.4.1
	C:/gdal-1.5.1
	C:/gdal-1.5.0
	C:/gdal-1.4.4
	C:/gdal-1.4.2
	C:/gdal-1.4.1
)
IF(HDF5)
	FIND_LIBRARY(HDF5_LIBRARY 
		NAMES hdf5dll HDF5
		PATHS
		C:/gdal/gdal-1.5.1
		C:/gdal/gdal-1.5.0
		C:/gdal/gdal-1.4.4
		C:/gdal/gdal-1.4.2
		C:/gdal/gdal-1.4.1
		C:/gdal-1.5.1
		C:/gdal-1.5.0
		C:/gdal-1.4.4
		C:/gdal-1.4.2
		C:/gdal-1.4.1
	)
ENDIF(HDF5)

IF(HDF4)
	FIND_LIBRARY(HDF4HMM_LIBRARY 
		NAMES hm422m HDF4HMM
		PATHS
		C:/gdal/gdal-1.5.1
		C:/gdal/gdal-1.5.0
		C:/gdal/gdal-1.4.4
		C:/gdal/gdal-1.4.2
		C:/gdal/gdal-1.4.1
		C:/gdal-1.5.1
		C:/gdal-1.5.0
		C:/gdal-1.4.4
		C:/gdal-1.4.2
		C:/gdal-1.4.1
	)

	FIND_LIBRARY(HDF4HDM_LIBRARY 
		NAMES hd422m HDF4HDM
		PATHS
		C:/gdal/gdal-1.5.1
		C:/gdal/gdal-1.5.0
		C:/gdal/gdal-1.4.4
		C:/gdal/gdal-1.4.2
		C:/gdal/gdal-1.4.1
		C:/gdal-1.5.1
		C:/gdal-1.5.0
		C:/gdal-1.4.4
		C:/gdal-1.4.2
		C:/gdal-1.4.1
	)
ENDIF(HDF4)

SET(GDAL_FOUND "NO")
IF(GDAL_LIBRARY AND GDAL_INCLUDE_DIR)
		SET(GDAL_FOUND "YES")
ENDIF(GDAL_LIBRARY AND GDAL_INCLUDE_DIR)

