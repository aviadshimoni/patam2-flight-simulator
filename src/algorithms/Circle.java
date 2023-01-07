package algorithms;

class Circle {

    private static final double MULTIPLICATIVE_EPSILON = 1 + 1e-14;

    public Point center;
    public double radius;

    public Circle(float x, float y, double radius) {
        center = new Point(x, y);
        this.radius = radius;
    }

    public Circle(final Point p1, final Point p2) {
        center = new Point(((p1.getX() + p2.getX()) * (float) 0.5), ((p1.getY() + p2.getY()) * (float) 0.5));
        radius = center.distanceTo(p1);
    }

    public Circle(final Point p1, final Point p2, final Point p3) {
        final float p2YMp1Y = p2.getY() - p1.getY();
        final float p3YMp2Y = p3.getY() - p2.getY();

        if (p2YMp1Y == 0.0 || p3YMp2Y == 0.0) {
            center = new Point(0, 0);
            radius = 0;
        }

        else {
            final float a = -(p2.getX() - p1.getX()) / p2YMp1Y;
            final float aP = -(p3.getX() - p2.getX()) / p3YMp2Y;
            final float aPMinusA = aP - a;

            if (aPMinusA == 0.0) {
                center = new Point(0, 0);
                radius = 0;
            }
            else {
                final float p2SquaredX = p2.getX() * p2.getX();
                final float p2SquaredY = p2.getY() * p2.getY();

                final float b = ((p2SquaredX - p1.getX() * p1.getX() + p2SquaredY - p1.getY() * p1.getY()) /
                        ((float) 2.0 * p2YMp1Y));
                final float bP = ((p3.getX() * p3.getX() - p2SquaredX + p3.getY() * p3.getY() - p2SquaredY) /
                        ((float) 2.0 * p3YMp2Y));

                final float xc = (b - bP) / aPMinusA;
                final float yc = a * xc + b;

                final float dxc = p1.getX() - xc;
                final float dyc = p1.getY() - yc;

                center = new Point(xc, yc);
                radius = Math.sqrt(dxc * dxc + dyc * dyc);
            }
        }
    }

    public boolean isInside(Point p) {
        return center.distanceTo(p) <= radius * MULTIPLICATIVE_EPSILON;
    }

    @Override
    public String toString() {
        return center.toString() + ", Radius: " + radius;
    }

}
