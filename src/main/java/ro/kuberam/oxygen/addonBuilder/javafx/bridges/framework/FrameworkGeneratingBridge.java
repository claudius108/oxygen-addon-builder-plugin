package ro.kuberam.oxygen.addonBuilder.javafx.bridges.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ro.kuberam.oxygen.addonBuilder.AddonBuilderPluginExtension;
import ro.kuberam.oxygen.addonBuilder.javafx.DialogModel;
import ro.kuberam.oxygen.addonBuilder.javafx.JavaFXDialog;
import ro.kuberam.oxygen.addonBuilder.javafx.bridges.BaseBridge;
import ro.kuberam.oxygen.addonBuilder.javafx.bridges.filesystem.FileSystemBridge;
import ro.kuberam.oxygen.addonBuilder.operations.XQueryOperation;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.editor.persistance.DocumentTypeEntryPO;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;

public class FrameworkGeneratingBridge extends BaseBridge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1583182879142664468L;
	public String frameworkId;
	public String oxygenInstallDir;
	private Pattern frameworkIdPattern = Pattern.compile("^[a-z]+(\\.[a-z][a-z0-9]*)*$");

	public FrameworkGeneratingBridge() {
	}

	public FrameworkGeneratingBridge(JavaFXDialog dialogWindow) {
		super(dialogWindow);
	}

	public void editFramework(String frameworkIdArg) {
		closeDialogWindow();

		frameworkId = frameworkIdArg;

		File addonDirectory = new File(AddonBuilderPluginExtension.frameworksDir + File.separator + frameworkId);
		logger.debug("addonDirectory = " + addonDirectory);
		
		Matcher extractTemplateIdPatternMatcher = frameworkIdPattern.matcher(frameworkId);
		if (!extractTemplateIdPatternMatcher.matches()) {
			JOptionPane.showMessageDialog(new JFrame(),
					"The framework name has to be conformant to Java package naming, namely\nit should contains only all-lowercase ASCII letters!\n"
							+ "The current name is: '" + frameworkId
							+ "'.\nPlease change the framework name or recreate it with a proper name.",
					"Error", JOptionPane.INFORMATION_MESSAGE);
			return;

		}

		File xqueryFrameworkDescriptor = new File(addonDirectory + File.separator + "addon.xq");
		logger.debug("xqueryFrameworkDescriptor = " + xqueryFrameworkDescriptor);

		_generateFramework(addonDirectory);

		try {
			pluginWorkspaceAccess.open(xqueryFrameworkDescriptor.toURI().toURL(), EditorPageConstants.PAGE_TEXT,
					"text/xquery");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}

	public void generateFramework() {
		WSEditor wseditor = pluginWorkspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
		logger.debug("wseditor = " + wseditor);

		if (wseditor == null) {
			JOptionPane.showMessageDialog(new JFrame(), "No XQuery script for addon configuration is opened!", "Error",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		URL xqueryFrameworkDescriptorUrl = null;

		try {
			xqueryFrameworkDescriptorUrl = new URL(URLDecoder.decode(wseditor.getEditorLocation().toURI().toASCIIString(), "UTF-8"));
			logger.debug("xqueryFrameworkDescriptorUrl = " + xqueryFrameworkDescriptorUrl);

			String protocol = xqueryFrameworkDescriptorUrl.getProtocol();
			logger.debug("protocol = " + protocol);

			if (protocol.equals("file")) {
				File xqueryFrameworkDescriptor = new File(xqueryFrameworkDescriptorUrl.getFile());
				logger.debug("xqueryFrameworkDescriptor = " + xqueryFrameworkDescriptor);

				File addonDirectory = new File(xqueryFrameworkDescriptor.getParent());
				logger.debug("addonDirectory = " + addonDirectory);

				frameworkId = addonDirectory.getName();
				logger.debug("frameworkId = " + frameworkId);

				// generate the framework jar
				_generateFramework(addonDirectory);

				JOptionPane.showMessageDialog(new JFrame(), "The framework was generated!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(new JFrame(), "The protocol '" + protocol + "' is not supported!",
						"Error", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

		} catch (Exception e4) {
			e4.printStackTrace();
		}
	}

	public void _generateFramework(File addonDirectory) {
		logger.debug("addonDirectory = " + addonDirectory.getAbsolutePath());

		runAntBuildFile(addonDirectory.getParentFile(), frameworkId);

		File frameworkDescriptor = new File(addonDirectory + File.separator + frameworkId + ".framework");
		logger.debug("frameworkDescriptor = " + frameworkDescriptor);

		File frameworkDescriptorModifier = new File(AddonBuilderPluginExtension.pluginInstallDir + File.separator
				+ "generate-framework" + File.separator + "framework-descriptor-modifier.xq");
		logger.debug(frameworkDescriptorModifier);

		XQueryOperation.update(frameworkDescriptor, frameworkDescriptorModifier);

		if (pluginWorkspaceAccess != null) {
			pluginWorkspaceAccess.setGlobalObjectProperty("document.types.order", new DocumentTypeEntryPO[0]);

			pluginWorkspaceAccess.setGlobalObjectProperty("document.types.order", null);
		}
	}

	public void manageFramework(String frameworkIdArg) {
		closeDialogWindow();

		frameworkId = frameworkIdArg;
		logger.debug("frameworkId in manageFramework() = " + frameworkId);

		try {

			String url = new URL("file:" + AddonBuilderPluginExtension.pluginInstallDir + File.separator + "components"
					+ File.separator + "manage-framework-dialog.html" + "?frameworkPath="
					+ AddonBuilderPluginExtension.frameworksDir + File.separator + frameworkIdArg).toExternalForm();
			logger.debug("url = " + url);

			final DialogModel dialogModel = new DialogModel("manage-framework-dialog", "modeless", "Manage framework",
					500, 900, "both", new String[] { "auto", "0", "auto", "auto" }, url, "OxygenAddonBuilder",
					new FileSystemBridge(dialogWindow), "");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new JavaFXDialog(AddonBuilderPluginExtension.parentFrame, dialogModel);
				}
			});

		} catch (Exception e4) {
			e4.printStackTrace();
		}
	}

	public void displayFrameworkSelectDialog(String action) {

		logger.debug("action = " + action);

		String url = null;
		try {
			url = new URL("file:" + AddonBuilderPluginExtension.pluginInstallDir + File.separator + "components"
					+ File.separator + "select-framework-dialog.html" + "?action=" + action).toExternalForm();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		logger.debug("url = " + url);

		final DialogModel dialogModel = new DialogModel("select-framework-dialog", "modal", "Select addon's directory",
				700, 400, "both", new String[] { "auto" }, url, "OxygenAddonBuilder",
				new FrameworkGeneratingBridge(dialogWindow), "");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new JavaFXDialog(AddonBuilderPluginExtension.parentFrame, dialogModel);
			}
		});
	}

	public String getframeworksNames(String filter) {
		return (new FileSystemBridge()).list(AddonBuilderPluginExtension.frameworksDir.getAbsolutePath(), filter);
	}

	private void runAntBuildFile(File frameworksDir, String frameworkId) {
		logger.debug("frameworksDir = " + frameworksDir.getAbsolutePath());

		if (pluginWorkspaceAccess != null) {
			oxygenInstallDir = pluginWorkspaceAccess.getUtilAccess().expandEditorVariables("${oxygenInstallDir}", null);
		}
		logger.debug("oxygenInstallDir = " + oxygenInstallDir);

		String executableName = (File.separator.equals("/")) ? "bash" : "cmd";
		logger.debug("executableName = " + executableName);

		List<String> command = null;

		switch (executableName) {
		case "bash":
			command = Arrays.asList(executableName,
					oxygenInstallDir + File.separator + "tools" + File.separator + "ant" + File.separator + "bin"
							+ File.separator + "ant",
					"-f",
					AddonBuilderPluginExtension.pluginInstallDir + File.separator + "generate-framework"
							+ File.separator + "build-framework-structure.xml",
					"build-framework", "-DoxygenAddonBuilder.frameworksDir=" + frameworksDir,
					"-DoxygenAddonBuilder.frameworkId=" + frameworkId,
					"-DoxygenAddonBuilder.pluginInstallDir=" + AddonBuilderPluginExtension.pluginInstallDir);
			break;
		case "cmd":
			String executablePath = "\"" + oxygenInstallDir + File.separator + "tools" + File.separator + "ant"
					+ File.separator + "bin" + File.separator + "ant" + "\"";
			String filePath = "\"" + AddonBuilderPluginExtension.pluginInstallDir + File.separator
					+ "generate-framework" + File.separator + "build-framework-structure.xml" + "\"";

			command = Arrays.asList(executableName, "/c", executablePath, "-f", filePath,
					"build-framework",
					"-DoxygenAddonBuilder.frameworksDir=" + "\"" + frameworksDir.getAbsolutePath() + "\"",
					"-DoxygenAddonBuilder.frameworkId=" + frameworkId, "-DoxygenAddonBuilder.pluginInstallDir=" + "\""
							+ AddonBuilderPluginExtension.pluginInstallDir + "\"");
			break;
		}

		logger.debug("command = " + String.join(" ", command));

		ProcessBuilder builder = new ProcessBuilder(command);

		Process process;
		try {
			process = builder.start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
