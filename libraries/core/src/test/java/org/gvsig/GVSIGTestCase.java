package org.gvsig;

import junit.framework.TestCase;

import org.gvsig.inject.LibModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public abstract class GVSIGTestCase extends TestCase {

	@Override
	protected void setUp() throws Exception {
		Injector injector = Guice.createInjector(new LibModule());
		injector.injectMembers(this);

		super.setUp();
	}
}
