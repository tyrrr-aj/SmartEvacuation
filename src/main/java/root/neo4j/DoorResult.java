package root.neo4j;

import root.geometry.Point;

public class DoorResult {
    private final String globalId;
    private final Point coords;

    public DoorResult(String globalId, Point coords) {
        this.globalId = globalId;
        this.coords = coords;
    }

    public String getGlobalId() {
        return globalId;
    }

    public Point getCoords() {
        return coords;
    }
}
