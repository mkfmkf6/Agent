package com.agent.service;

import com.agent.entity.leaveRequests.vo.LeaveApplyReq;
import com.agent.entity.leaveRequests.vo.LeaveResultVo;
import com.agent.result.Result;

import java.math.BigDecimal;
import java.util.List;

public interface LeaveService {
    Result<LeaveResultVo> applyLeave(LeaveApplyReq req);

    Result<List<LeaveResultVo>> queryLeaveByEmpId(Integer empId);

    Result<String> calculateRemainingLeave(Integer empId);
}