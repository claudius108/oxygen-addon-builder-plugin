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
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.util.editorvars.EditorVariables;

public class ReplaceOperation implements AuthorOperation {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(ReplaceOperation.class.getName());

	/**
	 * The choice for the operation. The value is <code>string</code> .
	 */
	protected String ARGUMENT_ACTION = "replaceAction";

	/**
	 * The source expression for the operation. The value is <code>script</code>
	 * .
	 */
	protected String ARGUMENT_SOURCE_LOCATION = "replaceSourceLocation";

	/**
	 * The target expression for the operation. The value is <code>script</code>
	 * .
	 */
	protected String ARGUMENT_TARGET_LOCATION = "replaceTargetLocation";

	/**
	 * The arguments of the operation.
	 */
	private ArgumentDescriptor[] arguments = null;

	/**
	 * Constructor.
	 */
	public ReplaceOperation() {
		arguments = new ArgumentDescriptor[3];

		ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(ARGUMENT_ACTION, ArgumentDescriptor.TYPE_STRING,
				"The choice for the operation.");
		arguments[0] = argumentDescriptor;
		argumentDescriptor = new ArgumentDescriptor(ARGUMENT_SOURCE_LOCATION, ArgumentDescriptor.TYPE_STRING,
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
		logger.debug("================ start replace operation ============================");

		AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();
		AuthorDocumentController authorDocumentController = authorAccess.getDocumentController();

		// the action object
		Object actionObj = args.getArgumentValue(ARGUMENT_ACTION);

		// The sourceLocation object
		Object sourceExprObj = args.getArgumentValue(ARGUMENT_SOURCE_LOCATION);

		// The targetLocation object
		Object targetExprObj = args.getArgumentValue(ARGUMENT_TARGET_LOCATION);

		String targetChoice = (String) actionObj;
		logger.debug("targetChoice: " + targetChoice);
		String sourceExpr = (String) sourceExprObj;
		logger.debug("sourceExpr: " + sourceExpr);
		String targetExpr = (String) targetExprObj;

		try {
			int caretOffset = authorEditorAccess.getCaretOffset();
			logger.debug("caretOffset = " + caretOffset);
			AuthorNode currentNode = authorDocumentController.getNodeAtOffset(caretOffset);
			logger.debug("currentNode = " + currentNode);
			String currentNodeXpathExpr = authorDocumentController.getXPathExpression(caretOffset);
			logger.debug("currentNodeXpathExpr = " + currentNodeXpathExpr);

			// int caretOffset = authorEditorAccess.getCaretOffset();
			// logger.debug("caret offset: " + caretOffset);
			// OffsetInformation contentInformationAtOffset = null;
			// try {
			// contentInformationAtOffset =
			// authorDocumentController.getContentInformationAtOffset(caretOffset);
			// } catch (BadLocationException e2) {
			// e2.printStackTrace();
			// }
			// logger.debug("contentInformationAtOffset: " +
			// contentInformationAtOffset.getNodeForOffset().getName());
			//
			// AuthorNode nodeForMarkerOffset =
			// contentInformationAtOffset.getNodeForMarkerOffset();
			// logger.debug("nodeForMarkerOffset: " + nodeForMarkerOffset);
			//
			// AuthorNode currentNode = null;
			// String currentNodeXpathExpr = null;
			//
			// try {
			// if (nodeForMarkerOffset != null) {
			// currentNode = nodeForMarkerOffset;
			// logger.debug("current node is readonly");
			// currentNodeXpathExpr =
			// XML.getXPathExpression(authorDocumentController,
			// (AuthorParentNode)
			// currentNode);
			// } else {
			// currentNode =
			// authorDocumentController.getNodeAtOffset(authorEditorAccess.getSelectionStart());
			// currentNodeXpathExpr =
			// XML.getXPathExpression(authorDocumentController,
			// authorEditorAccess.getSelectionStart());
			// }
			// logger.debug("current node name: " + currentNode.getName());
			// logger.debug("currentNode XPath expression = " +
			// currentNodeXpathExpr);
			// } catch (BadLocationException e) {
			// e.printStackTrace();
			// }

			sourceExpr = sourceExpr.replace("$ua:context", currentNodeXpathExpr).trim();
			sourceExpr = authorAccess.getUtilAccess().expandEditorVariables(sourceExpr, null);
			sourceExpr = XQuery.completeXqueryScript(sourceExpr);
			logger.debug("sourceExpr: " + sourceExpr);
			
			targetExpr = targetExpr.replace("$ua:context", currentNodeXpathExpr);
			targetExpr = XML.completeXpathExpression(targetExpr);
			logger.debug("targetExpr: " + targetExpr);

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
			logger.debug("sourceSequence.size(): " + sourceSequence.size());
			logger.debug("processedSourceExpr: " + sourceSequence.getUnderlyingValue().toString());

			// generate $rlist list
			ArrayList<XdmItem> rlist = new ArrayList<XdmItem>();
			int nonAttributesNodesNumber = 0;
			int attributesNodesNumber = 0;
			for (int i = 0; i < sourceSequence.size(); i++) {
				XdmItem item = sourceSequence.itemAt(i);
				logger.debug("item: " + item.toString());

				if (item.isAtomicValue()) {
					++nonAttributesNodesNumber;
					rlist.add(item);
				} else {
					XdmNode node = (XdmNode) item;
					String nodeKind = node.getNodeKind().name();
					logger.debug("nodeKind: " + nodeKind);

					switch (nodeKind) {
					case "ELEMENT":
					case "TEXT":
					case "COMMENT":
					case "PROCESSING_INSTRUCTION":
						++nonAttributesNodesNumber;
						rlist.add(item);
						break;
					case "ATTRIBUTE":
						++attributesNodesNumber;
						rlist.add(item);
						break;
					case "DOCUMENT":
						++nonAttributesNodesNumber;
						rlist.add(XQueryOperation.getDocumentElement(node));
						break;
					}
				}
			}
			logger.debug("rlist: " + rlist);

			Object[] targetObjects = authorDocumentController.evaluateXPath(targetExpr, currentNode, false, true, true,
					false, XPathVersion.XPATH_3_0);

			if (targetObjects.length == 0) {
				throw new AuthorOperationException(ErrorMessages.err_XUDY0027);
			}

			Object targetObject = targetObjects[0];
			logger.debug("targetObject: " + targetObject);

			String targetKind = UpdatePrimitives.getOxygenNodeKind(targetObject);
			logger.debug("targetKind: " + targetKind);

			AuthorNode parent = UpdatePrimitives.getOxygenParentNode(targetObject, targetKind);
			logger.debug("parent: " + parent);

			if (targetObjects.length > 1
					|| !"ELEMENT ATTRIBUTE TEXT COMMENT PROCESSING_INSTRUCTION".contains(targetKind)) {
				throw new AuthorOperationException(ErrorMessages.err_XUTY0008);
			}

			// replace node ...
			if (targetChoice.equals("After")) {
				if (parent == null) {
					throw new AuthorOperationException(ErrorMessages.err_XUDY0009);
				}

				if ("ELEMENT TEXT COMMENT PROCESSING_INSTRUCTION".contains(targetKind) && (attributesNodesNumber > 0)) {
					throw new AuthorOperationException(ErrorMessages.err_XUTY0010);
				}

				if (targetKind.equals("ATTRIBUTE")) {
					if (nonAttributesNodesNumber > 0) {
						throw new AuthorOperationException(ErrorMessages.err_XUTY0011);
					}
				}

				UpdatePrimitives.replaceNode(targetObject, rlist, authorDocumentController, targetKind);
			}

			// replace value of node ...
			if (targetChoice.equals("Before")) {
				StringBuilder textSb = new StringBuilder();
				String delim = "";

				for (int i = 0; i < rlist.size(); i++) {
					XdmItem item = rlist.get(i);
					textSb.append(delim);

					if (item.isAtomicValue()) {
						textSb.append(item.getStringValue());
					} else {
						XdmNode node = (XdmNode) item;
						String nodeKind = node.getNodeKind().name();
						logger.debug("nodeKind: " + nodeKind);

						switch (nodeKind) {
						case "TEXT":
							textSb.append(node.getStringValue());
							break;
						case "ELEMENT":
							textSb.append(node.getUnderlyingNode().getStringValue());
							break;
						}
					}

					delim = "";
				}

				String text = textSb.toString();
				logger.debug("text: " + text);

				if (targetKind.equals("ELEMENT")) {
					UpdatePrimitives.replaceElementContent(targetObject, text, authorDocumentController, targetKind);
				}

				if ("ATTRIBUTE TEXT COMMENT PROCESSING_INSTRUCTION".contains(targetKind)) {
					UpdatePrimitives.replaceValue(targetObject, text, authorDocumentController, targetKind, parent);
				}
			}

			authorEditorAccess.setCaretPosition(caretOffset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		logger.debug("================ end replace operation ============================\n");
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
