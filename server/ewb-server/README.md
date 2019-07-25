# 白板服务

​	白板web页面对应的后台服务，以http+json的交互方式传递数据。

### 接口文档

​	白板创建和销毁的接口请参见《白板服务接口文档.docx》

### 配置说明

- 配置文件

  application.properties — 服务启动端口，数据库连接信息

  config.properties — 自定义配置信息

- 需要安装MySQL数据库，数据库脚本

  src\main\resources\sql\create.sql

### 注意事项

+ 包含token检验，可在config.properties中取消