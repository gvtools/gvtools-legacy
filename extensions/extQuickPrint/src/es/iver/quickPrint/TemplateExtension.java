package es.iver.quickPrint;

import java.io.File;
import java.io.IOException;

import com.iver.andami.Launcher;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.utiles.FileUtils;

public class TemplateExtension extends Extension {
	private static final String templatesDirName = "templates";
	protected static final String templatesDir = Launcher.getAppHomeDir()
			+ File.separator + templatesDirName;

	public void initialize() {
		checkTemplates();
	}

	/**
	 * <p>
	 * Templates are loaded from $HOME/gvSIG/printTemplates, so that the user
	 * can modify them. When we load the extension, we want to ensure the
	 * Templates are in the right place. If they are not, we copy them from the
	 * plugin Dir to $HOME/gvSIG/printTemplates
	 * <p>
	 * .
	 * 
	 * <p>
	 * If the 'checkTemplates' property is set to 'false' in the
	 * plugin-persistence file, then this check is skept and templates are not
	 * copied.
	 * </p>
	 */
	private void checkTemplates() {
		PluginServices ps = PluginServices.getPluginServices(this);
		if (ps.getPersistentXML().contains("checkTemplates")) {
			if (!ps.getPersistentXML().getBooleanProperty("ckeckTemplates")) {
				// if property checkTemplate == false, just return
				return;
			}
		}

		new File(this.templatesDir).mkdirs();
		File srcTemplatesDir = new File(ps.getPluginDirectory()
				.getAbsolutePath() + File.separator + templatesDirName);

		String[] templates = srcTemplatesDir.list();
		for (int i = 0; i < templates.length; i++) {
			File template = new File(templatesDir + File.separator
					+ templates[i]);
			if (!template.exists()) {
				try {
					FileUtils.copy(new File(srcTemplatesDir + File.separator
							+ templates[i]), template);
				} catch (IOException e) {
					ps.getLogger().error("Error copying templates", e);
				}
			}
		}
	}

	public void execute(String actionCommand) {
		IView view = (IView) PluginServices.getMDIManager().getActiveWindow();
		ModelTemplatePanel mtp = new ModelTemplatePanel(view);
		SelectTemplatePanel stp = new SelectTemplatePanel(mtp);
		PluginServices.getMDIManager().addCentredWindow(stp);

	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		return window instanceof IView;
	}

}
