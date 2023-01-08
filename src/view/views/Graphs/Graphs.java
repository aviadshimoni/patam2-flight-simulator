package view.views.Graphs;


import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Graphs extends AnchorPane {
    public StringProperty selectedAttribute, correlatedAttribute;
    public DoubleProperty value, valueCorrelate,timeStamp, graphSpeed;

    public IntegerProperty sizeTS;

    GraphsController graphsController;

    public Graphs() {
        super();
        FXMLLoader fxmlL = new FXMLLoader();

        selectedAttribute = new SimpleStringProperty();
        correlatedAttribute = new SimpleStringProperty();
        value = new SimpleDoubleProperty();
        valueCorrelate = new SimpleDoubleProperty();
        graphSpeed = new SimpleDoubleProperty();
        timeStamp = new SimpleDoubleProperty();
        sizeTS = new SimpleIntegerProperty();

        try {
            AnchorPane graphAP = fxmlL.load(getClass().getResource("Graphs.fxml").openStream());
            graphsController = fxmlL.getController();
            graphsController.init();

            graphsController.value.bind(value);
            graphsController.timeStamp.bind(timeStamp);
            graphsController.selectedAttribute.bind(selectedAttribute);
            graphsController.correlatedAttribute.bind(correlatedAttribute);
            graphsController.valueCorrelate.bind(valueCorrelate);
            this.getChildren().add(graphAP);
        }
        catch (IOException e) {}

    }
}
