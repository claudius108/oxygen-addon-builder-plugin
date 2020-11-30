package ro.kuberam.oxygen.addonBuilder.parser;

public class SimpleAction {

	private static String id;
	private static String name;
	private static String templateId;

	public SimpleAction(String actionId, String actionName, String templateId) {
		setId(actionId);
		setName(actionName);
		setTemplateId(templateId);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		SimpleAction.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		SimpleAction.name = name;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		SimpleAction.templateId = templateId;
	}

}
