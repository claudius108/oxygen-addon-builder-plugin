package ro.kuberam.oxygen.addonBuilder.urlSchemes.xquery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.util.HashMap;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class XqueryUrlStreamHandler extends URLStreamHandler {

	private StandalonePluginWorkspace pluginWorkspaceAccess;

	public XqueryUrlStreamHandler(StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
	}

	protected URLConnection openConnection(URL url) throws IOException {
		HashMap<String, String> queryParameters = getQueryParameters(url.getQuery());
		String urlString = url.toString();
		urlString = urlString.substring(0, urlString.indexOf("?")).replaceAll("xquery:",
				queryParameters.get("scheme") + ":");

		return new XqueryURLConnection(new URL(urlString), pluginWorkspaceAccess,
				queryParameters.get("script"));
	}

	private HashMap<String, String> getQueryParameters(String query) {
		HashMap<String, String> queryParametersMap = new HashMap<String, String>();
		String[] queryParameters = query.split("&");

		for (String queryParameter : queryParameters) {
			String temp[] = queryParameter.split("=");
			try {
				queryParametersMap.put(temp[0], URLDecoder.decode(temp[1], "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return queryParametersMap;
	}
}
