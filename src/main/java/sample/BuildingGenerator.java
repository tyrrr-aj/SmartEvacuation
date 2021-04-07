package sample;

public class BuildingGenerator {

    // []
    // [][]
    // []
    public static Building getSmallBuilding() {
        Building building = new Building();
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, true);
        building.createConnection(0, 1, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(1, 2, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(1, 3, Neighbour.NeighboursConnection.BOTTOM);

        return building;
    }

    //   []
    // [][][][]
    //   []
    public static Building getSmallBuilding2() {
        Building building = new Building();
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, true);

        building.createConnection(0, 2, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(1, 2, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(2, 4, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(2, 3, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(3, 5, Neighbour.NeighboursConnection.RIGHT);

        return building;
    }

    //   []
    //   [][][]
    //       []
    // [][][][]
    public static Building getSmallBuilding3() {
        Building building = new Building();
        building.addArea(false, true);
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, false);
        building.addArea(false, true);

        building.createConnection(0, 1, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(1, 2, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(2, 3, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(3, 4, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(4, 5, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(5, 6, Neighbour.NeighboursConnection.LEFT);
        building.createConnection(6, 7, Neighbour.NeighboursConnection.LEFT);
        building.createConnection(7, 8, Neighbour.NeighboursConnection.LEFT);

        return building;
    }

    // [][][]
    // [][][]
    // [][][][][][][][]
    // [][][][][][][][]
    // [][][][][][][][]
    public static Building getSmallBuilding4() {
        Building building = new Building();
        building.addArea(false, false); // 0
        building.addArea(false, true); // 1
        building.addArea(false, false); // 2
        building.addArea(false, false); // 3
        building.addArea(false, false); // 4
        building.addArea(false, false); // 5
        building.addArea(false, false); // 6
        building.addArea(false, false); // 7
        building.addArea(false, false); // 8
        building.addArea(false, false); // 9
        building.addArea(false, false); // 10
        building.addArea(false, true); // 11
        building.addArea(false, false); // 12
        building.addArea(false, false); // 13
        building.addArea(false, false); // 14
        building.addArea(false, false); // 15
        building.addArea(false, false); // 16
        building.addArea(false, false); // 17
        building.addArea(false, false); // 18
        building.addArea(false, false); // 19
        building.addArea(false, false); // 20
        building.addArea(false, false); // 21
        building.addArea(false, false); // 22
        building.addArea(false, false); // 23
        building.addArea(false, false); // 24
        building.addArea(false, false); // 25
        building.addArea(false, false); // 26
        building.addArea(false, false); // 27
        building.addArea(false, false); // 28
        building.addArea(false, false); // 29

        building.createConnection(0, 1, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(1, 2, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(1, 4, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(3, 4, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(4, 5, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(3, 6, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(4, 7, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(14, 15, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(7, 15, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(8, 16, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(22, 23, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(15, 23, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(15, 16, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(16, 17, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(17, 18, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(18, 19, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(19, 20, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(20, 21, Neighbour.NeighboursConnection.RIGHT);
        building.createConnection(16, 24, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(9, 17, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(17, 25, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(10, 18, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(18, 26, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(11, 19, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(19, 27, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(12, 20, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(20, 28, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(13, 21, Neighbour.NeighboursConnection.BOTTOM);
        building.createConnection(21, 29, Neighbour.NeighboursConnection.BOTTOM);

        return building;
    }

//    public static Building getBigBuilding(){
//        Building building = new Building();
//        building.addArea(true, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(true, true);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, true);
//        building.createConnection(0, 1);
//        building.createConnection(0, 3);
//        building.createConnection(1, 3);
//        building.createConnection(2, 3);
//        building.createConnection(5, 6);
//        building.createConnection(5, 3);
//        building.createConnection(6, 3);
//        building.createConnection(7, 3);
//        building.createConnection(7, 8);
//        building.createConnection(3, 9);
//
//        return building;
//    }

//    public static Building getAppBuilding(){
//        Building building = new Building();
//        building.addArea(false, false);
//        building.addArea(false, true);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, true);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//        building.addArea(false, false);
//
//        building.createConnection(0, 1);
//        building.createConnection(3, 4);
//        building.createConnection(2, 1);
//        building.createConnection(5, 4);
//        building.createConnection(4, 1);
//        building.createConnection(4, 6);
//        building.createConnection(8, 11);
//        building.createConnection(8, 9);
//        building.createConnection(9, 6);
//        building.createConnection(9, 12);
//        building.createConnection(9, 10);
//        building.createConnection(10, 7);
//        building.createConnection(10, 13);
//        building.createConnection(10, 18);
//        building.createConnection(18, 23);
//        building.createConnection(18, 14);
//        building.createConnection(18, 19);
//        building.createConnection(15, 19);
//        building.createConnection(24, 19);
//        building.createConnection(19, 20);
//        building.createConnection(16, 20);
//        building.createConnection(25, 20);
//        building.createConnection(21, 20);
//        building.createConnection(17, 21);
//        building.createConnection(26, 21);
//        building.createConnection(22, 21);
//        building.createConnection(27, 22);
//
//        return building;
//    }
}
