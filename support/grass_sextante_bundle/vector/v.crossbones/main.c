
/***********************************************************************************
* 
* v.crossbones 
*
* A GRASS C API implementation of Leif Isaaksen's Crossbones Java program.

* (c) 2009 Benjamin Ducke <benjamin.ducke@oadigital.net> for Oxford Archaeology

* This file is part of GRASS GIS. It is free software. You can
* redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software
* Foundation; either version 2 of the License, or (at your option)
* any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
***********************************************************************************/

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <grass/gis.h>
#include <grass/Vect.h>
#include <grass/dbmi.h>
#include <grass/glocale.h>

#define MAXSTR 5000

#define DEBUG 0

#define MODE_DARTS 1
#define MODE_LINES 2
#define MODE_PLANES_H 3
#define MODE_PLANES_V 4
#define MODE_POINTS 5
#define MODE_PYRAMIDS 6

#define RGBMAX 32
int RGB[RGBMAX][3] = 
{
{143,221,54},
{162,54,221},
{54,221,85},
{221,54,115},
{49,45,94},
{221,54,59},
{150,150,150},
{54,140,221},
{221,104,54},
{54,68,221},
{54,221,176},
{154,51,99},
{221,162,54},
{98,54,221},
{154,108,51},
{94,45,82},
{75,75,75},
{138,132,70},
{77,51,154},
{45,92,92},
{248,73,208},
{215,221,54},
{133,51,154},
{51,154,55},
{94,54,45},
{51,154,114},
{154,53,51},
{51,143,154},
{51,82,154},
{150,154,51},
{45,94,52},
{92,94,45}
};

int RGBNUM = 0;

/* for bone colouring */
int RGBNUM_BONE = 0;
int MAX_BONE_ID;
int *RGB_MAPPER_COLOUR = NULL;

/* width:1 ratios for triangles */
int NUM_RATIOS = 0;
double *RATIO_VAL;
int *RATIO_ID;

typedef struct {
    char *ID;
    double X;
    double Y;
    double Z;
    int BONE_ID;        
    int SKEL_ID;
    int invalid;
} Point;

static Point *pointTable;
int numPoints = 0;

double ax, ay, az, bx, by, bz;

int PROGRESS = 0;
int MODE;
int ID_COL_POS;
int X_COL_POS;
int Y_COL_POS;
int Z_COL_POS;

char *delimiter;

/* vector output map and DBMS stuff */
struct Map_info *Map;
struct field_info *Fi;
dbDriver *driver;
dbString sql;

/* GRASS module description and options */
struct GModule *module;
struct Option *file, *output, *mode, *delim, *idcol, *xcol, *ycol, *zcol, *ratios;


static void createTable();
static void writeMap();
static void doTransform();
int compare ( const void *A, const void *B );


/*
    Release memory allocated for global points array.
*/
void freePts ()
{
    int i;

    for ( i = 0; i < numPoints; i ++) {
        if ( pointTable[i].ID != NULL ) {
            G_free ( pointTable[i].ID );
        }
    }
    G_free ( pointTable );
}


/*
    Print contents of global points array (debugging).
*/
void dumpPts ()
{
    int i;
    
    
    fprintf ( stderr, "\nCONTENTS OF GLOBAL POINTS ARRAY:\n" );
    fprintf ( stderr, "--------------------------------\n" );
    
    for ( i = 0; i < numPoints; i ++ ) {
        fprintf ( stderr, "%i:\tID:%s\tX:%.3f\tY:%.3f\tZ:%.3f\tBONE_ID:%i\tSKEL_ID:%i", 
	          i, pointTable[i].ID, pointTable[i].X, pointTable[i].Y, pointTable[i].Z,
		  pointTable[i].BONE_ID, pointTable[i].SKEL_ID );
	if ( pointTable[i].invalid ) {
	    fprintf ( stderr, " [INVALID]\n" );
	} else {
	    fprintf ( stderr, "\n" );
	}
    }
}


/*
    Removes invalid point records from the global points array.
    Returns a pointer to the mem location of the new, pruned array.
    
    Usage:
    
        pointTable = cleanPts();
	
*/
Point *cleanPts ()
{
    int i, j;
    int numValidPts;
    Point *tmp;
    
    
    /* find number of valid points */
    numValidPts = 0;
    for ( i = 0; i < numPoints; i ++) {
        if ( pointTable[i].invalid == 0 ) {
	    numValidPts ++;
	}
    }
    
    /* allocate a new array to hold only the valid points */
    tmp = G_malloc ( sizeof ( Point ) * numValidPts );
    for ( i = 0; i < numValidPts; i ++ ) {
        tmp[i].invalid = 0;
    }
    
    /* copy only valid points into the new array */
    j = 0;
    for ( i = 0; i < numPoints; i ++) {
        if ( pointTable[i].invalid == 0 ) {
            if ( pointTable[i].ID == NULL ) {
	        tmp[j].ID = NULL;
            } else {
                tmp[j].ID = strdup ( pointTable[i].ID );
            }
	    tmp[j].X = pointTable[i].X;
	    tmp[j].Y = pointTable[i].Y;
	    tmp[j].Z = pointTable[i].Z;
            tmp[j].BONE_ID = pointTable[i].BONE_ID;
	    tmp[j].SKEL_ID = pointTable[i].SKEL_ID;
	    j ++;
	}
    }
    
    /* update global points count */
    numPoints = numValidPts;
    
    /* free old array contents */
    freePts();    

    return (tmp);
}


/*
    This sorts the points in the global table into a tree-like structure.
    It first sorts by skeleton ID. If the skeleton ID of two points is the
    same, it sorts by bone ID. Both are sorted ascendingly.
    By definition, there cannot be any two points that are exactly the same.
    They differ either in their skeleton IDs or in their bone IDs!
    If not: we have a duplicate that needs to be marked invalid.
*/
int comparePts ( const void *A, const void *B )
{
    const Point* a;
    const Point* b;
    int bone_id_a, bone_id_b;
    int skel_id_a, skel_id_b;
    int i;
    
    
    a = ( const Point* ) A;
    bone_id_a =  a->BONE_ID;
    skel_id_a =  a->SKEL_ID;

    b = ( const Point* ) B;
    bone_id_b =  b->BONE_ID;
    skel_id_b =  b->SKEL_ID;

    if ( skel_id_a == skel_id_b && bone_id_a == bone_id_b ) {
        /* oops, we have a duplicate point: mark all occurences as invalid */
        G_warning (_("Duplicate point: skeleton ID = %i, bone ID = %i"), skel_id_b, bone_id_b );
        for ( i = 0; i < numPoints; i ++ ) {
            if ( pointTable[i].BONE_ID == bone_id_b 
	         && pointTable[i].SKEL_ID == skel_id_b 
	       ) {
	         pointTable[i].invalid = 1;
	    }
        }
        return ( 0 );
    }

    /* Different skeleton IDs? Then sort by those! */
    if ( skel_id_a != skel_id_b ) {
        return ( skel_id_a - skel_id_b );
    }
    
    /* Otherwise: sort by bone ID */
    return ( bone_id_a - bone_id_b );
        
    /* prune duplicate points */
    pointTable = cleanPts();    
    
    return ( 0 );
}


/*
    Cleans a field string: removes leading and trailing whitespaces as well as any quotation
    marks that could be enclosing the field contents.
    Returns a newly allocated string. Caller must free it when done.
    Returns NULL on error or if input string was NULL.
*/
char *cleanString ( char *input ) 
{
    char *str;
    
    
    if ( input == NULL )
        return ( NULL );
	
    str = strdup ( input );
    
    G_strchg ( str, '\'', ' ' );
    
    if ( str != NULL )
        G_strchg ( str, '"', ' ' );  
    
    if ( str != NULL )
        G_squeeze ( str );
    
    return ( str );
}


/* 
    Read ASCII file with point IDs and measurements.
    Store ID and X/Y/Z coordinates in the global double array 'pointTable'.
    Sort the array into a tree-like structure: first by skeleton ID, then all points
    of the same skeleton ID by bone ID, so the points are always "skull to toe" for
    each coherent skeleton.
    Any line the parser can make no sense of will be read as an invalid point.
*/
void createTable()
{
    int num_lines = 0;
    FILE *fp;
    char *line, *copy;
    int i, j;
    char *token;
    int num_tokens;
    char *id = NULL;
    char *id_token;
    char *clean_token;
    int skipped;
    
    
    fp = fopen ( file->answer, "r" );
    if ( fp == NULL ) {
        G_fatal_error ("Unable to open ASCII input file for reading");
    }
    
    line = G_malloc ( sizeof (char) * MAXSTR );
    
    while ( fgets ( line, MAXSTR, fp ) ) {
        num_lines ++;
    }
    
    if ( num_lines < 2 ) {
        G_fatal_error ("Input file too short (need at least two points)");
    }
    
    rewind ( fp );
    
    /* now that we know how many points to expect, 
       we can allocate the array to store the points */
    pointTable = G_malloc ( sizeof ( Point ) * num_lines );
    for ( i = 0; i < num_lines; i ++ ) {
        pointTable[i].ID = NULL;
        pointTable[i].invalid = 0;
    }
    
    G_message (_("Importing %i lines from input file:"), num_lines );
        
    /* split input lines into tokens and save data */
    i = -1;
    while ( fgets ( line, MAXSTR, fp ) ) {    	
        i ++;
    	num_tokens = 0;
	j = 1;
	G_squeeze ( line );
	/* skip empty lines and commented lines */
	if ( strlen ( line  ) > 0 && line[0] != '#' ) {
    	    token = strtok ( line, delimiter );
	    skipped = 0;	    
	} else {
	    token = NULL;
	    skipped = 1;
	}
	while ( token != NULL ) {
	    num_tokens ++;
	    /* point ID ? */
	    if ( j == ID_COL_POS ) { 
	    	if ( id != NULL ) {
		    free ( id );
		}
		clean_token = cleanString ( token );
		if ( clean_token == NULL ) {
                    G_warning (_("Invalid ID field on line %i"), i + 1);
                    pointTable[i].invalid = 1;		
		} else {
	    	    pointTable[i].ID = strdup ( clean_token );
		    G_free ( clean_token );
		}
	    }
	    /* X coordinate (Easting) ? */
	    if ( j == X_COL_POS ) {
	        clean_token = cleanString ( token );
		if ( clean_token == NULL ) {
                    G_warning ("Invalid X coordinate on line %i", i + 1);
                    pointTable[i].invalid = 1;		
		} else {
	            pointTable[i].X = atof ( clean_token );
		    G_free ( clean_token );
		    if ( pointTable[i].X == 0.0 ) {
                        G_warning (_("Invalid X coordinate (0.0) on line %i"), i + 1);
                        pointTable[i].invalid = 1;
                    }	    
		}
	    }
	    /* Y coordinate (Northing) ? */
	    if ( j == Y_COL_POS ) {
	        clean_token = cleanString ( token );
		if ( clean_token == NULL ) {
                    G_warning (_("Invalid Y coordinate on line %i"), i + 1);
                    pointTable[i].invalid = 1;		
		} else {
	            pointTable[i].Y = atof ( clean_token );
		    G_free ( clean_token );
		    if ( pointTable[i].Y == 0.0 ) {
                        G_warning (_("Invalid Y coordinate (0.0) on line %i"), i + 1);
                        pointTable[i].invalid = 1;
                    }
		} 
	    }
	    /* Z coordinate (Elevation) ? */
	    if ( j == Z_COL_POS ) {
	        clean_token = cleanString ( token );
		if ( clean_token == NULL ) {
                    G_warning (_("Invalid Z coordinate on line %i"), i + 1);
                    pointTable[i].invalid = 1;				
		} else {
	            pointTable[i].Z = atof ( clean_token );
		    G_free ( clean_token );
		    if ( pointTable[i].Z == 0.0 ) {
                        G_warning (_("Invalid Z coordinate (0.0) on line %i"), i + 1);
                        pointTable[i].invalid = 1;
                    }
		}
	    }	    
	    /* next field */
	    token = strtok ( NULL, delimiter );	    
	    j ++;
	}
	/* we need EXACTLY four fields in pre-defined order */
	if ( num_tokens < 4 ) {
	    if ( skipped == 0 ) {
	        G_warning (_("Wrong field count (needed 4, got %i) on line %i"), num_tokens, i + 1);
	    }
	    pointTable[i].invalid = 1;
	}
        G_free ( line );
        line = G_malloc ( sizeof (char) * MAXSTR );
	
	G_percent ( i, num_lines - 1, 10 );
    }
    numPoints = num_lines;

    /* split ID into BONE_ID and SKEL_ID */
    
    for ( i = 0; i < numPoints; i ++ ) {
        if ( pointTable[i].invalid == 0 ) {
            id = strdup ( pointTable[i].ID );
	    id_token = strtok ( id, "." );
	    if ( id_token == NULL ) {
	        G_warning (_("Invalid point ID on line %i"),i + 1);
	        pointTable[i].invalid = 1;
	    }
	    pointTable[i].SKEL_ID = atoi ( id_token );
	    if ( pointTable[i].SKEL_ID < 0 ) {
	        G_warning (_("Invalid (negative) point ID on line %i"),i + 1);
	        pointTable[i].invalid = 1;	
	    }
	    id_token = strtok ( NULL, "." );
	    if ( id_token == NULL ) {
	        G_warning (_("Invalid point ID on line %i"),i + 1);
	        pointTable[i].invalid = 1;
	    }
	    pointTable[i].BONE_ID = atoi ( id_token );
	    if ( pointTable[i].BONE_ID < 0 ) {
	        G_warning (_("Invalid (negative) point ID on line %i"),i + 1);
	        pointTable[i].invalid = 1;	
	    }	
	    id_token = strtok ( NULL, "." );
	    if ( id_token != NULL ) {
	        G_warning (_("Invalid point ID on line %i"),i + 1);
	        pointTable[i].invalid = 1;
	    }
            G_free ( id );
        }
    }

    if ( DEBUG ) {
        fprintf ( stderr, "createTable() DONE.\n" );
	dumpPts();
    }    
    
    fclose ( fp );
    
    G_message ("Sorting points and deleting duplicates:");
    
    /* prune invalid point records */
    pointTable = cleanPts();

    G_percent ( 1, 4, 1 );
    
    if ( DEBUG ) {
        fprintf ( stderr, "Prune invalid input file records DONE.\n" );
	dumpPts();
    }    
    
    /* sort global points array to make it a tree-like structure */
    qsort ( pointTable, numPoints, sizeof ( Point ), comparePts );

    G_percent ( 2, 4, 1 );

    if ( DEBUG ) {
        fprintf ( stderr, "Sort points DONE.\n" );
	dumpPts();
    }    

    /* prune invalid point records */
    pointTable = cleanPts();
    if ( DEBUG ) {
        fprintf ( stderr, "Pruning duplicate points DONE.\n" );
	dumpPts();
    }
    
    G_percent ( 3, 4, 1 );
    
    /* get max bone ID from remaining valid points */
    if ( numPoints > 0 ) {
        MAX_BONE_ID = pointTable[0].BONE_ID;
        for ( i = 0; i < numPoints; i ++ ) {
	    if ( pointTable[i].BONE_ID > MAX_BONE_ID ) {
	        MAX_BONE_ID = pointTable[i].BONE_ID;
	    }
        }
	MAX_BONE_ID = MAX_BONE_ID / 2;	
        /* map bone possible bone IDs to RGB values */ 
	if ( MAX_BONE_ID > 0 ) {
	    RGB_MAPPER_COLOUR = G_malloc ( sizeof ( int ) * MAX_BONE_ID );
            for ( i = 0; i < MAX_BONE_ID; i ++ ) {
		RGB_MAPPER_COLOUR[i] = RGBNUM_BONE;
		RGBNUM_BONE ++;
		if ( RGBNUM_BONE == RGBMAX ) {
		    RGBNUM_BONE = 0;
		}
            }	    	    
	}
    }
    
    G_percent ( 4, 4, 1 );

}


/* 
   Go through the remaining points that are still valid.
   Check if we always have an odd bone ID preceeding an even one.
   If not: those are singletons and they also need to be marked invalid.
    
   At this point, duplicates have already been removed from the list!

*/
void validatePoints()
{
    int i;

    
    G_message ("Validating points:");
    
    /* set all points to be invalid by default */
    for ( i = 0; i < numPoints; i++ ) {
        pointTable[i].invalid = 1;
    }

    /*
       The validation works like this:
       
       1. Start at the first point.
       2. Check whether the point and the one following it are a correct tuple.
          If so: jump 2 points ahead
	  If not: mark points as invalid and jump only one point ahead
	  
       A correct point tuple [A,B] is where A.SKEL_ID = B.SKEL_ID and A.BONE_ID = B.BONE_ID-1
    */
    i = 0;
    while ( i < numPoints - 1 ) { 
        if ( pointTable[i].SKEL_ID == pointTable[i+1].SKEL_ID ) {
	    if ( pointTable[i].BONE_ID == pointTable[i+1].BONE_ID-1 &&
	         pointTable[i].BONE_ID % 2 != 0 ) {
	        /* got a valid tuple: mark both members as valid and skip two points ahead */
	        pointTable[i].invalid = 0;
		pointTable[i+1].invalid = 0;
		i = i + 2;
	    } else {
	        /* got an invalid point: skip only one ahead and leave marked as invalid */
    		G_warning (_("Invalid point: skeleton ID = %i, bone ID = %i"), pointTable[i].SKEL_ID, pointTable[i].BONE_ID );		
	        i = i + 1;	    
	    }
	} else {
	    /* got an invalid point: skip only one ahead and leave marked as invalid */
    	    G_warning (_("Invalid point: skeleton ID = %i, bone ID = %i"), pointTable[i].SKEL_ID, pointTable[i].BONE_ID );		
	    i = i + 1;
	}
    }
    
    G_percent ( 1, 4, 1 );
    
    /* prune invalid point records */
    pointTable = cleanPts();
    
    G_percent ( 2, 4, 1 );
    
    /* Cannot construct triangles if points are _exactly_ above each other on Z axis */
    if ( MODE == MODE_PYRAMIDS || MODE == MODE_DARTS || MODE == MODE_PLANES_H || MODE == MODE_PLANES_V ) {
        /* Now check for points that have identical X and Y coordinates. */
        for ( i = 0; i < numPoints; i = i + 2 ) {
	    if ( pointTable[i].X == pointTable[i+1].X && 
	         pointTable[i].Y == pointTable[i+1].Y ) {
    	         G_warning (_("Points %i.%i and %i.%i are exactly above each other. Cannot construct triangle. Will delete both"), 
		               pointTable[i].SKEL_ID, pointTable[i].BONE_ID,
			       pointTable[i+1].SKEL_ID, pointTable[i+1].BONE_ID );
                 pointTable[i].invalid = 1;
		 pointTable[i+1].invalid = 1;
            }
	}	
    }

    G_percent ( 3, 4, 1 );

    /* prune invalid point records */
    pointTable = cleanPts();

    G_percent ( 4, 4, 1 );
    
    if ( DEBUG ) {
        fprintf ( stderr, "Validate points DONE.\n" );
	dumpPts();
    }
}


/*
    Write a vector geometry to the output GRASS vector map.
    Write attributes to table linked to that map. Link vector object to attribute
    table record.
*/
void write_vect( int cat, int skelID, int boneID, int unitID,
		 double *xpnts, double *ypnts, double *zpnts, 
		 int arr_size, int type )
{
    struct line_cats *Cats;
    struct line_pnts *Points;
    char buf[MAXSTR];
    char rgbbuf[12];
    char rgbbuf2[12];
    

    /* copy xyzpnts to Points */
    Points = Vect_new_line_struct();
    Vect_copy_xyz_to_pnts(Points, xpnts, ypnts, zpnts, arr_size);

    /* write database attributes */
    Cats = Vect_new_cats_struct();
    sprintf ( rgbbuf, "%i:%i:%i", RGB[RGBNUM][0], RGB[RGBNUM][1], RGB[RGBNUM][2] );
    sprintf ( rgbbuf2, "%i:%i:%i", RGB[RGB_MAPPER_COLOUR[boneID-1]][0],RGB[RGB_MAPPER_COLOUR[boneID-1]][1], RGB[RGB_MAPPER_COLOUR[boneID-1]][2] );
    sprintf(buf, "insert into %s (cat, skel_id, bone_id, unit_id, GRASSRGB, BONERGB) values(%i,%i,%i,%i,'%s','%s');",
            Fi->table, cat, skelID, boneID, unitID, rgbbuf, rgbbuf2);

    if ( DEBUG ) {
        fprintf ( stderr, "Writing attribute: %s\n", buf );
    }

    db_set_string(&sql, buf);
    if (db_execute_immediate(driver, &sql) != DB_OK) {
	G_fatal_error(_("Unable to insert new record: %s"), db_get_string(&sql));
    }
    db_free_string(&sql);
 
    Vect_cat_set(Cats, 1, cat);

    /* write */
    Vect_write_line(Map, type, Points, Cats);

    Vect_destroy_cats_struct(Cats);
    Vect_destroy_line_struct(Points);
}


/*
    Extract both points that make up a bone measurement.
*/
void writePoints ( int cat, int skelID, int boneID, int unitID )
{
    double *xpnts, *ypnts, *zpnts;
    
    	
    xpnts = G_malloc ( sizeof ( double ) * 1 );
    ypnts = G_malloc ( sizeof ( double ) * 1 );
    zpnts = G_malloc ( sizeof ( double ) * 1 );
    
    xpnts[0] = ax;
    ypnts[0] = ay;
    zpnts[0] = az;
    
    write_vect ( cat, skelID, boneID, unitID, xpnts, ypnts, zpnts, 1, GV_POINT );    
    
    xpnts[0] = bx;
    ypnts[0] = by;
    zpnts[0] = bz;
    
    write_vect ( cat + 1, skelID, boneID, unitID, xpnts, ypnts, zpnts, 1, GV_POINT );
    
    G_free ( xpnts );  
    G_free ( ypnts );
    G_free ( zpnts );        
}


/*
    Construct a straight line between two point measurements.
*/
void writeLine ( int cat, int skelID, int boneID, int unitID )
{
    double *xpnts, *ypnts, *zpnts;
    
    	
    xpnts = G_malloc ( sizeof ( double ) * 2 );
    ypnts = G_malloc ( sizeof ( double ) * 2 );
    zpnts = G_malloc ( sizeof ( double ) * 2 );
    
    xpnts[0] = ax;
    ypnts[0] = ay;
    zpnts[0] = az;
    
    xpnts[1] = bx;
    ypnts[1] = by;
    zpnts[1] = bz;
    
    write_vect ( cat, skelID, boneID, unitID, xpnts, ypnts, zpnts, 2, GV_LINE );
    
    G_free ( xpnts );  
    G_free ( ypnts );
    G_free ( zpnts );
}


/*
    Construct a 3D pyramid enclosing two point measurements.
*/
void writeTriangle ( int cat, int skelID, int boneID, int unitID, 
                     double xys[], int start1, int start2 )
{
    double *xpnts, *ypnts, *zpnts;
    
    	
    xpnts = G_malloc ( sizeof ( double ) * 4 );
    ypnts = G_malloc ( sizeof ( double ) * 4 );
    zpnts = G_malloc ( sizeof ( double ) * 4 );
    
    xpnts[0] = bx;
    ypnts[0] = by;
    zpnts[0] = bz;
    
    xpnts[1] = xys[start1];
    ypnts[1] = xys[start1 + 1];
    zpnts[1] = xys[start1 + 2];
   
    xpnts[2] = xys[start2];
    ypnts[2] = xys[start2 + 1];
    zpnts[2] = xys[start2 + 2];

    xpnts[3] = bx;
    ypnts[3] = by;
    zpnts[3] = bz;
    
    write_vect ( cat, skelID, boneID, unitID, xpnts, ypnts, zpnts, 4, GV_FACE );
    
    G_free ( xpnts );  
    G_free ( ypnts );
    G_free ( zpnts );    
}


/*
    Construct a 3D rectangular plane enclosing two point measurements.
*/
void writeSquare( int cat, int skelID, int boneID, int unitID, 
                     double xys[] )
{
    double *xpnts, *ypnts, *zpnts;
    
    	
    xpnts = G_malloc ( sizeof ( double ) * 5 );
    ypnts = G_malloc ( sizeof ( double ) * 5 );
    zpnts = G_malloc ( sizeof ( double ) * 5 );
    
    xpnts[0] = xys[0];
    ypnts[0] = xys[1];
    zpnts[0] = xys[2];
    
    xpnts[1] = xys[3];
    ypnts[1] = xys[4];
    zpnts[1] = xys[5];
   
    xpnts[2] = xys[6];
    ypnts[2] = xys[7];
    zpnts[2] = xys[8];

    xpnts[3] = xys[9];
    ypnts[3] = xys[10];
    zpnts[3] = xys[11];

    xpnts[4] = xys[0];
    ypnts[4] = xys[1];
    zpnts[4] = xys[2];    
    
    write_vect ( cat, skelID, boneID, unitID, xpnts, ypnts, zpnts, 5, GV_FACE );
    
    G_free ( xpnts );  
    G_free ( ypnts );
    G_free ( zpnts );
}


/*
    Create GRASS vector output map.
    Create attribute table.
    Calculate geometries and write them into the output map.
    Calculate attributes and write them into the output map's attribute table.
*/
void writeMap()
{
    int i, j;
    
    double xlength, ylength, zlength;
    double length, flatLength, bailLength;
    double xoffset, yoffset, zoffset;
    double xys[12];
    int ratio;
    double zRatio;
       
    /* attributes to be written to output map */
    int boneID;
    int skelID;
    int unitID;
    int oldID;
    int cat;
    
    char *organization;
    
    char buf[MAXSTR];
   
    
    
    if ( numPoints < 2 ) {
        G_fatal_error ("Less than two valid measurement points in input file");
    }
    

    G_message (_("Constructing geometries for %i valid points:"), numPoints );
    
    /* CREATE OUTPUT VECTOR MAP */
    
    if (Vect_legal_filename(output->answer) < 0) {
	G_fatal_error(_("Use '%s' option to change vector map name"), output->key);
    }
    
    Map = (struct Map_info *) G_malloc (sizeof ( struct Map_info ) );
    if (Vect_open_new(Map, output->answer, WITH_Z) < 0) {
	G_fatal_error(_("Unable to create vector map <%s>"), output->answer);
    }

    Vect_set_map_name(Map, output->answer);

    Vect_hist_command(Map);    
  
    if ((organization = getenv("GRASS_ORGANIZATION"))) {
	Vect_set_organization(Map, organization);
    } else {
	Vect_set_organization(Map, "UNKNOWN ORGANIZATION");
    }
    Vect_set_date(Map, G_date());
    Vect_set_person(Map, G_whoami());
    Vect_set_map_date(Map, "");
    Vect_set_scale(Map, 2400);
    Vect_set_comment(Map, "");
    Vect_set_zone(Map, 0);
    Vect_set_thresh(Map, 0.0);
    
    
    /* START DBMS INTERFACE */
    
    /* prepare strings for use in db_* calls */
    db_init_string(&sql);
 	
    /* start default database driver */
    Fi = Vect_default_field_info(Map, 1, NULL, GV_1TABLE);
    driver = db_start_driver_open_database(Fi->driver,Vect_subst_var(Fi->database, Map));
    if (driver == NULL) {
	Vect_delete(output->answer);
        G_fatal_error(_("Unable to open database <%s> by driver <%s>"),
			      Vect_subst_var(Fi->database, Map), Fi->driver);
    }
    
    /* create attribute table */
    db_begin_transaction ( driver );
    sprintf(buf, "create table %s (cat integer, skel_id integer, bone_id integer, unit_id integer, GRASSRGB varchar(11),BONERGB varchar(11));",
                  Fi->table);
    
    if ( DEBUG ) {
        fprintf ( stderr, "Creating attribute table: %s\n", buf );
    }
    
    db_set_string(&sql, buf);
    if (db_execute_immediate(driver, &sql) != DB_OK) {
        Vect_delete(output->answer);
	G_fatal_error(_("Unable to create attribute table: %s"), db_get_string(&sql));
    }
        
    if (db_grant_on_table
	(driver, output->answer, DB_PRIV_SELECT, DB_GROUP | DB_PUBLIC) != DB_OK) {
	Vect_delete(output->answer);
	G_fatal_error(_("Unable to grant privileges on table <%s>"), output->answer);
    }
    
    if (db_create_index2(driver, output->answer, "cat") != DB_OK) {
	G_warning(_("Unable to create index for table <%s>, key <%s>"), output->answer, "cat");
    }

    /* link vector map to attribute table */
    if (Vect_map_add_dblink(Map, 1, NULL, Fi->table, "cat", Fi->database, Fi->driver) ) {
        Vect_delete(output->answer);
	G_fatal_error(_("Unable to add database link for vector map <%s>"), Vect_get_full_name(Map));
    }
            
    
    /* PROCESS POINTS AND WRITE GEOMETRIES */
    /* Now process point measurements and write geometries into output vector map. */    
    /* At this stage, the global points array has an even number of valid points. */
    oldID = pointTable[0].SKEL_ID;
    unitID = 1;
    cat = 0;
    for ( i = 0; i < numPoints; i = i + 2 ) {
        /* This boneID is a generalized ID that does not differentiate 
	   between start and end measurement. */
        boneID = (int) pointTable[i+1].BONE_ID / 2;
        skelID = pointTable[i+1].SKEL_ID;

	/* get coordinates for top and bottom of bone */
        ax = pointTable[i].X;
        ay = pointTable[i].Y;
        az = pointTable[i].Z;
	
        bx = pointTable[i+1].X;
        by = pointTable[i+1].Y;
        bz = pointTable[i+1].Z;
	
        /* get vector lengths */
        xlength = fabs (ax - bx);
        ylength = fabs (ay - by);
        zlength = fabs (az - bz);
		
        /* get real length */
        length = sqrt ( (xlength*xlength) + (ylength*ylength) + (zlength*zlength) );
		
        /* get length in x/y plane */
        flatLength = sqrt ( (xlength*xlength) + (ylength*ylength) );
	
        /* determine ratio for triangles, depending on bone type */
        ratio = 12; /* default */
	for ( j = 0; j < NUM_RATIOS; j ++ ) {
	    if ( boneID == RATIO_ID[j] ) {
	        ratio = RATIO_VAL[j];
	    }
	}
			
	/* get bail length */
	bailLength = (double) ( length / (double) ratio);
	
        /* calculate bail offsets from top point (one bail is mirror of the other) */
        xoffset = (bailLength * ylength) / flatLength;
        yoffset = ( (bailLength * xlength) / flatLength ) * (-1);
        zoffset = 0;
						
        xys[0]= ax + xoffset;
        xys[1]= ay + yoffset;
        xys[2]= az + zoffset;
        xys[6]= ax - xoffset;
        xys[7]= ay - yoffset;
        xys[8]= az - zoffset;		
			
        /* get 3rd axis offsets */
        zRatio = (zlength/ratio) / flatLength;
        xoffset = xlength * zRatio;
        yoffset = ylength * zRatio;
        zoffset = (flatLength/ratio) * (-1);
	
        xys[3]= ax + xoffset;
        xys[4]= ay + yoffset;
        xys[5]= az + zoffset;
        xys[9]= ax - xoffset;
        xys[10]= ay - yoffset;
        xys[11]= az - zoffset;
	
        /* Increase unit ID by "1", if we have another skeleton ID */
        if ( oldID != pointTable[i+1].SKEL_ID ) {
            unitID ++;
            oldID = pointTable[i+1].SKEL_ID;
	    /* switch to next colour for next geometry */
            RGBNUM ++;
            if ( RGBNUM == RGBMAX ) {
                RGBNUM = 0;
            }	    
        }
	
	/* write geometries */
        if ( MODE == MODE_DARTS ) {
            writeTriangle ( cat, skelID, boneID, unitID, xys, 0, 6 );
	    cat ++;
            writeTriangle ( cat, skelID, boneID, unitID, xys, 3, 9 );
	    cat ++;
        }	
        if ( MODE == MODE_LINES ) {
            writeLine ( cat, skelID, boneID, unitID );
	    cat ++;
        }
        if ( MODE == MODE_PLANES_H ) {
	    writeTriangle ( cat, skelID, boneID, unitID, xys, 0, 6 );
	    cat ++;
	}
        if ( MODE == MODE_PLANES_V ) {
	    writeTriangle ( cat, skelID, boneID, unitID, xys, 3, 9 );
	    cat ++;
        }
	if ( MODE == MODE_POINTS ) {
            writePoints ( cat, skelID, boneID, unitID );	
	    cat = cat + 2;
	}
	if ( MODE == MODE_PYRAMIDS ) {
            writeTriangle ( cat, skelID, boneID, unitID, xys, 0, 3 );
	    cat ++;
            writeTriangle ( cat, skelID, boneID, unitID, xys, 3, 6 );
	    cat ++;
            writeTriangle ( cat, skelID, boneID, unitID, xys, 6, 9 );
	    cat ++;
            writeTriangle ( cat, skelID, boneID, unitID, xys, 9, 0 );
	    cat ++;
            writeSquare ( cat, skelID, boneID, unitID, xys );	
	    cat ++;
        }
	
	/* switch to next colour for bone colouring */
	RGBNUM_BONE ++;
        if ( RGBNUM_BONE == RGBMAX ) {
            RGBNUM_BONE = 0;
        }
	
	G_percent ( i, numPoints - 2, 1 );	    
	
     }
     fprintf ( stdout, "\n" );
    
    /* commit DBMS actions */
    db_commit_transaction(driver);
    db_close_database_shutdown_driver(driver);
    
    if (!Vect_build(Map)) {
        G_warning("Building topology failed");
    }
    
    Vect_close(Map);  
    db_free_string(&sql);
}


int main(int argc, char **argv)
{
    G_gisinit(argv[0]);
    char *tmp, *token;
    char **ratio_str = NULL;
    int i, j;
    int id;
    double val;
    

    module = G_define_module();
    module->keywords = _("vector, surveying, crossbones");
    module->description =
	_("A GRASS version of Crossbones: Create simple 3D representations of skeletal assemblages.");

    file = G_define_standard_option(G_OPT_F_INPUT);
    file->description = _("Input ASCII file with point measurements");
    file->required = YES;

    output = G_define_standard_option(G_OPT_V_OUTPUT);
    output->description = _("Name of output vector map");
    output->required = YES;
    
    mode = G_define_option();
    mode->key = "mode";
    mode->description = _("3D Representation mode");
    mode->type = TYPE_STRING;
    mode->required = NO;
    mode->multiple = NO;
    mode->options = "darts,lines,planes_h,planes_v,points,pyramids";
    mode->answer = "pyramids";
    
    delim = G_define_option();
    delim->key = "delimiter";
    delim->description = _("Field delimiter for ASCII input file (default: comma)");
    delim->type = TYPE_STRING;
    delim->required = NO;
    delim->multiple = NO;
    delim->answer = ",";

    idcol = G_define_option();
    idcol->key = "idcolumn";
    idcol->description = _("Position of ID column in ASCII input file (leftmost = 1)");
    idcol->type = TYPE_INTEGER;
    idcol->required = NO;
    idcol->multiple = NO;
    idcol->answer = "1";

    xcol = G_define_option();
    xcol->key = "xcolumn";
    xcol->description = _("Position of X (Easting) column in ASCII input file (leftmost = 1)");
    xcol->type = TYPE_DOUBLE;
    xcol->required = NO;
    xcol->multiple = NO;
    xcol->answer = "2";

    ycol = G_define_option();
    ycol->key = "ycolumn";
    ycol->description = _("Position of Y (Northing) column in ASCII input file (leftmost = 1)");
    ycol->type = TYPE_DOUBLE;
    ycol->required = NO;
    ycol->multiple = NO;
    ycol->answer = "3";    

    zcol = G_define_option();
    zcol->key = "zcolumn";
    zcol->description = _("Position of Z (Elevation) column in ASCII input file (leftmost = 1)");
    zcol->type = TYPE_DOUBLE;
    zcol->required = NO;
    zcol->multiple = NO;
    zcol->answer = "4";
    
    ratios = G_define_option();
    ratios->key = "ratios";
    ratios->description = _("List of length:1 ratios for specific bone IDs (boneID1=ratio, boneID2=ratio, ...).");
    ratios->type = TYPE_STRING;
    ratios->required = NO;
    ratios->answer = "1=3.0,2=8.0,5=6.0,8=6.0,11=6.0,14=6.0";
    

    if (G_parser(argc, argv))
	exit(EXIT_FAILURE);

    if ( !strcmp (delim->answer, "tab") || !strcmp (delim->answer, "tabulator") ) {
        delimiter = G_malloc ( sizeof ( char ) * 2 );
        sprintf ( delimiter, "\t");
    } else {
        delimiter = delim->answer;
    }
    
    /* figure out ratios */
    tmp = strdup ( ratios->answer );
    i = 0;
    token = strtok ( tmp, "," );
    while ( token != NULL ) {
        i ++;	
        token = strtok ( NULL, "," );
    }       
    G_free ( tmp );
        
    RATIO_VAL = G_malloc ( sizeof ( double ) * i );
    RATIO_ID = G_malloc ( sizeof ( int ) * i );
    ratio_str = G_malloc ( sizeof ( char* ) * i );
    
    tmp = strdup ( ratios->answer );
    i = 0;
    token = strtok ( tmp, "," );
    while ( token != NULL ) {
	ratio_str[i] = strdup ( token );    
	i ++;
        token = strtok ( NULL, "," );
    }       
    G_free ( tmp );
    
    NUM_RATIOS = 0;
    for  ( j = 0; j < i; j ++ ) {
        tmp = strdup ( ratio_str[j] );
	id = -1;
	val = -1.0;
	token = strtok ( tmp, "=" );
	if ( token != NULL ) {
	    id = atoi ( token );
	}
	token = strtok ( NULL, "=" );
	if ( token != NULL ) {
	    val = atof ( token );
	}
	if ( id > 0 && val > 0.0 ) {
	    RATIO_ID[NUM_RATIOS] = id;
	    RATIO_VAL[NUM_RATIOS] = val;
	    NUM_RATIOS ++;
	}
	G_free ( tmp );   
    }  
    
    for ( j = 0; j < i; j ++ ) {
        G_free ( ratio_str[j] );
    }
    if ( ratio_str != NULL ) {
        G_free ( ratio_str );
    }
    
    if ( DEBUG ) {
        fprintf ( stderr, "RATIOS: \n" );
            for ( i = 0; i < NUM_RATIOS; i ++ ) {
            fprintf ( stderr, " %i = %.3f\n", RATIO_ID[i], RATIO_VAL[i] ); 
        }
    }
    

    ID_COL_POS = atoi ( idcol->answer );
    X_COL_POS = atoi ( xcol->answer );
    Y_COL_POS = atoi ( ycol->answer );
    Z_COL_POS = atoi ( zcol->answer );
    if ( !strcmp ( mode->answer, "darts" ) ) {
        MODE = MODE_DARTS;
    }
    if ( !strcmp ( mode->answer, "lines" ) ) {
        MODE = MODE_LINES;
    }
    if ( !strcmp ( mode->answer, "planes_h" ) ) {
        MODE = MODE_PLANES_H;
    }
    if ( !strcmp ( mode->answer, "planes_v" ) ) {
        MODE = MODE_PLANES_V;
    }
    if ( !strcmp ( mode->answer, "points" ) ) {
        MODE = MODE_POINTS;
    }
    if ( !strcmp ( mode->answer, "pyramids" ) ) {
        MODE = MODE_PYRAMIDS;
    }    

    /* read point coordinates */
    createTable();
    
    /* create a new array with only valid points */
    validatePoints();

    if ( DEBUG ) {
        fprintf ( stderr, " *** POINTS PROCESSING DONE ***\n " );
    }
    
    /* 
    
    /* write out the result vector map */
    writeMap();

    /* clean up */
    freePts();
    
    if ( DEBUG ) {
        fprintf ( stderr, " *** WRITING OUTPUT MAP DONE ***\n " );
    }
    
    if ( RGB_MAPPER_COLOUR != NULL ) {
        G_free ( RGB_MAPPER_COLOUR );
    }
    
    G_message ("DONE!\n");

    exit(EXIT_SUCCESS);
}
