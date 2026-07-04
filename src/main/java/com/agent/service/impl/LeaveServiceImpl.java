package com.agent.service.impl;

import com.agent.entity.employees.Employees;
import com.agent.entity.leaveRequests.LeaveRequests;
import com.agent.entity.leaveRequests.vo.LeaveApplyReq;
import com.agent.entity.leaveRequests.vo.LeaveResultVo;
import com.agent.mapper.EmployeeMapper;
import com.agent.mapper.LeaveRequestsMapper;
import com.agent.result.Result;
import com.agent.service.LeaveService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveRequestsMapper leaveRequestsMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    private String getLeaveTypeName(Integer type) {
        switch (type) {
            case 1: return "年假";
            case 2: return "事假";
            case 3: return "病假";
            case 4: return "婚假";
            case 5: return "产假";
            case 6: return "丧假";
            default: return "其他";
        }
    }

    private String getStatusName(Integer status) {
        switch (status) {
            case 1: return "待审批";
            case 2: return "已通过";
            case 3: return "已驳回";
            default: return "未知";
        }
    }

    private BigDecimal calculateLeaveDays(LocalDateTime startTime, LocalDateTime endTime) {
        long hours = Duration.between(startTime, endTime).toHours();
        BigDecimal days = BigDecimal.valueOf(hours / 24.0).setScale(1, BigDecimal.ROUND_HALF_UP);
        return days.compareTo(BigDecimal.ZERO) > 0 ? days : BigDecimal.ZERO;
    }

    @Override
    public Result<LeaveResultVo> applyLeave(LeaveApplyReq req) {
        Employees employee = employeeMapper.selectById(req.getEmpId());
        if (employee == null) {
            return Result.error("员工不存在");
        }

        if (req.getLeaveType() == 1) {
            BigDecimal usedDays = leaveRequestsMapper.selectUsedLeaveDays(req.getEmpId(), 1);
            if (usedDays == null) usedDays = BigDecimal.ZERO;
            BigDecimal remainingDays = employee.getAnnualLeaveQuota().subtract(usedDays);

            BigDecimal requestDays = calculateLeaveDays(req.getStartTime(), req.getEndTime());

            if (remainingDays.compareTo(requestDays) < 0) {
                return Result.error(String.format("剩余年假不足！剩余%.1f天，申请%.1f天", remainingDays, requestDays));
            }
        }

        LeaveRequests leaveRequest = new LeaveRequests();
        BeanUtils.copyProperties(req, leaveRequest);

        BigDecimal leaveDays = calculateLeaveDays(req.getStartTime(), req.getEndTime());
        leaveRequest.setLeaveDays(leaveDays);
        leaveRequest.setApplyStatus(1);
        leaveRequest.setApplyTime(LocalDateTime.now());

        leaveRequestsMapper.insert(leaveRequest);

        LeaveResultVo vo = new LeaveResultVo();
        BeanUtils.copyProperties(leaveRequest, vo);
        vo.setLeaveType(getLeaveTypeName(leaveRequest.getLeaveType()));
        vo.setApplyStatus(getStatusName(leaveRequest.getApplyStatus()));

        return Result.success(vo);
    }

    @Override
    public Result<List<LeaveResultVo>> queryLeaveByEmpId(Integer empId) {
        List<LeaveRequests> leaveList = leaveRequestsMapper.selectByEmpIdAndStatus(empId, 2);

        List<LeaveResultVo> resultList = new ArrayList<>();
        for (LeaveRequests leave : leaveList) {
            LeaveResultVo vo = new LeaveResultVo();
            BeanUtils.copyProperties(leave, vo);
            vo.setLeaveType(getLeaveTypeName(leave.getLeaveType()));
            vo.setApplyStatus(getStatusName(leave.getApplyStatus()));
            resultList.add(vo);
        }
        return Result.success(resultList);
    }

    @Override
    public Result<String> calculateRemainingLeave(Integer empId) {
        Employees employee = employeeMapper.selectById(empId);
        if (employee == null) {
            return Result.error("员工不存在");
        }

        BigDecimal totalQuota = employee.getAnnualLeaveQuota();
        BigDecimal usedDays = leaveRequestsMapper.selectUsedLeaveDays(empId, 1);
        if (usedDays == null) usedDays = BigDecimal.ZERO;
        BigDecimal remainingDays = totalQuota.subtract(usedDays);

        return Result.success(String.format("员工[%s]年假剩余情况：\n总天数：%.1f天\n已休天数：%.1f天\n剩余天数：%.1f天",
                employee.getName(), totalQuota, usedDays, remainingDays));
    }
}