package utils;

import java.lang.Math;

public class StatLib {
    private static float sum(float[] arr) {
        float accumulator = 0;
        for (float value : arr) {
            accumulator += value;
        }
        return accumulator;
    }

    public static float avg(float[] arr) {
        return sum(arr) / arr.length;
    }

    public static float variance(float[] arr) {
        float avg = avg(arr);
        float sum = 0;
        for (float value : arr) {
            sum += (value - avg) * (value - avg);
        }
        return sum / arr.length;
    }

    public static float cov(float[] x, float[] y) {
        float xAvg = avg(x);
        float yAvg = avg(y);
        float sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += (x[i] - xAvg) * (y[i] - yAvg);
        }
        return sum / x.length;
    }

    public static float pearson(float[] x, float[] y) {
        float xVar = variance(x);
        float yVar = variance(y);
        if (xVar < 0 || yVar < 0) {
            return 0;
        }
        if (Math.sqrt(xVar) * Math.sqrt(yVar) == 0) {
            return 0;
        }
        return cov(x, y) / (float)(Math.sqrt(xVar) * Math.sqrt(yVar));
    }

    public static Line LinearRegression(Point[] points){
        float[] x =new float[points.length];
        float[] y =new float[points.length];
        for(int i=0;i<points.length;i++){
            x[i]=points[i].x;
            y[i]=points[i].y;
        }
        float a=cov(x,y)/ variance(x);
        float b=avg(y) - a*(avg(x));
        return new Line(a,b);
    }
}