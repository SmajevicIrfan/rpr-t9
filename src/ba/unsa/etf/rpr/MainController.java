package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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

        Thread thread = new Thread(() -> {
            GeografijaDAO geografijaDAO = GeografijaDAO.getInstance();
            Platform.runLater(() -> {
                controller.setItems(geografijaDAO.gradovi());
            });
        });
        thread.start();
    }

    public void findCountry(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Unos imena države");
        dialog.setHeaderText(null);
        dialog.setContentText("Unesite ime države: ");

        final Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pronađena država");
            alert.setHeaderText(null);

            Drzava drzava = GeografijaDAO.getInstance().nadjiDrzavu(result.get());
            alert.setContentText(
                    String.format("Ime države: %s\nIme glavnog grada: %s", drzava.getNaziv(), drzava.getGlavniGrad().getNaziv())
            );

            alert.show();
        }
    }
}
