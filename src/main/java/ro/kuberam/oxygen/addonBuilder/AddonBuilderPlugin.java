package ro.kuberam.oxygen.addonBuilder;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

public class AddonBuilderPlugin extends Plugin {

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
