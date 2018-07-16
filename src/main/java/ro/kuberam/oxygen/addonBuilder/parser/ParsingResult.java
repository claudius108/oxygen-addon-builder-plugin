package ro.kuberam.oxygen.addonBuilder.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import ro.kuberam.oxygen.addonBuilder.javafx.DialogModel;
import ro.kuberam.oxygen.addonBuilder.mutations.ObserverConnection;
import ro.kuberam.oxygen.addonBuilder.oxyFormControlDescriptors.OxyEditorDescriptor;
import ro.kuberam.oxygen.addonBuilder.utils.IOUtilities;

public class ParsingResult {

	public ParsingResult() {
		actionsByClass.put("load", new ArrayList<String>());
		actions.add("@charset \"utf-8\";");
	}

	public Map<String, String> templates = new HashMap<String, String>();
	public String attachedTemplates = "@charset \"utf-8\"; @import \"actions/actions.less\"; @import \"datalists/datalists.less\"; * {-oxy-display-tags: none;} ";
	public Map<String, Element> derivedActionElements = new HashMap<String, Element>();
	public Map<String, String[]> observers = new HashMap<String, String[]>();
	public Map<String, ObserverConnection> connectObserverActions = new HashMap<String, ObserverConnection>();
	public Map<String, String> nodeSelectors = new HashMap<String, String>();
	public ArrayList<String> actionsByName = new ArrayList<String>();
	public Map<String, ArrayList<String>> actionsByClass = new HashMap<String, ArrayList<String>>();
	public Map<String, String> variables = new HashMap<String, String>();
	public Map<String, DialogModel> dialogs = new HashMap<String, DialogModel>();
	public Map<String, OxyEditorDescriptor> checkboxes = new HashMap<String, OxyEditorDescriptor>();
	public String prolog = "";
	public ArrayList<Datalist> datalists = new ArrayList<Datalist>();
	public ArrayList<String> actions = new ArrayList<String>();
	private Charset utf8 = StandardCharsets.UTF_8;

	public void writeToFile(File javaDirectory, File addonDirectory) throws FileNotFoundException, IOException {
		Path cssResourcesDirectory = Paths.get(addonDirectory.getAbsolutePath(), "resources", "css");

		IOUtilities.serializeObjectToFile(javaDirectory, observers, "observers");
		IOUtilities.serializeObjectToFile(javaDirectory, connectObserverActions, "connectObserverActions");
		IOUtilities.serializeObjectToFile(javaDirectory, nodeSelectors, "nodeSelectors");
		IOUtilities.serializeObjectToFile(javaDirectory, actionsByName, "actionsByName");
		IOUtilities.serializeObjectToFile(javaDirectory, actionsByClass, "actionsByClass");
		IOUtilities.serializeObjectToFile(javaDirectory, templates, "templates");
		IOUtilities.serializeObjectToFile(javaDirectory, dialogs, "dialogs");
		IOUtilities.serializeObjectToFile(javaDirectory, prolog, "prolog");
		Files.write(cssResourcesDirectory.resolve("framework.less"), attachedTemplates.getBytes(utf8),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

		generateDatalists(cssResourcesDirectory);
		generateActions(cssResourcesDirectory);
	}

	private void generateActions(Path cssResourcesDirectory) {
		try {
			Path actionsDirectory = cssResourcesDirectory.resolve("actions");
			Utils.deleteDirectoryContent(actionsDirectory);
			Files.createDirectory(actionsDirectory);

			Files.write(actionsDirectory.resolve("actions.less"), actions, utf8, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generateDatalists(Path cssResourcesDirectory) {
		try {
			Path datalistsDirectory = cssResourcesDirectory.resolve("datalists");
			Utils.deleteDirectoryContent(datalistsDirectory);
			Files.createDirectory(datalistsDirectory);

			ArrayList<String> datalistImportStatements = new ArrayList<>();
			datalistImportStatements.add("@charset \"utf-8\";");

			for (Datalist datalist : datalists) {
				String datalistId = datalist.getId();

				datalistImportStatements.add("@import \"" + datalistId + ".less\";");

				ArrayList<String> lines = new ArrayList<>();
				lines.add("@charset \"utf-8\";");
				lines.add("@" + datalistId + "-labels: \"" + datalist.getLabels() + "\";");
				lines.add("@" + datalistId + "-values: \"" + datalist.getValues() + "\";");

				Files.write(datalistsDirectory.resolve(datalistId + ".less"), lines, utf8, StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING);
			}

			Files.write(datalistsDirectory.resolve("datalists.less"), datalistImportStatements, utf8,
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
