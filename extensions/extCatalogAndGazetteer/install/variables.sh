#!/bin/sh

export APPNAME=gvsig_CatalogAndGazetteer
export FULLVERSION='1_1_0-1005'
#export FULLVERSION='1.0'
export TARGET_DIR=/tmp/"$APPNAME"/versiones
#export TARGET_DIR=/mnt/sercartlin/grupo-sig-ca/Testing/Versiones/
export MAIN_INSTALL_PLUGIN=es.gva.cit.gvsig.catalogClient #Nombre del plugin que proporciona el 'build.nuber' para la distribucion
export GENERATE_SOURCES=N  # usar 'Y' para generar fuentes

## estas extensiones se muestran en los packs, y se puede elegir instalarlos o no
## (el fichero install.xml tambien debe estar actualizado para reflejar esto)
EXTENSIONS=(
"$MAIN_INSTALL_PLUGIN"
com.iver.cit.gvsig
)
