package com.d10ng.aicodereview.util;

import com.d10ng.aicodereview.constant.Constants;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

    public static String readFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();
        return content.toString();
    }

    public static String loadProjectPrompt(Project project) {
        String res = "";
        if (project != null) {
            File promptFile = new File(project.getBasePath(), Constants.PROJECT_PROMPT_FILE_NAME);
            if (promptFile.exists()) {
                try {
                    res = FileUtil.readFile(promptFile.getPath());
                } catch (Exception ex) {
                    res = "Error reading project prompt file: " + ex.getMessage();
                    throw new IllegalArgumentException(res);
                }
            } else {
                res = "No " + Constants.PROJECT_PROMPT_FILE_NAME + " file found in the project root directory : " + project.getBasePath();
                throw new IllegalArgumentException(res);
            }
        } else {
            res = "No project provided.";
            throw new IllegalArgumentException(res);
        }

        if (res.isEmpty()) {
            throw new IllegalArgumentException("No content found in the project prompt file.");
        }

        return res;
    }

    /**
     * 写入文件
     * @param project 项目
     * @param content 内容
     * @param fileName 文件名
     * @return 文件路径
     * @throws IOException IO异常
     */
    public static String writeFile(Project project, String content, String fileName) throws IOException {
        if (project == null) {
            throw new IllegalArgumentException("No project provided.");
        }
        
        String basePath = project.getBasePath();
        if (basePath == null) {
            throw new IllegalArgumentException("Project base path is null.");
        }
        
        File file = new File(basePath, fileName);
        // 确保文件操作在后台线程中执行
        if (ApplicationManager.getApplication().isDispatchThread()) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    java.io.FileWriter writer = new java.io.FileWriter(file);
                    writer.write(content);
                    writer.close();
                } catch (IOException e) {
                    // 无法在这里抛出异常，所以只能记录日志
                    e.printStackTrace();
                }
            });
        } else {
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write(content);
            writer.close();
        }
        
        return file.getAbsolutePath();
    }

    /**
     * 追加内容到文件
     * @param filePath 文件路径
     * @param content 内容
     * @throws IOException IO异常
     */
    public static void appendToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        // 确保文件操作在后台线程中执行
        if (ApplicationManager.getApplication().isDispatchThread()) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    java.io.FileWriter writer = new java.io.FileWriter(file, true);
                    writer.write(content);
                    writer.close();
                } catch (IOException e) {
                    // 无法在这里抛出异常，所以只能记录日志
                    e.printStackTrace();
                }
            });
        } else {
            java.io.FileWriter writer = new java.io.FileWriter(file, true);
            writer.write(content);
            writer.close();
        }
    }

    /**
     * 清空文件内容
     * @param filePath 文件路径
     * @throws IOException IO异常
     */
    public static void clearFile(String filePath) throws IOException {
        File file = new File(filePath);
        // 确保文件操作在后台线程中执行
        if (ApplicationManager.getApplication().isDispatchThread()) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    java.io.FileWriter writer = new java.io.FileWriter(file);
                    writer.write("");
                    writer.close();
                } catch (IOException e) {
                    // 无法在这里抛出异常，所以只能记录日志
                    e.printStackTrace();
                }
            });
        } else {
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write("");
            writer.close();
        }
    }
}
