package view.views.Joystic;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

public class JoystickController {

    @FXML
    Slider rudder;

    @FXML
    Slider throttle;

    @FXML
    Canvas joystick;

    private double middleX, middleY;
    public DoubleProperty joysticX, joysticY;

    public JoystickController() {
        joysticX = new SimpleDoubleProperty();
        joysticX.setValue(0);
        joysticX.addListener(v -> DrawJoystick());

        joysticY = new SimpleDoubleProperty();
        joysticY.setValue(0);
        joysticY.addListener(v -> DrawJoystick());
    }

    public void DrawJoystick()
    {
        GraphicsContext circle = joystick.getGraphicsContext2D();
        GraphicsContext circle_outline = joystick.getGraphicsContext2D();
        middleX = joystick.getWidth() / 2;
        middleY = joystick.getHeight() / 2;

        circle.clearRect(0,0, joystick.getWidth(), joystick.getHeight());

        int radius = 75;
        int velocityFactor = 60;
        double circleCenterX = middleX - (radius/2) + (joysticX.doubleValue() * velocityFactor);
        double circleCenterY = middleY - (radius/2) + (joysticY.doubleValue() * velocityFactor);
        circle.fillOval(circleCenterX, circleCenterY, radius, radius);
        circle_outline.strokeOval(middleX - radius,middleY - radius,radius*2,radius*2);

        circle.setFill(Color.rgb(255, 17, 79));
        circle_outline.setLineWidth(2);
    }
}
