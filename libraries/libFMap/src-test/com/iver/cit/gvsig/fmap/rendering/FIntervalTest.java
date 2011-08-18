package com.iver.cit.gvsig.fmap.rendering;

import junit.framework.TestCase;

public class FIntervalTest extends TestCase {

	private static final double DELTA = 0.000001;

	public void testNegativeIntervals() throws Exception {
		testNegativeInterval("-2.1--1.1", -2, -1.1);
		testNegativeInterval("-2.1-1.1", -2.1, 1.1);
		testNegativeInterval("2.1--1", 2.1, -1);
		testNegativeInterval("1.1-2", 1.1, 2);
	}

	private void testNegativeInterval(String intervalString, double min,
			double max) {
		FInterval interval = (FInterval) FInterval.create(intervalString);
		assertTrue(interval.getMin() - min < DELTA);
		assertTrue(interval.getMax() - max < DELTA);
	}
}
