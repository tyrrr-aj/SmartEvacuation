package root.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Floor {
    private Map<Integer, Area> areas = new HashMap<>();
    private Map<Integer, List<Neighbour>> neighbours = new HashMap<>();

    public Floor() {
    }

    public Map<Integer, List<Neighbour>> getNeighbours() { return neighbours; }

    public Map<Integer, Area> getAreas() { return areas; }


    public void addArea(int id, boolean isInDanger){
        areas.put(id, new Area(id, isInDanger, null));
    }

    public void addArea(int id, boolean isInDanger, ConnectionDirection exitDirection){
        areas.put(id, new Area(id, isInDanger, exitDirection, null));
    }

    public void updateArea(Integer id, boolean isInDanger, boolean containsExit, ConnectionDirection exitDirection){
        areas.get(id).setIsInDanger(isInDanger);
        areas.get(id).setContainsExit(containsExit);
        areas.get(id).setExitDirection(exitDirection);
    }

    private void updateConnections(Integer area1, Integer area2, ConnectionDirection connectionDirection) {
        List<Neighbour> updated;
        if(neighbours.containsKey(area1)) {
            updated = neighbours.get(area1);
        }
        else {
            updated = new ArrayList<>();
        }

        updated.add(new Neighbour(area2, connectionDirection));
        neighbours.put(area1, updated);
    }

    public void createConnection(Integer area1, Integer area2, ConnectionDirection connectionDirection) {
        updateConnections(area1, area2, connectionDirection);
//        updateConnections(area2, area1, reverseConnectionDirection(connectionDirection));
    }

    private ConnectionDirection reverseConnectionDirection(ConnectionDirection connectionDirection) {
        switch (connectionDirection) {
            case BOTTOM:
                return ConnectionDirection.TOP;

            case TOP:
                return ConnectionDirection.BOTTOM;

            case LEFT:
                return ConnectionDirection.RIGHT;

            case RIGHT:
                return ConnectionDirection.LEFT;

            default:
                return ConnectionDirection.NONE;
        }
    }

    public void print() {
        System.out.println("Areas: ");
        for (Map.Entry<Integer, Area> entry : areas.entrySet())
        {
            entry.getValue().print();
        }

        System.out.println("\nConnections:");
        for (Map.Entry<Integer, List<Neighbour>> entry : neighbours.entrySet()) {
            System.out.println("Area " + entry.getKey() + " is neighbours with ");
            for (var connection : entry.getValue()) {
                System.out.println(connection.getNeighbourId() + " from side: " + connection.getConnectionDirection());
            }
            System.out.println();
        }

        System.out.println("\n");

    }
}
