package com.agent.entity.leaveRequests.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveApplyReq {
    private Integer empId;
    private Integer leaveType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String remark;
}