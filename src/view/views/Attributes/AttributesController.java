package view.views.Attributes;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class AttributesController {

    @FXML
    public ListView listView;

    public void initialize() {
        listView.getItems().addAll("Item 1", "Item 2", "Item 3");
    }

}
