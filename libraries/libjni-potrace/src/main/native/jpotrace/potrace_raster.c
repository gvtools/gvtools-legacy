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
#include <stdio.h>
#include <errno.h>
#include <math.h>
#include "getopt.h"

#include "main.h"
#include "platform.h"

#include "backend_pdf.h"
#include "backend_eps.h"
#include "backend_pgm.h"
#include "backend_svg.h"
#include "backend_gimp.h"
#include "backend_xfig.h"
#include "bitmap_io.h"
#include "auxiliary.h"

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

#if defined(_MSC_VER)
	/* replacement of Unix rint() for Windows */
	static int rint (double x)
	{
	char *buf;
	int i,dec,sig;

	buf = _fcvt(x, 0, &dec, &sig);
	i = atoi(buf);
	if(sig == 1) {
	i = i * -1;
	}
	return(i);
	}
	#define strcasecmp  stricmp
	#define strncasecmp  strnicmp
#endif

#define UNDEF ((double)(1e30))   /* a value to represent "undefined" */
#define INFTY ((double)(1e30))   /* a value to represent +infinity */

/* backends and their characteristics */
struct backend_s {
	char *name; /* name of this backend */
	char *ext; /* file extension */
	int fixed; /* fixed page size backend? */
	int pixel; /* pixel-based backend? */
	int multi; /* multi-page backend? */
	int (*init_f)(FILE *fout); /* initialization function */
	int (*page_f)(FILE *fout, potrace_path_t *plist, imginfo_t *imginfo);
	/* per-bitmap function */
	int (*term_f)(FILE *fout); /* finalization function */
	int opticurve; /* opticurve capable (true Bezier curves?) */
};
typedef struct backend_s backend_t;

static backend_t backend[] = { {"eps", ".eps", 0, 0, 0, NULL, page_eps, NULL, 1}, {"postscript", ".ps", 1, 0, 1,
		init_ps, page_ps, term_ps, 1}, {"ps", ".ps", 1, 0, 1, init_ps, page_ps, term_ps, 1}, {"pdf", ".pdf", 0, 0, 1,
		init_pdf, page_pdf, term_pdf, 1}, {"svg", ".svg", 0, 0, 0, NULL, page_svg, NULL, 1}, {"pgm", ".pgm", 0, 1, 1, NULL,
		page_pgm, NULL, 1}, {"gimppath", ".gimppath", 0, 1, 0, NULL, page_gimp, NULL, 1}, {"xfig", ".fig", 1, 0, 0, NULL,
		page_xfig, NULL, 0}, {NULL, NULL, 0, 0, 0, NULL, NULL, NULL}, };

struct info_s info;

#define COL0 "\033[G"  /* reset cursor to column 0 */

/* ---------------------------------------------------------------------- */
/* callback function for progress bar */

struct simple_progress_s {
	char name[22]; /* filename for status bar */
	double dnext; /* threshold value for next tick */
};
typedef struct simple_progress_s simple_progress_t;

static inline double double_of_dim(dim_t d, double def) {
	if (d.d) {
		return d.x * d.d;
	} else {
		return d.x * def;
	}
}

/* ---------------------------------------------------------------------- */
/* calculations with bitmap dimensions, positioning etc */

/* consider a rectangle spanned by the vectors (w,0) and (0,h). Rotate
 it counterclockwise by angle alpha. Then set the rect_t structure
 to the resulting rectangle, setting its bounding box, origin,
 x-basis and y-basis. */

static void rotate_dim(double alpha, double w, double h, rect_t *r) {
	double s, c, x0, x1, y0, y1;

	s = sin(alpha / 180 * M_PI);
	c = cos(alpha / 180 * M_PI);

	/* apply the transformation matrix to the basis vectors */
	x0 = c * w;
	x1 = s * w;
	y0 = -s * h;
	y1 = c * h;

	/* determine bounding box and origin relative to bounding box */
	r->bb[0] = fabs(x0) + fabs(y0);
	r->bb[1] = fabs(x1) + fabs(y1);
	r->orig[0] = -min(x0, 0) - min(y0, 0);
	r->orig[1] = -min(x1, 0) - min(y1, 0);
}

/* determine the dimensions of the output based on command line and
 image dimensions */
static void calc_dimensions(imginfo_t *imginfo) {
	double dim_def;
	double maxwidth, maxheight, sc;
	rect_t r;

	/* we take care of a special case: if one of the image dimensions is
	 0, we change it to 1. Such an image is empty anyway, so there
	 will be 0 paths in it. Changing the dimensions avoids division by
	 0 error in calculating scaling factors, bounding boxes and
	 such. This doesn't quite do the right thing in all cases, but it
	 is better than causing overflow errors or "nan" output in
	 backends.  Human users don't tend to process images of size 0
	 anyway; they might occur in some pipelines. */
	if (imginfo->pixwidth == 0) {
		imginfo->pixwidth = 1;
	}
	if (imginfo->pixheight == 0) {
		imginfo->pixheight = 1;
	}

	/* set the default dimension for width, height, margins */
	if (info.backend->pixel) {
		dim_def = DIM_PT;
	} else {
		dim_def = DEFAULT_DIM;
	}

	/* apply default dimension to width, height, margins */
	imginfo->width = info.width_d.x == UNDEF ? UNDEF : double_of_dim(info.width_d, dim_def);
	imginfo->height = info.height_d.x == UNDEF ? UNDEF : double_of_dim(info.height_d, dim_def);
	imginfo->lmar = info.lmar_d.x == UNDEF ? UNDEF : double_of_dim(info.lmar_d, dim_def);
	imginfo->rmar = info.rmar_d.x == UNDEF ? UNDEF : double_of_dim(info.rmar_d, dim_def);
	imginfo->tmar = info.tmar_d.x == UNDEF ? UNDEF : double_of_dim(info.tmar_d, dim_def);
	imginfo->bmar = info.bmar_d.x == UNDEF ? UNDEF : double_of_dim(info.bmar_d, dim_def);

	/* determine width and height from desired resolution / scaling
	 factor, if given */
	if (info.backend->pixel) {
		if (imginfo->width == UNDEF && info.sx != UNDEF) {
			imginfo->width = imginfo->pixwidth * info.sx;
		}
		if (imginfo->height == UNDEF && info.sy != UNDEF) {
			imginfo->height = imginfo->pixheight * info.sy;
		}
	} else {
		if (imginfo->width == UNDEF && info.rx != UNDEF) {
			imginfo->width = imginfo->pixwidth / info.rx * 72;
		}
		if (imginfo->height == UNDEF && info.ry != UNDEF) {
			imginfo->height = imginfo->pixheight / info.ry * 72;
		}
	}

	/* if one of width/height is specified, determine the other */
	if (imginfo->width == UNDEF && imginfo->height != UNDEF) {
		imginfo->width = imginfo->height / imginfo->pixheight * imginfo->pixwidth / info.stretch;
	} else
		if (imginfo->width != UNDEF && imginfo->height == UNDEF) {
			imginfo->height = imginfo->width / imginfo->pixwidth * imginfo->pixheight * info.stretch;
		}

	/* if width and height are still variable, figure them out */
	if (imginfo->width == UNDEF && imginfo->height == UNDEF) {

		if (info.backend->fixed) {

			/* in fixed-size backends, try to squeeze it between margins */
			maxwidth = UNDEF;
			maxheight = UNDEF;

			if (imginfo->lmar != UNDEF && imginfo->rmar != UNDEF) {
				maxwidth = info.paperwidth - imginfo->lmar - imginfo->rmar;
			}
			if (imginfo->bmar != UNDEF && imginfo->tmar != UNDEF) {
				maxheight = info.paperheight - imginfo->bmar - imginfo->tmar;
			}
			if (maxwidth == UNDEF && maxheight == UNDEF) {
				maxwidth = max(info.paperwidth - 144, info.paperwidth * 0.75);
				maxheight = max(info.paperheight - 144, info.paperheight * 0.75);
			}

			rotate_dim(info.angle, imginfo->pixwidth, imginfo->pixheight * info.stretch, &r);

			sc = min(maxwidth == UNDEF ? INFTY : maxwidth / r.bb[0], maxheight == UNDEF ? INFTY : maxheight / r.bb[1]);
			imginfo->width = imginfo->pixwidth * sc;
			imginfo->height = imginfo->pixheight * info.stretch * sc;

		} else
			if (info.backend->pixel) {

				/* in pixel-based backends, assume default scaling factor of 1 */

				imginfo->width = imginfo->pixwidth;
				imginfo->height = imginfo->pixheight * info.stretch;
			} else {

				/* otherwise, choose a default size based on the default paper format */

				maxwidth = max(info.paperwidth - 144, info.paperwidth * 0.75);
				maxheight = max(info.paperheight - 144, info.paperheight * 0.75);

				sc = min(maxwidth / imginfo->pixwidth, maxheight / imginfo->pixheight / info.stretch);
				imginfo->width = imginfo->pixwidth * sc;
				imginfo->height = imginfo->pixheight * info.stretch * sc;
			}
	}

	/* calculate coordinate system */
	rotate_dim(info.angle, imginfo->width, imginfo->height, &imginfo->trans);

	/* adjust margins */
	if (info.backend->fixed) {
		if (imginfo->lmar == UNDEF && imginfo->rmar == UNDEF) {
			imginfo->lmar = (info.paperwidth - imginfo->trans.bb[0]) / 2;
		} else
			if (imginfo->lmar == UNDEF) {
				imginfo->lmar = (info.paperwidth - imginfo->trans.bb[0] - imginfo->rmar);
			} else
				if (imginfo->lmar != UNDEF && imginfo->rmar != UNDEF) {
					imginfo->lmar += (info.paperwidth - imginfo->trans.bb[0] - imginfo->lmar - imginfo->rmar) / 2;
				}
		if (imginfo->bmar == UNDEF && imginfo->tmar == UNDEF) {
			imginfo->bmar = (info.paperheight - imginfo->trans.bb[1]) / 2;
		} else
			if (imginfo->bmar == UNDEF) {
				imginfo->bmar = (info.paperheight - imginfo->trans.bb[1] - imginfo->tmar);
			} else
				if (imginfo->bmar != UNDEF && imginfo->tmar != UNDEF) {
					imginfo->bmar += (info.paperheight - imginfo->trans.bb[1] - imginfo->bmar - imginfo->tmar) / 2;
				}
	} else {
		if (imginfo->lmar == UNDEF) {
			imginfo->lmar = 0;
		}
		if (imginfo->rmar == UNDEF) {
			imginfo->rmar = 0;
		}
		if (imginfo->bmar == UNDEF) {
			imginfo->bmar = 0;
		}
		if (imginfo->tmar == UNDEF) {
			imginfo->tmar = 0;
		}
	}
}

static FILE *my_fopen_read(const char *filename) {
	if (filename == NULL || strcmp(filename, "-") == 0) {
		return stdin;
	}
	return fopen(filename, "rb");
}

static FILE *my_fopen_write(const char *filename) {
	if (filename == NULL || strcmp(filename, "-") == 0) {
		return stdout;
	}
	return fopen(filename, "wb");
}

/* close a file, but do nothing is filename is NULL or "-" */
static void my_fclose(FILE *f, const char *filename) {
	if (filename == NULL || strcmp(filename, "-") == 0) {
		return;
	}
	fclose(f);
}

/* print a simple progress bar. This is a callback function that is
 potentially called often; thus, it has been optimized for the
 typical case, which is when the progress bar does not need updating. */
static void simple_progress(double d, void *data) {
	simple_progress_t *p = (simple_progress_t *) data;
	static char b[] = "========================================";
	int tick; /* number of visible tickmarks, 0..40 */
	int perc; /* visible percentage, 0..100 */

	/* note: the 0.01 and 0.025 ensure that we always end on 40
	 tickmarks and 100%, despite any rounding errors. The 0.995
	 ensures that tick always increases when d >= p->dnext. */
	if (d >= p->dnext) {
		tick = (int) floor(d * 40 + 0.01);
		perc = (int) floor(d * 100 + 0.025);
		fprintf(stderr, "%-21s |%-40s| %d%% "COL0"", p->name, b + 40 - tick, perc);
		p->dnext = (tick + 0.995) / 40.0;
	}
}

/* Initialize parameters for simple progress bar. The caller passes an
 allocated simple_progress_t structure to avoid having to malloc it
 here and free it later. */
static inline void init_progress(potrace_progress_t *prog, simple_progress_t *p, const char *filename, int count) {
	const char *q, *s;
	int len;

	/* initialize callback function's data */
	p->dnext = 0;

	if (count != 0) {
		sprintf(p->name, " (p.%d):", count + 1);
	} else {
		s = filename;
		if ((q = strrchr(s, '/')) != NULL) {
			s = q + 1;
		}
		len = strlen(s);
		strncpy(p->name, s, 21);
		p->name[20] = 0;
		if (len > 20) {
			p->name[17] = '.';
			p->name[18] = '.';
			p->name[19] = '.';
		}
		strcat(p->name, ":");
	}

	/* initialize progress parameters */
	prog->callback = &simple_progress;
	prog->data = (void *) p;
	prog->min = 0.0;
	prog->max = 1.0;
	prog->epsilon = 0.0;

	/* draw first progress bar */
	simple_progress(0.0, prog->data);
	return;
}

struct pageformat_s {
	char *name;
	int w, h;
};
typedef struct pageformat_s pageformat_t;

/* dimensions of the various page formats, in postscript points */
static pageformat_t pageformat[] = {
	{ "a4",        595,  842 },
	{ "a3",        842, 1191 },
	{ "a5",        421,  595 },
	{ "b5",        516,  729 },
	{ "letter",    612,  792 },
	{ "legal",     612, 1008 },
	{ "tabloid",   792, 1224 },
	{ "statement", 396,  612 },
	{ "executive", 540,  720 },
	{ "folio",     612,  936 },
	{ "quarto",    610,  780 },
	{ "10x14",     720, 1008 },
	{ NULL, 0, 0 },
};

struct turnpolicy_s {
	char *name;
	int n;
};
typedef struct turnpolicy_s turnpolicy_t;

/* names of turn policies */
static turnpolicy_t turnpolicy[] = {
	{"black",    POTRACE_TURNPOLICY_BLACK},
	{"white",    POTRACE_TURNPOLICY_WHITE},
	{"left",     POTRACE_TURNPOLICY_LEFT},
	{"right",    POTRACE_TURNPOLICY_RIGHT},
	{"minority", POTRACE_TURNPOLICY_MINORITY},
	{"majority", POTRACE_TURNPOLICY_MAJORITY},
	{"random",   POTRACE_TURNPOLICY_RANDOM},
	{NULL, 0},
};

static dim_t parse_dimension(char *s, char **endptr) {
	char *p;
	dim_t res;

	res.x = strtod(s, &p);
	res.d = 0;
	if (p!=s) {
		if (!strncasecmp(p, "in", 2)) {
			res.d = DIM_IN;
			p += 2;
		} else if (!strncasecmp(p, "cm", 2)) {
			res.d = DIM_CM;
			p += 2;
		} else if (!strncasecmp(p, "mm", 2)) {
			res.d = DIM_MM;
			p += 2;
		} else if (!strncasecmp(p, "pt", 2)) {
			res.d = DIM_PT;
			p += 2;
		}
	}
	if (endptr!=NULL) {
		*endptr = p;
	}
	return res;
}

/* parse a pair of dimensions, such as "8.5x11in", "30mmx4cm" */
static void parse_dimensions(char *s, char **endptr, dim_t *dxp, dim_t *dyp) {
	char *p, *q;
	dim_t dx, dy;

	dx = parse_dimension(s, &p);
	if (p==s) {
		goto fail;
	}
	if (*p != 'x') {
		goto fail;
	}
	p++;
	dy = parse_dimension(p, &q);
	if (q==p) {
		goto fail;
	}
	if (dx.d && !dy.d) {
		dy.d = dx.d;
	} else if (!dx.d && dy.d) {
		dx.d = dy.d;
	}
	*dxp = dx;
	*dyp = dy;
	if (endptr != NULL) {
		*endptr = q;
	}
	return;

 fail:
	dx.x = dx.d = dy.x = dy.d = 0;
	*dxp = dx;
	*dyp = dy;
	if (endptr != NULL) {
		*endptr = s;
	}
	return;
}

#define OPT_GROUP     300
#define OPT_OPAQUE    301
#define OPT_FILLCOLOR 302
#define OPT_PROGRESS  303

static struct option longopts[] = {
	{"help",          0, 0, 'h'},
	{"version",       0, 0, 'v'},
	{"license",       0, 0, 'l'},
	{"show-defaults", 0, 0, 'V'},
	{"progress",      0, 0, OPT_PROGRESS},
	{"width",         1, 0, 'W'},
	{"height",        1, 0, 'H'},
	{"resolution",    1, 0, 'r'},
	{"scale",         1, 0, 'x'},
	{"stretch",       1, 0, 'S'},
	{"margin",        1, 0, 'M'},
	{"leftmargin",    1, 0, 'L'},
	{"rightmargin",   1, 0, 'R'},
	{"topmargin",     1, 0, 'T'},
	{"bottommargin",  1, 0, 'B'},
	{"rotate",        1, 0, 'A'},
	{"pagesize",      1, 0, 'P'},
	{"turdsize",      1, 0, 't'},
	{"unit",          1, 0, 'u'},
	{"cleartext",     0, 0, 'c'},
	{"level2",        0, 0, '2'},
	{"level3",        0, 0, '3'},
	{"eps",           0, 0, 'e'},
	{"postscript",    0, 0, 'p'},
	{"svg",           0, 0, 's'},
	{"pgm",           0, 0, 'g'},
	{"backend",       1, 0, 'b'},
	{"debug",         1, 0, 'd'},
	{"color",         1, 0, 'C'},
	{"fillcolor",     1, 0, OPT_FILLCOLOR},
	{"turnpolicy",    1, 0, 'z'},
	{"gamma",         1, 0, 'G'},
	{"longcurve",     0, 0, 'n'},
	{"longcoding",    0, 0, 'q'},
	{"alphamax",      1, 0, 'a'},
	{"opttolerance",  1, 0, 'O'},
	{"output",        1, 0, 'o'},
	{"blacklevel",    1, 0, 'k'},
	{"invert",        0, 0, 'i'},
	{"opaque",        0, 0, OPT_OPAQUE},
	{"group",         0, 0, OPT_GROUP},

	{0, 0, 0, 0}
};

static char *shortopts = "hvlVW:H:r:x:S:M:L:R:T:B:A:P:t:u:c23epsgb:d:C:z:G:nqa:O:o:k:i";

static void dopts(int ac, char *av[]) {
	int c;
	char *p;
	int i, j, r;
	dim_t dim, dimx, dimy;
	int matches, bestmatch;

	/* defaults */
	info.backend = &backend[0];

	info.debug = 0;
	info.width_d.x = UNDEF;
	info.height_d.x = UNDEF;
	info.rx = UNDEF;
	info.ry = UNDEF;
	info.sx = UNDEF;
	info.sy = UNDEF;
	info.stretch = 1;
	info.lmar_d.x = UNDEF;
	info.rmar_d.x = UNDEF;
	info.tmar_d.x = UNDEF;
	info.bmar_d.x = UNDEF;
	info.angle = 0;
	info.paperwidth = DEFAULT_PAPERWIDTH;
	info.paperheight = DEFAULT_PAPERHEIGHT;
	info.unit = 10;
	info.compress = 1;
	info.pslevel = 2;
	info.color = 0x000000;
	info.gamma = 2.2;
	info.param = potrace_param_default();
	if (!info.param) {
		fprintf(stderr, ""POTRACE": %s\n", strerror(errno));
		exit(1);
	}
	info.longcoding = 0;
	info.outfile = NULL;
	info.blacklevel = 0.5;
	info.invert = 0;
	info.opaque = 0;
	info.group = 0;
	info.fillcolor = 0xffffff;
	info.progress = 0;

	while ((c = getopt_long(ac, av, shortopts, longopts, NULL)) != -1) {
		switch (c) {
			case OPT_PROGRESS:
				info.progress = 1;
				break;
			case 'W':
				info.width_d = parse_dimension(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid dimension -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'H':
				info.height_d = parse_dimension(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid dimension -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'r':
				parse_dimensions(optarg, &p, &dimx, &dimy);
				if (*p == 0 && dimx.d == 0 && dimy.d == 0) {
					info.rx = dimx.x;
					info.ry = dimy.x;
					break;
				}
				dim = parse_dimension(optarg, &p);
				if (*p == 0 && dim.d == 0) {
					info.rx = info.ry = dim.x;
					break;
				}
				fprintf(stderr, ""POTRACE": invalid resolution -- %s\n", optarg);
				exit(1);
				break;
			case 'x':
				parse_dimensions(optarg, &p, &dimx, &dimy);
				if (*p == 0 && dimx.d == 0 && dimy.d == 0) {
					info.sx = dimx.x;
					info.sy = dimy.x;
					break;
				}
				dim = parse_dimension(optarg, &p);
				if (*p == 0 && dim.d == 0) {
					info.sx = info.sy = dim.x;
					break;
				}
				fprintf(stderr, ""POTRACE": invalid scaling factor -- %s\n", optarg);
				exit(1);
				break;
			case 'S':
				info.stretch = atof(optarg);
				break;
			case 'M':
				info.lmar_d = parse_dimension(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid dimension -- %s\n", optarg);
					exit(1);
				}
				info.rmar_d = info.tmar_d = info.bmar_d = info.lmar_d;
				break;
			case 'L':
				info.lmar_d = parse_dimension(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid dimension -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'R':
				info.rmar_d = parse_dimension(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid dimension -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'T':
				info.tmar_d = parse_dimension(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid dimension -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'B':
				info.bmar_d = parse_dimension(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid dimension -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'A':
				info.angle = strtod(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid angle -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'P':
				matches = 0;
				bestmatch = 0;
				for (i=0; pageformat[i].name!=NULL; i++) {
					if (strcasecmp(pageformat[i].name, optarg)==0) {
						matches = 1;
						bestmatch = i;
						break;
					} else if (strncasecmp(pageformat[i].name, optarg, strlen(optarg))==0) {
						/* don't allow partial match on "10x14" */
						if (optarg[0] != '1') {
							matches++;
							bestmatch = i;
						}
					}
				}
				if (matches == 1) {
					info.paperwidth = pageformat[bestmatch].w;
					info.paperheight = pageformat[bestmatch].h;
					break;
				}
				parse_dimensions(optarg, &p, &dimx, &dimy);
				if (*p == 0) {
					info.paperwidth = (int)rint(double_of_dim(dimx, DEFAULT_DIM));
					info.paperheight = (int)rint(double_of_dim(dimy, DEFAULT_DIM));
					break;
				}
				if (matches == 0) {
					fprintf(stderr, ""POTRACE": unrecognized page format -- %s\n", optarg);
				} else {
					fprintf(stderr, ""POTRACE": ambiguous page format -- %s\n", optarg);
				}
				j = fprintf(stderr, "Use one of: ");
				for (i=0; pageformat[i].name!=NULL; i++) {
					if (j + strlen(pageformat[i].name) > 75) {
						fprintf(stderr, "\n");
						j = 0;
					}
					j += fprintf(stderr, "%s, ", pageformat[i].name);
				}
				fprintf(stderr, "or specify <dim>x<dim>.\n");
				exit(1);
				break;
			case 't':
				info.param->turdsize = atoi(optarg);
				break;
			case 'u':
				info.unit = strtod(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid unit -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'c':
				info.pslevel = 2;
				info.compress = 0;
				break;
			case '2':
				info.pslevel = 2;
				info.compress = 1;
				break;
			case '3':
#ifdef HAVE_ZLIB
				info.pslevel = 3;
				info.compress = 1;
#else
				fprintf(stderr, ""POTRACE": option -3 not supported, using -2 instead.\n");
				info.pslevel = 2;
				info.compress = 1;
#endif
				break;
			case 'd':
				info.debug = atoi(optarg);
				break;
			case 'z':
				matches = 0;
				bestmatch = 0;
				for (i=0; turnpolicy[i].name!=NULL; i++) {
					if (strcasecmp(turnpolicy[i].name, optarg)==0) {
						matches = 1;
						bestmatch = i;
						break;
					} else if (strncasecmp(turnpolicy[i].name, optarg, strlen(optarg))==0) {
						matches++;
						bestmatch = i;
					}
				}
				if (matches == 1) {
					info.param->turnpolicy = turnpolicy[bestmatch].n;
					break;
				}
				if (matches == 0) {
					fprintf(stderr, ""POTRACE": unrecognized turnpolicy -- %s\n", optarg);
				} else {
					fprintf(stderr, ""POTRACE": ambiguous turnpolicy -- %s\n", optarg);
				}
				j = fprintf(stderr, "Use one of: ");
				for (i=0; turnpolicy[i].name!=NULL; i++) {
					if (j + strlen(turnpolicy[i].name) > 75) {
						fprintf(stderr, "\n");
						j = 0;
					}
					j += fprintf(stderr, "%s%s", turnpolicy[i].name, turnpolicy[i+1].name ? ", " : "");
				}
				fprintf(stderr, ".\n");
				exit(1);
				break;
			case 'G':
				info.gamma = atof(optarg);
				break;
			case 'n':
				info.param->opticurve = 0;
				break;
			case 'q':
				info.longcoding = 1;
				break;
			case 'a':
				info.param->alphamax = strtod(optarg, &p);

				if (*p) {
					fprintf(stderr, ""POTRACE": invalid alphamax -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'O':
				info.param->opttolerance = strtod(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid opttolerance -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'o':
				free(info.outfile);
				info.outfile = strdup(optarg);
				break;
			case 'k':
				info.blacklevel = strtod(optarg, &p);
				if (*p) {
					fprintf(stderr, ""POTRACE": invalid blacklevel -- %s\n", optarg);
					exit(1);
				}
				break;
			case 'i':
				info.invert = 1;
				break;
			case OPT_OPAQUE:
				info.opaque = 1;
				break;
			case OPT_GROUP:
				info.group = 1;
				break;
			case '?':
				fprintf(stderr, "Try --help for more info\n");
				exit(1);
				break;
			default:
				fprintf(stderr, ""POTRACE": Unimplemented option -- %c\n", c);
				exit(1);
		}
	}
	info.infiles = &av[optind];
	info.infilecount = ac-optind;
}

static double* showList(potrace_path_t *plist) {
	int i;
	int count;
	potrace_path_t *p;
	int n, *tag;
	potrace_dpoint_t (*c)[3];
	double *memory;

	p = plist;
	count = 0;

	// Comprobamos cuanto va a ocupar la memoria a rellenar
	while (p != NULL) {
		n = p->curve.n;
		tag = p->curve.tag;
		c = p->curve.c;
		count += 3; // moveTo(x, y)
		for (i = 0; i < n; i++) {
			switch (tag[i]) {
				case POTRACE_CORNER:
					count += 3; // lineTo(x, y)
					count += 3; // lineTo(x, y)
					break;
				case POTRACE_CURVETO:
					count += 7; // curveTo(x1, y1, x2, y2, x3, y3)
					break;
			}
		}
		// at the end of a group of a positive path and its negative children, fill
		if (p->next == NULL || p->next->sign == '+')
			count++; // closePath()
		p = p->next;
	}
	count++;

	// Creamos la zona de memoria que devolveremos a Java
	memory = (double *) malloc(sizeof(double) * count);

	// Rellenamos la memoria con la lista de operaciones a realizar
	p = plist;
	count = 1;
	while (p != NULL) {
		n = p->curve.n;
		tag = p->curve.tag;
		c = p->curve.c;

		// moveTo(x, y);
		memory[count++] = 0;
		memory[count++] = c[n - 1][2].x;
		memory[count++] = c[n - 1][2].y;
		for (i = 0; i < n; i++) {
			switch (tag[i]) {
				case POTRACE_CORNER:
					// lineTo(x, y);
					memory[count++] = 1;
					memory[count++] = c[i][1].x;
					memory[count++] = c[i][1].y;
					// lineTo(x, y);
					memory[count++] = 1;
					memory[count++] = c[i][2].x;
					memory[count++] = c[i][2].y;
					break;
				case POTRACE_CURVETO:
					// curveTo(x1, y1, x2, y2, x3, y3);
					memory[count++] = 2;
					memory[count++] = c[i][0].x;
					memory[count++] = c[i][0].y;
					memory[count++] = c[i][1].x;
					memory[count++] = c[i][1].y;
					memory[count++] = c[i][2].x;
					memory[count++] = c[i][2].y;
					break;
			}
		}
		if (p->next == NULL || p->next->sign == '+')
			memory[count++] = 3;
		p = p->next;
	}
	memory[0] = count;
	return memory;
}

static double* process_buffer(const long *inbuffer, int width, int height) {
	int r;
	double *memory;
	potrace_bitmap_t *bm = NULL;
	imginfo_t imginfo;
	int eof_flag = 0; /* to indicate premature eof */
	potrace_state_t *st;
	simple_progress_t progress_data;
	int dy;

	bm = (potrace_bitmap_t *) malloc (sizeof(potrace_bitmap_t));
	bm->w = width;
	bm->h = height;
	dy = (width + 32 - 1) / 32;
	bm->dy = dy;

	bm->map = (potrace_word *) inbuffer;

	/* prepare progress bar, if requested */
	info.param->progress.callback = NULL;

	// Invertir colores, el negro es blanco y el blanco es negro
	if (info.invert)
		bm_invert(bm);

	/* process the image */

	st = potrace_trace(info.param, bm);
	if (!st || st->status != POTRACE_STATUS_OK) {
		fprintf(stderr, ""POTRACE": %s\n", strerror(errno));
		exit(2);
	}
	/* calculate image dimensions */
	imginfo.pixwidth = bm->w;
	imginfo.pixheight = bm->h;
	calc_dimensions(&imginfo);
	
	// rgaitan: We cannot use here bm_free (becouse it
	// frees also the bm->map (that is a inbuffer, that in this
	// case comes from java so, FAIL!.
	//bm_free(bm);

	memory = showList(st->plist);

	potrace_state_free(st);

	if (info.progress) {
		fprintf(stderr, "\n");
	}

	return memory;
}

double* vectorizarBuffer(const long *bufferIn, int width, int height, int argc, char *argv[]) {
	/* platform-specific initializations, e.g., set file i/o to binary */
	platform_init();

	// Reiniciar el contador externo de los parametros
	optind = 1;

	/* process options */
	dopts(argc, argv);

	return process_buffer(bufferIn, width, height);
}
