package view;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.util.Observable;
import java.util.Observer;
import view.views.Joystic.Joystick;
import view.views.Gauges.Gauges;

public class ControllerView extends Pane implements Observer {


    @FXML
    Joystick Joystick;

    @FXML
    Gauges timeBoard;


    void init() {
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
