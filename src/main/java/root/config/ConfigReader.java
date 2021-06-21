package root.config;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

public class ConfigReader {
    private final Ini ini;

    public ConfigReader() throws IOException {
        ini = new Ini(new File("config.cfg"));
    }

    public ConfigReader(String configPath) throws IOException {
        ini = new Ini(new File(configPath));
    }

    public Neo4jConfig getNeo4jConfig() {
        final String neo4jSectionName = "Neo4j";

        return new Neo4jConfig(
                ini.get(neo4jSectionName, "uri"),
                ini.get(neo4jSectionName, "user"),
                ini.get(neo4jSectionName, "password"),
                ini.get(neo4jSectionName, "db_name")
        );
    }
}
