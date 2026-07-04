package com.agent.entity.employees;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 员工主表
 * </p>
 *
 * @author author
 * @since 2026-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("employees")
public class Employees implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    @TableId(value = "emp_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer empId;

    /**
     * 员工工号
     */
    private String empNo;

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 性别：1-男 2-女
     */
    private Integer gender;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 岗位名称
     */
    private String position;

    /**
     * 职级
     */
    private String jobLevel;

    /**
     * 入职日期
     */
    private LocalDate hireDate;

    /**
     * 转正日期
     */
    private LocalDate regularDate;

    /**
     * 离职日期
     */
    private LocalDate resignDate;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 企业邮箱
     */
    private String email;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 直属领导ID
     */
    private Integer managerId;

    /**
     * 员工状态：1-试用期 2-在职 3-离职
     */
    private Integer empStatus;

    /**
     * 年度年假总天数
     */
    private BigDecimal annualLeaveQuota;

    /**
     * 已休年假天数
     */
    private BigDecimal annualLeaveUsed;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
