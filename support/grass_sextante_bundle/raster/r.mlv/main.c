/****************************************************************************
 *
 * MODULE:       r.mlv
 * AUTHOR(S):    Jachym Cepicky 
 *               jachym.cepicky at centrum dot cz
 *               with hints from: r.example
 * PURPOSE:      Mean of least variance filter
 *
 * COPYRIGHT:    (C) 2002 by the GRASS Development Team
 *
 *               This program is free software under the GNU General Public
 *   	    	 License (>=v2). Read the file COPYING that comes with GRASS
 *   	    	 for details.
 *
 *****************************************************************************/


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <float.h>

#include <grass/gis.h>

#ifndef DBG
#define DBG 0
#endif

/* 
 * local variables 
 */
int nrows;
int ncols;

/* function prototypes */
DCELL mlv(int infd, DCELL *values, DCELL **rows, int col, int row, int size);

int main(int argc, char *argv[])
{
	struct Cell_head cellhd; 
        char *name;                     /* input raster name */
	char *result;                   /* output raster name */
        char *mapset;                   /* mapset name */
	DCELL *inrast;                  /* input buffer */
	DCELL *outrast;                 /* output buffer */
	int row,col;
	int infd, outfd;                /* file descriptor */
	int verbose;
        DCELL **D_rows;
        DCELL *tmp;
        
	RASTER_MAP_TYPE data_type;      /* type of the map */
        DCELL *values;                  /* neighborhood values */
        int i;                          /* number of neighborhood cells */
        int size;                       /* matrix size */
        char title[1024];               /* map title */


	struct GModule *module;         /* GRASS module for parsing arguments */
	struct
	    {
		struct Option *input, *output;
		struct Option *size;
		struct Option *title;
	} parm;
	struct
	    {
		struct Flag *quiet;
	} flag;

        /* initialize GIS environment */
	G_gisinit(argv[0]);     /* reads grass env, 
                                   stores program name to 
                                   G_program_name
                                   */

        /* initialize module */
	module = G_define_module();
	module->description = ("Mean of least variance filter for raster maps.");
					        
	/* Define the different options */
	parm.input = G_define_option() ;
	parm.input->key        = "input";
	parm.input->type       = TYPE_STRING;
	parm.input->required   = YES;
	parm.input->description= ("Name of an input layer" );

	parm.output = G_define_option() ;
	parm.output->key        = "output";
	parm.output->type       = TYPE_STRING;
	parm.output->required   = YES;
	parm.output->description= ("Name of an output layer");

        parm.size = G_define_option() ;
	parm.size->key        = "size";
	parm.size->type       = TYPE_INTEGER;
	parm.size->required   = YES;
	parm.size->description= ("Size of the matrix (odd number)");

	/* Define the different flags */
	flag.quiet = G_define_flag() ;
	flag.quiet->key         = 'q' ;
	flag.quiet->description = "Quiet" ;
        
        /* options and flags pareser */
	if (G_parser(argc, argv))
		exit (-1);
		
        /* stores options and flags to variables */
	name    = parm.input->answer;
	result  = parm.output->answer;
	verbose = (! flag.quiet->answer);
        sscanf(parm.size->answer, "%d", &size);
        /* real size of the window is size*2-1 */
        size = (size*2 - 1);


        /* controlling the input values */
        if (size%2 == 0) 
            G_fatal_error("Size <%d> is not odd number", size);

	/* returs NULL if the map was not found in any mapset, 
         * mapset name otherwise*/
	mapset = G_find_cell2 (name, ""); 
        if (mapset == NULL)
                G_fatal_error ("cell file [%s] not found", name);
        
        if (G_legal_filename (result) < 0)
                G_fatal_error ("[%s] is an illegal name", result);

	/* determine the inputmap type (CELL/FCELL/DCELL) */
	data_type = G_raster_map_type(name, mapset);

        /* G_open_cell_old - returns file destriptor (>0) */
	if ( (infd = G_open_cell_old (name, mapset)) < 0)
		G_fatal_error ("Cannot open cell file [%s]", name);


        /* controlling, if we can open input raster */
	if (G_get_cellhd (name, mapset, &cellhd) < 0)
		G_fatal_error ("Cannot read file header of [%s]", name);

	/* Allocate input buffer */
	inrast = G_allocate_raster_buf(data_type);
	
	/* Allocate output buffer, use input map data_type */
	nrows = G_window_rows();
	ncols = G_window_cols();
	outrast = G_allocate_d_raster_buf();


        /* Allocate values buffers
         * NOTE: size is (size*2-1) now */
        values = (DCELL *) malloc(size * size * sizeof(DCELL));
        
        if (values == NULL) 
            G_fatal_error("Cannot allocate memory");

        /* allocating memory for rows */
        D_rows = (DCELL **)malloc(size * sizeof(DCELL));
        for (i = 0; i < size; i++) {
            D_rows[i] = G_allocate_d_raster_buf();
        }
        
        /* controlling, if we can write the raster */
	if ( (outfd = G_open_raster_new (result, data_type)) < 0)
		G_fatal_error ("Could not open <%s>",result);

        /* write first rows as NULL values */
        for (row = 0; row < size/2; row++) {
            G_set_d_null_value(outrast, ncols);
            if (G_put_d_raster_row (outfd, outrast) < 0)
                G_fatal_error ("Cannot write to <%s>",result);
        }

        /* allocate first size-1 rows */
        for (row = 0; row < size; row++) 
            if (G_get_d_raster_row(infd, D_rows[row], row) < 0)
		G_fatal_error ("Could not open <%s>",result);
            
        /****************************************************************/
        /* for each row inside the region */
	for (row = size/2; row < nrows - size/2; row++) {
            

		if (verbose)
		    G_percent (row, nrows, 2);
                
                /* allocate new last row */
               G_get_d_raster_row(infd, D_rows[size-1], row+(size/2));

                /*process the data */
		for (col=0; col < ncols; col++){

                    /* skip the outside columns */
                    if ( (col - size/2) < 0 || ncols <= (col + size/2)) {
                        G_set_d_null_value(outrast, 1);
                    }
                    /* work only with columns, which are inside */
                    else {

                        /* find the least of variance */
                        ((DCELL *)outrast)[col] = mlv(infd, values, D_rows, col, row,size);
		    }
                } /* for each column */

                /* write raster row to output raster file */
		G_put_d_raster_row (outfd, outrast);

                /* switch rows */
		tmp = D_rows[0];
                for (i = 0; i < size; i++){
                   D_rows[i] = D_rows[i + 1];
                }
		D_rows[size-1] = tmp;

	} /* for each row */
        
        /* write last rows as NULL values */
        for (i = 0; i < size/2; i++) {
            G_set_d_null_value(outrast, ncols);
            G_put_d_raster_row (outfd, outrast);
        }
 
        /* memory cleaning */
	G_free(outrast);
	G_free(inrast);
        G_free(values);

        for (i = 0; i < size; i++) {
            G_free(D_rows[i]);
        }
        free((void *) D_rows);


        /* closing rastr files */
	G_close_cell (infd);
	G_close_cell (outfd);

        /* set the map title */
        sprintf(title, "Mean of least variance filter of %s with %dx%d matrix", name, (size+1)/2, (size+1)/2); 
        G_put_cell_title (result, title ); 

	return 0;
}


/*
 *
 */
DCELL mlv(int infd, DCELL *values, DCELL **rows, int col, int row, int size)
{
    int n = 0;                  /* number of values = size * size */
    double minvar = DBL_MAX;    /* least variance */
    double variance;            /* resulting varinace */
    double *ave;                /* mean of the subwindow */
    int winrow, wincol;         /* row and column of the window */
    int subrow, subcol = 0;     /* row and column of the subwindow */
    int subsize = (size + 1)/2; /* size of the subwindow */
    long double sum = 0.0;      /* sum of values of the subwindow */
    double d;                   /* tmp variance */
    int aves = 0;               /* number of averages */
    int minvar_index = 0;       /* index of least variance subwindow */

    /* how many averages are here ? */
    if((ave = (double *)malloc(subsize * subsize * sizeof(double))) == NULL)
        G_fatal_error("Can not allocate memory");
    
    /*printf("%f\n", ((DCELL *)rows[0])[0+col]); */

    /* for each matrix row */
    for (winrow = -subsize/2; winrow <= subsize/2; winrow++) {
    
        /* for each matrix column */
        for (wincol = -subsize/2; wincol <= subsize/2; wincol++) {

            /* mean */
            n = 0;
            for (subrow = -subsize/2; subrow <= subsize/2; subrow++) {
                
                for (subcol = -subsize/2; subcol <= subsize/2; subcol++) {

                   sum += ((DCELL *)rows[size/2 + winrow + subrow])[col + wincol + subcol];

                    n++;
                }
            }

            ave[aves] = sum / n;
            sum = 0.0;

            /* variance */
            for (subrow = -subsize/2; subrow < subsize/2; subrow++) {
                
                for (subrow = -subsize/2; subrow < subsize/2; subrow++) {

                   d = ((DCELL *)rows[size/2 + winrow + subrow])[col +  wincol + subcol]; 
                   d -= ave[aves];

                    sum += d*d;
                }
            }
            variance = sum/n;
            variance = abs(((int )variance));
            sum = 0.0;
            
            if (variance < minvar) {
                minvar = variance;
                minvar_index = aves;
            }
            aves++;
        }
    }
    
    free((void *)ave);

    /* return minvar */
    return ave[minvar_index];
}
