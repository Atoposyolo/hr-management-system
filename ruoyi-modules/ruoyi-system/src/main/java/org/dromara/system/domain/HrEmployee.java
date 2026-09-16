package org.dromara.system.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 员工管理对象 hr_employee
 *
 * @author Lion Li
 * @date 2026-09-09 22:21:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_employee")
public class HrEmployee extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 工号
     */
    private String empNo;

    /**
     * 员工姓名
     */
    private String empName;

    /**
     * 性别（0男 1女 2未知）
     */
    private String gender;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 岗位id
     */
    private Long postId;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 学历（1大专 2本科 3硕士 4博士 5其他）
     */
    private String education;

    /**
     * 入职日期
     */
    private LocalDateTime entryDate;

    /**
     * 状态（0在职 1离职）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer delFlag;


}
