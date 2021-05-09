package sample.neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

public class Neo4jDriver implements AutoCloseable {
    private final Driver driver;

    public Neo4jDriver( String uri, String user, String password ) {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() {
        driver.close();
    }

    public void readQuery(String query) {
        try ( Session session = driver.session() ) {
            session.readTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run(query);

                    while(result.hasNext()) {
                        System.out.println(result.next());
                    }
                    return "Success";
                }
            } );
        }
    }

    public void getConnectedSpaces() {
        readQuery("MATCH (s1:IfcSpace)--(b1:IfcRelSpaceBoundary)--(d:IfcDoor)--(b2:IfcRelSpaceBoundary)--(s2:IfcSpace)\n" +
                "CALL apoc.create.vRelationship(s1,'IS_CONNECTED',{},s2) YIELD rel\n" +
                "RETURN s1, rel, s2");
    }

    public void getAreasWithExit() {
        readQuery("MATCH (s:IfcSpace)--(:IfcRelSpaceBoundary)--(:IfcDoor)--(:IfcRelDefinesByProperties)--(:IfcPropertySet {Name:'Pset_DoorCommon'})--(e:IfcPropertySingleValue {Name:'IsExternal', NominalValue:'True'})\n" +
                "RETURN s");
    }

    public void getConnectionsBetweenFloors() {
        readQuery("MATCH (s1:IfcSpace)--(:IfcRelSpaceBoundary {PhysicalOrVirtualBoundary:'VIRTUAL'})--(:IfcVirtualElement)--(:IfcRelSpaceBoundary)--(s2:IfcSpace)\n" +
                        "CALL apoc.create.vRelationship(s1,'CONNECT_LEVELS',{},s2) YIELD rel\n" +
                        "RETURN s1, rel, s2");
    }

    public void getHeightFromGround() {
        readQuery("MATCH (s:IfcSpace)--(:IfcRelAggregates)--(storey:IfcBuildingStorey)\n" +
                                    "\tRETURN s, storey.Elevation");
    }

    public void getConnectedSpacesWithFloors() {
        readQuery("MATCH (s1:IfcSpace)--(b1:IfcRelSpaceBoundary)--(:IfcDoor)--(b2:IfcRelSpaceBoundary)--(s2:IfcSpace)--(:IfcRelAggregates)--(storey:IfcBuildingStorey)\n" +
                                    "CALL apoc.create.vRelationship(s1,'IS_CONNECTED',{},s2) YIELD rel\n" +
                                    "RETURN s1, rel, s2, storey.Elevation");
    }

    public static void main( String... args )
        {
            var uri = "bolt://localhost:7687";
            var user = "neo4j";
            var password = "password";
            System.out.println("=======================");
            System.out.println("RUN QUERY");
            try ( Neo4jDriver neo4jDriver = new Neo4jDriver( uri, user, password ) )
            {
                neo4jDriver.getConnectedSpaces();
                System.out.println("=======================");
                neo4jDriver.getAreasWithExit();
                System.out.println("=======================");
                neo4jDriver.getConnectionsBetweenFloors();
                System.out.println("=======================");
                neo4jDriver.getHeightFromGround();
                System.out.println("=======================");
                neo4jDriver.getConnectedSpacesWithFloors();
                System.out.println("=======================");
            }
        }
}
