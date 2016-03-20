/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2012 Syncro Soft SRL, Romania.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistribution of source or in binary form is allowed only with
 *  the prior written permission of Syncro Soft SRL.
 *
 *  2. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  3. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  4. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Syncro Soft SRL (http://www.sync.ro/)."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  5. The names "Oxygen" and "Syncro Soft SRL" must
 *  not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact support@oxygenxml.com.
 *
 *  6. Products derived from this software may not be called "Oxygen",
 *  nor may "Oxygen" appear in their name, without prior written
 *  permission of the Syncro Soft SRL.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE SYNCRO SOFT SRL OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 */
package ro.kuberam.oxygen.addonBuilder.templates.java.richTextArea;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ro.kuberam.oxygen.addonBuilder.templates.java.richTextArea.RichTextAreaElement;
import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.editor.EditingEvent;
import ro.sync.ecss.extensions.api.editor.InplaceEditingListener;
import ro.sync.ecss.extensions.api.editor.InplaceEditorArgumentKeys;
import ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter;
import ro.sync.ecss.extensions.api.editor.RendererLayoutInfo;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;

/**
 * A simple text area based form control.
 */

public class RichTextAreaFormControl extends InplaceEditorRendererAdapter {

	private List<InplaceEditingListener> listeners = new ArrayList<InplaceEditingListener>();
	private JTextArea textArea;
	private RichTextAreaElement componentPanel;
	/**
	 * Inhibits the firing of fake editing occurred events.
	 */
	private boolean fireEditOccurred = true;

	/**
	 * Constructor.
	 */
	public RichTextAreaFormControl() {

		componentPanel = new RichTextAreaElement();

		textArea = componentPanel.textArea;
		// We must fire editingOccured events. Otherwise the author document
		// will not be updated when the editing session finishes.
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (fireEditOccurred) {
					List<InplaceEditingListener> clone = new ArrayList<InplaceEditingListener>(listeners);
					for (InplaceEditingListener listener : clone) {
						listener.editingOccured();
					}
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				removeUpdate(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				removeUpdate(e);
			}
		});
	}

	/**
	 * @see ro.sync.ecss.extensions.api.Extension#getDescription()
	 */
	@Override
	public String getDescription() {
		return "A rich text area for annotating the content.";
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

		int cols = Integer.parseInt(context.getArguments().get("cols").toString());
		int rows = Integer.parseInt(context.getArguments().get("rows").toString());
		String insertChars = (String) context.getArguments().get("insertChars");
		rows = (insertChars.length() == 0) ? 30 : rows;
		

		ro.sync.exml.view.graphics.Dimension size = new ro.sync.exml.view.graphics.Dimension(cols, rows);

		return new RendererLayoutInfo(componentPanel.getBaseline(cols, rows), size);
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#getTooltipText(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext,
	 *      int, int)
	 */
	@Override
	public String getTooltipText(AuthorInplaceContext context, int x, int y) {
		prepareComponent(context);

		return textArea.getText();
	}

	// /////////////////////////// EDITOR METHODS //////////////////////

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#getEditorComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext,
	 *      ro.sync.exml.view.graphics.Rectangle,
	 *      ro.sync.exml.view.graphics.Point)
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
		return textArea.getText();
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#stopEditing()
	 */
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
		componentPanel.buttonPanel.removeAll();
		String insertChars = (String) context.getArguments().get("insertChars");
		componentPanel.addInsertCharButton(insertChars);

		String content = (String) context.getArguments().get(InplaceEditorArgumentKeys.INITIAL_VALUE);
		if (content == null) {
			content = "";
		}
		// We don't want to generate editingOccured events here, only on user
		// interaction.
		fireEditOccurred = false;
		try {
			textArea.setText(content);
		} finally {
			fireEditOccurred = true;
		}
	}

	@Override
	public boolean insertContent(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh(AuthorInplaceContext arg0) {
		// TODO Auto-generated method stub
		
	}

}
