#!/bin/bash

PROJECTS=$(grep "\<module\>" pom.xml | awk -F"\<module\>" '{print $2}' | awk -F"\<\/module\>" '{print $1}')
URL=https://svn.forge.osor.eu/svn/gvsig-desktop/trunk

for i in $PROJECTS; do
	project=$(echo $i | sed 's/.*\/ext/extensions\/ext/g' |  sed 's/.*\/lib/libraries\/lib/g' |  sed 's/.*\/open/libraries\/open/g' | sed 's/.*\/_fw/frameworks\/_fw/g' | sed 's/.*\/app/applications\/app/g';)
	echo $project
	svn co $URL/$project $i
done


