#!/bin/sh

############################################################################
#
# script:	klad.sh
# autor:	Jachym Cepicky [jachym.cepicky <at> centrum <dot> cz]
# date:         11.4.2005
# popis:	creates vector map of raster boundaries
# copyright:	This program is free software under the GNU General Public
#		License (>=v2). 
# pozadavky:    - sed
#               - grep
#############################################################################

if test "$GISBASE" = ""; then
 echo "You must be in GRASS GIS to run this program." >&2
 exit 1
fi

#%Module
#% description: creates vector map of raster boundaries
#%End
#%option
#% key: raster
#% type: string
#% multiple : yes
#% description: rasters to be traced
#% required: yes
#%End
#%option
#% key: output
#% type: string
#% description: name of resulting vector file
#% required: yes
#%End

if [ "$1" != "@ARGS_PARSED@" ] ; then
  exec g.parser "$0" "$@"
fi

#####################################################################
# global variables
#####################################################################
group="$GIS_OPT_raster"
output="$GIS_OPT_output"

outfile="/tmp/grass-raster_vector.ascii"
i=0


eval `g.gisenv`



echo "Creating table $output"

if [ "`g.mlist vect |grep $output`" != "" ]; then
    echo "Vector exists, removing..."
    g.remove vect=$output
fi
    
echo "CREATE TABLE $output (cat int, raster varchar(50));"| db.execute

echo "Creating ascii file + vector table"
echo -n "ORGANIZATION: GRASS Development Team
DIGIT DATE:   
DIGIT NAME:   -
MAP NAME:     $output
MAP DATE:     2005
MAP SCALE:    10000
OTHER INFO:   Klad listu
ZONE:         0
MAP THRESH:   0.500000
VERTI:
" > $outfile
       
for file in `echo $group|sed -e s/,/\ /g`; do
    i=`echo $i+1|bc`
    
    # grass 6.1 or < ?
    if [ "`echo $GRASS_VERSION| grep 6.1`" == "" ]; then
        cd $GISDBASE/$LOCATION_NAME/$MAPSET/cellhd/
        eval `cat $file | grep 'north\|south\|east\|west'|sed -e s/:\ */=/`
    else 
        eval `r.info -g $file`
    fi
    echo "B 5" >> $outfile
    echo "$east $north" >> $outfile
    echo "$east $south" >> $outfile
    echo "$west $south" >> $outfile
    echo "$west $north" >> $outfile
    echo "$east $north" >> $outfile
    east=`echo "$east+(($west)-($east))/2" |bc `
    north=`echo "$south+(($north)-($south))/2" |bc `
    echo -e "C 1 1\n$east $north\n1 $i" >> $outfile
    
    echo "INSERT INTO $output (cat, raster) VALUES ($i,'$file');"|db.execute
done
    
outfileout="temp_vector_file_klad"
v.in.ascii in=$outfile out=$outfileout format=standard
v.clean tool=break,rmdupl in=$outfileout out=$output
g.remove vect=$outfileout
v.db.connect map=$output table=$output
rm -f $outfile
