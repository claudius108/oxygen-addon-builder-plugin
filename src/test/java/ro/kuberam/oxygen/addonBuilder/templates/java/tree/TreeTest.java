package ro.kuberam.oxygen.addonBuilder.templates.java.tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.InputStreamReader;

import javax.swing.JFrame;

import net.sf.saxon.s9api.SaxonApiUncheckedException;

import org.junit.Ignore;
import org.junit.Test;

import ro.kuberam.oxygen.addonBuilder.javafx.JavaFXPanel;
import ro.kuberam.oxygen.addonBuilder.javafx.bridges.ui.UserInterfaceBridge;
import ro.kuberam.oxygen.addonBuilder.operations.XQueryOperation;

public class TreeTest {

	@Ignore
	@Test
	public void testGenerateTreeDatasource() {
		String content = null;

		try {
			content = XQueryOperation
					.query(new InputStreamReader(TreeTest.class.getResourceAsStream("01.xml")),
							TreeTest.class.getResourceAsStream("tree-template.xq"), true, null).itemAt(0)
					.toString();
		} catch (IndexOutOfBoundsException | SaxonApiUncheckedException e) {
			e.printStackTrace();
		}

		System.out.println(content);

		JavaFXPanel componentPanel = new JavaFXPanel(content, "", new UserInterfaceBridge(),
				"UserInterfaceBridge");

		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame();

		frame.setLayout(new BorderLayout());
		frame.setPreferredSize(new Dimension(700, 700));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(componentPanel);
		frame.setLocation(200, 200);
		frame.pack();

		frame.setVisible(true);
	}
}
