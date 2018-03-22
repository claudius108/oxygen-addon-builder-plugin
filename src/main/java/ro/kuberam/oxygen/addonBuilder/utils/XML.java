package ro.kuberam.oxygen.addonBuilder.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorDocument;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.AuthorParentNode;
import ro.sync.xml.XmlUtil;

public class XML {
	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private static DocumentBuilder builder = null;
	static {
		try {
			factory.setNamespaceAware(true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static String xmlToString(Node node, String indent, String omit_xml_declaration) {
		Transformer transformer = null;
		StreamResult result = null;

		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, indent);
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omit_xml_declaration);
			result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(node);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		String xmlString = result.getWriter().toString();

		return xmlString;
	}

	public static String formatXmlString(String xmlString) {

		Source xmlInput = new StreamSource(new StringReader(xmlString));
		StreamResult xmlOutput = new StreamResult(new StringWriter());

		Transformer transformer = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		try {
			transformer.transform(xmlInput, xmlOutput);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return xmlOutput.getWriter().toString();
	}

	public static Element parse(String xmlString) {
		Document result = null;
		try {
			InputSource is = new InputSource(new StringReader(xmlString));
			result = builder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.getDocumentElement();
	}

	public static Element parse(File xmlFile) {
		Document result = null;
		try {
			result = builder.parse(xmlFile);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.getDocumentElement();
	}

	public static Element parse(InputStream is) {
		Document result = null;
		try {
			result = builder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.getDocumentElement();
	}

	public static String getXPathExpression(AuthorDocumentController authorDocumentController,
			int selectionStart) {
		String xpathExpression = "";
		try {
			xpathExpression = authorDocumentController.getXPathExpression(selectionStart);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return xpathExpression.replace("/", "/*:");
	}

//	public static String getXPathExpression(AuthorDocumentController authorDocumentController,
//			AuthorParentNode currentNode) throws BadLocationException {
//		String xpathExpr = "";
//		AuthorDocument doc = authorDocumentController.getAuthorDocumentNode();
//		if (doc != null) {
//
//			while (currentNode != null && AuthorNode.NODE_TYPE_DOCUMENT != currentNode.getType()) {
//				String name = currentNode.getName();
//				String proxy = XmlUtil.getProxy(name);
//				String localname = XmlUtil.getLocalName(name);
//
//				String indexInParent = "";
//
//				AuthorParentNode parent = (AuthorParentNode) currentNode.getParent();
//				if (parent != null && AuthorNode.NODE_TYPE_DOCUMENT != parent.getType()) {
//					List<AuthorNode> children = parent.getContentNodes();
//					int index = 0;
//					for (Iterator<AuthorNode> iter = children.iterator(); iter.hasNext();) {
//						AuthorNode child = (AuthorNode) iter.next();
//						index++;
//						if (child == currentNode) {
//							indexInParent = "[" + index + "]";
//							break;
//						}
//
//					}
//				}
//
//				if (proxy != null && localname != null) {
//					xpathExpr = "/*" + indexInParent + xpathExpr;
//				} else {
//					// This should not happen.
//					xpathExpr = "";
//					break;
//				}
//				currentNode = (AuthorParentNode) currentNode.getParent();
//			}
//		}
//		return xpathExpr;
//	}

	public static String completeXpathExpression(String xpathExpression) {
		String completedXpathExpression = "";

		if (xpathExpression.startsWith("$document")) {
			System.out.println(xpathExpression);
			xpathExpression = xpathExpression.substring(xpathExpression.indexOf("/") + 1);
			System.out.println(xpathExpression);
			if (xpathExpression.contains("/")) {
				xpathExpression = xpathExpression.substring(xpathExpression.indexOf("/"));
			} else {
				xpathExpression = "/element()[1]";
			}
			System.out.println(xpathExpression);
			xpathExpression = "$document" + xpathExpression;
			System.out.println(xpathExpression);
			completedXpathExpression += "let $document := /* return ";
		}

		return completedXpathExpression + xpathExpression;
	}
}
