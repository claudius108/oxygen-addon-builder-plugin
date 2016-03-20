package ro.kuberam.oxygen.addonBuilder.urlSchemes.xquery;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import ro.kuberam.oxygen.addonBuilder.operations.XQueryOperation;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class XqueryURLConnection extends URLConnection {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(XqueryURLConnection.class.getName());

	private WSEditor editorAccess;
	private String scriptUrl;

	protected XqueryURLConnection(URL url, StandalonePluginWorkspace pluginWorkspaceAccess, String scriptUrl) {
		super(url);
		this.editorAccess = pluginWorkspaceAccess.getEditorAccess(url, PluginWorkspace.MAIN_EDITING_AREA);
		this.scriptUrl = scriptUrl;
	}

	/**
	 * @see java.net.URLConnection#connect()
	 */
	@Override
	public void connect() throws IOException {
	}

	/**
	 * @see java.net.URLConnection#getInputStream()
	 */
	@Override
	public synchronized InputStream getInputStream() throws IOException {

		editorAccess.save();

		String content = XQueryOperation
				.query(editorAccess.createContentReader(), new FileInputStream(scriptUrl), true, null)
				.itemAt(0).toString();

		logger.debug(content);

		return IOUtils.toInputStream(content, "UTF-8");
	}
}
