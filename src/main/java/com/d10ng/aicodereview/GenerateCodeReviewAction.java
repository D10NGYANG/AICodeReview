package com.d10ng.aicodereview;

import com.d10ng.aicodereview.constant.Constants;
import com.d10ng.aicodereview.service.CodeReviewService;
import com.d10ng.aicodereview.util.GItUtil;
import com.d10ng.aicodereview.util.IdeaDialogUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.ui.CommitMessage;
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Action 类，用于生成代码质量检查报告
 * 继承自 AnAction 以集成到 IDEA 的操作系统中
 */
public class GenerateCodeReviewAction extends AnAction {

    /**
     * 获取CommitMessage对象
     */
    private CommitMessage getCommitMessage(AnActionEvent e) {
        return (CommitMessage) e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL);
    }

    private final StringBuilder messageBuilder = new StringBuilder();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        // 根据配置，创建对应的服务
        CodeReviewService codeReviewService = new CodeReviewService();

        if (!codeReviewService.checkNecessaryModuleConfigIsRight()) {
            IdeaDialogUtil.handleModuleNecessaryConfigIsWrong(project);
            return;
        }

        AbstractCommitWorkflowHandler<?, ?> commitWorkflowHandler = (AbstractCommitWorkflowHandler<?, ?>) e.getData(
                VcsDataKeys.COMMIT_WORKFLOW_HANDLER);
        if (commitWorkflowHandler == null) {
            IdeaDialogUtil.handleNoChangesSelected(project);
            return;
        }

        CommitMessage commitMessage = getCommitMessage(e);

        List<Change> includedChanges = commitWorkflowHandler.getUi().getIncludedChanges();
        List<FilePath> includedUnversionedFiles = commitWorkflowHandler.getUi().getIncludedUnversionedFiles();

        if (includedChanges.isEmpty() && includedUnversionedFiles.isEmpty()) {
            commitMessage.setCommitMessage(Constants.NO_FILE_SELECTED);
            return;
        }

        commitMessage.setCommitMessage(Constants.GENERATING_CODE_REVIEW);

        // Run the time-consuming operations in a background task
        ProgressManager.getInstance().run(new Task.Backgroundable(project, Constants.TASK_TITLE_CODE_REVIEW, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    String diff = GItUtil.computeDiff(includedChanges, includedUnversionedFiles, project);
                    if (codeReviewService.generateByStream()) {
                        messageBuilder.setLength(0);
                        codeReviewService.generateCodeReviewStream(
                                project,
                                diff,
                                // onNext 处理每个token
                                token -> ApplicationManager.getApplication().invokeLater(() -> {
                                    if (messageBuilder.isEmpty()) {
                                        messageBuilder.append(token);
                                        commitMessage.setCommitMessage(token);
                                    } else {
                                        messageBuilder.append(token);
                                        commitMessage.setCommitMessage(messageBuilder.toString());
                                    }
                                }),
                                // onError 处理错误
                                error -> ApplicationManager.getApplication().invokeLater(() -> {
                                    IdeaDialogUtil.showError(project, "生成代码质量检查报告时出错: <br>" + getErrorMessage(error.getMessage()), "错误");
                                })
                        );
                    } else {
                        String codeReviewFromAi = codeReviewService.generateCodeReview(project, diff).trim();
                        ApplicationManager.getApplication().invokeLater(() -> {
                            commitMessage.setCommitMessage(codeReviewFromAi);
                        });
                    }
                } catch (IllegalArgumentException ex) {
                    IdeaDialogUtil.showWarning(project, ex.getMessage(), "AI代码质量检查警告");
                } catch (Exception ex) {
                    IdeaDialogUtil.showError(project, "生成代码质量检查报告时出错: <br>" + getErrorMessage(ex.getMessage()), "错误");
                }
            }
        });
    }

    private static @NotNull String getErrorMessage(String errorMessage) {
        if (errorMessage.contains("429")) {
            errorMessage = "请求过多。请稍后再试。";
        } else if (errorMessage.contains("Read timeout") || errorMessage.contains("Timeout") || errorMessage.contains("timed out")) {
            errorMessage = "读取超时。请稍后再试。<br> " +
                    "这可能是由API密钥或网络问题或服务器繁忙引起的。";
        } else if (errorMessage.contains("400")) {
            errorMessage = "错误请求。请稍后再试。";
        } else if (errorMessage.contains("401")) {
            errorMessage = "未授权。请检查您的API密钥。";
        } else if (errorMessage.contains("403")) {
            errorMessage = "禁止访问。请检查您的API密钥。";
        } else if (errorMessage.contains("404")) {
            errorMessage = "未找到。请检查您的API密钥。";
        } else if (errorMessage.contains("500")) {
            errorMessage = "服务器内部错误。请稍后再试。";
        } else if (errorMessage.contains("502")) {
            errorMessage = "网关错误。请稍后再试。";
        } else if (errorMessage.contains("503")) {
            errorMessage = "服务不可用。请稍后再试。";
        } else if (errorMessage.contains("504")) {
            errorMessage = "网关超时。请稍后再试。";
        }
        return errorMessage;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 控制 Action 的启用/禁用状态
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        // 指定在后台线程更新 Action 状态，提高性能
        return ActionUpdateThread.BGT;
    }
} 