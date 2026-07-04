package com.agent.entity.recruitment.vo;

import lombok.Data;

import java.util.Date;

@Data
public class QueryInterviewVo {
    private Integer recruitId;
    private String candidateName;
    private Integer currentInterviewRound;
    private Date interviewTime;
    private String interviewer;
}
