package com.agent.entity.payroll.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollResultVo {
    private Integer payrollId;
    private Integer empId;
    private String salaryMonth;
    private BigDecimal baseSalary;
    private BigDecimal performanceSalary;
    private BigDecimal postAllowance;
    private BigDecimal transportAllowance;
    private BigDecimal mealAllowance;
    private BigDecimal otherAllowance;
    private BigDecimal grossSalary;
    private BigDecimal socialSecurityPersonal;
    private BigDecimal housingFundPersonal;
    private BigDecimal personalIncomeTax;
    private BigDecimal otherDeduction;
    private BigDecimal netSalary;
    private String payStatus;
}