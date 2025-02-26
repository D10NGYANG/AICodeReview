package com.d10ng.aicodereview;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WelcomeNotification implements ProjectActivity {

    private static final String NOTIFICATION_GROUP_ID = "AI Code Review Notifications";
    private static final String PLUGIN_NAME = "AI Code Review";
    private static final String WELCOME_TITLE = "欢迎使用 AI Code Review!";
    private static final String WELCOME_CONTENT = "感谢您安装 AI Code Review。" +
            "要开始使用，请在设置中配置插件。";
    private static final String PLUGIN_VERSION_PROPERTY = "com.d10ng.aicodereview.version";

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        if (isNewOrUpdatedVersion()) {
            showWelcomeNotification(project);
            updateStoredVersion();
        }
        return Unit.INSTANCE;
    }

    private boolean isNewOrUpdatedVersion() {
        String storedVersion = PropertiesComponent.getInstance().getValue(PLUGIN_VERSION_PROPERTY);
        String currentVersion = getCurrentPluginVersion();
        return storedVersion == null || !storedVersion.equals(currentVersion);
    }

    private void updateStoredVersion() {
        PropertiesComponent.getInstance().setValue(PLUGIN_VERSION_PROPERTY, getCurrentPluginVersion());
    }

    private String getCurrentPluginVersion() {
        return PluginManagerCore.getPlugin(PluginId.getId("com.d10ng.aicodereview")).getVersion();
    }

    private void showWelcomeNotification(@NotNull Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(NOTIFICATION_GROUP_ID)
                .createNotification(WELCOME_TITLE, WELCOME_CONTENT, NotificationType.INFORMATION)
                .setIcon(null) // You can set a custom icon here if you have one
                .addAction(new ConfigureAction())
                .notify(project);
    }

    private static class ConfigureAction extends com.intellij.openapi.actionSystem.AnAction {
        ConfigureAction() {
            super("配置");
        }

        @Override
        public void actionPerformed(@NotNull com.intellij.openapi.actionSystem.AnActionEvent e) {
            com.intellij.openapi.options.ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), PLUGIN_NAME);
        }
    }
}