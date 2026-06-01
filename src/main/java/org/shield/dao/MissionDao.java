package org.shield.dao;

import org.shield.db.DatabaseConnection;
import org.shield.model.Mission;
import org.shield.model.MissionCategory;
import org.shield.model.MissionPriority;
import org.shield.model.MissionStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MissionDao {
    private Mission mapRow(ResultSet resultSet) throws SQLException {
        return new Mission(
                resultSet.getInt("mission_id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                MissionStatus.valueOf(resultSet.getString("status").toUpperCase()),
                MissionPriority.valueOf(resultSet.getString("priority").toUpperCase()),
                resultSet.getObject("hero_id", Integer.class),
                MissionCategory.valueOf(resultSet.getString("category").toUpperCase()),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getString("hero_name")
        );
    }
    public List<Mission> findAll() {
        String query = """
                SELECT M.mission_id, M.title, M.description, M.status, M.priority, M.category, M.created_at,
                       M.hero_id, H.hero_name
                FROM Mission M
                LEFT JOIN Hero H 
                ON M.hero_id = H.hero_id;
                """;
        List<Mission> missions = new ArrayList<>();

        try (var connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)){

            while (resultSet.next()) {
                Mission mission = mapRow(resultSet);
                missions.add(mission);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return missions;
    }

    public Optional<Mission> findById(int id) {
        String query = """
                SELECT M.mission_id, M.title, M.description, M.status, M.priority, M.category, M.created_at,
                       M.hero_id, H.hero_name
                FROM Mission M 
                LEFT JOIN Hero H
                ON M.hero_id = H.hero_id
                WHERE M.mission_id = ?;
                """;
        try (var connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
             ){
            preparedStatement.setInt(1,id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public boolean insert(Mission m) {
        String query = """
                INSERT INTO Mission (title, description, status, priority, category, hero_id)
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        try (var connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setString(1, m.getTitle());
            preparedStatement.setString(2, m.getDescription());
            preparedStatement.setObject(3, m.getStatus().name().toLowerCase(), Types.OTHER);
            preparedStatement.setObject(4, m.getPriority().name().toLowerCase(), Types.OTHER);
            preparedStatement.setObject(5, m.getCategory().name().toLowerCase(), Types.OTHER);
            preparedStatement.setObject(6, m.getHeroId());
            if (preparedStatement.executeUpdate() != 0) return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean updateStatus(int id, String status) {
        String query = """
                UPDATE Mission
                SET status = ?
                WHERE mission_id = ?;
                """;

        try (var connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setObject(1,
                    MissionStatus.valueOf(status.toUpperCase()).name().toLowerCase(),
                    Types.OTHER);
            preparedStatement.setInt(2, id);
            if (preparedStatement.executeUpdate() != 0) return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean delete(int id) {
        String query = """
                DELETE FROM Mission
                WHERE mission_id = ?;
                """;

        try (var connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setInt(1, id);
            if (preparedStatement.executeUpdate() != 0) return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
