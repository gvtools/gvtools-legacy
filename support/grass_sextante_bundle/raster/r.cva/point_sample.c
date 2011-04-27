/***********************************************************************/
/*
  point_sample.c

  Updated by Mark Lake on 17/8/01 for GRASS 5.x

  CONTAINS

  1) point_sample                                                      */

/***********************************************************************/

/***********************************************************************/
/*
  point_sample

  Called from:
  
  1) main                   main.c
 
  Calls:
 
  1) line_of_sight          line_of_site.c
  2) count_visible_cells    count_visible_cells.c
  3) cumulate_visible_cells cumulate_visible_cells.c
  4) delete_lists           delete_lists.c                             */

/***********************************************************************/

#include <stdlib.h>
#include <string.h>

#include <grass/gis.h>
#include <grass/segment.h>
#include <grass/Vect.h>
#include <grass/dbmi.h>
#include <grass/glocale.h>

#include "config.h"
#include "point.h"
#include "global_vars.h"

#include "init_segment_file.h"
#include "line_of_sight.h"
#include "count_visible_cells.h"
#include "cumulate_visible_cells.h"
#include "delete_lists.h"

/***
 *
 *  THE FOLLOWING TWO FUNCS ARE HELPERS FOR ATTRIBUTE DATA HANDLING
 *  
 ***/


/* Check if an attribute '*name' exists in the attribute table and
   is of numeric type (int or double).
   Database '*driver' and struct '*field' must have been initialized properly
   before calling this function!
   
   Returns:
      1   = attribute with this name exists and is of numeric type
      0   = attribute with this name does not exist
      -1  = attribute exists but is of wrong type
      -2  = no records found for this attribute in current layer
      -3  = error reading attribute
*/
int check_att ( char* name, dbDriver *driver, dbTable *table, struct field_info *field, dbCatValArray *Cvarr ) {
      int dbcol, dbncols;
      int found;
      int ctype;
      int nrec = 0;
      
          
      /* number of attributes in the current table */
      dbncols = db_get_table_number_of_columns(table);

      /* step thru all cols in table to see if the att we want is there */
      found = 0;
      /* step thru all attributes in the database table which the current layer is connected to */
      for (dbcol = 0; dbcol < dbncols; dbcol++) {
        if ( !strcmp (name, db_get_column_name(db_get_table_column(table, dbcol)))  ) {
          found = 1;
          nrec = db_select_CatValArray ( driver, field->table, field->key, name, NULL, Cvarr );
        }          
      }

      if ( !found ) {
          /* attribute does not exist */
          return (0);
      }

      /* general error reading the att */
      if ( nrec < 0 ) {
          G_warning ("Error reading attribute '%s'.", name );
          return (-3);
      }
        
      /* there is not at least one vector object (record) in the current layer */
      if ( nrec < 1 ) {
          G_warning ("Attribute '%s' exists in table but no records found.", name );
          return (-2);
      }
        
      /* attribute exists. Now check if it is also of the right type */
      ctype = Cvarr->ctype;
      if ( (ctype == DB_C_TYPE_DOUBLE) || (ctype == DB_C_TYPE_INT) ) {
          return (1);
      } 
        
      /* last possibility after all checks: att exists but is not numeric ! */
      return (-1);     
}

/* Gets a numeric attribute's value (double or int type), always as type double. 
   Value of atribute '*name' for current vector object ("line") will be stored in '*val'.
   
   Returns:
        1 if OK
        0 if attribute does not exist in table/layer ('fieldn') or is not of numeric type
        -1 if attribute value is missing
        -2 if attribute value is NULL
        
        if return val <1, then '*val' will also be set -9999.99
        
*/
int get_att_dbl ( double *val, char* name, dbDriver *driver, dbTable *table, struct field_info *field, 
                  int fieldn, struct line_cats *vect_cats, dbCatValArray *Cvarr ) {
    int i;
    dbCatVal *catval;
    int ctype;


    *val = -9999.99;

    /* test if attribute is OK to read */
    if ( check_att ( name, driver, table, field, Cvarr ) < 1 ) {
      return (0);
    }

    for ( i = 0; i < vect_cats->n_cats; i++ ) {
      /* check if this point has a dblink in the current layer */
      if ( vect_cats->field[i] == fieldn ) {
                    
        /* problem reading attribute value? */
        if ( db_CatValArray_get_value ( Cvarr, vect_cats->cat[i], &catval ) != DB_OK ) {
		      G_warning ( "Missing '%s' attribute for cat = %d", name, vect_cats->cat[i] );
		      /* THIS IS A MISSING VALUE */
		      return (-1);
		    }
	    	                
        /* attribute value is a NULL value? */
        if ( catval->isNull ) {
          G_warning ( "Got NULL value for '%s' for cat = %d", name, vect_cats->cat[i] );
          /* THIS IS A NULL VALUE */
		      return (-2);
        }

        /* All OK, so store the value for this point */
        ctype = Cvarr->ctype;
		    if ( ctype == DB_C_TYPE_INT ) {
		      *val = catval->val.i;
		    } else if ( ctype == DB_C_TYPE_DOUBLE ) {
		      *val = catval->val.d;
		    }
		  }
	  }
	  
	  /* all OK */
	  return (1);
}



void point_sample (struct Cell_head *window, int nrows, int ncols,
		   SEGMENT *seg_in_p, SEGMENT *seg_out_1_p,
		   SEGMENT *seg_out_2_p, SEGMENT *seg_patt_p,
		   SEGMENT *seg_patt_p_v,
		   double *attempted_sample, double *actual_sample,
		   char *site_file, int terse, RASTER_MAP_TYPE data_type,
		   int ignore_att, int fieldn )
{
  int row_viewpt, col_viewpt;
  
  /* 'value' stores a byte-sized (CELL) item from the mask segment file(s) */
  void *value = NULL;
  	
  CELL cell_no;
  double viewpt_elev = 0;
  struct point *heads[16];
  long int sites_in_region, sites_of_interest, cells_in_map;
  int null_value;
  char *site_mapset;
  CELL mask = 0;
  long num_sites = 0;
  /* the following are used to store different raster map types */
  CELL c_value;
  FCELL f_value;
  DCELL d_value;

  struct Map_info in_vect_map;
  struct line_pnts *vect_points;
  struct line_cats *vect_cats;
  int cur_type;
  char errmsg [200];
  double x,y,z;
  int n_points = 1;
  
  /* site attribute management */
  int read_attributes;
  dbTable *table;
  dbString table_name;  
  int is3d, warn3d;
  dbDriver *Driver = NULL; 
  struct field_info *field = NULL;   
  int num_dblinks;
  dbCatValArray Cvarr;
  double val;
  
 
  vect_points = Vect_new_line_struct ();
  vect_cats = Vect_new_cats_struct ();

  if ((site_mapset = G_find_vector2 (site_file, "")) == NULL) {
    sprintf (errmsg, "Could not find input vector map %s\n", site_file);
    G_fatal_error ("%s",errmsg);
  }

  Vect_set_open_level (2);
          
  if (1 > Vect_open_old (&in_vect_map, site_file, site_mapset)) {
    sprintf (errmsg, "Could not open input vector map.\n");
    G_fatal_error ("%s",errmsg);
  }

  is3d = Vect_is_3d ( &in_vect_map );
  warn3d = 0;
  
  if  ( ( is3d ) && ( SPOT_SET ) ) {
  	G_warning ("Sites (points) map is 3D but global spot height given. Ignoring z coordinates.\n");
  }

  /* Initialise output segment file with appropriate data value */
  init_segment_file (nrows, ncols, seg_out_2_p, seg_patt_p);

  /* Initialise variables */
  cells_in_map = nrows * ncols;
  sites_in_region = 0;
  sites_of_interest = 0;
  cell_no = 0;

  /* filter vector objects to current region and point types only */
  Vect_set_constraint_region (&in_vect_map, window->north, window->south, 
			      window->east, window->west, window->top, window->bottom ); 
  Vect_set_constraint_type (&in_vect_map, GV_POINT);
  
  /* calculate number of vector points in map and current region */
  while ((cur_type = Vect_read_next_line (&in_vect_map, vect_points, NULL)) > 0) {     
      num_sites ++;            
  }
  
  if ( num_sites < 1 ) {
  	 if ( is3d ) { 
  		    G_fatal_error ( "No sites (points) found in current 3D region. Make sure top and bottom settings are right.\n" );
	   } else {
 		      G_fatal_error ( "No sites (points) found in current 2D region.\n" );	
	   }
  }   
    
    /* Check for site attributes in layer #fieldn and open DBMI connection if OK. */
    read_attributes = 0;
    num_dblinks = Vect_get_num_dblinks(&in_vect_map);
    if ( (num_dblinks > 0) && (!ignore_att) ) {
      /* there is at least one attribute table connected
         to this layer and we cannot ignore it */
     
     	/* get layer as specified by user */
    	field = Vect_get_field( &in_vect_map, fieldn );
      if ( field == NULL ) {
		    G_fatal_error("Database connection not defined for layer %i", fieldn );
	    }

	    /* start database driver for the current field (layer) */
	    Driver = db_start_driver_open_database ( field->driver, field->database );

      if (Driver == NULL)
        G_fatal_error("Cannot open database %s (type %s) for layer %i", field->database, field->driver, fieldn );
 	 
      /* init structs for getting table information (attribute names) */
      db_init_string(&table_name);
      db_set_string(&table_name, field->table);
      /* copy table information from current layer into '*table' */
      if(db_describe_table (Driver, &table_name, &table) != DB_OK)
        G_fatal_error("Cannot open table <%s>", field->table);

      /* init struct to access attribute values */
	    db_CatValArray_init ( &Cvarr );
	    	    
	    /* we will process attributes later */
      read_attributes = 1;	
    }

 	 /* rewind vector points file  */
  	Vect_rewind ( &in_vect_map );
  	/* now step thru all points in the input map */
	while ((cur_type = Vect_read_next_line (&in_vect_map, vect_points, vect_cats)) > 0) {   
      	mask = 0;
    	cell_no ++;
    	if ( read_attributes ) {
    	    	/* now test for the presence/validity of all supported attributes */
    	    
        	/* SPOT = absolute height of current observer point */  
   		if ( get_att_dbl ( &val, "SPOT", Driver, table, field, fieldn, vect_cats, &Cvarr ) > 0 ) {
        		SPOT = val;
	        	SPOT_SET = 1;
	            	/* issue override warning if this is a 3D map */
			if ( (is3d) && (!warn3d) ) {
				warn3d ++;
				G_warning ("Sites map is 3D but 'SPOT' attribute found. Ignoring z coordinates.");
			}
		}
		        	
		if ( get_att_dbl ( &val, "OFFSETA", Driver, table, field, fieldn, vect_cats, &Cvarr ) > 0 ) {
			obs_elev = val;
		}
	
		if ( get_att_dbl ( &val, "OFFSETB", Driver, table, field, fieldn, vect_cats, &Cvarr ) > 0 ) {	
			OFFSETB = val ;
			OFFSETB_SET =1;			
		}

		if ( get_att_dbl ( &val, "AZIMUTH1", Driver, table, field, fieldn, vect_cats, &Cvarr ) > 0 ) {	
			AZIMUTH1 = val ;
			AZIMUTH1_SET = 1;
		}

		if ( get_att_dbl ( &val, "AZIMUTH2", Driver, table, field, fieldn, vect_cats, &Cvarr ) > 0 ) {	
			AZIMUTH2 = val ;
			AZIMUTH2_SET = 1;
		}
		
		if ( get_att_dbl ( &val, "VERT1", Driver, table, field, fieldn, vect_cats, &Cvarr ) > 0 ) {	
			VERT1 = val ;
			VERT1_SET = 1;
		}

		if ( get_att_dbl ( &val, "VERT2", Driver, table, field, fieldn, vect_cats, &Cvarr ) > 0 ) {	
			VERT2 = val ;
			VERT2_SET = 1;
		}

		if ( get_att_dbl ( &val, "RADIUS1", Driver, table, field, fieldn, vect_cats, &Cvarr ) > 0 ) {	
			RADIUS1 = val ;
		}

		if ( get_att_dbl ( &val, "RADIUS2", Driver, table, field, fieldn, vect_cats, &Cvarr ) > 0 ) {	
			max_dist = val ;
		}				        
    } /* end (read attributes) */
        			
    Vect_copy_pnts_to_xyz (vect_points, &x, &y, &z, &n_points);
    /* If site falls within current region */	  
	  sites_in_region ++;
	 /* Get array coordinates of viewpoint */ 
	 /* row_viewpt = (window->north - site->north) / window->ns_res;
        col_viewpt = (site->east - window->west) / window->ew_res;
	 */
	 row_viewpt = G_northing_to_row ( y, window );
	 col_viewpt = G_easting_to_col ( x, window );
	  
	 /* Check whether viewpoint is in area of interest */
	 if (patt_flag_v) {
		  value = (char*) &mask;
	    segment_get(seg_patt_p_v, value, row_viewpt, col_viewpt);
	 } else { 
		  mask = 1;
	 }
	
	 /* Only include if viewpoint is in area of interest */
	 if (mask == 1) {
		  sites_of_interest ++;

	 /* We do not check for duplicates because it is possible
		in cases of low resolution that two or more sites with 
		different map coordinates could have the same array 
		coordinates */

	 /* Read elevation of current view point */			
	 if ( data_type == CELL_TYPE ) {
	   value = (CELL*) &c_value;
	 }		      
	 if ( data_type == FCELL_TYPE ) {
	 	 value = (FCELL*) &f_value;
	 }		      
	 if ( data_type == DCELL_TYPE ) {
	   value = (DCELL*) &d_value;
	 }
	    
	 null_value = 0;
	 segment_get (seg_in_p, value, row_viewpt, col_viewpt); /* height now in value */    														     /* and in read_viewpt_elev */
	   
	 if ( data_type == CELL_TYPE ) {
			viewpt_elev = c_value + obs_elev;
      if (G_is_c_null_value (&c_value)) {
			   null_value = 1;
	   }		
   }		
	 if ( data_type == FCELL_TYPE ) {
	    viewpt_elev = f_value + obs_elev;
			if (G_is_f_null_value (&f_value)) {
			   null_value = 1;
			}		
   }		
	 if ( data_type == DCELL_TYPE ) {
	    viewpt_elev = d_value + obs_elev;
			if (G_is_d_null_value (&d_value)) {
			   null_value = 1;
			}
   }

  /* skip sites on NULL DEM cells */
	if (!null_value) {
	   if ( (is3d) && (!SPOT_SET) ) {
		 /* if we have 3D coordinates, we just pretend z = SPOT at this point */
		    SPOT_SET = 1;
				SPOT = z;
		 }
	   if (SPOT_SET) {
	       if (ADJUST) {
				    if (SPOT >= viewpt_elev) {
						    viewpt_elev = SPOT + obs_elev;
					  }
	       } else {
	    	    viewpt_elev = SPOT;
				 }
	   }	    
	    
    /* Do line of sight analysis from current viewpoint */	      
    /* this updates the information in 'heads', defined in 'point.h' */
    line_of_sight (cell_no, row_viewpt, col_viewpt,
			           nrows, ncols, viewpt_elev,
			           heads, seg_in_p, seg_out_1_p, seg_patt_p,
			           patt_flag, data_type);
	      	      
    /* Calculate viewshed */	      
    if (! from_cells) {
		    count_visible_cells (cell_no, row_viewpt, col_viewpt,
		                	       seg_out_1_p, seg_out_2_p, seg_patt_p, heads);
    } else {
	      cumulate_visible_cells (cell_no, row_viewpt, col_viewpt,
		                	          seg_out_1_p, seg_out_2_p, seg_patt_p, heads);	      
    }
		      
    /* Free memory used in line-of-sight analysis */	      
	  delete_lists (heads);
 	}
	    	    
	 /* Print progress message */
	 if (!terse) {
	     G_percent (sites_of_interest, num_sites, 1);
	 }	    	
  } /* END (if (mask == 1)) */  
 } /* END (loop through sites list) */

  /* close DB connection, release unneeded mem */
  if ( read_attributes ) {
     db_free_string ( &table_name );
     db_free_table ( table );  
  	 db_close_database(Driver);
	   db_shutdown_driver(Driver);
  }

  Vect_close ( &in_vect_map );

  /* Calculate_sample */
  /* % of cells in region */
  *attempted_sample = 100.0 / (double) cells_in_map * (double) sites_in_region;

  /* % of cells in area of interest */
  *actual_sample = 100.0 / (double) cells_in_map * (double) sites_of_interest;

#ifdef DEBUG
  fprintf (stdout, "\nSites in: region = %ld, area of interest = %ld", 
	   sites_in_region,
	   sites_of_interest);
  fflush (stdout); 
#endif
}
