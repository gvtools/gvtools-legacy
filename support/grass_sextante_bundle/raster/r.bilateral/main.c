/****************************************************************************
 *
 * MODULE:       r.bilateral
 * AUTHOR(S):    Jachym Cepicky jachym.cepicky @ centrum . cz
 * PURPOSE:      Bilateral filter is edge-preserving filter, which takes spatial
 *               and color part into count
 *
 *               The result value = spatial_weight*color_weight.
 *
 *               Spatial_weight= exp(distance_from_center^2)/(2*spatial_sigma^2)
 *               Color_weight= exp(distance_between_values^2)/(2*color_sigma^2)
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
#include <grass/gis.h>

#ifndef DBG
#define DBG 0
#endif


/* function prototypes */
int D_gather (int infd, DCELL *values, DCELL **rows, int col, int row, int size);

DCELL D_bilateral (DCELL *values, double ssigma, double csigma, int size, double *weights);

void count_weights (double *weights, int size, double sigma);

int main(int argc, char *argv[])
{
	struct Cell_head cellhd; 
        struct Range range;
        char *name;                     /* input raster name */
	char *result;                   /* output raster name */
        char *mapset;                   /* mapset name */
	DCELL *inrast;                  /* input buffer */
	DCELL *outrast;                 /* output buffer */
	int row,col;
	int infd, outfd;                /* file descriptor */
	int verbose;
        double *weights;                /* array of weights */
        DCELL **D_rows;
        DCELL *tmp;
        int nrows;                      
        int ncols;
        double min, max;                /* raster map range */
        
	RASTER_MAP_TYPE data_type;      /* type of the map */
        void *values;                   /* neighborhood values */
        int n,i;                        /* number of neighborhood cells */
        int size;                       /* matrix size */
        double ssigma;                  /* sigma of the spatial part */
        double csigma;                  /* sigma of the color part */
        char title[1024];               /* map title */


	struct GModule *module;         /* GRASS module for parsing arguments */
	struct
	    {
		struct Option *input, *output;
		struct Option *sigma_s, *sigma_c, *size;
		struct Option *title;
	} parm;
	struct
	    {
		struct Flag *quiet;
		struct Flag *print_sigmas;
	} flag;

        /* initialize GIS environment */
	G_gisinit(argv[0]);     /* reads grass env, 
                                   stores program name to 
                                   G_program_name
                                   */

        /* initialize module */
	module = G_define_module();
	module->description = ("Gaussian filter for raster maps.");
					        
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

	parm.sigma_s = G_define_option() ;
	parm.sigma_s->key        = "ssigma";
	parm.sigma_s->type       = TYPE_DOUBLE;
	parm.sigma_s->required   = NO;
	parm.sigma_s->description= ("Sigma for space part of the filter\n\t(default: 0.465*((size-1)/2)");

	parm.sigma_c = G_define_option() ;
	parm.sigma_c->key        = "csigma";
	parm.sigma_c->type       = TYPE_DOUBLE;
	parm.sigma_c->required   = NO;
	parm.sigma_c->description= ("Sigma for color part of the filter\n(default: 0.465*((color_range)/2)");

        parm.size = G_define_option() ;
	parm.size->key        = "size";
	parm.size->type       = TYPE_INTEGER;
	parm.size->required   = YES;
	parm.size->description= ("Size of the matrix (odd number)");

        flag.print_sigmas = G_define_flag() ;
	flag.print_sigmas->key         = 's' ;
	flag.print_sigmas->description = "Print calculated values for sigmas" ;
        
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
        if (!parm.sigma_s->answer)
            ssigma = 0.465*((size-1)/2);
        else 
            sscanf(parm.sigma_s->answer, "%lf", &ssigma);
      
        

        /* controlling the input values */
        if (size%2 == 0) 
            G_fatal_error("Size <%d> is not odd number", size);

	/* returs NULL if the map was not found in any mapset, 
         * mapset name otherwise*/
	mapset = G_find_cell2 (name, ""); 
        if (mapset == NULL)
                G_fatal_error ("cell file [%s] not found", name);

       /* color sigma next */
        if (!parm.sigma_c->answer) {
            if (G_read_range(name, mapset, &range) < 0) 
                G_fatal_error("Could not read the raster map range");
                
            /* for raster maps with range from 0-255 the result
             * should be around 60 
             */
            min = (double)range.min;
            max = (double)range.max;
            csigma = 0.456*(max - min)/2;
        }
        else
            sscanf(parm.sigma_c->answer, "%lf", &csigma);
        
        /* print if appropriate */
        if (flag.print_sigmas->answer)
            printf("Space sigma: %f\nColor sigma: %f\n", ssigma, csigma);

 
        if (G_legal_filename (result) < 0)
                G_fatal_error ("[%s] is an illegal name", result);

        /* count weights */
        weights = (double *)malloc(size * size * sizeof(double));
        /* stores values of gauss. bell into 'weigts'*/
        count_weights(weights, size, ssigma);
        
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


        /* Allocate values buffers */
        values = (DCELL *) malloc(size * size * sizeof(DCELL));
        
        /* allocating memory for rows */
        D_rows = (DCELL **)malloc(size * sizeof(DCELL));
        for (i = 0; i < size; i++) {
            D_rows[i] = G_allocate_raster_buf(DCELL_TYPE);
        }
        
        if (values == NULL) 
            G_fatal_error("Cannot allocate memory");

        /* controlling, if we can write the raster */
	if ( (outfd = G_open_raster_new (result, data_type)) < 0)
		G_fatal_error ("Could not open <%s>",result);

        /* write first rows as NULL values */
        for (row = 0; row < size/2; row++) {
            G_set_d_null_value(outrast, ncols);
            if (G_put_d_raster_row (outfd, outrast) < 0)
                G_fatal_error ("Cannot write to <%s>",result);
        }

        /* allocate first size/2 rows */
        for (row = 0; row < size; row++) 
            if (G_get_d_raster_row(infd, D_rows[row], row) < 0)
		G_fatal_error ("Could not open <%s>",result);
            
        /****************************************************************/
        /* for each row inside the region */
	for ( row = size/2; row < nrows - size/2; row++) {
            

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

                        /* store values of the matrix into arry 'values', 'n' is
                         * number of elements of the matrix */
                        n = D_gather(infd, values, D_rows, col, row,size);
                        ((DCELL *)outrast)[col] = D_bilateral(values, ssigma, csigma, size, weights);
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
        sprintf(title, "Bilateral filter of %s with %dx%d matrix: ssigma %.3f, csigma %.3f", name, size, size, ssigma, csigma ); 
        G_put_cell_title (result, title ); 

	return 0;
}


/*
 * stores size*size adresses of cells around center pixel into array 
 * 'values'
 */
int D_gather (int infd, DCELL *values, DCELL **rows, int col, int row, int size)
{
    int n = 0;                      /* number of values = size * size */
    int mat_row, mat_col;       /* matrix row and column */

   /* adress of the center pixel */
    /*printf("%d ", ((CELL *)rows[size/2])[col] ); */
    
    /* for each matrix row */
    for (mat_row = 0; mat_row < size; mat_row++) {

        /* for each matrix column */
        for (mat_col = -size/2; mat_col <= size/2; mat_col++) {

            /* store the value of the matrix into array 'values' */
            values[n] = ((DCELL *)rows[mat_row])[mat_col+col];
            n++;
        }
    }
            
    /* return number of elements */
    return n ? n : -1;
}

DCELL D_bilateral(DCELL *values, double ssigma, double csigma, int size, double *weights)
{
    int row, col, n = 0;        /* matrix row, column and index */
    int sum = 0;
    int center = ((size * size) - 1) / 2; /* index of the central pixel */
    long double col_weight;          /* weight of the color part */
    long double sum_weights = 0;

    for (row = -size/2; row <= size/2; row++) {
        for (col = -size/2; col <= size/2; col++) {
            /* sum of all values in the matrix */
            col_weight = exp(-(values[n]*values[n] - values[center]*values[center])/(2*csigma*csigma));
            
            sum_weights += weights[n]*col_weight;
            
            sum += values[n]*weights[n]*col_weight;
            n++;
        }
    }
    
    return sum/sum_weights;
} 

/*
 * stores values of the gaussian bell into array fo 'weights'
 * and returns their sum
 */
void count_weights(double *weights, int size, double sigma)
{
    int n = 0, i, j;
    
    for (i = -size/2; i <= size/2; i++) {
        for (j = -size/2; j <= size/2; j++) {
            /* weight = 1/(2*pi*sigma*sigma) * exp(-((row*row)+(col*col))/(2 * sigma*sigma)); */
            /* gaussian  */
            weights[n] = exp(-((i*i + j*j)/(2 * sigma * sigma)));
            n++;
        }
    }
}
