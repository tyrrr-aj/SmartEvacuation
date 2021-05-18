package sample.neo4j;

import java.util.List;

public class AreaResult {
    private int id;
    private List<Double[]> cornerCoordinates;

    public AreaResult(int id, List<Double[]> cornerCoordinates) {
        this.id = id;
        this.cornerCoordinates = cornerCoordinates;
    }

    public int getId() {
        return id;
    }

    public List<Double[]> getCornerCoordinates() {
        return cornerCoordinates;
    }

    @Override
    public String toString() {
        var result = "ID: " + this.id + ", coordinates: \n";

        for(var coords : this.cornerCoordinates) {
            result += "[" + coords[0] + ", " + coords[1] + "]\n";
        }

        return result;
    }
}
