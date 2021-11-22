package root.models;

import java.util.Objects;

public class InterfloorNeighbour {
    private final Area neighbour;
    private final VerticalDirection direction;

    public InterfloorNeighbour(Area neighbour, VerticalDirection direction) {
        this.neighbour = neighbour;
        this.direction = direction;
    }


    public Area getNeighbour() {
        return neighbour;
    }

    public VerticalDirection getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterfloorNeighbour that = (InterfloorNeighbour) o;
        return neighbour.equals(that.neighbour) && direction == that.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(neighbour, direction);
    }
}
