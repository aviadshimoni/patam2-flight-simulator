package algorithms;

import java.lang.Math;

public class StatLib {

    // Simple average
    public static float avg(float[] x){
        float sum=0;
        for(int i=0;i<x.length;sum+=x[i],i++);
        return sum/x.length;
    }

    // Returns the variance of X and Y
    public static float var(float[] x){
        float av=avg(x);
        float sum=0;
        for(int i=0;i<x.length;i++){
            sum+=x[i]*x[i];
        }
        return sum/x.length - av*av;
    }

    // returns the covariance of X and Y
    public static float cov(float[] x, float[] y){
        float sum=0;
        for(int i=0;i<x.length;i++){
            sum+=x[i]*y[i];
        }
        sum/=x.length;
        return sum - avg(x)*avg(y);
    }

    // returns the Pearson correlation coefficient of X and Y
    public static float pearson(float[] x, float[] y){
        if(var(x) < 0 || var(y) < 0)
            return 0;
        if(Math.sqrt(var(x))*Math.sqrt(var(y)) == 0)
            return 0;
        return (float) (cov(x,y)/(Math.sqrt(var(x))*Math.sqrt(var(y))));
    }

    // performs a linear regression and returns the line equation
    public static Line linear_reg(Point[] points){
        float x[]=new float[points.length];
        float y[]=new float[points.length];
        for(int i=0;i<points.length;i++){
            x[i]=points[i].x;
            y[i]=points[i].y;
        }
        float a=cov(x,y)/var(x);
        float b=avg(y) - a*(avg(x));
        return new Line(a,b);
    }

    // returns the deviation between point p and the line equation of the points
    public static float dev(Point p,Point[] points){
        Line l=linear_reg(points);
        return dev(p,l);
    }

    // returns the deviation between point p and the line
    public static float dev(Point p,Line l){
        return Math.abs(p.y-l.f(p.x));
    }

}