package sample;

public class Neighbour {
    public enum NeighboursConnection {
        NONE,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    private Integer neighbourId;
    private NeighboursConnection neighboursConnection;

    public Neighbour(Integer neighbourId, NeighboursConnection neighboursConnection) {
        this.neighbourId = neighbourId;
        this.neighboursConnection = neighboursConnection;
    }

    public Integer getNeighbourId() { return neighbourId; }

    public NeighboursConnection getNeighboursConnection() { return neighboursConnection; }

    public void setNeighbourId(Integer neighbourId) { this.neighbourId = neighbourId; }

    public void setNeighboursConnection(NeighboursConnection neighboursConnection) { this.neighboursConnection = neighboursConnection; }
}
