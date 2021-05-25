package root.models;

import java.util.*;

public class Building {
    private List<Floor> floors = new ArrayList<>();
    public List<Floor> getFloors() {
        return floors;
    }

    public Building() {

    }

    public void addFloor(Floor floor) {
        floors.add(floor);
    }
}
