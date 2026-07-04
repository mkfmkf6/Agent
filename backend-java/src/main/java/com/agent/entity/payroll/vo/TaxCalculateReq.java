package com.agent.entity.payroll.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaxCalculateReq {
    private BigDecimal grossSalary;
    private BigDecimal socialSecurityPersonal;
    private BigDecimal housingFundPersonal;
}