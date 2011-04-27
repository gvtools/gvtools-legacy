#!/bin/sh

VER=0.6.0
VERPROJ=0.5.0
INSTALL_PATH=./library_path

cp libcrsjniproj.so libproj.so.$VERPROJ $INSTALL_PATH

ln -s $INSTALL_PATH/libproj.so.$VERPROJ $INSTALL_PATH/libproj.so
ln -s $INSTALL_PATH/libproj.so.$VERPROJ $INSTALL_PATH/libproj.so.0