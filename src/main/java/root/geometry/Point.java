package root.geometry;

import root.models.ConnectionDirection;

import static java.lang.Math.abs;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public ConnectionDirection getRelativeDirection(Point other) {
        double xDiff = x - other.x();
        double yDiff = y - other.y();

        if (abs(xDiff) > abs(yDiff)) {
            if (xDiff > 0) {
                return ConnectionDirection.LEFT;
            }
            else {
                return ConnectionDirection.RIGHT;
            }
        }
        else {
            if (yDiff > 0) {
                return ConnectionDirection.BOTTOM;
            }
            else {
                return ConnectionDirection.TOP;
            }
        }
    }
}
