package com.d10ng.aicodereview.util;

import com.d10ng.aicodereview.config.ApiKeySettings;
import com.d10ng.aicodereview.constant.Constants;
import com.intellij.openapi.project.Project;

/**
 * PromptUtil
 *
 * @author hmydk
 */
public class PromptUtil {

    public static final String DEFAULT_PROMPT_1 = getConciseCodeReviewPrompt();
    public static final String DEFAULT_PROMPT_2 = getCodeReviewPrompt();
    public static final String DEFAULT_PROMPT_3 = getDetailedCodeReviewPrompt();
    public static final String DEFAULT_PROMPT_4 = getSecurityFocusedCodeReviewPrompt();


    public static String constructPrompt(Project project, String diff) {
        String promptContent = "";

        // get prompt content
        ApiKeySettings settings = ApiKeySettings.getInstance();
        if (Constants.PROJECT_PROMPT.equals(settings.getPromptType())) {
            promptContent = FileUtil.loadProjectPrompt(project);
        } else {
            promptContent = settings.getCustomPrompt().getPrompt();
        }

        // check prompt content
        if (!promptContent.contains("{diff}")) {
            throw new IllegalArgumentException("The prompt file must contain the placeholder {diff}.");
        }


        if (Constants.PROJECT_PROMPT.equals(settings.getPromptType())) {
            //使用项目级别的提示文件时：language可以在文件中指定，所以这里不做强制替换
            if (promptContent.contains("{language}")) {
                promptContent = promptContent.replace("{language}", settings.getCommitLanguage());
            }
        } else {
            if (!promptContent.contains("{language}")) {
                throw new IllegalArgumentException("The prompt file must contain the placeholder {language}.");
            }
            // replace placeholder
            promptContent = promptContent.replace("{language}", settings.getCommitLanguage());
        }
        promptContent = promptContent.replace("{diff}", diff);
        //增加提示：以纯文本的形式输出结果，不要包含任何的markdown格式
        promptContent = promptContent + "\n\nNote: Output the result in plain text format, do not include any markdown formatting";
        return promptContent;
    }

    private static String getConciseCodeReviewPrompt() {
        return """
                你是一位高效的代码审查专家。请使用{language}对以下代码变更进行简洁的质量评估。
                
                代码变更：
                {diff}
                
                请遵循以下原则：
                1. 如果代码变更很小或没有明显问题，请保持报告简短精炼
                2. 只关注重要的问题和改进点，不要过度分析
                3. 如果代码质量良好，简单说明即可，无需详细解释
                
                请按以下格式组织报告：
                
                【总体评估】
                简短的总体评价，1-2句话
                
                【问题和建议】(如果有)
                - 问题1及改进建议
                - 问题2及改进建议
                
                【优点】(如果有)
                - 代码中值得肯定的地方
                
                注意：如果代码变更很小且没有明显问题，只需给出简短的总体评估即可，无需列出问题和优点。
                """;
    }

    private static String getCodeReviewPrompt() {
        return """
                你是一位经验丰富的代码审查专家。请分析以下代码变更并生成代码质量检查报告，使用{language}语言。
                
                代码变更：
                {diff}
                
                请在报告中包含以下内容：
                1. 代码质量总体评估（简洁明了）
                2. 潜在的问题和缺陷（如果有）
                   - 逻辑错误
                   - 性能问题
                   - 安全漏洞
                   - 代码风格问题
                3. 改进建议（具体且可操作）
                4. 代码亮点和优秀实践（如果有）
                
                重要提示：
                - 如果代码变更很小或没有明显问题，请保持报告简短精炼
                - 只关注重要的问题，不要过度分析
                - 如果代码质量良好，可以简单说明，无需详细解释每个方面
                """;
    }


    private static String getDetailedCodeReviewPrompt() {
        return
                """
                作为一名资深代码审查专家，请对以下代码变更进行质量评估，并使用{language}生成代码审查报告。
                
                代码变更：
                {diff}
                
                请按照以下结构组织您的代码审查报告：
                
                ## 1. 总体评估
                - 代码质量评分（1-10分）
                - 代码变更的主要目的和影响
                - 整体设计和实现的评价
                
                ## 2. 问题分析（如果存在）
                - 功能性问题
                - 性能问题
                - 安全问题
                - 可维护性问题
                
                ## 3. 改进建议
                - 针对发现的问题提供具体的改进建议
                
                ## 4. 代码亮点
                - 突出代码中的优秀实践
                
                重要提示：
                - 如果代码变更很小或没有明显问题，请保持报告简短精炼
                - 只有在确实发现问题时才详细展开分析
                - 如果某个部分没有发现问题，可以简单说明"未发现明显问题"，无需详细展开
                """;
    }

    private static String getSecurityFocusedCodeReviewPrompt() {
        return """
                作为一名专注于安全的代码审查专家，请对以下代码变更进行安全性和质量评估，并使用{language}生成代码审查报告。
                
                代码变更：
                {diff}
                
                请特别关注以下安全方面的问题：
                
                1. 输入验证和数据清理
                2. 认证和授权
                3. 敏感数据处理
                4. 错误处理和日志记录
                5. 第三方库和依赖
                6. 可能导致安全问题的代码质量问题
                
                请按以下格式组织报告：
                
                【安全评估总结】
                简要总结代码的安全状况
                
                【发现的安全问题】（如果有）
                - 问题描述
                - 潜在影响
                - 修复建议
                
                【安全建议】（如果适用）
                提供一般性的安全改进建议
                
                重要提示：
                - 如果代码变更很小或没有明显安全问题，请保持报告简短精炼
                - 只有在确实发现安全问题时才详细展开分析
                - 如果代码安全性良好，简单说明即可
                """;
    }
}
