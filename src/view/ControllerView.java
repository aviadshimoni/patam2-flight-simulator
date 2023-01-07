package view;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.util.Observable;
import java.util.Observer;
import view.views.Joystic.Joystick;
<<<<<<< HEAD
import view.views.Gauges.Gauges;
=======
import view.views.Buttons.PlayerButtons;
import view.views.Gauges.Gauges;

>>>>>>> 9f180516f63d0641f199a9c7b069ad7376c9a304

public class ControllerView extends Pane implements Observer {


    @FXML
    Joystick Joystick;

    @FXML
<<<<<<< HEAD
    Gauges timeBoard;
=======
    PlayerButtons playerButtons;

    @FXML
    Gauges gauges;
>>>>>>> 9f180516f63d0641f199a9c7b069ad7376c9a304


    void init() {
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
