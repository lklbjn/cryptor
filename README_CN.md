# Cryptor - IntelliJ IDEA 加密货币价格追踪插件

Cryptor 是一个 IntelliJ IDEA 插件，让您可以直接在 IDE 中追踪加密货币价格。它提供实时价格更新、可自定义的刷新间隔以及多币种支持。

## 功能特点

- 实时加密货币价格追踪
- 可自定义刷新间隔
- 支持多种货币
- 收藏币种管理
- 价格变动追踪（24小时和7天）
- 自定义不同货币的价格显示
- 自动价格更新
- 直观的工具栏界面

## 安装方法

1. 打开 IntelliJ IDEA
2. 进入设置/首选项
3. 导航到插件页面
4. 点击 Marketplace 标签
5. 搜索 "Cryptor"
6. 点击安装
7. 重启 IntelliJ IDEA

## 使用方法

1. 安装后，您可以在 IDE 右侧找到名为 "Cryptor" 的工具窗口
2. 使用工具栏按钮可以：
   - 开始/停止自动价格刷新
   - 添加收藏币种
   - 管理收藏币种
   - 手动刷新价格
3. 通过以下方式配置设置：
   - 设置 → 工具 → Cryptor 设置
   - 自定义刷新间隔
   - 设置首选价格显示货币

## 系统要求

- IntelliJ IDEA 2023.2 或更高版本
- Java 17 或更高版本

## 开发相关

### 从源码构建

1. 克隆仓库
2. 在 IntelliJ IDEA 中打开项目
3. 运行 `./gradlew build` 构建插件
4. 构建好的插件将位于 `build/distributions/` 目录

### 开发模式运行

1. 在 IntelliJ IDEA 中打开项目
2. 运行 `runIde` Gradle 任务
3. 将启动一个安装了插件的新 IDE 实例

## 参与贡献

欢迎贡献代码！请随时提交 Pull Request。

## 许可证

本项目采用 MIT 许可证 - 详见 LICENSE 文件。 