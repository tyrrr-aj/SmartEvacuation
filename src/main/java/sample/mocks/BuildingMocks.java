package sample.mocks;

import sample.models.Building;
import sample.models.ConnectionDirection;
import sample.neo4j.Neo4jDriver;

public class BuildingMocks {

    public static Building getBuildingFromIFC() {
        Building building = new Building();
        try ( Neo4jDriver neo4jDriver = new Neo4jDriver() )
        {
            var roomsWithCoordinates = neo4jDriver.readAreasWithCoordinates();
            var roomsWithExits = neo4jDriver.readAreasWithExits();
            var connectedRooms = neo4jDriver.readConnectedSpaces();
            for(var room : roomsWithCoordinates) {
                var isInDanger = false;
                System.out.println(room.getId());
                building.addArea(room.getId(), isInDanger, null);
            }

            connectedRooms
                    .entrySet()
                    .stream()
                    .forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));
        }
        return building;
    }

    // []
    // [][]
    // []
    public static Building getSmallBuilding() {
        Building building = new Building();
        for(int i = 0; i < 3; i++) {
            building.addArea(i,false);
        }
        building.addArea(3, false, ConnectionDirection.BOTTOM);
        building.createConnection(0, 1, ConnectionDirection.BOTTOM);
        building.createConnection(1, 2, ConnectionDirection.RIGHT);
        building.createConnection(1, 3, ConnectionDirection.BOTTOM);

        return building;
    }

//    //   []
//    // [][][][]
//    //   []
//    public static Building getSmallBuilding2() {
//        Building building = new Building();
//        for(int i = 0; i < 5; i++) {
//            building.addArea(false);
//        }
//        building.addArea(false, ConnectionDirection.RIGHT);
//
//        building.createConnection(0, 2, ConnectionDirection.BOTTOM);
//        building.createConnection(1, 2, ConnectionDirection.RIGHT);
//        building.createConnection(2, 4, ConnectionDirection.BOTTOM);
//        building.createConnection(2, 3, ConnectionDirection.RIGHT);
//        building.createConnection(3, 5, ConnectionDirection.RIGHT);
//
//        return building;
//    }
//
//    //   []
//    //   [][][]
//    //       []
//    // [][][][]
//    public static Building getSmallBuilding3() {
//        Building building = new Building();
//        building.addArea(false, ConnectionDirection.TOP);
//        for(int i = 0; i < 8; i++) {
//            building.addArea(false);
//        }
//
//        building.createConnection(0, 1, ConnectionDirection.BOTTOM);
//        building.createConnection(1, 2, ConnectionDirection.RIGHT);
//        building.createConnection(2, 3, ConnectionDirection.RIGHT);
//        building.createConnection(3, 4, ConnectionDirection.BOTTOM);
//        building.createConnection(4, 5, ConnectionDirection.BOTTOM);
//        building.createConnection(5, 6, ConnectionDirection.LEFT);
//        building.createConnection(6, 7, ConnectionDirection.LEFT);
//        building.createConnection(7, 8, ConnectionDirection.LEFT);
//
//        return building;
//    }
//
//    // [][][]
//    // [][][]
//    // [][][][][][][][]
//    // [][][][][][][][]
//    // [][][][][][][][]
//    public static Building getSmallBuilding4() {
//        Building building = new Building();
//        building.addArea(false); // 0
//        building.addArea(false, ConnectionDirection.TOP); // 1
//        for(int i = 0; i < 9; i++) {
//            building.addArea(false); // 2 - 10
//        }
//        building.addArea(false, ConnectionDirection.TOP); // 11
//        for(int i = 0; i < 18; i++) {
//            building.addArea(false); // 12 - 29
//        }
//
//        building.createConnection(0, 1, ConnectionDirection.RIGHT);
//        building.createConnection(1, 2, ConnectionDirection.RIGHT);
//        building.createConnection(1, 4, ConnectionDirection.BOTTOM);
//        building.createConnection(3, 4, ConnectionDirection.RIGHT);
//        building.createConnection(4, 5, ConnectionDirection.RIGHT);
//        building.createConnection(3, 6, ConnectionDirection.BOTTOM);
//        building.createConnection(4, 7, ConnectionDirection.BOTTOM);
//        building.createConnection(14, 15, ConnectionDirection.RIGHT);
//        building.createConnection(7, 15, ConnectionDirection.BOTTOM);
//        building.createConnection(8, 16, ConnectionDirection.BOTTOM);
//        building.createConnection(22, 23, ConnectionDirection.RIGHT);
//        building.createConnection(15, 23, ConnectionDirection.BOTTOM);
//        building.createConnection(15, 16, ConnectionDirection.RIGHT);
//        building.createConnection(16, 17, ConnectionDirection.RIGHT);
//        building.createConnection(17, 18, ConnectionDirection.RIGHT);
//        building.createConnection(18, 19, ConnectionDirection.RIGHT);
//        building.createConnection(19, 20, ConnectionDirection.RIGHT);
//        building.createConnection(20, 21, ConnectionDirection.RIGHT);
//        building.createConnection(16, 24, ConnectionDirection.BOTTOM);
//        building.createConnection(9, 17, ConnectionDirection.BOTTOM);
//        building.createConnection(17, 25, ConnectionDirection.BOTTOM);
//        building.createConnection(10, 18, ConnectionDirection.BOTTOM);
//        building.createConnection(18, 26, ConnectionDirection.BOTTOM);
//        building.createConnection(11, 19, ConnectionDirection.BOTTOM);
//        building.createConnection(19, 27, ConnectionDirection.BOTTOM);
//        building.createConnection(12, 20, ConnectionDirection.BOTTOM);
//        building.createConnection(20, 28, ConnectionDirection.BOTTOM);
//        building.createConnection(13, 21, ConnectionDirection.BOTTOM);
//        building.createConnection(21, 29, ConnectionDirection.BOTTOM);
//
//        return building;
//    }
}