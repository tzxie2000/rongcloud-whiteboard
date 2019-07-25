# 白板文件服务

​	支持image(png、jpg、jpeg、bmp、gif), pdf, ppt/pptx, doc/docx, xls/xlsx, txt文件的上传，并将文件转换成图片格式后，存到fastdfs上返回。

### 接口文档

​	白板中文件上传和删除的接口请参见《白板文件服务接口文档.docx》

### 配置说明

​	application.properties — 服务启动端口，文件上传大小限制

​	config.properties — 自定义配置信息

​	fdfs_client.conf — fastdfs配置信息

### 注意事项

- 引用了fastdfs, jodconverter, LibreOffice, poi及相关组件，需要安装fastdfs, LibreOffice

  ​	fastdfs: https://github.com/happyfish100/fastdfs-client-java.git

  ​	jodconverter: https://github.com/mirkonasato/jodconverter.git

- 包含token检验，可在config.properties中取消