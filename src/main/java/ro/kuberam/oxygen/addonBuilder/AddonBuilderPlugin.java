package ro.kuberam.oxygen.addonBuilder;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import org.apache.log4j.Logger;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

public class AddonBuilderPlugin extends Plugin {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(AddonBuilderPlugin.class.getName());

	/**
	 * Plugin instance.
	 */
	private static AddonBuilderPlugin instance = null;

	/**
	 * AddonBuilderPlugin constructor.
	 * 
	 * @param descriptor
	 *            Plugin descriptor.
	 */
	public AddonBuilderPlugin(PluginDescriptor descriptor) {
		super(descriptor);

		if (instance != null) {
			throw new IllegalStateException("Already instantiated!");
		}
		instance = this;

		ClassLoader classLoader = this.getClass().getClassLoader();
		if (classLoader instanceof URLClassLoader) {
			URL[] urls = ((URLClassLoader) classLoader).getURLs();
			logger.debug("classpath: " + Arrays.toString(urls));
		}
	}

	/**
	 * Get the plugin instance.
	 * 
	 * @return the shared plugin instance.
	 */
	public static AddonBuilderPlugin getInstance() {
		return instance;
	}
}
