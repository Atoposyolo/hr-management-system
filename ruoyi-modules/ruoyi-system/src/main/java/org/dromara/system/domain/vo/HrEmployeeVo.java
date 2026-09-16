package org.dromara.system.domain.vo;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.HrEmployee;
import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工管理视图对象 hr_employee
 *
 * @author Lion Li
 * @date 2026-09-09 22:21:44
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = HrEmployee.class)
public class HrEmployeeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 工号
     */
    @ExcelProperty(value = "工号")
    private String empNo;

    /**
     * 员工姓名
     */
    @ExcelProperty(value = "员工姓名")
    private String empName;

    /**
     * 性别（0男 1女 2未知）
     */
    @ExcelProperty(value = "性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_user_gender")
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
     * 部门名
     */
    @Translation(type = TransConstant.DEPT_ID_TO_NAME, mapper = "deptId")
    private String deptName;

    /**
     * 手机号码
     */
    @ExcelProperty(value = "手机号码")
    private String phone;


    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱")
    private String email;

    /**
     * 身份证号
     */
    @ExcelProperty(value = "身份证号")
    private String idCard;

    /**
     * 学历（1大专 2本科 3硕士 4博士 5其他）
     */
    @ExcelProperty(value = "学历", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=大专,2=本科,3=硕士,4=博士,5=其他")
    private String education;

    /**
     * 入职日期
     */
    @ExcelProperty(value = "入职日期")
    private LocalDateTime entryDate;

    /**
     * 状态（0在职 1离职）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=在职,1=离职")
    private String status;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
