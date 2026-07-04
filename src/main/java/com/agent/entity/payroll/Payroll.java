package com.agent.entity.payroll;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 薪酬表
 * </p>
 *
 * @author author
 * @since 2026-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("payroll")
public class Payroll implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 薪资记录ID
     */
    @TableId(value = "payroll_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer payrollId;

    /**
     * 员工ID
     */
    private Integer empId;

    /**
     * 薪资月份 YYYY-MM
     */
    private String salaryMonth;

    /**
     * 基本工资
     */
    private BigDecimal baseSalary;

    /**
     * 绩效工资
     */
    private BigDecimal performanceSalary;

    /**
     * 岗位补贴
     */
    private BigDecimal postAllowance;

    /**
     * 交通补贴
     */
    private BigDecimal transportAllowance;

    /**
     * 餐补
     */
    private BigDecimal mealAllowance;

    /**
     * 其他补贴
     */
    private BigDecimal otherAllowance;

    /**
     * 应发工资
     */
    private BigDecimal grossSalary;

    /**
     * 社保个人部分
     */
    private BigDecimal socialSecurityPersonal;

    /**
     * 公积金个人部分
     */
    private BigDecimal housingFundPersonal;

    /**
     * 个人所得税
     */
    private BigDecimal personalIncomeTax;

    /**
     * 其他扣款
     */
    private BigDecimal otherDeduction;

    /**
     * 实发工资
     */
    private BigDecimal netSalary;

    /**
     * 发放状态：1-待发放 2-已发放
     */
    private Integer payStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
