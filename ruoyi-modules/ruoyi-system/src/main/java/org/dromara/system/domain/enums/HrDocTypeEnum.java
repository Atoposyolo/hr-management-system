package org.dromara.system.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.common.core.exception.ServiceException;

/**
 * AI 人事文档类型
 */
@Getter
@AllArgsConstructor
public enum HrDocTypeEnum {

    /** 在职证明 */
    CERT("cert", "在职证明"),

    /** 转正评语 */
    COMMENT("comment", "转正评语"),

    /** 招聘 JD */
    JD("jd", "招聘JD");

    /**
     * 类型编码（与前端传参保持一致）
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String desc;

    /**
     * 按编码解析文档类型，非法编码抛出业务异常
     *
     * @param code 文档类型编码
     * @return 文档类型枚举
     */
    public static HrDocTypeEnum of(String code) {
        for (HrDocTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new ServiceException("不支持的文档类型：" + code);
    }
}
