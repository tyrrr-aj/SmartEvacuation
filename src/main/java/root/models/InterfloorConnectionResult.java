package root.models;

import java.util.Objects;

public class InterfloorConnectionResult {
    private final int firstAreaId;
    private final int secondAreaId;
    private final VerticalDirection direction;

    public InterfloorConnectionResult(int firstAreaId, int secondAreaId, VerticalDirection direction) {
        this.firstAreaId = firstAreaId;
        this.secondAreaId = secondAreaId;
        this.direction = direction;
    }

    public int getFirstAreaId() {
        return firstAreaId;
    }

    public int getSecondAreaId() {
        return secondAreaId;
    }

    public VerticalDirection getDirection() {
        return direction;
    }
}
