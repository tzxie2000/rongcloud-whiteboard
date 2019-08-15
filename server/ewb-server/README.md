# 白板服务

​		白板web页面对应的后台服务，以http+json的交互方式传递数据。项目采用SpringBoot框架，SpringMVC作为视图控制层，Spring Data JPA构建数据访问，结合Spring的依赖注入和事务管理，此外还引用了jjwt用以token的生成和校验。白板中实体对应关系可分为白板、白板页、笔画、激光笔，白板是基础的承载，可以在白板中管理白板页，在白板页中管理笔画和激光笔。白板服务不包含用户体系，用户可以根据自己的需求完善权限和安全控制。

### 接口文档

​		白板服务接口请参见《白板服务接口文档.docx》

​		白板服务源码结构说明请参见《白板服务源码结构说明.docx》

​		参数说明和配置信息说明请参见《白板二次开发手册.docx》

### 配置说明

- 配置文件

  application.properties — 服务启动端口，数据库连接信息

  config.properties — 自定义配置信息

- 需要安装MySQL数据库，数据库脚本

  src\main\resources\sql\create.sql

### 注意事项

+ 包含token检验，可在config.properties中取消