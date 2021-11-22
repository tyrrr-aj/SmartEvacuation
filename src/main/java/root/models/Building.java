package root.models;

import org.javatuples.Pair;

import java.util.*;

public class Building {
    private final List<Floor> floors = new LinkedList<>();

    public Building() {}

    public List<Floor> getFloors() {
        return floors;
    }


    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    public int getLowestFloorIdWithDirectExit() {
        for (int i = 0; i < floors.size(); i++) {
            if (floors.get(i).containsDirectExit()) {
                return i;
            }
        }
        return 0;
    }
}
