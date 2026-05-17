# ClockInPro - Android 打卡应用

<div align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0.0-purple?style=flat-square&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Android-6.0+-green?style=flat-square&logo=android" alt="Android">
  <img src="https://img.shields.io/badge/Compose-BOM-blue?style=flat-square" alt="Compose">
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="License">
</div>

<p align="center">
  一款简洁高效的安卓打卡应用，支持签到签退、GPS定位、拍照记录、统计报表等功能。
</p>

<p align="center">
  <a href="https://github.com/moletagelo/ClockInPro/releases">📦 下载APK</a>
  ·
  <a href="https://github.com/moletagelo/ClockInPro/issues">🐛 报告问题</a>
  ·
  <a href="https://github.com/moletagelo/ClockInPro/pulls">🔧 贡献代码</a>
</p>

---

## 📱 功能特性

### 🔐 用户模块
- 手机号 + 验证码注册
- 手机号 + 密码登录
- 忘记密码（验证码重置）
- 个人资料编辑（头像、昵称）

### ⏰ 打卡模块
- **签到打卡** - 记录到达时间
- **签退打卡** - 记录离开时间
- **GPS定位** - 自动获取当前位置
- **拍照附件** - 支持打卡拍照
- **文字备注** - 记录打卡说明
- **状态指示** - 实时显示打卡状态

### 📊 记录模块
- **日历视图** - 月度打卡日历
- **打卡详情** - 查看每次打卡信息
- **统计报表** - 连续打卡天数、本月统计
- **出勤率** - 自动计算出勤比例

### 🔔 提醒模块
- 自定义打卡提醒时间
- 工作日/每天重复设置
- 本地通知推送

### ☁️ 数据同步
- 离线数据缓存
- 同步状态显示
- 数据安全存储

---

## 🛠 技术架构

| 层级 | 技术选型 | 说明 |
|------|----------|------|
| **UI** | Jetpack Compose + Material 3 | 现代声明式UI |
| **架构** | MVVM + Clean Architecture | 清晰分层架构 |
| **DI** | Hilt | 依赖注入 |
| **数据库** | Room + DataStore | 本地持久化 |
| **网络** | Retrofit + OkHttp | API通信 |
| **定位** | Google FusedLocationProvider | GPS定位 |
| **语言** | Kotlin 2.0 | 主力开发语言 |

### 项目结构

```
app/src/main/java/com/clockinpro/
├── ClockInApp.kt              # Application类
├── di/                        # Hilt依赖注入模块
│   └── DatabaseModule.kt
├── data/
│   ├── local/                 # Room数据库
│   │   ├── AppDatabase.kt    # 数据库配置
│   │   ├── UserDao.kt        # 用户数据访问
│   │   ├── CheckRecordDao.kt # 打卡记录数据访问
│   │   └── ReminderDao.kt    # 提醒数据访问
│   └── repository/            # 数据仓库
│       ├── UserRepository.kt
│       ├── CheckRecordRepository.kt
│       └── ReminderRepository.kt
├── domain/
│   └── model/                 # 领域模型
│       ├── User.kt
│       ├── CheckRecord.kt
│       └── Reminder.kt
├── ui/
│   ├── MainActivity.kt        # 主入口
│   ├── theme/                 # Compose主题
│   ├── components/            # 通用组件
│   ├── navigation/            # 导航管理
│   ├── auth/                  # 认证页面
│   ├── home/                  # 首页
│   ├── checkin/               # 打卡页面
│   ├── record/                # 记录页面
│   └── profile/               # 个人中心
├── util/                      # 工具类
│   └── SecurityUtil.kt
└── worker/                    # 广播接收器
    ├── ReminderReceiver.kt
    └── BootReceiver.kt
```

---

## 📋 系统要求

- **最低Android版本**: Android 6.0 (API 23)
- **推荐Android版本**: Android 8.0+ (API 26+)
- **目标Android版本**: Android 14 (API 34)

### 所需权限

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
```

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Android SDK 34
- Gradle 8.5+

### 构建步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/moletagelo/ClockInPro.git
   cd ClockInPro
   ```

2. **配置环境变量**
   ```bash
   export JAVA_HOME=/path/to/jdk17
   export ANDROID_HOME=/path/to/android-sdk
   ```

3. **构建Debug版本**
   ```bash
   ./gradlew assembleDebug
   ```

4. **构建Release版本**
   ```bash
   ./gradlew assembleRelease
   ```

5. **安装APK**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   # 或
   adb install app/build/outputs/apk/release/app-release.apk
   ```

---

## 📁 APK下载

| 版本 | 类型 | 下载链接 | 说明 |
|------|------|----------|------|
| v1.0 | Release | [点击下载](https://github.com/moletagelo/ClockInPro/releases) | 正式发布版 |

---

## 🎨 界面预览

### 首页
- 欢迎卡片
- 打卡统计（连续天数/总次数）
- 今日打卡记录
- 快捷操作入口

### 打卡页
- 当前时间显示
- GPS位置信息
- 拍照/备注功能
- 大按钮签到/签退

### 记录页
- 月度日历视图
- 打卡天数标记
- 月度统计数据
- 打卡时间轴

---

## 🔒 安全特性

- ✅ 密码SHA-256加密存储
- ✅ HTTPS安全通信
- ✅ GPS坐标防伪造检测
- ✅ 设备指纹绑定

---

## ⚠️ 注意事项

1. **首次使用**需要授予定位和相机权限
2. **Android 13+**需要授予通知权限才能接收打卡提醒
3. **离线模式**下数据会本地缓存，联网后自动同步
4. **定位功能**需要GPS开启才能正常使用

---

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源许可。

---

## 🙏 致谢

- [Jetpack Compose](https://developer.android.com/compose) - 现代Android UI工具包
- [Material Design 3](https://m3.material.io/) - Material设计语言
- [Hilt](https://dagger.dev/hilt/) - 依赖注入框架
- [Room](https://developer.android.com/room) - 数据库解决方案

---

<div align="center">
  <p>如果你觉得这个项目有帮助，请给我一个 ⭐️</p>
  <p>Made with ❤️ by <a href="https://github.com/moletagelo">moletagelo</a></p>
</div>
