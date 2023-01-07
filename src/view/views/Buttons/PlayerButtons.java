package view.views.Buttons;

import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;
import java.io.IOException;


public class PlayerButtons extends AnchorPane {
    public DoubleProperty sliderTime, choiceSpeed;
    public  StringProperty timeFlight, milliSec, seconds, minutes;
    public BooleanProperty onOpenCSVTrain, onOpenCSVTest, onOpenXML, onPlay, onPause, onSpeed, onStop,
                           onRewind, onForward,onAnomalyDetector;

    public PlayerButtons() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerButtons.fxml"));
        sliderTime = new SimpleDoubleProperty();
        choiceSpeed = new SimpleDoubleProperty();

        // Clocks
        milliSec = new SimpleStringProperty();
        seconds = new SimpleStringProperty();
        minutes = new SimpleStringProperty();
        timeFlight = new SimpleStringProperty();

        // Buttons
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

        onOpenCSVTrain.setValue(false);
        onOpenCSVTest.setValue(false);
        onOpenXML.setValue(false);
        onAnomalyDetector.setValue(false);
        onPlay.setValue(false);
        onPause.setValue(false);
        onSpeed.setValue(false);
        onStop.setValue(false);
        onRewind.setValue(false);
        onForward.setValue(false);

        try {
            AnchorPane buttons= loader.load();
            PlayerButtonsController playerButtonsController= loader.getController();
            playerButtonsController.init();

            playerButtonsController.milliSec.textProperty().bind(milliSec);
            playerButtonsController.seconds.textProperty().bind(seconds);
            playerButtonsController.minutes.textProperty().bind(minutes);

            playerButtonsController.sliderTime.valueProperty().bindBidirectional(sliderTime);
            choiceSpeed.bind(playerButtonsController.choiceSpeed.valueProperty());

            onOpenCSVTrain.bind(playerButtonsController.onOpenCSVTrain);
            onOpenCSVTest.bind(playerButtonsController.onOpenCSVTest);
            onOpenXML.bind(playerButtonsController.onOpenXML);
            onAnomalyDetector.bind(playerButtonsController.onAnomalyDetector);
            onPlay.bind(playerButtonsController.onPlay);
            onPause.bind(playerButtonsController.onPause);
            onSpeed.bind(playerButtonsController.onSpeed);
            onStop.bind(playerButtonsController.onStop);
            onRewind.bind(playerButtonsController.onRewind);
            onForward.bind(playerButtonsController.onForward);

            this.getChildren().add(buttons);

        } catch (IOException e) {}
    }
}


