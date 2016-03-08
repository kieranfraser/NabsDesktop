/**
 * Sample Skeleton for 'NabsDesktop.fxml' Controller Class
 */

package MastersProject.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

public class NabsDesktopController {

    @FXML // fx:id="androidImg"
    private ImageView androidImg; // Value injected by FXMLLoader

    @FXML // fx:id="helloBtn"
    private Button helloBtn; // Value injected by FXMLLoader

    @FXML // fx:id="consoleTextArea"
    private TextArea consoleTextArea; // Value injected by FXMLLoader

    @FXML
    void sayHello(ActionEvent event) {
    	consoleTextArea.setText("Button has been clicked!");
    }

}
