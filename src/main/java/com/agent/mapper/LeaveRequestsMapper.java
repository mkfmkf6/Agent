package com.agent.mapper;

import com.agent.entity.leaveRequests.LeaveRequests;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LeaveRequestsMapper extends BaseMapper<LeaveRequests> {
    @Select("SELECT * FROM leave_requests WHERE emp_id = #{empId} AND apply_status = #{status}")
    List<LeaveRequests> selectByEmpIdAndStatus(@Param("empId") Integer empId, @Param("status") Integer status);

    @Select("SELECT SUM(leave_days) FROM leave_requests WHERE emp_id = #{empId} AND leave_type = #{leaveType} AND apply_status = 2")
    java.math.BigDecimal selectUsedLeaveDays(@Param("empId") Integer empId, @Param("leaveType") Integer leaveType);
}