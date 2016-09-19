package ro.kuberam.oxygen.addonBuilder.parser;

import java.util.HashMap;
import java.util.Map;

public class OxyAction {

	private String id;
	private String name;
	private String description;
	private String icon;
	private String operation;
	private Map<String, String> arguments = new HashMap<String, String>();
	private String ID;

	public OxyAction() {
	}

	public void setArgument(String argumentName, String argumentValue) {
		arguments.put(argumentName, argumentValue);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String getName() {
		return (name != null) ? "name, \"" + name + "\"" : "";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private String getDescription() {
		return (description != null) ? ", description, \"" + description + "\"" : "";
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	private String getIcon() {
		return (icon != null) ? ", icon, \"" + icon + "\"" : "";
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	private String getOperation() {
		return (operation != null) ? ", operation, \"" + operation + "\"" : "";
	}

	private String getArguments() {
		StringBuilder result = new StringBuilder();
		String delim = "";

		for (Map.Entry<String, String> argument : arguments.entrySet()) {
			String argumentName = "arg-" + argument.getKey() + ", ";
			String argumentValue = "\"" + argument.getValue() + "\"";

			result.append(delim).append(argumentName).append(argumentValue);
			delim = ", ";
		}

		String arguments = result.toString();

		return (arguments != null) ? ", " + arguments : "";
	}

	private String getID() {
		return ID;
	}

	public String toLessDeclaration() {
		return "@" + getId() + ": " + "oxy_action(" + getName() + getDescription() + getIcon() + getOperation() + getArguments()
				+ ");";
	}
}
