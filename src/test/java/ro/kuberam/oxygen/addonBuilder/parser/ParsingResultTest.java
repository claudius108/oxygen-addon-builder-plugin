package ro.kuberam.oxygen.addonBuilder.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ParsingResultTest {

	@Test
	public void testGenerateDatalistImportStatements() {
		Map<String, String> datalists = new HashMap<String, String>();
		datalists.put("datalist-1", "value-11, value-12, value-13");
		datalists.put("datalist-2", "value-21, value-22, value-23");

		ArrayList<String> datalistImportStatements = Utils.generateDatalistImportStatements(datalists);

		Assert.assertEquals("@charset \"utf-8\"; @import \"datalists/datalist-1.less\"; @import \"datalists/datalist-2.less\";",
				String.join(" ", datalistImportStatements));
	}
}
