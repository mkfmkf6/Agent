package com.agent.service;

import com.agent.entity.attendance.vo.AttendanceQueryReq;
import com.agent.entity.attendance.vo.AttendanceResultVo;
import com.agent.result.Result;

import java.util.List;

public interface AttendanceService {
    Result<List<AttendanceResultVo>> queryAttendance(AttendanceQueryReq req);

    Result<List<AttendanceResultVo>> queryOvertime(Integer empId);
}