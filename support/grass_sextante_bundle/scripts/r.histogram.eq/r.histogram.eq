#!/bin/sh

############################################################################
#
# MODULE:       r.histogram.eq
# AUTHOR(S):    Benjamin Ducke <benjamin.ducke AT oadigital.net>
# PURPOSE:      Produces histogram-equalized color or grey scaling
#
# USAGE:	This is a convenience function which simply produces a copy
#		of the input raster map data and its color scheme, but
#		applies histogram equalization to the later.
#		This can be used to make better use of available color
#		dynamics in visualization tasks. The actual cell data is not
#		changed. By default, the input raster map's color scheme
#		will be copied and then equalized. Alternatively, a grey
#		scale scheme may be produced using the "-g" (grey) flag.
#
#		Sometimes, data may look clearer with inverted colors/grey
#		scales. This can be achieved via the "-i" (invert) flag.
#
# COPYRIGHT:    (C) 2010 by Benjamin Ducke
#
#               This program is free software under the GNU General Public
#               License (>=v2). Read the file COPYING that comes with GRASS
#               for details.
#
#############################################################################


#%Module
#% description: Trims input data around mean, removing extreme values and outliers.
#% keywords: raster, histogram, equalization, colors, visualization, geophysics, signals
#%End

#%Option
#% key: input
#% type: string
#% required: yes
#% multiple: no
#% key_desc: name
#% description: Name of input raster map
#% gisprompt: old,cell,raster
#%End

#%Option
#% key: output
#% type: string
#% required: yes
#% multiple: no
#% key_desc: name
#% description: Name of output raster map
#% gisprompt: new,cell,raster
#%End

#%Flag
#%  key: g
#%  description: Produce grey scale output
#%End

#%Flag
#%  key: i
#%  description: Invert color/grey scale
#%End

MODULE_NAME=r.histogram.eq


if [ -z "$GISBASE" ] ; then
	echo "ERROR: You must be in GRASS GIS to run this program." 1>&2
	exit 1
fi

if [ "$1" != "@ARGS_PARSED@" ] ; then
	exec g.parser "$0" "$@"
fi


# Make copy of original input map
g.message "Copying input data..."
g.copy --quiet rast="$GIS_OPT_INPUT","$GIS_OPT_OUTPUT"

g.message "Equalizing..."
INVERT=""
if [ "$GIS_FLAG_I" = "1" ] ; then
	INVERT="-n"
fi
if [ "$GIS_FLAG_G" = "0" ] ; then
	# copy color table from input map, rescale (-e)
	r.colors --quiet map="$GIS_OPT_OUTPUT" raster="$GIS_OPT_INPUT" -e $INVERT
else
	# make greyscale table, rescale (-e)
	r.colors --quiet map="$GIS_OPT_OUTPUT" color=grey -e $INVERT
fi

g.message "Done."

exit 0

