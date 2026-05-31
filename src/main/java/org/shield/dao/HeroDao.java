package org.shield.dao;

import org.shield.db.DatabaseConnection;
import org.shield.model.Hero;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HeroDao {
    public List<Hero> findAll() {
        String query = "SELECT hero_id, hero_name FROM hero;";
        List<Hero> heroes =new ArrayList<>();
        try (var connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ) {

            while (resultSet.next()) {
                Hero hero = new Hero(resultSet.getInt("hero_id"),
                        resultSet.getString("hero_name"));
                heroes.add(hero);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return heroes;
    }
}
