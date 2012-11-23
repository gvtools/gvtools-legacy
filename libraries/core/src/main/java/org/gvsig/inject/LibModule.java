package org.gvsig.inject;

import org.gvsig.layer.SourceManager;
import org.gvsig.layer.impl.SourceManagerImpl;

import com.google.inject.AbstractModule;

public class LibModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SourceManager.class).to(SourceManagerImpl.class);
	}

}
