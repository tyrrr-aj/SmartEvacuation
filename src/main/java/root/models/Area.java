package root.models;

import java.util.ArrayList;
import java.util.List;

public class Area {

    private Integer id;
    private boolean isInDanger = false;
    private boolean containsExit = false;
    private List<Double[]> cornerCoordinates = new ArrayList<>();
    private ConnectionDirection exitDirection;
    private String action = "E";
    private List<Neighbour> neighbours;

    public Area(int id) {
        this.id = id;
    }

    public Area(int id, boolean isInDanger, List<Neighbour> neighbours) {
        this.id = id;
        this.isInDanger = isInDanger;
        this.neighbours = neighbours;
    }

    public Area(int id, boolean isInDanger, ConnectionDirection exitDirection, List<Neighbour> neighbours) {
        this.id = id;
        this.isInDanger = isInDanger;
        this.containsExit = true;
        this.cornerCoordinates = new ArrayList<>();
        this.exitDirection = exitDirection;
        this.neighbours = neighbours;
    }

    public Integer getId(){
        return id;
    }

    public boolean isInDanger() { return isInDanger; }

    public boolean isContainsExit() { return containsExit; }

    public ConnectionDirection getExitDirection() { return exitDirection; }

    public List<Neighbour> getNeighbours() { return neighbours; }

    public void setIsInDanger(boolean inDanger) { this.isInDanger = inDanger; }

    public void setContainsExit(boolean containsExit) { this.containsExit = containsExit; }

    public void setAction(String action) { this.action = action; }

    public String getAction() { return action; }

    public void print() {
        System.out.println("Area " + id + ": isInDanger: " + isInDanger + ", containsExit: " + containsExit);
    }
}
