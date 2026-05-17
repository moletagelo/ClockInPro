# 安卓打卡软件 - 项目规格说明书

## 1. 项目概述

**项目名称**：ClockInPro（打卡助手）
**项目类型**：Android原生应用
**核心功能**：企业考勤、学习打卡、习惯养成的签到签退管理与统计

## 2. 技术栈与框架

| 层级 | 技术选型 |
|------|----------|
| 开发语言 | Kotlin |
| 最低SDK | Android 6.0 (API 23) |
| 目标SDK | Android 14 (API 34) |
| UI框架 | Jetpack Compose + Material Design 3 |
| 本地存储 | Room Database + DataStore Preferences |
| 网络请求 | Retrofit 2.9.0 + OkHttp 4.12.0 |
| 定位服务 | Android FusedLocationProvider (高德SDK备选) |
| 依赖注入 | Hilt 2.48 |
| 异步处理 | Kotlin Coroutines + Flow |
| 图片加载 | Coil 2.5.0 |
| 推送服务 | Firebase Cloud Messaging (可选) |
| 架构模式 | MVVM + Clean Architecture |

## 3. 功能列表

### 3.1 用户模块
- 手机号 + 验证码注册
- 手机号 + 密码登录
- 忘记密码（验证码重置）
- 个人资料编辑（头像、昵称、手机号）
- 退出登录

### 3.2 打卡模块
- 签到打卡（记录时间 + GPS坐标）
- 签退打卡
- 拍照附件上传
- 文字备注
- 当前位置显示
- 打卡状态指示（已签到/已签退）

### 3.3 记录模块
- 今日打卡记录展示
- 日历视图（月视图）
- 按日期查看打卡详情
- 连续打卡天数统计
- 本月打卡次数统计
- 打卡时间轴展示

### 3.4 提醒模块
- 自定义每日打卡提醒时间
- 提醒开关控制
- 本地通知推送
- 重复提醒设置（工作日/每天）

### 3.5 数据同步
- 离线打卡数据缓存
- 网络恢复后自动上传
- 同步状态显示
- 手动刷新同步

## 4. UI/UX 设计方向

### 4.1 整体视觉风格
- Material Design 3 设计语言
- 简洁现代的卡片式布局
- 圆润的组件边角
- 清晰的状态反馈

### 4.2 颜色方案
- 主色调：蓝色 (#2196F3) - 代表专业、信任
- 辅助色：绿色 (#4CAF50) - 代表成功、打卡完成
- 强调色：橙色 (#FF9800) - 代表提醒
- 背景色：浅灰 (#F5F5F5) 配合白色卡片
- 深色模式支持

### 4.3 布局方案
- 底部导航栏（首页/打卡/记录/我的）
- 首页：今日打卡卡片 + 快捷操作
- 打卡页：大按钮点击打卡 + 拍照/备注入口
- 记录页：日历视图 + 统计数据
- 我的页：个人资料 + 设置

### 4.4 交互动效
- 打卡成功：庆祝动画 + 震动反馈
- 页面切换：平滑过渡动画
- 加载状态：骨架屏/进度指示器
- 下拉刷新：Material pull-to-refresh

## 5. 数据库设计

### 5.1 user表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER PRIMARY KEY | 用户ID |
| phone | TEXT UNIQUE | 手机号 |
| password_hash | TEXT | 密码哈希 |
| nickname | TEXT | 昵称 |
| avatar_url | TEXT | 头像URL |
| created_at | INTEGER | 注册时间戳 |
| updated_at | INTEGER | 更新时间戳 |

### 5.2 check_record表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER PRIMARY KEY | 记录ID |
| user_id | INTEGER | 用户ID |
| type | TEXT | check_in / check_out |
| timestamp | INTEGER | 打卡时间戳 |
| latitude | REAL | 纬度 |
| longitude | REAL | 经度 |
| address | TEXT | 地址描述 |
| photo_url | TEXT | 照片URL |
| remark | TEXT | 备注 |
| sync_status | TEXT | synced / pending / failed |
| created_at | INTEGER | 创建时间戳 |

### 5.3 reminder表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER PRIMARY KEY | 提醒ID |
| user_id | INTEGER | 用户ID |
| time_hour | INTEGER | 提醒小时 |
| time_minute | INTEGER | 提醒分钟 |
| repeat_type | TEXT | daily / weekday |
| is_enabled | INTEGER | 是否启用 |
| created_at | INTEGER | 创建时间戳 |

## 6. 非功能性需求

### 6.1 性能指标
- 冷启动时间 < 2秒
- 打卡操作响应 < 1秒
- 列表滑动流畅度 60fps
- 应用包大小 < 30MB

### 6.2 安全性
- 密码使用 BCrypt 加密存储
- HTTPS 通信加密
- GPS 坐标防伪造检测
- 设备指纹绑定

### 6.3 兼容性
- Android 6.0 (API 23) 及以上
- 主流屏幕分辨率适配
- 横竖屏适配
- 深色模式支持

### 6.4 可访问性
- 支持 TalkBack 屏幕阅读
- 内容描述完整
- 触摸目标大小 ≥ 48dp

## 7. 项目结构

```
app/
├── src/main/
│   ├── java/com/clockinpro/
│   │   ├── di/                    # 依赖注入模块
│   │   ├── data/
│   │   │   ├── local/             # Room数据库
│   │   │   ├── remote/            # Retrofit API
│   │   │   └── repository/        # 数据仓库
│   │   ├── domain/
│   │   │   ├── model/             # 领域模型
│   │   │   └── usecase/           # 用例
│   │   ├── ui/
│   │   │   ├── theme/             # Compose主题
│   │   │   ├── components/        # 通用组件
│   │   │   ├── navigation/        # 导航
│   │   │   ├── auth/              # 认证页面
│   │   │   ├── home/              # 首页
│   │   │   ├── checkin/           # 打卡页面
│   │   │   ├── record/            # 记录页面
│   │   │   └── profile/          # 个人中心
│   │   ├── util/                  # 工具类
│   │   └── worker/                # 后台任务
│   └── res/
└── build.gradle.kts
```
