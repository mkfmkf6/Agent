package com.agent.service.impl;

import com.agent.entity.attendance.Attendance;
import com.agent.entity.attendance.vo.AttendanceQueryReq;
import com.agent.entity.attendance.vo.AttendanceResultVo;
import com.agent.mapper.AttendanceMapper;
import com.agent.result.Result;
import com.agent.service.AttendanceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    private String getStatusName(Integer status) {
        switch (status) {
            case 1: return "正常";
            case 2: return "迟到";
            case 3: return "早退";
            case 4: return "缺勤";
            case 5: return "请假";
            case 6: return "加班";
            default: return "未知";
        }
    }

    @Override
    public Result<List<AttendanceResultVo>> queryAttendance(AttendanceQueryReq req) {
        List<Attendance> attendanceList = attendanceMapper.selectByEmpIdAndDateRange(
                req.getEmpId(), req.getStartDate(), req.getEndDate());

        List<AttendanceResultVo> resultList = new ArrayList<>();
        for (Attendance attendance : attendanceList) {
            AttendanceResultVo vo = new AttendanceResultVo();
            BeanUtils.copyProperties(attendance, vo);
            vo.setAttendanceStatus(getStatusName(attendance.getAttendanceStatus()));
            resultList.add(vo);
        }
        return Result.success(resultList);
    }

    @Override
    public Result<List<AttendanceResultVo>> queryOvertime(Integer empId) {
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getEmpId, empId)
               .eq(Attendance::getAttendanceStatus, 6);

        List<Attendance> attendanceList = attendanceMapper.selectList(wrapper);

        List<AttendanceResultVo> resultList = new ArrayList<>();
        for (Attendance attendance : attendanceList) {
            AttendanceResultVo vo = new AttendanceResultVo();
            BeanUtils.copyProperties(attendance, vo);
            vo.setAttendanceStatus(getStatusName(attendance.getAttendanceStatus()));
            resultList.add(vo);
        }
        return Result.success(resultList);
    }
}