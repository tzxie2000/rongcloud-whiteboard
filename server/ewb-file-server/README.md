# 白板文件服务

​		白板文件服务支持image(png、jpg、jpeg、bmp、gif), pdf, ppt/pptx, doc/docx, xls/xlsx, txt文件的上传，并将文件转换成图片格式后，存到fastdfs上返回。项目采用SpringBoot框架，引用了fastdfs作为文件存储, 引用了jodconverter, LibreOffice, sejda-icepdf, poi及相关组件用以对文件进行转换操作，此外还引用了jjwt用以token的生成和校验。白板文件服务不包含用户体系，用户可以根据自己的需求完善权限和安全控制。

### 接口文档

​		白板文件服务接口请参见《白板文件服务接口文档.docx》
​		白板文件服务源码结构说明请参见《白板文件服务源码结构说明.docx》

### 配置说明

​		application.properties — 服务启动端口，文件上传大小限制

​		config.properties — 自定义配置信息

​		fdfs_client.conf — fastdfs配置信息

### 注意事项

- 引用了fastdfs, jodconverter, LibreOffice, sejda-icepdf, poi及相关组件，需要安装fastdfs, LibreOffice

  ​	fastdfs: https://github.com/happyfish100/fastdfs-client-java.git

  ​	jodconverter: https://github.com/mirkonasato/jodconverter.git

- 包含token检验，可在config.properties中取消