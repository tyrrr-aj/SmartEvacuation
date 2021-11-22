package root.models;

import java.util.ArrayList;
import java.util.List;

public class Area {

    private final Integer id;
    private boolean isInDanger = false;
    private boolean containsDirectExit = false;
    private ConnectionDirection exitDirection;
    private String action = "S";
    private List<Neighbour> ownFloorNeighbours;
    private final List<InterfloorNeighbour> otherFloorNeighbours = new ArrayList<>();
    private final List<InterfloorNeighbour> exitsThroughOtherFloors = new ArrayList<>();

    public Area(int id) {
        this.id = id;
    }

    public Area(int id, boolean isInDanger, List<Neighbour> ownFloorNeighbours) {
        this.id = id;
        this.isInDanger = isInDanger;
        this.ownFloorNeighbours = ownFloorNeighbours;
    }

    public Area(int id, boolean isInDanger, ConnectionDirection exitDirection, List<Neighbour> ownFloorNeighbours) {
        this.id = id;
        this.isInDanger = isInDanger;
        this.containsDirectExit = true;
        this.exitDirection = exitDirection;
        this.ownFloorNeighbours = ownFloorNeighbours;
    }

    public Integer getId(){
        return id;
    }

    private boolean isInDanger() { return isInDanger; }

    public boolean isContainsExit() { return containsDirectExit || exitsThroughOtherFloors.size() > 0; }

    public boolean isContainsDirectExit() {return containsDirectExit; }

    public ConnectionDirection getExitDirection() { return exitDirection; }

    public VerticalDirection getVerticalExitDirection() { return exitsThroughOtherFloors.size() > 0 ? exitsThroughOtherFloors.get(0).getDirection() : null; }

    public List<Neighbour> getOwnFloorNeighbours() { return ownFloorNeighbours; }

    public void setIsInDanger(boolean inDanger) {
        if (inDanger) {
            otherFloorNeighbours.forEach(neigh -> neigh
                    .getNeighbour()
                    .removeExitThroughInterfloorNeighbour(new InterfloorNeighbour(this, neigh.getDirection().opposite())));
        }
        else {
            otherFloorNeighbours.forEach(neigh -> neigh
                    .getNeighbour()
                    .addExitThroughInterfloorNeighbour(new InterfloorNeighbour(this, neigh.getDirection().opposite())));
        }
        this.isInDanger = inDanger;
    }

    public boolean getIsInDanger() { return isInDanger; }

    public void setContainsDirectExit(boolean containsDirectExit) { this.containsDirectExit = containsDirectExit; }

    public void setAction(String action) {
        if (action.equals("E")) {
            otherFloorNeighbours.forEach(neigh -> neigh
                    .getNeighbour()
                    .addExitThroughInterfloorNeighbour(new InterfloorNeighbour(this, neigh.getDirection().opposite())));
        }
        if (action.equals("S")) {
            otherFloorNeighbours.forEach(neigh -> neigh
                    .getNeighbour()
                    .removeExitThroughInterfloorNeighbour(new InterfloorNeighbour(this, neigh.getDirection().opposite())));
        }
        this.action = action;
    }

    public String getAction() { return action; }

    public void setExitDirection(ConnectionDirection exitDirection) {
        this.exitDirection = exitDirection;
    }

    public void addOtherFloorNeighbour(InterfloorNeighbour neigh) {
        otherFloorNeighbours.add(neigh);
    }

    public void addExitThroughInterfloorNeighbour(InterfloorNeighbour neighbour) { exitsThroughOtherFloors.add(neighbour); }

    public void removeExitThroughInterfloorNeighbour(InterfloorNeighbour neighbour) {
        exitsThroughOtherFloors.remove(neighbour);
        System.out.println("removing exit through interfloor connection");
    }

    public void print() {
        System.out.println("Area " + id + ": isInDanger: " + isInDanger + ", containsExit: " + containsDirectExit);
    }
}
