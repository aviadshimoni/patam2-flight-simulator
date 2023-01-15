package view.views.Buttons;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PlayerButtonsController {

    @FXML
    Slider sliderTime;

    @FXML
    ChoiceBox choiceSpeed;

    @FXML
    TextField milliSec;

    @FXML
    TextField seconds;

    @FXML
    TextField minutes;

    @FXML
    Button playBTN, stopBTN ,pauseBTN;

    public BooleanProperty onOpenCSVTrain, onOpenCSVTest, onOpenXML, onPlay, onPause, onSpeed, onStop,
            onRewind, onForward, onAnomalyDetector;

    public PlayerButtonsController() {
        onOpenCSVTrain = new SimpleBooleanProperty();
        onOpenCSVTest = new SimpleBooleanProperty();
        onOpenXML = new SimpleBooleanProperty();
        onAnomalyDetector = new SimpleBooleanProperty();
        onPlay = new SimpleBooleanProperty();
        onPause = new SimpleBooleanProperty();
        onSpeed = new SimpleBooleanProperty();
        onStop = new SimpleBooleanProperty();
        onRewind = new SimpleBooleanProperty();
        onForward = new SimpleBooleanProperty();
            //set init values
        onOpenCSVTest.setValue(false);
        onOpenCSVTrain.setValue(false);
        onOpenXML.setValue(false);
        onAnomalyDetector.setValue(false);
        onPlay.setValue(false);
        onPause.setValue(false);
        onSpeed.setValue(false);
        onStop.setValue(false);
        onRewind.setValue(false);
        onForward.setValue(false);
    }

    public void init() {
        ObservableList<Double> speedOptionsList = FXCollections.observableArrayList(0.5, 1.0, 1.5, 2.0, 2.5);
        choiceSpeed.setItems(speedOptionsList);
        choiceSpeed.setValue(1.0);
    }

    public void onOpenCSVTrain() {
        onOpenCSVTrain.setValue(!onOpenCSVTrain.getValue());
    }

    public void onOpenCSVTest() {
        onOpenCSVTest.setValue(!onOpenCSVTest.getValue());
    }

    public void onOpenXML() {
        onOpenXML.setValue(!onOpenXML.getValue());
    }
    public void onAnomalyDetector(){
        onAnomalyDetector.setValue(!onAnomalyDetector.getValue());
    }

    public void onRewind() {
        onRewind.setValue(!onRewind.getValue());
    }
    public void onPlay() {
        onPlay.setValue(!onPlay.getValue());
        playBTN.setDisable(true);
        pauseBTN.setDisable(false);
        stopBTN.setDisable(false);

    }
    public void onPause() {
        onPause.setValue(!onPause.getValue());
        disablePlayBTN();
    }
    public void onStop() {
        onStop.setValue(!onStop.getValue());
        disablePlayBTN();

    }
    public void onForward() {
        onForward.setValue(!onForward.getValue());
    }

    public void disablePlayBTN(){
        playBTN.setDisable(false);
        pauseBTN.setDisable(true);
        stopBTN.setDisable(true);
    }
}
