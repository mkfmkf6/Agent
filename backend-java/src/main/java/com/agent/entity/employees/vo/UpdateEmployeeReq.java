package com.agent.entity.employees.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateEmployeeReq {
    private Integer empId;
    private String name;
    private Integer gender;
    private String department;
    private String position;
    private String jobLevel;
    private String phone;
    private String email;
    private Integer managerId;
}