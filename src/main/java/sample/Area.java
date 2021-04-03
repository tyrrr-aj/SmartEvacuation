package sample;

public class Area {
    private Integer id;
    private boolean isInDanger;
    private boolean containsExit;
    private String action = "E";

    public Area(int id, boolean isInDanger, boolean containsExit) {
        this.id = id;
        this.isInDanger = isInDanger;
        this.containsExit = containsExit;
    }

    public Integer getId(){
        return id;
    }

    public Integer isInDanger(){
        if(isInDanger) {
            return 1;
        }
        return -1;
    }

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

    public void setAction(String action){this.action = action;}

    public String getAction(){return action;}

    public void print()
    {
        System.out.println("Area " + id + ": isInDanger: " + isInDanger + ", containsExit: " + containsExit);
    }
}
