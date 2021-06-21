package root.config;

public class Neo4jConfig {
    private final String uri;
    private final String user;
    private final String password;
    private final String databaseName;

    public Neo4jConfig(String uri, String user, String password, String databaseName) {
        this.uri = uri;
        this.user = user;
        this.password = password;
        this.databaseName = databaseName;
    }

    public String getUri() {
        return uri;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
