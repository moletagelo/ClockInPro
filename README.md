# ClockInPro

<div align="center">
  <img src="https://img.shields.io/badge/Android-6.0%2B-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Android 6.0+">
  <img src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin 2.0">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat-square" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/License-Apache%202.0-F4B400?style=flat-square" alt="Apache 2.0">
  <img src="https://img.shields.io/badge/Build-GitHub%20Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white" alt="GitHub Actions">
</div>

<p align="center">
  一个面向个人日常习惯与目标追踪的 Android 打卡应用。<br>
  用尽可能低的操作阻力，帮助你持续完成每天想坚持的事情。
</p>

<p align="center">
  <a href="https://github.com/moletagelo/ClockInPro/releases">下载 APK</a>
  ·
  <a href="https://github.com/moletagelo/ClockInPro/issues">报告问题</a>
  ·
  <a href="https://github.com/moletagelo/ClockInPro/actions">查看构建</a>
</p>

## 项目简介

ClockInPro 是一个本地优先的 Android 打卡应用，适合记录日常习惯、工作到岗、阅读、健身或任何需要长期坚持的目标。

它当前聚焦于 4 件事：

- 让“完成今天”这件事足够快
- 用连续记录和历史回顾强化正反馈
- 不依赖账号系统也能正常使用
- 在需要时提供可导出的本地备份

## 当前状态

本项目处于持续迭代阶段，主流程已经可用：

- `v2` 首页、目标详情、设置页和引导页已接入主导航
- GitHub Actions 会自动构建并发布 `release APK`
- 已提供 English / 简体中文 资源
- 本地 JSON 备份与目标提醒链路已经接入

当前仍在持续完善的部分：

- 应用内语言切换体验仍在优化中
- `release` 构建目前暂时关闭了混淆和资源压缩，以优先保证安装与启动稳定性
- 仓库内还保留了一部分旧版目录，后续会继续清理

## 核心功能

- 自定义目标：为工作、阅读、健身、健康或任意日常习惯创建打卡目标
- 一键打卡：在首页快速完成当天记录，并支持撤销刚刚的操作
- 连续记录：查看当前连续天数、累计完成次数和最近打卡历史
- 月历视图：在目标详情中回看每月完成情况
- 每日提醒：可为单个目标设置本地通知提醒，并在设备重启后自动恢复
- JSON 备份：支持导出和导入本地备份文件
- 双语界面：提供 English / 简体中文 资源，并支持跟随系统语言

## 主要界面

- 引导页：介绍无账号、本地优先和快速打卡体验
- 首页：展示全部目标、今日状态和核心操作入口
- 目标详情：展示提醒时间、连续天数、累计完成次数、月历和最近记录
- 设置页：管理备份导入导出、语言和提醒说明

## 技术栈

| 模块 | 方案 |
| --- | --- |
| UI | Jetpack Compose + Material 3 |
| 导航 | Navigation Compose |
| 架构 | MVVM |
| 依赖注入 | Hilt |
| 本地存储 | Room |
| 偏好设置 | DataStore |
| 后台任务 | WorkManager |
| 提醒调度 | AlarmManager |
| 通知 | NotificationManager + BroadcastReceiver |
| 多语言 | AppCompat per-app locales |
| JSON 备份 | Gson |

## 项目结构

```text
app/src/main/java/com/clockinpro/
├── data/               # 旧版数据层代码
├── di/                 # Hilt 依赖注入配置
├── domain/             # 旧版领域模型
├── ui/                 # 应用入口、主题、导航
├── util/               # 语言切换等通用工具
├── v2/
│   ├── data/           # 当前版本的数据、备份与本地存储
│   ├── domain/         # 当前版本领域模型
│   ├── reminder/       # 目标提醒调度
│   └── ui/             # onboarding / home / detail / settings
└── worker/             # 通知接收、开机恢复提醒等组件
```

## 数据与隐私

- 默认不需要注册或登录账号
- 数据默认保存在当前设备本地
- 只有当你手动导出时，才会生成 JSON 备份文件
- 当前版本没有云同步服务

## 系统要求

- 最低 Android 版本：Android 6.0 (API 23)
- 目标 Android 版本：Android 14 (API 34)
- JDK：17
- Android SDK：34

## 应用权限

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

权限说明：

- `POST_NOTIFICATIONS`：用于发送目标提醒通知
- `VIBRATE`：用于提醒震动反馈
- `RECEIVE_BOOT_COMPLETED`：用于设备重启后恢复提醒

## 快速开始

1. 克隆仓库

```bash
git clone https://github.com/moletagelo/ClockInPro.git
cd ClockInPro
```

2. 配置环境

```powershell
$env:JAVA_HOME="C:\path\to\jdk-17"
$env:ANDROID_HOME="C:\path\to\android-sdk"
```

3. 构建调试包

```bash
./gradlew assembleDebug
```

4. 构建正式包

```bash
./gradlew assembleRelease
```

5. 安装 APK

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
# 或
adb install app/build/outputs/apk/release/app-release.apk
```

## Release 流程

- GitHub Actions 会在 `main` 分支推送后自动构建并发布 `release APK`
- `release` 签名通过 GitHub Secrets 注入，不会把 keystore 或密码提交到仓库
- 本地 `keystore.properties`、keystore 文件、构建产物和设计文档均已被 `.gitignore` 忽略

当前 CI 使用的 Secrets：

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_PASSWORD`

## 开发说明

- 当前主流程使用 `v2` 目录下的界面和数据实现
- 仓库中仍保留部分旧版目录，便于迁移参考
- 默认资源文件为 English，`values-zh-rCN` 提供简体中文界面
- `release` 构建当前优先保证可安装和可启动，尚未重新打开压缩优化链路

## Roadmap

- 稳定应用内语言切换体验
- 重新启用并验证 `release` 混淆 / 资源压缩
- 继续清理旧版模块与迁移遗留代码
- 完善测试与发布前校验流程

## Contributing

欢迎通过以下方式参与项目：

- 提交 Issue 反馈 bug 或体验问题
- 提交 Pull Request 改进功能、修复问题或完善文档
- 在 release APK 上反馈真实设备兼容性情况

如果你准备提交代码，建议先：

1. 从最新 `main` 分支拉取代码
2. 在本地完成构建和基础验证
3. 清楚说明改动范围和测试结果

## License

本项目基于 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源，详见 [LICENSE](./LICENSE)。
