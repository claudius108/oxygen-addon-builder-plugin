package ro.kuberam.oxygen.addonBuilder.operations;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.OptionsStorage;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.listeners.WSEditorListener;
import ro.sync.util.URLUtil;

public class OpenDocumentFragmentInNewTabOperation implements AuthorOperation {

	/**
	 * This operation is designated to allow editing of XML fragment of a main
	 * document in new editors, opened in new tabs. When the data in the new
	 * editors is saved, the main document is updated accordingly.
	 */

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(OpenDocumentFragmentInNewTabOperation.class.getName());

	private static String xmlPI = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static Charset utf8 = StandardCharsets.UTF_8;

	/**
	 * Constructor.
	 */
	public OpenDocumentFragmentInNewTabOperation() {
	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess,
	 *      ArgumentsMap)
	 */
	public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
		logger.debug("================ start OpenFileInNewTabOperation ========================");

		AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();
		AuthorDocumentController authorDocumentController = authorAccess.getDocumentController();
		final OptionsStorage optionsStorage = authorAccess.getOptionsStorage();

		final URL openerLocation = authorEditorAccess.getEditorLocation();
		logger.debug("openerLocation = " + openerLocation);
		String openerFileName = URLUtil.extractFileName(openerLocation.toString());
		logger.debug("openerFileName = " + openerFileName);

		try {
			Path openedFile = Files.createTempFile(openerFileName + "#", ".xml");
			logger.debug("openedFile = " + openedFile);

			final Path openedFileName = openedFile.getFileName();
			logger.debug("openedFileName = " + openedFileName);
			final URL openedFileUrl = openedFile.toUri().toURL();
			logger.debug("openedFileUrl = " + openedFileUrl);

			int caretOffset = authorEditorAccess.getCaretOffset();
			logger.debug("caretOffset = " + caretOffset);
			AuthorNode currentNode = authorDocumentController.getNodeAtOffset(caretOffset);
			logger.debug("currentNode = " + currentNode);
			String openedXpathExpr = authorDocumentController.getXPathExpression(caretOffset);
			logger.debug("openedXpathExpr = " + openedXpathExpr);

			AuthorDocumentFragment currentNodeAsDocumentFragment = authorDocumentController
					.createDocumentFragment(currentNode, true);
			String currentNodeAsString = authorDocumentController.serializeFragmentToXML(currentNodeAsDocumentFragment);
			logger.debug("currentNodeAsString = " + currentNodeAsString);

			authorDocumentController.beginCompoundEdit();

			Files.write(openedFile, (xmlPI + currentNodeAsString).getBytes(utf8), StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);

			final PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
			optionsStorage.setOption(openedFileName + " xpath", openedXpathExpr);
			logger.debug(
					"openedXpathExpr in optionsStorage = " + optionsStorage.getOption(openedFileName + " xpath", ""));

			Thread openThread = new Thread(new Runnable() {
				@Override
				public void run() {

					pluginWorkspace.open(openedFileUrl, EditorPageConstants.PAGE_AUTHOR, "text/xml");

					WSEditor secondaryEditor = pluginWorkspace.getEditorAccess(openedFileUrl,
							PluginWorkspace.MAIN_EDITING_AREA);
					secondaryEditor.addEditorListener(new OpenNewEditorListener(openerLocation, openedFileUrl));
				}
			}, "open-secondary-editor");
			openThread.start();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			authorDocumentController.endCompoundEdit();
		}

		logger.debug("================ end OpenFileInNewTabOperation ============================");

	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
	 */
	@Override
	public ArgumentDescriptor[] getArguments() {
		return null;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
	 */
	public String getDescription() {
		return "Execute an operation of opening a file in a new tab.";
	}

	private static class OpenNewEditorListener extends WSEditorListener {
		private final URL openedLocation;
		private final URL openerLocation;

		public OpenNewEditorListener(URL openerLocation, URL openedLocation) {
			this.openedLocation = openedLocation;
			this.openerLocation = openerLocation;
		}

		@Override
		public void editorSaved(int operationType) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					String openedFileName = URLUtil.uncorrect(URLUtil.extractFileName(openedLocation));

					String currentContent = "";
					try {
						currentContent = new String(Files.readAllBytes(Paths.get(openedLocation.toURI())), "UTF-8");

					} catch (IOException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					currentContent = currentContent.substring(xmlPI.length());

					WSEditor mainEditor = PluginWorkspaceProvider.getPluginWorkspace().getEditorAccess(openerLocation,
							PluginWorkspace.MAIN_EDITING_AREA);
					WSAuthorEditorPage mainWSAuthorEditorPage = (WSAuthorEditorPage) mainEditor.getCurrentPage();
					AuthorDocumentController openerAuthorDocumentController = mainWSAuthorEditorPage
							.getDocumentController();
					OptionsStorage optionsStorage = mainWSAuthorEditorPage.getOptionsStorage();

					String openedXpathExpr = optionsStorage.getOption(openedFileName + " xpath", "");

					AuthorNode targetNode = null;
					try {
						targetNode = openerAuthorDocumentController.findNodesByXPath(openedXpathExpr, true, true,
								true)[0];
					} catch (AuthorOperationException e) {
						e.printStackTrace();
					}

					try {
						openerAuthorDocumentController.insertXMLFragment(currentContent, targetNode,
								AuthorConstants.POSITION_AFTER);
					} catch (AuthorOperationException e) {
						e.printStackTrace();
					}

					int currentOffset = targetNode.getStartOffset();
					System.out.println("currentOffset " + currentOffset);

					openerAuthorDocumentController.deleteNode(targetNode);

					// int caretPosition = ((WSAuthorEditorPage)
					// purePage).getCaretOffset();
					// ((WSAuthorEditorPage)
					// purePage).setCaretPosition(caretPosition);
				}
			});

		}
	}
}
