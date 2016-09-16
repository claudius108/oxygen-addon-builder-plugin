package ro.kuberam.oxygen.addonBuilder.parser;

import org.junit.Assert;
import org.junit.Test;

public class OxyActionTest {

	@Test
	public void test1() {
		OxyAction oxyAction = new OxyAction();
		oxyAction.setId("search");
		System.out.println(oxyAction.toLessDeclaration());
		oxyAction.setName("Variantă lexicală");

		System.out.println(oxyAction.toLessDeclaration());
		Assert.assertEquals(
				"@search: oxy_action(name, \"Search\", operation, \"XQueryOperation\", arg-action, \"After\", arg-script, \"search.xq\");",
				oxyAction.toLessDeclaration());
	}
	
	@Test
	public void testXQueryOperation() {
		OxyAction oxyAction = new OxyAction();
		oxyAction.setId("search");
		System.out.println(oxyAction.toLessDeclaration());
		oxyAction.setName("Search");
		oxyAction.setDescription("description");
		System.out.println(oxyAction.toLessDeclaration());
		oxyAction.setIcon("icon.jpg");
		System.out.println(oxyAction.toLessDeclaration());
		oxyAction.setOperation("XQueryOperation");

		oxyAction.setArgument("script", "search.xq");
		oxyAction.setArgument("action", "After");

		System.out.println(oxyAction.toLessDeclaration());
		Assert.assertEquals(
				"@search: oxy_action(name, \"Search\", operation, \"XQueryOperation\", arg-action, \"After\", arg-script, \"search.xq\");",
				oxyAction.toLessDeclaration());
	}
}
