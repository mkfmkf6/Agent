package com.agent.entity.attendance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceResultVo {
    private Integer attendanceId;
    private Integer empId;
    private LocalDate attendanceDate;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private String attendanceStatus;
    private BigDecimal overtimeHours;
    private String remark;
}