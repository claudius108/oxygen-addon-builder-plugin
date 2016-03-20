package ro.kuberam.oxygen.addonBuilder.templates.java.richTextArea;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ro.kuberam.oxygen.addonBuilder.templates.java.richTextArea.RichTextAreaElement;

public class RichTextAreaElement extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7269401194139271662L;
	public JPanel componentPanel;
	public JTextArea textArea;

	private static String INSERT_CHAR_COMMAND = "insert-char";
	private ActionListener defaultAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			int caretPosition = textArea.getCaretPosition();
			textArea.insert(source.getName(), caretPosition);
		}
	};
	public JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	/**
	 * Constructor.
	 */
	public RichTextAreaElement() {
		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.PAGE_START);

		// define the textarea itself
		this.textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		JScrollPane textAreaPanel = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(textAreaPanel, BorderLayout.CENTER);
	}

	public void addInsertCharButton(String insertCharsString) {
		// define annotators for inserting special characters
		if (insertCharsString.length() == 0) {
			return;
		}
		
		String[] insertChars = insertCharsString.split(",");

		for (int i = 0, il = insertChars.length; i < il; i++) {
			String charString = new String(insertChars[i]);
			JButton button = new JButton(charString);
			button.setName(charString);
			button.setPreferredSize(new Dimension(45, 25));
			button.setActionCommand(INSERT_CHAR_COMMAND);
			button.addActionListener(defaultAction);
			buttonPanel.add(button, BorderLayout.PAGE_START);
		}

	}

	public static void main(String[] args) {
		JFrame parentFrame = new JFrame();
		parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		RichTextAreaElement richTextArea = new RichTextAreaElement();
		richTextArea.addInsertCharButton("á,é,í,ó,ú,ắ,ấ,î́");

		parentFrame.setPreferredSize(new Dimension(700, 200));
		parentFrame.add(richTextArea);
		parentFrame.pack();
		parentFrame.setLocation(200, 200);
		parentFrame.setVisible(true);

	}

}
