package ro.kuberam.oxygen.addonBuilder.operations;

public class ErrorMessages {

	public static String err_XUTY0004 = "err:XUTY0004: It is a type error if the insertion sequence of an insert expression contains an attribute node following a node that is not an attribute node.";
	public static String err_XUTY0005 = "err:XUTY0005: In an insert expression where into, as first into, or as last into is specified, it is a type error if the target expression returns a non-empty result that does not consist of a single element or document node.";
	public static String err_XUTY0006 = "err:XUTY0006: In an insert expression where before or after is specified, it is a type error if the target expression returns a non-empty result that does not consist of a single element, text, comment, or processing instruction node.";
	public static String err_XUTY0008 = "err:XUTY0008: In a replace expression, it is a type error if the target expression returns a non-empty result that does not consist of a single element, attribute, text, comment, or processing instruction node.";
	public static String err_XUDY0009 = "err:XUDY0009: In a replace expression where value of is not specified, it is a dynamic error if the node returned by the target expression does not have a parent.";
	public static String err_XUTY0010 = "err:XUTY0010: In a replace expression where value of is not specified and the target is an element, text, comment, or processing instruction node, it is a type error if the replacement sequence does not consist of zero or more element, text, comment, or processing instruction nodes.";
	public static String err_XUTY0011 = "err:XUTY0011: In a replace expression where value of is not specified and the target is an attribute node, it is a type error if the replacement sequence does not consist of zero or more attribute nodes.";
	public static String err_XUTY0022 = "err:XUTY0022: It is a type error if an insert expression specifies the insertion of an attribute node into a document node.";
	public static String err_XUDY0027 = "err:XUDY0027: It is a dynamic error if the target expression of an insert, replace, or rename expression evaluates to an empty sequence.";
	public static String err_XUDY0029 = "err:XUDY0029: In an insert expression where before or after is specified, it is a dynamic error if the node returned by the target expression does not have a parent.";
	public static String err_XUDY0030 = "err:XUDY0030: It is a dynamic error if an insert expression specifies the insertion of an attribute node before or after a child of a document node.";

}
