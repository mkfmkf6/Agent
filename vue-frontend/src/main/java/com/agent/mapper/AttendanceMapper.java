package com.agent.mapper;

import com.agent.entity.attendance.Attendance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
    @Select("SELECT * FROM attendance WHERE emp_id = #{empId} AND attendance_date BETWEEN #{startDate} AND #{endDate}")
    List<Attendance> selectByEmpIdAndDateRange(@Param("empId") Integer empId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}