package com.iver.cit.gvsig.graphtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import junit.framework.TestCase;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 *         Working with arrays is 4 times faster (inserting) than hashsets, and
 *         consumes twice less memory
 */
public class TestArrayVsHashSet extends TestCase {

	private void runGC() throws Exception {
		// It helps to call Runtime.gc()
		// using several method calls:
		for (int r = 0; r < 4; ++r)
			_runGC();
	}

	private void _runGC() throws Exception {
		long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
		for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
			s_runtime.runFinalization();
			s_runtime.gc();
			Thread.currentThread().yield();

			usedMem2 = usedMem1;
			usedMem1 = usedMemory();
		}
	}

	private long usedMemory() {
		return s_runtime.totalMemory() - s_runtime.freeMemory();
	}

	private static final Runtime s_runtime = Runtime.getRuntime();

	private void test(Collection lst) {
		long t1 = System.currentTimeMillis();
		Random rnd = new Random();
		long mem1 = usedMemory();
		for (int i = 0; i < 1000000; i++) {
			Integer aux = rnd.nextInt();
			lst.add(aux);
		}
		long mem2 = usedMemory();
		long t2 = System.currentTimeMillis();

		System.out.println("load " + lst.getClass().getCanonicalName()
				+ " msecs: " + (t2 - t1) + " memUsed:" + ((mem2 - mem1) / 1024)
				+ "K");

	}

	public void testArray() {
		try {
			runGC();
			ArrayList array = new ArrayList();
			test(array);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void testHashSet() {
		try {
			runGC();

			HashSet<Integer> set = new HashSet<Integer>();
			test(set);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void setUp() throws Exception {

	}

}
