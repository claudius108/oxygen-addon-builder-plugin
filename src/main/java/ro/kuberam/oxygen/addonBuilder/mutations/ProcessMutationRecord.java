package ro.kuberam.oxygen.addonBuilder.mutations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;

import ro.kuberam.oxygen.addonBuilder.javafx.DialogModel;
import ro.kuberam.oxygen.addonBuilder.utils.IOUtilities;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.exml.workspace.api.editor.page.author.actions.AuthorActionsProvider;

@SuppressWarnings("unchecked")
public class ProcessMutationRecord {

	public static Map<String, String[]> observers;
	private static Map<String, ObserverConnection> connectObserverActions = new HashMap<String, ObserverConnection>();
	private static Map<String, String> nodeSelectors = new HashMap<String, String>();
	public static Map<String, DialogModel> dialogs = new HashMap<String, DialogModel>();

	static {
		try {
			observers = (Map<String, String[]>) IOUtilities.deserializeObjectFromFile("/observers.ser");
			connectObserverActions = (Map<String, ObserverConnection>) IOUtilities
					.deserializeObjectFromFile("/connectObserverActions.ser");
			nodeSelectors = (Map<String, String>) IOUtilities
					.deserializeObjectFromFile("/nodeSelectors.ser");
			dialogs = (Map<String, DialogModel>) IOUtilities.deserializeObjectFromFile("/dialogs.ser");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void attributes(AuthorActionsProvider authorActionsProvider, int offset,
			AuthorElement target, String attributeName, String attributeNamespace, String oldValue) {

		AttrValue newAttrValueObj = target.getAttribute(attributeName);
		String newAttrValue = newAttrValueObj.getValue();
		String targetName = target.getLocalName();

		ObserverConnection observerConnection = connectObserverActions.get("//" + targetName);

		if (observerConnection == null) {
			return;
		}

		String observerHandler = observerConnection.getObserverHandler();
		String[] actionHandlers = (String[]) observers.get(observerHandler);
		Map<String, Object> options = observerConnection.getOptions();
		List<String> attributeFilter = (List<String>) options.get("attributeFilter");

		if (attributeFilter.contains(attributeName)) {
			for (String actionHandler : actionHandlers) {
				javax.swing.AbstractAction action = (AbstractAction) authorActionsProvider
						.getAuthorExtensionActions().get(actionHandler);
				action.setEnabled(true);
				authorActionsProvider.invokeAuthorExtensionActionInContext(authorActionsProvider
						.getAuthorExtensionActions().get(actionHandler), offset);

			}
		}

	}

	public void characterData() {

	}

	public void childList() {

	}

}
