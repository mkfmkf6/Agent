package com.agent.service;

import com.agent.entity.employees.Employees;
import com.agent.entity.employees.vo.DepartmentMembersVo;
import com.agent.entity.employees.vo.EmployeeInfoVo;
import com.agent.entity.employees.vo.EmployeeOffboardingReq;
import com.agent.entity.employees.vo.EmployeeOnboardingReq;
import com.agent.entity.employees.vo.UpdateEmployeeReq;
import com.agent.result.Result;

import java.util.List;

public interface EmployeeService {
    Result<Employees> createOnboarding(EmployeeOnboardingReq req);

    Result<String> generateOnboardingMaterials(Integer empId);

    Result<String> openAccounts(Integer empId);

    Result<Employees> createOffboarding(EmployeeOffboardingReq req);

    Result<Employees> createOffboardingByName(String name, Integer resignType);

    Result<String> generateOffboardingList(Integer empId);

    Result<String> confirmHandover(Integer empId);

    Result<EmployeeInfoVo> getEmployeeInfo(Integer empId);

    Result<String> updateEmployee(UpdateEmployeeReq req);

    Result<List<String>> getAllDepartments();

    Result<DepartmentMembersVo> getDepartmentMembers(String department);

    Result<Employees> searchByName(String name);
}