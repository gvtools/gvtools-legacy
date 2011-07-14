PROJECTS="libraries/libCacheService
libraries/lib3DMap-share
libraries/libGeometries3D
libraries/libGPE-OSG
libraries/lib3DMap
extensions/ext3Dgui"

if [ $# -ne 2 ]
then
  echo "Usage: `basename $0` {branch name} {branch message}"
  exit 65
fi

svn mkdir https://gvsig.org/svn/gvSIG/branches/$1 -m "$2"
svn mkdir https://gvsig.org/svn/gvSIG/branches/$1/libraries -m "$2"
svn mkdir https://gvsig.org/svn/gvSIG/branches/$1/extensions -m "$2"

svn copy https://gvsig.org/svn/gvSIG/trunk/build https://gvsig.org/svn/gvSIG/branches/$1/ -m "$2"
svn copy https://gvsig.org/svn/gvSIG/trunk/binaries https://gvsig.org/svn/gvSIG/branches/$1/ -m "$2"

for i in $PROJECTS; do
	project=$(echo $i | awk -F/ '{ print $2; }')
	svn copy https://svn.forge.osor.eu/svn/gvsig-desktop/trunk/$i https://svn.forge.osor.eu/svn/gvsig-desktop/branches/$1/$i -m "$2"
done


