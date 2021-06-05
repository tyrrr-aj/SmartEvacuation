package root.geometry;

public class Vector {
    private final double[] coords;

    public Vector(double[] coords) {
        this.coords = coords;
    }

    public double dot(Point point) {
        if (coords.length != 2) {
            throw new IllegalStateException("Only Vectors of length 2 can be dotted with Points");
        }

        return coords[0] * point.x() + coords[1] * point.y();
    }
}
