import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import ro.kuberam.oxygen.addonBuilder.utils.IOUtilities;
import ro.sync.ecss.css.EditorContent;
import ro.sync.ecss.css.StaticContent;
import ro.sync.ecss.css.Styles;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorCaretEvent;
import ro.sync.ecss.extensions.api.AuthorCaretListener;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.page.author.actions.AuthorActionsProvider;

@SuppressWarnings("unchecked")
public class AuthorExtensionStateListener implements ro.sync.ecss.extensions.api.AuthorExtensionStateListener {

	private static final Logger logger = Logger.getLogger(AuthorExtensionStateListener.class.getName());

	private AuthorAccess authorAccess;
	private static ArrayList<String> actionsByName = new ArrayList<String>();
	private static Map<String, ArrayList<String>> actionsByClass = new HashMap<String, ArrayList<String>>();
	private static Properties scripts = new Properties();

	// loading the serialized objects
	static {
		try {
			actionsByName = (ArrayList<String>) IOUtilities.deserializeObjectFromFile("/actionsByName.ser");
			actionsByClass = (Map<String, ArrayList<String>>) IOUtilities
					.deserializeObjectFromFile("/actionsByClass.ser");

			InputStream scriptsIs = AuthorExtensionStateListener.class.getResourceAsStream("/scripts.xml");
			scripts.loadFromXML(scriptsIs);
			scriptsIs.close();
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
		final AuthorDocumentController authorDocumentController = authorAccess.getDocumentController();

		// Add document filter.
		authorDocumentController.setDocumentFilter(new DocumentFilter(authorAccess));

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

		JPanel authorComponent = (JPanel) authorEditorAccess.getAuthorComponent();
		authorComponent.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent event) {
			}

			@Override
			public void focusLost(FocusEvent event) {
				Component formControl = event.getOppositeComponent();
				boolean isFormControl = detectFormControl(authorComponent, formControl, false);

				if (isFormControl && formControl instanceof JTextComponent) {
					AuthorNode currentNode = null;
					try {
						currentNode = authorDocumentController.getNodeAtOffset(authorEditorAccess.getCaretOffset());
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					logger.debug("currentNode = " + currentNode);

					AttrValue xmlIdAttrValue = ((AuthorElement) currentNode).getAttribute("xml:lang");
					logger.debug("xmlIdAttrValue = " + xmlIdAttrValue);

					if (xmlIdAttrValue == null) {
						return;
					}

					String lang = xmlIdAttrValue.getValue();
					logger.debug("lang = " + lang);

					PluginWorkspaceProvider.getPluginWorkspace().setGlobalObjectProperty("recently.used.characters",
							scripts.get(lang));
				}
			}
		});
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

	private boolean detectFormControl(JPanel authorComponent, Component formControl, boolean isFormControl) {
		Component formControlParent = formControl.getParent();

		if (formControlParent.equals(authorComponent)) {
			isFormControl = true;
		} else {
			detectFormControl(authorComponent, formControlParent, false);
		}

		return isFormControl;
	}

}
