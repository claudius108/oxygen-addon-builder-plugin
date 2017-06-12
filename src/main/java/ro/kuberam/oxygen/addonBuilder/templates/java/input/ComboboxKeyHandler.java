package ro.kuberam.oxygen.addonBuilder.templates.java.input;

import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class ComboboxKeyHandler extends KeyAdapter {
	private final JComboBox<String> comboBox;
	private boolean shouldHide;
	private String[] datalistValues;
	public List<String> values = null;	

	public ComboboxKeyHandler(JComboBox<String> comboBox, String[] datalistValues) {
		super();
		this.comboBox = comboBox;
		values = Arrays.asList(datalistValues);
	}

	@Override
	public void keyTyped(final KeyEvent event) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				String text = ((JTextField) event.getComponent()).getText();
				ComboBoxModel<String> model;

				if (text.isEmpty()) {
					String[] array = values.toArray(new String[values.size()]);
					model = new DefaultComboBoxModel<String>(array);
					setSuggestionModel(comboBox, model, "");
					comboBox.hidePopup();
				} else {
					model = getSuggestedModel(values, text);
					if (model.getSize() == 0 || shouldHide) {
						comboBox.hidePopup();
					} else {
						setSuggestionModel(comboBox, model, text);
						comboBox.showPopup();
					}
				}
			}
		});
	}

	@Override
	public void keyPressed(KeyEvent event) {
		JTextField textField = (JTextField) event.getComponent();
		String editedValue = textField.getText();
		shouldHide = false;

		if (event.getKeyCode() == KeyEvent.VK_ENTER) {
			if (values.contains(editedValue)) {
				comboBox.setSelectedItem(editedValue);
			} else {
				comboBox.setModel(new DefaultComboBoxModel<String>());
				comboBox.setSelectedIndex(-1);
				comboBox.setSelectedItem("");
			}

			System.out.println("event = " + String.valueOf(comboBox.getSelectedItem()));
			// comboBox.dispatchEvent(new ItemEvent((ItemSelectable)
			// event.getComponent(), ItemEvent.ITEM_STATE_CHANGED,
			// comboBox.getSelectedItem(), ItemEvent.SELECTED));

			shouldHide = true;
			comboBox.requestFocus();
//			textField.transferFocus();
		}
	}

	private static void setSuggestionModel(JComboBox<String> comboBox, ComboBoxModel<String> model,
			String str) {
		comboBox.setModel(model);
		comboBox.setSelectedIndex(-1);
		((JTextField) comboBox.getEditor().getEditorComponent()).setText(str);
	}

	private static ComboBoxModel<String> getSuggestedModel(List<String> list, String text) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

		for (String item : list) {
			if (item.toLowerCase().contains(text.toLowerCase())) {
				model.addElement(item);
			}
		}

		return model;
	}
}
