### 启动文件

> blink-wb.html

### 配置说明

`配置文件:` src/config.js

```js
var global_config = {
  server_path : 'http://10.xx.xx.xx:9500', // 白板服务地址
  file_server_path : 'http://10.xx.xx.xx:9300', // 白板文件服务地址
  width : 1280, // 白板宽度
  height : 960 // 白板高度
};
```

### 注意事项

1. 白板 Web 需配置至白板 Server 中
2. 应用层需调用白板 Server 接口获取白板 Web 地址并展示

### 功能汇总

| 功能 | 代码位置 | 渲染组件 | 调用方法名 |
| :--: | :--: | :---: | :----: |
| 上传文件/图片 | js/ewb/App.js | upload | uploadFileNew |
| 创建新页面 | js/ewb/App.js | newPage | selectNewPage |
| 预览所有页面 | js/ewb/App.js | previewPages | selectPreviewPages |
| 放大页面 | js/ewb/App.js | zoomInButton | zoomInPane |
| 缩小页面 | js/ewb/App.js | zoomOutButton | zoomOutPane |
| 选择颜色 | js/ewb/App.js | colorPicker | colorItemSelected |
| 选择线宽 | js/ewb/App.js | lineWidthPicker | setLineWidth |
| 选择铅笔 | js/ewb/App.js | pencilButton | selectPen |
| 选择毛笔 | js/ewb/App.js | highlighter | selectHighlighter |
| 添加文字 | js/ewb/App.js | addTextButton | addText |
| 选择形状 | js/ewb/App.js | penPicker | drawRectangle、drawSquare ... |
| 激光笔 | js/ewb/App.js | laserPen | selectLaserPen |
| 选择操作 | js/ewb/App.js | selectButton | doSelect |
| 撤回 | js/ewb/App.js | redoButton | redoPath |
| 反撤回 | js/ewb/App.js | undoButton | undoPath |
| 橡皮擦 | js/ewb/App.js | eraser | selectEraser |
| 清空页面 | js/ewb/App.js | clear | selectClear |
| 删除页面 | js/ewb/App.js | app_deletePage | app_deletePage |
| 开始写画 | js/ewb/App.js | canvasContainer | touchstart |
| 写画移动 | js/ewb/App.js | canvasContainer | touchmove |
| 写画结束 | js/ewb/App.js | canvasContainer | touchend |

### 写画传输

**以钢笔写画为例:**

1、本地写画

本地写画分为三步:

(1) 开始写画(touchstart), 定义起始位置. touchstart(App.js) -> startPath(Svg.js) -> drawPath2(Svg.js)

(2) 写画移动(touchmove), 记录移动位置, 展示本地写画. touchmove(App.js) -> continuePath(Svg.js) -> drawPath2(Svg.js)

(3) 结束写画(touchend), 记录结束位置, 将本地写画通过 server 传输给其他端. touchend(App.js) -> endPath(Svg.js) -> drawPath2(Svg.js) -> sendPath(Connection.js) -> createPrint(blinkEwb.js)

2、写画传输

通过 blinkEwb.js 的 createPrint 方法, 调用 server 接口传输数据

```js
{
  url: 'https://{path}/ewb/print/create', // 请求 url
  data: {
    roomKey : 'roomKey', // 房间号
    pageId : 'pageId', // 白板页 id
    userId : 'userId', // 用户 id
    data : [
      {
        drawingItem: 'pen', // 画笔类型
        guid: 'path-260e1f06-f4e6-cc25-973b-30e7e2401595', // 生成的 path id
        oldx: '636.9668246445498', // 起始位置 x 坐标
        oldy: '517.5355450236966', // 起始位置 y 坐标
        lineColor: 'black', // 颜色
        lineWidth: '3px', // 画笔宽度
        path: [ // 所有画笔路径, 其他端接收后, 根据此参数做渲染
          [636.9668246445498, 517.5355450236966],
          [995.2606635071089, 885.781990521327],
          [1413.2701421800946, 1064.9289099526065]
        ],
        type: 'touchmovement' // 操作类型
      }
    ],
  }
}
```

3、展示远程写画

通过 blinkEwb.js 的 loadData 方法监听其他端发送的白板操作(轮训访问 server), 接收的数据与步骤 2 发送的数据格式相同

```js
{
  url: 'https://{path}/ewb/page/load', // 请求 url
  data: {
    roomKey : 'roomKey', // 房间号
    pageId : 'pageId', // 白板页 id
    userId : 'userId', // 用户 id
  }
}
```

收到步骤 2 传输的数据后, 渲染 path 参数. 

渲染方式与步骤 1 相同, 循环 path 列表, 开始写画(第一个 path)调用 startPath, 写画移动调用 continuePath, 结束写画(最后一个 path)调用 endPath

remoteDraw(blinkEwb.js) -> remoteDraw(Connection.js) -> startPath(Svg.js) || continuePath(Svg.js) || endPath(Svg.js)

**以橡皮擦为例:**

橡皮擦需选中对象后, 再点击橡皮擦擦除. 本质上是删除功能. 本地渲染为删除节点

selectEraser(App.js) -> removeSelected(Svg.js) -> removeSelectedEx(Svg.js) -> createPrint(blinkEwb.js)

传输参数:

```js
{
  url: 'https://{path}/ewb/print/create', // 请求 url
  data: {
    roomKey : 'roomKey', // 房间号
    pageId : 'pageId', // 白板页 id
    userId : 'userId', // 用户 id
    data : [
      {
        guid: 'path-196262ff-6827-ec11-6600-b26541449528', // 生成的 path id
        op_guid: 'path-b4f2b090-4624-c937-ea16-ceb94837fdde', // 被删除的 path id
        type: 'rm' // 操作类型
      }
    ],
  }
}
```

**撤回**

在 Svg.js 中, 通过 undoStack 存储所有可撤回的步骤, 包括: 写画、删除 ...

撤回时, 取 undoStack 的最后一个值, 取值后渲染并将撤回操作传输给其他端

*server 传值如下:*

```js
{
  url: 'https://{path}/ewb/print/create', // 请求 url
  data: {
    roomKey : 'roomKey', // 房间号
    pageId : 'pageId', // 白板页 id
    userId : 'userId', // 用户 id
    data : [
      {
        guid: 'path-7c3eac20-d8da-be82-1448-064c3edc29f8', // 生成的 path id
        type: 'undo' // 操作类型
      }
    ],
  }
}
```

二次开发集成撤回功能, 重点在于构建 undoStack 变量, 例如写画结束时, 将会在 undoStack 中添加数据:

```js
// Svg.js
this.undoStack.push({
  type: 'path-line',
  pathID: pathID,
  datum: this.penPoints, // 矢量值
  lineColor: lc, // 颜色 
  lineWidth: lw, // 宽度
  guid: guid
});
```

### 数据格式

以下为传输的数据格式

**毛笔写画:**

```js
{
  url: 'https://{path}/ewb/print/create',
  data: {
    drawingItem: 'highlighter', // 画笔类型
    guid: 'path-260e1f06-f4e6-cc25-973b-30e7e2401595', // 生成的 path id
    oldx: '636.9668246445498', // 起始位置 x 坐标
    oldy: '517.5355450236966', // 起始位置 y 坐标
    lineColor: 'black', // 颜色
    lineWidth: '10px', // 画笔宽度
    path: [ // 所有画笔路径, 其他端接收后, 根据此参数做渲染
      [636.9668246445498, 517.5355450236966],
      [995.2606635071089, 885.781990521327],
      [1413.2701421800946, 1064.9289099526065]
    ],
    type: 'touchmovement' // 操作类型
  }
}
```

**文字:**

```js
{
  url: 'https://{path}/ewb/print/create',
  data: {
    guid: 'path-b72da160-a9d2-2a6f-404c-86ebf57f70d5' // 生成的 path id
    oldx: 1814.5631067961167 // x 坐标
    oldy: 618.4466019417476 // y 坐标
    type: 'addtext' // 操作类型
    value: '2222' // 文字内容
  }
}
```

**正方形:**

```js
{
  url: 'https://{path}/ewb/print/create',
  data: {
    drawingItem: 'square', // 画笔类型(正方形)
    guid: 'path-260e1f06-f4e6-cc25-973b-30e7e2401595', // 生成的 path id
    oldx: '636.9668246445498', // 起始位置 x 坐标
    oldy: '517.5355450236966', // 起始位置 y 坐标
    lineColor: 'black', // 颜色
    lineWidth: '10px', // 画笔宽度
    path: [ // 所有画笔路径, 其他端接收后, 根据此参数做渲染
      [636.9668246445498, 517.5355450236966],
      [995.2606635071089, 885.781990521327],
      [1413.2701421800946, 1064.9289099526065]
    ],
    type: 'touchmovement' // 操作类型
  }
}
```

**清空页面:**

```js
{
  url: 'https://{path}/ewb/page/clean', // 请求 url
  data: {
    roomKey : 'roomKey', // 房间号
    pageId : 'pageId' // 被清空的白板页 id
  }
}
```

**删除页面:**

```js
{
  url: 'https://{path}/ewb/page/delete', // 请求 url
  data: {
    roomKey : 'roomKey', // 房间号
    pageId : 'pageId' // 被删除的白板页 id
  }
}
```

### 目录说明

```
├── blink-wb.html  白板入口页面
├── config.js  配置文件
├── css  样式文件
├── images  图标
├── js
│   ├── blink
│   │   └── blinkEwb.js  与白板服务交互
│   ├── ewb
│   │   ├── App.js  页面渲染、事件绑定
│   │   ├── Connection.js  数据传输
│   │   ├── Svg.js  写、画等操作逻辑
│   │   └── WhiteBoardApi.js  初始化白板
│   ├── lib  第三方插件
└── resources
    └── strings.json  页面文案汇总
```