package com.agent.service.impl;

import com.agent.entity.payroll.Payroll;
import com.agent.entity.payroll.vo.PayrollQueryReq;
import com.agent.entity.payroll.vo.PayrollResultVo;
import com.agent.entity.payroll.vo.TaxCalculateReq;
import com.agent.mapper.PayrollMapper;
import com.agent.result.Result;
import com.agent.service.PayrollService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PayrollServiceImpl implements PayrollService {

    private static final BigDecimal TAX_THRESHOLD = new BigDecimal("5000");

    @Autowired
    private PayrollMapper payrollMapper;

    private String getPayStatusName(Integer status) {
        return status == 1 ? "待发放" : (status == 2 ? "已发放" : "未知");
    }

    private BigDecimal calculateTax(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal annualIncome = taxableIncome.multiply(new BigDecimal("12"));
        BigDecimal annualTax = BigDecimal.ZERO;

        if (annualIncome.compareTo(new BigDecimal("36000")) <= 0) {
            annualTax = annualIncome.multiply(new BigDecimal("0.03"));
        } else if (annualIncome.compareTo(new BigDecimal("144000")) <= 0) {
            annualTax = annualIncome.multiply(new BigDecimal("0.10")).subtract(new BigDecimal("2520"));
        } else if (annualIncome.compareTo(new BigDecimal("300000")) <= 0) {
            annualTax = annualIncome.multiply(new BigDecimal("0.20")).subtract(new BigDecimal("16920"));
        } else if (annualIncome.compareTo(new BigDecimal("420000")) <= 0) {
            annualTax = annualIncome.multiply(new BigDecimal("0.25")).subtract(new BigDecimal("31920"));
        } else if (annualIncome.compareTo(new BigDecimal("660000")) <= 0) {
            annualTax = annualIncome.multiply(new BigDecimal("0.30")).subtract(new BigDecimal("52920"));
        } else if (annualIncome.compareTo(new BigDecimal("960000")) <= 0) {
            annualTax = annualIncome.multiply(new BigDecimal("0.35")).subtract(new BigDecimal("85920"));
        } else {
            annualTax = annualIncome.multiply(new BigDecimal("0.45")).subtract(new BigDecimal("181920"));
        }

        return annualTax.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
    }

    @Override
    public Result<PayrollResultVo> queryPayrollDetail(PayrollQueryReq req) {
        Payroll payroll = payrollMapper.selectByEmpIdAndMonth(req.getEmpId(), req.getSalaryMonth());
        if (payroll == null) {
            return Result.error("该月份没有薪资记录");
        }

        PayrollResultVo vo = new PayrollResultVo();
        BeanUtils.copyProperties(payroll, vo);
        vo.setPayStatus(getPayStatusName(payroll.getPayStatus()));

        return Result.success(vo);
    }

    @Override
    public Result<String> calculatePersonalIncomeTax(TaxCalculateReq req) {
        BigDecimal grossSalary = req.getGrossSalary();
        BigDecimal socialSecurity = req.getSocialSecurityPersonal() != null ? req.getSocialSecurityPersonal() : BigDecimal.ZERO;
        BigDecimal housingFund = req.getHousingFundPersonal() != null ? req.getHousingFundPersonal() : BigDecimal.ZERO;

        BigDecimal taxableIncome = grossSalary.subtract(socialSecurity).subtract(housingFund).subtract(TAX_THRESHOLD);
        BigDecimal tax = calculateTax(taxableIncome);
        BigDecimal netSalary = grossSalary.subtract(socialSecurity).subtract(housingFund).subtract(tax);

        StringBuilder result = new StringBuilder();
        result.append("个人所得税计算结果：\n");
        result.append(String.format("应发工资：%.2f元\n", grossSalary));
        result.append(String.format("社保个人部分：%.2f元\n", socialSecurity));
        result.append(String.format("公积金个人部分：%.2f元\n", housingFund));
        result.append(String.format("起征点：%.2f元\n", TAX_THRESHOLD));
        result.append(String.format("应纳税所得额：%.2f元\n", taxableIncome.compareTo(BigDecimal.ZERO) > 0 ? taxableIncome : BigDecimal.ZERO));
        result.append(String.format("个人所得税：%.2f元\n", tax));
        result.append(String.format("实发工资：%.2f元\n", netSalary));

        return Result.success(result.toString());
    }

    @Override
    public Result<String> calculatePersonalIncomeTaxSimple(BigDecimal grossSalary) {
        BigDecimal taxableIncome = grossSalary.subtract(TAX_THRESHOLD);
        BigDecimal tax = calculateTax(taxableIncome);
        BigDecimal netSalary = grossSalary.subtract(tax);

        StringBuilder result = new StringBuilder();
        result.append("个人所得税计算结果（简化版）：\n");
        result.append(String.format("应发工资：%.2f元\n", grossSalary));
        result.append(String.format("起征点：%.2f元\n", TAX_THRESHOLD));
        result.append(String.format("应纳税所得额：%.2f元\n", taxableIncome.compareTo(BigDecimal.ZERO) > 0 ? taxableIncome : BigDecimal.ZERO));
        result.append(String.format("个人所得税：%.2f元\n", tax));
        result.append(String.format("实发工资：%.2f元\n", netSalary));
        result.append("\n税率表（年度累计）：\n");
        result.append("0-36000元：3%\n");
        result.append("36000-144000元：10%（速算扣除数2520）\n");
        result.append("144000-300000元：20%（速算扣除数16920）\n");
        result.append("300000-420000元：25%（速算扣除数31920）\n");
        result.append("420000-660000元：30%（速算扣除数52920）\n");
        result.append("660000-960000元：35%（速算扣除数85920）\n");
        result.append("960000元以上：45%（速算扣除数181920）\n");

        return Result.success(result.toString());
    }
}