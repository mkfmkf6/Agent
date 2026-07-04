package com.agent.controller;

import com.agent.entity.leaveRequests.vo.LeaveApplyReq;
import com.agent.entity.leaveRequests.vo.LeaveResultVo;
import com.agent.result.Result;
import com.agent.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/apply")
    public Result<LeaveResultVo> applyLeave(@RequestBody LeaveApplyReq req) {
        return leaveService.applyLeave(req);
    }

    @GetMapping("/query")
    public Result<List<LeaveResultVo>> queryLeaveByEmpId(@RequestParam Integer empId) {
        return leaveService.queryLeaveByEmpId(empId);
    }

    @GetMapping("/calculateRemaining")
    public Result<String> calculateRemainingLeave(@RequestParam Integer empId) {
        return leaveService.calculateRemainingLeave(empId);
    }
}