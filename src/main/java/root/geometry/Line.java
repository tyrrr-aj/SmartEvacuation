package root.geometry;

public class Line { // y = ax + b
    private double a;
    private double b;
    private boolean isVertical = false;

    public Line(Point P1, Point P2) {
        if (P1.x() - P2.x() == 0.0) {
            isVertical = true;
            b = P1.x();
        }
        else {
            a = (P1.y() - P2.y()) / (P1.x() - P2.x());
            b = P1.y() - a * P1.x();
        }
    }

    public Point symmetricalPoint(Point source) {
        if (isVertical) {
            return new Point(2 * b - source.x(), source.y());
        }
        else {
            return new Point(
                    ((1 - a * a) / (1 + a * a)) * source.x() + (2 * a / (1 + a * a)) * source.y(),
                    (2 * a / (1 + a * a)) * source.x() + ((1 - a * a) / (1 + a * a)) * source.y()
            );
        }
    }

    public boolean pointLiesEarlierAlong(Point point, Point other) {
        Vector direction = directionVector();
        return direction.dot(point) < direction.dot(other);
    }

    private Vector directionVector() {
        double[] coords = isVertical ? new double[] {1.0, a + b} : new double[] {0.0, -1.0};
        return new Vector(coords);
    }
}
