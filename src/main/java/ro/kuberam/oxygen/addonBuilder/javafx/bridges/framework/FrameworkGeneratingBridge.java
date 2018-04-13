package ro.kuberam.oxygen.addonBuilder.javafx.bridges.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

public class FrameworkGeneratingBridge extends BaseBridge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1583182879142664468L;
	public String oxygenInstallDir;
	private Pattern frameworkIdPattern = Pattern.compile("^[a-z]+(\\.[a-z][a-z0-9]*)*$");

	public FrameworkGeneratingBridge() {
	}

	public FrameworkGeneratingBridge(JavaFXDialog dialogWindow) {
		super(dialogWindow);
	}

	public void editFramework(String addonDirectory) {
		closeDialogWindow();

		logger.debug("action = 'editFramework'");

		Path addonDirectoryPath = Paths.get(addonDirectory);
		logger.debug("addonDirectoryPath = " + addonDirectoryPath);

		String frameworkId = addonDirectoryPath.getFileName().toString();
		logger.debug("frameworkId = " + frameworkId);

		Matcher extractTemplateIdPatternMatcher = frameworkIdPattern.matcher(frameworkId);
		if (!extractTemplateIdPatternMatcher.matches()) {
			JOptionPane.showMessageDialog(new JFrame(),
					"The framework name has to be conformant to Java package naming, namely\nit should contains only all-lowercase ASCII letters!\n"
							+ "The current name is: '" + frameworkId
							+ "'.\nPlease change the framework name or recreate it with a proper name.",
					"Error", JOptionPane.INFORMATION_MESSAGE);
			return;

		}

		Path xqueryFrameworkDescriptor = addonDirectoryPath.resolve("addon.xq");
		logger.debug("xqueryFrameworkDescriptor = " + xqueryFrameworkDescriptor);

		_generateFramework(addonDirectoryPath);

		try {
			pluginWorkspaceAccess.open(xqueryFrameworkDescriptor.toUri().toURL(), EditorPageConstants.PAGE_TEXT,
					"text/xquery");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}

	public void generateSelectedFramework(String addonDirectory) {
		closeDialogWindow();

		logger.debug("action = 'generateSelectedFramework'");

		Path addonDirectoryPath = Paths.get(addonDirectory);
		logger.debug("addonDirectoryPath = " + addonDirectoryPath);

		_generateFramework(addonDirectoryPath);

		JOptionPane.showMessageDialog(new JFrame(), "The framework was generated!", "Success",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void generateFramework() {
		logger.debug("action = 'generateFramework'");

		URL[] editorLocations = pluginWorkspaceAccess.getAllEditorLocations(PluginWorkspace.MAIN_EDITING_AREA);
		ArrayList<URL> eligibleEditorLocations = new ArrayList<URL>();

		for (URL editorLocation : editorLocations) {
			if (!editorLocation.getFile().contains("addon.xq")) {
				continue;
			}

			eligibleEditorLocations.add(editorLocation);
		}
		logger.debug("eligibleEditorLocations = " + eligibleEditorLocations);

		int eligibleEditorLocationsSize = eligibleEditorLocations.size();
		logger.debug("eligibleEditorLocationsSize = " + eligibleEditorLocationsSize);

		if (eligibleEditorLocationsSize > 1 || eligibleEditorLocationsSize == 0) {
			displayFrameworkSelectDialog("generateSelectedFramework");
			return;
		}

		Path xqueryFrameworkDescriptorPath = null;

		try {
			xqueryFrameworkDescriptorPath = Paths
					.get(URLDecoder.decode(eligibleEditorLocations.get(0).getPath(), "UTF-8"));
			logger.debug("xqueryFrameworkDescriptorUrl = " + xqueryFrameworkDescriptorPath);

			String scheme = xqueryFrameworkDescriptorPath.toUri().getScheme();
			logger.debug("scheme = " + scheme);

			if (scheme.equals("file")) {
				Path addonDirectoryPath = xqueryFrameworkDescriptorPath.getParent();
				logger.debug("addonDirectoryPath = " + addonDirectoryPath);

				// generate the framework jar
				_generateFramework(addonDirectoryPath);

				JOptionPane.showMessageDialog(new JFrame(), "The framework was generated!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(new JFrame(), "The protocol '" + scheme + "' is not supported!", "Error",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

		} catch (Exception e4) {
			e4.printStackTrace();
		}
	}

	public void _generateFramework(Path frameworkDirPath) {
		try {
			String pluginInstallDirPath = AddonBuilderPluginExtension.pluginInstallDir.toString();
			logger.debug("pluginInstallDirPath = " + pluginInstallDirPath);

			logger.debug("frameworkDirPath = " + frameworkDirPath);

			URI frameworkDirUri = URI.create("file://" + frameworkDirPath.toString());
			logger.debug("frameworkDirUri = " + frameworkDirUri);

			String frameworkId = frameworkDirPath.getFileName().toString();
			logger.debug("frameworkId = " + frameworkId);

			Map<String, String> xqueryExternalVariables = new HashMap<String, String>();
			xqueryExternalVariables.put("pluginInstallDirPath", pluginInstallDirPath);
			xqueryExternalVariables.put("frameworkDirPath", frameworkDirPath.toString());
			logger.debug("xqueryExternalVariables = " + xqueryExternalVariables);

			File frameworkDescriptor = frameworkDirPath.resolve(frameworkId + ".framework").toFile();
			logger.debug("frameworkDescriptor = " + frameworkDescriptor.getAbsolutePath());

			runAntBuildFile(frameworkDirPath.getParent(), frameworkId, "pre-build-framework-structure.xml");

			File generateFrameworkXQueryScript = Paths
					.get(pluginInstallDirPath, "generate-framework", "generate-framework.xql").toFile();
			logger.debug("generateFrameworkXQueryScript = " + generateFrameworkXQueryScript);

			XQueryOperation.query(new FileReader(frameworkDescriptor.getAbsolutePath()),
					new FileInputStream(generateFrameworkXQueryScript), true, frameworkDirUri, xqueryExternalVariables);

			File frameworkSpecificXQueryScript = frameworkDirPath
					.resolve(Paths.get("resources", "xquery", "framework-specific.xql")).toFile();
			logger.debug("frameworkSpecificXQueryScript = " + frameworkSpecificXQueryScript);

			if (frameworkSpecificXQueryScript.exists()) {
				XQueryOperation.query(new FileReader(frameworkDescriptor.getAbsolutePath()),
						new FileInputStream(frameworkSpecificXQueryScript), true, frameworkDirUri,
						xqueryExternalVariables);
			}

			runAntBuildFile(frameworkDirPath.getParent(), frameworkId, "build-framework-structure.xml");

			File frameworkDescriptorModifier = Paths
					.get(pluginInstallDirPath, "generate-framework", "framework-descriptor-modifier.xql").toFile();
			logger.debug("frameworkDescriptorModifier = " + frameworkDescriptorModifier);

			XQueryOperation.update(frameworkDescriptor, frameworkDescriptorModifier);

			if (pluginWorkspaceAccess != null) {
				pluginWorkspaceAccess.setGlobalObjectProperty("document.types.order", new DocumentTypeEntryPO[0]);

				pluginWorkspaceAccess.setGlobalObjectProperty("document.types.order", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void manageFramework(String addonDirectory) {
		closeDialogWindow();

		logger.debug("action = 'manageFramework'");

		Path addonDirectoryPath = Paths.get(addonDirectory);
		logger.debug("addonDirectoryPath = " + addonDirectoryPath);

		String frameworkId = addonDirectoryPath.getFileName().toString();
		logger.debug("frameworkId = " + frameworkId);

		try {

			String url = new URL("file:" + AddonBuilderPluginExtension.pluginInstallDir + "/" + "components" + "/"
					+ "manage-framework-dialog.html" + "?frameworkPath="
					+ AddonBuilderPluginExtension.oxygenFrameworksDir + "/" + frameworkId).toExternalForm();
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
			url = new URL("file:" + AddonBuilderPluginExtension.pluginInstallDir + "/" + "components" + "/"
					+ "select-framework-dialog.html" + "?action=" + action).toExternalForm();
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

	public String getframeworkNames(String filter) {
		return (new FileSystemBridge()).list(AddonBuilderPluginExtension.oxygenFrameworksDir, filter);
	}

	public String getExternalframeworkNames(String filter) {
		Path externalFrameworksDirPath = AddonBuilderPluginExtension.pluginInstallDir.resolve("../../frameworks");
		logger.debug("externalFrameworksDirPath = " + externalFrameworksDirPath);

		return (new FileSystemBridge()).list(externalFrameworksDirPath, filter);
	}

	private void runAntBuildFile(Path frameworksDir, String frameworkId, String buildFileName) {
		logger.debug("frameworksDir = " + frameworksDir);

		if (pluginWorkspaceAccess != null) {
			oxygenInstallDir = pluginWorkspaceAccess.getUtilAccess().expandEditorVariables("${oxygenInstallDir}", null);
		}
		logger.debug("oxygenInstallDir = " + oxygenInstallDir);

		String executableName = (File.separator.equals("/")) ? "bash" : "cmd";
		logger.debug("executableName = " + executableName);

		List<String> command = null;
		String executablePath = "";
		String filePath = "";

		switch (executableName) {
		case "bash":
			executablePath = Paths.get(oxygenInstallDir, "tools", "ant", "bin", "ant").toString();
			filePath = AddonBuilderPluginExtension.pluginInstallDir
					.resolve(Paths.get("generate-framework", buildFileName)).toString();
			command = Arrays.asList(executableName, executablePath, "-f", filePath, "build-framework",
					"-DoxygenAddonBuilder.frameworksDir=" + frameworksDir,
					"-DoxygenAddonBuilder.frameworkId=" + frameworkId,
					"-DoxygenAddonBuilder.oxygenInstallDir=" + oxygenInstallDir);
			break;
		case "cmd":
			executablePath = Paths.get("\"" + oxygenInstallDir, "tools", "ant", "bin", "ant" + "\"").toString();
			filePath = Paths.get("\"" + AddonBuilderPluginExtension.pluginInstallDir, "generate-framework",
					buildFileName + "\"").toString();

			command = Arrays.asList(executableName, "/c", executablePath, "-f", filePath, "build-framework",
					"-DoxygenAddonBuilder.frameworksDir=" + "\"" + frameworksDir + "\"",
					"-DoxygenAddonBuilder.frameworkId=" + frameworkId,
					"-DoxygenAddonBuilder.oxygenInstallDir=" + "\"" + oxygenInstallDir + "\"");
			break;
		}
		logger.debug("command = " + command.stream().collect(Collectors.joining(" ")));

		ProcessBuilder builder = new ProcessBuilder(command);

		Process process;
		try {
			process = builder.start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String outputMessage = br.lines().collect(Collectors.joining("\n"));
			if (outputMessage.contains("[java] Exception in thread \"main\"")) {
				JOptionPane.showMessageDialog(new JFrame(), outputMessage, "Error", JOptionPane.ERROR_MESSAGE);
			}

			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}
}
