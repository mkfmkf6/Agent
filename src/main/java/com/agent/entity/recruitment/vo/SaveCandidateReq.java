package com.agent.entity.recruitment.vo;

import lombok.Data;

@Data
public class SaveCandidateReq {
    private String positionName;
    private String candidateName;
    private String candidatePhone;
    private String candidateEmail;
    private String resumeSource;
    private Integer resumeParseStatus;
    private String resumeFilePath;
    private String educationBackground;
    private String workExperience;
    private String skillTags;
}