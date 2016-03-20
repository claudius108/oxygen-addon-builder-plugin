package ro.kuberam.oxygen.addonBuilder.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;

import ro.kuberam.oxygen.addonBuilder.javafx.DialogModel;
import ro.kuberam.oxygen.addonBuilder.mutations.ObserverConnection;
import ro.kuberam.oxygen.addonBuilder.utils.IOUtilities;

public class ParsingResult {

	public ParsingResult() {
		actionsByClass.put("load", new ArrayList<String>());
	}

	public Map<String, String> templates = new HashMap<String, String>();
	public String attachedTemplates = "@charset \"utf-8\";  @import \"actions.less\"; @import \"datalists.less\"; * {-oxy-display-tags: none;} ";
	public Map<String, Element> derivedActionElements = new HashMap<String, Element>();
	public Map<String, String[]> observers = new HashMap<String, String[]>();
	public Map<String, ObserverConnection> connectObserverActions = new HashMap<String, ObserverConnection>();
	public Map<String, String> nodeSelectors = new HashMap<String, String>();
	public ArrayList<String> actionsByName = new ArrayList<String>();
	public Map<String, ArrayList<String>> actionsByClass = new HashMap<String, ArrayList<String>>();
	public Map<String, String> variables = new HashMap<String, String>();
	public Map<String, DialogModel> dialogs = new HashMap<String, DialogModel>();
	public String prolog = "";
	public Map<String, String> datalists = new HashMap<String, String>();
	public ArrayList<String> actions = new ArrayList<String>();

	public void writeToFile(File javaDirectory, File addonDirectory) throws FileNotFoundException,
			IOException {
		Path cssResourcesDirectory = Paths.get(addonDirectory.getAbsolutePath(), "resources", "css");

		IOUtilities.serializeObjectToFile(javaDirectory, observers, "observers");
		IOUtilities.serializeObjectToFile(javaDirectory, connectObserverActions, "connectObserverActions");
		IOUtilities.serializeObjectToFile(javaDirectory, nodeSelectors, "nodeSelectors");
		IOUtilities.serializeObjectToFile(javaDirectory, actionsByName, "actionsByName");
		IOUtilities.serializeObjectToFile(javaDirectory, actionsByClass, "actionsByClass");
		IOUtilities.serializeObjectToFile(javaDirectory, templates, "templates");
		IOUtilities.serializeObjectToFile(javaDirectory, dialogs, "dialogs");
		IOUtilities.serializeObjectToFile(javaDirectory, prolog, "prolog");
		// TODO: when Oxygen will use commons-io >= 2.1, the function
		// writeStringToFile(File file, String data, boolean append) will be
		// used instead of the following one
		FileUtils.writeStringToFile(new File(cssResourcesDirectory + File.separator
				+ "framework.less"), attachedTemplates);

		// processing the datalists
		FileUtils.writeStringToFile(new File(cssResourcesDirectory + File.separator + "datalists.less"),
				Utils.generateDatalistImportStatements(datalists));

		for (Map.Entry<String, String> datalist : datalists.entrySet()) {
			String datalistId = datalist.getKey();
			String content = "@charset \"utf-8\"; @" + datalistId + ": \"" + datalist.getValue() + "\";";

			FileUtils.writeStringToFile(new File(cssResourcesDirectory + File.separator + datalistId
					+ ".less"), content);
		}

		String actionsFileContent = "@charset \"utf-8\"; "
				+ actions.stream().collect(Collectors.joining(" "));
		FileUtils.writeStringToFile(new File(cssResourcesDirectory + File.separator + "actions.less"),
				actionsFileContent);
	}
}
