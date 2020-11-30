package ro.kuberam.oxygen.addonBuilder.templates.java.select;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class SelectElement extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JList<Object> list;

	public SelectElement() {
		list = new JList<Object>();
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		add(new JScrollPane(list));
	}

	public void setValues(String values) {
		String[] valueObject = values.split(" ");
		DefaultListModel<Object> listModel = new DefaultListModel<Object>();
		for (int i = 0; i < valueObject.length; i++) {
			listModel.add(i, valueObject[i]);
		}

		list.setModel(listModel);
	}

	public String getSelectedValues() {
		StringBuilder result = new StringBuilder();

		for (Object value : list.getSelectedValuesList()) {
			result.append(value + " ");
		}
		return result.toString().trim();
	}

	public void setVisibleRowCount(int rowCount) {
		list.setVisibleRowCount(rowCount);
	}

	public static void main(String[] args) {
		JFrame parentFrame = new JFrame();
		parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final SelectElement selectElement = new SelectElement();
		selectElement.setValues("á é í ó ú ắ ấ î́");
		selectElement.setVisibleRowCount(4);

		JButton button = new JButton("Căutare");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(selectElement.getSelectedValues());
			}
		});

		parentFrame.setLayout(new FlowLayout());
		parentFrame.setPreferredSize(new Dimension(700, 200));
		parentFrame.add(selectElement);
		parentFrame.add(button);
		parentFrame.pack();
		parentFrame.setLocation(200, 200);
		parentFrame.setVisible(true);

	}
}