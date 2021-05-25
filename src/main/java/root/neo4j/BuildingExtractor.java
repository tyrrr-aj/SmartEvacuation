package root.neo4j;

import org.javatuples.Triplet;
import root.geometry.Point;
import root.models.Building;
import root.models.ConnectionDirection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuildingExtractor {
    private final Neo4jDriver neo4jDriver;

    private Building building;
    private Map<Integer, AreaResult> areasByName;
    private Map<String, Point> doorCoords;

    public BuildingExtractor(Neo4jDriver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    public Building extractBuilding() {
        building = new Building();

        var areaResults = extractAreas();
        doorCoords = extractDoors();
        extractConnections(areaResults);
        extractExits();

        return building;
    }

    private List<AreaResult> extractAreas() {
        var areaResults = neo4jDriver.readAreasWithCoordinates();
        areaResults.forEach(areaResult -> building.addArea(areaResult.getId(), false));

        return areaResults;
    }

    private Map<String, Point> extractDoors() {
        var doorResults = neo4jDriver.readDoorsWithCoordinates();
        return doorResults
                .stream()
                .collect(Collectors.toMap(DoorResult::getGlobalId, DoorResult::getCoords));
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
                        .filter(rd -> areasByName.containsKey(rd.getValue0())) // necessary only while working on single floor
                        .map(rd -> new Triplet<>(
                                entry.getKey(),
                                rd.getValue0(),
                                areasByName.get(entry.getKey()).getRelativeDirection(doorCoords.get(rd.getValue1()))))
                        )
                .forEach(conn -> building.createConnection(conn.getValue0(), conn.getValue1(), conn.getValue2()));
    }

    private void extractExits() {
        var areasWithExit = neo4jDriver.readAreasWithExits();
        areasWithExit
                .stream()
                .filter(areaId -> areasByName.containsKey(areaId)) // necessary only while working on single floor
                .forEach(areaId -> building.updateArea(areaId, false, true, ConnectionDirection.NONE));
    }
}
