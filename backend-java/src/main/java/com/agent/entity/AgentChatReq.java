package com.agent.entity;


import lombok.Data;

@Data
public class AgentChatReq {
    private String thread_id;
    private String query;
}
