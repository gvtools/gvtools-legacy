#!/bin/sh
export INSTALL_PATH="$PWD"

if [ -n "$1"]
then
	cd "$INSTALL_PATH/libs"
else
    cd "$1"
fi

ln -sf libosgText.so.2.8.2 libosgText.so.55
ln -sf libosgManipulator.so.2.8.2 libosgManipulator.so.55
ln -sf libOpenThreads.so.11 libOpenThreads.so
ln -sf libosgDB.so.2.8.2 libosgDB.so.55
ln -sf libosgTerrain.so.2.8.2 libosgTerrain.so.55
ln -sf libosgShadow.so.55 libosgShadow.so
ln -sf libosgGA.so.2.8.2 libosgGA.so.55
ln -sf libosgFX.so.2.8.2 libosgFX.so.55
ln -sf libosgParticle.so.2.8.2 libosgParticle.so.55
ln -sf libosgAnimation.so.2.8.2 libosgAnimation.so.55
ln -sf libosg.so.55 libosg.so
ln -sf libosgUtil.so.55 libosgUtil.so
ln -sf libosgViewer.so.2.8.2 libosgViewer.so.55
ln -sf libosgDB.so.55 libosgDB.so
ln -sf libosgSim.so.55 libosgSim.so
ln -sf libosgSim.so.2.8.2 libosgSim.so.55
ln -sf libosgFX.so.55 libosgFX.so
ln -sf libOpenThreads.so.2.4.0 libOpenThreads.so.11
ln -sf libosgParticle.so.55 libosgParticle.so
ln -sf libosgTerrain.so.55 libosgTerrain.so
ln -sf libosgGA.so.55 libosgGA.so
ln -sf libosgManipulator.so.55 libosgManipulator.so
ln -sf libosgUtil.so.2.8.2 libosgUtil.so.55
ln -sf libosgShadow.so.2.8.2 libosgShadow.so.55
ln -sf libosgText.so.55 libosgText.so
ln -sf libosgViewer.so.55 libosgViewer.so
ln -sf libosgAnimation.so.55 libosgAnimation.so
ln -sf libosg.so.2.8.2 libosg.so.55
