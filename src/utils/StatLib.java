package utils;


public class StatLib
{

    public static float avg(float[] x)
    {
        float sum = 0;
        for (int i=0; i < x.length; i++)
            sum += x[i];

        return (float)(sum/x.length);
    }

    public static float var(float[] x)
    {
        float mean = avg(x);
        float sqDiff = 0;
        for (int i=0; i<x.length; i++)
            sqDiff += (x[i]-mean)*(x[i]-mean);
        return sqDiff / x.length;

    }

    public static float cov(float[] x, float[] y)
    {
        float mean_x = avg(x);
        float mean_y = avg(y);
        float temp = 0;
        for(int i=0; i < x.length; i++)
            temp += (x[i]-mean_x)*(y[i]-mean_y);

        return temp/x.length;
    }


    public static float pearson(float[] x, float[] y){
        return (float) (cov(x,y)/(Math.sqrt(var(x))*Math.sqrt(var(y))));
    }

    public static Line linearReg(Point[] points){
        float[] arrayX = new float[points.length];
        float[] arrayY = new float[points.length];
        for(int i=0;i<points.length;i++) {
            arrayX[i] = points[i].x;
            arrayY[i] = points[i].y;
        }

        float a = cov(arrayX,arrayY)/var(arrayX);
        float b = avg(arrayY) - (a*avg(arrayX));
        return new Line(a,b);
    }

    public static float dev(Point p,Point[] points){
        Line examinedLine = linearReg(points);
        float LineYPoint = examinedLine.f(p.x);
        return Math.abs(LineYPoint-p.y);

    }

    public static float dev(Point p,Line l){
        return Math.abs(l.f(p.x)-p.y);
    }

}