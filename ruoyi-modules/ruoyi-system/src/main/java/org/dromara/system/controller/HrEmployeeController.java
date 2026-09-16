package org.dromara.system.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.system.domain.vo.HrEmployeeStatVo;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.redis.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelBuilder;
import org.dromara.system.domain.vo.HrEmployeeVo;
import org.dromara.system.domain.bo.HrEmployeeBo;
import org.dromara.system.service.IHrEmployeeService;
import org.dromara.common.core.domain.PageResult;

/**
 * 员工管理
 *
 * @author Lion Li
 * @date 2026-09-09 22:21:44
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/employee")
public class HrEmployeeController extends BaseController {

    private final IHrEmployeeService hrEmployeeService;

    /**
     * 查询员工管理列表
     */
    @SaCheckPermission("system:employee:list")
    @GetMapping("/list")
    public R<PageResult<HrEmployeeVo>> list(HrEmployeeBo bo, PageQuery pageQuery) {
        return R.ok(hrEmployeeService.queryPageList(bo, pageQuery));
    }

    /**
     * 导出员工管理列表
     */
    @SaCheckPermission("system:employee:export")
    @Log(title = "员工管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HrEmployeeBo bo, HttpServletResponse response) {
        List<HrEmployeeVo> list = hrEmployeeService.queryList(bo);
        ExcelBuilder.of(list, HrEmployeeVo.class).sheetName("员工管理").toResponse(response);
    }

    /**
     * 获取员工管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:employee:query")
    @GetMapping("/{id}")
    public R<HrEmployeeVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(hrEmployeeService.queryById(id));
    }

    /**
     * 新增员工管理
     */
    @SaCheckPermission("system:employee:add")
    @Log(title = "员工管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HrEmployeeBo bo) {
        return toAjax(hrEmployeeService.insertByBo(bo));
    }

    /**
     * 修改员工管理
     */
    @SaCheckPermission("system:employee:edit")
    @Log(title = "员工管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HrEmployeeBo bo) {
        return toAjax(hrEmployeeService.updateByBo(bo));
    }



    /**
     * 删除员工管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:employee:remove")
    @Log(title = "员工管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(hrEmployeeService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 员工数据统计看板
     */
    @SaCheckPermission("system:employee:list")
    @GetMapping("/statistics")
    public R<HrEmployeeStatVo> statistics() {
        return R.ok(hrEmployeeService.getStatistics());
    }
}
