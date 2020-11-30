package ro.kuberam.oxygen.addonBuilder.utils;

import org.junit.Test;

public class XMLTest {
	
	@Test
	public void testCompleteXpathExpression1() {
		
		String expression = "$document/*:TEI/*:text[1]/*:body[1]/*:entry[1]/*:gramGrp[1]";
		
		expression = XML.completeXpathExpression(expression);
		
		System.out.println(expression);
	}
	
	@Test
	public void testCompleteXpathExpression2() {
		
		String expression = "$document/*:sense";
		
		expression = XML.completeXpathExpression(expression);
		
		System.out.println(expression);
	}
}
