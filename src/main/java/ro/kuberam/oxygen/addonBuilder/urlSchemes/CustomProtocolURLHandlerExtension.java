package ro.kuberam.oxygen.addonBuilder.urlSchemes;

import java.net.URLStreamHandler;

import ro.kuberam.oxygen.addonBuilder.urlSchemes.xquery.XqueryUrlStreamHandler;
import ro.sync.exml.plugin.urlstreamhandler.URLStreamHandlerPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Plugin extension.
 */
public class CustomProtocolURLHandlerExtension implements URLStreamHandlerPluginExtension {

	public static StandalonePluginWorkspace pluginWorkspaceAccess;

	/**
	 * Constructor.
	 */
	public CustomProtocolURLHandlerExtension() {
	}

	/**
	 * @see ro.sync.exml.plugin.urlstreamhandler.URLStreamHandlerPluginExtension#getURLStreamHandler(java.lang.String)
	 */
	@Override
	public URLStreamHandler getURLStreamHandler(String urlScheme) {
		if ("xquery".equals(urlScheme)) {
			return new XqueryUrlStreamHandler(pluginWorkspaceAccess);
		}
		return null;
	}

	public static void setPluginWorkspaceAccess(StandalonePluginWorkspace pluginWorkspaceAccess) {
		CustomProtocolURLHandlerExtension.pluginWorkspaceAccess = pluginWorkspaceAccess;
	}
}
