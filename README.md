<div align="center">
    <a href="https://github.com/D10NGYANG/AICodeReview">
        <img src="./src/main/resources/icons/code-review-logo.svg" width="200" height="200" alt="logo"/>
    </a>
</div>
<h1 align="center">AI Code Review</h1>

<p align="center">
<a href="https://github.com/D10NGYANG/AICodeReview"><img src="https://img.shields.io/badge/IntelliJ-Plugin-blue.svg?style=flat-square"></a>
</p>
<br>

> 本项目是从 [HMYDK/AIGitCommit](https://github.com/HMYDK/AIGitCommit) 项目 fork 而来，在原有生成提交日志的基础上改造为生成代码质量检查报告。特此感谢原作者的杰出工作！

## 描述

这个插件使用AI自动生成代码质量检查报告，帮助您提高代码质量。

## 功能特点

- 支持配置多种"提示词"模板
  - 简洁版代码审查（默认）
  - 标准代码审查
  - 详细代码审查
  - 安全性审查
- 支持项目级别的个性化配置
  - 使用项目根目录下的`code-review-prompt.txt`文件中的提示词
- 支持多种语言输出
- 支持流式生成，实时查看生成结果

## 支持的模型

- Gemini
- Ollama
- Cloudflare Workers AI(Model Hub)
- 阿里云百炼(Model Hub)
- SiliconFlow(Model Hub)
- OpenAI API
- DeepSeek-v3

## 使用方法

1. 选择您想要检查的代码文件
2. 点击"生成AI代码质量检查报告"按钮
3. 生成的代码质量检查报告将显示在提交消息编辑器中

## 自定义提示词

您可以在设置中添加、编辑和删除自定义提示词，以满足不同的代码审查需求。您也可以在[讨论区](https://github.com/D10NGYANG/AICodeReview/discussions)分享和查找更多关于自定义提示词的信息。

## 安装

1. 在IntelliJ IDEA中，转到`设置` -> `插件`
2. 点击`从磁盘安装插件`
3. 选择下载的插件文件
4. 重启IDE

## 贡献

欢迎提交问题和改进建议！请访问我们的[GitHub仓库](https://github.com/D10NGYANG/AICodeReview)。

## 致谢

- 特别感谢 [HMYDK](https://github.com/HMYDK) 创建的 [AIGitCommit](https://github.com/HMYDK/AIGitCommit) 项目，本项目是在其基础上进行改造的
- 感谢所有为原项目做出贡献的开发者
- 感谢各大AI模型提供商提供的API支持
