# jSMTH-Android

> forked from [zfdang/zSMTH-Android](https://github.com/zfdang/zSMTH-Android)

[![Build_jSMTH_APK](https://github.com/cluefly/jSMTH-Android/workflows/Build_jSMTH_APK/badge.svg)](https://github.com/cluefly/jSMTH-Android/actions)
[![GitHub release](https://img.shields.io/github/v/release/cluefly/jSMTH-Android?color=blue&logo=github)](https://github.com/cluefly/jSMTH-Android/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-5.0%2B%20(API%2021%2B)-brightgreen?logo=android)](https://developer.android.com)

现代化、轻量优雅的水木社区 BBS 开源 Android 客户端。

<p align="center">
  <img src="jsmth.png" alt="jSMTH Banner" width="480">
</p>

---

## 🌟 项目简介

**jSMTH-Android** 是基于开源社区优秀的客户端演进维护的版本，旨在为 Android 设备提供更流畅、干净、现代化的水木社区 BBS 浏览与发帖体验。

- **仓库地址**：[https://github.com/cluefly/jSMTH-Android](https://github.com/cluefly/jSMTH-Android)
- **最新版本**：[GitHub Releases](https://github.com/cluefly/jSMTH-Android/releases)

---

## ✨ 核心特性

- 🚀 **全面适配现代 Android 环境**：适配 Android 15 / 16 (API 35/36)，支持高版本系统的通知管理与安全规范。
- 🎨 **Material Design & 深色模式**：支持一键切换日间与夜间模式，UI 布局更清晰舒心。
- 📰 **丰富内容展现**：
  - 首页十大导读、分区版面、版面收藏夹一应俱全。
  - 帖子支持多种字号调节、长图自动切片绘制防 OOM、图片手势缩放预览。
- ⚡ **无缝会话与状态维护**：
  - 支持 Webview 登录与自动会话保持，头像与用户信息无缝同步。
  - 后台消息提醒服务（新邮件、@我的消息、新回复、新点赞等）。
- 🛠️ **贴心细节**：
  - 音量键翻页支持。
  - 帖子详情页一键快捷跳转所在版面。
  - 发帖草稿缓存，防止内容丢失。

---

## 🛠️ 技术栈与依赖

本项目基于 Android 原生架构与现代开发技术构建：

| 模块 | 核心依赖 / 技术方案 | 作用 |
| :--- | :--- | :--- |
| **网络层** | `RxJava 2` + `Retrofit 2` + `OkHttp 3` | 响应式网络请求、Cookie 同步管理与缓存 |
| **图片加载** | `Fresco` (3.9+) + `PhotoView` | 内存优化图片加载、长图切片防溢出与手势缩放 |
| **异步与后台** | `AndroidX WorkManager` | 用户登录状态与后台新消息定时轮询 |
| **页面解析** | `Jsoup` | 网页端高效 HTML 节点解析与排版提取 |
| **UI 架构** | `AndroidX` + `Material Components` | 现代 Material 规范控件、侧边栏抽屉与沉浸式体验 |
| **本地存储** | `SharedPreferences` + `SQLiteAssetHelper` | 用户偏好配置、离线 IP 归属地数据库 |

---

## 🚀 构建与运行

### 开发环境要求
- **Android Studio**：Ladybug (2024.2) 或更高版本
- **Gradle**：8.13
- **JDK**：Java 17 或 Java 21
- **Compile SDK**：36
- **Min SDK**：21 (Android 5.0 Lollipop)

### 本地编译

```bash
# 克隆代码仓库
git clone https://github.com/cluefly/jSMTH-Android.git
cd jSMTH-Android

# 编译 Debug APK
./gradlew assembleDebug

# 运行单元测试
./gradlew testDebugUnitTest
```

编译产物位于 `app/build/outputs/apk/debug/` 目录下。

---

## 🤝 参与贡献

欢迎提交 Issue 反馈问题，或通过 Pull Request 参与代码改进。

---

## 📄 开源许可

本项目遵循 [Apache License 2.0](LICENSE) 开源协议。
