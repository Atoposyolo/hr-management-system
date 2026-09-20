package org.dromara.system.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.domain.enums.HrDocTypeEnum;
import org.dromara.system.domain.vo.HrEmployeeVo;
import org.dromara.system.mapper.SysDeptMapper;
import org.dromara.system.mapper.SysPostMapper;
import org.dromara.system.service.IHrAiDocumentService;
import org.dromara.system.service.IHrEmployeeService;
import org.dromara.system.util.OpenAiCompatibleClient;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class HrAiDocumentServiceImpl implements IHrAiDocumentService {

    private final IHrEmployeeService hrEmployeeService;
    private final SysDeptMapper sysDeptMapper;
    private final SysPostMapper sysPostMapper;
    private final OpenAiCompatibleClient openAiCompatibleClient;

    @Override
    public String generateDocument(Long empId, String docType) {
        // 0. 校验文档类型（非法类型直接抛业务异常）
        HrDocTypeEnum documentType = HrDocTypeEnum.of(docType);

        // 1. 查询员工信息
        HrEmployeeVo emp = hrEmployeeService.queryById(empId);
        if (emp == null) {
            throw new ServiceException("员工不存在");
        }

        // 2. 入职日期格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String entryDateStr = emp.getEntryDate() != null ? emp.getEntryDate().format(formatter) : "";

        // 3. 查询部门名称
        String deptName = "";
        if (emp.getDeptId() != null) {
            var dept = sysDeptMapper.selectById(emp.getDeptId());
            if (dept != null) {
                deptName = dept.getDeptName();
            }
        }

        // 4. 查询岗位名称
        String postName = "";
        if (emp.getPostId() != null) {
            var post = sysPostMapper.selectById(emp.getPostId());
            if (post != null) {
                postName = post.getPostName();
            }
        }

        // 5. 根据文档类型拼接 Prompt
        String prompt = switch (documentType) {
            case CERT -> String.format(
                "请生成一份正式的在职证明，员工姓名：%s，工号：%s，所属部门：%s，入职日期：%s，岗位：%s。落款公司：XXX科技有限公司。要求格式规范、语气正式，最后留出公司盖章和日期位置。",
                emp.getEmpName(), emp.getEmpNo(), deptName, entryDateStr, postName);
            case COMMENT -> String.format(
                "请为员工%s（部门：%s，入职时间：%s）撰写一份转正考核评语，要求客观、正式，包含工作表现、能力评价和转正建议，300字左右。",
                emp.getEmpName(), deptName, entryDateStr);
            case JD -> String.format(
                "请为该部门撰写一份招聘JD（职位描述）：部门：%s，学历要求：%s，岗位编号：%s。包含岗位职责、任职要求、加分项，300字左右。",
                deptName, emp.getEducation(), postName);
        };

        // 6. 调用 AI 生成文档
        return openAiCompatibleClient.chat(prompt);
    }
}
