package ba.unsa.etf.rpr;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class DisplayListController {
    public ListView<Grad> displayList;

    public DisplayListController() {}

    public void setItems(ArrayList<Grad> gradovi) {
        displayList.setItems(FXCollections.observableArrayList(gradovi));
    }
}
