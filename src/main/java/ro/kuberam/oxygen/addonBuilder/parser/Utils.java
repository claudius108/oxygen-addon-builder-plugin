package ro.kuberam.oxygen.addonBuilder.parser;

import java.util.Map;

public class Utils {
	
	public static String generateDatalistImportStatements(Map<String, String> datalists) {
		String datalistImportStatements = "";

		for (Map.Entry<String, String> datalist : datalists.entrySet()) {
			datalistImportStatements += "@import \"datalists/" + datalist.getKey() + ".less\"; ";
		}

		datalistImportStatements = datalistImportStatements.trim();

		return datalistImportStatements;
	}
	
}
