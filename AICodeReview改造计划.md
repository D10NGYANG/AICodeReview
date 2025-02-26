# AICodeReview插件改造计划

## 1. 项目概述

原项目AIGitCommit是一个帮助用户自动生成Git提交日志的IntelliJ IDEA插件。我们需要将其改造为AICodeReview插件，用于生成代码质量检查报告，而不是提交日志。

## 2. 需要修改的文件

### 2.1 核心功能文件

1. **GenerateCommitMessageAction.java**
   - 重命名为`GenerateCodeReviewAction.java`
   - 修改类名和相关方法名
   - 修改按钮文本和描述
   - 修改生成逻辑，从生成提交日志改为生成代码质量检查报告

2. **CommitMessageService.java**
   - 重命名为`CodeReviewService.java`
   - 修改类名和相关方法名
   - 修改生成逻辑，调用AI API生成代码质量检查报告

3. **PromptUtil.java**
   - 修改提示模板，从提交日志生成改为代码质量检查报告生成
   - 添加新的默认提示模板，专注于代码质量检查

### 2.2 配置文件

1. **plugin.xml**
   - 修改插件ID、名称、描述
   - 修改按钮文本和描述
   - 更新action的ID和类引用

2. **Constants.java**
   - 修改相关常量名称和值
   - 添加代码质量检查相关的常量

3. **ApiKeySettings.java**
   - 可能需要添加代码质量检查相关的配置项

### 2.3 UI文件

1. **ApiKeyConfigurableUI.java**
   - 修改UI文本，从"提交日志"改为"代码质量检查"

## 3. 具体修改内容

### 3.1 GenerateCommitMessageAction.java → GenerateCodeReviewAction.java

```java
// 修改类名
public class GenerateCodeReviewAction extends AnAction {
    // 修改常量引用
    // ...
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 修改逻辑，从生成提交日志改为生成代码质量检查报告
        // ...
        
        // 修改UI提示文本
        commitMessage.setCommitMessage(Constants.GENERATING_CODE_REVIEW);
        
        // 修改服务调用
        CodeReviewService codeReviewService = new CodeReviewService();
        // ...
        
        // 修改后台任务标题
        ProgressManager.getInstance().run(new Task.Backgroundable(project, Constants.TASK_TITLE_CODE_REVIEW, true) {
            // ...
        });
    }
}
```

### 3.2 CommitMessageService.java → CodeReviewService.java

```java
// 修改类名
public class CodeReviewService {
    // ...
    
    // 修改方法名和实现
    public String generateCodeReview(Project project, String diff) throws Exception {
        String prompt = PromptUtil.constructCodeReviewPrompt(project, diff);
        return aiService.generateCodeReview(prompt);
    }
    
    public void generateCodeReviewStream(Project project, String diff, Consumer<String> onNext, Consumer<Throwable> onError) throws Exception {
        String prompt = PromptUtil.constructCodeReviewPrompt(project, diff);
        aiService.generateCodeReviewStream(prompt, onNext);
    }
    // ...
}
```

### 3.3 PromptUtil.java

```java
public class PromptUtil {
    // 添加新的代码质量检查提示模板
    public static final String DEFAULT_CODE_REVIEW_PROMPT = getCodeReviewPrompt();
    
    // 修改构建提示的方法
    public static String constructCodeReviewPrompt(Project project, String diff) {
        // 类似于constructPrompt方法，但针对代码质量检查
        // ...
    }
    
    // 添加代码质量检查的提示模板
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
    // ...
}
```

### 3.4 plugin.xml

```xml
<idea-plugin>
    <id>com.d10ng.aicodereview</id>
    <n>AICodeReview</n>
    <vendor email="paranoia_zk@yeah.net">HMYDK</vendor>
    <description>
    <![CDATA[
    <h3>这个插件使用AI自动生成代码质量检查报告，帮助您提高代码质量。</h3>
    
    <h3>支持的模型：</h3>
    <ul>
        <li>支持OpenAI API</li>
        <li>支持Gemini</li>
        <li>支持DeepSeek</li>
        <li>支持Ollama</li>
        <li>支持Cloudflare Workers AI</li>
        <li>支持阿里云百炼(Model Hub)</li>
        <li>支持SiliconFlow(Model Hub)</li>
    </ul>

    <h3>使用方法：</h3>
    <ul>
        <li>选择您想要检查的代码文件</li>
        <li>点击"生成AI代码质量检查报告"按钮</li>
        <li>生成的代码质量检查报告将显示在提交消息编辑器中</li>
    </ul>
    ]]>
    </description>
    
    <!-- ... -->
    
    <actions>
        <action id="AICodeReview.Generate"
                class="com.d10ng.aicodereview.GenerateCodeReviewAction"
                text="生成AI代码质量检查报告"
                icon="/icons/git-commit-logo.svg"
                description="使用AI生成代码质量检查报告">
            <add-to-group group-id="Vcs.MessageActionGroup" anchor="first"/>
        </action>
    </actions>
    
    <!-- ... -->
</idea-plugin>
```

### 3.5 Constants.java

```java
public class Constants {
    // 修改常量
    public static final String NO_FILE_SELECTED = "未选择文件";
    public static final String GENERATING_CODE_REVIEW = "正在生成代码质量检查报告...";
    public static final String TASK_TITLE_CODE_REVIEW = "生成代码质量检查报告";
    
    // 添加新常量
    public static final String PROJECT_CODE_REVIEW_PROMPT_FILE_NAME = "code-review-prompt.txt";
    
    // ...
}
```

### 3.6 AIService.java

```java
public interface AIService {
    // 添加新方法
    String generateCodeReview(String content) throws Exception;
    
    void generateCodeReviewStream(String content, Consumer<String> onNext) throws Exception;
    
    // 保留原有方法，但可能需要修改实现
    // ...
}
```

## 4. 实现步骤

1. **备份原始代码**：确保有原始代码的备份，以便在需要时参考。

2. **重命名和修改核心文件**：
   - 创建新的`GenerateCodeReviewAction.java`
   - 创建新的`CodeReviewService.java`
   - 修改`PromptUtil.java`添加代码质量检查相关的提示模板

3. **更新配置文件**：
   - 修改`plugin.xml`中的插件信息和按钮配置
   - 更新`Constants.java`中的常量

4. **修改接口和实现类**：
   - 更新`AIService.java`接口
   - 修改所有实现类以支持代码质量检查功能

5. **测试**：
   - 确保插件可以正确加载
   - 测试按钮点击功能
   - 测试代码质量检查报告生成功能

6. **更新文档**：
   - 更新README.md文件，说明插件的新功能和使用方法

## 5. 注意事项

1. 保持与原有插件的兼容性，确保用户可以平滑过渡。
2. 确保代码质量检查报告的格式适合在提交消息编辑器中显示。
3. 考虑添加配置选项，允许用户自定义代码质量检查的重点和格式。
4. 确保错误处理机制能够适当处理代码质量检查过程中可能出现的问题。

## 6. 未来扩展

1. 添加对特定编程语言的专门支持，提供更精确的代码质量检查。
2. 添加历史记录功能，允许用户查看和比较过去的代码质量检查报告。
3. 添加统计功能，帮助用户跟踪代码质量的改进情况。
4. 考虑与其他代码质量工具集成，提供更全面的代码质量评估。 