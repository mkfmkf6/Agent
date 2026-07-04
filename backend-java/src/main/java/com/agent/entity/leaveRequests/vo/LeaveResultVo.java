package com.agent.entity.leaveRequests.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LeaveResultVo {
    private Integer leaveId;
    private Integer empId;
    private String leaveType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal leaveDays;
    private String applyStatus;
    private LocalDateTime applyTime;
    private String remark;
}