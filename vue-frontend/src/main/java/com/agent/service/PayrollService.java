package com.agent.service;

import com.agent.entity.payroll.vo.PayrollQueryReq;
import com.agent.entity.payroll.vo.PayrollResultVo;
import com.agent.entity.payroll.vo.TaxCalculateReq;
import com.agent.result.Result;

import java.math.BigDecimal;

public interface PayrollService {
    Result<PayrollResultVo> queryPayrollDetail(PayrollQueryReq req);

    Result<String> calculatePersonalIncomeTax(TaxCalculateReq req);

    Result<String> calculatePersonalIncomeTaxSimple(BigDecimal grossSalary);
}