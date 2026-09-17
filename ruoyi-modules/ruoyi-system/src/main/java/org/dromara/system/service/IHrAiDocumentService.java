package org.dromara.system.service;

/**
 * AI人事文档生成服务接口
 */
public interface IHrAiDocumentService {

    /**
     * 根据员工ID和文档类型生成AI文档
     *
     * @param empId   员工ID
     * @param docType 文档类型：cert-在职证明, comment-转正评语, jd-招聘JD
     * @return AI生成的文档内容
     */
    String generateDocument(Long empId, String docType);
}
