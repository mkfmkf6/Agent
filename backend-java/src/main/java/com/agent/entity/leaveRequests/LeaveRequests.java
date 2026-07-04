package com.agent.entity.leaveRequests;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 请假申请表
 * </p>
 *
 * @author author
 * @since 2026-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("leave_requests")
public class LeaveRequests implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请假申请ID
     */
    @TableId(value = "leave_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer leaveId;

    /**
     * 员工ID
     */
    private Integer empId;

    /**
     * 请假类型：1-年假 2-事假 3-病假 4-婚假 5-产假 6-丧假
     */
    private Integer leaveType;

    /**
     * 请假开始时间
     */
    private LocalDateTime startTime;

    /**
     * 请假结束时间
     */
    private LocalDateTime endTime;

    /**
     * 请假总天数
     */
    private BigDecimal leaveDays;

    /**
     * 申请状态：1-待审批 2-已通过 3-已驳回
     */
    private Integer applyStatus;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 审批人ID
     */
    private Integer approverId;

    /**
     * 请假事由
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
