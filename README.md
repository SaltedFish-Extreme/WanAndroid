# WanAndroid

#### 介绍
玩安卓项目APP，使用玩Android网站提供的接口，参考各位大佬的玩安卓项目以及集成了很多优秀的开源库的项目

#### 软件截图

![启动页](https://images.gitee.com/uploads/images/2022/0213/102717_cb6419bf_5661404.jpeg "微信图片_20220213102319.jpg")
![首页](https://images.gitee.com/uploads/images/2022/0213/102733_eac323b8_5661404.jpeg "微信图片_20220213102328.jpg")
![侧滑栏](https://images.gitee.com/uploads/images/2022/0213/102754_0f1c5d0e_5661404.jpeg "微信图片_20220213102332.jpg")
![项目fragment](https://images.gitee.com/uploads/images/2022/0213/102809_24c1c60f_5661404.jpeg "微信图片_20220213102336.jpg")
![广场fragment](https://images.gitee.com/uploads/images/2022/0213/102834_43c55c8c_5661404.jpeg "微信图片_20220213102340.jpg")
![体系fragment](https://images.gitee.com/uploads/images/2022/0213/102855_38b30c76_5661404.jpeg "微信图片_20220213102343.jpg")
![导航fragment](https://images.gitee.com/uploads/images/2022/0213/102913_b545bc1a_5661404.jpeg "微信图片_20220213102347.jpg")
![设置fragment](https://images.gitee.com/uploads/images/2022/0213/102929_a7271ba2_5661404.jpeg "微信图片_20220213102351.jpg")
![设置页](https://images.gitee.com/uploads/images/2022/0213/102943_65ff2209_5661404.jpeg "微信图片_20220213102354.jpg")
![关于](https://images.gitee.com/uploads/images/2022/0213/103024_c1123001_5661404.jpeg "微信图片_20220213102415.jpg")
![扫码下载页](https://images.gitee.com/uploads/images/2022/0213/103037_9cabf91a_5661404.jpeg "微信图片_20220213102423.jpg")
![扫码](https://images.gitee.com/uploads/images/2022/0213/103050_7f5e2f28_5661404.jpeg "微信图片_20220213102435.jpg")
![选择图片](https://images.gitee.com/uploads/images/2022/0213/103101_595f18d9_5661404.jpeg "微信图片_20220213102501.jpg")
![下载链接](https://images.gitee.com/uploads/images/2022/0213/103115_4e0c9c58_5661404.jpeg "微信图片_20220213102511.jpg")
![官网](https://images.gitee.com/uploads/images/2022/0213/103137_de1039d8_5661404.jpeg "微信图片_20220213102526.jpg")
![源码](https://images.gitee.com/uploads/images/2022/0213/103153_f0a9900e_5661404.jpeg "微信图片_20220213102528.jpg")

#### 功能介绍
目前只做完了主页面以及设置页面的东西，还有网页，后续会把个人功能方面做完，以及各种二级页面

#### 参考项目
感谢
[冰可乐大佬的玩安卓](https://github.com/iceCola7/WanAndroid)
[鸡你太美大佬的玩安卓](https://github.com/hegaojian/JetpackMvvm)
[goweii大佬的玩安卓](https://github.com/goweii/WanAndroid)

#### 开源框架
````gradle
    //navigation支持
    implementation 'androidx.navigation:navigation-fragment-ktx:2.3.5'
    implementation 'androidx.navigation:navigation-ui-ktx:2.3.5'
    //协程
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0'
    //OkHttp
    implementation 'com.squareup.okhttp3:okhttp:5.0.0-alpha.4'
    //网络请求 https://github.com/liangjingkanji/Net
    implementation 'com.github.liangjingkanji:Net:3.1.2'
    //JSON解析
    implementation 'com.google.code.gson:gson:2.8.9'
    //region 注释掉刷新加载及缺省页(BRV自带)
    /*//刷新加载 https://github.com/scwang90/SmartRefreshLayout
    implementation 'io.github.scwang90:refresh-layout-kernel:2.0.5'
    implementation 'io.github.scwang90:refresh-header-classics:2.0.5'
    implementation 'io.github.scwang90:refresh-footer-classics:2.0.5'
    //缺省页 https://github.com/liangjingkanji/StateLayout
    implementation 'com.github.liangjingkanji:StateLayout:1.2.0'*/
    //endregion
    //RecyclerView https://github.com/liangjingkanji/BRV
    implementation 'com.github.liangjingkanji:BRV:1.3.51'
    //Adapter https://github.com/CymChad/BaseRecyclerViewAdapterHelper
    implementation 'com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.6'
    //序列化数据 https://github.com/liangjingkanji/Serialize/
    implementation 'com.github.liangjingkanji:Serialize:1.2.3'
    //侧滑返回activity https://github.com/goweii/SwipeBack
    implementation 'com.github.goweii:SwipeBack:2.0.5'
    //今日诗词 https://github.com/xenv/jinrishici-sdk-android
    implementation 'com.jinrishici:android-sdk:1.5'
    //ViewPager指示器 https://github.com/hackware1993/MagicIndicator
    implementation 'com.github.hackware1993:MagicIndicator:1.7.0'
    //图片加载 https://github.com/bumptech/glide
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    //轮播图 https://github.com/youth5201314/banner
    implementation 'io.github.youth5201314:banner:2.2.2'
    //相机相册 https://github.com/HuanTanSheng/EasyPhotos
    implementation 'com.github.HuanTanSheng:EasyPhotos:3.1.5'
    //标题栏 https://github.com/getActivity/TitleBar
    implementation 'com.github.getActivity:TitleBar:9.3'
    //吐司 https://github.com/getActivity/ToastUtils
    implementation 'com.github.getActivity:ToastUtils:10.3'
    //权限请求 https://github.com/getActivity/XXPermissions
    implementation 'com.github.getActivity:XXPermissions:13.2'
    //Shape https://github.com/getActivity/ShapeView
    implementation 'com.github.getActivity:ShapeView:6.2'
    //揭示效果 https://github.com/goweii/RevealLayout
    implementation 'com.github.goweii:RevealLayout:1.3.4'
    //伸缩布局 https://github.com/google/flexbox-layout
    implementation 'com.google.android.flexbox:flexbox:3.0.0'
    //垂直选项卡布局 https://github.com/qstumn/VerticalTabLayout
    implementation 'q.rorbin:VerticalTabLayout:1.2.9'
    //滑动布局 https://github.com/daimajia/AndroidSwipeLayout
    implementation 'com.daimajia.swipelayout:library:1.2.0'
    //web浏览器 https://github.com/Justson/AgentWeb
    implementation 'com.github.Justson.AgentWeb:agentweb-core:v5.0.0-alpha.1-androidx'
    //web文件下载 https://github.com/Justson/Downloader
    implementation 'com.github.Justson:Downloader:v5.0.0-androidx'
    //web进度指示条 https://github.com/Justson/CoolIndicator
    implementation 'com.github.Justson:CoolIndicator:v1.0.0'
    //扫描二维码 https://github.com/bingoogolapple/BGAQRCode-Android
    implementation 'com.github.bingoogolapple.BGAQRCode-Android:zxing:1.3.8'
    implementation 'com.github.bingoogolapple.BGAQRCode-Android:zbar:1.3.8'
````
