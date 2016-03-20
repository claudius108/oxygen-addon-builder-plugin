package ro.kuberam.oxygen.addonBuilder.parser;

import org.junit.Assert;
import org.junit.Test;

public class OxyActionTest {

	@Test
	public void testXQueryOperation() {
		OxyAction oxyAction = new OxyAction();
		oxyAction.setId("search");
		oxyAction.setName("Search");
		oxyAction.setOperation("ro.sync.ecss.extensions.commons.operations.XQueryOperation");

		oxyAction
				.setArgument("script",
						"import module namespace biblio = 'http://dlri.ro/ns/biblio/' at 'form-controls/search.xq'; biblio:run()");
		oxyAction.setArgument("action", "After");

		Assert.assertEquals(
				"@search: oxy_action(name, \"Search\", operation, \"ro.sync.ecss.extensions.commons.operations.XQueryOperation\", arg-action, \"After\", arg-script, \"import module namespace biblio = 'http://dlri.ro/ns/biblio/' at 'form-controls/search.xq'; biblio:run()\");",
				oxyAction.toLessDeclaration());
	}
}
