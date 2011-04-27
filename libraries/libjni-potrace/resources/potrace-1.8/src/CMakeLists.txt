
IF(DEFINED MAKELIBRARY)
	ADD_LIBRARY(
		potrace SHARED
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
	)

	ADD_LIBRARY(
		potrace180 STATIC
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
ELSE(DEFINED ${MAKELIBRARY})
	ADD_EXECUTABLE(
		${POTRACE}
		main.c
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
	)

	ADD_EXECUTABLE(
		${MKBITMAP}
		mkbitmap.c
		greymap.c
		bitmap_io.c
	)

	ADD_EXECUTABLE(
		potracelib_demo
		potracelib_demo.c
		potracelib.c
		decompose.c
		trace.c
		curve.c
	)
ENDIF(DEFINED ${MAKELIBRARY})
