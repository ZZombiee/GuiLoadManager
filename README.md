# GuiLoadManager
自用简易图片缓存管理器工具

 链式调用  
```
builder = new GuiLoadConfig.Builder(context);   //初始化设置
bitmapList = new GuiLoadManager()               //创建管理器
.init(builder.setRate(2).build())               //可以给config设置保存图片的压缩倍率，为n的平方
.setDefaultPages(defaultPages)                  //设置默认图片，如本地没有缓存需要下载时
.setType(LoadTask.LoadType.GUI_PAGES)           //设置类型，保存到不同目录
.loadUrl(pagesList)                             //加载网络地址，数组或单个元素
.setVersion(version)                            //设置版本，如设置版本与上次记录相同且本地有缓存，则不下载
.execute()                                      //执行任务
.get();                                         //获取bitmap集合
```
 
也可以在链式调用中添加回调
```
 new GuiLoadManager()
         .init(new GuiLoadConfig.Builder(context).build())
         .loadUrl(homeAD.imageURL)
         .setType(LoadTask.LoadType.HOME_ADVERTISEMENT)
         .setCallback(new DefaultLoadCallback() {
             @Override
             public void loadSuccess(List<Bitmap> bitmapList) {    //回调成功即时得到Bitmap集合
                  bitList = bitmapList;
             }
         })
         .execute();
 ```
