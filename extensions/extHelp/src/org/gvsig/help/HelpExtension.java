package org.gvsig.help;

import com.iver.andami.help.Help;
import com.iver.andami.plugins.Extension;

public class HelpExtension extends Extension {
	@Override
	public void execute(String actionCommand) {
		Help.show();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void initialize() {
		// do nothing
	}

	@Override
	public void postInitialize() {
		// do nothing
	}
}
