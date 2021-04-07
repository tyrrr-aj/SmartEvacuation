package sample;

import java.util.*;

public class Building {
    private Map<Integer, Area> areas = new HashMap<>();
    private Map<Integer, List<Neighbour>> neighbours = new HashMap<>();
    private Integer buildingSize = 0;

    public Building() {}

    public Map<Integer, List<Neighbour>> getNeighbours() { return neighbours; }

    public Map<Integer, Area> getAreas() { return areas; }

    public Integer getBuildingSize() { return buildingSize; }

    public void addArea(boolean isInDanger, boolean containsExit){
        areas.put(buildingSize, new Area(buildingSize, isInDanger, containsExit, null));
        buildingSize++;
    }

    public void updateArea(Integer id, boolean isInDanger, boolean containsExit){
        areas.get(id).setIsInDanger(isInDanger);
        areas.get(id).setContainsExit(containsExit);
    }

    public void removeArea(Integer id){
        areas.remove(id);
        neighbours.remove(id);

        for (Map.Entry<Integer, List<Neighbour>> entry : neighbours.entrySet()) {
            Integer idx = entry.getValue().indexOf(id);

            if(idx != -1){
                List<Neighbour> updated = entry.getValue();
                updated.remove(entry.getValue().indexOf(id));
                neighbours.put(entry.getKey(), updated);
            }
        }
    }

    private void updateConnections(Integer area1, Integer area2, Neighbour.NeighboursConnection neighboursConnection) {
        List<Neighbour> updated;
        if(neighbours.containsKey(area1)) {
            updated = neighbours.get(area1);
        }
        else {
            updated = new ArrayList<>();
        }

        updated.add(new Neighbour(area2, neighboursConnection));
        neighbours.put(area1, updated);
    }

    private void updateRemoveConnections(Integer area1, Integer area2) {
        var updated = neighbours.get(area1);
        updated.remove(neighbours.get(area1).indexOf(area2));
        neighbours.put(area1, updated);
    }


    public void createConnection(Integer area1, Integer area2, Neighbour.NeighboursConnection neighboursConnection) {
        updateConnections(area1, area2, neighboursConnection);
        switch (neighboursConnection) {
            case BOTTOM:
                updateConnections(area2, area1, Neighbour.NeighboursConnection.TOP);
                break;

            case TOP:
                updateConnections(area2, area1, Neighbour.NeighboursConnection.BOTTOM);
                break;

            case LEFT:
                updateConnections(area2, area1, Neighbour.NeighboursConnection.RIGHT);
                break;

            case RIGHT:
                updateConnections(area2, area1, Neighbour.NeighboursConnection.LEFT);
                break;

            default:
                updateConnections(area2, area1, Neighbour.NeighboursConnection.NONE);
                break;
        }
    }

    public void removeConnection(Integer area1, Integer area2) {
        updateRemoveConnections(area1, area2);
        updateRemoveConnections(area2, area1);
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
                System.out.println(connection.getNeighbourId() + " from side: " + connection.getNeighboursConnection());
            }
            System.out.println();
        }

        System.out.println("\n");

    }
}
