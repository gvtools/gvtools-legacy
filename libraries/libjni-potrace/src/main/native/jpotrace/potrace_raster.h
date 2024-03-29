/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 */
#ifndef POTRACE_RASTER_H
#define POTRACE_RASTER_H

#include "potracelib.h"

/* structure to hold a tilted rectangle */
struct rect_s {
	double bb[2];    /* dimensions of bounding box */
	double orig[2];  /* origin relative to bounding box */
};
typedef struct rect_s rect_t;

#ifdef USE_A4
#define DEFAULT_PAPERWIDTH 595
#define DEFAULT_PAPERHEIGHT 842
#define DEFAULT_PAPERFORMAT "a4"
#else
#define DEFAULT_PAPERWIDTH 612
#define DEFAULT_PAPERHEIGHT 792
#define DEFAULT_PAPERFORMAT "letter"
#endif

/* structure to hold a dimensioned value */
struct dim_s {
	double x; /* value */
	double d; /* dimension (in pt), or 0 if not given */
};
typedef struct dim_s dim_t;

/* structure to hold command line options */
struct info_s {
	struct backend_s *backend;  /* type of backend (eps,ps,pgm etc) */
	potrace_param_t *param;  /* tracing parameters, see potracelib.h */
	int debug;         /* type of output (0-2) (for BACKEND_PS/EPS only) */
	dim_t width_d;     /* desired width of image */
	dim_t height_d;    /* desired height of image */
	double rx;         /* desired x resolution (in dpi) */
	double ry;         /* desired y resolution (in dpi) */
	double sx;         /* desired x scaling factor */
	double sy;         /* desired y scaling factor */
	double stretch;    /* ry/rx, if not otherwise determined */
	dim_t lmar_d, rmar_d, tmar_d, bmar_d;   /* margins */
	double angle;      /* rotate by this many degrees */
	int paperwidth, paperheight;  /* paper size for ps backend (in pt) */
	double unit;       /* granularity of output grid */
	int compress;      /* apply compression? */
	int pslevel;       /* postscript level to use: affects only compression */
	int color;         /* rgb color code 0xrrggbb: line color */
	int fillcolor;     /* rgb color code 0xrrggbb: fill color */
	double gamma;      /* gamma value for pgm backend */
	int longcoding;    /* do not optimize for file size? */
	char *outfile;     /* output filename, if given */
	char **infiles;    /* array of input filenames */
	int infilecount;   /* number of input filenames */
	double blacklevel; /* 0 to 1: black/white cutoff in input file */
	int invert;        /* invert bitmap? */
	int opaque;        /* paint white shapes opaquely? */
	int group;         /* group paths together? */
	int progress;      /* should we display a progress bar? */
};
typedef struct info_s info_t;

extern info_t info;

/* structure to hold per-image information, set e.g. by calc_dimensions */
struct imginfo_s {
	int pixwidth;        /* width of input pixmap */
	int pixheight;       /* height of input pixmap */
	double width;        /* desired width of image (in pt or pixels) */
	double height;       /* desired height of image (in pt or pixels) */
	double lmar, rmar, tmar, bmar;   /* requested margins (in pt) */
	rect_t trans;        /* specify relative position of a tilted rectangle */
};
typedef struct imginfo_s imginfo_t;

double* vectorizarBuffer(const long *cbufferIn, int width, int height, int argc, char *argv[]);

#endif /* POTRACE_RASTER_H */
