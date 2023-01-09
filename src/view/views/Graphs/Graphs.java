package view.views.Graphs;


import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Graphs extends AnchorPane {
    public StringProperty selectedAttribute, correlatedAttribute;
    public DoubleProperty value, valueCorrelate,timeStamp, graphSpeed;

    public IntegerProperty sizeTimeSeries;

    GraphsController graphsController;

    public Graphs() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Graphs.fxml"));

        selectedAttribute = new SimpleStringProperty();
        correlatedAttribute = new SimpleStringProperty();
        value = new SimpleDoubleProperty();
        valueCorrelate = new SimpleDoubleProperty();
        graphSpeed = new SimpleDoubleProperty();
        timeStamp = new SimpleDoubleProperty();
        sizeTimeSeries = new SimpleIntegerProperty();

        try {
            AnchorPane graphAP = loader.load();
            graphsController = loader.getController();
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
