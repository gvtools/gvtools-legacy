#!/bin/sh

export APPNAME=measure_geometry
#export FULLVERSION='0.1_alpha'
export FULLVERSION='0.1'
export TARGET_DIR=/tmp/"$APPNAME"/versiones

## estas extensiones se muestran en los packs, y se puede elegir instalarlos o no
## (el fichero install.xml también debe estar actualizado para reflejar esto)
EXTENSIONS=(
com.iver.gvsig.extMeasureGeometry
)

