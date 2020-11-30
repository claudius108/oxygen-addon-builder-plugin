package ro.kuberam.oxygen.addonBuilder.templates.java.input;

import java.awt.event.FocusAdapter;

import javax.swing.text.JTextComponent;

public class CursorAtStartFocusListener extends FocusAdapter {

	@Override
	public void focusGained(java.awt.event.FocusEvent evt) {
		Object source = evt.getSource();
		if (source instanceof JTextComponent) {
			JTextComponent comp = (JTextComponent) source;
			comp.setCaretPosition(0);
			comp.selectAll();
		}
	}
}