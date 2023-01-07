package view.views.Gauges;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Gauges extends AnchorPane {

    public StringProperty altimeter, airSpeed, flightDirection;
    public DoubleProperty pitch, pitchMax, pitchMin, roll, rollMax, rollMin, yaw, yawMax, yawMin;

    public Gauges() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Gauges.fxml"));
        altimeter = new SimpleStringProperty();
        airSpeed = new SimpleStringProperty();
        flightDirection = new SimpleStringProperty();

        pitch = new SimpleDoubleProperty();
        pitchMax = new SimpleDoubleProperty();
        pitchMin = new SimpleDoubleProperty();

        roll = new SimpleDoubleProperty();
        rollMax = new SimpleDoubleProperty();
        rollMin = new SimpleDoubleProperty();

        yaw = new SimpleDoubleProperty();
        yawMax = new SimpleDoubleProperty();
        yawMin = new SimpleDoubleProperty();

        try {
            AnchorPane gauges = loader.load();
            GaugesController gaugesController = loader.getController();
            this.getChildren().add(gauges);

            gaugesController.altimeter.textProperty().bind(altimeter);
            gaugesController.airSpeed.textProperty().bind(airSpeed);
            gaugesController.flightDirection.textProperty().bind(flightDirection);

            this.pitch.addListener((o, ov, nv) -> gaugesController.pitch.setValue(pitch.doubleValue()));
            pitchMax.addListener((o, ov, nv) -> gaugesController.pitch.setMaxValue(nv.doubleValue()));
            pitchMin.addListener((o, ov, nv) -> gaugesController.pitch.setMinValue(nv.doubleValue()));

            this.roll.addListener((o, ov, nv) -> gaugesController.roll.setValue(roll.doubleValue()));
            rollMax.addListener((o, ov, nv) -> gaugesController.roll.setMaxValue(nv.doubleValue()));
            rollMin.addListener((o, ov, nv) -> gaugesController.roll.setMinValue(nv.doubleValue()));

            this.yaw.addListener((o, ov, nv) -> gaugesController.yaw.setValue(yaw.doubleValue()));
            yawMax.addListener((o, ov, nv) -> gaugesController.yaw.setMaxValue(nv.doubleValue()));
            yawMin.addListener((o, ov, nv) -> gaugesController.yaw.setMinValue(nv.doubleValue()));


        } catch (IOException e) {}
    }
}
