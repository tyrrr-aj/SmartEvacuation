package root.neo4j;

import org.javatuples.Triplet;
import root.models.Building;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuildingExtractor {
    private final Neo4jDriver neo4jDriver;

    private Building building;
    private Map<Integer, AreaResult> areasByName;

    public BuildingExtractor(Neo4jDriver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    public Building extractBuilding() {
        building = new Building();

        var areaResults = extractAreas();
        extractConnections(areaResults);
        extractExits();

        return building;
    }

    private List<AreaResult> extractAreas() {
        var areaResults = neo4jDriver.readAreasWithCoordinates();
        areaResults.forEach(areaResult -> building.addArea(areaResult.getId(), false));

        return areaResults;
    }

    private void extractConnections(List<AreaResult> areaResults) {
        areasByName = areaResults
                .stream()
                .collect(Collectors.toMap(AreaResult::getId, areaResult -> areaResult));

        var connections = neo4jDriver.readConnectedSpaces();
        connections
                .stream()
                .filter(roomPair -> areasByName.containsKey(roomPair.getValue0())) // necessary only while working on single floor
                .map(roomPair -> new Triplet<>(
                        roomPair.getValue0(),
                        roomPair.getValue1(),
                        areasByName.get(roomPair.getValue0()).getCenterCoord().getRelativeDirection(
                                areasByName.get(roomPair.getValue1()).getCenterCoord()
                        )))
                .forEach(conn -> building.createConnection(conn.getValue0(), conn.getValue1(), conn.getValue2()));
    }

    private void extractExits() {
        var areasWithExit = neo4jDriver.readAreasWithExits();
        areasWithExit
                .stream()
                .filter(areaId -> areasByName.containsKey(areaId)) // necessary only while working on single floor
                .forEach(areaId -> building.updateArea(areaId, false, true));
    }
}
