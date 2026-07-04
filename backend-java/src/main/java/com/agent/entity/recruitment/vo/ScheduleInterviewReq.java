package com.agent.entity.recruitment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ScheduleInterviewReq {
    private Integer recruitId;
    private Integer interviewRound;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private Date interviewTime;
    private String interviewer;
}
