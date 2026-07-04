package com.agent.openfeign;

import com.agent.entity.AgentChatData;
import com.agent.entity.AgentChatReq;
import com.agent.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-agent-service")
public interface AgentFeign {

    @PostMapping("/agent/chat")
    Result<AgentChatData> chat(@RequestBody AgentChatReq agentChatReq);
}
