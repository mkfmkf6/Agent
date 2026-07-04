package com.agent.mapper;

import com.agent.entity.payroll.Payroll;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PayrollMapper extends BaseMapper<Payroll> {
    @Select("SELECT * FROM payroll WHERE emp_id = #{empId} AND salary_month = #{salaryMonth}")
    Payroll selectByEmpIdAndMonth(@Param("empId") Integer empId, @Param("salaryMonth") String salaryMonth);
}