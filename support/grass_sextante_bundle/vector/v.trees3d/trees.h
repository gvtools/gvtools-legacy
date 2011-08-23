/*
 *  header.h    ver. 0.1
 *
 *  header file for main.c
 *
 *  Jachym Cepicky,
 *
 */

#ifndef TREESPAR
#define TREESPAR
typedef struct {
    char name[24];
    double akoef;
    double lkoef;
    double bkoef;
    double rkrakoef;
} TREES;

TREES paramsspruce = {"spruce", 1.  , 0.66, 1.  , 0.5 };
TREES paramsfir    = {"fir",    0.5 , 0.5 , 0.5 , 0.5 };
TREES paramspine   = {"pine",   0.5  , 0.68, 0.5 , 0.63}; /* original parametres does not work :-/ */
TREES paramslarch  = {"larch",  0.5  , 0.66, 0.5 , 0.5 }; /* original parametres does not work :-/ */
TREES paramsbeech  = {"beech",  .33 , 0.4 , 0.33, 0.33};
TREES paramsoak    = {"oak",    0.33, 0.39, 0.33, 0.36};
TREES paramsdouglas= {"douglas",0.5  , 0.66, 0.5 , 0.5 }; /* original parametres does not work :-/ */
TREES paramsmaple  = {"maple",  0.33, 0.35, 0.52, 0.  };
TREES paramsalder  = {"alder",  0.5 , 0.56, 0.5 , 0.  };

#endif
