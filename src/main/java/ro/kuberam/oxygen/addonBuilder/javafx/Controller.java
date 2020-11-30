package ro.kuberam.oxygen.addonBuilder.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Controller {
    @FXML
    private TextField textField ;

    @FXML
    private void printMessage() {
        System.out.println(textField.getText());
    }
    
    @FXML
    private WebView webview ;

    @FXML
    private WebEngine webengine ;

    public void initialize() {
        this.webengine.load("http://www.oracle.com/us/products/index.html");
    }
}
