package root.neo4j;

import root.geometry.Point;

import java.util.List;

public class AreaResult {
    private int id;
    private List<Point> cornerCoordinates;

    public AreaResult(int id, List<Point> cornerCoordinates) {
        this.id = id;
        this.cornerCoordinates = cornerCoordinates;
    }

    public int getId() {
        return id;
    }

    public List<Point> getCornerCoordinates() {
        return cornerCoordinates;
    }

    @Override
    public String toString() {
        var result = "ID: " + this.id + ", coordinates: \n";

        for(var coords : this.cornerCoordinates) {
            result += "[" + coords.x() + ", " + coords.y() + "]\n";
        }

        return result;
    }

    public Point getCenterCoord() {
        var centerX = cornerCoordinates
                .stream()
                .mapToDouble(Point::x)
                .average()
                .getAsDouble();

        var centerY = cornerCoordinates
                .stream()
                .mapToDouble(Point::y)
                .average()
                .getAsDouble();

        return new Point(centerX, centerY);
    }
}
