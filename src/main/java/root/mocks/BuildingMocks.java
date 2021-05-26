package root.mocks;

import root.models.Building;
import root.models.ConnectionDirection;
import root.models.Floor;
import root.neo4j.Neo4jDriver;

public class BuildingMocks {
    //     [0]
    //     [1][2]
    //     [3]
    //
    //      Exit in 3 on bottom wall
    //      3 Floors
    public static Building getSmallBuilding() {
        Building building = new Building();
        Floor floor = new Floor();

        for(int i = 0; i < 3; i++) {
            floor.addArea(i, false);
        }
        floor.addArea(3, false, ConnectionDirection.BOTTOM);

        floor.createConnection(0, 1, ConnectionDirection.BOTTOM);
        floor.createConnection(1, 2, ConnectionDirection.RIGHT);
        floor.createConnection(1, 3, ConnectionDirection.BOTTOM);

        building.addFloor(floor);
        building.addFloor(floor);
        building.addFloor(floor);
        return building;
    }

    //        [0]
    //     [1][2][3][4]
    //        [5]
    //
    //    Exit in 5 on bottom wall
    //    2 Floors
    public static Building getSmallBuilding2() {
        Building building = new Building();
        Floor floor = new Floor();

        for(int i = 0; i < 5; i++) {
            floor.addArea(i, false);
        }
        floor.addArea(5, false, ConnectionDirection.BOTTOM);

        floor.createConnection(0, 2, ConnectionDirection.BOTTOM);
        floor.createConnection(1, 2, ConnectionDirection.RIGHT);
        floor.createConnection(2, 3, ConnectionDirection.RIGHT);
        floor.createConnection(2, 5, ConnectionDirection.BOTTOM);
        floor.createConnection(3, 4, ConnectionDirection.RIGHT);

        building.addFloor(floor);
        building.addFloor(floor);
        return building;
    }

    //       [0]
    //       [1][2][3]
    //             [4]
    //    [8][7][6][5]
    //
    //    Exit in 0 on top wall
    //    4 Floors
    public static Building getSmallBuilding3() {
        Building building = new Building();
        Floor floor = new Floor();

        floor.addArea(0, false, ConnectionDirection.TOP);
        for(int i = 1; i < 9; i++) {
            floor.addArea(i, false);
        }

        floor.createConnection(0, 1, ConnectionDirection.BOTTOM);
        floor.createConnection(1, 2, ConnectionDirection.RIGHT);
        floor.createConnection(2, 3, ConnectionDirection.RIGHT);
        floor.createConnection(3, 4, ConnectionDirection.BOTTOM);
        floor.createConnection(4, 5, ConnectionDirection.BOTTOM);
        floor.createConnection(5, 6, ConnectionDirection.LEFT);
        floor.createConnection(6, 7, ConnectionDirection.LEFT);
        floor.createConnection(7, 8, ConnectionDirection.LEFT);

        building.addFloor(floor);
        building.addFloor(floor);
        building.addFloor(floor);
        building.addFloor(floor);
        return building;
    }
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