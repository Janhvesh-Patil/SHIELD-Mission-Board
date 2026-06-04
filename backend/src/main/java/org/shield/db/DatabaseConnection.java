package org.shield.db;

import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null) {
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

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        }

        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        return dataSource.getConnection(user, password);
    }
}
