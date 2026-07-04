package com.agent.service.impl;

import com.agent.entity.recruitment.Recruitment;
import com.agent.entity.recruitment.vo.QueryInterviewVo;
import com.agent.entity.recruitment.vo.QueryRecruitStatusVo;
import com.agent.entity.recruitment.vo.SaveCandidateReq;
import com.agent.entity.recruitment.vo.ScheduleInterviewReq;
import com.agent.mapper.RecruitmentMapper;
import com.agent.result.Result;
import com.agent.service.RecruitmentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecruitmentServiceImpl implements RecruitmentService {
    @Autowired
    private RecruitmentMapper recruitmentMapper;

    @Override
    public Result<QueryRecruitStatusVo> queryRecruitStatus(Integer recruitId) {
        LambdaQueryWrapper<Recruitment> wrapper = new LambdaQueryWrapper<Recruitment>()
                .eq(Recruitment::getRecruitId, recruitId)
                .select(Recruitment::getRecruitId, Recruitment::getCandidateName, Recruitment::getRecruitStatus);
        Recruitment recruitment = recruitmentMapper.selectOne(wrapper);
        if (recruitment == null) {
            return Result.error("招聘信息不存在");
        }
        QueryRecruitStatusVo vo = new QueryRecruitStatusVo();
        vo.setRecruitId(recruitment.getRecruitId());
        vo.setCandidateName(recruitment.getCandidateName());
        vo.setRecruitStatus(recruitment.getRecruitStatus());
        return Result.success(vo);
    }

    @Override
    public Result<QueryInterviewVo> queryInterview(Integer recruitId) {
        LambdaQueryWrapper<Recruitment> wrapper = new LambdaQueryWrapper<Recruitment>()
                .eq(Recruitment::getRecruitId, recruitId)
                .select(Recruitment::getRecruitId, Recruitment::getCandidateName, Recruitment::getCurrentInterviewRound, Recruitment::getInterviewTime, Recruitment::getInterviewer);
        Recruitment recruitment = recruitmentMapper.selectOne(wrapper);
        if (recruitment == null) {
            return Result.error("招聘信息不存在");
        }
        QueryInterviewVo vo = new QueryInterviewVo();
        vo.setRecruitId(recruitment.getRecruitId());
        vo.setCandidateName(recruitment.getCandidateName());
        vo.setCurrentInterviewRound(recruitment.getCurrentInterviewRound());
        vo.setInterviewTime(recruitment.getInterviewTime());
        vo.setInterviewer(recruitment.getInterviewer());
        return Result.success(vo);
    }

    @Override
    public Result<String> saveCandidate(SaveCandidateReq req) {
        try {
            Recruitment recruitment = new Recruitment();
            BeanUtils.copyProperties(req, recruitment);
            recruitment.setRecruitStatus(1);
            recruitment.setCurrentInterviewRound(0);
            
            if (recruitment.getPositionDepartment() == null || recruitment.getPositionDepartment().isEmpty()) {
                recruitment.setPositionDepartment("技术部");
            }
            
            if (recruitment.getCandidateName() == null || recruitment.getCandidateName().isEmpty()) {
                return Result.error("候选人为空");
            }
            
            recruitmentMapper.insert(recruitment);
            return Result.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> scheduleInterview(ScheduleInterviewReq req) {
        Recruitment recruitment = recruitmentMapper.selectById(req.getRecruitId());
        if (recruitment == null) {
            return Result.error("招聘信息不存在");
        }
        recruitment.setCurrentInterviewRound(req.getInterviewRound());
        recruitment.setInterviewTime(req.getInterviewTime());
        recruitment.setInterviewer(req.getInterviewer());
        recruitment.setRecruitStatus(req.getInterviewRound() + 1);
        recruitmentMapper.updateById(recruitment);
        return Result.success("面试安排成功，已通知候选人");
    }

    @Override
    public Result<List<QueryRecruitStatusVo>> queryRecruitStatusByName(String candidateName) {
        LambdaQueryWrapper<Recruitment> wrapper = new LambdaQueryWrapper<Recruitment>()
                .like(Recruitment::getCandidateName, candidateName)
                .select(Recruitment::getRecruitId, Recruitment::getCandidateName, Recruitment::getRecruitStatus, Recruitment::getPositionName);
        List<Recruitment> list = recruitmentMapper.selectList(wrapper);
        if (list.isEmpty()) {
            return Result.error("未找到该候选人的招聘信息");
        }
        List<QueryRecruitStatusVo> voList = list.stream()
                .map(r -> {
                    QueryRecruitStatusVo vo = new QueryRecruitStatusVo();
                    vo.setRecruitId(r.getRecruitId());
                    vo.setCandidateName(r.getCandidateName());
                    vo.setRecruitStatus(r.getRecruitStatus());
                    return vo;
                })
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    public Result<List<QueryInterviewVo>> queryInterviewByName(String candidateName) {
        LambdaQueryWrapper<Recruitment> wrapper = new LambdaQueryWrapper<Recruitment>()
                .like(Recruitment::getCandidateName, candidateName)
                .select(Recruitment::getRecruitId, Recruitment::getCandidateName, Recruitment::getCurrentInterviewRound, Recruitment::getInterviewTime, Recruitment::getInterviewer);
        List<Recruitment> list = recruitmentMapper.selectList(wrapper);
        if (list.isEmpty()) {
            return Result.error("未找到该候选人的面试安排");
        }
        List<QueryInterviewVo> voList = list.stream()
                .map(r -> {
                    QueryInterviewVo vo = new QueryInterviewVo();
                    vo.setRecruitId(r.getRecruitId());
                    vo.setCandidateName(r.getCandidateName());
                    vo.setCurrentInterviewRound(r.getCurrentInterviewRound());
                    vo.setInterviewTime(r.getInterviewTime());
                    vo.setInterviewer(r.getInterviewer());
                    return vo;
                })
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    public Result<String> updateRecruitStatus(Integer recruitId, Integer status) {
        try {
            Recruitment recruitment = recruitmentMapper.selectById(recruitId);
            if (recruitment == null) {
                return Result.error("招聘信息不存在");
            }
            recruitment.setRecruitStatus(status);
            recruitmentMapper.updateById(recruitment);
            String statusText = getStatusText(status);
            return Result.success("状态更新成功，当前状态：" + statusText);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> updateRecruitStatusByName(String candidateName, Integer status) {
        try {
            LambdaQueryWrapper<Recruitment> wrapper = new LambdaQueryWrapper<Recruitment>()
                    .like(Recruitment::getCandidateName, candidateName);
            List<Recruitment> list = recruitmentMapper.selectList(wrapper);
            if (list.isEmpty()) {
                return Result.error("未找到该候选人");
            }
            if (list.size() > 1) {
                return Result.error("找到多个匹配的候选人，请使用招聘ID精确更新");
            }
            Recruitment recruitment = list.get(0);
            recruitment.setRecruitStatus(status);
            recruitmentMapper.updateById(recruitment);
            String statusText = getStatusText(status);
            return Result.success("状态更新成功，" + recruitment.getCandidateName() + "当前状态：" + statusText);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case 1 -> "简历筛选";
            case 2 -> "初面";
            case 3 -> "复面";
            case 4 -> "终面";
            case 5 -> "Offer";
            case 6 -> "已入职";
            case 7 -> "淘汰";
            default -> "未知";
        };
    }
}
