package ro.kuberam.oxygen.addonBuilder.actions;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.access.AuthorWorkspaceAccess;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.listeners.WSEditorListener;

public class KeepAuthorView implements AuthorOperation {

	@Override
	public String getDescription() {
		return "Keeps unchanged the author view.";
	}

	@Override
	public void doOperation(final AuthorAccess authorAccess, ArgumentsMap args)
			throws IllegalArgumentException, AuthorOperationException {
		final AuthorWorkspaceAccess authorWorkspaceAccess = authorAccess.getWorkspaceAccess();
		final AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();
		final WSEditor editorAccess = authorWorkspaceAccess.getEditorAccess(authorEditorAccess
				.getEditorLocation());

		editorAccess.addPageChangedListener(new WSEditorListener() {
			@Override
			public void editorPageChanged() {
				if (editorAccess.getCurrentPageID() == EditorPageConstants.PAGE_TEXT
						|| editorAccess.getCurrentPageID() == EditorPageConstants.PAGE_GRID) {
					editorAccess.changePage(EditorPageConstants.PAGE_AUTHOR);
				}
			}
		});

	}

	@Override
	public ArgumentDescriptor[] getArguments() {
		return null;
	}

}
