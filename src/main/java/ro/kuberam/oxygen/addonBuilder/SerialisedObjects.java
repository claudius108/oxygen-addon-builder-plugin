package ro.kuberam.oxygen.addonBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ro.kuberam.oxygen.addonBuilder.utils.IOUtilities;

public class SerialisedObjects {

	public static String variablesModule = "";
	public static Map<String, String> templates = new HashMap<String, String>();

	// loading the serialized objects
	static {
		try {
			variablesModule = (String) IOUtilities.deserializeObjectFromFile("/prolog.ser");
			templates = (Map<String, String>) IOUtilities.deserializeObjectFromFile("/templates.ser");

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
