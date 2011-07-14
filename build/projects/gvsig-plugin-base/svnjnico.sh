PROJECTS="libraries/libjni-gdal
libraries/libjni-ecw
libraries/libjni-mrsid
libraries/libjni-potrace
libraries/libjni-proj4
"

for i in $PROJECTS; do
	project=$(echo $i | awk -F/ '{ print $2; }')
	svn co https://svn.forge.osor.eu/svn/gvsig-desktop/trunk/$i ../../../$project
done


