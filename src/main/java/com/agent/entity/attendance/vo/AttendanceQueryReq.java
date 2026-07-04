package com.agent.entity.attendance.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceQueryReq {
    private Integer empId;
    private LocalDate startDate;
    private LocalDate endDate;
}