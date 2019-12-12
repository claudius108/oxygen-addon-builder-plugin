import java.util.Map;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.kuberam.oxygen.addonBuilder.mutations.ProcessMutationRecord;
import ro.sync.ecss.css.StaticContent;
import ro.sync.ecss.css.Styles;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorDocumentFilter;
import ro.sync.ecss.extensions.api.AuthorDocumentFilterBypass;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.content.OffsetInformation;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.page.author.actions.AuthorActionsProvider;
import ro.kuberam.oxygen.addonBuilder.Constants;

public class DocumentFilter extends AuthorDocumentFilter {

	private static final Logger logger = Logger.getLogger(DocumentFilter.class.getName());

	/**
	 * The author access.
	 */
	private final AuthorAccess authorAccess;
	private final AuthorEditorAccess authorEditorAccess;
	private AuthorDocumentController authorDocumentController;
	private AuthorActionsProvider authorActionsProvider;
	private Map<String, Object> authorExtensionActions;

	/**
	 * Constructor.
	 * 
	 * @param access The author access.
	 */
	public DocumentFilter(AuthorAccess access) {
		this.authorAccess = access;
		this.authorEditorAccess = authorAccess.getEditorAccess();
		this.authorDocumentController = access.getDocumentController();
		authorActionsProvider = authorEditorAccess.getActionsProvider();
		this.authorExtensionActions = authorActionsProvider.getAuthorExtensionActions();
	}

	@Override
	public boolean delete(AuthorDocumentFilterBypass filterBypass, int startOffset, int endOffset,
			boolean withBackspace) {
		if (withBackspace) {
			return true;
		} else {
			return filterBypass.delete(startOffset, endOffset, withBackspace);
		}
	}

	@Override
	public void setAttribute(AuthorDocumentFilterBypass filterBypass, String attributeName, AttrValue newAttrValueObj,
			AuthorElement currentElement) {

		filterBypass.setAttribute(attributeName, newAttrValueObj, currentElement);

		if (currentElement.getAttribute(attributeName).getValue().equals(Constants.valueOfAttributeToBeDeleted)) {
			filterBypass.removeAttribute(attributeName, currentElement);
		} else {
			ProcessMutationRecord.attributes(authorActionsProvider, currentElement.getStartOffset() + 1, currentElement,
					attributeName, null, null);
		}
	}

	@Override
	public void removeAttribute(AuthorDocumentFilterBypass filterBypass, String attributeName, AuthorElement element) {

		filterBypass.removeAttribute(attributeName, element);
		AttrValue attrValue = new AttrValue("");
		authorDocumentController.setAttribute(attributeName, attrValue, element);

	}

	@Override
	public void insertText(AuthorDocumentFilterBypass filterBypass, int offset, String textContent) {
		filterBypass.insertText(offset, textContent);
		int newOffset = offset + textContent.length();
		logger.debug("new offset = " + newOffset);

		authorEditorAccess.setCaretPosition(newOffset);
	}
}
