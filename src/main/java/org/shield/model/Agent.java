package org.shield.model;

public class Agent {
    private int agentId;
    private String agentName;

    public Agent(int agentId, String agentName) {
        this.agentId = agentId;
        this.agentName = agentName;
    }

    public int getAgentId() {
        return agentId;
    }

    public String getAgentName() {
        return agentName;
    }
}
