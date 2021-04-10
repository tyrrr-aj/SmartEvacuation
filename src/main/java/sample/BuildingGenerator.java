package sample;

public class BuildingGenerator {

    // []
    // [][]
    // []
    public static Building getSmallBuilding() {
        Building building = new Building();
        building.addArea(false);
        building.addArea(false);
        building.addArea(false);
        building.addArea(false, Neighbour.NeighboursConnection.BOTTOM);
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
        building.addArea(false);
        building.addArea(false);
        building.addArea(false);
        building.addArea(false);
        building.addArea(false);
        building.addArea(false, Neighbour.NeighboursConnection.RIGHT);

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
        building.addArea(false, Neighbour.NeighboursConnection.TOP);
        building.addArea(false);
        building.addArea(false);
        building.addArea(false);
        building.addArea(false);
        building.addArea(false);
        building.addArea(false);
        building.addArea(false);
        building.addArea(false);

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
        building.addArea(false); // 0
        building.addArea(false, Neighbour.NeighboursConnection.TOP); // 1
        building.addArea(false); // 2
        building.addArea(false); // 3
        building.addArea(false); // 4
        building.addArea(false); // 5
        building.addArea(false); // 6
        building.addArea(false); // 7
        building.addArea(false); // 8
        building.addArea(false); // 9
        building.addArea(false); // 10
        building.addArea(false, Neighbour.NeighboursConnection.TOP); // 11
        building.addArea(false); // 12
        building.addArea(false); // 13
        building.addArea(false); // 14
        building.addArea(false); // 15
        building.addArea(false); // 16
        building.addArea(false); // 17
        building.addArea(false); // 18
        building.addArea(false); // 19
        building.addArea(false); // 20
        building.addArea(false); // 21
        building.addArea(false); // 22
        building.addArea(false); // 23
        building.addArea(false); // 24
        building.addArea(false); // 25
        building.addArea(false); // 26
        building.addArea(false); // 27
        building.addArea(false); // 28
        building.addArea(false); // 29

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
}