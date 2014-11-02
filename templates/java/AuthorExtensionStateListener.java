package addon-javapackage-name;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ro.kuberam.oxygen.addonBuilder.utils.IOUtilities;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.exml.workspace.api.editor.page.author.actions.AuthorActionsProvider;

@SuppressWarnings("unchecked")
public class AuthorExtensionStateListener implements
		ro.sync.ecss.extensions.api.AuthorExtensionStateListener {

	private AuthorAccess authorAccess;
	private static ArrayList<String> actionsByName = new ArrayList<String>();
	private static Map<String, ArrayList<String>> actionsByClass = new HashMap<String, ArrayList<String>>();

	static {
		try {
			actionsByName = (ArrayList<String>) IOUtilities.deserializeObjectFromFile("/actionsByName.ser");
			actionsByClass = (Map<String, ArrayList<String>>) IOUtilities
					.deserializeObjectFromFile("/actionsByClass.ser");

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return null;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorExtensionStateListener#activated(ro.sync.ecss.extensions.api.AuthorAccess)
	 */
	@Override
	public void activated(final AuthorAccess authorAccess) {

		this.setAuthorAccess(authorAccess);

		final AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();

		// Add document filter.
		authorAccess.getDocumentController().setDocumentFilter(new DocumentFilter(authorAccess));

		// invoke actions by name
		AuthorActionsProvider actionsProvider = authorEditorAccess.getActionsProvider();
		Map<String, Object> authorActions = actionsProvider.getAuthorCommonActions();
		authorActions.putAll(actionsProvider.getAuthorExtensionActions());

		for (String actionName : actionsByName) {
			Object action = authorActions.get(actionName);

			if (action != null) {
				actionsProvider.invokeAction(action);
			}
		}

		// invoke actions by class name
		for (String actionClass : actionsByClass.get("load")) {
			Class<?> c;
			try {
				c = Class.forName(actionClass);
				AuthorOperation o = (AuthorOperation) c.newInstance();
				o.doOperation(authorAccess, null);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (AuthorOperationException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void deactivated(AuthorAccess arg0) {
	}

	public AuthorAccess getAuthorAccess() {
		return authorAccess;
	}

	public void setAuthorAccess(AuthorAccess authorAccess) {
		this.authorAccess = authorAccess;
	}

}
