package com.agent.service;

import com.agent.entity.recruitment.vo.QueryInterviewVo;
import com.agent.entity.recruitment.vo.QueryRecruitStatusVo;
import com.agent.entity.recruitment.vo.SaveCandidateReq;
import com.agent.entity.recruitment.vo.ScheduleInterviewReq;
import com.agent.result.Result;

import java.util.List;

public interface RecruitmentService {
    Result<QueryRecruitStatusVo> queryRecruitStatus(Integer recruitId);

    Result<QueryInterviewVo> queryInterview(Integer recruitId);

    Result<String> saveCandidate(SaveCandidateReq req);

    Result<String> scheduleInterview(ScheduleInterviewReq req);

    Result<List<QueryRecruitStatusVo>> queryRecruitStatusByName(String candidateName);

    Result<List<QueryInterviewVo>> queryInterviewByName(String candidateName);

    Result<String> updateRecruitStatus(Integer recruitId, Integer status);

    Result<String> updateRecruitStatusByName(String candidateName, Integer status);
}
