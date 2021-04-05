package sample;

import java.util.ArrayList;
import java.util.List;

public class Area {
    private Integer id;
    private boolean isInDanger;
    private boolean containsExit;
    private String action = "E";
    private List<Integer> neighbours;

    public Area(int id, boolean isInDanger, boolean containsExit) {
        this.id = id;
        this.isInDanger = isInDanger;
        this.containsExit = containsExit;
        this.neighbours = new ArrayList<>();
    }

    public Integer getId(){
        return id;
    }

    public boolean isInDanger() { return isInDanger; }

    public boolean containsExit(){
        return containsExit;
    }

    public void setIsInDanger(boolean inDanger)
    {
        this.isInDanger = inDanger;
    }

    public void setContainsExit(boolean containsExit)
    {
        this.containsExit = containsExit;
    }

    public void setAction(String action){ this.action = action; }

    public String getAction(){ return action; }

    public void print() {
        System.out.println("Area " + id + ": isInDanger: " + isInDanger + ", containsExit: " + containsExit);
    }
}
