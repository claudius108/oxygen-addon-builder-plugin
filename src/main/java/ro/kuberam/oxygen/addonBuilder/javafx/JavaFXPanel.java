package ro.kuberam.oxygen.addonBuilder.javafx;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import javax.swing.JOptionPane;

import netscape.javascript.JSObject;
import ro.kuberam.oxygen.addonBuilder.javafx.bridges.BaseBridge;

public class JavaFXPanel extends JFXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3574628538755271115L;

	JFXPanel panel;

	public JavaFXPanel(final String content, final String contentUrl, final BaseBridge bridge,
			final String jsObjectName) {

		panel = this;

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final WebView webView = new WebView();
				final WebEngine webEngine = webView.getEngine();
				bridge.webEngine = webEngine;

				webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
					@Override
					public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
						if (newState == State.SUCCEEDED) {
							JSObject window = (JSObject) webEngine.executeScript("window");
							window.setMember(jsObjectName, bridge);

							webEngine.executeScript("console.log = function(message) {" + jsObjectName
									+ ".log(message);};");

							JSObject jdoc = (JSObject) webEngine.getDocument();
						}
					}
				});

				webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
					@Override
					public void handle(WebEvent<String> event) {
						JOptionPane.showMessageDialog(null, event.getData(), "",
								JOptionPane.WARNING_MESSAGE);

					}
				});

				webEngine.setConfirmHandler(new Callback<String, Boolean>() {
					public Boolean call(String message) {
						boolean response = true;
						Object[] options = { "OK", "Cancel" };
						int option = JOptionPane.showOptionDialog(null, message, "",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
						if (option == 1) {
							response = false;
						}

						return response;
					}
				});

				webEngine.setPromptHandler(new Callback<PromptData, String>() {
					@Override
					public String call(PromptData message) {
						String response = JOptionPane.showInputDialog(null, message.getMessage(), "", JOptionPane.PLAIN_MESSAGE);
						
						return response;
					}
				});

				if (!content.equals("")) {
					webEngine.loadContent(content);
				} else if (!contentUrl.equals("")) {
					webEngine.load(contentUrl);
				}

				Scene scene = new Scene(webView);
				panel.setScene(scene);
			}
		});

	}
}
