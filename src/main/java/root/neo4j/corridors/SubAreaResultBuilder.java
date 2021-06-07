package root.neo4j.corridors;

import root.geometry.Point;
import root.neo4j.AreaResult;

import java.util.List;

public class SubAreaResultBuilder {
    private final AreaResult sourceAreaResult;
    private final List<Point> cornerCoordinates;

    private AreaResult topNeighbour = null;
    private AreaResult bottomNeighbour = null;
    private AreaResult leftNeighbour = null;
    private AreaResult rightNeighbour = null;

    public SubAreaResultBuilder(AreaResult sourceAreaResult) {
        this.sourceAreaResult = sourceAreaResult;
        cornerCoordinates = sourceAreaResult.getCornerCoordinates();
    }

    public SubAreaResult build() {
        return new SubAreaResult(sourceAreaResult, cornerCoordinates, topNeighbour, bottomNeighbour, leftNeighbour, rightNeighbour);
    }

    public void setLUCorner(Point newCorner) {
        cornerCoordinates.set(0, newCorner);
    }

    public void setRUCorner(Point newCorner) {
        cornerCoordinates.set(1, newCorner);
    }

    public void setRBCorner(Point newCorner) {
        cornerCoordinates.set(2, newCorner);
    }

    public void setLBCorner(Point newCorner) {
        cornerCoordinates.set(3, newCorner);
    }

    public void addTopNeighbour(AreaResult topNeighbour) {
        this.topNeighbour = topNeighbour;
    }

    public void addBottomNeighbour(AreaResult bottomNeighbour) {
        this.bottomNeighbour = bottomNeighbour;
    }

    public void addLeftNeighbour(AreaResult leftNeighbour) {
        this.leftNeighbour = leftNeighbour;
    }

    public void addRightNeighbour(AreaResult rightNeighbour) {
        this.rightNeighbour = rightNeighbour;
    }
}
