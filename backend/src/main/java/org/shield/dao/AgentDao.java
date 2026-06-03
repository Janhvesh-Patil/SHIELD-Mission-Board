package org.shield.dao;

import org.shield.db.DatabaseConnection;
import org.shield.model.Agent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AgentDao {
    public List<Agent> findAll() {
        String query = "SELECT agent_id, agent_name FROM agent;";
        List<Agent> agents =new ArrayList<>();
        try (var connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ) {

            while (resultSet.next()) {
                Agent agent = new Agent(resultSet.getInt("agent_id"),
                        resultSet.getString("agent_name"));
                agents.add(agent);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return agents;
    }
}
