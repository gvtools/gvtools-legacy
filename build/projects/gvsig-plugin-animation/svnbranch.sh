PROJECTS="libraries/libAnimation
libraries/libAnimationCommon
libraries/libAnimation2D
libraries/libAnimation3D
extensions/extAnimation2D
extensions/extAnimation3D
extensions/extAnimationCommon
extensions/extAnimationGUI"


if [ $# -ne 2 ]
then
  echo "Usage: `basename $0` {branch name} {branch message}"
  exit 65
fi

#svn mkdir https://gvsig.org/svn/gvSIG/branches/$1 -m "$2"
#svn mkdir https://gvsig.org/svn/gvSIG/branches/$1/libraries -m "$2"
#svn mkdir https://gvsig.org/svn/gvSIG/branches/$1/extensions -m "$2"

#svn copy https://gvsig.org/svn/gvSIG/trunk/build https://gvsig.org/svn/gvSIG/branches/$1/ -m "$2"
#svn copy https://gvsig.org/svn/gvSIG/trunk/binaries https://gvsig.org/svn/gvSIG/branches/$1/ -m "$2"

for i in $PROJECTS; do
	project=$(echo $i | awk -F/ '{ print $2; }')
	svn copy https://svn.forge.osor.eu/svn/gvsig-desktop/trunk/$i https://svn.forge.osor.eu/svn/gvsig-desktop/branches/$1/$i -m "$2"
done


