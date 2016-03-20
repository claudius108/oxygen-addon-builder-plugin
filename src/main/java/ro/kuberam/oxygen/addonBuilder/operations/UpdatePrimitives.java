package ro.kuberam.oxygen.addonBuilder.operations;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import ro.sync.ecss.dom.wrappers.AuthorAttrDomWrapper;
import ro.sync.ecss.dom.wrappers.AuthorCommentDomWrapper;
import ro.sync.ecss.dom.wrappers.AuthorDocumentDomWrapper;
import ro.sync.ecss.dom.wrappers.AuthorElementDomWrapper;
import ro.sync.ecss.dom.wrappers.AuthorPIDomWrapper;
import ro.sync.ecss.dom.wrappers.AuthorTextNodeDomWrapper;
import ro.sync.ecss.dom.wrappers.AuthorNodeDomWrapper;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil;

public class UpdatePrimitives {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(UpdatePrimitives.class.getName());

	public static String getOxygenNodeKind(Object targetNodeObject) {
		String targetNodeKind = "";

		if (targetNodeObject instanceof AuthorDocumentDomWrapper) {
			targetNodeKind = "DOCUMENT";
		}

		if (targetNodeObject instanceof AuthorElementDomWrapper) {
			targetNodeKind = "ELEMENT";
		}

		if (targetNodeObject instanceof AuthorAttrDomWrapper) {
			targetNodeKind = "ATTRIBUTE";
		}

		if (targetNodeObject instanceof AuthorTextNodeDomWrapper) {
			targetNodeKind = "TEXT";
		}

		if (targetNodeObject instanceof AuthorCommentDomWrapper) {
			targetNodeKind = "COMMENT";
		}

		if (targetNodeObject instanceof AuthorPIDomWrapper) {
			targetNodeKind = "PROCESSING_INSTRUCTION";
		}

		return targetNodeKind;
	}

	public static String getOxygenNodeKind(AuthorNode targetNode) {
		int targetNodeType = targetNode.getType();
		String targetNodeKind = "ATTRIBUTE";

		switch (targetNodeType) {
		case AuthorNode.NODE_TYPE_DOCUMENT:
			targetNodeKind = "DOCUMENT";
			break;
		case AuthorNode.NODE_TYPE_ELEMENT:
			targetNodeKind = "ELEMENT";
			break;
		case AuthorNode.NODE_TYPE_TEXT:
			targetNodeKind = "TEXT";
			break;
		case AuthorNode.NODE_TYPE_COMMENT:
			targetNodeKind = "COMMENT";
			break;
		case AuthorNode.NODE_TYPE_PI:
			targetNodeKind = "PROCESSING_INSTRUCTION";
			break;
		}

		return targetNodeKind;
	}

	public static AuthorNode getOxygenNode(Object targetObject, String targetNodeKind) {
		AuthorNode target = null;

		switch (targetNodeKind) {
		case "ELEMENT":
			target = ((AuthorElementDomWrapper) targetObject).getWrappedAuthorNode();
			break;
		case "TEXT":
			target = ((AuthorTextNodeDomWrapper) targetObject).getWrappedAuthorNode();
			break;
		case "COMMENT":
			target = ((AuthorCommentDomWrapper) targetObject).getWrappedAuthorNode();
			break;
		case "PROCESSING_INSTRUCTION":
			target = ((AuthorPIDomWrapper) targetObject).getWrappedAuthorNode();
			break;
		case "ATTRIBUTE":
			target = ((AuthorNodeDomWrapper) targetObject).getWrappedAuthorNode();
			break;
		}

		return target;

	}

	public static AuthorNode getOxygenParentNode(Object target, String targetKind) {
		AuthorNode parentNode = null;

		switch (targetKind) {
		case "ELEMENT":
			parentNode = ((AuthorElementDomWrapper) target).getWrappedAuthorNode().getParent();
			break;
		case "ATTRIBUTE":
			Attr targetNode = (Attr) target;
			parentNode = ((AuthorNodeDomWrapper) targetNode.getOwnerElement()).getWrappedAuthorNode();
			break;
		}

		return parentNode;

	}

	public static void insertAttributes(AuthorElement target, ArrayList<XdmNode> alist,
			AuthorDocumentController authorDocumentController) {
		for (int i = 0; i < alist.size(); i++) {
			XdmNode attribute = alist.get(i);
			NodeInfo attributeUnderlyingNode = attribute.getUnderlyingNode();
			CommonsOperationsUtil.setAttributeValue(authorDocumentController, target, new QName(
					attributeUnderlyingNode.getURI(), attributeUnderlyingNode.getLocalPart(),
					attributeUnderlyingNode.getPrefix()), attribute.getStringValue(), false);
		}
	}

	public static void insertIntoAsFirst(AuthorElement target, ArrayList<XdmNode> clist,
			AuthorDocumentController authorDocumentController) {
		for (int i = clist.size() - 1; i >= 0; i--) {
			XdmNode node = clist.get(i);
			if (!node.isAtomicValue()) {
				String nodeKind = node.getNodeKind().name();
				switch (nodeKind) {
				case "ELEMENT":
					try {
						authorDocumentController.insertXMLFragment(node.toString(), target,
								"Inside as first child");
					} catch (AuthorOperationException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	public static void insertIntoAsLast(AuthorElement target, ArrayList<XdmNode> clist,
			AuthorDocumentController authorDocumentController) {
		for (int i = 0; i < clist.size(); i++) {
			XdmNode node = clist.get(i);
			if (!node.isAtomicValue()) {
				String nodeKind = node.getNodeKind().name();
				switch (nodeKind) {
				case "ELEMENT":
					try {
						authorDocumentController.insertXMLFragment(node.toString(), target,
								"Inside as last child");
					} catch (AuthorOperationException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	public static void insertBefore(AuthorElement target, ArrayList<XdmNode> clist,
			AuthorDocumentController authorDocumentController) {
		for (int i = 0; i < clist.size(); i++) {
			XdmNode node = clist.get(i);
			if (!node.isAtomicValue()) {
				String nodeKind = node.getNodeKind().name();
				switch (nodeKind) {
				case "ELEMENT":
					try {
						authorDocumentController.insertXMLFragment(node.toString(), target, "Before");
					} catch (AuthorOperationException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	public static void insertAfter(AuthorElement target, ArrayList<XdmNode> clist,
			AuthorDocumentController authorDocumentController) {
		for (int i = clist.size() - 1; i >= 0; i--) {
			XdmNode node = clist.get(i);
			if (!node.isAtomicValue()) {
				String nodeKind = node.getNodeKind().name();
				switch (nodeKind) {
				case "ELEMENT":
					try {
						authorDocumentController.insertXMLFragment(node.toString(), target, "After");
					} catch (AuthorOperationException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	public static void replaceNode(Object targetObject, ArrayList<XdmItem> rlist,
			AuthorDocumentController authorDocumentController, String targetKind) {

		switch (targetKind) {
		case "ELEMENT":

			AuthorElement target = (AuthorElement) getOxygenNode(targetObject, "ELEMENT");
			int insertOffset = target.getStartOffset();

			authorDocumentController.deleteNode(target);

			for (int i = rlist.size() - 1; i >= 0; i--) {
				XdmItem item = rlist.get(i);

				if (item.isAtomicValue()) {
					authorDocumentController.insertText(insertOffset, item.getStringValue());
				} else {
					XdmNode node = (XdmNode) item;
					String nodeKind = node.getNodeKind().name();

					switch (nodeKind) {
					case "TEXT":
						authorDocumentController.insertText(insertOffset, node.getStringValue());
						break;
					case "ELEMENT":
						try {
							authorDocumentController.insertXMLFragment(node.toString(), insertOffset);
						} catch (AuthorOperationException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
			break;
		case "ATTRIBUTE":
			Attr targetNode = (Attr) targetObject;
			String attributeName = targetNode.getName();
			AuthorElement parent = (AuthorElement) ((AuthorNodeDomWrapper) targetNode.getOwnerElement())
					.getWrappedAuthorNode();

			for (int i = rlist.size() - 1; i >= 0; i--) {
				XdmItem item = rlist.get(i);
				XdmNode node = (XdmNode) item;

				if (!node.isAtomicValue()) {
					String nodeKind = node.getNodeKind().name();

					switch (nodeKind) {
					case "ATTRIBUTE":
						parent.removeAttribute(attributeName);
						NodeInfo attributeUnderlyingNode = node.getUnderlyingNode();
						CommonsOperationsUtil.setAttributeValue(
								authorDocumentController,
								parent,
								new QName(attributeUnderlyingNode.getURI(), attributeUnderlyingNode
										.getLocalPart(), attributeUnderlyingNode.getPrefix()), node
										.getStringValue(), false);
						break;
					}

				}
			}
			break;
		}
	}

	public static void replaceElementContent(Object targetObject, String text,
			AuthorDocumentController authorDocumentController, String targetKind) {

		AuthorElement target = (AuthorElement) getOxygenNode(targetObject, "ELEMENT");

		int insertOffset = target.getStartOffset();

		if (target.getStartOffset() + 1 < target.getEndOffset()) {
			authorDocumentController.delete(target.getStartOffset() + 1, target.getEndOffset() - 1);
		}

		authorDocumentController.insertText(insertOffset + 1, text);
	}

	public static void replaceValue(Object targetObject, String text,
			AuthorDocumentController authorDocumentController, String targetKind, AuthorNode parent) {

		switch (targetKind) {
		case "ATTRIBUTE":
			Attr targetNode = (Attr) targetObject;
			String attributeName = targetNode.getName();
			authorDocumentController.setAttribute(attributeName, new AttrValue(text),
					(AuthorElement) parent);
			break;
		}
	}
}
