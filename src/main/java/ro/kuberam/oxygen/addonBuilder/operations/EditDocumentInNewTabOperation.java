package ro.kuberam.oxygen.addonBuilder.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.exml.editor.EditorPageConstants;

public class EditDocumentInNewTabOperation implements AuthorOperation {

	/**
	 * This operation is designated to allow editing of XML fragment of a main
	 * document in new editors, opened in new tabs. When the data in the new editors
	 * is saved, the main document is updated accordingly.
	 */

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(EditDocumentInNewTabOperation.class.getName());

	/**
	 * The dialog id. The value is <code>string</code> .
	 */
	protected String ARGUMENT_DOCUMENT_URL = "document_url";

	/**
	 * The arguments of the operation.
	 */
	private ArgumentDescriptor[] arguments = null;

	/**
	 * Constructor.
	 */
	public EditDocumentInNewTabOperation() {
		arguments = new ArgumentDescriptor[1];

		ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(ARGUMENT_DOCUMENT_URL,
				ArgumentDescriptor.TYPE_STRING, "The url of document.");
		arguments[0] = argumentDescriptor;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess,
	 *      ArgumentsMap)
	 */
	public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
		logger.debug("================ start EditDocumentInNewTabOperation ========================");

		Object documentUrlObj = args.getArgumentValue(ARGUMENT_DOCUMENT_URL);

		try {
			URL documentUrl = new URL((String) documentUrlObj);
			logger.debug("documentUrl = " + documentUrl);
			String documentName = Paths.get(documentUrl.getPath()).getFileName().toString();
			logger.debug("documentName = " + documentName);

			Path path = Paths.get(System.getProperty("java.io.tmpdir"), documentName);
			File file = path.toFile();
			file.deleteOnExit();

			ReadableByteChannel rbc = Channels.newChannel(documentUrl.openStream());
			FileOutputStream fos = new FileOutputStream(file);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();

			authorAccess.getWorkspaceAccess().open(file.toURI().toURL(), EditorPageConstants.PAGE_AUTHOR, "text/xml");

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		logger.debug("================ end EditDocumentInNewTabOperation ============================");

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
}
