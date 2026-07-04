package com.agent.service.impl;

import com.agent.entity.attendance.Attendance;
import com.agent.entity.employees.Employees;
import com.agent.entity.leaveRequests.LeaveRequests;
import com.agent.mapper.AttendanceMapper;
import com.agent.mapper.EmployeeMapper;
import com.agent.mapper.LeaveRequestsMapper;
import com.agent.result.Result;
import com.agent.service.DashboardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private LeaveRequestsMapper leaveRequestsMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        LambdaQueryWrapper<Employees> empWrapper = new LambdaQueryWrapper<>();
        empWrapper.eq(Employees::getEmpStatus, 2);
        stats.put("totalEmployees", employeeMapper.selectCount(empWrapper));

        YearMonth currentMonth = YearMonth.now();
        LocalDate firstDay = currentMonth.atDay(1);
        LocalDate lastDay = currentMonth.atEndOfMonth();

        LambdaQueryWrapper<Employees> hireWrapper = new LambdaQueryWrapper<>();
        hireWrapper.ge(Employees::getHireDate, firstDay)
               .le(Employees::getHireDate, lastDay);
        stats.put("newHiresThisMonth", employeeMapper.selectCount(hireWrapper));

        LambdaQueryWrapper<Employees> resignWrapper = new LambdaQueryWrapper<>();
        resignWrapper.ge(Employees::getResignDate, firstDay)
                   .le(Employees::getResignDate, lastDay);
        stats.put("leaversThisMonth", employeeMapper.selectCount(resignWrapper));

        LambdaQueryWrapper<LeaveRequests> leaveWrapper = new LambdaQueryWrapper<>();
        leaveWrapper.eq(LeaveRequests::getApplyStatus, 0);
        stats.put("pendingLeaveRequests", leaveRequestsMapper.selectCount(leaveWrapper));

        LambdaQueryWrapper<Attendance> overtimeWrapper = new LambdaQueryWrapper<>();
        overtimeWrapper.ge(Attendance::getAttendanceDate, firstDay)
                      .le(Attendance::getAttendanceDate, lastDay)
                      .isNotNull(Attendance::getOvertimeHours);
        List<Attendance> overtimeRecords = attendanceMapper.selectList(overtimeWrapper);
        double totalOvertime = overtimeRecords.stream()
                .mapToDouble(a -> a.getOvertimeHours() != null ? a.getOvertimeHours().doubleValue() : 0)
                .sum();
        stats.put("overtimeHours", totalOvertime);

        LambdaQueryWrapper<Attendance> attendanceWrapper = new LambdaQueryWrapper<>();
        attendanceWrapper.ge(Attendance::getAttendanceDate, firstDay)
                         .le(Attendance::getAttendanceDate, lastDay);
        List<Attendance> attendanceRecords = attendanceMapper.selectList(attendanceWrapper);
        if (!attendanceRecords.isEmpty()) {
            long normalCount = attendanceRecords.stream()
                    .filter(a -> a.getAttendanceStatus() != null && a.getAttendanceStatus() == 1)
                    .count();
            double rate = (double) normalCount / attendanceRecords.size() * 100;
            stats.put("avgAttendanceRate", Math.round(rate * 10) / 10.0);
        } else {
            stats.put("avgAttendanceRate", 0);
        }

        return Result.success(stats);
    }

    @Override
    public Result<Map<String, Object>> getChartsData() {
        Map<String, Object> chartsData = new HashMap<>();

        List<Double> attendanceRateData = new ArrayList<>();
        List<String> monthLabels = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            monthLabels.add(month.getMonthValue() + "月");

            LocalDate firstDay = month.atDay(1);
            LocalDate lastDay = month.atEndOfMonth();

            LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(Attendance::getAttendanceDate, firstDay)
                   .le(Attendance::getAttendanceDate, lastDay);
            List<Attendance> records = attendanceMapper.selectList(wrapper);

            if (!records.isEmpty()) {
                long normalCount = records.stream()
                        .filter(a -> a.getAttendanceStatus() != null && a.getAttendanceStatus() == 1)
                        .count();
                attendanceRateData.add(Math.round((double) normalCount / records.size() * 1000) / 10.0);
            } else {
                attendanceRateData.add(0.0);
            }
        }
        chartsData.put("attendanceRate", attendanceRateData);
        chartsData.put("monthLabels", monthLabels);

        LambdaQueryWrapper<Employees> empWrapper = new LambdaQueryWrapper<>();
        empWrapper.eq(Employees::getEmpStatus, 2);
        List<Employees> employees = employeeMapper.selectList(empWrapper);
        Map<String, Long> deptCount = employees.stream()
                .filter(e -> e.getDepartment() != null && !e.getDepartment().isEmpty())
                .collect(Collectors.groupingBy(Employees::getDepartment, Collectors.counting()));

        List<Map<String, Object>> employeePieData = new ArrayList<>();
        deptCount.forEach((dept, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", dept);
            item.put("value", count);
            employeePieData.add(item);
        });
        chartsData.put("employeePieData", employeePieData);

        Map<Integer, Integer> leaveTypeCount = new HashMap<>();
        for (int i = 1; i <= 6; i++) {
            leaveTypeCount.put(i, 0);
        }
        LambdaQueryWrapper<LeaveRequests> leaveWrapper = new LambdaQueryWrapper<>();
        leaveWrapper.ge(LeaveRequests::getApplyTime, YearMonth.now().minusMonths(5).atDay(1));
        List<LeaveRequests> leaveRequests = leaveRequestsMapper.selectList(leaveWrapper);
        leaveRequests.forEach(req -> {
            Integer type = req.getLeaveType();
            if (type != null && leaveTypeCount.containsKey(type)) {
                leaveTypeCount.put(type, leaveTypeCount.get(type) + 1);
            }
        });

        List<String> leaveTypeLabels = Arrays.asList("年假", "事假", "病假", "婚假", "产假", "丧假");
        List<Integer> leaveTypeData = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            leaveTypeData.add(leaveTypeCount.get(i));
        }
        chartsData.put("leaveTypeLabels", leaveTypeLabels);
        chartsData.put("leaveTypeData", leaveTypeData);

        List<Integer> weeklyOvertime = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LambdaQueryWrapper<Attendance> overtimeWrapper = new LambdaQueryWrapper<>();
            overtimeWrapper.eq(Attendance::getAttendanceDate, date)
                          .isNotNull(Attendance::getOvertimeHours);
            List<Attendance> records = attendanceMapper.selectList(overtimeWrapper);
            double total = records.stream()
                    .mapToDouble(a -> a.getOvertimeHours() != null ? a.getOvertimeHours().doubleValue() : 0)
                    .sum();
            weeklyOvertime.add((int) total);
        }
        chartsData.put("weeklyOvertime", weeklyOvertime);

        return Result.success(chartsData);
    }
}