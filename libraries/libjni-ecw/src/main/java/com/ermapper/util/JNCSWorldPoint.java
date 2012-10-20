// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JNCSWorldPoint.java

package com.ermapper.util;

public class JNCSWorldPoint {

	public JNCSWorldPoint() {
		x = 0.0D;
		y = 0.0D;
		z = 0.0D;
	}

	public JNCSWorldPoint(double d, double d1) {
		x = d;
		y = d1;
	}

	public JNCSWorldPoint(double d, double d1, double d2) {
		x = d;
		y = d1;
		z = d2;
	}

	public double x;
	public double y;
	public double z;
}
