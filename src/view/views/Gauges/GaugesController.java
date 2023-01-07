package view.views.Gauges;

import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class GaugesController {

    @FXML
    Text altimeter, airSpeed, flightDirection;

    @FXML
    Gauge pitch, roll, yaw;
}
