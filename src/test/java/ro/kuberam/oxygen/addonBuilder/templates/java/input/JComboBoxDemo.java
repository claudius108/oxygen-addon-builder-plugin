package ro.kuberam.oxygen.addonBuilder.templates.java.input;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class JComboBoxDemo implements Runnable {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new JComboBoxDemo());
	}

	public void run() {
		JComboBox comboBox = new JComboBox(new String[] { "A", "B", "C" });
		comboBox.setEditable(true);

		final JTextField editorComponent = (JTextField) comboBox.getEditor().getEditorComponent();
		editorComponent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(editorComponent.getText());
//				editorComponent.transferFocus();
			}
		});

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("Field 1"));
		panel.add(comboBox);
		panel.add(new JLabel("Field 2"));
		panel.add(new JTextField(10));
		panel.add(new JLabel("Field 3"));
		panel.add(new JTextField(10));

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		Container c = frame.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}
}
