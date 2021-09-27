package com.lxj.livegiftplayer

import com.lxj.alphaplayer.view.AlphaPosition

enum class GiftFileType {
    MP4, WEBP, SVGA
}

data class GiftPlayItem(
    var path: String,  //文件路径
    var fileType: GiftFileType, //礼物文件类型
    var alphaPosition: AlphaPosition? = null //透明通道位置，MP4类型需要传
)
