package algorithms;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import viewModel.TimeSeries;

import java.util.*;

public class ZScoreAlgorithm implements AnomalyDetector {
    Vector<Float> tx;

    public HashMap<Integer, ArrayList<Float>> ZScoreMap;
    public HashMap<String, ArrayList<Float>> ZScoreReg;
    public HashMap<String, ArrayList<Integer>> ZScoreAnomaly;

    public HashMap<String, ArrayList<Float>> avgMap;

    public StringProperty Attribute = new SimpleStringProperty();
    public DoubleProperty timeStep = new SimpleDoubleProperty();


    public ZScoreAlgorithm() {
        this.tx = new Vector<>();
        this.ZScoreMap = new HashMap<>();
        this.avgMap = new HashMap<>();
        this.ZScoreReg = new HashMap<>();
        this.ZScoreAnomaly = new HashMap<>();
    }

    public float[] ListToArr(List<Float> lst) {
        float[] res = new float[lst.size()];

        for (int i = 0; i < res.length; i++) {
            res[i] = lst.get(i);
        }
        return res;
    }

    public float calcZScore(List<Float> col) {
        float avg, sigma, zScore, var;
        float[] arrFloat;
        int colSize = col.size();
        float x;

        if (colSize == 0) {
            return 0;
        }

        x = col.get(colSize - 1);
        if (colSize == 1) {
            arrFloat = ListToArr(col);
            if(StatLib.var(arrFloat) != 0)
                return Math.abs((x - StatLib.avg(arrFloat))) / StatLib.var(arrFloat);
            return 0;
        }

        arrFloat = ListToArr(col.subList(0, col.size() - 1));
        avg = StatLib.avg(arrFloat);
        var = StatLib.var(arrFloat);

        if (var < 0)
            return 0;
        sigma = (float) Math.sqrt(var);

        if (sigma == 0)
            return 0;
        zScore = Math.abs(x - avg) / sigma;

        return zScore;
    }


    public float argMax(LinkedList<Float> z) {
        float max = 0;
        for (int i = 0; i < z.size(); i++) {
            if (max < z.get(i))
                max = z.get(i);
        }
        return max;
    }

    public float argMax(ArrayList<Float> z) {
        float max = 0;
        for (int i = 0; i < z.size(); i++) {
            if (max < z.get(i))
                max = z.get(i);
        }
        return max;
    }

    @Override
    public void learnNormal(TimeSeries ts) {
        int index = 0;

        ArrayList<Float> zScored = new ArrayList<>();
        String attribute;
        int colSize = ts.atts.size();

        for (int i = 0; i < colSize; i++) {
            ArrayList<Float> col = ts.tsNum.get(i);
            attribute = ts.atts.get(index);
            avgMap.put(attribute, new ArrayList<>());

            for (int j = 0; j < col.size(); j++) {

                zScored.add(calcZScore(col.subList(0, j)));
            }
            tx.add(argMax(zScored));
            this.ZScoreReg.put(attribute, zScored);
            this.ZScoreMap.put(index++, zScored);
        }
    }

    public List<AnomalyReport> detect(TimeSeries data) {
        List<AnomalyReport> lst = new LinkedList<>();
        String attribute;

        for (int indexCol = 0; indexCol < data.atts.size(); indexCol++) {
            ArrayList<Float> col = data.tsNum.get(indexCol);
            attribute = data.atts.get(indexCol);
            for (int indexTime = 0; indexTime < col.size(); indexTime++) {
                if (calcZScore(col.subList(0, indexTime)) > tx.get(indexCol)) {
                    lst.add(new AnomalyReport(attribute, indexTime));

                    if (!ZScoreAnomaly.containsKey(attribute)) {
                        ZScoreAnomaly.put(attribute, new ArrayList<>());
                        ZScoreAnomaly.get(attribute).add(indexTime);
                    } else
                        ZScoreAnomaly.get(attribute).add(indexTime);
                }
            }
        }
        return lst;
    }

    @Override
    public AnchorPane paint() {
        AnchorPane ap = new AnchorPane();
        //line Chart, child of Anchor
        LineChart<Number, Number> sc = new LineChart<>(new NumberAxis(), new NumberAxis());
        sc.setPrefHeight(210);
        sc.setPrefWidth(290);
        XYChart.Series line = new XYChart.Series();
        XYChart.Series lineAnomal = new XYChart.Series();
        sc.getData().addAll(line,lineAnomal);
        lineAnomal.getNode().setStyle("-fx-stroke: #01aa18;");

        Attribute.addListener((ob, oldV, newV) -> {//to delete the old graph if attribute has changed
            timeStep.addListener((o, ov, nv) -> {
                Platform.runLater(() -> {
                    if (!ZScoreAnomaly.containsKey(Attribute.getValue())) {// i dont think it's work
                        lineAnomal.getData().add(new XYChart.Data<>(timeStep.getValue(), ZScoreReg.get(Attribute.getValue().toString()).get(timeStep.intValue())));
                    } else {
                        if (ZScoreAnomaly.get(Attribute.getValue()).contains(timeStep.intValue()))//if we are at att with anomal and there is anomal in the present time
                            line.getData().add(new XYChart.Data<>(timeStep.getValue(), ZScoreReg.get(Attribute.getValue().toString()).get(timeStep.intValue())));
                        else
                            lineAnomal.getData().add(new XYChart.Data<>(timeStep.getValue(), ZScoreReg.get(Attribute.getValue().toString()).get(timeStep.intValue())));
                    }
                });
            });

            if (!newV.equals(oldV)) {   //if change the attribute
                lineAnomal.getData().clear();
            }
        });
        sc.setAnimated(false);
        sc.setCreateSymbols(false);
        ap.getChildren().add(sc);
        return ap;
    }
    public HashMap<String, ArrayList<Float>>getZScoreReg(){
        return this.ZScoreReg;
    }
    public HashMap<String, ArrayList<Integer>>getZscoreAnomal(){
        return this.ZScoreAnomaly;
    }
}
