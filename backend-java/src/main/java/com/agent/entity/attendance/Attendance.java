package com.agent.entity.attendance;

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
 * 考勤表
 * </p>
 *
 * @author author
 * @since 2026-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("attendance")
public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 考勤记录ID
     */
    @TableId(value = "attendance_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer attendanceId;

    /**
     * 员工ID
     */
    private Integer empId;

    /**
     * 考勤日期
     */
    private LocalDate attendanceDate;

    /**
     * 上班打卡时间
     */
    private LocalDateTime clockInTime;

    /**
     * 下班打卡时间
     */
    private LocalDateTime clockOutTime;

    /**
     * 考勤状态：1-正常 2-迟到 3-早退 4-缺勤 5-请假 6-加班
     */
    private Integer attendanceStatus;

    /**
     * 加班时长（小时）
     */
    private BigDecimal overtimeHours;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
