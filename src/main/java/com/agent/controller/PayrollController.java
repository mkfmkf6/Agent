package com.agent.controller;

import com.agent.entity.payroll.vo.PayrollQueryReq;
import com.agent.entity.payroll.vo.PayrollResultVo;
import com.agent.entity.payroll.vo.TaxCalculateReq;
import com.agent.result.Result;
import com.agent.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payroll")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @GetMapping("/query")
    public Result<PayrollResultVo> queryPayrollDetail(
            @RequestParam Integer empId,
            @RequestParam String salaryMonth) {
        PayrollQueryReq req = new PayrollQueryReq();
        req.setEmpId(empId);
        req.setSalaryMonth(salaryMonth);
        return payrollService.queryPayrollDetail(req);
    }

    @PostMapping("/calculateTax")
    public Result<String> calculatePersonalIncomeTax(@RequestBody TaxCalculateReq req) {
        return payrollService.calculatePersonalIncomeTax(req);
    }

    @GetMapping("/calculateTaxSimple")
    public Result<String> calculatePersonalIncomeTaxSimple(@RequestParam BigDecimal grossSalary) {
        return payrollService.calculatePersonalIncomeTaxSimple(grossSalary);
    }
}