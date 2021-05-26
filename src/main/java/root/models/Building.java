package root.models;

import java.util.*;

public class Building {
    private final List<Floor> floors = new ArrayList<>();

    public Building() {}

    public List<Floor> getFloors() {
        return floors;
    }

    public void addFloor(Floor floor) {
        floors.add(floor);
    }
}
