#!/bin/sh

# Makes copies of extra module in existing GRASS GIS source tree and 
# patches the GRASS top-level Makefiles in "DIRS" (see variable definition
# below.
#
# Only parameter is the full path and name of the GRASS source directory.
#
# WARNING: DO NOT USE THIS ON ANY GRASS SOURCE CODE TREE THAT YOU
# ACTIVELY WORK ON OR THAT'S CONNECTED TO AN SVN!!! IT WILL MESS UP
# YOUR SOURCE CODE!!!
#
# When the linking and patching is done, just configure, compile and 
# install your GRASS as you normally would.
#
# Please note:
# 1. This script was developed for GRASS 6.4.x. Other versions of
#    GRASS may require changes to the patching instructions in the
#    main program loop.
# 2. Tested on Linux only.
# 3. Each time this script is run, any compiled object binaries will 
#    be deleted (externally linked modules only, not GRASS distribution
#    modules).
# 4. A copy of the original Makefile (before the first attempt
#    to patch them) is stored as Makefile.org.0 for each "DIR"
# 5. This actually makes copies, not links, as links proved problematic
#    during compilation. So re-run this script after making any modifications
#    to any of the extra modules.



# Location for temporary files
TMP="/tmp"

# List of local directories to search for links
DIRS="general
imagery
lib
misc
raster
raster3d
scripts
vector"


# Check if we have a valid target directory
if [ -z $1 ]
then
	echo "ERROR: You need to specify the target path."
	exit 1
fi

if [ ! -d $1 ]
then
	echo "ERROR: $1 is not a valid directory."
	exit 1
fi

if [ ! -w $1 ]
then
	echo "ERROR: $1 is not writable."
	exit 1
fi

if [ -d "$1/.svn" ]
then
	echo "ERROR: $1 is connected to an SVN repository."
	echo "ERROR: Too risky. Aborting."
	exit 1
fi


# Create backup-copies of original Makefiles, before attempting to patch
# them for the first time.
for dir in $DIRS ; do
	echo "Backing up original top-level Makefiles: $dir"
	if [ ! -e "$1/$dir/Makefile.org.0" ] ; then
		echo "\t$1/$dir/Makefile -> $1/$dir/Makefile.org.0"
		cp -f "$1/$dir/Makefile" "$1/$dir/Makefile.org.0"
	fi
done


# Copy over include files
echo "Copying C header files (include)"
cp -fR "include" "$1/"


# Go through all local directories and make links.
# Also create patch files.
for dir in $DIRS ; do

	echo "Processing dir: $dir"
	
	# Create temporary file for Makefile patch
	MAKEPATCH="$TMP/$dir.Makefile.patch"
	touch $MAKEPATCH
	# The following closely matches GRASS 6.4, but might need to be adapted
	# for other versions of GRASS
	echo "--- $1/$dir/Makefile" > $MAKEPATCH
	echo "+++ $MAKEPATCH" >> $MAKEPATCH
	MAKEPATCH_ENTRIES=
	if [ $dir = "general" ] ; then
		MAKEPATCH_ENTRIES=" \tg.pnmcomp \0134\n \tg.region \0134\n \tg.setproj \0134\n"
		MATCH_LINES=6
		MATCH_FIRST=21
	fi
	if [ $dir = "imagery" ] ; then
		MAKEPATCH_ENTRIES=" \ti.maxlik \0134\n \ti.rectify \0134\n \ti.rgb.his \0134\n"
		MATCH_LINES=6
		MATCH_FIRST=11
	fi
	if [ $dir = "lib" ] ; then
		MAKEPATCH_ENTRIES=" \tdspf \0134\n \tsymbol \0134\n \tinit \0134\n"
		MATCH_LINES=6
		MATCH_FIRST=33
	fi
	if [ $dir = "misc" ] ; then
		MAKEPATCH_ENTRIES=" MODULE_TOPDIR = ..\n \n SUBDIRS = \0134\n"
		MATCH_LINES=6
		MATCH_FIRST=1
	fi
	if [ $dir = "raster" ] ; then
		MAKEPATCH_ENTRIES=" \tr.watershed \0134\n \tr.what \0134\n \tr.what.color \0134\n"
		MATCH_LINES=6
		MATCH_FIRST=110
	fi
	if [ $dir = "raster3d" ] ; then
		MAKEPATCH_ENTRIES=" \tr3.mkdspf \0134\n \tr3.out.ascii \0134\n \tr3.out.v5d \0134\n"
		MATCH_LINES=6
		MATCH_FIRST=8
	fi
	if [ $dir = "scripts" ] ; then
		MAKEPATCH_ENTRIES=" \tv.in.wfs \0134\n \tv.out.gpsbabel \0134\n \tv.rast.stats \0134\n"		
		MATCH_LINES=6
		MATCH_FIRST=79
	fi
	if [ $dir = "vector" ] ; then
		MAKEPATCH_ENTRIES=" \tv.type \0134\n \tv.univar \0134\n \tv.voronoi \0134\n"
		MATCH_LINES=6
		MATCH_FIRST=68
	fi
	MATCH_NEW=$MATCH_LINES

	# Go through all module subdirectories
	MODS=`ls $dir`
	for mod in $MODS ; do
		# Check if it's a valid GRASS module
		if [ -d $1 ] & [ -r "$1/Makefile" ]
		then
			# Looks like a module code directory
			echo -n "\tFound module: $mod ..."
			
			# Do we need to delete an existing module in
			# target directory?
			if [ -d "$1/$dir/$mod" ]
			then
				echo " OVERWRITING."
				rm -rf "$1/$dir/$mod"
			else
				echo " COPYING."
			fi

			# copy module directory
			cp -Rf "$dir/$mod" "$1/$dir/$mod"

			# Clean-up: Delete object binaries and SVN entries if any
			find "$1/$dir/$mod" -name "OBJ.*" -print0 | xargs -0 rm -rf
			find "$1/$dir/$mod" -name ".svn" -print0 | xargs -0 rm -rf
			# On Mac OS X, the lines below may work better:
			# find "$1/$dir/$mod" -name "OBJ.*" -exec rm -rf {} \;
			# find "$1/$dir/$mod" -name ".svn" -exec rm -rf {} \;

			# Add Makefile entry
			MAKEPATCH_ENTRIES="$MAKEPATCH_ENTRIES+\t$mod \0134\n"
			MATCH_NEW=`expr $MATCH_NEW + 1`
		fi
	done

	# Finalize Makefile patch and apply it
	echo "@@ -$MATCH_FIRST,$MATCH_LINES +$MATCH_FIRST,$MATCH_NEW @@" >> $MAKEPATCH
	# The following closely matches GRASS 6.4, but might need to be adapted
	# for other versions of GRASS
	if [ $dir = "general" ] ; then
		MAKEPATCH_ENTRIES="$MAKEPATCH_ENTRIES \tg.tempfile \0134\n \tg.transform \0134\n \tg.version \0134\n \tmanage\n"
	fi
	if [ $dir = "imagery" ] ; then
		MAKEPATCH_ENTRIES="$MAKEPATCH_ENTRIES \ti.smap \0134\n \ti.target \0134\n \ti.pca \0134\n \ti.cca\n"
	fi
	if [ $dir = "lib" ] ; then
		MAKEPATCH_ENTRIES="$MAKEPATCH_ENTRIES \tcdhc \0134\n \tstats \0134\n \tarraystats \0134\n \tpython\n"
	fi
	if [ $dir = "misc" ] ; then
		MAKEPATCH_ENTRIES=$MAKEPATCH_ENTRIES' \tm.cogo\n \n include $(MODULE_TOPDIR)/include/Make/Dir.make\n \n'
	fi
	if [ $dir = "raster" ] ; then
		MAKEPATCH_ENTRIES="$MAKEPATCH_ENTRIES \tsimwe \0134\n \twildfire\n\n"
	fi
	if [ $dir = "raster3d" ] ; then
		MAKEPATCH_ENTRIES="$MAKEPATCH_ENTRIES \tr3.out.vtk \0134\n \tr3.stats \0134\n \tr3.to.rast \0134\n base\n"
	fi
	if [ $dir = "scripts" ] ; then
		MAKEPATCH_ENTRIES="$MAKEPATCH_ENTRIES \tv.report \0134\n \tv.univar.sh \0134\n \tv.what.vect\n \n"
	fi
	if [ $dir = "vector" ] ; then
		MAKEPATCH_ENTRIES="$MAKEPATCH_ENTRIES \tv.what \0134\n \tv.what.rast \0134\n \tv.vol.rst\n \tlidar"
	fi
	echo "$MAKEPATCH_ENTRIES" >> $MAKEPATCH			
	# Patch only if there is something to patch in
	if [ $MATCH_NEW != $MATCH_LINES ] ; then
		# Can't patch the same file twice, so restore the original Makefile first
		if [ -e "$1/$dir/Makefile.org.0" ] ; then
			echo "Patching..."
			cp -f "$1/$dir/Makefile.org.0" "$1/$dir/Makefile"
		else
			echo "WARNING: Patching without backup."
		fi		
		#cat $MAKEPATCH
		patch -p0 "$1/$dir/Makefile" "$MAKEPATCH"
	fi
done


# All done!
echo "All done. Now configure, compile and install GRASS GIS."

exit 0
