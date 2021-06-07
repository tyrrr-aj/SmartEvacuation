package root.neo4j;

import root.geometry.GeometryUtils;
import root.geometry.Line;
import root.geometry.Point;
import root.geometry.Vector;
import root.models.ConnectionDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AreaResult {
    private final int id;
    private final String floorId;
    protected List<Point> cornerCoordinates; // stored clockwise, starting from left upper corner
    private final Point centerCoord;

    public AreaResult(int id, String floorId, List<Point> cornerCoordinates) {
        this.id = id;
        this.floorId = floorId;
        this.centerCoord = findCenterCoord(cornerCoordinates);
        this.cornerCoordinates = getCornersInOrder(cornerCoordinates);
        limitCornersNumberToFour(); // workaround until non-rectangular areas splitting is implemented
    }

    public int getId() {
        return id;
    }

    public String getFloorId() {
        return floorId;
    }

    public List<Point> getCornerCoordinates() {
        return cornerCoordinates;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("ID: " + this.id + ", coordinates: \n");

        for(var coords : this.cornerCoordinates) {
            result.append("[").append(coords.x()).append(", ").append(coords.y()).append("]\n");
        }

        return result.toString();
    }

    public Point getCenterCoord() {
        return centerCoord;
    }

    public ConnectionDirection getRelativeDirection(Point doorCoord) {
        Point ownCenter = getCenterCoord();

        if (ownCenter.y() < doorCoord.y()) {
            if (ownCenter.x() < doorCoord.x()) {
                if (GeometryUtils.orient(ownCenter, RightUpperCorner(), doorCoord) < 0) {
                    return ConnectionDirection.RIGHT;
                }
                else {
                    return ConnectionDirection.TOP;
                }
            }
            else if (GeometryUtils.orient(ownCenter, LeftUpperCorner(), doorCoord) < 0) {
                return ConnectionDirection.TOP;
            }
            else {
                return ConnectionDirection.LEFT;
            }
        }
        else {
            if (ownCenter.x() < doorCoord.x()) {
                if (GeometryUtils.orient(ownCenter, RightLowerCorner(), doorCoord) < 0) {
                    return ConnectionDirection.BOTTOM;
                } else {
                    return ConnectionDirection.RIGHT;
                }
            }
            else {
                if (GeometryUtils.orient(ownCenter, LeftLowerCorner(), doorCoord) < 0) {
                    return ConnectionDirection.LEFT;
                }
                else {
                    return ConnectionDirection.BOTTOM;
                }
            }
        }
    }

    public Line getHorizontalAxis() {
        double leftX = (LeftUpperCorner().x() + LeftLowerCorner().x()) / 2;
        double leftY = (LeftUpperCorner().y() + LeftLowerCorner().y()) / 2;
        double rightX = (RightUpperCorner().x() + RightLowerCorner().x()) / 2;
        double rightY = (RightUpperCorner().y() + RightLowerCorner().y()) / 2;

        return new Line(new Point(leftX, leftY), new Point(rightX, rightY));
    }

    public Line getVerticalAxis() {
        double upperX = (LeftUpperCorner().x() + RightUpperCorner().x()) / 2;
        double upperY = (LeftUpperCorner().y() + RightUpperCorner().y()) / 2;
        double lowerX = (LeftLowerCorner().x() + RightLowerCorner().x()) / 2;
        double lowerY = (LeftLowerCorner().y() + RightLowerCorner().y()) / 2;

        return new Line(new Point(upperX, upperY), new Point(lowerX, lowerY));
    }

    public Point LeftUpperCorner() {
        return cornerCoordinates.get(0);
    }

    public Point RightUpperCorner() {
        return cornerCoordinates.get(1);
    }

    public Point RightLowerCorner() {
        return cornerCoordinates.get(2);
    }

    public Point LeftLowerCorner() {
        return cornerCoordinates.get(3);
    }

    private Point findCenterCoord(List<Point> corners) {
        var centerX = corners
                .stream()
                .mapToDouble(Point::x)
                .average()
                .getAsDouble();

        var centerY = corners
                .stream()
                .mapToDouble(Point::y)
                .average()
                .getAsDouble();

        return new Point(centerX, centerY);
    }

    private List<Point> getCornersInOrder(List<Point> corners) {
        Point LUCorner = findLeftUpperCorner(corners);

        List<Point> orderedCorners = corners
                .stream()
                .sorted(Comparator.comparing(Function.identity(), (p1, p2) -> GeometryUtils.orient(centerCoord, p1, p2)))
                .collect(Collectors.toCollection(ArrayList::new));

        int leftUpperCornerIndex = orderedCorners.lastIndexOf(LUCorner);
        Collections.rotate(orderedCorners, -leftUpperCornerIndex);
        return orderedCorners;
    }

    private Point findLeftUpperCorner(List<Point> corners) {
        return corners
                .stream()
                .sorted(Comparator.comparing(Point::y))
                .skip(2)
                .min(Comparator.comparing(Point::x))
                .get();
    }

    private void limitCornersNumberToFour() {
        if (cornerCoordinates.size() > 4) {
            List<Point> fourCorners = new ArrayList<>(4);
            int n = cornerCoordinates.size();

            for (int i = 0; i < n; i++) {
                if (GeometryUtils.orient(cornerCoordinates.get(i), cornerCoordinates.get((i + 1) % n), cornerCoordinates.get((i + 2) % n)) != 0) {
                    fourCorners.add(cornerCoordinates.get((i + 1) % n));
                }
            }

            Point LUCorner = findLeftUpperCorner(fourCorners);

            int leftUpperCornerIndex = fourCorners.lastIndexOf(LUCorner);
            Collections.rotate(fourCorners, -leftUpperCornerIndex);
            cornerCoordinates = fourCorners;
        }
    }
}
