package algorithms;
public class Point {

    public float x;
    public float y;

    public Point(float x, float y) {
        this.x=x;
        this.y=y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public double distanceSquaredTo(final Point p) {
        final double DX = getX() - p.getX();
        final double DY = getY() - p.getY();
        return DX * DX + DY * DY;
    }

    public float distanceTo(final Point p) {
        return (float)Math.sqrt(distanceSquaredTo(p));
    }
}
