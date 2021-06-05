package root.neo4j;

import org.javatuples.Triplet;
import root.geometry.Point;
import root.models.Building;
import root.models.ConnectionDirection;
import root.models.Floor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuildingExtractor {
    private final Neo4jDriver neo4jDriver;
    private Building building;

    private LinkedHashMap<String, Floor> floors;
    private Map<Integer, AreaResult> areasByName;
    private Map<String, Point> doorCoords;
    private List<Triplet<Integer, Integer, ConnectionDirection>> connections;
    private List<Integer> areasWithExit;

    public BuildingExtractor(Neo4jDriver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    public Building extractBuilding() {
        building = new Building();

        floors = extractFloors();
        areasByName = extractAreas();
        doorCoords = extractDoors();
        connections = extractConnections();
        areasWithExit = extractExits();

        addFloorsToBuilding();
        addAreasToBuilding();
        addConnectionsToBuilding();
        addExitsToBuilding();

        return building;
    }

    private LinkedHashMap<String, Floor> extractFloors() {
        return neo4jDriver.readOrderedFloors()
                .stream()
                .collect(Collectors.toMap(
                        floorId -> floorId,
                        floorId -> new Floor(),
                        (a, b) -> b,
                        LinkedHashMap::new));
    }

    private Map<Integer, AreaResult> extractAreas() {
        return neo4jDriver.readAreasWithCoordinates()
                .stream()
                .collect(Collectors.toMap(AreaResult::getId, areaResult -> areaResult));
    }

    private Map<String, Point> extractDoors() {
        var doorResults = neo4jDriver.readDoorsWithCoordinates();
        return doorResults
                .stream()
                .collect(Collectors.toMap(DoorResult::getGlobalId, DoorResult::getCoords));
    }

    private List<Triplet<Integer, Integer, ConnectionDirection>> extractConnections() {
        return neo4jDriver.readConnectedSpaces()
                .entrySet()
                .stream()
                .flatMap(entry -> entry
                        .getValue()
                        .stream()
                        .map(rd -> new Triplet<>(
                                entry.getKey(),
                                rd.getValue0(),
                                areasByName.get(entry.getKey()).getRelativeDirection(doorCoords.get(rd.getValue1()))))
                        )
                .collect(Collectors.toList());
    }

    private List<Integer> extractExits() {
        return neo4jDriver.readAreasWithExits();
    }

    private void addFloorsToBuilding() {
        floors.values().forEach(floor -> building.addFloor(floor));
    }

    private void addAreasToBuilding() {
        areasByName.values().forEach(areaResult -> floors.get(areaResult.getFloorId()).addArea(areaResult.getId(), false));
    }

    private void addConnectionsToBuilding() {
        connections.forEach(conn -> getFloorOfAreaById(conn.getValue0()).createConnection(conn.getValue0(), conn.getValue1(), conn.getValue2()));
    }

    private void addExitsToBuilding() {
        areasWithExit.forEach(areaId -> getFloorOfAreaById(areaId).updateArea(areaId, false, true, ConnectionDirection.NONE));
    }

    private Floor getFloorOfAreaById(int id) {
        return floors.get(areasByName.get(id).getFloorId());
    }
}
