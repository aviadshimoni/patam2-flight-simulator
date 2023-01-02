package algorithms;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import viewModel.TimeSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

public class hybridAlgorithm {

    HashMap<String, CorrelatedFeatureForAll> attALG;
    HashMap<CorrelatedFeatureForAll, Circle> circlesMap;
    HashMap<String, ArrayList<Integer>> anomalyMapByAtt;

    SimpleAnomalyDetector ad;
    ZScoreAlgorithm zScore;


    // Regression
    public TimeSeries tsReg;
    public TimeSeries tsRegAnomal;

    // Hybrid
    HashMap<String, CorrelatedFeatures> cfmore95;
    public TimeSeries tsWezleAnomal;

    // ZScore
    public TimeSeries tsZscore;
    public TimeSeries tsZscoreAnomal;
    public HashMap<String, ArrayList<Float>> ZScoreReg;
    public HashMap<String, ArrayList<Integer>> ZScoreAnomaly;

    public StringProperty attribute1;
    public StringProperty attribute2;

    public Line regLineForCorrelateAttribute;

    public DoubleProperty valAtt1X;     //static for line- minValX
    public DoubleProperty valAtt2Y;     //static for line- minValY
    public DoubleProperty vaAtt1Xend;   //static for line -maxValX
    public DoubleProperty vaAtt2Yend;   //static for line -maxValY

    public DoubleProperty valPointX;
    public DoubleProperty valPointY;

    public DoubleProperty timeStep;


    public hybridAlgorithm() {
        this.circlesMap = new HashMap<>();
        this.anomalyMapByAtt = new HashMap<>();

        this.attALG = new HashMap<>();
        this.cfmore95 = new HashMap<>();

        this.ad = new SimpleAnomalyDetector();
        this.zScore = new ZScoreAlgorithm();

        this.tsReg = new TimeSeries();
        this.tsRegAnomal = new TimeSeries();
        this.tsZscore = new TimeSeries();
        this.tsZscoreAnomal = new TimeSeries();
        this.tsWezleAnomal = new TimeSeries();

        attribute1 = new SimpleStringProperty();
        attribute2 = new SimpleStringProperty();

        this.valAtt1X = new SimpleDoubleProperty();
        this.valAtt2Y = new SimpleDoubleProperty();
        this.vaAtt1Xend = new SimpleDoubleProperty();
        this.vaAtt2Yend = new SimpleDoubleProperty();

        this.valPointX = new SimpleDoubleProperty();
        this.valPointY = new SimpleDoubleProperty();

        this.timeStep = new SimpleDoubleProperty();
    }

    private List<Point> toListPoints(List<Float> x, List<Float> y) {
        List<Point> lst = new ArrayList<>();
        for (int i = 0; i < x.size(); i++)
            lst.add(new Point(x.get(i), y.get(i)));
        return lst;
    }

    public void learnNormal(TimeSeries ts) {
        WelzlAlgorithm algorithm = new WelzlAlgorithm();

        findCorrelation(ts);

        int indexReg = 0;
        int indexZS = 0;

        for (String att : attALG.keySet()) {
            Integer index1 = ts.atts.indexOf(att);

            if (attALG.get(att).nameALG.equals("Regression")) {
                String att2 = attALG.get(att).feature2;
                tsReg.ts.put(att, ts.ts.get(att));
                tsReg.tsNum.put(indexReg++, ts.tsNum.get(index1));
                Integer index2 = ts.atts.indexOf(att2);
                tsReg.ts.put(att2, ts.ts.get(att));
                tsReg.tsNum.put(indexReg++, ts.tsNum.get(index2));
                tsReg.atts.add(att);
                tsReg.atts.add(att2);
            } else if (attALG.get(att).nameALG.equals("ZScore")) {
                tsZscore.ts.put(att, ts.ts.get(att));
                tsZscore.tsNum.put(indexZS++, ts.tsNum.get(index1));
                tsZscore.atts.add(att);
            }
        }

        ad.learnNormal(tsReg);
        zScore.learnNormal(tsZscore);
        ZScoreReg = zScore.getZScoreReg();
        ZScoreAnomaly = zScore.getZscoreAnomal();

        for (String key : attALG.keySet()) {    // Activate welze on all the attribute with 0.5-0.95 correlation
            if (attALG.get(key).nameALG.equals("Welzl"))
                circlesMap.put(attALG.get(key), algorithm.welzl(toListPoints(ts.getAttributeData(attALG.get(key).feature1), ts.getAttributeData(attALG.get(key).feature2))));
        }
    }

    public void findCorrelation(TimeSeries ts) {
        ArrayList<String> atts = ts.getAttributes();
        int len = ts.getRowSize();

        float vals[][] = new float[atts.size()][len];

        for (int i = 0; i < atts.size(); i++) {
            for (int j = 0; j < ts.getRowSize(); j++) {
                vals[i][j] = ts.getAttributeData(atts.get(i)).get(j);
            }
        }

        for (int i = 0; i < atts.size(); i++) {
            for (int j = i + 1; j < atts.size(); j++) {
                float p = StatLib.pearson(vals[i], vals[j]);    //for the pearson
                float threshold;
                if (Math.abs(p) >= 0.95) {

                    Point ps[] = toPoints(ts.getAttributeData(atts.get(i)), ts.getAttributeData(atts.get(j)));
                    Line lin_reg = StatLib.linear_reg(ps);
                    threshold = findThreshold(ps, lin_reg) * 1.1f; // 10% increase
                    CorrelatedFeatures c = new CorrelatedFeatures(atts.get(i), atts.get(j), p, lin_reg, threshold);//att1_att2_pearsonCorrelate_null_threshold(the max one)

                    if (!attALG.containsKey(atts.get(i))) {
                        attALG.put(atts.get(i), new CorrelatedFeatureForAll(atts.get(i), atts.get(j), "Regression", p)); // override down level if was exist
                        cfmore95.put(atts.get(i), c);
                    }  // if contain the attribute we'll take the max

                    else if (attALG.get(atts.get(i)).corrlation < Math.abs(p))//if the the val with the different att is higher,we'll tack the other att
                    {
                        attALG.get(atts.get(i)).feature2 = atts.get(j);//change the name of the correlate att
                        attALG.get(atts.get(i)).corrlation = Math.abs(p);//change the val of correlation
                        attALG.get(atts.get(i)).nameALG = "Regression";
                        cfmore95.put(atts.get(i), c);
                    }
                } else if ((0.5 <= Math.abs(p)) && (Math.abs(p) < 0.95)) {// 0.5<val<0.95
                    if (!attALG.containsKey(atts.get(i))) {
                        CorrelatedFeatureForAll ca = new CorrelatedFeatureForAll(atts.get(i), atts.get(j), "Welzl", Math.abs(p));
                        attALG.put(atts.get(i), ca);

                    }  // if contain the attribute we'll take the max

                    else if (attALG.get(atts.get(i)).corrlation < Math.abs(p))//if the the val with the different att is higher,we'll take the other att
                    {
                        attALG.get(atts.get(i)).feature2 = atts.get(j);
                        attALG.get(atts.get(i)).corrlation = Math.abs(p);
                        attALG.get(atts.get(i)).nameALG = "Welzl";
                    }
                } else if (Math.abs(p) < 0.5) {
                    if (!attALG.containsKey(atts.get(i))) {
                        CorrelatedFeatureForAll ca = new CorrelatedFeatureForAll(atts.get(i), atts.get(j), "ZScore", Math.abs(p));
                        attALG.put(atts.get(i), ca);
                    }  // if contain the attribute we'll take the max
                    else if (attALG.get(atts.get(i)).corrlation < Math.abs(p))//if the the val with the different att is higher,we'll tack the other att
                    {
                        attALG.get(atts.get(i)).feature2 = atts.get(j);
                        attALG.get(atts.get(i)).corrlation = Math.abs(p);
                        attALG.get(atts.get(i)).nameALG = "ZScore";
                    }
                }
            }
        }
    }

    private float findThreshold(Point ps[], Line rl) {  // To find Anomal
        float max = 0;
        for (int i = 0; i < ps.length; i++) {
            float d = Math.abs(ps[i].y - rl.f(ps[i].x));
            if (d > max)
                max = d;
        }
        return max;
    }


    public List<AnomalyReport> detect(TimeSeries ts) {
        int indexReg = 0;
        int indexZS = 0;
        int indexWZ = 0;

        for (String att : attALG.keySet()) {
            Integer index1 = ts.atts.indexOf(att);

            if (attALG.get(att).nameALG.equals("Regression")) {
                String att2 = attALG.get(att).feature2;
                tsRegAnomal.ts.put(att, ts.ts.get(att));
                //Integer index1 = ts.atts.indexOf(att);
                tsRegAnomal.tsNum.put(indexReg++, ts.tsNum.get(index1));
                Integer index2 = ts.atts.indexOf(att2);
                tsRegAnomal.ts.put(att2, ts.ts.get(att));
                tsRegAnomal.tsNum.put(indexReg++, ts.tsNum.get(index2));

                tsRegAnomal.atts.add(att);
                tsRegAnomal.atts.add(att2);
            } else if (attALG.get(att).nameALG.equals("ZScore")) {
                tsZscoreAnomal.ts.put(att, ts.ts.get(att));
                tsZscoreAnomal.tsNum.put(indexZS++, ts.tsNum.get(index1));
                tsZscoreAnomal.atts.add(att);
            } else if (attALG.get(att).nameALG.equals("Welzl")) {
                String att2 = attALG.get(att).feature2;
                tsWezleAnomal.ts.put(att, ts.ts.get(att));
                tsWezleAnomal.tsNum.put(indexWZ++, ts.tsNum.get(index1));
                Integer index2 = ts.atts.indexOf(att2);
                tsWezleAnomal.ts.put(att2, ts.ts.get(att));
                tsWezleAnomal.tsNum.put(indexWZ++, ts.tsNum.get(index2));

                tsWezleAnomal.atts.add(att);
                tsWezleAnomal.atts.add(att2);
            }
        }

        List<AnomalyReport> lst = new ArrayList<>();
        lst.addAll(ad.detect(tsRegAnomal));
        lst.addAll(zScore.detect(tsZscoreAnomal));

        for (CorrelatedFeatureForAll c : circlesMap.keySet()) {
            ArrayList<Integer> anomalyLst = new ArrayList<>();
            List<Point> ps = toListPoints(ts.getAttributeData(c.feature1), ts.getAttributeData(c.feature2));

            for (int i = 0; i < ps.size(); i++) {
                if (!circlesMap.get(c).isInside(ps.get(i))) {
                    lst.add(new AnomalyReport(c.feature1 + "-" + c.feature2, i));
                    anomalyLst.add(i);
                }
            }
            anomalyMapByAtt.put(c.feature1, anomalyLst);
        }
        return lst;
    }

    public Circle getCircle(String att1) {
        for (CorrelatedFeatureForAll cf : circlesMap.keySet()) {
            if (cf.feature1.equals(att1) || cf.feature2.equals(att1))
                return circlesMap.get(cf);
        }
        return null;
    }

    public void initDataForGraphTimeChange() {
        if (attALG.get(attribute1.getValue()).nameALG.equals("Regression")) {
            valPointX.setValue(tsRegAnomal.getValueByTime(attribute1.getValue(), timeStep.intValue()));
            if (attribute2.getValue() != null)
                valPointY.setValue(tsRegAnomal.getValueByTime(attribute2.getValue(), timeStep.intValue()));
        } else if (attALG.get(attribute1.getValue()).nameALG.equals("Welzl")) {
            valPointX.setValue(tsWezleAnomal.getValueByTime(attribute1.getValue(), timeStep.intValue()));
            if (attribute2.getValue() != null)
                valPointY.setValue(tsWezleAnomal.getValueByTime(attribute2.getValue(), timeStep.intValue()));
        }
    }

    public void initDataForGraphAttChange() {
        regLineForCorrelateAttribute = cfmore95.get(attribute1.getValue()).lin_reg;
        valAtt1X.setValue(tsReg.getMinFromAttribute(attribute1.getValue()));
        valAtt2Y.setValue(regLineForCorrelateAttribute.f(valAtt1X.floatValue()));
        vaAtt1Xend.setValue(tsReg.getMaxFromAttribute(attribute2.getValue()));
        vaAtt2Yend.setValue(regLineForCorrelateAttribute.f(vaAtt1Xend.floatValue()));
    }


    public AnchorPane paint() {
        AnchorPane board = new AnchorPane();

        //data for CircleALG

        NumberAxis X = new NumberAxis();
        X.setForceZeroInRange(false);
        NumberAxis Y = new NumberAxis();
        Y.setForceZeroInRange(false);
        BubbleChart<Number, Number> circleGraph = new BubbleChart(X, Y);

        circleGraph.setAnimated(false);
        circleGraph.setPrefSize(250, 250);
        XYChart.Series<Number, Number> seriesPoints = new XYChart.Series();
        XYChart.Series<Number, Number> seriesPointsAnomal = new XYChart.Series();
        XYChart.Series<Number, Number> seriesCircle = new XYChart.Series();
        circleGraph.getData().addAll(seriesPoints, seriesCircle);

        //Data for Zscore:
        LineChart<Number, Number> sc = new LineChart<>(new NumberAxis(), new NumberAxis());
        sc.setAnimated(false);
        sc.setCreateSymbols(false);
        sc.setPrefSize(290, 218);
        XYChart.Series line = new XYChart.Series();
        XYChart.Series lineAnomal = new XYChart.Series();
        sc.getData().addAll(line, lineAnomal);
        lineAnomal.getNode().setStyle("-fx-stroke: #01aa18;");
        line.getNode().setStyle("-fx-stroke: #ef2626;");

        // Data for Reg:
        LineChart<Number, Number> regBoard = new LineChart<>(new NumberAxis(), new NumberAxis());
        regBoard.setPrefSize(290, 218);
        XYChart.Series pointsNormal = new XYChart.Series(); //points for normal Flight
        XYChart.Series pointsAnomal = new XYChart.Series(); //points for Anomaly parts
        XYChart.Series regLine = new XYChart.Series();  //line
        regBoard.getData().addAll(pointsNormal, regLine, pointsAnomal);

        board.getStylesheets().add("style.css");

        sc.setVisible(false);
        regBoard.setVisible(false);
        circleGraph.setVisible(false);

        attribute1.addListener((ob, oldV, newV) -> {    //to delete the old graph if attribute has changed
            if (oldV != null && !oldV.equals(newV)) {
                seriesCircle.getData().clear();
                seriesPoints.getData().clear();
                seriesPointsAnomal.getData().clear();
                pointsNormal.getData().clear();
                pointsAnomal.getData().clear();
                regLine.getData().clear();
                line.getData().clear();
                lineAnomal.getData().clear();
            }

            attribute2.setValue(attALG.get(attribute1.getValue()).feature2);

            if (attribute2.getValue() != null) {
                if (attALG.get(newV).nameALG.equals("Regression"))
                    initDataForGraphAttChange();

                timeStep.addListener((o, ov, nv) -> {
                    initDataForGraphTimeChange();
                    if (attALG.get(attribute1.getValue().toString()).nameALG.equals("Welzl")) {
                        sc.setVisible(false);
                        regBoard.setVisible(false);
                        circleGraph.setVisible(true);

                        pointsAnomal.getData().clear();
                        pointsAnomal.getData().clear();
                        regLine.getData().clear();
                        line.getData().clear();
                        lineAnomal.getData().clear();

                        double radius = getCircle(attribute1.getValue()).radius;
                        float x = getCircle(attribute1.getValue()).center.x;
                        float y = getCircle(attribute1.getValue()).center.y;

                        Platform.runLater(() -> {
                            if (!anomalyMapByAtt.containsKey(attribute1.getValue())) {
                                seriesCircle.getData().add(new XYChart.Data(x, y, radius));
                                seriesPoints.getData().add(new XYChart.Data(valPointX.doubleValue(), valPointY.doubleValue(), (radius / 20)));//points
                            } else {    // if there are anomalies
                                if (!anomalyMapByAtt.get(attribute1.getValue()).contains(timeStep.intValue())) {
                                    seriesCircle.getData().add(new XYChart.Data(x, y, radius));
                                    seriesPoints.getData().add(new XYChart.Data(valPointX.doubleValue(), valPointY.doubleValue(), (radius / 20)));//points
                                } else {
                                    seriesCircle.getData().add(new XYChart.Data(x, y, radius));
                                    seriesPointsAnomal.getData().add(new XYChart.Data(valPointX.doubleValue(), valPointY.doubleValue(), 0.2));//points of anomaly
                                }
                            }
                        });


                    } else if (attALG.get(attribute1.getValue().toString()).nameALG.equals("Regression")) {
                        sc.setVisible(false);
                        circleGraph.setVisible(false);
                        regBoard.setVisible(true);

                        seriesCircle.getData().clear();
                        seriesPoints.getData().clear();
                        seriesPointsAnomal.getData().clear();

                        Platform.runLater(() -> {
                            if (!ad.anomalyAndTimeStep.containsKey(attribute1.getValue())) {
                                pointsNormal.getData().add(new XYChart.Data(valPointX.doubleValue(), valPointY.doubleValue()));//points
                                regLine.getData().add(new XYChart.Data(valAtt1X.doubleValue(), valAtt2Y.doubleValue()));//reg first point
                                regLine.getData().add(new XYChart.Data(vaAtt1Xend.doubleValue(), vaAtt2Yend.doubleValue()));//reg sec point
                            } else {
                                if (!ad.anomalyAndTimeStep.get(attribute1.getValue()).contains(timeStep.intValue())) {
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

                    } else if (attALG.get(attribute1.getValue().toString()).nameALG.equals("ZScore")) {
                        sc.setVisible(true);
                        regBoard.setVisible(false);
                        circleGraph.setVisible(false);

                        seriesCircle.getData().clear();
                        seriesPoints.getData().clear();
                        seriesPointsAnomal.getData().clear();

                        Platform.runLater(() -> {
                            if (!ZScoreAnomaly.containsKey(attribute1.getValue())) {// i dont think it's work
                                lineAnomal.getData().add(new XYChart.Data<>(timeStep.getValue(), ZScoreReg.get(attribute1.getValue().toString()).get(timeStep.intValue())));
                            } else {
                                if (ZScoreAnomaly.get(attribute1.getValue()).contains(timeStep.intValue()))//if we are at att with anomal and there is anomal in the present time
                                    line.getData().add(new XYChart.Data<>(timeStep.getValue(), tsZscoreAnomal.ts.get(attribute1.getValue().toString()).get(timeStep.intValue())));
                                else
                                    lineAnomal.getData().add(new XYChart.Data<>(timeStep.getValue(), tsZscoreAnomal.ts.get(attribute1.getValue().toString()).get(timeStep.intValue())));
                            }
                        });

                        if (!newV.equals(oldV)) {   //if change the attribute
                            line.getData().clear();
                        }
                    }
                });
            }
        });

        board.getChildren().addAll(circleGraph, sc, regBoard);
        return board;
    }

    private Point[] toPoints(ArrayList<Float> x, ArrayList<Float> y) {
        Point[] ps = new Point[x.size()];
        for (int i = 0; i < ps.length; i++)
            ps[i] = new Point(x.get(i), y.get(i));
        return ps;
    }
}
