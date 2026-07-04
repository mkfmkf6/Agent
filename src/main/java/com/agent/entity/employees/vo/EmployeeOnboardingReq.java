package com.agent.entity.employees.vo;

import lombok.Data;

@Data
public class EmployeeOnboardingReq {
    private String empNo;
    private String name;
    private Integer gender;
    private String department;
    private String position;
    private String phone;
    private String email;
    private String idCard;
}