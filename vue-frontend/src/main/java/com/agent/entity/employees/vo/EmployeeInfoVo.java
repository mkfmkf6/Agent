package com.agent.entity.employees.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeInfoVo {
    private Integer empId;
    private String empNo;
    private String name;
    private String gender;
    private String department;
    private String position;
    private String jobLevel;
    private LocalDate hireDate;
    private String phone;
    private String email;
    private String managerName;
    private String empStatus;
    private BigDecimal annualLeaveQuota;
    private BigDecimal annualLeaveUsed;
}