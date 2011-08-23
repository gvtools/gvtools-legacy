/****************************************************************
 *
 * MODULE:     v.trees3d
 *
 * AUTHOR(S):  Jachym Cepicky jachym.cepicky _at_ centrum _dot_ cz
 *
 * PURPOSE:    Makes 3D forest vectors from input point vector file
 *             Useful for creating screenshots for displaying with NVIZ
 *
 * COPYRIGHT:  (C) 2005 by the GRASS Development Team
 *
 *             This program is free software under the
 *             GNU General Public License (>=v2).
 *             Read the file COPYING that comes with GRASS
 *             for details.
 *
 * TODO:      heavy code cleaning
 ****************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <grass/gis.h>
#include <grass/Vect.h>
#include <grass/dbmi.h>
#include <grass/glocale.h>
#include "trees.h"

long make3dTree(struct Map_info Out, struct line_cats *Cats, struct line_pnts *Points, struct line_pnts *NewPoints, int fdrast, char *species, double height, double diameter, double hvar, double vvar, double clength, struct Cell_head window);

int main(int argc, char *argv[])
{
    struct Map_info In, Out;
    static struct line_pnts *Points;
    static struct line_pnts *NewPoints;
    struct GModule *module;
    struct line_cats *Cats;
    int i, type, cat, fdrast = 0;
    char *mapset;
    struct Option *old, *new, *heightopt, *diameteropt, *hcolumn, *dcolumn, *specolumn, *elevation, *vvaropt, *hvaropt, *clengthopt, *field_opt;
    struct Option *spruceopt, *pineopt, *larchopt, *beechopt, *firopt, *oakopt, *douglasopt, *mapleopt, *alderopt;
    double height = 0, diameter = 0, hvar = 0, vvar = 0, clength = 50;
    char species[24];
    int spruce, pine, larch, beech, fir, oak, douglas, maple, alder;
    struct Cell_head window;
    struct cat_list *Clist;
    int nelements;
    int point;
    int height_from_db = 0;
    int diameter_from_db = 0;
    int spec_from_db = 0;
    int random;
    BOUND_BOX box;

    /* dbmi */
    dbDriver *driver = NULL;
    dbString sql, spec_string;
    dbCursor cursor;
    dbTable *table;
    dbColumn *hdbcolumn; /* height database column */
    dbColumn *ddbcolumn; /* diameter database column */
    dbColumn *sdbcolumn; /* species database column */
    dbValue *value;
    struct field_info *Fi;
    char query[1024];
    int more;
    

    /* random */
    srand((unsigned int) time(NULL));

    module = G_define_module();
    module->description =
	_("Makes 3D trees from input point vector file");

    old = G_define_standard_option(G_OPT_V_INPUT);
    old->description = _("Name of input point vector map");

    new = G_define_standard_option(G_OPT_V_OUTPUT);
    new->description = _("Name of resulting 3D vector map");

    diameteropt = G_define_option();
    diameteropt->key = "diameter";
    diameteropt->description = _("Diameter of the tree crown in mapunits");
    diameteropt->type = TYPE_DOUBLE;
    diameteropt->required = NO;
    diameteropt->answer = "8";

    heightopt = G_define_option();
    heightopt->key = "height";
    heightopt->description = _("Height of tree trunk");
    heightopt->type = TYPE_DOUBLE;
    heightopt->required = NO;
    heightopt->answer = "30";

    dcolumn = G_define_option();
    dcolumn->key = "dcolumn";
    dcolumn->description = _("Name of the column with tree crown width");
    dcolumn->type = TYPE_DOUBLE;
    dcolumn->required = NO;

    hcolumn = G_define_option();
    hcolumn->key = "hcolumn";
    hcolumn->description = _("Name of the column with tree height");
    hcolumn->type = TYPE_STRING;
    hcolumn->required = NO;

    elevation = G_define_standard_option(G_OPT_R_MAP);
    elevation->key = "elevation";
    elevation->required = NO;
    elevation->description = _("Elevation raster");

    specolumn = G_define_option();
    specolumn->key = "specolumn";
    specolumn->description = _("Name of the column with tree species");
    specolumn->type = TYPE_STRING;
    specolumn->required = NO;

    spruceopt = G_define_option();
    spruceopt->key = "spruce";
    spruceopt->type = TYPE_INTEGER;
    spruceopt->required = NO;
    spruceopt->multiple = NO;
    spruceopt->options = "0-100";
    spruceopt->answer = "100";
    spruceopt->description = _("Representation of spruce");

    firopt = G_define_option();
    firopt->key = "fir";
    firopt->type = TYPE_INTEGER;
    firopt->required = NO;
    firopt->multiple = NO;
    firopt->options = "0-100";
    firopt->answer = "0";
    firopt->description = _("Representation of fir");

    pineopt = G_define_option();
    pineopt->key = "pine";
    pineopt->type = TYPE_INTEGER;
    pineopt->required = NO;
    pineopt->multiple = NO;
    pineopt->options = "0-100";
    pineopt->answer = "0";
    pineopt->description = _("Representation of pine");

    larchopt = G_define_option();
    larchopt->key = "larch";
    larchopt->type = TYPE_INTEGER;
    larchopt->required = NO;
    larchopt->multiple = NO;
    larchopt->options = "0-100";
    larchopt->answer = "0";
    larchopt->description = _("Representation of larch");

    beechopt = G_define_option();
    beechopt->key = "beech";
    beechopt->type = TYPE_INTEGER;
    beechopt->required = NO;
    beechopt->multiple = NO;
    beechopt->options = "0-100";
    beechopt->answer = "0";
    beechopt->description = _("Representation of beech");

    oakopt = G_define_option();
    oakopt->key = "oak";
    oakopt->type = TYPE_INTEGER;
    oakopt->required = NO;
    oakopt->multiple = NO;
    oakopt->options = "0-100";
    oakopt->answer =  "0";
    oakopt->description = _("Representation of oak");

    douglasopt = G_define_option();
    douglasopt->key = "douglas";
    douglasopt->type = TYPE_INTEGER;
    douglasopt->required = NO;
    douglasopt->multiple = NO;
    douglasopt->options = "0-100";
    douglasopt->answer = "0";
    douglasopt->description = _("Representation of douglas");

    mapleopt = G_define_option();
    mapleopt->key = "maple";
    mapleopt->type = TYPE_INTEGER;
    mapleopt->required = NO;
    mapleopt->multiple = NO;
    mapleopt->options = "0-100";
    mapleopt->answer = "0";
    mapleopt->description = _("Representation of maple");

    alderopt = G_define_option();
    alderopt->key = "alder";
    alderopt->type = TYPE_INTEGER;
    alderopt->required = NO;
    alderopt->multiple = NO;
    alderopt->options = "0-100";
    alderopt->answer = "0";
    alderopt->description = _("Representation of alder");

    vvaropt = G_define_option();
    vvaropt->key = "vvar";
    vvaropt->type = TYPE_DOUBLE;
    vvaropt->required = NO;
    vvaropt->multiple = NO;
    vvaropt->gisprompt = "vvar";
    vvaropt->description = _("Vertical variability. Variability of tree heights [% of height].");
    vvaropt->options = "0-100";
    vvaropt->answer = "0";

    hvaropt = G_define_option();
    hvaropt->key = "hvar";
    hvaropt->type = TYPE_DOUBLE;
    hvaropt->required = NO;
    hvaropt->multiple = NO;
    hvaropt->gisprompt = "hvar";
    hvaropt->description = _("Horizontal variability in map units");
    hvaropt->answer = "0";

    clengthopt = G_define_option();
    clengthopt->key = "clength";
    clengthopt->type = TYPE_DOUBLE;
    clengthopt->required = NO;
    clengthopt->multiple = NO;
    clengthopt->gisprompt = "clength";
    clengthopt->description = _("Crown length [% of tree height]");
    clengthopt->answer = "50";
    clengthopt->options = "0-100";

    field_opt = G_define_standard_option(G_OPT_V_FIELD);

    G_gisinit(argv[0]);
    if (G_parser(argc, argv))
	exit(EXIT_FAILURE);

    /* parameters control */
    if (!heightopt->answer && !hcolumn->answer) {
	G_fatal_error(_("One of [%s] or [%s] parameters must be set"), heightopt->key,
		      hcolumn->key);
    }

    if (!diameteropt->answer && !dcolumn->answer) {
	G_fatal_error(_("One of [%s] or [%s] parameters must be set"), diameteropt->key,
		      dcolumn->key);
    }

    /* species */
    if (spruceopt->answer) {
        sscanf(spruceopt->answer, "%d", &spruce);
    }
    if (pineopt->answer) {
        sscanf(pineopt->answer, "%d", &pine);
    }
    if (larchopt->answer) {
        sscanf(larchopt->answer, "%d", &larch);
    }
    if (beechopt->answer) {
        sscanf(beechopt->answer, "%d", &beech);
    }
    if (firopt->answer) {
        sscanf(firopt->answer, "%d", &fir);
    }
    if (oakopt->answer) {
        sscanf(oakopt->answer, "%d", &oak);
    }
    if (douglasopt->answer) {
        sscanf(douglasopt->answer, "%d", &douglas);
    }
    if (mapleopt->answer) {
        sscanf(mapleopt->answer, "%d", &maple);
    }
    if (alderopt->answer) {
        sscanf(alderopt->answer, "%d", &alder);
    }
    if(spruce+pine+larch+beech+fir+oak+douglas+maple+alder > 100)
        G_warning(_("Total species representation is > 100"));

    if(spruce+pine+larch+beech+fir+oak+douglas+maple+alder < 100)
        G_warning(_("Total species representation is < 100"));


    if (hcolumn->answer)
        height_from_db = 1;
    if (dcolumn->answer)
        diameter_from_db = 1;
    if (specolumn->answer)
        spec_from_db = 1;

    /* variabels setting */
    sscanf(heightopt->answer, "%lf", &height);
    sscanf(diameteropt->answer, "%lf", &diameter);
    sscanf(hvaropt->answer, "%lf", &hvar);
    sscanf(vvaropt->answer, "%lf", &vvar);
    sscanf(clengthopt->answer, "%lf", &clength);



    /* set input vector file name and mapset */
    Vect_check_input_output_name(old->answer, new->answer, GV_FATAL_EXIT);
    if ((mapset = G_find_vector2(old->answer, "")) == NULL)
	G_fatal_error(_("Could not find input vector map %s"), old->answer);

    /* vector setup */
    Points = Vect_new_line_struct();
    NewPoints = Vect_new_line_struct();
    Cats = Vect_new_cats_struct();

    Vect_set_open_level(2);
    Vect_open_new(&Out, new->answer, WITH_Z);

    /* opening old vector */
    Vect_open_old(&In, old->answer, mapset);

    /* opening database connection, if required */
    if (height_from_db || diameter_from_db || spec_from_db) {
	Clist = Vect_new_cat_list();
	Clist->field = atoi(field_opt->answer);
	if ((Fi = Vect_get_field(&In, Clist->field)) == NULL)
	    G_fatal_error(_("Database connection not defined"));

	if ((driver =
	     db_start_driver_open_database(Fi->driver, Fi->database)) == NULL)
	    G_fatal_error(_("Cannot open driver %s"), Fi->driver);
    }

    /* do we work with elevation raster? */
    if (elevation->answer) {

	/* raster setup */
	G_get_window(&window);
        Vect_region_box ( &window, &box );

	/* check for the elev raster, and check for error condition */
	if ((mapset = G_find_cell2(elevation->answer, "")) == NULL) {
	    G_fatal_error("cell file [%s] not found", elevation->answer);
	}

	/* open the elev raster, and check for error condition */
	if ((fdrast = G_open_cell_old(elevation->answer, mapset)) < 0) {
	    G_fatal_error("can't open cell file [%s]", elevation->answer);
	}
    }

   /* if not type=area */
    G_debug(1, "drawing trees");
    i = 1;
    /* loop through each point in the dataset */
    nelements = Vect_get_num_lines(&In);
    if (nelements < 1)
	    G_fatal_error("no points found in map [%s], done", old->answer);

    for (point = 1; point <= nelements; point++) {

        /* progress feedback */
        G_percent(point, nelements, 1);

        /* read point */
        type = Vect_read_line(&In, Points, Cats, point);

        if (!(type & GV_POINT) ) continue; /* points only */

        /* check the region */
        /* if (!Vect_point_in_box ( Points->x[0], Points->y[0], 0.0, &box))
            continue; */



        if (Vect_cat_get(Cats, 1, &cat) == 0) {
            Vect_cat_set(Cats, 1, i);
            i++;
        }

        /* what tree do we draw? */
        random = rand() % 101;
        if (random <= spruce)
            strcpy(species, "spruce");
        else if (random < spruce+fir)
            strcpy(species, "fir");
        else if (random < spruce+fir+pine)
            strcpy(species, "pine");
        else if (random < spruce+fir+pine+larch)
            strcpy(species, "larch");
        else if (random < spruce+fir+pine+beech)
            strcpy(species, "beech");
        else if (random < spruce+fir+pine+beech+oak)
            strcpy(species, "oak");
        else if (random < spruce+fir+pine+beech+oak+douglas)
            strcpy(species, "douglas");
        else if (random < spruce+fir+pine+beech+oak+douglas+maple)
            strcpy(species, "maple");
        else
            strcpy(species, "alder");


        /* height or diameter columns set */
        if (height_from_db || diameter_from_db || spec_from_db) {
            G_debug(3,"height or diameter or species from database");
            
            cat = Vect_get_line_cat(&In, point, Clist->field);
            db_init_string(&sql);

            /* height, diameter, species */
            if (height_from_db && diameter_from_db && spec_from_db) {
                G_debug(3,"height and diameter and species from database");
                sprintf(query, "SELECT %s, %s, %s FROM %s WHERE %s = %d",
                        hcolumn->answer, dcolumn->answer, specolumn->answer, Fi->table, Fi->key, cat);
            }
            /* height, diameter */
            else if (height_from_db && diameter_from_db) {
                G_debug(3,"height and diameter from database");
                sprintf(query, "SELECT %s, %s FROM %s WHERE %s = %d",
                        hcolumn->answer, dcolumn->answer, Fi->table, Fi->key, cat);
            }
            /* height, species */
            else if (height_from_db && spec_from_db) {
                G_debug(3,"height and species from database");
                sprintf(query, "SELECT %s, %s FROM %s WHERE %s = %d",
                        hcolumn->answer, specolumn->answer, Fi->table, Fi->key, cat);
            }
             /* diameter, species */
            else if (diameter_from_db && spec_from_db) {
                G_debug(3,"diameter and species from database");
                sprintf(query, "SELECT %s, %s FROM %s WHERE %s = %d",
                        dcolumn->answer, specolumn->answer, Fi->table, Fi->key, cat);
            }
             /* height */
           else if (height_from_db) {
                G_debug(3,"height from database");
                sprintf(query, "SELECT %s FROM %s WHERE %s = %d",
                        hcolumn->answer, Fi->table, Fi->key, cat);
            }
             /* diameter */
            else if (diameter_from_db) {
                G_debug(3,"diameter from database");
                sprintf(query, "SELECT %s FROM %s WHERE %s = %d",
                        dcolumn->answer, Fi->table, Fi->key, cat);
            }
            /* species */
            else if (spec_from_db) {
                G_debug(3,"species from database");
                sprintf(query, "SELECT %s FROM %s WHERE %s = %d",
                        specolumn->answer, Fi->table, Fi->key, cat);
            }

            /* debug */
            G_debug(3, "SQL: %s", query);
            db_append_string(&sql, query);

            if (db_open_select_cursor(driver, &sql, &cursor, DB_SEQUENTIAL)
                != DB_OK)
                G_fatal_error(_("Cannot select attributes for point #%d"),
                                point);
            table = db_get_cursor_table(&cursor);

            /* data mining*/
            /* height, diameter, species */
            if (height_from_db && diameter_from_db && spec_from_db) {
                hdbcolumn = db_get_table_column(table, 0);/* first column */
                ddbcolumn = db_get_table_column(table, 1);/* second column */
                sdbcolumn = db_get_table_column(table, 2);/* second column */
                if (db_fetch(&cursor, DB_NEXT,&more) != DB_OK)
                    continue;

                    value = db_get_column_value(hdbcolumn);
                height = db_get_value_as_double(value, db_get_column_host_type(hdbcolumn));
                    value = db_get_column_value(ddbcolumn);
                diameter = db_get_value_as_double(value, db_get_column_host_type(ddbcolumn));
                    value = db_get_column_value(sdbcolumn);
                db_convert_column_value_to_string (sdbcolumn, &spec_string);
                sprintf(species, "%s", db_get_string(&spec_string));
                /* control */
                G_debug(3, "point %d: height: %f diameter: %f species: %s", point, height, diameter, species);
            }
            /* height, diameter  */
            else if (height_from_db && diameter_from_db) {
                hdbcolumn = db_get_table_column(table, 0);/* first column */
                ddbcolumn = db_get_table_column(table, 1);/* second column */
                if (db_fetch(&cursor, DB_NEXT,&more) != DB_OK)
                    continue;

                    value = db_get_column_value(hdbcolumn);
                height = db_get_value_as_double(value, db_get_column_host_type(hdbcolumn));
                    value = db_get_column_value(ddbcolumn);
                diameter = db_get_value_as_double(value, db_get_column_host_type(ddbcolumn));
                /* control */
                G_debug(3, "point %d: height: %f diamter: %f", point, height, diameter);
            }
            /* height, species  */
            else if (height_from_db && spec_from_db) {
                hdbcolumn = db_get_table_column(table, 0);/* first column */
                sdbcolumn = db_get_table_column(table, 1);/* second column */
                if (db_fetch(&cursor, DB_NEXT,&more) != DB_OK)
                    continue;

                    value = db_get_column_value(hdbcolumn);
                height = db_get_value_as_double(value, db_get_column_host_type(hdbcolumn));
                    value = db_get_column_value(sdbcolumn);
                db_convert_column_value_to_string (sdbcolumn, &spec_string);
                sprintf(species, "%s", db_get_string(&spec_string));
                /* control */
                G_debug(3, "point %d: height: %f diamter: %f", point, height, diameter);
            }
             /* diameter species */
            else if (diameter_from_db && spec_from_db) {
                ddbcolumn = db_get_table_column(table, 0);/* first column */
                sdbcolumn = db_get_table_column(table, 1);/* second column */
                if (db_fetch(&cursor, DB_NEXT,&more) != DB_OK)
                    continue;

                    value = db_get_column_value(ddbcolumn);
                diameter = db_get_value_double(value);
                    value = db_get_column_value(sdbcolumn);
                db_convert_column_value_to_string (sdbcolumn, &spec_string);
                sprintf(species, "%s", db_get_string(&spec_string));
                /* control */
                G_debug(3, "point %d: diamter: %f species: %s", point, diameter, species);
            }
             /* height */
            else if (height_from_db) {
                hdbcolumn = db_get_table_column(table, 0);/* first column */
                if (db_fetch(&cursor, DB_NEXT,&more) != DB_OK)
                    continue;
                /* height value */
                value = db_get_column_value(hdbcolumn);
                height = db_get_value_as_double(value, db_get_column_host_type(hdbcolumn));
                /* control */
                G_debug(3, "point %d: height: %f", point, height);
            }

             /* diameter */
            else if (diameter_from_db) {
                ddbcolumn = db_get_table_column(table, 0);/* first column */
                if (db_fetch(&cursor, DB_NEXT,&more) != DB_OK)
                    continue;
                /* diameter value */
                value = db_get_column_value(ddbcolumn);
                diameter = db_get_value_as_double(value, db_get_column_host_type(ddbcolumn));
                /* control */
                G_debug(3, "point %d: diameter: %f", point, diameter);
            }

            /* species */
            else if (spec_from_db) {
                sdbcolumn = db_get_table_column(table, 0);/* first column */
                if (db_fetch(&cursor, DB_NEXT,&more) != DB_OK)
                    continue;
                /* diameter value */
                value = db_get_column_value(sdbcolumn);
                db_convert_column_value_to_string (sdbcolumn, &spec_string);
                sprintf(species, "%s", db_get_string(&spec_string));
                /* control */
                G_debug(3, "point %d: species: %s", point, species);
            }

            if (db_fetch(&cursor, DB_NEXT, &more) != DB_OK)
                continue;



        } /* at least one database colunm set */
        G_debug(3, "%d: height: %.2f diameter: %.2f species: %s vvar: %.2f hvar: %.2f clength: %.2f random: %d\n", point,  height, diameter, species, vvar, hvar, clength, random);
        if (!strcmp(species,"") || height == 0 || diameter == 0) {
            continue;
        }
    make3dTree(Out, Cats, Points, NewPoints, fdrast, species, height, diameter, hvar, vvar, clength, window); 

    }			/* for each line */

    if (driver) {
	db_close_database(driver);
	db_shutdown_driver(driver);
    }

    Vect_build(&Out, stdout);
    Vect_close(&In);
    Vect_close(&Out);

    exit(EXIT_SUCCESS);
}

/* for each point int struct line_pnts *Poins calculates "roof" and "walls",
 * result is stored to struct line_pnts *NewPoints
 */
long make3dTree(struct Map_info Out, struct line_cats *Cats, struct line_pnts *Points, struct line_pnts *NewPoints, int fdrast, char *species, double height, double diameter, double hvar, double vvar, double clength, struct Cell_head window)
{
    float estimated_elevation = 0.;
    long result = 0;
    double pi=3.141592653;
    double v_step; /* viz v_step = lo/5; */
    double h_step=2.*pi/8.;
    double lo;    /* length of the sun part of crown */
    double r_on_this_level; /* crown radius on current level */
    double r_on_previous_level; /* crown radius on previous level */
    double peast;   /* x coordinate of new point */
    double pnorth;   /* y coordinate of new point */
    double pelev;   /* z coordinate of new point */
    double x, y;    /* new position of the tree according to vvar */
    double maxradius = diameter/2;

    /* variables for the crown formula */
    double akoef;
    double lkoef;
    double bkoef;
    double rkrakoef;

    /* help variables */
    double i, j;
    double random;

    /* setting parameters for tree crown */
    if (!strcmp(paramsspruce.name, species)) {
        akoef = paramsspruce.akoef;
        lkoef = paramsspruce.lkoef;
        bkoef = paramsspruce.bkoef;
        rkrakoef = paramsspruce.rkrakoef;
    }
    else if (!strcmp(paramspine.name, species)) {
        akoef = paramspine.akoef;
        lkoef = paramspine.lkoef;
        bkoef = paramspine.bkoef;
        rkrakoef = paramspine.rkrakoef;
    }

    else if (!strcmp(paramsfir.name, species)) {
        akoef = paramsfir.akoef;
        lkoef = paramsfir.lkoef;
        bkoef = paramsfir.bkoef;
        rkrakoef = paramsfir.rkrakoef;
    }

    else if (!strcmp(paramslarch.name, species)) {
        akoef = paramslarch.akoef;
        lkoef = paramslarch.lkoef;
        bkoef = paramslarch.bkoef;
        rkrakoef = paramslarch.rkrakoef;
    }

    else if (!strcmp(paramsbeech.name, species)) {
        akoef = paramsbeech.akoef;
        lkoef = paramsbeech.lkoef;
        bkoef = paramsbeech.bkoef;
        rkrakoef = paramsbeech.rkrakoef;
    }

    else if (!strcmp(paramsoak.name, species)) {
        akoef = paramsoak.akoef;
        lkoef = paramsoak.lkoef;
        bkoef = paramsoak.bkoef;
        rkrakoef = paramsoak.rkrakoef;
    }

    else if (!strcmp(paramsdouglas.name, species)) {
        akoef = paramsdouglas.akoef;
        lkoef = paramsdouglas.lkoef;
        bkoef = paramsdouglas.bkoef;
        rkrakoef = paramsdouglas.rkrakoef;
    }
    else if (!strcmp(paramsmaple.name, species)) {
        akoef = paramsmaple.akoef;
        lkoef = paramsmaple.lkoef;
        bkoef = paramsmaple.bkoef;
        rkrakoef = paramsmaple.rkrakoef;
    }

    else if (!strcmp(paramsalder.name, species)) {
        akoef = paramsalder.akoef;
        lkoef = paramsalder.lkoef;
        bkoef = paramsalder.bkoef;
        rkrakoef = paramsalder.rkrakoef;
    }
    else {
        G_warning(_("Species <%s> not known. I'll use parameters for spruce instead"), species);
        akoef = paramsspruce.akoef;
        lkoef = paramsspruce.lkoef;
        bkoef = paramsspruce.bkoef;
        rkrakoef = paramsspruce.rkrakoef;
    }

    /*
     * Pretz, Hans 2002:Grundlagen der Waldwachstumsforschung, Parey Buchverlag. Berlin, Wien, ISBN: 3-8263-3223-7, s.208
     *
     * 
     * +----_          ----------------+---------+
     * |      \        ^               ^         ^
     * |        \      | E             | lo      |
     * |          \    V               |         |
     * | <------->  \                  |         |
     * | r=diameter/(lo)**akoef*E**bkoef         |
     * |              \                |         |
     * |               \               |         |
     * |                |              |         |
     * |     diameter   |              V         |
     * | <------------> |  ------------+         | length = height*clength/100
     * |               /               ^         |
     * |              /                |         |
     * |             /                 |         |
     * |            /                  |         |
     * |           /                   |         |
     * |          /                    | lu = length-lo
     * |         /                     |         |
     * |        /                      |         |
     * |       /                       |         |
     * |      /                        |         |
     * |     /                         V         V
     * +-----            --------------+---------+
     * ^    ^
     * |    |
     * +----+
     *  rkrakoef
     */
    
    rkrakoef *= maxradius;
    lo = lkoef*clength/100*height; /* length of sun part of the crown */
    v_step = lo/4.;
    

    /* new tree position */
    random = ((double)rand()/(RAND_MAX +1.))*2-1; /* <-1:1> */
    x = Points->x[0] + (hvar*random);
    y = Points->y[0] + (hvar*random);
    /* new tree height */
    random = ((double)rand()/(RAND_MAX +1.))*2-1; /* <-1:1> */
    height = height + height*(vvar/200)*random;

    G_debug(3, "%s: random: %f height: %.2f maxradius: %.2f x: %.2f y: %.2f\n", species, random, height, maxradius, x, y);



    /* let it be the forest! */

    /* height from the raster */
    if (fdrast) {
        estimated_elevation = 
		G_get_raster_sample(fdrast, &window, NULL, y, x, 0, NEAREST);

    }		/* /if(fdrast) */
    else {
        estimated_elevation = 0.;
    }

        /* horizontal loop of the crown */
    for( i = h_step; i <= 2*pi; i+=h_step) {

        /* vertical loop of the crown - sun part of the crown */
        for ( j = v_step; j <= lo+0.1; j += v_step){
        G_debug(4, "lo %f j %f v_step %f %d\n",lo, j, v_step);
            Vect_reset_line(NewPoints);
            /* printf("tady j %f lo %f\n", j, lo);
           */
            /* radius of the crown on current and previous level */
            r_on_this_level = maxradius/(pow(lo,akoef))*pow(j,bkoef);
            r_on_previous_level = maxradius/(pow(lo,akoef))*pow((j-v_step),bkoef);

            /* the crown is build up from tetragons
                *    3      2
                *     +----+
                *    /      \
                *   /        \
                *  +----------+
                * 4            1
                *
                */

            /* point #1 */
            peast =  x+cos(i)*r_on_this_level;
            pnorth = y+sin(i)*r_on_this_level;
            pelev =  estimated_elevation+height-j;
            Vect_append_point(NewPoints, peast, pnorth, pelev);

            /* point #2 */
            /* first segment is ONLY triangle */
            if (j-v_step == 0) {
                peast =  x;
                pnorth = y;
                pelev =  estimated_elevation+height;
                Vect_append_point(NewPoints, peast, pnorth, pelev);
            }
            else {
                peast =  x+cos(i)*r_on_previous_level;
                pnorth = y+sin(i)*r_on_previous_level;
                pelev =  estimated_elevation+height-j+v_step;
                Vect_append_point(NewPoints, peast, pnorth, pelev);
                /* point #3 */
                peast =  x+cos(i+h_step)*r_on_previous_level;
                pnorth = y+sin(i+h_step)*r_on_previous_level;
                pelev =  estimated_elevation+height-j+v_step;
                Vect_append_point(NewPoints, peast, pnorth, pelev);
            }
            /* point #4 */
            peast =  x+cos(i+h_step)*r_on_this_level;
            pnorth = y+sin(i+h_step)*r_on_this_level;
            pelev =  estimated_elevation+height-j;
            Vect_append_point(NewPoints, peast, pnorth, pelev);

            /* point #1 */
            peast =  x+cos(i)*r_on_this_level;
            pnorth = y+sin(i)*r_on_this_level;
            pelev =  estimated_elevation+height-j;
            Vect_append_point(NewPoints, peast, pnorth, pelev);

            /* write whole crown segment */
            Vect_write_line(&Out, GV_FACE, NewPoints, Cats);


        } /* end of vertical loop */

        /* shadowed part of the crown */
        Vect_reset_line(NewPoints);
        /*  3          2
         *  +----------+
         *   \        /
         *    \      /
         *     +----+
         *    4      1
         *
         */

        /* point #1 */
        peast = x+cos(i)*rkrakoef;
        pnorth = y+sin(i)*rkrakoef;
        pelev = estimated_elevation+height-height*clength/100;
        Vect_append_point(NewPoints, peast, pnorth, pelev);
        /* point #2 */
        peast =  x+cos(i)*r_on_this_level;
        pnorth = y+sin(i)*r_on_this_level;
        pelev = estimated_elevation+height-lo;
        Vect_append_point(NewPoints, peast, pnorth, pelev);
        /* point #3 */
        peast =  x+cos(i+h_step)*r_on_this_level;
        pnorth = y+sin(i+h_step)*r_on_this_level;
        pelev =  estimated_elevation+height-lo;
        Vect_append_point(NewPoints, peast, pnorth, pelev);
        /* point #4 */
        peast  = x+cos(i+h_step)*rkrakoef;
        pnorth = y+sin(i+h_step)*rkrakoef;
        pelev =  estimated_elevation+height-height*clength/100;
        Vect_append_point(NewPoints, peast, pnorth, pelev);
        /* point #1 */
        peast = x+cos(i)*rkrakoef;
        pnorth = y+sin(i)*rkrakoef;
        pelev = estimated_elevation+height-height*clength/100;
        Vect_append_point(NewPoints, peast, pnorth, pelev);
        Vect_write_line(&Out, GV_FACE, NewPoints, Cats);

    } /* end of horizontal loop */

    /* trunk */
    Vect_reset_line(NewPoints);
    Vect_append_point(NewPoints, x, y, estimated_elevation);
    Vect_append_point(NewPoints, x, y, estimated_elevation+height-height*clength/100);
    Vect_write_line(&Out, GV_LINE, NewPoints, Cats);

    return result;
}
