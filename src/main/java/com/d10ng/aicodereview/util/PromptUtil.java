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

    public static final String DEFAULT_PROMPT_1 = getCodeReviewPrompt();
    public static final String DEFAULT_PROMPT_2 = getDetailedCodeReviewPrompt();
    public static final String DEFAULT_PROMPT_3 = getSecurityFocusedCodeReviewPrompt();


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

    private static String getCodeReviewPrompt() {
        return """
                你是一位经验丰富的代码审查专家。请分析以下代码变更并生成一份详细的代码质量检查报告，使用{language}语言。
                
                代码变更：
                {diff}
                
                请在报告中包含以下内容：
                1. 代码质量总体评估
                2. 潜在的问题和缺陷
                   - 逻辑错误
                   - 性能问题
                   - 安全漏洞
                   - 代码风格问题
                3. 改进建议
                4. 代码亮点和优秀实践
                
                请确保报告清晰、具体，并提供有价值的反馈。
                """;
    }


    private static String getDetailedCodeReviewPrompt() {
        return
                """
                作为一名资深代码审查专家，请对以下代码变更进行全面的质量评估，并使用{language}生成详细的代码审查报告。
                
                代码变更：
                {diff}
                
                请按照以下结构组织您的代码审查报告：
                
                ## 1. 总体评估
                - 代码质量评分（1-10分）
                - 代码变更的主要目的和影响
                - 整体设计和实现的评价
                
                ## 2. 详细问题分析
                ### 2.1 功能性问题
                - 是否正确实现了预期功能
                - 是否存在边缘情况未处理
                - 是否有潜在的业务逻辑错误
                
                ### 2.2 性能问题
                - 是否存在性能瓶颈
                - 是否有不必要的计算或操作
                - 是否有可能导致内存泄漏的代码
                
                ### 2.3 安全问题
                - 是否存在安全漏洞
                - 是否有输入验证不足的情况
                - 是否有敏感数据处理不当的问题
                
                ### 2.4 可维护性问题
                - 代码结构和组织是否合理
                - 命名和注释是否清晰
                - 是否遵循了项目的编码规范
                
                ## 3. 具体改进建议
                - 针对每个问题提供具体的改进建议
                - 提供代码示例（如适用）
                
                ## 4. 代码亮点
                - 突出代码中的优秀实践和创新点
                
                请确保您的评审全面、客观，并提供具体的建议以帮助改进代码质量。
                """;
    }

    private static String getSecurityFocusedCodeReviewPrompt() {
        return """
                作为一名专注于安全的代码审查专家，请对以下代码变更进行安全性和质量评估，并使用{language}生成一份详细的代码审查报告。
                
                代码变更：
                {diff}
                
                请特别关注以下安全方面的问题：
                
                1. 输入验证和数据清理
                   - 是否存在注入攻击的风险（SQL注入、XSS、命令注入等）
                   - 是否对用户输入进行了充分的验证和清理
                
                2. 认证和授权
                   - 是否正确实现了身份验证和授权检查
                   - 是否存在权限提升的风险
                
                3. 敏感数据处理
                   - 是否安全地处理和存储敏感数据
                   - 是否使用了适当的加密方法
                
                4. 错误处理和日志记录
                   - 是否泄露了敏感信息
                   - 是否记录了足够的安全相关事件
                
                5. 第三方库和依赖
                   - 是否使用了已知存在漏洞的库
                   - 是否正确配置了第三方组件
                
                6. 代码质量问题
                   - 是否存在可能导致安全问题的代码质量问题
                   - 是否有未使用的代码或功能
                
                7. 具体的安全改进建议
                   - 针对每个安全问题提供具体的修复建议
                
                请提供一份全面、详细的安全评估报告，帮助开发团队提高代码的安全性和质量。
                """;
    }
}
