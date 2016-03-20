package ro.kuberam.oxygen.addonBuilder.templates.java.select;

import ro.sync.ecss.extensions.api.CursorType;
import ro.sync.ecss.extensions.api.editor.AbstractInplaceEditor;
import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.editor.InplaceRenderer;
import ro.sync.ecss.extensions.api.editor.RendererLayoutInfo;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;

/**
 * A select form control.
 * 
 * @author Claudius Teodorescu
 */
public class SelectFormControl extends AbstractInplaceEditor implements InplaceRenderer {

	/**
	 * Select element.
	 */
	SelectElement selectElement;

	/**
	 * Constructor.
	 */
	public SelectFormControl() {
		selectElement = new SelectElement();

	}

	/**
	 * @see ro.sync.ecss.extensions.api.Extension#getDescription()
	 */
	@Override
	public String getDescription() {
		return "A select element.";
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getRendererComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext)
	 */
	@Override
	public Object getRendererComponent(AuthorInplaceContext context) {
		prepareComponent(context);

		return selectElement;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getRenderingInfo(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext)
	 */
	@Override
	public RendererLayoutInfo getRenderingInfo(AuthorInplaceContext context) {
		prepareComponent(context);

		int cols = 400;//Integer.parseInt(context.getArguments().get("cols").toString());
		int rows = 18 * Integer.parseInt(context.getArguments().get("size").toString()) + 10;
		cols = (cols > 0) ? cols : 30;

		ro.sync.exml.view.graphics.Dimension size = new ro.sync.exml.view.graphics.Dimension(cols, rows);

		return new RendererLayoutInfo(selectElement.getBaseline(cols, rows), size);
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#getEditorComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext,
	 *      ro.sync.exml.view.graphics.Rectangle,
	 *      ro.sync.exml.view.graphics.Point)
	 */
	@Override
	public Object getEditorComponent(AuthorInplaceContext context, Rectangle allocation, Point mouseLocation) {
		prepareComponent(context);

		return selectElement;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.editor.InplaceEditorRendererAdapter#getValue()
	 */
	@Override
	public Object getValue() {
		return selectElement.getSelectedValues();
	}

	/**
	 * Prepare UI components.
	 * 
	 * @param context
	 *            The current context.
	 */
	private void prepareComponent(AuthorInplaceContext context) {
		String values = (String) context.getArguments().get("valuesAsString").toString();
		selectElement.setValues(values);
		selectElement.setVisibleRowCount(Integer.parseInt(context.getArguments().get("size").toString()));
	}

	@Override
	public void refresh(AuthorInplaceContext arg0) {
	}

	@Override
	public void cancelEditing() {
		fireEditingCanceled();
	}

	@Override
	public Rectangle getScrollRectangle() {
		return null;
	}

	@Override
	public void requestFocus() {
		selectElement.requestFocus();
	}

	@Override
	public void stopEditing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CursorType getCursorType(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CursorType getCursorType(AuthorInplaceContext arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTooltipText(AuthorInplaceContext arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}
}
