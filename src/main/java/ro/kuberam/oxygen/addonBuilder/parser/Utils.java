package ro.kuberam.oxygen.addonBuilder.parser;

import java.util.ArrayList;
import java.util.Map;

public class Utils {

	public static ArrayList<String> generateDatalistImportStatements(Map<String, String> datalists) {
		ArrayList<String> datalistImportStatements = new ArrayList<>();
		datalistImportStatements.add("@charset \"utf-8\";");

		for (Map.Entry<String, String> datalist : datalists.entrySet()) {
			datalistImportStatements.add("@import \"datalists/" + datalist.getKey() + ".less\";");
		}

		return datalistImportStatements;
	}

}
