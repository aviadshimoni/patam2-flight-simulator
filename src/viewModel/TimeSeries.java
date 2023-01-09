package viewModel;

import algorithms.CorrelatedFeatures;
import algorithms.Line;
import algorithms.Point;
import algorithms.StatLib;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSeries {

    public ArrayList<CorrelatedFeatures> cf;

    public Map<String, ArrayList<Float>> timeSeries;
    public Map<Integer, ArrayList<Float>> timeSeriesNumber;
    public ArrayList<String> rows;
    public ArrayList<String> attributes;
    int dataRowSize;

    public TimeSeries(){
        this.timeSeries = new HashMap<>();
        this.timeSeriesNumber = new HashMap<>();
        this.rows = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.dataRowSize = 0;
    }

    public TimeSeries(String csvFileName) {
        timeSeries = new HashMap<>();
        timeSeriesNumber = new HashMap<>();
        rows = new ArrayList<>();

        attributes = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(csvFileName));
            String line = in.readLine();
            int j = 0;
            for (String att : line.split(",")) {
                attributes.add(att);
                timeSeries.put(att, new ArrayList<>());
                timeSeriesNumber.put(j++, new ArrayList<>());
            }
            while ((line = in.readLine()) != null) {
                int i = 0;
                for (String val : line.split(",")) {
                    timeSeries.get(attributes.get(i)).add(Float.parseFloat(val));
                    timeSeriesNumber.get(i).add(Float.parseFloat(val));
                    i++;
                }
                rows.add(line);
            }
            dataRowSize = timeSeries.get(attributes.get(0)).size();

            in.close();
        } catch (IOException e) {
        }
    }

    public ArrayList<Float> getAttributeData(String name) {
        return timeSeries.get(name);
    }

    public ArrayList<String> getAttributes() {
        return attributes;
    }

    public int getSize() {// they both doing the exact same thing, but cuz we mixed code we need to change the name we call that function
        return dataRowSize;
    }

    public int getRowSize() {
        return dataRowSize;
    }

    public float getValueByTime(int index, int time) {

        float f = timeSeriesNumber.get(index).get(time);
        return timeSeriesNumber.get(index).get(time);
    }

    public float getValueByTime(String index, int time) {
        return timeSeries.get(index).get(time);
    }

    public float getMinFromAttribute(String val) {
        ArrayList<Float> lst = timeSeries.get(val);

        float minVal = timeSeries.get(val).get(0);//get the val of the first timeStep

        for (float f : lst) {
            if ((f < minVal))
                minVal = f;
        }
        return minVal;
    }

    public float getMaxFromAttribute(String val) {
        ArrayList<Float> lst = timeSeries.get(val);
        float maxVal = 0;
        for (float f : lst) {
            if (f > maxVal)
                maxVal = f;
        }
        return maxVal;
    }

    public ListProperty<Float> getDataOfAttUntilIndex(String s, int index){
        ListProperty<Float>dataUntilIndex= new SimpleListProperty<>();
        List<Float>lst= timeSeries.get(s).subList(0,index);
        dataUntilIndex.addAll(lst);
        return  dataUntilIndex;
    }

    public void checkCorrelate(TimeSeries ts) {
        cf = new ArrayList<>();
        ArrayList<String> atts = ts.getAttributes();
        int len = ts.timeSeries.get(ts.attributes.get(0)).size();

        float vals[][] = new float[atts.size()][len];
        for (int i = 0; i < atts.size(); i++) {
            for (int j = 0; j < len; j++) {
//                vals[i][j] = ts.getAttributeData(atts.get(i)).get(j);
                vals[i][j] =ts.getValueByTime(atts.get(i),j);
            }
        }

        for (int i = 0; i < atts.size(); i++) {
            for (int j = i + 1; j < atts.size(); j++) {
                float p = StatLib.pearson(vals[i], vals[j]);//for the pearson

                if (Math.abs(p) > 0.9) {//only if above o.
                    Point ps[] = toPoints(ts.getAttributeData(atts.get(i)), ts.getAttributeData(atts.get(j)));
                    Line lin_reg = StatLib.linear_reg(ps);
                    float threshold = findThreshold(ps, lin_reg) * 1.1f; // 10% increase
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
    public String getCorrelateFeature(String attribute1) {
        for (CorrelatedFeatures c : cf) {
            if (c.feature1.equals(attribute1))
                return c.feature2;
        }
        return null;
    }
}
