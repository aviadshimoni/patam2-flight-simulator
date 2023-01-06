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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleAnomalyDetector implements AnomalyDetector {

    TimeSeries tsReg;
    TimeSeries tsAnomal;
    public Line regLineForCorrelateAttribute;
    public ArrayList<CorrelatedFeatures> cf;

    public Map<String, ArrayList<Integer>> anomalyAndTimeStep;

    public StringProperty attribute1 = new SimpleStringProperty();
    public StringProperty attribute2 = new SimpleStringProperty();

    public DoubleProperty valAtt1X;     //static for line- minValX
    public DoubleProperty valAtt2Y;     //static for line- minValY
    public DoubleProperty vaAtt1Xend;   //static for line -maxValX
    public DoubleProperty vaAtt2Yend;   //static for line -maxValY

    public DoubleProperty valPointX;
    public DoubleProperty valPointY;

    public DoubleProperty timeStep;


    public SimpleAnomalyDetector() {
        this.cf = new ArrayList<>();
        this.anomalyAndTimeStep = new HashMap<>();
        this.valAtt1X = new SimpleDoubleProperty();
        this.valAtt2Y = new SimpleDoubleProperty();
        this.vaAtt1Xend = new SimpleDoubleProperty();
        this.vaAtt2Yend = new SimpleDoubleProperty();
        this.valPointX = new SimpleDoubleProperty();
        this.valPointY = new SimpleDoubleProperty();
        this.timeStep = new SimpleDoubleProperty();
    }

    @Override
    public void learnNormal(TimeSeries ts) {
        tsReg = ts;
        ArrayList<String> atts = ts.getAttributes();
        int len = ts.ts.get(ts.atts.get(0)).size();

        float vals[][] = new float[atts.size()][len];
        for (int i = 0; i < atts.size(); i++) {
            for (int j = 0; j < len; j++) {
                vals[i][j] =ts.getValueByTime(atts.get(i),j);
            }
        }

        for (int i = 0; i < atts.size(); i++) {
            for (int j = i + 1; j < atts.size(); j++) {
                float p = StatLib.pearson(vals[i], vals[j]);    //for the pearson

                if (Math.abs(p) > 0.9) {    //only if above o.
                    Point ps[] = toPoints(ts.getAttributeData(atts.get(i)), ts.getAttributeData(atts.get(j)));
                    Line lin_reg = StatLib.linear_reg(ps);
                    float threshold = findThreshold(ps, lin_reg) * 1.1f;     // 10% increase
                    CorrelatedFeatures c = new CorrelatedFeatures(atts.get(i), atts.get(j), p, lin_reg, threshold);
                    cf.add(c);
                }
            }
        }
    }

    private Point[] toPoints(ArrayList<Float> x, ArrayList<Float> y) {
        Point[] ps = new Point[x.size()];
        for (int i = 0; i < ps.length; i++)
            ps[i] = new Point(x.get(i), y.get(i));
        return ps;
    }

    private float findThreshold(Point ps[], Line rl) {
        float max = 0;
        for (int i = 0; i < ps.length; i++) {
            float d = Math.abs(ps[i].y - rl.f(ps[i].x));
            if (d > max)
                max = d;
        }
        return max;
    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        tsAnomal = ts;
        ArrayList<AnomalyReport> v = new ArrayList<>();

        for (CorrelatedFeatures c : cf) {
            ArrayList<Float> x = ts.getAttributeData(c.feature1);
            ArrayList<Float> y = ts.getAttributeData(c.feature2);
            for (int i = 0; i < x.size(); i++) {
                if (Math.abs(y.get(i) - c.lin_reg.f(x.get(i))) > c.threshold) {
                    String d = c.feature1 + "-" + c.feature2;
                    v.add(new AnomalyReport(d, (i + 1)));
                    if (!anomalyAndTimeStep.containsKey(c.feature1)) {
                        anomalyAndTimeStep.put(c.feature1, new ArrayList<>());
                        anomalyAndTimeStep.get(c.feature1).add(i + 1);

                    } else
                        anomalyAndTimeStep.get(c.feature1).add(i + 1);
                }
            }
        }

        return v;
    }

    public void initDataForGraphTimeChange() {
        valPointX.setValue(tsAnomal.getValueByTime(attribute1.getValue(), timeStep.intValue()));
        if (attribute2.getValue() != null)
            valPointY.setValue(tsAnomal.getValueByTime(attribute2.getValue(), timeStep.intValue()));
    }
    public void initDataForGraphAttChange() {
        regLineForCorrelateAttribute = getRegLine(attribute1.getValue(), attribute2.getValue());
        valAtt1X.setValue(tsReg.getMinFromAttribute(attribute1.getValue()));
        valAtt2Y.setValue(regLineForCorrelateAttribute.f(valAtt1X.floatValue()));
        vaAtt1Xend.setValue(tsReg.getMaxFromAttribute(attribute2.getValue()));
        vaAtt2Yend.setValue(regLineForCorrelateAttribute.f(vaAtt1Xend.floatValue()));
    }

    @Override
    public AnchorPane paint() {
        AnchorPane ap = new AnchorPane();
        //Data for BubbleChart
        LineChart<Number, Number> sc = new LineChart<>(new NumberAxis(), new NumberAxis());
        sc.setPrefHeight(210);
        sc.setPrefWidth(290);
        XYChart.Series pointsNormal = new XYChart.Series();//points for normal Flight
        XYChart.Series pointsAnomal = new XYChart.Series();//points for Anomaly parts
        XYChart.Series regLine = new XYChart.Series();//line
        sc.getData().addAll(pointsNormal, regLine, pointsAnomal);

        attribute1.addListener((ob, oldV, newV) -> {//to delete the old graph if attribute has changed
            attribute2.setValue(getCorrelateFeature(attribute1.getValue()));
            if (attribute2.getValue() != null) {
                sc.setVisible(true);
                initDataForGraphAttChange();
                timeStep.addListener((o, ov, nv) -> {
                    initDataForGraphTimeChange();
                    Platform.runLater(() -> {
                        if (!anomalyAndTimeStep.containsKey(attribute1.getValue())) {
                            pointsNormal.getData().add(new XYChart.Data(valPointX.doubleValue(), valPointY.doubleValue()));//points
                            regLine.getData().add(new XYChart.Data(valAtt1X.doubleValue(), valAtt2Y.doubleValue()));//reg first point
                            regLine.getData().add(new XYChart.Data(vaAtt1Xend.doubleValue(), vaAtt2Yend.doubleValue()));//reg sec point
                        } else {
                            if (!anomalyAndTimeStep.get(attribute1.getValue()).contains(timeStep.intValue())) {
                                pointsNormal.getData().add(new XYChart.Data(valPointX.doubleValue(), valPointY.doubleValue()));//points
                                regLine.getData().add(new XYChart.Data(valAtt1X.doubleValue(), valAtt2Y.doubleValue()));//reg first point
                                regLine.getData().add(new XYChart.Data(vaAtt1Xend.doubleValue(), vaAtt2Yend.doubleValue()));//reg sec point
                            } else {
                                pointsAnomal.getData().add(new XYChart.Data(valPointX.doubleValue(), valPointY.doubleValue()));//points of anomaly
                                regLine.getData().add(new XYChart.Data(valAtt1X.doubleValue(), valAtt2Y.doubleValue()));//reg first point
                                regLine.getData().add(new XYChart.Data(vaAtt1Xend.doubleValue(), vaAtt2Yend.doubleValue()));//reg sec point
                            }
                        }
                    });
                });
                if (!newV.equals(oldV)) {   //if change the attribute
                    pointsAnomal.getData().clear();
                    pointsNormal.getData().clear();
                    regLine.getData().clear();
                }
            }
            else if (attribute2.getValue() == null) {   // if the new att doesn't have correlate
                sc.setVisible(false);
            }
        });

        sc.setAnimated(false);
        sc.setCreateSymbols(true);
        ap.getChildren().add(sc);
        ap.getStylesheets().add("style.css");

        return ap;
    }

    public String getCorrelateFeature(String attribute1) {
        for (CorrelatedFeatures c : cf) {
            if (c.feature1.equals(attribute1))
                return c.feature2;
        }
        return null;
    }

    public Line getRegLine(String f1, String f2) {
        for (CorrelatedFeatures c : cf) {
            if (c.feature1.equals(f1) && c.feature2.equals(f2))
                return c.lin_reg;
        }
        return null;
    }
}
