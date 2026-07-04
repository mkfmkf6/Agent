package com.agent.entity.employees.vo;

import lombok.Data;

import java.util.List;

@Data
public class DepartmentMembersVo {
    private String department;
    private Integer memberCount;
    private List<EmployeeInfoVo> members;
}