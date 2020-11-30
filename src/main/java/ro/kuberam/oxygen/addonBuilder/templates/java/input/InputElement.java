package ro.kuberam.oxygen.addonBuilder.templates.java.input;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InputElement extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1319456194128007119L;

	public JComboBox<String> comboBox;
	public JTextField editor;
	public List<String> values = new ArrayList<>();

	// the current constructor is for 'input' with @list
	public InputElement() {
		comboBox = new JComboBox<String>();
		add(comboBox);
	}

	public void initialize(final String[] datalistValues, int width) {
		comboBox.setModel(new DefaultComboBoxModel<String>(datalistValues));
		comboBox.setSelectedIndex(-1);
		comboBox.setEditable(true);
		comboBox.setPreferredSize(new Dimension(width, comboBox.getPreferredSize().height));
//		comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
//		comboBox.setFocusTraversalKeysEnabled(true);
		
		editor = (JTextField) comboBox.getEditor().getEditorComponent();
		editor.setText("");
		ComboboxKeyHandler comboboxKeyHandler = new ComboboxKeyHandler(comboBox, datalistValues);
		values = comboboxKeyHandler.values;
		editor.addKeyListener(comboboxKeyHandler);
		editor.addFocusListener(new CursorAtStartFocusListener());
		// field.addFocusListener(new FocusListener() {
		// public void focusGained(FocusEvent arg0) {
		// }
		//
		// public void focusLost(FocusEvent ev) {
		// String editedValue = field.getText();
		//
		// System.out.println("field.getText() = " + field.getText());
		// if (!values.contains(editedValue)) {
		// comboBox.setModel(new DefaultComboBoxModel<String>(datalistValues));
		// comboBox.setSelectedIndex(-1);
		// comboBox.setSelectedItem("");
		// } else {
		// comboBox.setSelectedItem(editedValue);
		// }
		// }
		// });
	}

	public String getSelectedItem() {
		return String.valueOf(comboBox.getSelectedItem());
	}

	public void setValue(String value) {
		comboBox.setSelectedItem((String) value);
	}

	public int getBaseline(int width, int height) {
		int baseline = comboBox.getBaseline(width, height);

		// When the combo has no initial item selected, the baseline is -1
		// Try to compute it after a prototype display value was set
		if (baseline <= 0) {
			Object initialPrototypeDV = comboBox.getPrototypeDisplayValue();
			comboBox.setPrototypeDisplayValue(" ");
			baseline = comboBox.getBaseline(width, height);
			comboBox.setPrototypeDisplayValue((String) initialPrototypeDV);
		}

		return baseline;

	}
}
