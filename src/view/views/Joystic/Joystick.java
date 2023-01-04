package view.views.Joystic;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;


public class Joystick extends BorderPane {

    public DoubleProperty aileron, elevators, rudder, throttle;
    JoystickController MyJoystickController;

    public Joystick() {

        super();
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("Joystick.fxml"));
        aileron = new SimpleDoubleProperty();
        elevators = new SimpleDoubleProperty();
        rudder = new SimpleDoubleProperty();
        throttle = new SimpleDoubleProperty();
        try {
            BorderPane joystick = fxml.load();
            MyJoystickController = fxml.getController();

            MyJoystickController.rudder.valueProperty().bind(rudder);
            MyJoystickController.throttle.valueProperty().bind(throttle);

            MyJoystickController.joysticX.bind(aileron);
            MyJoystickController.joysticY.bind(throttle);
            this.getChildren().add(joystick);

        } catch (IOException e) {}
    }
}
