package sample;

import java.util.*;

public class Building {
    private Map<Integer, Area> areas = new HashMap<>();
    private Map<Integer, List<Integer>> neighbours = new HashMap<>();
    private Integer buildingSize = 0;

    public Building(){
    }

    public Map<Integer, List<Integer>> getNeighbours(){
        return neighbours;
    }

    public Map<Integer, Area> getAreas(){
        return areas;
    }

    public void addArea(boolean isInDanger, boolean containsExit){
        areas.put(buildingSize, new Area(buildingSize, isInDanger, containsExit));
        buildingSize++;
    }

    public void updateArea(Integer id, boolean isInDanger, boolean containsExit){
        areas.get(id).setIsInDanger(isInDanger);
        areas.get(id).setContainsExit(containsExit);
    }

    public void removeArea(Integer id){
        areas.remove(id);
        neighbours.remove(id);

        for (Map.Entry<Integer, List<Integer>> entry : neighbours.entrySet()) {
            Integer idx = entry.getValue().indexOf(id);

            if(idx != -1){
                List<Integer> updated = entry.getValue();
                updated.remove(entry.getValue().indexOf(id));
                neighbours.put(entry.getKey(), updated);
            }
        }
    }

    private void updateConnections(Integer area1, Integer area2){
        List<Integer> updated;
        if(neighbours.containsKey(area1)){
            updated = neighbours.get(area1);
        }
        else{
            updated = new ArrayList<>();
        }
        updated.add(area2);
        neighbours.put(area1, updated);
    }

    private void updateRemoveConnections(Integer area1, Integer area2){
        List<Integer> updated;

        updated = neighbours.get(area1);
        updated.remove(neighbours.get(area1).indexOf(area2));
        neighbours.put(area1, updated);
        }


    public void createConnection(Integer area1, Integer area2){
        updateConnections(area1, area2);
        updateConnections(area2, area1);
    }

    public void removeConnection(Integer area1, Integer area2){
        updateRemoveConnections(area1, area2);
        updateRemoveConnections(area2, area1);
    }

    public void print(){
        System.out.println("Areas: ");
        for (Map.Entry<Integer, Area> entry : areas.entrySet())
        {
            entry.getValue().print();
        }

        System.out.println("\nConnections:");
        for (Map.Entry<Integer, List<Integer>> entry : neighbours.entrySet()) {
            System.out.println("Area " + entry.getKey() + " is neighbours with " + entry.getValue());
        }

        System.out.println("\n");

    }
}
