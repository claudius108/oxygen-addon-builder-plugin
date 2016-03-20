package ro.kuberam.oxygen.addonBuilder.templates.java.input;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.editor.EditingEvent;
import ro.sync.ecss.extensions.api.editor.InplaceEditingListener;
import ro.sync.ecss.extensions.api.editor.InplaceEditorArgumentKeys;
import ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter;
import ro.sync.ecss.extensions.api.editor.RendererLayoutInfo;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;

/**
 * An input form control.
 * 
 * @author Claudius Teodorescu
 */
public class InputFormControl extends InplaceEditorRendererAdapter {

	/**
	 * Select element.
	 */
	private InputElement componentPanel;
	private JComboBox<String> comboBox;
	private List<InplaceEditingListener> listeners = new ArrayList<InplaceEditingListener>();
	/**
	 * Inhibits the firing of fake editing occurred events.
	 */
	private boolean fireEditOccurred = true;

	/**
	 * Constructor.
	 */
	public InputFormControl() {
		componentPanel = new InputElement();
		comboBox = componentPanel.comboBox;
		final JTextField editorComponent = (JTextField) comboBox.getEditor().getEditorComponent();

		editorComponent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				comboBox.setSelectedItem((String) editorComponent.getText());
				if (fireEditOccurred) {
					editorComponent.transferFocus();
					List<InplaceEditingListener> clone = new ArrayList<InplaceEditingListener>(listeners);
					for (InplaceEditingListener listener : clone) {
						listener.editingOccured();
					}
				}
			}
		});

		// comboBox.addItemListener(new ItemListener() {
		// public void itemStateChanged(ItemEvent event) {
		// if (event.getStateChange() == ItemEvent.SELECTED) {
		// if (fireEditOccurred) {
		// editorComponent.transferFocus();
		// List<InplaceEditingListener> clone = new
		// ArrayList<InplaceEditingListener>(
		// listeners);
		// for (InplaceEditingListener listener : clone) {
		// listener.editingOccured();
		// }
		// }
		// }
		//
		// }
		// });
	}

	/**
	 * @see ro.sync.ecss.extensions.api.Extension#getDescription()
	 */
	@Override
	public String getDescription() {
		return "An input element with @list.";
	}

	// /////////////////////////// RENDERER METHODS //////////////////////

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
		prepareComponent(context);

		int width = Integer.parseInt(context.getArguments().get("columns").toString());

		final java.awt.Dimension preferredSize = componentPanel.getPreferredSize();

		ro.sync.exml.view.graphics.Dimension size = new ro.sync.exml.view.graphics.Dimension(width,
				preferredSize.height);

		return new RendererLayoutInfo(componentPanel.getBaseline(width, preferredSize.height), size);
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#getTooltipText(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext,
	 *      int, int)
	 */
	@Override
	public String getTooltipText(AuthorInplaceContext context, int x, int y) {
		prepareComponent(context);

		return (String) getValue();
	}

	// ///////////////////////// EDITOR METHODS //////////////////////
	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#getEditorComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext,
	 *      ro.sync.exml.view.graphics.Rectangle,
	 *      ro.sync.exml.view.graphics.Point)
	 */
	@Override
	public Object getEditorComponent(final AuthorInplaceContext context, Rectangle allocation,
			Point mouseLocation) {
		prepareComponent(context);

		return componentPanel;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#getValue()
	 */
	@Override
	public Object getValue() {
		return componentPanel.getSelectedItem();
	}

	@Override
	public void stopEditing() {
		String value = (String) getValue();

		List<InplaceEditingListener> clone = new ArrayList<InplaceEditingListener>(listeners);
		for (InplaceEditingListener listener : clone) {
			listener.editingStopped(new EditingEvent(value));
		}
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
	 * Prepare UI components.
	 * 
	 * @param context
	 *            The current context.
	 */
	private void prepareComponent(AuthorInplaceContext context) {
		String values = (String) context.getArguments().get("datalist").toString();
		componentPanel.initialize(values.split(","),
				Integer.parseInt(context.getArguments().get("columns").toString()));

		String content = (String) context.getArguments().get(InplaceEditorArgumentKeys.INITIAL_VALUE);
		if (content == null) {
			content = "";
		}

		fireEditOccurred = false;
		try {
			componentPanel.setValue(content);
		} finally {
			fireEditOccurred = true;
		}
	}

	@Override
	public boolean insertContent(String arg0) {
		return false;
	}

	@Override
	public void refresh(AuthorInplaceContext arg0) {
	}
}
