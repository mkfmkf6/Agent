package com.agent.controller;

import com.agent.result.Result;
import com.agent.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return dashboardService.getDashboardStats();
    }

    @GetMapping("/charts")
    public Result<Map<String, Object>> getChartsData() {
        return dashboardService.getChartsData();
    }
}