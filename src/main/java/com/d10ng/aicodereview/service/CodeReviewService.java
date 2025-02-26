package com.d10ng.aicodereview.service;


import com.d10ng.aicodereview.config.ApiKeySettings;
import com.d10ng.aicodereview.constant.Constants;
import com.d10ng.aicodereview.service.impl.*;
import com.d10ng.aicodereview.util.PromptUtil;
import com.intellij.openapi.project.Project;

import java.util.function.Consumer;

/**
 * 代码质量检查服务类
 */
public class CodeReviewService {
    private final AIService aiService;

    ApiKeySettings settings = ApiKeySettings.getInstance();

    public CodeReviewService() {
        String selectedClient = settings.getSelectedClient();
        this.aiService = getAIService(selectedClient);
    }

    public boolean checkNecessaryModuleConfigIsRight() {
        return aiService.checkNecessaryModuleConfigIsRight();
    }

    /**
     * 生成代码质量检查报告
     * @param project 项目
     * @param diff 代码差异
     * @return 代码质量检查报告
     * @throws Exception 异常
     */
    public String generateCodeReview(Project project, String diff) throws Exception {
        String prompt = PromptUtil.constructPrompt(project, diff);
        return aiService.generateCommitMessage(prompt);
    }

    /**
     * 流式生成代码质量检查报告
     * @param project 项目
     * @param diff 代码差异
     * @param onNext 处理每个token的回调
     * @param onError 处理错误的回调
     * @throws Exception 异常
     */
    public void generateCodeReviewStream(Project project, String diff, Consumer<String> onNext, Consumer<Throwable> onError) throws Exception {
        String prompt = PromptUtil.constructPrompt(project, diff);
        aiService.generateCommitMessageStream(prompt, onNext);
    }

    /**
     * 是否支持流式生成
     * @return 是否支持流式生成
     */
    public boolean generateByStream() {
        return aiService.generateByStream();
    }

    /**
     * 获取AI服务实例
     * @param selectedClient 选择的客户端
     * @return AI服务实例
     */
    public static AIService getAIService(String selectedClient) {
        return switch (selectedClient) {
            case Constants.Ollama -> new OllamaService();
            case Constants.Gemini -> new GeminiService();
            case Constants.DeepSeek -> new DeepSeekAPIService();
            case Constants.OpenAI_API -> new OpenAIAPIService();
            case Constants.CloudflareWorkersAI -> new CloudflareWorkersAIService();
            case Constants.阿里云百炼 -> new AliYunBaiLianService();
            case Constants.SiliconFlow -> new SiliconFlowService();
            default -> throw new IllegalArgumentException("Invalid LLM client: " + selectedClient);
        };
    }
} 