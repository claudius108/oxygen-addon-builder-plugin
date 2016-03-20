package ro.kuberam.oxygen.addonBuilder.operations;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorNode;

public class SetTextContent {

	public static void execute(AuthorDocumentController authorDocumentController, AuthorNode authorNode,
			String textContent) {

		authorDocumentController.beginCompoundEdit();
		try {
			if (authorNode.getStartOffset() + 1 < authorNode.getEndOffset()) {
				authorDocumentController.delete(authorNode.getStartOffset() + 1,
						authorNode.getEndOffset() - 1);
			}

			authorDocumentController.insertText(authorNode.getStartOffset() + 1, textContent);
		} finally {
			authorDocumentController.endCompoundEdit();
		}

	}

}
