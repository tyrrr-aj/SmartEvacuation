package root.neo4j;

import root.models.ConnectionDirection;

import java.util.Objects;

public class ConnectionResult {
    private final AreaResult sourceArea;
    private final AreaResult destArea;
    private final ConnectionDirection direction;

    public ConnectionResult(AreaResult sourceArea, AreaResult destArea, ConnectionDirection direction) {
        this.sourceArea = sourceArea;
        this.destArea = destArea;
        this.direction = direction;
    }

    public AreaResult getSourceArea() {
        return sourceArea;
    }

    public AreaResult getDestArea() {
        return destArea;
    }

    public ConnectionDirection getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionResult that = (ConnectionResult) o;
        return sourceArea.equals(that.sourceArea) && destArea.equals(that.destArea) && direction == that.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceArea, destArea, direction);
    }
}
