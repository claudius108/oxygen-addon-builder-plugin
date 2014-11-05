package ${frameworkId};

import java.util.Map;

import javax.swing.text.BadLocationException;

import ro.kuberam.oxygen.addonBuilder.mutations.ProcessMutationRecord;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorDocumentFilter;
import ro.sync.ecss.extensions.api.AuthorDocumentFilterBypass;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.content.OffsetInformation;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.AuthorParentNode;
import ro.sync.exml.workspace.api.editor.page.author.actions.AuthorActionsProvider;

public class DocumentFilter extends AuthorDocumentFilter {
	/**
	 * The author access.
	 */
	private final AuthorAccess authorAccess;
	private final AuthorEditorAccess authorEditorAccess;
	private static AuthorDocumentController authorDocumentController;
	private static AuthorActionsProvider authorActionsProvider;
	private static Map<String, Object> authorExtensionActions;

	/**
	 * Constructor.
	 * 
	 * @param access
	 *            The author access.
	 */
	public DocumentFilter(AuthorAccess access) {
		this.authorAccess = access;
		this.authorEditorAccess = authorAccess.getEditorAccess();
		DocumentFilter.setAuthorDocumentController(access.getDocumentController());
		authorActionsProvider = authorEditorAccess.getActionsProvider();
		DocumentFilter.setAuthorExtensionActions(authorActionsProvider.getAuthorExtensionActions());
	}

	@Override
	public boolean delete(AuthorDocumentFilterBypass filterBypass, int startOffset, int endOffset,
			boolean withBackspace) {

		AuthorNode currentNode = null;
		try {
			currentNode = getAuthorDocumentController().getNodeAtOffset(startOffset);
		} catch (BadLocationException e) {

			e.printStackTrace();
		}
		AuthorElement currentElement = (AuthorElement) currentNode;
		String currentElementName = currentElement.getLocalName();
		int caretPosition = startOffset;

		try {
			OffsetInformation ci = getAuthorDocumentController().getContentInformationAtOffset(startOffset);
			if (ci.getNodeForMarkerOffset() != null) {
				if (withBackspace && ci.getNodeForMarkerOffset().getStartOffset() == startOffset) {
					if (ci.getNodeForMarkerOffset().getStartOffset() + 1 == ci.getNodeForMarkerOffset()
							.getEndOffset()) {
						return true;
					}
				}

			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return filterBypass.delete(startOffset, endOffset, withBackspace);
	}

	@Override
	public void setAttribute(AuthorDocumentFilterBypass filterBypass, String attributeName,
			AttrValue newAttrValueObj, AuthorElement currentElement) {

		filterBypass.setAttribute(attributeName, newAttrValueObj, currentElement);

		ProcessMutationRecord.attributes(authorActionsProvider, currentElement.getStartOffset() + 1,
				currentElement, attributeName, null, null);

	}

	@Override
	public void insertText(AuthorDocumentFilterBypass filterBypass, int offset, String textContent) {
		AuthorNode currentNode = null;
		try {
			currentNode = getAuthorDocumentController().getNodeAtOffset(offset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		AuthorElement currentElement = (AuthorElement) currentNode;

		int caretPosition = currentElement.getStartOffset() + 1;

		filterBypass.insertText(offset, textContent);

		authorEditorAccess.setCaretPosition(caretPosition + 1);
	}

	@Override
	public void removeAttribute(AuthorDocumentFilterBypass filterBypass, String attributeName,
			AuthorElement element) {

		filterBypass.removeAttribute(attributeName, element);
		AttrValue attrValue = new AttrValue("");
		getAuthorDocumentController().setAttribute(attributeName, attrValue, element);

	}

	public static Map<String, Object> getAuthorExtensionActions() {
		return authorExtensionActions;
	}

	public static void setAuthorExtensionActions(Map<String, Object> authorExtensionActions) {
		DocumentFilter.authorExtensionActions = authorExtensionActions;
	}

	public static AuthorDocumentController getAuthorDocumentController() {
		return authorDocumentController;
	}

	public static void setAuthorDocumentController(AuthorDocumentController authorDocumentController) {
		DocumentFilter.authorDocumentController = authorDocumentController;
	}

}
