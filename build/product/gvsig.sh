#!/bin/sh
[ -x /usr/bin/dirname ] && cd `dirname $0`
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:"../../binaries/linux/"
export DYLD_LIBRARY_PATH=$DYLD_LIBRARY_PATH:"../../binaries/mac/"
#java  -cp _fwAndami-2.1-SNAPSHOT.jar:lib/castor-gvsig.jar:lib/commons-codec-gvsig.jar:lib/commons-collections-gvsig.jar:lib/commons-dbcp-gvsig.jar:lib/commons-pool-gvsig.jar:lib/crimson-gvsig.jar:lib/javaws-gvsig.jar:lib/jcalendar-gvsig.jar:lib/jcommon-gvsig.jar:lib/ jfreechart-gvsig.jar:lib/jh-gvsig.jar:lib/JUF-gvsig.jar:lib/junit-3.8.1.jar:lib/JWizardComponent-gvsig.jar:lib/kxml2-gvsig.jar:lib/libExceptions-2.1-SNAPSHOT.jar:lib/libInternationalization-2.1-SNAPSHOT.jar:lib/libIverUtiles-2.1-SNAPSHOT.jar:lib/libUIComponent-2.1-SNAPSHOT.jar:lib/log4j-1.2.8.jar:lib/looks-gvsig.jar:lib/tempFileManager-gvsig.jar:lib/xerces-gvsig.jar:lib/xml-apis-gvsig.jar:lib/xmlrpc-gvsig.jar: -Xmx500M com.iver.andami.Launcher gvSIG gvSIG/extensiones "$@"

for i in ./lib/*.jar ; do
  LIBRARIES=$LIBRARIES:"$i"
done
for i in ./lib/*.zip ; do
  LIBRARIES=$LIBRARIES:"$i"
done

$HOME/gvSIG/jre/1.5.0_12/bin/java -Djava.library.path=/usr/lib:"../../binaries/linux/:../../binaries/mac/:../../binaries/w32" -cp _fwAndami-1.9-SNAPSHOT.jar$LIBRARIES -Xmx500M com.iver.andami.Launcher gvSIG gvSIG/extensiones "$@"

