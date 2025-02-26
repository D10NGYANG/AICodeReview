package com.d10ng.aicodereview.service;


import com.d10ng.aicodereview.config.ApiKeySettings;
import com.d10ng.aicodereview.constant.Constants;
import com.d10ng.aicodereview.service.impl.*;
import com.d10ng.aicodereview.util.PromptUtil;
import com.intellij.openapi.project.Project;

import java.util.function.Consumer;

public class CommitMessageService {
    private final AIService aiService;

    ApiKeySettings settings = ApiKeySettings.getInstance();

    public CommitMessageService() {
        String selectedClient = settings.getSelectedClient();
        this.aiService = getAIService(selectedClient);
    }

    public boolean checkNecessaryModuleConfigIsRight() {
        return aiService.checkNecessaryModuleConfigIsRight();
    }

    public String generateCommitMessage(Project project, String diff) throws Exception {
        String prompt = PromptUtil.constructPrompt(project, diff);
        return aiService.generateCommitMessage(prompt);
    }

    public void generateCommitMessageStream(Project project, String diff, Consumer<String> onNext, Consumer<Throwable> onError) throws Exception {
        String prompt = PromptUtil.constructPrompt(project, diff);
//        System.out.println(prompt);
        aiService.generateCommitMessageStream(prompt, onNext);
    }

    public boolean generateByStream() {
        return aiService.generateByStream();
    }


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
