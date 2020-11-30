package ro.kuberam.oxygen.addonBuilder.templates.java.tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import javafx.embed.swing.JFXPanel;
import ro.kuberam.oxygen.addonBuilder.SerialisedObjects;
import ro.kuberam.oxygen.addonBuilder.javafx.FXML2JavaFX;
import ro.kuberam.oxygen.addonBuilder.javafx.JavaFXPanel;
import ro.kuberam.oxygen.addonBuilder.javafx.bridges.ui.UserInterfaceBridge;
import ro.kuberam.oxygen.addonBuilder.operations.XQueryOperation;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.editor.InplaceEditingListener;
import ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter;
import ro.sync.ecss.extensions.api.editor.RendererLayoutInfo;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;

/**
 * A simple text area based form control.
 */

public class TreeFormControl extends InplaceEditorRendererAdapter {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(TreeFormControl.class.getName());

	/**
	 * Access to the author specific functions.
	 */
	private AuthorAccess authorAccess;
	private AuthorEditorAccess authorEditorAccess;
	private List<InplaceEditingListener> listeners = new ArrayList<InplaceEditingListener>();
	private JPanel componentPanel = new JPanel();
	private ArrayList<String> generatedTreeIds = new ArrayList<String>();

	/**
	 * Constructor.
	 * 
	 * @param access
	 *            Author access.
	 * @throws BadLocationException
	 */
	public TreeFormControl() {
		// logger.debug("componentPanel = " + componentPanel);
		componentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

		String content;
		try (Scanner scanner = new Scanner(TreeFormControl.class.getResourceAsStream("tree-example.html"), "UTF-8")) {
			content = scanner.useDelimiter("\\A").next();
		}

		System.out.println(content);

		// logger.debug("content = " + content);

		JFXPanel treePanel = new JavaFXPanel(content, "", new UserInterfaceBridge(), "UserInterfaceBridge");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// logger.debug("treePanel = " + treePanel);

		componentPanel.add(treePanel);
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#getEditorComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext,
	 *      ro.sync.exml.view.graphics.Rectangle, ro.sync.exml.view.graphics.Point)
	 */
	@Override
	public Object getEditorComponent(AuthorInplaceContext context, Rectangle allocation, Point mouseLocation) {
		prepareComponent(context);

		return componentPanel;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#getValue()
	 */
	@Override
	public Object getValue() {
		return null;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#stopEditing()
	 */
	@Override
	public void stopEditing() {
		listeners.get(0).editingCanceled();
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#addEditingListener(ro.sync.ecss.extensions.api.editor.InplaceEditingListener)
	 */
	@Override
	public void addEditingListener(InplaceEditingListener editingListener) {
		listeners.add(editingListener);
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#removeEditingListener(ro.sync.ecss.extensions.api.editor.InplaceEditingListener)
	 */
	@Override
	public void removeEditingListener(InplaceEditingListener editingListener) {
		listeners.remove(editingListener);
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#getTooltipText(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext,
	 *      int, int)
	 */
	@Override
	public String getTooltipText(AuthorInplaceContext context, int x, int y) {
		return "";
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getRendererComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext)
	 */
	@Override
	public Object getRendererComponent(AuthorInplaceContext context) {
		prepareComponent(context);

		return componentPanel;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getRenderingInfo(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext)
	 */
	@Override
	public RendererLayoutInfo getRenderingInfo(AuthorInplaceContext context) {
		int width = Integer.parseInt(context.getArguments().get("columns").toString());
		int height = Integer.parseInt(context.getArguments().get("rows").toString());

		ro.sync.exml.view.graphics.Dimension size = new ro.sync.exml.view.graphics.Dimension(width, height);

		return new RendererLayoutInfo(componentPanel.getBaseline(width, height), size);
	}

	/**
	 * Initialize the tree.
	 * 
	 * @throws AuthorOperationException
	 * @throws BadLocationException
	 */
	private void prepareComponent(final AuthorInplaceContext context) {

		this.authorAccess = context.getAuthorAccess();
		this.authorEditorAccess = authorAccess.getEditorAccess();

		String treeGeneratorTemplateId = (String) context.getArguments().get("treeGeneratorTemplateId");
		// logger.debug("treeGeneratorTemplateId = " + treeGeneratorTemplateId);

		if (generatedTreeIds.contains(treeGeneratorTemplateId)) {
			// logger.debug("template '" + treeGeneratorTemplateId + "' was already
			// generated");
		} else {
			generatedTreeIds.add(treeGeneratorTemplateId);

			String treeTemplate = SerialisedObjects.templates.get(treeGeneratorTemplateId);
			// logger.debug("treeTemplate = " + treeTemplate);

			String content = XQueryOperation.query(authorEditorAccess.createContentReader(),
					new ByteArrayInputStream(treeTemplate.getBytes(StandardCharsets.UTF_8)), true, null,
					new HashMap<String, String>()).itemAt(0).toString();

			// logger.debug("content = " + content);

			JFXPanel treePanel = null;
			try {
				treePanel = new JavaFXPanel(content, "", new UserInterfaceBridge(), "UserInterfaceBridge");
				Thread.sleep(1000);
			} catch (Exception e) {
				logger.debug("e1.printStackTrace() = " + e.getMessage());
			}

			logger.debug("treePanel = " + treePanel.getParent());

			// logger.debug("treePanel = " + treePanel);

			componentPanel.add(treePanel);

			logger.debug("treePanel = " + treePanel.getParent());
		}
	}

	public boolean insertContent(String arg0) {
		return false;
	}

	public void refresh(AuthorInplaceContext arg0) {
	}

	public static void main(String args[]) throws Exception {
		String content;
		try (Scanner scanner = new Scanner(TreeFormControl.class.getResourceAsStream("tree-example.html"), "UTF-8")) {
			content = scanner.useDelimiter("\\A").next();
		}

		System.out.println(content);

		JavaFXPanel componentPanel = new JavaFXPanel(content, "", new UserInterfaceBridge(), "UserInterfaceBridge");

		JFrame frame = new JFrame();

		frame.setLayout(new BorderLayout());
		frame.setPreferredSize(new Dimension(1100, 350));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(componentPanel);
		frame.setLocation(200, 200);
		frame.pack();

		frame.setVisible(true);
	}
}
