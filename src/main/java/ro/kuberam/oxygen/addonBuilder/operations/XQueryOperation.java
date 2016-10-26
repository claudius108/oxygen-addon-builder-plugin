package ro.kuberam.oxygen.addonBuilder.operations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Iterator;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import net.sf.saxon.om.TreeModel;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;
import net.sf.saxon.s9api.XdmSequenceIterator;
import net.sf.saxon.s9api.XdmValue;

public class XQueryOperation {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(XQueryOperation.class.getName());

	public static XdmValue query(Reader xml, InputStream xquery, boolean omitXmlDeclaration, URI baseURI) {
		XdmValue result = null;

		Source xmlSrc = new StreamSource(xml);

		Processor proc = new Processor(true);
		XQueryCompiler comp = proc.newXQueryCompiler();

		if (baseURI != null) {
			comp.setBaseURI(baseURI);
		}

		XQueryExecutable exp;
		try {
			exp = comp.compile(xquery);
			XQueryEvaluator qe = exp.load();
			qe.setSource(xmlSrc);
			result = qe.evaluate();
		} catch (SaxonApiException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;

	}

	public static void update(File xml, File query) {
		Processor processor = new Processor(true);

		DocumentBuilder builder = processor.newDocumentBuilder();
		builder.setTreeModel(TreeModel.LINKED_TREE);
		XdmNode source = null;
		try {
			source = builder.build(xml);
		} catch (SaxonApiException e2) {
			e2.printStackTrace();
		}

		XQueryCompiler comp = processor.newXQueryCompiler();
		comp.setUpdatingEnabled(true);
		// comp.setBaseURI(addonDirectory.toURI());

		XQueryExecutable exp;
		XQueryEvaluator eval = null;
		try {
			exp = comp.compile(query);

			eval = exp.load();
			eval.setContextItem(source);
			eval.run();
		} catch (SaxonApiException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Iterator<XdmNode> iter = eval.getUpdatedDocuments(); iter.hasNext();) {
			XdmNode root = iter.next();
			URI rootUri = root.getDocumentURI();
			if (rootUri != null && rootUri.getScheme().equals("file")) {
				Serializer out = new Serializer();
				out.setOutputProperty(Serializer.Property.METHOD, "xml");
				out.setOutputProperty(Serializer.Property.INDENT, "yes");
				out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
				try {
					logger.debug("Rewriting " + rootUri);
					out.setOutputStream(new FileOutputStream(new File(rootUri)));
					processor.writeXdmValue(root, out);
				} catch (FileNotFoundException e) {
					logger.debug("Could not write to file " + rootUri);
				} catch (SaxonApiException e) {
					e.printStackTrace();
				}
			} else {
				logger.debug("Updated document not rewritten: location unknown or not updatable");
			}
		}

	}

	public static XdmNode getDocumentElement(XdmNode documentNode) {
		XdmNode documentElement = null;
		XdmSequenceIterator iter = documentNode.axisIterator(Axis.CHILD);
		if (iter.hasNext()) {
			XdmNode child = (XdmNode) iter.next();
			if (child.getNodeKind() == XdmNodeKind.ELEMENT) {
				documentElement = child;
			}
		}

		return documentElement;
	}

}
