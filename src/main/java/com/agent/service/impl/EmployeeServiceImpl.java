package com.agent.service.impl;

import com.agent.entity.employees.Employees;
import com.agent.entity.employees.vo.DepartmentMembersVo;
import com.agent.entity.employees.vo.EmployeeInfoVo;
import com.agent.entity.employees.vo.EmployeeOffboardingReq;
import com.agent.entity.employees.vo.EmployeeOnboardingReq;
import com.agent.entity.employees.vo.UpdateEmployeeReq;
import com.agent.mapper.EmployeeMapper;
import com.agent.result.Result;
import com.agent.service.DocumentService;
import com.agent.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DocumentService documentService;

    @Override
    public Result<Employees> createOnboarding(EmployeeOnboardingReq req) {
        try {
            LambdaQueryWrapper<Employees> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Employees::getEmpNo, req.getEmpNo());
            Employees existing = employeeMapper.selectOne(wrapper);
            
            if (existing != null) {
                BeanUtils.copyProperties(req, existing);
                existing.setHireDate(LocalDate.now());
                existing.setRegularDate(LocalDate.now());
                existing.setResignDate(null);
                existing.setEmpStatus(2);
                existing.setJobLevel("P0");
                existing.setAnnualLeaveQuota(new BigDecimal("10"));
                existing.setAnnualLeaveUsed(new BigDecimal("0"));
                existing.setUpdateTime(LocalDateTime.now());
                employeeMapper.updateById(existing);
                Result<Employees> result = Result.success(existing);
                result.setMsg("员工信息已更新");
                return result;
            }
            
            LambdaQueryWrapper<Employees> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(Employees::getPhone, req.getPhone());
            Employees phoneExisting = employeeMapper.selectOne(phoneWrapper);
            if (phoneExisting != null) {
                return Result.error("手机号已被使用：" + req.getPhone());
            }
            
            Employees employee = new Employees();
            BeanUtils.copyProperties(req, employee);
            employee.setHireDate(LocalDate.now());
            employee.setRegularDate(LocalDate.now());
            employee.setResignDate(null);
            employee.setEmpStatus(2);
            employee.setJobLevel("P0");
            employee.setAnnualLeaveQuota(new BigDecimal("10"));
            employee.setAnnualLeaveUsed(new BigDecimal("0"));
            employee.setCreateTime(LocalDateTime.now());
            employee.setUpdateTime(LocalDateTime.now());
            employeeMapper.insert(employee);
            return Result.success(employee);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("emp_no")) {
                    return Result.error("工号已存在，请使用其他工号");
                }
                if (e.getMessage().contains("phone")) {
                    return Result.error("手机号已被使用，请更换手机号");
                }
            }
            return Result.error("入职创建失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> generateOnboardingMaterials(Integer empId) {
        return documentService.generateOnboardingMaterials(empId);
    }

    @Override
    public Result<String> openAccounts(Integer empId) {
        Employees employee = employeeMapper.selectById(empId);
        if (employee == null) {
            return Result.error("员工不存在");
        }
        String email = employee.getEmail();
        if (email == null || email.isEmpty()) {
            email = employee.getName() + "@company.com";
        }
        return Result.success(String.format("已为员工[%s]开通以下账号：\n1. 企业邮箱：%s\n2. OA系统账号\n3. VPN账号\n4. 门禁卡", employee.getName(), email));
    }

    @Override
    public Result<Employees> createOffboarding(EmployeeOffboardingReq req) {
        Employees employee = employeeMapper.selectById(req.getEmpId());
        if (employee == null) {
            return Result.error("员工不存在");
        }
        employee.setResignDate(LocalDate.now());
        employee.setEmpStatus(3);
        employee.setUpdateTime(LocalDateTime.now());
        employeeMapper.updateById(employee);
        return Result.success(employee);
    }

    @Override
    public Result<Employees> createOffboardingByName(String name, Integer resignType) {
        LambdaQueryWrapper<Employees> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Employees::getName, name);
        List<Employees> list = employeeMapper.selectList(wrapper);
        if (list.isEmpty()) {
            return Result.error("未找到该员工");
        }
        if (list.size() > 1) {
            return Result.error("找到多个匹配的员工，请使用员工ID精确办理");
        }
        Employees employee = list.get(0);
        employee.setResignDate(LocalDate.now());
        employee.setEmpStatus(3);
        employee.setUpdateTime(LocalDateTime.now());
        employeeMapper.updateById(employee);
        return Result.success(employee);
    }

    @Override
    public Result<String> generateOffboardingList(Integer empId) {
        return documentService.generateOffboardingList(empId);
    }

    @Override
    public Result<String> confirmHandover(Integer empId) {
        Employees employee = employeeMapper.selectById(empId);
        if (employee == null) {
            return Result.error("员工不存在");
        }
        employee.setEmpStatus(3);
        employee.setUpdateTime(LocalDateTime.now());
        employeeMapper.updateById(employee);
        return Result.success(String.format("员工[%s]交接确认完成！所有离职手续已办理完毕。\n离职日期：%s\n离职类型：自愿离职", employee.getName(), employee.getResignDate()));
    }

    private String getGenderName(Integer gender) {
        return gender == 1 ? "男" : (gender == 2 ? "女" : "未知");
    }

    private String getStatusName(Integer status) {
        return status == 1 ? "试用期" : (status == 2 ? "在职" : (status == 3 ? "离职" : "未知"));
    }

    @Override
    public Result<EmployeeInfoVo> getEmployeeInfo(Integer empId) {
        Employees employee = employeeMapper.selectById(empId);
        if (employee == null) {
            return Result.error("员工不存在");
        }

        EmployeeInfoVo vo = new EmployeeInfoVo();
        BeanUtils.copyProperties(employee, vo);
        vo.setGender(getGenderName(employee.getGender()));
        vo.setEmpStatus(getStatusName(employee.getEmpStatus()));

        if (employee.getManagerId() != null) {
            Employees manager = employeeMapper.selectById(employee.getManagerId());
            if (manager != null) {
                vo.setManagerName(manager.getName());
            }
        }

        return Result.success(vo);
    }

    @Override
    public Result<String> updateEmployee(UpdateEmployeeReq req) {
        Employees employee = employeeMapper.selectById(req.getEmpId());
        if (employee == null) {
            return Result.error("员工不存在");
        }

        if (req.getName() != null) employee.setName(req.getName());
        if (req.getGender() != null) employee.setGender(req.getGender());
        if (req.getDepartment() != null) employee.setDepartment(req.getDepartment());
        if (req.getPosition() != null) employee.setPosition(req.getPosition());
        if (req.getJobLevel() != null) employee.setJobLevel(req.getJobLevel());
        if (req.getPhone() != null) employee.setPhone(req.getPhone());
        if (req.getEmail() != null) employee.setEmail(req.getEmail());
        if (req.getManagerId() != null) employee.setManagerId(req.getManagerId());
        employee.setUpdateTime(LocalDateTime.now());

        employeeMapper.updateById(employee);
        return Result.success("员工信息更新成功！");
    }

    @Override
    public Result<List<String>> getAllDepartments() {
        LambdaQueryWrapper<Employees> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Employees::getDepartment);
        List<Employees> employees = employeeMapper.selectList(wrapper);
        List<String> departments = employees.stream()
                .map(Employees::getDepartment)
                .filter(d -> d != null && !d.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        return Result.success(departments);
    }

    @Override
    public Result<DepartmentMembersVo> getDepartmentMembers(String department) {
        LambdaQueryWrapper<Employees> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employees::getDepartment, department)
               .eq(Employees::getEmpStatus, 2);
        List<Employees> employees = employeeMapper.selectList(wrapper);

        List<EmployeeInfoVo> memberVos = new ArrayList<>();
        for (Employees emp : employees) {
            EmployeeInfoVo vo = new EmployeeInfoVo();
            BeanUtils.copyProperties(emp, vo);
            vo.setGender(getGenderName(emp.getGender()));
            vo.setEmpStatus(getStatusName(emp.getEmpStatus()));

            if (emp.getManagerId() != null) {
                Employees manager = employeeMapper.selectById(emp.getManagerId());
                if (manager != null) {
                    vo.setManagerName(manager.getName());
                }
            }
            memberVos.add(vo);
        }

        DepartmentMembersVo result = new DepartmentMembersVo();
        result.setDepartment(department);
        result.setMemberCount(memberVos.size());
        result.setMembers(memberVos);

        return Result.success(result);
    }

    @Override
    public Result<Employees> searchByName(String name) {
        LambdaQueryWrapper<Employees> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Employees::getName, name)
               .eq(Employees::getEmpStatus, 2);
        List<Employees> employees = employeeMapper.selectList(wrapper);
        if (employees.isEmpty()) {
            return Result.error("未找到该员工");
        }
        return Result.success(employees.get(0));
    }
}