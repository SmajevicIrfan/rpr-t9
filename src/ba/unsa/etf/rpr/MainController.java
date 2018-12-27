package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public Label title;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void displayAllCities(ActionEvent event) throws IOException {
        Stage displayPopup = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/displayList.fxml"));
        Parent root = loader.load();
        DisplayListController controller = loader.getController();

        displayPopup.setTitle("Izlistavanje svih gradova");

        displayPopup.setScene(new Scene(root));
        displayPopup.initOwner(title.getScene().getWindow());

        displayPopup.show();

        GeografijaDAO geografijaDAO = GeografijaDAO.getInstance();
        System.out.println(geografijaDAO);
        controller.setItems(geografijaDAO.gradovi());
    }
}
