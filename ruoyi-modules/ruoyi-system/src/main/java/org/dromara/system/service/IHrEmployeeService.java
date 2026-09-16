package org.dromara.system.service;

import org.dromara.system.domain.vo.HrEmployeeVo;
import org.dromara.system.domain.bo.HrEmployeeBo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;
import org.dromara.system.domain.vo.HrEmployeeStatVo;

/**
 * 员工管理Service接口
 *
 * @author Lion Li
 * @date 2026-09-09 22:21:44
 */
public interface IHrEmployeeService {

    /**
     * 查询员工管理
     *
     * @param id 主键
     * @return 员工管理
     */
    HrEmployeeVo queryById(Long id);

    /**
     * 分页查询员工管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 员工管理分页列表
     */
    PageResult<HrEmployeeVo> queryPageList(HrEmployeeBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的员工管理列表
     *
     * @param bo 查询条件
     * @return 员工管理列表
     */
    List<HrEmployeeVo> queryList(HrEmployeeBo bo);


    /**
     * 新增员工管理
     *
     * @param bo 员工管理
     * @return 是否新增成功
     */
    Boolean insertByBo(HrEmployeeBo bo);

    /**
     * 修改员工管理
     *
     * @param bo 员工管理
     * @return 是否修改成功
     */
    Boolean updateByBo(HrEmployeeBo bo);



    /**
     * 校验并批量删除员工管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 查询员工统计数据（走 Redis 缓存）
     */
    HrEmployeeStatVo getStatistics();
}
