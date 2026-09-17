package org.dromara.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.system.service.IHrAiDocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI人事文档生成控制器
 */
@RestController
@RequestMapping("/system/hr/ai")
@RequiredArgsConstructor
public class HrAiDocumentController {

    private final IHrAiDocumentService hrAiDocumentService;

    /**
     * AI生成人事文档
     *
     * @param empId   员工ID
     * @param docType 文档类型：cert=在职证明, comment=转正评语, jd=招聘JD
     */
    @GetMapping("/genDoc")
    @SaCheckPermission("system:employee:query")
    public R<String> genDoc(@RequestParam Long empId, @RequestParam String docType) {
        String result = hrAiDocumentService.generateDocument(empId, docType);
        return R.ok("生成成功", result);
    }
}
