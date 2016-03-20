package ro.kuberam.oxygen.addonBuilder.templates.java.input;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.awt.BorderLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class AutoCompleteCombobox {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			JFrame frame = new JFrame("TestFrame");

			JComboBox<String> comboBox = new JComboBox<>();
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
			model.addElement("First");
			model.addElement("Second");
			comboBox.setModel(model);
			comboBox.setEditable(true);

			AutoCompleteDecorator.decorate(comboBox);

			frame.getContentPane().add(comboBox);

			JButton button = new JButton("Add item");
			button.addActionListener(e -> {
				String selectedItem = (String) comboBox.getSelectedItem();
				if (comboBox.getSelectedIndex() == -1) {
					model.addElement(selectedItem);
				}
			});
			frame.getContentPane().add(button, BorderLayout.SOUTH);

			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setPreferredSize(new Dimension(700, 100));
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}
