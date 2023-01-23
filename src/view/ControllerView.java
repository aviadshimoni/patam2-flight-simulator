package view;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.Observable;
import java.util.Observer;

import view.views.Buttons.PlayerButtonsController;
import view.views.Graphs.Graphs;
import view.views.Joystic.Joystick;
import view.views.Gauges.Gauges;
import view.views.Buttons.PlayerButtons;
import view.views.Attributes.Attributes;
import viewModel.ViewModelController;


public class ControllerView extends Pane implements Observer {


    @FXML
    Joystick joystick;

    @FXML
    PlayerButtons playerButtons;

    @FXML
    Gauges gauges;

    @FXML
    Graphs graphs;

    @FXML
    Attributes listView;

    @FXML
    AnchorPane anchorPane;

    ViewModelController myViewModelController;

    void init(ViewModelController myViewModelController) {
        this.myViewModelController = myViewModelController;

        joystick.aileron.bind(myViewModelController.aileron);
        joystick.elevators.bind(myViewModelController.elevators);
        joystick.rudder.bind(myViewModelController.rudder);
        joystick.throttle.bind(myViewModelController.throttle);

        playerButtons.timeFlight.bind(myViewModelController.timeFlight);
        playerButtons.milliSec.bind(myViewModelController.clock.miliSec.asString());
        playerButtons.seconds.bind(myViewModelController.clock.seconds.asString());
        playerButtons.minutes.bind(myViewModelController.clock.minutes.asString());
        playerButtons.sliderTime.bindBidirectional(myViewModelController.sliderTime);

        myViewModelController.choiceSpeed.bind(playerButtons.choiceSpeed);

        graphs.value.bind(myViewModelController.valueAxis);
        graphs.valueCorrelate.bind(myViewModelController.valueCorrelate);
        graphs.timeStamp.bind(myViewModelController.timeStamp);
        graphs.graphSpeed.bind(myViewModelController.choiceSpeed);
        graphs.sizeTimeSeries.setValue(myViewModelController.sizeTimeSeries.getValue());
        graphs.correlatedAttribute.setValue(myViewModelController.correlateFeature.getValue());

        gauges.airSpeed.bind(myViewModelController.airSpeed);
        gauges.altimeter.bind(myViewModelController.altimeter);
        gauges.flightDirection.bind(myViewModelController.flightDirection);
        gauges.pitch.bind(myViewModelController.pitch);
        gauges.pitchMax.bind(myViewModelController.pitchMax);
        gauges.pitchMin.bind(myViewModelController.pitchMin);
        gauges.roll.bind(myViewModelController.roll);
        gauges.rollMax.bind(myViewModelController.rollMax);
        gauges.rollMin.bind(myViewModelController.rollMin);
        gauges.yaw.bind(myViewModelController.yaw);
        gauges.yawMax.bind(myViewModelController.yawMax);
        gauges.yawMin.bind(myViewModelController.yawMin);


        playerButtons.onOpenCSVTrain.addListener((o, ov, nv) -> myViewModelController.openCSVTrainFile());
        playerButtons.onOpenCSVTest.addListener((o, ov, nv) -> myViewModelController.openCSVTestFile());
        playerButtons.onOpenXML.addListener((o, ov, nv) -> myViewModelController.openXMLFile());
        playerButtons.onPlay.addListener((o, ov, nv) -> myViewModelController.play());
        playerButtons.onPause.addListener((o, ov, nv) -> myViewModelController.pause());
        playerButtons.onStop.addListener((o, ov, nv) -> myViewModelController.stop());
        playerButtons.onRewind.addListener((o, ov, nv) -> myViewModelController.rewind());
        playerButtons.onForward.addListener((o, ov, nv) -> myViewModelController.forward());
        playerButtons.onAnomalyDetector.addListener((o, ov, nv)-> {
            if(myViewModelController.loadAnomalyDetector()) {
                try {
                    anchorPane.getChildren().setAll(myViewModelController.getPainter().call());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        graphs.selectedAttribute.bind(listView.attributesController.listView.getSelectionModel().selectedItemProperty());
        graphs.correlatedAttribute.bind(myViewModelController.correlateFeature);

        listView.attributesController.listView.setItems(myViewModelController.attributeList);
        listView.attributesController.listView.getSelectionModel().select(0);
        myViewModelController.chosenAttribute.bind(listView.attributesController.listView.getSelectionModel().selectedItemProperty());
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
