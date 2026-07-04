package com.agent.service;

import com.agent.result.Result;

public interface DocumentService {
    Result<String> generateOnboardingMaterials(Integer empId);

    Result<String> generateOffboardingList(Integer empId);
}