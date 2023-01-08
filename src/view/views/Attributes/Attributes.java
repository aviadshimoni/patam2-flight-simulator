package view.views.Attributes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Attributes extends AnchorPane {

    public ObservableList list;
    public StringProperty chosenAttribute;
    public AttributesController attributesController;

    public Attributes() {
        super();
        FXMLLoader Loader = new FXMLLoader((getClass().getResource("Attributes.fxml")));
        list = FXCollections.observableArrayList();
        chosenAttribute = new SimpleStringProperty();
        try {
            AnchorPane list = Loader.load();
            attributesController = Loader.getController();
            chosenAttribute.bind(attributesController.listView.getSelectionModel().selectedItemProperty());
            this.getChildren().add(list);
        } catch (IOException e) {
        }
    }
}