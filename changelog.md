# 更新日志

## v1.3
- 新增 WebUI：分身数量可随意修改（1~999，默认 99），即时生效无需重启
- 静态 overlay 保底值提升至 999
- 开机脚本按配置值重新拉起全套限制（fw.max_users + 双 fabrication）
- 自动维护 vfs 交付 overlay APK 所需的 SELinux 标签

## v1.2
- 新增 xml/config_user_types 覆盖：MANAGED 每父用户上限 1 → 99（"Cannot add more profiles of type MANAGED" 报错的真正拦截点）
- 修复 SELinux 标签问题，使 PackageManager 能解析 vfs 交付的 overlay APK

## v1.1
- 首个公开版本：RRO overlay（用户总数/同时运行数）+ fw.max_users 属性 + 运行时 fabrication
