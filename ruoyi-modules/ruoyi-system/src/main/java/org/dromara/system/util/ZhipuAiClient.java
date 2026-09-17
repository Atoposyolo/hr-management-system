package org.dromara.system.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ZhipuAiClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.zhipu.api-key}")
    private String apiKey;

    @Value("${ai.zhipu.api-url}")
    private String apiUrl;

    @Value("${ai.zhipu.model}")
    private String model;

    /**
     * 调用智谱大模型，返回生成文本
     */
    public String chat(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("未配置 ZHIPU_API_KEY 环境变量，请在启动配置中添加");
        }
        try {
            java.util.Map<String, Object> bodyMap = new java.util.HashMap<>();
            bodyMap.put("model", model);
            bodyMap.put("messages", List.of(
                Map.of("role", "system", "content", "你是一位专业的人力资源助理，负责撰写正式的人事文档。只输出文档正文，不要任何解释、前缀或Markdown标记。"),
                Map.of("role", "user", "content", prompt)
            ));
            bodyMap.put("temperature", 0.3);
            bodyMap.put("stream", false);
            // 关闭深度思考，直接返回正文
            //bodyMap.put("thinking", Map.of("type", "disabled"));

            String jsonBody = objectMapper.writeValueAsString(bodyMap);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(120))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 打印智谱原始返回，便于排查问题
            log.info("智谱AI原始返回: {}", response.body());

            JsonNode message = objectMapper.readTree(response.body())
                .path("choices").path(0).path("message");
            String content = message.path("content").asText("");
            if (content.isBlank()) {
                content = message.path("reasoning_content").asText("");
            }
            return content;
        } catch (Exception e) {
            log.error("调用智谱AI接口异常", e);
            throw new RuntimeException("AI文档生成失败，请稍后重试");
        }
    }
}
