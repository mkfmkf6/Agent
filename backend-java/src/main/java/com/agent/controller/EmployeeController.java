package com.agent.controller;

import com.agent.entity.employees.Employees;
import com.agent.entity.employees.vo.DepartmentMembersVo;
import com.agent.entity.employees.vo.EmployeeInfoVo;
import com.agent.entity.employees.vo.EmployeeOffboardingReq;
import com.agent.entity.employees.vo.EmployeeOnboardingReq;
import com.agent.entity.employees.vo.UpdateEmployeeReq;
import com.agent.result.Result;
import com.agent.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/onboarding")
    public Result<Employees> createOnboarding(@RequestBody EmployeeOnboardingReq req) {
        return employeeService.createOnboarding(req);
    }

    @PostMapping("/onboarding/materials")
    public Result<String> generateOnboardingMaterials(@RequestParam Integer empId) {
        return employeeService.generateOnboardingMaterials(empId);
    }

    @PostMapping("/onboarding/openAccounts")
    public Result<String> openAccounts(@RequestParam Integer empId) {
        return employeeService.openAccounts(empId);
    }

    @PostMapping("/offboarding")
    public Result<Employees> createOffboarding(@RequestBody EmployeeOffboardingReq req) {
        return employeeService.createOffboarding(req);
    }

    @PostMapping("/offboardingByName")
    public Result<Employees> createOffboardingByName(@RequestParam String name, @RequestParam Integer resignType) {
        return employeeService.createOffboardingByName(name, resignType);
    }

    @PostMapping("/offboarding/generateList")
    public Result<String> generateOffboardingList(@RequestParam Integer empId) {
        return employeeService.generateOffboardingList(empId);
    }

    @PostMapping("/offboarding/confirmHandover")
    public Result<String> confirmHandover(@RequestParam Integer empId) {
        return employeeService.confirmHandover(empId);
    }

    @GetMapping("/info")
    public Result<EmployeeInfoVo> getEmployeeInfo(@RequestParam Integer empId) {
        return employeeService.getEmployeeInfo(empId);
    }

    @GetMapping("/searchByName")
    public Result<Employees> searchByName(@RequestParam String name) {
        return employeeService.searchByName(name);
    }

    @PostMapping("/update")
    public Result<String> updateEmployee(@RequestBody UpdateEmployeeReq req) {
        return employeeService.updateEmployee(req);
    }

    @GetMapping("/departments")
    public Result<List<String>> getAllDepartments() {
        return employeeService.getAllDepartments();
    }

    @GetMapping("/departmentMembers")
    public Result<DepartmentMembersVo> getDepartmentMembers(@RequestParam String department) {
        return employeeService.getDepartmentMembers(department);
    }
}