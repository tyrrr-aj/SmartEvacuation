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
        areaResults.forEach(areaResult -> building.getFloors().get(0).addArea(areaResult.getId(), false));

        return areaResults;
    }

    private void extractConnections(List<AreaResult> areaResults) {
        areasByName = areaResults
                .stream()
                .collect(Collectors.toMap(AreaResult::getId, areaResult -> areaResult));

        var connections = neo4jDriver.readConnectedSpaces();
        connections
                .entrySet()
                .stream()
                .filter(entry -> areasByName.containsKey(entry.getKey())) // necessary only while working on single floor
                .flatMap(entry -> entry
                        .getValue()
                        .stream()
                        .filter(roomId -> areasByName.containsKey(roomId)) // necessary only while working on single floor
                        .map(roomId -> new Triplet<>(
                                entry.getKey(),
                                roomId,
                                areasByName.get(entry.getKey()).getCenterCoord().getRelativeDirection(
                                        areasByName.get(roomId).getCenterCoord())))
                        )
                .forEach(conn -> building.getFloors().get(0).createConnection(conn.getValue0(), conn.getValue1(), conn.getValue2()));
    }

    private void extractExits() {
        var areasWithExit = neo4jDriver.readAreasWithExits();
        areasWithExit
                .stream()
                .filter(areaId -> areasByName.containsKey(areaId)) // necessary only while working on single floor
                .forEach(areaId -> building.getFloors().get(0).updateArea(areaId, false, true));
    }
}
