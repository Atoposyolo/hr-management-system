package org.dromara.system.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAI 兼容协议大模型客户端
 * 基于 JDK 原生 HttpClient 封装，零第三方 SDK 依赖，可在硅基流动、智谱等兼容平台间切换
 */
@Slf4j
@Component
public class OpenAiCompatibleClient {

    /**
     * ObjectMapper 初始化完成后线程安全，全局复用一个实例
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .build();

    @Value("${SILICON_API_KEY:}")
    private String apiKey;

    @Value("${SILICON_API_URL:https://api.siliconflow.cn/v1/chat/completions}")
    private String apiUrl;

    @Value("${SILICON_API_MODEL:Qwen/Qwen2.5-7B-Instruct}")
    private String model;

    /**
     * 调用大模型对话接口，返回生成文本
     *
     * @param prompt 提示词
     * @return 模型生成的正文
     */
    public String chat(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ServiceException("未配置 SILICON_API_KEY 环境变量，请在启动配置中添加后重试");
        }
        try {
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("model", model);
            bodyMap.put("messages", List.of(
                Map.of("role", "system", "content", "你是一位专业的人力资源助理，负责撰写正式的人事文档。只输出文档正文，不要任何解释、前缀或Markdown标记。"),
                Map.of("role", "user", "content", prompt)
            ));
            bodyMap.put("temperature", 0.3);
            bodyMap.put("stream", false);

            String jsonBody = OBJECT_MAPPER.writeValueAsString(bodyMap);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(120))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // HTTP 状态码非 2xx 时给出明确错误，避免把错误响应当成空内容返回
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("AI接口调用失败，HTTP状态码：{}，返回：{}", response.statusCode(), response.body());
                throw new ServiceException("AI服务暂不可用（HTTP " + response.statusCode() + "），请稍后重试");
            }

            JsonNode message = OBJECT_MAPPER.readTree(response.body())
                .path("choices").path(0).path("message");
            String content = message.path("content").asText("");
            if (content.isBlank()) {
                // 兼容推理模型把正文放在 reasoning_content 的情况
                content = message.path("reasoning_content").asText("");
            }
            if (content.isBlank()) {
                log.error("AI接口未返回有效内容，原始返回：{}", response.body());
                throw new ServiceException("AI未返回有效内容，请稍后重试");
            }
            return content;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用AI接口异常", e);
            throw new ServiceException("AI文档生成失败，请稍后重试");
        }
    }
}
