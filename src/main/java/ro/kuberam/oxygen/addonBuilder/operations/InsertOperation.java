package ro.kuberam.oxygen.addonBuilder.operations;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

import ro.kuberam.oxygen.addonBuilder.utils.XML;
import ro.kuberam.oxygen.addonBuilder.utils.XQuery;
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
import ro.sync.util.editorvars.EditorVariables;

public class InsertOperation implements AuthorOperation {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(InsertOperation.class.getName());

	/**
	 * The choice for the operation. The value is <code>string</code> .
	 */
	protected String ARGUMENT_INSERT_ACTION = "insertAction";

	/**
	 * The source expression for the operation. The value is <code>script</code>
	 * .
	 */
	protected String ARGUMENT_INSERT_SOURCE_LOCATION = "insertSourceLocation";

	/**
	 * The target expression for the operation. The value is <code>script</code>
	 * .
	 */
	protected String ARGUMENT_TARGET_LOCATION = "insertTargetLocation";

	/**
	 * The arguments of the operation.
	 */
	private ArgumentDescriptor[] arguments = null;

	/**
	 * Constructor.
	 */
	public InsertOperation() {
		arguments = new ArgumentDescriptor[3];

		ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(ARGUMENT_INSERT_ACTION,
				ArgumentDescriptor.TYPE_STRING, "The choice for the operation.");
		arguments[0] = argumentDescriptor;
		argumentDescriptor = new ArgumentDescriptor(ARGUMENT_INSERT_SOURCE_LOCATION, ArgumentDescriptor.TYPE_STRING,
				"The source expression for the operation.");
		arguments[1] = argumentDescriptor;

		argumentDescriptor = new ArgumentDescriptor(ARGUMENT_TARGET_LOCATION, ArgumentDescriptor.TYPE_STRING,
				"The target expression for the operation.");
		arguments[2] = argumentDescriptor;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess,
	 *      ArgumentsMap)
	 */
	public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
		logger.debug("\n================ start insert operation ============================");

		AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();
		AuthorDocumentController authorDocumentController = authorAccess.getDocumentController();

		Object actionObj = args.getArgumentValue(ARGUMENT_INSERT_ACTION);
		Object sourceExprObj = args.getArgumentValue(ARGUMENT_INSERT_SOURCE_LOCATION);
		Object targetExprObj = args.getArgumentValue(ARGUMENT_TARGET_LOCATION);

		String targetChoice = (String) actionObj;
		String sourceExpr = (String) sourceExprObj;
		logger.debug("sourceExpr: " + sourceExpr);
		String targetExpr = (String) targetExprObj;
		logger.debug("targetExpr: " + targetExpr);

		try {
			int caretOffset = authorEditorAccess.getCaretOffset();
			logger.debug("caretOffset = " + caretOffset);
			AuthorNode currentNode = authorDocumentController.getNodeAtOffset(caretOffset);
			logger.debug("currentNode = " + currentNode);
			String currentNodeXpathExpr = authorDocumentController.getXPathExpression(caretOffset);
			logger.debug("currentNodeXpathExpr = " + currentNodeXpathExpr);

			sourceExpr = sourceExpr.replace("$ua:context", currentNodeXpathExpr);
			sourceExpr = authorAccess.getUtilAccess().expandEditorVariables(sourceExpr, null);
			sourceExpr = XQuery.completeXqueryScript(sourceExpr);
			logger.debug("processed sourceExpr for insert: " + sourceExpr);

			targetExpr = targetExpr.replace("$ua:context", currentNodeXpathExpr);
			targetExpr = XML.completeXpathExpression(targetExpr);
			logger.debug("processed targetExpr for insert: " + targetExpr);

			URI baseURI = null;
			try {
				baseURI = new URI(authorAccess.getUtilAccess().expandEditorVariables(EditorVariables.FRAMEWORK_URL,
						authorAccess.getEditorAccess().getEditorLocation()));
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			logger.debug("baseURI: " + baseURI.toASCIIString());

			XdmValue sourceSequence = XQueryOperation.query(authorEditorAccess.createContentReader(),
					new ByteArrayInputStream(sourceExpr.getBytes(StandardCharsets.UTF_8)), true, baseURI);
			logger.debug("processedSourceExpr: " + sourceSequence.getUnderlyingValue().toString());

			// generate the $alist and $clist lists
			ArrayList<XdmNode> alist = new ArrayList<XdmNode>();
			ArrayList<XdmNode> clist = new ArrayList<XdmNode>();

			for (int i = 0; i < sourceSequence.size(); i++) {
				XdmItem item = sourceSequence.itemAt(i);
				logger.debug("item: " + item.toString());

				if (!item.isAtomicValue()) {
					XdmNode node = (XdmNode) item;
					String nodeKind = node.getNodeKind().name();
					logger.debug("nodeKind: " + nodeKind);

					switch (nodeKind) {
					case "ATTRIBUTE":
						if (clist.size() > 0) {
							throw new AuthorOperationException(ErrorMessages.err_XUTY0004);
						}
						alist.add(node);
						break;
					case "DOCUMENT":
						clist.add(XQueryOperation.getDocumentElement(node));
						break;
					default:
						clist.add(node);
						break;
					}
				}
			}

			logger.debug("alist: " + alist);
			logger.debug("clist: " + clist);

			Object[] targetObjects = authorDocumentController.evaluateXPath(targetExpr, currentNode, false, true, true,
					false, XPathVersion.XPATH_3_0);
			int targetObjectsNumber = targetObjects.length;
			logger.debug("targetObjectsNumber: " + targetObjectsNumber);

			if (targetObjectsNumber == 0) {
				throw new AuthorOperationException(ErrorMessages.err_XUDY0027);
			}

			Object targetObject = targetObjects[0];
			String targetKind = UpdatePrimitives.getOxygenNodeKind(targetObject);
			logger.debug("targetKind: " + targetKind);
			AuthorNode target = UpdatePrimitives.getOxygenNode(targetObject, targetKind);
			AuthorNode parent = target.getParent();

			if (targetChoice.contains("Inside")) {
				if (targetObjectsNumber > 1) {
					throw new AuthorOperationException(ErrorMessages.err_XUTY0005);
				} else {
					if (!"DOCUMENT ELEMENT".contains(targetKind)) {
						throw new AuthorOperationException(ErrorMessages.err_XUTY0005);
					}
				}
			}

			if (targetChoice.equals("After") || targetChoice.equals("Before")) {
				if (targetObjectsNumber > 1) {
					throw new AuthorOperationException(ErrorMessages.err_XUTY0006);
				} else {
					if (!"ELEMENT TEXT COMMENT PROCESSING_INSTRUCTION".contains(targetKind)) {
						throw new AuthorOperationException(ErrorMessages.err_XUTY0006);
					}
					if (parent == null) {
						throw new AuthorOperationException(ErrorMessages.err_XUDY0029);
					}
				}
			}

			String parentKind = UpdatePrimitives.getOxygenNodeKind(parent);
			logger.debug("parentKind: " + parentKind);

			if (alist.size() > 0 && targetChoice.contains("Inside")) {
				if (!targetKind.equals("ELEMENT")) {
					throw new AuthorOperationException(ErrorMessages.err_XUTY0022);
				}
			}

			if (!parentKind.equals("ELEMENT")) {
				throw new AuthorOperationException(ErrorMessages.err_XUDY0030);
			}

			// starting the updates
			if (alist.size() > 0) {
				UpdatePrimitives.insertAttributes((AuthorElement) target, alist, authorDocumentController);
			}

			if (targetChoice.equals("Inside as first child")) {
				if (clist.size() > 0) {
					UpdatePrimitives.insertIntoAsFirst((AuthorElement) target, clist, authorDocumentController);
				}
			}

			if (targetChoice.equals("Inside as last child")) {
				if (clist.size() > 0) {
					UpdatePrimitives.insertIntoAsLast((AuthorElement) target, clist, authorDocumentController);
				}
			}

			if (targetChoice.equals("Before")) {
				if (clist.size() > 0) {
					UpdatePrimitives.insertBefore((AuthorElement) target, clist, authorDocumentController);
				}
			}

			if (targetChoice.equals("After")) {
				if (clist.size() > 0) {
					UpdatePrimitives.insertAfter((AuthorElement) target, clist, authorDocumentController);
				}
			}

			authorEditorAccess.setCaretPosition(caretOffset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		logger.debug("================ end insert operation ============================\n");

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
		return "Executes an XQuery Update insert operation.";
	}
}
