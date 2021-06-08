package root.neo4j.corridors;

import root.geometry.Point;
import root.models.ConnectionDirection;
import root.neo4j.AreaResult;
import root.neo4j.ConnectionResult;

import java.util.LinkedList;
import java.util.List;

public class SubAreaResult extends AreaResult {
    private AreaResult topNeighbour;
    private AreaResult bottomNeighbour;
    private AreaResult leftNeighbour;
    private AreaResult rightNeighbour;

    private static int nextSuffix = 0;

    SubAreaResult(AreaResult sourceAreaResult, List<Point> cornerCoordinates, AreaResult neighbour, AreaResult bottomNeighbour, AreaResult leftNeighbour, AreaResult rightNeighbour) {
        super(sourceAreaResult.getId() * 10 + nextSuffix, sourceAreaResult.getFloorId(), cornerCoordinates);
        topNeighbour = neighbour;
        this.bottomNeighbour = bottomNeighbour;
        this.leftNeighbour = leftNeighbour;
        this.rightNeighbour = rightNeighbour;

        nextSuffix += 1;
    }

    public SubAreaResult(SubAreaResult cloningSource) {
        super(cloningSource.getId() * 10 + nextSuffix, cloningSource.getFloorId(), cloningSource.getCornerCoordinates());
        topNeighbour = cloningSource.topNeighbour;
        bottomNeighbour = cloningSource.bottomNeighbour;
        leftNeighbour = cloningSource.leftNeighbour;
        rightNeighbour = cloningSource.rightNeighbour;

        nextSuffix += 1;
    }

    public AreaResult getTopNeighbour() {
        return topNeighbour;
    }

    public void setTopNeighbour(AreaResult topNeighbour) {
        this.topNeighbour = topNeighbour;
    }

    public AreaResult getBottomNeighbour() {
        return bottomNeighbour;
    }

    public void setBottomNeighbour(AreaResult bottomNeighbour) {
        this.bottomNeighbour = bottomNeighbour;
    }

    public AreaResult getLeftNeighbour() {
        return leftNeighbour;
    }

    public void setLeftNeighbour(AreaResult leftNeighbour) {
        this.leftNeighbour = leftNeighbour;
    }

    public AreaResult getRightNeighbour() {
        return rightNeighbour;
    }

    public void setRightNeighbour(AreaResult rightNeighbour) {
        this.rightNeighbour = rightNeighbour;
    }

    public void setTopBoundary(Point boundaryPoint) {
        Point oldLUCorner = LeftUpperCorner();
        Point oldRUCorner = RightUpperCorner();

        Point newLUCorner = new Point(oldLUCorner.x(), boundaryPoint.y());
        Point newRUCorner = new Point(oldRUCorner.x(), boundaryPoint.y());

        cornerCoordinates.set(0, newLUCorner);
        cornerCoordinates.set(1, newRUCorner);
    }

    public void setBottomBoundary(Point boundaryPoint) {
        Point oldLBCorner = LeftLowerCorner();
        Point oldRBCorner = RightLowerCorner();

        Point newLBCorner = new Point(oldLBCorner.x(), boundaryPoint.y());
        Point newRBCorner = new Point(oldRBCorner.x(), boundaryPoint.y());

        cornerCoordinates.set(2, newRBCorner);
        cornerCoordinates.set(3, newLBCorner);
    }

    public List<ConnectionResult> getBidirectionalConnections() {
        List<ConnectionResult> connections = new LinkedList<>();

        if (topNeighbour != null) {
            connections.add(new ConnectionResult(this, topNeighbour, ConnectionDirection.TOP));
            connections.add(new ConnectionResult(topNeighbour, this, ConnectionDirection.BOTTOM));
        }

        if (bottomNeighbour != null) {
            connections.add(new ConnectionResult(this, bottomNeighbour, ConnectionDirection.BOTTOM));
            connections.add(new ConnectionResult(bottomNeighbour, this, ConnectionDirection.TOP));
        }

        if (leftNeighbour != null) {
            connections.add(new ConnectionResult(this, leftNeighbour, ConnectionDirection.LEFT));
            connections.add(new ConnectionResult(leftNeighbour, this, ConnectionDirection.RIGHT));
        }

        if (rightNeighbour != null) {
            connections.add(new ConnectionResult(this, rightNeighbour, ConnectionDirection.RIGHT));
            connections.add(new ConnectionResult(rightNeighbour, this, ConnectionDirection.LEFT));
        }

        return connections;
    }
}
