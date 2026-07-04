package com.agent.entity.payroll.vo;

import lombok.Data;

@Data
public class PayrollQueryReq {
    private Integer empId;
    private String salaryMonth;
}