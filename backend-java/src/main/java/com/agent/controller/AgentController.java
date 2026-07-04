package com.agent.controller;

import com.agent.entity.AgentChatData;
import com.agent.entity.AgentChatReq;
import com.agent.result.Result;
import com.agent.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/agent")
public class AgentController {
    @Autowired
    private AgentService agentService;

    @PostMapping("/chat")
    public Result<AgentChatData> chat(@RequestBody AgentChatReq agentChatReq) {
        return agentService.chat(agentChatReq);
    }

    @PostMapping(value = "/chat", consumes = "multipart/form-data")
    public Result<AgentChatData> chatWithFile(
            @RequestParam("thread_id") String threadId,
            @RequestParam("query") String query,
            @RequestParam(value = "emp_id", required = false) Integer empId,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        String filePath = null;
        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = file.getOriginalFilename();
                if (fileName == null || fileName.isEmpty()) {
                    fileName = "upload_" + System.currentTimeMillis() + ".tmp";
                }
                filePath = uploadDir + File.separator + fileName;
                File destFile = new File(filePath);
                file.transferTo(destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return agentService.chatWithFile(threadId, query, empId, filePath);
    }
}
