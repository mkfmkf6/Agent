package com.agent.service;

import com.agent.result.Result;

import java.util.Map;

public interface DashboardService {
    Result<Map<String, Object>> getDashboardStats();
    Result<Map<String, Object>> getChartsData();
}