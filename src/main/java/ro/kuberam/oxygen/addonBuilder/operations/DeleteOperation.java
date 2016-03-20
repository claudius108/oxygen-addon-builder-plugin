package ro.kuberam.oxygen.addonBuilder.operations;

import javax.swing.text.BadLocationException;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;

import ro.kuberam.oxygen.addonBuilder.Constants;
import ro.kuberam.oxygen.addonBuilder.utils.XML;
import ro.sync.ecss.dom.wrappers.AuthorAttrDomWrapper;
import ro.sync.ecss.dom.wrappers.AuthorElementDomWrapper;
import ro.sync.ecss.dom.wrappers.AuthorNodeDomWrapper;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.XPathVersion;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil;

public class DeleteOperation implements AuthorOperation {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(DeleteOperation.class.getName());

	/**
	 * The target expression for the operation. The value is <code>script</code>
	 * .
	 */
	protected String ARGUMENT_TARGET_LOCATION = "elementLocation";

	/**
	 * The arguments of the operation.
	 */
	private ArgumentDescriptor[] arguments = null;

	/**
	 * Constructor.
	 */
	public DeleteOperation() {
		arguments = new ArgumentDescriptor[1];

		ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(ARGUMENT_TARGET_LOCATION,
				ArgumentDescriptor.TYPE_XPATH_EXPRESSION, "The target expression for the operation.");
		arguments[0] = argumentDescriptor;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess,
	 *      ArgumentsMap)
	 */
	public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
		logger.debug("\n================ start delete operation ============================");

		AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();
		AuthorDocumentController authorDocumentController = authorAccess.getDocumentController();

		Object targetExprObj = args.getArgumentValue(ARGUMENT_TARGET_LOCATION);
		logger.debug("targetExprObj: " + targetExprObj);

		String targetExpr = (String) targetExprObj;
		logger.debug("targetExpr: " + targetExpr);

		AuthorNode currentNode = null;
		String currentNodeXpathExpr = null;

		logger.debug("getSelectionStart offset: " + authorEditorAccess.getSelectionStart());

		try {
			currentNode = authorDocumentController.getNodeAtOffset(authorEditorAccess.getSelectionStart());
			currentNodeXpathExpr = XML.getXPathExpression(authorDocumentController,
					authorEditorAccess.getSelectionStart());
		} catch (BadLocationException e) {

			e.printStackTrace();
		}

		logger.debug("currentNode.getName(): " + currentNode.getName());
		logger.debug("currentNodeXpathExpr: " + currentNodeXpathExpr);
		logger.debug("authorAccess.getEditorAccess().getCaretOffset(): "
				+ authorEditorAccess.getCaretOffset());
		logger.debug("currentNode.getStartOffset(): " + currentNode.getStartOffset());

		logger.debug("currentNode for delete: " + currentNode);

		targetExpr = targetExpr.replace("$ua:context", currentNodeXpathExpr);
		targetExpr = XML.completeXpathExpression(targetExpr);

		Object[] targetNodeObjects = authorDocumentController.evaluateXPath(targetExpr, currentNode, false,
				true, true, false, XPathVersion.XPATH_3_0);

		logger.debug("targetExpr processed for delete: " + targetExpr);

		if (targetNodeObjects.length > 0 && targetNodeObjects[0] != null) {
			for (int i = 0, il = targetNodeObjects.length; i < il; i++) {
				Object targetNodeObject = targetNodeObjects[i];

				if (targetNodeObject instanceof AuthorElementDomWrapper) {
					AuthorNode targetNode = ((AuthorElementDomWrapper) targetNodeObject)
							.getWrappedAuthorNode();

					authorDocumentController.deleteNode(targetNode);
				}

				if (targetNodeObject instanceof AuthorAttrDomWrapper) {
					Attr targetNode = (Attr) targetNodeObject;
					AuthorNode parentNode = ((AuthorNodeDomWrapper) targetNode.getOwnerElement())
							.getWrappedAuthorNode();
					AuthorElement parentElement = (AuthorElement) parentNode;

					try {
						CommonsOperationsUtil.setAttributeValue(authorDocumentController, parentElement,
								new QName(targetNode.getNamespaceURI(), targetNode.getLocalName(),
										targetNode.getPrefix()), Constants.valueOfAttributeToBeDeleted,
								true);
					} catch (Exception e) {
						e.printStackTrace();
					}

					authorEditorAccess.refresh();
				}

			}
		}

		authorEditorAccess.setCaretPosition(currentNode.getStartOffset() + 1);
		logger.debug("currentNode.getStartOffset(): " + currentNode.getStartOffset());
		logger.debug("================ end delete operation ============================\n");

	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
	 */
	@Override
	public ArgumentDescriptor[] getArguments() {
		return arguments;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
	 */
	public String getDescription() {
		return "Execute an XQuery Update delete operation.";
	}
}
