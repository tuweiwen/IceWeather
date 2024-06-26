# IceWeather | 冰棒天气

一个大量使用 Jetpack 组件的开源天气项目，简洁~~无打扰~~

## 版本历史

### 1.1.1

1. 增加了12小时天气的折线图
2. 优化了UI的显示效果
    - 各栏目的间距增加
    - 英文环境下，增加12小时天气的项目之间的间距
3. 对项目中的代码进行了一些整理
    - 对业务进行了提前的分包
    - 将高德 SDK 需要使用的 SHA1 代码修改为 Kotlin
    - 去除了 AdMob 加载失败情况下弹出的 Toast

### 1.0.0

1. 无广告，快速启动 ~~(虽然未来会有广告)~~
2. ~~仅~~支持定位获取当地天气
3. 支持多语言（简中/繁中/英）

## 功能排期表

1. “关于” 页面
    - 显示开发者相关信息
    - 快速跳转邮箱应用提交反馈&建议
    - 展示 “隐私政策”， “所使用 SDK 列表” 及 “开源协议”
2. 开屏页（Splash Screen）
    - ~~展示冰棒天气 logo~~
    - 需要优化，在低于 SDK 31的设备上仍然没有
3. ~~首次启动隐私协议展示~~(已完成)
4. 上架 AppGallery | GetApps | Play Store
5. 个性化设置
    - 华氏度数据展示
6. 天气优化
    - 手动搜索天气
    - 多城市
7. 消息推送
8. 广告（Cover 掉彩云 API 的成本）
    - HMS 广告
    - AdMob 广告
    - 付费去除广告
9. 隐藏各 SDK 的 ApiKEY
10. 优化定位速度，考虑使用系统提供的定位功能
11. 适配 “小白条”
12. 适配 “预测性返回手势”
13. 适配宽屏设备（平板 / 折叠屏）
14. 主页各板块导航改为使用 Jetpack Navigation