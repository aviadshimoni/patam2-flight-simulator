package view;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.Observable;
import java.util.Observer;
import view.views.Joystic.Joystick;

public class ControllerView extends Pane implements Observer {


    @FXML
    Joystick Joystick;

    @FXML
    AnchorPane adAnchorPane;

    void init() {
        adAnchorPane.setLayoutX(330);
        adAnchorPane.setLayoutY(200);
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
