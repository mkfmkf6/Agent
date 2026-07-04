package com.agent.controller;

import com.agent.entity.attendance.vo.AttendanceQueryReq;
import com.agent.entity.attendance.vo.AttendanceResultVo;
import com.agent.result.Result;
import com.agent.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/query")
    public Result<List<AttendanceResultVo>> queryAttendance(
            @RequestParam Integer empId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AttendanceQueryReq req = new AttendanceQueryReq();
        req.setEmpId(empId);
        req.setStartDate(startDate);
        req.setEndDate(endDate);
        return attendanceService.queryAttendance(req);
    }

    @GetMapping("/queryOvertime")
    public Result<List<AttendanceResultVo>> queryOvertime(@RequestParam Integer empId) {
        return attendanceService.queryOvertime(empId);
    }
}