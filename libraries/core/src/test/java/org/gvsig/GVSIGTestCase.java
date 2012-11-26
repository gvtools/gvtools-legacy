package org.gvsig;

import org.gvsig.inject.LibModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

import junit.framework.TestCase;

public abstract class GVSIGTestCase extends TestCase {

	@Override
	protected void setUp() throws Exception {
		Injector injector = Guice.createInjector(new LibModule());
		injector.injectMembers(this);

		super.setUp();
	}
}
