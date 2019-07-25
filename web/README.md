### 启动文件

> blink-wb.html

### 配置说明

`配置文件:` src/config.js

```js
var global_config = {
  server_path : 'http://10.xx.xx.xx:9500', // 白板服务地址
  file_server_path : 'http://10.xx.xx.xx:9300', // 白板文件服务地址
  width : 1280, // 白板宽度
  height : 960, // 白板高度
  seal_class_server_path: 'https://xx.xx.xx/api/v1' // sealmeeting server 地址
};
```

SealMeeting Server 见: [https://github.com/rongcloud/sealmeeting-server](https://github.com/rongcloud/sealmeeting-server)

### 注意事项

1. 白板 Web 需配置至白板 Server 中
2. 应用层需调用白板 Server 接口获取白板 Web 地址并展示