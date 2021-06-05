package root.neo4j;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import root.models.ConnectionDirection;

import java.util.LinkedList;
import java.util.List;

public class CorridorSplitter {
    private final List<Triplet<Integer, Integer, ConnectionDirection>> connections = new LinkedList<>();

    public CorridorSplitter(AreaResult corridor) {

    }

    public List<AreaResult> getAreas() {
        return null;
    }

    public List<Triplet<Integer, Integer, ConnectionDirection>> getConnections() {
        return connections;
    }
}
