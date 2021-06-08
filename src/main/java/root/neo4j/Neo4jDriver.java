package root.neo4j;

import org.javatuples.Pair;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import root.geometry.Point;

import java.util.*;
import java.util.stream.Collectors;


public class Neo4jDriver implements AutoCloseable {
    private final Driver driver;

    public Neo4jDriver() {
        driver = GraphDatabase.driver(
                "bolt://localhost:7687",
//                AuthTokens.basic( "neo4j", "password" )
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
            session.readTransaction(tx -> {
                Result result = tx.run(query);
                while(result.hasNext()) {
                    var record = result.next();
                    System.out.println(Integer.valueOf((String)record.fields().get(0).value().asMap().get("Name")));
                    System.out.println(Integer.valueOf((String)record.fields().get(2).value().asMap().get("Name")));
                }
                return "Success";
            });
        }
    }

    private String orderedFloorsQuery() {
        return "MATCH (s:IfcBuildingStorey)\n" +
                "RETURN s.GlobalId\n" +
                "ORDER BY s.Elevation";
    }

    public List<String> readOrderedFloors() {
        try (Session session = driver.session()) {
            var result = session.run(orderedFloorsQuery());
            return result
                    .stream()
                    .map(Record::values)
                    .map(values -> values.get(0).asString())
                    .collect(Collectors.toList());
        }
    }

    private String connectedSpacesQuery() {
        return "MATCH (s1:IfcSpace)--(b1:IfcRelSpaceBoundary)--(d:IfcDoor)--(b2:IfcRelSpaceBoundary)--(s2:IfcSpace)\n" +
                "RETURN s1.Name, d.GlobalId, s2.Name";
    }

    public Map<Integer, List<Pair<Integer, String>>> readConnectedSpaces() {
        try (Session session = driver.session()) {
            var result = session.run(connectedSpacesQuery());
            Map<Integer, List<Pair<Integer, String>>> connectedRooms = new HashMap<>();
            while (result.hasNext()) {
                var record = result.next();
                var values = record.values();
                var s1 = Integer.valueOf(values.get(0).asString());
                var doorId = values.get(1).asString();
                var s2 = Integer.valueOf(values.get(2).asString());
                connectedRooms.putIfAbsent(s1, new LinkedList<>());
                connectedRooms.get(s1).add(new Pair<>(s2, doorId));
            }
            return connectedRooms;
        }
    }

    private String areasWithExitQuery() {
        return "MATCH (s:IfcSpace)--(:IfcRelSpaceBoundary)--(:IfcDoor)--(:IfcRelDefinesByProperties)--(:IfcPropertySet {Name:'Pset_DoorCommon'})--(e:IfcPropertySingleValue {Name:'IsExternal', NominalValue:'True'})\n" +
                "RETURN s.Name\n" +
                "UNION\n" +
                "MATCH (s1:IfcSpace)--(:IfcRelSpaceBoundary {PhysicalOrVirtualBoundary:'VIRTUAL'})--(:IfcVirtualElement)--(:IfcRelSpaceBoundary)--(s2:IfcSpace)\n" +
                "UNWIND [s1, s2] as s\n" +
                "RETURN s.Name";
    }

    public List<Integer> readAreasWithExits() {
        try (Session session = driver.session())
        {
            var result = session.run(areasWithExitQuery());
            var roomsWithExits = new ArrayList<Integer>();
            while(result.hasNext()) {
                var rs = result.next();
                var values = rs.values();
                roomsWithExits.add(Integer.valueOf(values.get(0).asString()));
            }
            return roomsWithExits;
        }
    }

    private String areasWithCoordinatesQuery() {
        return "MATCH path = (:IfcBuilding) -[:ObjectPlacement]->(:IfcLocalPlacement) <-[:PlacementRelTo * ..]- (:IfcLocalPlacement) <-[:ObjectPlacement]- (space:IfcSpace)\n" +
                "\tWITH space, nodes(path)[2..-1] AS p\n" +
                "\tMATCH (storey:IfcBuildingStorey) <-[:RelatingObject]- (:IfcRelAggregates) -[:RelatedObjects]-> (space) -[:Representation]-> (:IfcProductDefinitionShape) -[:Representations]-> (:IfcShapeRepresentation) -[:Items]-> (:IfcExtrudedAreaSolid) -[:SweptArea]-> (:IfcArbitraryClosedProfileDef) -[:OuterCurve]-> (pl:IfcPolyline) -[:Points]-> (point:IfcCartesianPoint)\n" +
                "\tWITH storey, space, p, collect(DISTINCT point) AS points\n" +
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
                "\t\t\t-x_axis_y_coef AS y_axis_x_coef, // x_axis vector rotated by 90 degrees clockwise\n" +
                "\t\t\tx_axis_x_coef AS y_axis_y_coef // x_axis vector rotated by 90 degrees clockwise\n" +
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
                "\t\tRETURN [p IN points | [coord_system[4] + coord_system[0] * p[0] + coord_system[2] * p[1],\n" +
                "\t\t\t\t\t\t\tcoord_system[5] + coord_system[1] * p[0] + coord_system[3] * p[1]]] AS points_global\n" +
                "\t}\n" +
                "\tRETURN space.Name, space.GlobalId, storey.GlobalId, points_global";
    }

    public List<AreaResult> readAreasWithCoordinates() {
        try (Session session = driver.session())
        {
            var result = session.run(areasWithCoordinatesQuery());
            var resultData = new ArrayList<AreaResult>();
            while(result.hasNext()) {
                var rs = result.next();
                var values = rs.values();
                var spaceId = Integer.parseInt(values.get(0).asString());
                var floorId = values.get(2).asString();
                List<Point> retrieved_coords = values.get(3)
                        .asList(v -> v.asList(Value::asDouble))
                        .stream()
                        .map(coords -> new Point(coords.get(0), coords.get(1)))
                        .collect(Collectors.toList());
                resultData.add(new AreaResult(spaceId, floorId, retrieved_coords));
            }
            return resultData;
        }
    }

    public String doorsWithCoordinatesQuery() {
        return "MATCH path = (:IfcBuilding) -[:ObjectPlacement]->(:IfcLocalPlacement) <-[:PlacementRelTo * ..]- (:IfcLocalPlacement) <-[:ObjectPlacement]- (obj:IfcDoor)\n" +
                "\tWITH obj, nodes(path)[2..-1] AS p\n" +
                "\tCALL {\n" +
                "\t\tWITH p\n" +
                "\t\tUNWIND p AS lp_coord\n" +
                "\t\tMATCH (lp_coord) -[:RelativePlacement]-> (a:IfcAxis2Placement3D)\n" +
                "\t\tWITH a\n" +
                "\t\tMATCH (axes_node:IfcDirection) <-[:RefDirection]- (a) -[:Location]-> (coord_node:IfcCartesianPoint)\n" +
                "\t\tWITH\n" +
                "\t\t\tsplit(axes_node.DirectionRatios, ',') AS axes, \n" +
                "\t\t\tsplit(coord_node.Coordinates, ',') AS coords\n" +
                "\t\tWITH\n" +
                "\t\t\ttoFloat(coords[0]) AS x,\n" +
                "\t\t\ttoFloat(coords[1]) AS y,\n" +
                "\t\t\ttoFloat(axes[0]) AS x_axis_x_coef,\n" +
                "\t\t\ttoFloat(axes[1]) AS x_axis_y_coef\n" +
                "\t\tWITH x, y, x_axis_x_coef, x_axis_y_coef,\n" +
                "\t\t\t-x_axis_y_coef AS y_axis_x_coef, // x_axis vector rotated by 90 degrees clockwise\n" +
                "\t\t\tx_axis_x_coef AS y_axis_y_coef // x_axis vector rotated by 90 degrees clockwise\n" +
                "\t\tWITH collect([x_axis_x_coef, x_axis_y_coef, y_axis_x_coef, y_axis_y_coef,x,y]) AS coord_trans\n" +
                "\t\tWITH reduce(outer_coefs = [1.0,0.0,0.0,1.0,0.0,0.0], inner_coefs IN coord_trans | [\n" +
                "\t\t\touter_coefs[0] * inner_coefs[0] + outer_coefs[2] * inner_coefs[1],\n" +
                "\t\t\touter_coefs[1] * inner_coefs[0] + outer_coefs[3] * inner_coefs[1],\n" +
                "\t\t\touter_coefs[0] * inner_coefs[2] + outer_coefs[2] * inner_coefs[3],\n" +
                "\t\t\touter_coefs[1] * inner_coefs[2] + outer_coefs[3] * inner_coefs[3],\n" +
                "\t\t\touter_coefs[4] + inner_coefs[4] * outer_coefs[0] + inner_coefs[5] * outer_coefs[2],\n" +
                "\t\t\touter_coefs[5] + inner_coefs[4] * outer_coefs[1] + inner_coefs[5] * outer_coefs[3]\n" +
                "\t\t]) AS coord_system\n" +
                "\t\tRETURN coord_system\n" +
                "\t}\n" +
                "\tWITH obj, coord_system, toFloat(obj.OverallWidth) * 0.5 AS w\n" +
                "\tRETURN obj.GlobalId, coord_system[4] + coord_system[0] * w AS x, coord_system[5] + coord_system[1] * w AS y";
    }

    public List<DoorResult> readDoorsWithCoordinates() {
        try (Session session = driver.session())
        {
            var result = session.run(doorsWithCoordinatesQuery());
            var resultData = new ArrayList<DoorResult>();
            while(result.hasNext()) {
                var rs = result.next();
                var values = rs.values();
                var doorId = values.get(0).asString();
                var x = values.get(1).asDouble();
                var y = values.get(2).asDouble();
                resultData.add(new DoorResult(doorId, new Point(x, y)));
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
