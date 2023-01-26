package viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import model.Model;
import model.TimeSeries;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;

public class ViewModelController extends Observable implements Observer {

    Model model;
    public Clock clock;
    public TimeSeries timeSeriesRegression, timeSeriesAnomaly;
    public DoubleProperty timeStamp, timeStampGraph;
    public DoubleProperty throttle, rudder, aileron, elevators;
    public DoubleProperty sliderTime, choiceSpeed;
    public DoubleProperty pitch, pitchMax, pitchMin, roll, rollMax, rollMin, yaw, yawMax, yawMin;
    public StringProperty altimeter, airSpeed, flightDirection;
    public DoubleProperty valueAxis, valueCorrelate;
    public StringProperty timeFlight, chosenAttribute, correlateFeature;
    public IntegerProperty sizeTimeSeries;

    public ObservableList<String> attributeList;

    public int numberOfCorrelateAttribute;
    public Boolean xmlFile, csvTestFile, csvTrainFile, isAnomalyAlgorithmLoaded;

    public ViewModelController(Model model) {
        this.model = model;
        clock = new Clock();
        model.addObserver(this);    //add Model as Observable

        xmlFile = false;
        csvTestFile = false;
        csvTrainFile = false;
        isAnomalyAlgorithmLoaded = false;

        timeStamp = new SimpleDoubleProperty();
        timeStampGraph = new SimpleDoubleProperty();

        //Joystick:
        aileron = new SimpleDoubleProperty();
        elevators = new SimpleDoubleProperty();
        rudder = new SimpleDoubleProperty();
        throttle = new SimpleDoubleProperty();
        sliderTime = new SimpleDoubleProperty();
        choiceSpeed = new SimpleDoubleProperty();

        // Time board:
        pitch = new SimpleDoubleProperty();
        pitchMax = new SimpleDoubleProperty();
        pitchMin = new SimpleDoubleProperty();
        roll = new SimpleDoubleProperty();
        rollMax = new SimpleDoubleProperty();
        rollMin = new SimpleDoubleProperty();
        yaw = new SimpleDoubleProperty();
        yawMax = new SimpleDoubleProperty();
        yawMin = new SimpleDoubleProperty();
        altimeter = new SimpleStringProperty();
        airSpeed = new SimpleStringProperty();
        flightDirection = new SimpleStringProperty();

        // Graphs:
        valueAxis = new SimpleDoubleProperty();
        valueCorrelate = new SimpleDoubleProperty();
        chosenAttribute = new SimpleStringProperty();
        correlateFeature = new SimpleStringProperty();
        correlateFeature.setValue("0");

        timeFlight = new SimpleStringProperty();

        sizeTimeSeries = new SimpleIntegerProperty();

        attributeList = FXCollections.observableArrayList();

        choiceSpeed.addListener((o, ov, nv) -> {
            speedPlay();
        });

        sliderTime.addListener((o, ov, nv) -> {
            timeStamp.setValue(nv.doubleValue());
            model.setTime(nv.doubleValue());
            clock.update(nv.intValue() - ov.intValue());
        });

        timeStamp.addListener((o, ov, nv) -> {
            updateDisplayVariables(nv.intValue());
            if(isAnomalyAlgorithmLoaded == true)
                model.bindAlgorithmToTimestep();     //updating the date for the alg graph
        });

        chosenAttribute.addListener((o, ov, nv) -> {
            model.attribute.bind(chosenAttribute);
            if(isAnomalyAlgorithmLoaded == true)
                model.bindAlgorithmToTimestep();
        });

    }

    public void updateDisplayVariables(int time) {
        DecimalFormat df = new DecimalFormat("0.0#");
        df.setRoundingMode(RoundingMode.DOWN);

        sliderTime.setValue(time);
        timeFlight.setValue(String.valueOf(time));
        aileron.setValue(timeSeriesAnomaly.getValueByTime(model.attributeMap.get("aileron").associativeName, time));
        elevators.setValue(timeSeriesAnomaly.getValueByTime(model.attributeMap.get("elevators").associativeName, time));
        rudder.setValue(timeSeriesAnomaly.getValueByTime(model.attributeMap.get("rudder").associativeName, time));
        throttle.setValue(timeSeriesAnomaly.getValueByTime(model.attributeMap.get("throttle").associativeName, time));
        altimeter.setValue(String.valueOf(df.format(timeSeriesAnomaly.getValueByTime(model.attributeMap.get("altimeter").associativeName, time))));
        airSpeed.setValue(String.valueOf(df.format(timeSeriesAnomaly.getValueByTime(model.attributeMap.get("airSpeed").associativeName, time))));
        flightDirection.setValue(String.valueOf(df.format(timeSeriesAnomaly.getValueByTime(model.attributeMap.get("fd").associativeName, time))));
        pitch.setValue(timeSeriesAnomaly.getValueByTime(model.attributeMap.get("pitch").associativeName, time));
        roll.setValue(timeSeriesAnomaly.getValueByTime(model.attributeMap.get("roll").associativeName, time));
        yaw.setValue(timeSeriesAnomaly.getValueByTime(model.attributeMap.get("yaw").associativeName, time));


        valueAxis.setValue(timeSeriesAnomaly.getValueByTime(chosenAttribute.getValue(), time));

        //  Init the name of the correlate attribute
        correlateFeature=getCorrelateFeature();

        //  Getting the col's number of the correlate attribute
        if (correlateFeature.getValue() != null) {
            valueCorrelate.setValue(timeSeriesAnomaly.getValueByTime(correlateFeature.getValue(), time));
        } else {
            numberOfCorrelateAttribute = 0;
            valueCorrelate.setValue(0);
        }
    }
    public StringProperty getCorrelateFeature(){
        correlateFeature.setValue(timeSeriesRegression.getCorrelateFeature(chosenAttribute.getValue()));
        return correlateFeature;
    }

    public void openCSVTrainFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("open CSV train file");
        fc.setInitialDirectory(new File("./"));
        File chosen = fc.showOpenDialog(null);

        if(chosen == null) {
            model.fileNotFoundAlert("train");
        } else {
            if (!chosen.getName().contains(".csv"))  // Checking the file
            {
                model.wrongFileAlert("CSV");
            } else {
                timeSeriesRegression = new TimeSeries(chosen.getPath());
                timeSeriesRegression.checkCorrelate(timeSeriesRegression);
                model.setTimeSeries(timeSeriesRegression, "Train");   // Set timeSeries
                csvTrainFile = true;
                model.fileUpdateAlert("Train");
            }
        }
    }

    public void openCSVTestFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("open CSV test file");
        fc.setInitialDirectory(new File("./"));
        File chosen = fc.showOpenDialog(null);

        if(chosen == null) {
            model.fileNotFoundAlert("test");
        } else {
            if (!chosen.getName().contains(".csv")) {  //checking the file
                model.wrongFileAlert("CSV");
            } else {
                timeSeriesAnomaly = new TimeSeries(chosen.getPath());
                model.setTimeSeries(timeSeriesAnomaly, "Test");

                attributeList.addAll(timeSeriesAnomaly.getAttributes());
                sizeTimeSeries.setValue(timeSeriesAnomaly.getSize());
                altimeter.setValue("0");
                airSpeed.setValue("0");
                flightDirection.setValue("0");

                model.fileUpdateAlert("Test");
                this.csvTestFile = true;
            }
        }
    }

    public void openXMLFile() {
        xmlFile = model.openXML();
        if(xmlFile) {
            model.fileUpdateAlert("XML");
            pitchMax.setValue(model.attributeMap.get("pitch").getMax());
            pitchMin.setValue(model.attributeMap.get("pitch").getMin());
            rollMax.setValue(model.attributeMap.get("roll").getMax());
            rollMin.setValue(model.attributeMap.get("roll").getMin());
            yawMax.setValue(model.attributeMap.get("yaw").getMax());
            yawMin.setValue(model.attributeMap.get("yaw").getMin());
        }
    }

    public void play() {
        if (csvTestFile && csvTrainFile && xmlFile) {
            this.model.playFile();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File is missing");
            alert.setContentText("Please upload csv files and xml file");
            alert.showAndWait();
        }
    }

    public void pause() {
        this.model.pauseFile();
    }

    public void stop() {
        this.model.stopFile();
    }

    public void rewind() {
        this.model.rewindFile();
    }

    public void forward() {
        this.model.forwardFile();
    }

    public boolean loadAnomalyDetector() {
        FileChooser fc = new FileChooser();
        fc.setTitle("open ALG");
        fc.setInitialDirectory(new File("./"));
        File chosen = fc.showOpenDialog(null);

        if(!csvTestFile || !csvTrainFile) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong file chosen");
            alert.setContentText("Please upload csv files first");
            alert.showAndWait();
            return false;
        }

        if(chosen == null) {
            model.fileNotFoundAlert("algorithm");
            return false;
        } else {
            if (!chosen.getName().contains(".class"))  //checking the file
            {
                model.wrongFileAlert("CLASS");
                return false;
            }
            try {
                model.loadAnomalyDetector(chosen.getPath(), chosen.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            isAnomalyAlgorithmLoaded = true;
            model.fileUpdateAlert("Algorithm");
            return true;
        }
    }

    public void speedPlay() {
        if (choiceSpeed.doubleValue() == 0.5) model.properties.setPlaySpeed(150);
        else if (choiceSpeed.doubleValue() == 1.5) model.properties.setPlaySpeed(75);
        else if (choiceSpeed.doubleValue() == 2) model.properties.setPlaySpeed(50);
        else if (choiceSpeed.doubleValue() == 2.5) model.properties.setPlaySpeed(20);
        else model.properties.setPlaySpeed(100);
    }

    public Callable<AnchorPane> getPainter(){return model.getPainter();}

    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            this.timeStamp.setValue(model.getTime());
        }
    }
}
