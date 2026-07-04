package com.agent.entity.recruitment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 招聘候选人表
 * </p>
 *
 * @author author
 * @since 2026-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("recruitment")
public class Recruitment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 招聘记录ID
     */
    @TableId(value = "recruit_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer recruitId;

    /**
     * 招聘岗位
     */
    private String positionName;

    /**
     * 所属部门
     */
    private String positionDepartment;

    /**
     * 候选人姓名
     */
    private String candidateName;

    /**
     * 候选人手机号
     */
    private String candidatePhone;

    /**
     * 候选人邮箱
     */
    private String candidateEmail;

    /**
     * 简历来源
     */
    private String resumeSource;

    /**
     * 解析状态：1-未解析 2-已解析 3-失败
     */
    private Integer resumeParseStatus;

    /**
     * 招聘进度：1-简历筛选 2-初面 3-复面 4-终面 5-Offer 6-已入职 7-淘汰
     */
    private Integer recruitStatus;

    /**
     * 当前面试轮次
     */
    private Integer currentInterviewRound;

    /**
     * 面试时间
     */
    private Date interviewTime;

    /**
     * 面试官
     */
    private String interviewer;

    /**
     * 简历文件路径
     */
    private String resumeFilePath;

    /**
     * 教育背景 JSON
     */
    private String educationBackground;

    /**
     * 工作经历 JSON
     */
    private String workExperience;

    /**
     * 技能标签
     */
    private String skillTags;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
