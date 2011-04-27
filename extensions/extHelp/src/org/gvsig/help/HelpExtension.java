package org.gvsig.help;

import java.io.File;

import org.apache.log4j.Logger;

import com.iver.andami.PluginServices;
import com.iver.andami.help.Help;
import com.iver.andami.plugins.Extension;

public class HelpExtension  extends Extension {


	private Logger log() {
		return Logger.getLogger("org.gvsig");
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public void execute(String actionCommand) {

		// If the option pressed is help control the help panel is created.
		if(actionCommand.equalsIgnoreCase("Help")){

			Help help = Help.getHelp();	//My constructor.
			help.show();//Launch help panel.

			return;
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return true;
	}

	public static String getExtensionPath() {
		String pluginName = "org.gvsig.help";
		PluginServices ps = PluginServices.getPluginServices(pluginName);
		return ps.getPluginDirectory().getAbsolutePath();
	}

	@Override
	public void postInitialize() {
		super.postInitialize();
		Help help = Help.getHelp();	//My constructor.
		File folder = new File(HelpExtension.getExtensionPath()+File.separator+"manuals");
		String[] l = folder.list();
		for (int i = 0; i < l.length; i++) {
			File file = new File(folder,l[i]);
			String path = file.getAbsolutePath();
			String name = l[i];
			if (file.isDirectory()){
				path = folder.getAbsolutePath();
			} else {
				if ( !path.toLowerCase().endsWith(".zip") ){
					continue;
				}
			}
			if (name.endsWith(File.separator)){
				name = name.substring(0, name.length()-1);
			}
			if (name.toLowerCase().endsWith(".zip")){
				name = name.substring(0, name.length()-4);
			}
			help.addResource(path);
			help.addHelp(name);
		}
	}

}
