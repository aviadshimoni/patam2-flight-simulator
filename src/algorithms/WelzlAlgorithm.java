package algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WelzlAlgorithm {
    private Random rand = new Random();

    public Circle welzl(final List<Point> ps) {
        return welzlHelper(ps, new ArrayList<>());
    }

    private Circle welzlHelper(final List<Point> ps, final List<Point> boundary) {
        Circle minCircle;

        if (boundary.size() == 3) {
            minCircle = new Circle(boundary.get(0), boundary.get(1), boundary.get(2));
        }

        else if (ps.isEmpty() && boundary.size() == 2) {
            minCircle = new Circle(boundary.get(0), boundary.get(1));
        }

        else if (ps.size() == 1 && boundary.isEmpty()) {
            minCircle = new Circle(ps.get(0).getX(), ps.get(0).getY(), 0);
        }

        else if (ps.size() == 1 && boundary.size() == 1) {
            minCircle = new Circle(ps.get(0), boundary.get(0));
        }

        else {
            Point p = ps.remove(rand.nextInt(ps.size()));
            minCircle = welzlHelper(ps, boundary);

            if (minCircle != null && !minCircle.isInside(p)) {
                boundary.add(p);
                minCircle = welzlHelper(ps, boundary);
                boundary.remove(p);
                ps.add(p);
            }
        }
        return minCircle;
    }
}
