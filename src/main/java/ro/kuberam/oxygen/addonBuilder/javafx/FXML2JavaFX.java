package ro.kuberam.oxygen.addonBuilder.javafx;

import java.io.IOException;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ro.kuberam.oxygen.addonBuilder.javafx.bridges.BaseBridge;

public class FXML2JavaFX extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -679885858688312152L;
	JFXPanel panel;
	Scene scene;
	StackPane stack;
	Text hello;

	public FXML2JavaFX(final JFrame parent, final DialogModel dialogModel) {
		super(parent, dialogModel.title, (dialogModel.type == "modal") ? true : false);
		panel = new JFXPanel();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Parent root = null;
				try {
					root = FXMLLoader.load(getClass().getResource("FXComponents.fxml"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				Scene scene = new Scene(root, 250, 150);
				panel.setScene(scene);
			}
		});
		this.setSize(dialogModel.width, dialogModel.height);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.getContentPane().add(panel);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String content;
				try (Scanner scanner = new Scanner(
						FXML2JavaFX.class.getResourceAsStream("manage-framework-dialog.html"), "UTF-8")) {
					content = scanner.useDelimiter("\\A").next();
				}
				DialogModel dialogModel = new DialogModel("rich-textarea-dialog", "modal", "Dialog Title", 400, 320,
						"none", new String[] { "auto", "0", "auto", "auto" }, "", "OxygenAddonBuilder",
						new BaseBridge(), content);
				new FXML2JavaFX(new JFrame(), dialogModel);
			}
		});
	}

	final public class Bridge {

		private FXML2JavaFX dialogWindow;

		public Bridge(FXML2JavaFX dialogWindow) {
			this.dialogWindow = dialogWindow;
		}

		public void close() {
			dialogWindow.setVisible(false);
		}
	}
}
