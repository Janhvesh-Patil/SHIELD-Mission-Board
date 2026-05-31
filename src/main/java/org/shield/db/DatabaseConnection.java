package org.shield.db;

import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (var stream = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (stream == null) {
                throw new RuntimeException("config.properties not found on classpath");
            } else {
                props.load(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl(props.getProperty("db.url"));
        return dataSource.getConnection(props.getProperty("db.user"),
                props.getProperty("db.password"));
    }
}
