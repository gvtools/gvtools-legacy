set(LIB_NAME jpotrace)

SET(POTRACE_VERSION "1.9")

SET(POTRACE "potrace")
SET(MKBITMAP "mkbitmap")

ADD_DEFINITIONS(-DPOTRACE=potrace)
ADD_DEFINITIONS(-DVERSION=1.9)
ADD_DEFINITIONS(-DMKBITMAP=mkbitmap)
ADD_DEFINITIONS(-DHAVE_CONFIG_H)

FILE(GLOB HEADERS "*.h*")

set (JAVA_INCLUDE_PATH $ENV{JDK_HEADER_DIR})
set (JAVA_INCLUDE_PATH2 $ENV{JDK_HEADER_DIR}/linux)

SET(SOURCES 
	potrace.c
	potrace_raster.c
	potracelib.c
	bitmap_io.c
	backend_eps.c
	backend_gimp.c
	backend_pdf.c
	backend_pgm.c
	backend_svg.c
	backend_xfig.c
	decompose.c
	trace.c
	curve.c
	flate.c
	render.c
	greymap.c
	lzw.c
	getopt.c
	getopt1.c

)

include_directories(
	${CMAKE_SOURCE_DIR}/include
	${JAVA_INCLUDE_PATH}
	${JAVA_INCLUDE_PATH2}
)

SETUP_JNILIB(${LIB_NAME})


