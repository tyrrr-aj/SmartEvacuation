package sample;

import java.util.ArrayList;
import java.util.List;

public class Area {

    private Integer id;
    private boolean isInDanger;
    private boolean containsExit;
    private Neighbour.NeighboursConnection exitDirection;
    private String action = "E";
    private List<Neighbour> neighbours;

    public Area(int id, boolean isInDanger, List<Neighbour> neighbours) {
        this.id = id;
        this.isInDanger = isInDanger;
        this.containsExit = false;
        this.neighbours = neighbours;
    }

    public Area(int id, boolean isInDanger, Neighbour.NeighboursConnection exitDirection, List<Neighbour> neighbours) {
        this.id = id;
        this.isInDanger = isInDanger;
        this.containsExit = true;
        this.exitDirection = exitDirection;
        this.neighbours = neighbours;
    }

    public Integer getId(){
        return id;
    }

    public boolean isInDanger() { return isInDanger; }

    public boolean isContainsExit() { return containsExit; }

    public Neighbour.NeighboursConnection getExitDirection() { return exitDirection; }

    public List<Neighbour> getNeighbours() { return neighbours; }

    public void setIsInDanger(boolean inDanger) { this.isInDanger = inDanger; }

    public void setContainsExit(boolean containsExit) { this.containsExit = containsExit; }

    public void setAction(String action) { this.action = action; }

    public void setNeighbours(List<Neighbour> neighbours) { this.neighbours = neighbours; }

    public String getAction(){ return action; }

    public void print() {
        System.out.println("Area " + id + ": isInDanger: " + isInDanger + ", containsExit: " + containsExit);
    }
}
