package com.agent.entity.employees.vo;

import lombok.Data;

@Data
public class EmployeeOffboardingReq {
    private Integer empId;
    private String resignType;
}