package org.dromara.system.domain.bo;

import org.dromara.system.domain.HrEmployee;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 员工管理业务对象 hr_employee
 *
 * @author Lion Li
 * @date 2026-09-09 22:21:44
 */
@Data
@AutoMapper(target = HrEmployee.class, reverseConvertGenerate = false)
public class HrEmployeeBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 工号
     */
    @NotBlank(message = "工号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String empNo;

    /**
     * 员工姓名
     */
    @NotBlank(message = "员工姓名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String empName;

    /**
     * 性别（0男 1女 2未知）
     */
    private String gender;

    /**
     * 部门id
     */
    @NotNull(message = "部门id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long deptId;

    /**
     * 岗位id
     */
    private Long postId;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^(1[3-9]\\d{9})?$", message = "手机号格式不正确", groups = { AddGroup.class, EditGroup.class })
    private String phone;


    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确", groups = { AddGroup.class, EditGroup.class })
    private String email;

    /**
     * 身份证号
     */
    @Pattern(regexp = "^(\\d{17}[0-9Xx])?$", message = "身份证号格式不正确", groups = { AddGroup.class, EditGroup.class })
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
     * 查询参数
     */
    private Map<String, Object> params = new HashMap<>();

}
