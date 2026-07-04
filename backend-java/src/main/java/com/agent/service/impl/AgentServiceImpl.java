package com.agent.service.impl;

import com.agent.entity.AgentChatData;
import com.agent.entity.AgentChatReq;
import com.agent.openfeign.AgentFeign;
import com.agent.result.Result;
import com.agent.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentFeign agentFeign;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Result<AgentChatData> chat(AgentChatReq agentChatReq) {
        return agentFeign.chat(agentChatReq);
    }

    public Result<AgentChatData> chatWithFile(String threadId, String query, Integer empId, String filePath) {
        try {
            String pythonAgentUrl = "http://ai-agent-service/agent/chat_with_file";
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("thread_id", threadId);
            
            if (filePath != null && !filePath.isEmpty()) {
                File file = new File(filePath);
                if (file.exists()) {
                    body.add("file", new FileSystemResource(file));
                }
            }
            
            if (query != null && !query.isEmpty()) {
                body.add("query", query);
            } else if (filePath != null) {
                body.add("query", "请帮我解析这份文件");
            } else {
                body.add("query", "");
            }
            
            if (empId != null) {
                body.add("emp_id", empId.toString());
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Result> response = restTemplate.postForEntity(pythonAgentUrl, request, Result.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
            return Result.error("调用智能体失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("调用智能体失败：" + e.getMessage());
        }
    }
}
