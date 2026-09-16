package org.dromara.system.domain.vo;

import lombok.Data;
import java.util.List;

/**
 * 员工统计看板视图对象
 */
@Data
public class HrEmployeeStatVo {

    /** 员工总数 */
    private Long total;

    /** 在职人数 */
    private Long onJobCount;

    /** 离职人数 */
    private Long leaveCount;

    /** 按在职状态分布 */
    private List<StatItem> statusStats;

    /** 按学历分布 */
    private List<StatItem> educationStats;

    /** 通用统计项：标签 + 数量 */
    @Data
    public static class StatItem {
        private String label;
        private Long value;
    }
}

