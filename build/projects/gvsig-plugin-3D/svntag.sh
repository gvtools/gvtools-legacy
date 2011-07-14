PROJECTS=$(grep "\<module\>" pom.xml | awk -F"\<module\>" '{print $2}' | awk -F"\<\/module\>" '{print $1}')
URL=https://svn.forge.osor.eu/svn/gvsig-desktop/branches/gvSIG_19_ext3D_osgVP_2_2_0
URL_TAGS=https://svn.forge.osor.eu/svn/gvsig-desktop/tags

if [ $# -ne 2 ]
then
  echo "Usage: `basename $0` {tag name} {tag message}"
  echo "Example: `basename $0` gvSIG_3D_Animation_1_0_SNAPSHOT_build_9 \"Tag for gvsig 3D and animation extensions version 1.0 SNAPSHOT build 9\""
  exit 65
fi

svn mkdir $URL_TAGS/$1 -m "$2"
svn mkdir $URL_TAGS/$1/libraries -m "$2"
svn mkdir $URL_TAGS/$1/extensions -m "$2"

svn copy $URL/build $URL_TAGS/$1/ -m "$2"
svn copy $URL/binaries $URL_TAGS/$1/ -m "$2"

for i in $PROJECTS; do
	project=$(echo $i | sed 's/.*\/ext/extensions\/ext/g' |  sed 's/.*\/lib/libraries\/lib/g' | sed 's/.*\/_fw/frameworks\/_fw/g' | sed 's/.*\/app/applications\/app/g';)
	echo $project
	svn copy $URL/$project $URL_TAGS/$1/$project -m "$2"
done


