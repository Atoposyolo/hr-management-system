package org.dromara.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.system.domain.vo.HrEmployeeVo;
import org.dromara.system.service.IHrEmployeeService;
import org.dromara.system.util.OpenAiCompatibleClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.dromara.system.mapper.SysDeptMapper;
import org.dromara.system.mapper.SysPostMapper;
import org.dromara.system.domain.SysDept;
import org.dromara.system.domain.SysPost;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/system/hr/ai")
@RequiredArgsConstructor
public class HrAiDocumentController {

    private final OpenAiCompatibleClient zhipuAiClient;
    private final IHrEmployeeService hrEmployeeService;
    private final SysDeptMapper sysDeptMapper;
    private final SysPostMapper sysPostMapper;

    /**
     * AI生成人事文档
     * @param empId   员工ID
     * @param docType 文档类型：cert=在职证明，comment=转正评语，jd=招聘JD
     */
    @GetMapping("/genDoc")
    @SaCheckPermission("system:employee:query")
    public R<String> genDoc(@RequestParam Long empId, @RequestParam String docType) {
        HrEmployeeVo emp = hrEmployeeService.queryById(empId);
        if (emp == null) {
            return R.fail("员工不存在");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String entryDateStr = emp.getEntryDate() != null ? emp.getEntryDate().format(formatter) : "";
        String deptName = "";
        String postName = "";
        if (emp.getDeptId() != null) {
            SysDept dept = sysDeptMapper.selectById(emp.getDeptId());
            if (dept != null) {
                deptName = dept.getDeptName();
            }
        }
        if (emp.getPostId() != null) {
            SysPost post = sysPostMapper.selectById(emp.getPostId());
            if (post != null) {
                postName = post.getPostName();
            }
        }

        String prompt = switch (docType) {
            case "cert" -> String.format(
                "请生成一份正式的在职证明，员工姓名：%s，工号：%s，所属部门：%s，入职日期：%s，岗位：%s。落款公司：XXX科技有限公司。要求格式规范、语气正式，最后留出公司盖章和日期位置。",
                emp.getEmpName(), emp.getEmpNo(), deptName, entryDateStr, postName);
            case "comment" -> String.format(
                "请为员工%s（部门：%s，入职时间：%s）撰写一份转正考核评语，要求客观、正式，包含工作表现、能力评价和转正建议，300字左右。",
                emp.getEmpName(), deptName, entryDateStr);
            case "jd" -> String.format(
                "请为该部门撰写一份招聘JD（职位描述）：部门：%s，学历要求：%s，岗位编号：%s。包含岗位职责、任职要求、加分项，300字左右。",
                deptName, emp.getEducation(), postName);
            default -> throw new RuntimeException("不支持的文档类型：" + docType);
        };

        String aiResult = zhipuAiClient.chat(prompt);
        return R.ok("生成成功", aiResult);
    }
}

