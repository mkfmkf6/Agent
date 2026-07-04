package com.agent.controller;

import com.agent.entity.recruitment.vo.QueryInterviewVo;
import com.agent.entity.recruitment.vo.QueryRecruitStatusVo;
import com.agent.entity.recruitment.vo.SaveCandidateReq;
import com.agent.entity.recruitment.vo.ScheduleInterviewReq;
import com.agent.result.Result;
import com.agent.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruitment")
public class RecruitmentController {
    @Autowired
    private RecruitmentService recruitmentService;

    @GetMapping("/queryRecruitStatus")
    public Result<QueryRecruitStatusVo> queryRecruitStatus(@RequestParam Integer recruitId){
        return recruitmentService.queryRecruitStatus(recruitId);
    }

    @GetMapping("/queryInterview")
    public Result<QueryInterviewVo> queryInterview(@RequestParam Integer recruitId){
        return recruitmentService.queryInterview(recruitId);
    }

    @PostMapping("/saveCandidate")
    public Result<String> saveCandidate(@RequestBody SaveCandidateReq req){
        return recruitmentService.saveCandidate(req);
    }

    @PostMapping("/scheduleInterview")
    public Result<String> scheduleInterview(@RequestBody ScheduleInterviewReq req){
        return recruitmentService.scheduleInterview(req);
    }

    @GetMapping("/queryRecruitStatusByName")
    public Result<List<QueryRecruitStatusVo>> queryRecruitStatusByName(@RequestParam String candidateName){
        return recruitmentService.queryRecruitStatusByName(candidateName);
    }

    @GetMapping("/queryInterviewByName")
    public Result<List<QueryInterviewVo>> queryInterviewByName(@RequestParam String candidateName){
        return recruitmentService.queryInterviewByName(candidateName);
    }

    @PostMapping("/updateRecruitStatus")
    public Result<String> updateRecruitStatus(@RequestParam Integer recruitId, @RequestParam Integer status){
        return recruitmentService.updateRecruitStatus(recruitId, status);
    }

    @PostMapping("/updateRecruitStatusByName")
    public Result<String> updateRecruitStatusByName(@RequestParam String candidateName, @RequestParam Integer status){
        return recruitmentService.updateRecruitStatusByName(candidateName, status);
    }
}
