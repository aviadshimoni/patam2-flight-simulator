package model;

import utils.SimpleAnomalyDetector;
import utils.ZScoreAlgorithm;
import utils.hybridAlgorithm;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.Callable;

public class Model extends Observable implements SimulatorModel {

    public Socket socket;
    public PrintWriter out;
    public TimeSeries tsAnomal, tsReg;
    public Options options;
    Thread displaySetting;
    public FlightSetting properties;
    public Map<String, Attribute> attributeMap;

    private double time = 0;
    public boolean isConnect;
    public String algName;

    public SimpleAnomalyDetector ad;
    public ZScoreAlgorithm zScore;
    public hybridAlgorithm hyperALG;

    public StringProperty attribute;
    public DoubleProperty timeStep;
    private Thread displayFlightThread;

    public Model() {
        this.properties = new FlightSetting();
        this.options = new Options();
        this.isConnect = false;
        this.attribute = new SimpleStringProperty();
        this.timeStep = new SimpleDoubleProperty();
    }

    @Override
    public boolean connectToServer(String ip, double port) {
        try {
            socket = new Socket("127.0.0.1", 5400);
            out = new PrintWriter(socket.getOutputStream());
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    synchronized public void playFile() {
        if (options.isAfterForward()) {
            options.setAfterForward(false);
            properties.setPlaySpeed(100);
        } else if (options.isAfterRewind()) {
            options.setRewind(false);
        } else if (options.isAfterPause()) {
            this.notify();
            options.setPause(false);
            options.setAfterPause(false);
        } else {
            if (!options.isAfterStop()){
                isConnect = connectToServer(properties.getIp(), properties.getPort());
            }
            options.setAfterStop(false);
            displayFlightThread = new Thread(() -> displayFlight(isConnect), "FlightDisplayThread");
            displayFlightThread.start();
        }
    }

    synchronized public void displayFlight(boolean isConnected) {
        int i = 0;
        int sizeTS = tsAnomal.getSize();

        for (i = (int) time; i < sizeTS; i++) {
            timeStep.setValue(time);
            while (options.pause || options.scroll || options.afterStop || options.forward || options.rewind)
            {
                try {
                    if (options.afterStop)
                        displaySetting.join();

                    if (options.afterPause)
                        this.wait();

                    if (options.forward) {
                        i = Math.min(sizeTS - 151, i + 150);
                        options.forward = false;
                    }
                    if (options.rewind) {
                        i = Math.max(1, i - 150);
                        options.rewind = false;
                    }

                    if (options.scroll) {
                        options.scroll = false;
                        i = (int) time;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (isConnected) {
                out.println(tsAnomal.rows.get(i));
                out.flush();
            }
            time = i;
            setChanged();
            notifyObservers();
            try {
                Thread.sleep(properties.getPlaySpeed());//responsible for the speed of the display
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
        options.scroll = true;
    }

    @Override
    public void setTimeSeries(TimeSeries ts, String tsType) {
        if (tsType.equals("Train"))
            this.tsReg = ts;
        else if (tsType.equals("Test"))
            this.tsAnomal = ts;
    }


    // ***** Buttons Functions ***** //

    public void pauseFile() {
        options.pause = true;
        options.afterPause = true;
    }

    public void stopFile() {
        options.afterStop = true;
        this.time = 0;
    }

    public void rewindFile() {
        options.rewind = true;
    }

    public void forwardFile() {
        options.forward = true;
    }

    // ***** Alerts Functions ***** //

    public void fileNotFoundAlert(String type) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("File not found");
        alert.setContentText("Choose " + type + " file");
        alert.showAndWait();
    }

    public void brokenFileAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("The file you choose is broken");
        alert.showAndWait();
    }

    public void wrongFileAlert(String type) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Wrong file chosen");
        alert.setContentText("Please choose a " + type + " file");
        alert.showAndWait();
    }

    public void fileUpdateAlert(String type) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("File has been updated");
        alert.setContentText(type + " file has been updated");
        alert.showAndWait();
    }

    // ***** Algorithm Functions ***** //

    public boolean loadAnomalyDetector(String path, String nameALG) throws Exception {
        algName = nameALG.split("\\.")[0];
        URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{new URL("file:\\" + path)});
        Class<?> c = urlClassLoader.loadClass("utils."+algName);

        switch(algName) {
            case "hybridAlgorithm":
                hyperALG = (hybridAlgorithm) c.newInstance();
                hyperALG.learnNormal(tsReg);
                hyperALG.detect(tsAnomal);
                break;
            case "SimpleAnomalyDetector":
                ad = (SimpleAnomalyDetector) c.newInstance();
                ad.learnNormal(tsReg);
                ad.detect(tsAnomal);
                break;
            case "ZScoreAlgorithm":
                zScore = (ZScoreAlgorithm) c.newInstance();
                zScore.learnNormal(tsReg);
                zScore.detect(tsAnomal);
                break;
            default:
                throw new Exception("Invalid algorithm name.");
        }
        new Thread(() -> initAlgorithmData()).start();
        return false;
    }

    public void initAlgorithmData() {
        if (algName.equals("SimpleAnomalyDetector")) {
            ad.timeStep.bind(timeStep);
            ad.attribute1.bind(attribute);
        }
        else if (algName.equals("ZScoreAlgorithm")) {
            zScore.timeStep.bind(timeStep);
            zScore.property.bind(attribute);
        }
        else {
            hyperALG.timeStep.bind(timeStep);
            hyperALG.attribute1.bind(attribute);
        }
    }

    public void bindAlgorithmToTimestep() {
        if (algName.equals("SimpleAnomalyDetector"))
            ad.timeStep.bind(timeStep);
        else if (algName.equals("ZScoreAlgorithm"))
            zScore.timeStep.bind(timeStep);
        else
            hyperALG.timeStep.bind(timeStep);
    }

    public Callable<AnchorPane> getPainter() {
        if (algName.equals("SimpleAnomalyDetector"))
            return () -> ad.paint();
        else if (algName.equals("ZScoreAlgorithm"))
            return () -> zScore.paint();
        else
            return () -> hyperALG.paint();
    }

    // ***** XML File Functions ***** //

    public boolean openXML() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Settings XML file");
        fc.setInitialDirectory(new File("./"));
        File chosen = fc.showOpenDialog(null);

        if (chosen == null || !chosen.getName().contains(".xml")) {
            if (chosen == null) fileNotFoundAlert("Settings XML");
            else wrongFileAlert("Settings XML");
            return false;
        }

        try {
            this.properties = readFromXML(chosen.getAbsolutePath());
            if (this.properties != null) {
                createMapAttribute();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            brokenFileAlert();
        }
        return false;
    }

    public void createMapAttribute() {
        this.attributeMap = new HashMap<>();
        for (Attribute attribute : properties.getAttributes()) {
            attributeMap.put(attribute.name, attribute);
        }
    }

    public void writeToXML(FlightSetting settings) throws IOException {
        FileOutputStream fos = new FileOutputStream("settings.xml");
        XMLEncoder encoder = new XMLEncoder(fos);
        encoder.setExceptionListener(new ExceptionListener() {
            public void exceptionThrown(Exception e) {
                System.out.println("Exception! :" + e.toString());
            }
        });

        List<Attribute> lst = new ArrayList<>();
        lst.add(createAttribute("aileron", 0, -1, 1));
        lst.add(createAttribute("elevators", 1, -1, 1));
        lst.add(createAttribute("rudder", 2, 0, 1));
        lst.add(createAttribute("throttle", 6, 0, 1));
        lst.add(createAttribute("altimeter", 25, null, null));
        lst.add(createAttribute("airSpeed", 24, null, null));
        lst.add(createAttribute("fd", 36, 0, 360));
        lst.add(createAttribute("pitch", 29, -10, 17));
        lst.add(createAttribute("roll", 17, -38, 43));
        lst.add(createAttribute("yaw", 20, -29, 91));
        settings.setAttributes(lst);
        settings.setPort((double) 5400);
        settings.setIp("127.0.0.1");
        settings.setPlaySpeed(100);

        encoder.writeObject(settings);
        encoder.close();
        fos.close();
    }

    public Attribute createAttribute(String name, Integer associativeName, Integer min, Integer max) {
        Attribute res = new Attribute();
        res.setName(name);
        res.setAssociativeName(associativeName);
        res.setMax(max);
        res.setMin(min);
        return res;
    }

    public FlightSetting readFromXML(String fileName) throws IOException {
        System.out.println(fileName);
        FileInputStream fis = new FileInputStream(fileName);
        XMLDecoder decoder = new XMLDecoder(fis);
        FlightSetting decodedSettings = (FlightSetting) decoder.readObject();
        decoder.close();
        fis.close();
        return decodedSettings;
    }
}
