package ro.kuberam.oxygen.addonBuilder.utils;

import ro.kuberam.oxygen.addonBuilder.SerialisedObjects;


public class XQuery {

	public static String completeXqueryScript(String xqueryScript) {
		String completedXqueryScript = "";
		completedXqueryScript += SerialisedObjects.variablesModule;
		
		return completedXqueryScript + xqueryScript.replace("$document", "$ua:document");
	}
}
