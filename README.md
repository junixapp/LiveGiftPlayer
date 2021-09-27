# LiveGiftPlayer
直播礼物特效播放器，支持MP4(支持4个方向的透明通道)和WEBP。

# 特色
- 支持4个方向的透明通道的MP4文件
- 支持WEBP格式
- 实现Lifecycle自动释放资源

# 依赖

[![](https://jitpack.io/v/li-xiaojun/LiveGiftPlayer.svg)](https://jitpack.io/#li-xiaojun/LiveGiftPlayer)
```
implementation 'com.github.li-xiaojun:LiveGiftPlayer:版本号'
```

# 使用
1. 构造GiftPlayItem
```kotlin
data class GiftPlayItem(
    var path: String,  //文件路径
    var fileType: GiftFileType, //礼物文件类型
    var alphaPosition: AlphaPosition? = null //透明通道位置，MP4类型需要传
)
```

2. 播放
```kotlin
LiveGiftPlayer.play(lifecycleOwner, giftItem, giftContainer)
```

3. 监听
```kotlin
LiveGiftPlayer.playCallback = object : LiveGiftPlayer.PlayCallback{
    override fun onStart(gift: GiftPlayItem) {
        findViewById<TextView>(R.id.tvInfo).text = "开始播放 ${gift.path} 透明通道：${gift.alphaPosition}"
    }

    override fun onEnd(gift: GiftPlayItem) {
        findViewById<TextView>(R.id.tvInfo).text = "播放结束 ${gift.path} 透明通道：${gift.alphaPosition}"
    }

}
```