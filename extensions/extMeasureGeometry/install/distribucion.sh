#!/bin/bash

# Si se produce un error, salimos inmediatamente
set -e

# Obtenemos el locale
LOC=`echo $LANG | sed 's/_.*//'`

if [ $# -ne "1" ]; then
  if [ $LOC = "es" ] ; then
    echo "Uso: distribucion.sh numero_de_build"
  else
    echo "Usage: distribucion.sh build_number"
  fi
  exit
fi

DIR_BASE=`pwd`

source variables.sh
# Get a version with format 1_0_alpha
UNDERSCORE_VERSION=`echo $FULLVERSION | sed 'y/./_/'`
# Get a version with format 10_alpha
BRIEFVERSION=`echo $FULLVERSION | sed 'y/./_/'`
# The name of the dir which will be ZIPed, containing the resulting installer
INSTALLER_DIR="$APPNAME"-$FULLVERSION\_installer
JARNAME="$APPNAME"-"$UNDERSCORE_VERSION".jar
# The extension targets on the this version of gvSIG:
GVSIG_VERSION=1.0_alpha

BUILDNUMBER="$1"

ZIPNAME="$APPNAME"-"$UNDERSCORE_VERSION"\-"$BUILDNUMBER".zip
#WINDOWSZIP="$APPNAME"-"$BRIEFVERSION"\_"$BUILDNUMBER"-windows-i586.zip
#LINUXZIP="$APPNAME"-"$BRIEFVERSION"\_"$BUILDNUMBER"-linux-i586.zip

#Directorios
#OUTPUT_DIR=/mnt/sercartlin/grupo-sig-ca/Testing/Versiones/v"$FULLVERSION"\_"$BUILDNUMBER"
OUTPUT_DIR="$TARGET_DIR"/v"$FULLVERSION"\_"$BUILDNUMBER"
PATH_SOURCE_EXT=../../_fwAndami/gvSIG/extensiones
PATH_SOURCE_MANDAT_EXT=../../../../_fwAndami/gvSIG/extensiones

echo -e "\n*****************"
echo "      BASE       "
echo "*****************"
#echo -n "$DIR_BASE" "-- "
rm bin -rf
mkdir -p bin/gvSIG/extensiones
#cp "$APPNAME"_installer/LEEME "$APPNAME"_installer/LLIG-ME "$APPNAME"_installer/README bin
#cp resources/gpl.txt bin


echo -e "\n*****************"
[ $LOC = "es" ] && echo "   EXTENSIONES   "
[ $LOC != "es" ] && echo "   EXTENSIONS   "
echo "*****************"

rm -rf extensiones
mkdir extensiones


i=0
while [ ! -z ${EXTENSIONS[$i]} ]
do
  [ $LOC = "es" ] && echo "Copiando "${EXTENSIONS[$i]}
  [ $LOC != "es" ] && echo "Copying "${EXTENSIONS[$i]}
  echo cp "$PATH_SOURCE_EXT"/${EXTENSIONS[$i]} extensiones -rf
  cp "$PATH_SOURCE_EXT"/${EXTENSIONS[$i]} extensiones -rf
  i=`expr $i + 1`
done

echo -e "\n********************"
echo " BUILDING INSTALLER "
echo "********************"
# Generar el instalador (jar) para windows
cd "$DIR_BASE"
echo ant -DJARNAME="$JARNAME" -DGVSIG_VERSION="$GVSIG_VERSION" -DAPPNAME="$APPNAME"
ant -DJARNAME="$JARNAME" -DGVSIG_VERSION="$GVSIG_VERSION" -DAPPNAME="$APPNAME"

echo -e "\n******************"
[ $LOC = "es" ] && echo " GENERAR DISTRIB "
[ $LOC != "es" ] && echo " GENERATE DISTRIB "
echo "******************"
# Generar el tar.gz para Linux y el ZIP para Windows

# V=`echo $MAJOR.$MINOR$POINT$REL`
# V1=`echo $MAJOR$SEP$MINOR$SEP$REL`

mkdir -p "$OUTPUT_DIR"

cd "$DIR_BASE"
rm -Rf "$INSTALLER_DIR"
cp -a installer_files "$INSTALLER_DIR"
rm -R "$INSTALLER_DIR"/CVS 2> /dev/null && true
# Set the correct version number in the scripts and files
if [ -f  "$INSTALLER_DIR"/install.sh ] ; then
  sed "s/FULLVERSION/$UNDERSCORE_VERSION/ ; s/APPNAME/$APPNAME/" "$INSTALLER_DIR"/install.sh > "$INSTALLER_DIR"/install.sh.bak
  mv "$INSTALLER_DIR"/install.sh.bak "$INSTALLER_DIR"/install.sh ;
  chmod +x "$INSTALLER_DIR"/install.sh
fi
if [ -f  "$INSTALLER_DIR"/install.bat ] ; then
  sed "s/FULLVERSION/$UNDERSCORE_VERSION/ ; s/APPNAME/$APPNAME/" "$INSTALLER_DIR"/install.bat > "$INSTALLER_DIR"/install.bat.bak
  mv "$INSTALLER_DIR"/install.bat.bak "$INSTALLER_DIR"/install.bat;
fi
mv "$JARNAME" "$INSTALLER_DIR"
zip -9r "$ZIPNAME" "$INSTALLER_DIR"
rm -Rf "$INSTALLER_DIR"

[ $LOC = "es" ] && echo " Copiando" "$ZIPNAME" "a" "$OUTPUT_DIR"
[ $LOC != "es" ] && echo " Copying" $ZIPNAME "to" "$OUTPUT_DIR"
cp "$ZIPNAME" "$OUTPUT_DIR"
 
