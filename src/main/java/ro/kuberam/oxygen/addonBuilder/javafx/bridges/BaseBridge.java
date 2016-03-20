package ro.kuberam.oxygen.addonBuilder.javafx.bridges;

import java.io.Serializable;
import java.util.UUID;

import org.apache.log4j.Logger;

import javafx.scene.web.WebEngine;


import ro.kuberam.oxygen.addonBuilder.AddonBuilderPluginExtension;
import ro.kuberam.oxygen.addonBuilder.javafx.JavaFXDialog;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class BaseBridge implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1411422297600280660L;

	/**
	 * Logger for logging.
	 */
	protected static final Logger logger = Logger.getLogger(BaseBridge.class.getName());

	protected StandalonePluginWorkspace pluginWorkspaceAccess = AddonBuilderPluginExtension
			.getPluginWorkspaceAccess();
	public JavaFXDialog dialogWindow;
	public WebEngine webEngine;

	public BaseBridge() {
		this.dialogWindow = dialogWindow;
	}

	public BaseBridge(JavaFXDialog dialogWindow) {

	}

	public void closeDialogWindow() {
		dialogWindow.setVisible(false);
	}

	public void reload() {
		webEngine.reload();
	}

	public void setDialogWindow(JavaFXDialog dialogWindow) {
		this.dialogWindow = dialogWindow;
	}

	public void log(String message) {
		logger.info(message);
	}

	public String uuid() {
		return "id" + UUID.randomUUID().toString().replaceAll("-", "");
	}
}
