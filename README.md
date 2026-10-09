# UnlimitedProfiles Overlay

> 作者：林小冉 ｜ 测试设备：Redmi K70 ｜ 系统：澎湃OS 3.x（Android 16）

解除安卓多用户/分身数量限制 —— 纯系统原生机制，无 Xposed、无 hook、无性能损耗。

## 功能

- **分身数量上限可随意修改**：默认 99，范围 1~999，在原生设置界面里改，**即时生效、无需重启**
- 一次放开三处限制：
  - `config_multiuserMaximumUsers`（用户总数）
  - `config_multiuserMaxRunningUsers`（可同时运行数）
  - `xml/config_user_types` 中 MANAGED 分身"每父用户上限"（1 → 999，这是 "Cannot add more profiles of type MANAGED" 报错的真正拦截点）
- 三层冗余保底：静态 RRO overlay（保底 999）+ 运行时 fabrication（动态值，优先级更高）+ `fw.max_users` 系统属性
- 桌面图标可显示/隐藏（默认隐藏），从 KSU 管理器 → 模块 → 操作按钮进入

## 使用

1. 需要 KernelSU / SukiSU / APatch 等支持 KSU 模块规范的 root；
2. KSU 管理器刷入模块 zip；
3. 安装 `UnlimitedProfiles.apk`（从 [Releases](../../releases) 下载）；
4. KSU 管理器 → 模块 → UnlimitedProfiles Overlay → 操作按钮（⚙️）进入设置界面，修改数量即可。

## 适配说明

- 所改资源均为 AOSP 标准资源，其他保留多用户功能的 ROM 理论可用
- 小米设备若模块 system 挂载被阉割（如 SukiSU 某些配置），需配合 Hybrid Mount（元模块）的 vfs 规则；标准挂载环境无任何额外依赖

## License

[MIT](LICENSE)
