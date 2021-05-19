package root.neo4j;

import org.javatuples.Pair;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import root.geometry.Point;

import java.util.*;


public class Neo4jDriver implements AutoCloseable {
    private final Driver driver;

    public Neo4jDriver() {
        driver = GraphDatabase.driver(
                "bolt://localhost:7687",
                AuthTokens.basic( "neo4j", "letMEin!" )
        );

    }

    public Neo4jDriver( String uri, String user, String password ) {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() {
        driver.close();
    }

    public void readQuery(String query) {
        try ( Session session = driver.session() ) {
            session.readTransaction(new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run(query);
                    while(result.hasNext()) {
                        var record = result.next();
                        System.out.println(Integer.valueOf((String)record.fields().get(0).value().asMap().get("Name")));
                        System.out.println(Integer.valueOf((String)record.fields().get(2).value().asMap().get("Name")));
                    }
                    return "Success";
                }
            });
        }
    }

    private String connectedSpacesQuery() {
        return "MATCH (s1:IfcSpace)--(b1:IfcRelSpaceBoundary)--(d:IfcDoor)--(b2:IfcRelSpaceBoundary)--(s2:IfcSpace)\n" +
                "RETURN s1, s2";
    }

    public List<Pair<Integer, Integer>> readConnectedSpaces() {
        try (Session session = driver.session())
        {
            var result = session.run(connectedSpacesQuery());
            List<Pair<Integer, Integer>> connectedRooms = new LinkedList<>();
            while(result.hasNext()) {
                var record = result.next();
                Integer room1 = Integer.valueOf((String)record.fields().get(0).value().asMap().get("Name"));
                Integer room2 = Integer.valueOf((String)record.fields().get(1).value().asMap().get("Name"));
                if (!connectedRooms.contains(new Pair<>(room2, room1))) { // necessary only while working on single floor
                    connectedRooms.add(new Pair<>(room1, room2));
                }
            }
            return connectedRooms;
        }
    }

    private String areasWithExitQuery() {
        return "MATCH (s:IfcSpace)--(:IfcRelSpaceBoundary)--(:IfcDoor)--(:IfcRelDefinesByProperties)--(:IfcPropertySet {Name:'Pset_DoorCommon'})--(e:IfcPropertySingleValue {Name:'IsExternal', NominalValue:'True'})\n" +
                "RETURN s";
    }

    public List<Integer> readAreasWithExits() {
        try (Session session = driver.session())
        {
            var result = session.run(areasWithExitQuery());
            var roomsWithExits = new ArrayList<Integer>();
            while(result.hasNext()) {
                roomsWithExits.add(Integer.valueOf((String)result.next().fields().get(0).value().asMap().get("Name")));
            }
            return roomsWithExits;
        }
    }

    private String areasWithCoordinatesQuery() {
        return "MATCH path = (:IfcBuildingStorey {Name:'1. Obergeschoss'}) -[:ObjectPlacement]->(:IfcLocalPlacement) <-[:PlacementRelTo * ..]- (:IfcLocalPlacement) <-[:ObjectPlacement]- (space:IfcSpace)\n" +
                "\tWITH space, nodes(path)[2..-1] AS p\n" +
                "\tMATCH (space) -[:Representation]-> (:IfcProductDefinitionShape) -[:Representations]-> (:IfcShapeRepresentation) -[:Items]-> (:IfcExtrudedAreaSolid) -[:SweptArea]-> (:IfcArbitraryClosedProfileDef) -[:OuterCurve]-> (pl:IfcPolyline) -[:Points]-> (point:IfcCartesianPoint)\n" +
                "\tWITH space, p, collect(DISTINCT point) AS points\n" +
                "\tCALL {\n" +
                "\t\tWITH p, points\n" +
                "\t\tUNWIND p AS lp_coord\n" +
                "\t\tMATCH (lp_coord) -[:RelativePlacement]-> (a:IfcAxis2Placement3D)\n" +
                "\t\tWITH a, points\n" +
                "\t\tMATCH (axes_node:IfcDirection) <-[:RefDirection]- (a) -[:Location]-> (coord_node:IfcCartesianPoint)\n" +
                "\t\tWITH points,\n" +
                "\t\t\tsplit(axes_node.DirectionRatios, ',') AS axes, \n" +
                "\t\t\tsplit(coord_node.Coordinates, ',') AS coords\n" +
                "\t\tWITH points,\n" +
                "\t\t\ttoFloat(coords[0]) AS x,\n" +
                "\t\t\ttoFloat(coords[1]) AS y,\n" +
                "\t\t\ttoFloat(axes[0]) AS x_axis_x_coef,\n" +
                "\t\t\ttoFloat(axes[1]) AS x_axis_y_coef\n" +
                "\t\tWITH points, x, y, x_axis_x_coef, x_axis_y_coef,\n" +
                "\t\t\tx_axis_y_coef AS y_axis_x_coef, // x_axis vector rotated by 90 degrees clockwise\n" +
                "\t\t\t-x_axis_x_coef AS y_axis_y_coef // x_axis vector rotated by 90 degrees clockwise\n" +
                "\t\tWITH points, collect([x_axis_x_coef, x_axis_y_coef, y_axis_x_coef, y_axis_y_coef,x,y]) AS coord_trans\n" +
                "\t\tWITH points,\n" +
                "\t\t\treduce(outer_coefs = [1.0,0.0,0.0,1.0,0.0,0.0], inner_coefs IN coord_trans | [\n" +
                "\t\t\t\touter_coefs[0] * inner_coefs[0] + outer_coefs[2] * inner_coefs[1],\n" +
                "\t\t\t\touter_coefs[1] * inner_coefs[0] + outer_coefs[3] * inner_coefs[1],\n" +
                "\t\t\t\touter_coefs[0] * inner_coefs[2] + outer_coefs[2] * inner_coefs[3],\n" +
                "\t\t\t\touter_coefs[1] * inner_coefs[2] + outer_coefs[3] * inner_coefs[3],\n" +
                "\t\t\t\touter_coefs[4] + inner_coefs[4] * outer_coefs[0] + inner_coefs[5] * outer_coefs[1],\n" +
                "\t\t\t\touter_coefs[5] + inner_coefs[4] * outer_coefs[2] + inner_coefs[5] * outer_coefs[3]\n" +
                "\t\t\t]) AS coord_system\n" +
                "\t\tWITH [p IN points | split(p.Coordinates, ',')] AS points, coord_system\n" +
                "\t\tWITH [p IN points | [coord IN p | toFloat(coord)]] AS points, coord_system\n" +
                "\t\tRETURN [p IN points | [coord_system[4] + coord_system[0] * p[0] + coord_system[1] * p[1],\n" +
                "\t\t\t\t\t\t\tcoord_system[5] + coord_system[2] + p[0] + coord_system[3] * p[1]]] AS points_global\n" +
                "\t}\n" +
                "\tRETURN space.Name, space.GlobalId, points_global";
    }

    public ArrayList<AreaResult> readAreasWithCoordinates() {
        try (Session session = driver.session())
        {
            var result = session.run(areasWithCoordinatesQuery());
            var resultData = new ArrayList<AreaResult>();
            while(result.hasNext()) {
                var rs = result.next();
                var values = rs.values();
                var spaceId = Integer.valueOf(values.get(0).asString());
                var coor = new ArrayList<Point>();
                for(int i=0; i < 4; i++) {
                    Point coordinates = new Point((double) values.get(2).get(i).asList().get(0), (double) values.get(2).get(i).asList().get(1));
                    coor.add(coordinates);
                }
                resultData.add(new AreaResult(spaceId, coor));
            }
            return resultData;
        }
    }

    public static void main( String... args )
        {
            var uri = "bolt://localhost:7687";
            var user = "neo4j";
            var password = "letMEin!";

            try ( Neo4jDriver neo4jDriver = new Neo4jDriver( uri, user, password ) )
            {
                System.out.println("=======================");
                var roomsWithCoordinates = neo4jDriver.readAreasWithCoordinates();
                for (var c : roomsWithCoordinates) {
                    System.out.println(c);
                }
                System.out.println("=======================");
                var roomsWithExits = neo4jDriver.readAreasWithExits();
                for (var c : roomsWithExits) {
                    System.out.println(c);
                }
                System.out.println("=======================");
                var connectedRooms = neo4jDriver.readConnectedSpaces();
                System.out.println(connectedRooms);
                System.out.println("=======================");
            }
        }
}
