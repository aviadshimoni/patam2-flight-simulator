package view.views.Graphs;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class GraphsController {

    @FXML
    private LineChart<String, Number> chosenAttributeGraph, mostCorrelatedAttribute;


    public StringProperty selectedAttribute, correlatedAttribute;
    public DoubleProperty value, timeStamp, valueCorrelate;

    public GraphsController() {
        this.selectedAttribute = new SimpleStringProperty();
        this.correlatedAttribute = new SimpleStringProperty();


        this.value = new SimpleDoubleProperty();
        this.valueCorrelate = new SimpleDoubleProperty();
        this.timeStamp = new SimpleDoubleProperty();
    }

    public void init() {
        selectedAttribute.setValue("0");
        correlatedAttribute.setValue("0");
        value.setValue(0);
        valueCorrelate.setValue(0);

        XYChart.Series series1 = new XYChart.Series();
        chosenAttributeGraph.getData().add(series1);
        series1.getNode().setStyle("-fx-stroke: black;");


        XYChart.Series series2 = new XYChart.Series();
        mostCorrelatedAttribute.getData().add(series2);
        series2.getNode().setStyle("-fx-stroke: black;");
        selectedAttribute.addListener((ob, oldVal, newVal) -> {

            timeStamp.addListener((o, ov, nv) -> {
                Platform.runLater(() -> {
                    series1.getData().add(new XYChart.Data<>(timeStamp.getValue().toString(), value.doubleValue()));
                    if (correlatedAttribute.getValue() != null)
                        series2.getData().add(new XYChart.Data<>(timeStamp.getValue().toString(), valueCorrelate.doubleValue()));
                   else
                        series2.getData().clear();
                });
                if(ov != null && nv.intValue() < ov.intValue()) {
                    series1.getData().clear();
                    series2.getData().clear();
                }
            });

            if ((newVal != null) && (!newVal.equals(oldVal))) {     //if change the attribute
                series1.getData().clear();
                series2.getData().clear();
            }
        });

        chosenAttributeGraph.setCreateSymbols(false);
        mostCorrelatedAttribute.setCreateSymbols(false);
    }
}