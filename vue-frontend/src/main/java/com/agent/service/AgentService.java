package com.agent.service;

import com.agent.entity.AgentChatData;
import com.agent.entity.AgentChatReq;
import com.agent.result.Result;

public interface AgentService {
    Result<AgentChatData> chat(AgentChatReq agentChatReq);
    Result<AgentChatData> chatWithFile(String threadId, String query, Integer empId, String filePath);
}
