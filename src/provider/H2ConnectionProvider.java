package provider;

import annotation.Provides;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionProvider {

    @Provides
    public Connection buildConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:h2:/Users/shihabrahman/projects/java/pluralsight-java-reflection/db-files/db-pluralsight",
                "sa", "");
    }
}
