package root.neo4j;

import com.google.common.collect.Maps;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import root.geometry.Point;
import root.models.Building;
import root.models.ConnectionDirection;
import root.models.Floor;
import root.neo4j.corridors.CorridorSplitter;

import java.util.*;
import java.util.stream.Collectors;

public class BuildingExtractor {
    private final Neo4jDriver neo4jDriver;
    private Building building;

    private LinkedHashMap<String, Floor> floors;
    private Map<Integer, AreaResult> areasByName;
    private Map<String, Point> doorCoords;
    private Map<Integer, List<Pair<Integer, Point>>> areasWithNeighsAndDoors;
    private Map<Integer, List<ConnectionResult>> areasWithConnections;
    private List<ConnectionResult> connections;
    private List<Integer> areasWithExit;
    private List<Integer> corridors;

    public BuildingExtractor(Neo4jDriver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    public Building extractBuilding() {
        building = new Building();

        floors = extractFloors();
        areasByName = extractAreas();
        doorCoords = extractDoors();
        areasWithNeighsAndDoors = extractNeighbours();
        areasWithExit = extractExits();

        connections = new LinkedList<>();

        areasWithConnections = computeConnections();
        corridors = chooseCorridors();
        splitCorridors();

        fillConnectionsFromNonCorridors();
        removeCorridorsFromAreas();

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

    private Map<Integer, List<Pair<Integer, Point>>> extractNeighbours() {
        return neo4jDriver.readConnectedSpaces()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry
                                .getValue()
                                .stream()
                                .map(rd -> new Pair<>(
                                        rd.getValue0(),
                                        doorCoords.get(rd.getValue1())))
                                .collect(Collectors.toList())
                        ));
    }

    private List<Integer> extractExits() {
        return neo4jDriver.readAreasWithExits();
    }

    private Map<Integer, List<ConnectionResult>> computeConnections() {
        return Maps.transformEntries(areasWithNeighsAndDoors,
                (areaId, neighs) -> Objects.requireNonNull(neighs)
                        .stream()
                        .map(neighAndDoor -> new ConnectionResult(
                                areasByName.get(areaId),
                                areasByName.get(neighAndDoor.getValue0()),
                                areasByName.get(areaId).getRelativeDirection(neighAndDoor.getValue1())))
                        .collect(Collectors.toList()));
    }

    private List<Integer> chooseCorridors() {
        return Maps.transformValues(areasWithConnections,
                connectionResults -> connectionResults
                        .stream()
                        .collect(Collectors.groupingBy(ConnectionResult::getDirection))
                        .values()
                        .stream()
                        .map(List::size)
                        .collect(Collectors.toList()))
                .entrySet()
                .stream()
                .filter(entry -> entry
                        .getValue()
                        .stream()
                        .anyMatch(connNumber -> connNumber > 1))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void splitCorridors() {
        corridors.forEach(this::splitSingleCorridor);
    }

    private void splitSingleCorridor(int corridorId) {
        CorridorSplitter splitter = new CorridorSplitter(
                areasByName.get(corridorId),
                areasWithNeighsAndDoors
                        .get(corridorId)
                        .stream()
                        .collect(Collectors.toMap(
                                neighAndDoor -> areasByName.get(neighAndDoor.getValue0()),
                                Pair::getValue1)
                        )
        );

        areasByName.putAll(splitter
                .getAreas()
                .stream()
                .collect(Collectors.toMap(
                        AreaResult::getId,
                        area -> area
                )));
        connections.addAll(splitter.getConnections());
    }

    private void fillConnectionsFromNonCorridors() {
        var connectionsNotInvolvingCorridors = Maps
                .filterKeys(areasWithConnections, areaId -> !corridors.contains(areaId))
                .values()
                .stream()
                .flatMap(Collection::stream)
                .filter(conn -> !corridors.contains(conn.getDestArea().getId()))
                .collect(Collectors.toList());

        connections.addAll(connectionsNotInvolvingCorridors);
    }

    private void removeCorridorsFromAreas() {
        corridors.forEach(corridor -> areasByName.remove(corridor));
    }

    private void addFloorsToBuilding() {
        floors.values().forEach(floor -> building.addFloor(floor));
    }

    private void addAreasToBuilding() {
        areasByName.values().forEach(areaResult -> floors.get(areaResult.getFloorId()).addArea(areaResult.getId(), false));
    }

    private void addConnectionsToBuilding() {
        connections.forEach(conn -> getFloorOfAreaById(conn.getSourceArea().getId())
                .createConnection(conn.getSourceArea().getId(), conn.getDestArea().getId(), conn.getDirection()));
    }

    private void addExitsToBuilding() {
        areasWithExit.forEach(areaId -> getFloorOfAreaById(areaId).updateArea(areaId, false, true, ConnectionDirection.NONE));
    }

    private Floor getFloorOfAreaById(int id) {
        return floors.get(areasByName.get(id).getFloorId());
    }
}
