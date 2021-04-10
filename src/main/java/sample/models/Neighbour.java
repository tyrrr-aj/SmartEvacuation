package sample.models;

public class Neighbour {
    private Integer neighbourId;
    private ConnectionDirection connectionDirection;

    public Neighbour(Integer neighbourId, ConnectionDirection connectionDirection) {
        this.neighbourId = neighbourId;
        this.connectionDirection = connectionDirection;
    }

    public Integer getNeighbourId() { return neighbourId; }

    public ConnectionDirection getConnectionDirection() { return connectionDirection; }
}
