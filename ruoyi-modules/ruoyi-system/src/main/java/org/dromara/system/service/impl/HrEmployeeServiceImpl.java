package org.dromara.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.common.mybatis.core.query.QueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.HrEmployeeBo;
import org.dromara.system.domain.vo.HrEmployeeVo;
import org.dromara.system.domain.HrEmployee;
import org.dromara.system.mapper.HrEmployeeMapper;
import org.dromara.system.service.IHrEmployeeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import org.dromara.common.redis.utils.RedisUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.hutool.core.convert.Convert;
import org.dromara.system.domain.vo.HrEmployeeStatVo;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * 员工管理Service业务层处理
 *
 * @author Lion Li
 * @date 2026-09-09 22:21:44
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class HrEmployeeServiceImpl implements IHrEmployeeService {

    private final HrEmployeeMapper hrEmployeeMapper;

    /**
     * 员工统计数据的 Redis 缓存 key
     */
    private static final String STAT_CACHE_KEY = "hr:employee:statistics";


    /**
     * 查询员工管理
     *
     * @param id 主键
     * @return 员工管理
     */
    @Override
    public HrEmployeeVo queryById(Long id) {
        return hrEmployeeMapper.selectVoById(id);
    }

    /**
     * 分页查询员工管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 员工管理分页列表
     */
    @Override
    public PageResult<HrEmployeeVo> queryPageList(HrEmployeeBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HrEmployee> lqw = buildQueryWrapper(bo);
        Page<HrEmployeeVo> result = hrEmployeeMapper.selectVoPage(pageQuery.build(), lqw);
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    /**
     * 查询符合条件的员工管理列表
     *
     * @param bo 查询条件
     * @return 员工管理列表
     */
    @Override
    public List<HrEmployeeVo> queryList(HrEmployeeBo bo) {
        LambdaQueryWrapper<HrEmployee> lqw = buildQueryWrapper(bo);
        return hrEmployeeMapper.selectVoList(lqw);
    }


    private LambdaQueryWrapper<HrEmployee> buildQueryWrapper(HrEmployeeBo bo) {
        Map<String, Object> params = bo.getParams();
        return QueryBuilder.lambda(HrEmployee.class)
            .eqIfText(HrEmployee::getEmpNo, bo.getEmpNo())
            .likeIfText(HrEmployee::getEmpName, bo.getEmpName())
            .eqIfPresent(HrEmployee::getDeptId, bo.getDeptId())
            .betweenParams(HrEmployee::getEntryDate, params, "beginEntryDate", "endEntryDate")
            .orderByAsc(HrEmployee::getId)
            .build();
    }

    /**
     * 新增员工管理
     *
     * @param bo 员工管理
     * @return 是否新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(HrEmployeeBo bo) {
        HrEmployee add = MapstructUtils.convert(bo, HrEmployee.class);
        validEntityBeforeSave(add);
        boolean flag = hrEmployeeMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            // 新增成功后清除统计缓存
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    RedisUtils.deleteObject(STAT_CACHE_KEY);
                }
            });
        }
        return flag;
    }

    /**
     * 修改员工管理
     *
     * @param bo 员工管理
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(HrEmployeeBo bo) {
        HrEmployee update = MapstructUtils.convert(bo, HrEmployee.class);
        validEntityBeforeSave(update);
        boolean success = hrEmployeeMapper.updateById(update) > 0;
        if (success) {
            // 数据库更新成功后清除统计缓存
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    RedisUtils.deleteObject(STAT_CACHE_KEY);
                }
            });
        }
        return success;
    }



    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(HrEmployee entity) {
        // 校验工号是否唯一：同工号、且不是当前记录，即判定为重复
        boolean exists = hrEmployeeMapper.lambda()
            .eq(HrEmployee::getEmpNo, entity.getEmpNo())
            .neIfPresent(HrEmployee::getId, entity.getId())
            .exists();
        if (exists) {
            throw new ServiceException("工号'" + entity.getEmpNo() + "'已存在");
        }

    }


    /**
     * 校验并批量删除员工管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 可在此扩展删除前业务校验
        }
        boolean success = hrEmployeeMapper.deleteByIds(ids) > 0;
        if (success) {
            // 数据库删除成功后，清除统计缓存，下次查询自动重建（Cache-Aside）
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    RedisUtils.deleteObject(STAT_CACHE_KEY);
                }
            });
        }
        return success;
    }


    @Override
    public HrEmployeeStatVo getStatistics() {
        // 1.先查 Redis 缓存，命中直接返回
        String cacheKey = STAT_CACHE_KEY;
        HrEmployeeStatVo cache = RedisUtils.getCacheObject(cacheKey);
        if (cache != null) {
            return cache;
        }

        // 2.缓存未命中，查数据库做聚合统计
        HrEmployeeStatVo stat = new HrEmployeeStatVo();
        stat.setTotal(hrEmployeeMapper.selectCount(Wrappers.<HrEmployee>query()));

        // 2.1 按在职状态分组统计
        List<Map<String, Object>> statusMaps = hrEmployeeMapper.selectMaps(
            Wrappers.<HrEmployee>query()
                .select("status, count(*) as value")
                .groupBy("status")
        );
        List<HrEmployeeStatVo.StatItem> statusStats = new ArrayList<>();
        long onJob = 0L, leave = 0L;
        for (Map<String, Object> map : statusMaps) {
            HrEmployeeStatVo.StatItem item = new HrEmployeeStatVo.StatItem();
            item.setLabel(Convert.toStr(map.get("status")));
            item.setValue(Convert.toLong(map.get("value")));
            statusStats.add(item);
            if ("0".equals(item.getLabel())) {
                onJob = item.getValue();
            } else if ("1".equals(item.getLabel())) {
                leave = item.getValue();
            }
        }
        stat.setOnJobCount(onJob);
        stat.setLeaveCount(leave);
        stat.setStatusStats(statusStats);

        // 2.2 按学历分组统计
        List<Map<String, Object>> eduMaps = hrEmployeeMapper.selectMaps(
            Wrappers.<HrEmployee>query()
                .select("education, count(*) as value")
                .groupBy("education")
        );
        List<HrEmployeeStatVo.StatItem> eduStats = new ArrayList<>();
        for (Map<String, Object> map : eduMaps) {
            HrEmployeeStatVo.StatItem item = new HrEmployeeStatVo.StatItem();
            item.setLabel(Convert.toStr(map.get("education")));
            item.setValue(Convert.toLong(map.get("value")));
            eduStats.add(item);
        }
        stat.setEducationStats(eduStats);

        // 3.写入 Redis，有效期 5 分钟
        RedisUtils.setCacheObject(cacheKey, stat, Duration.ofMinutes(5));
        return stat;
    }
}
