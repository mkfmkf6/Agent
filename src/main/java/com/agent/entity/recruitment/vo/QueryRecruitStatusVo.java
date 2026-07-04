package com.agent.entity.recruitment.vo;

import lombok.Data;

@Data
public class QueryRecruitStatusVo {
    private Integer recruitId;
    private String candidateName;
    //招聘进度：1-简历筛选 2-初面 3-复面 4-终面 5-Offer 6-已入职 7-淘汰,
    private Integer recruitStatus;
}
