package com.lxj.livegiftplayerdemo

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.penfeizhou.animation.webp.WebPDrawable
import com.lxj.alphaplayer.view.AlphaPosition
import com.lxj.livegiftplayer.GiftFileType
import com.lxj.livegiftplayer.GiftPlayItem
import com.lxj.livegiftplayer.LiveGiftPlayer
import com.lxj.livegiftplayer.WebpGiftPlayView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btnCopy).setOnClickListener {
            ResourceUtils.copyFileFromAssets("gifts", getExternalFilesDir(null)?.absolutePath+"/gifts")
        }

        LiveGiftPlayer.playCallback = object : LiveGiftPlayer.PlayCallback{
            override fun onStart(gift: GiftPlayItem) {
                findViewById<TextView>(R.id.tvInfo).text = "开始播放 ${gift.path} 透明通道：${gift.alphaPosition}"
            }

            override fun onEnd(gift: GiftPlayItem) {
                findViewById<TextView>(R.id.tvInfo).text = "播放结束 ${gift.path} 透明通道：${gift.alphaPosition}"
            }

        }

        findViewById<View>(R.id.btnPlay).setOnClickListener {
            val dir = getExternalFilesDir(null)?.absolutePath+"/gifts"
            val files = FileUtils.listFilesInDir(dir)
            if(files.isNotEmpty()){
                val giftContainer = findViewById<FrameLayout>(R.id.giftContainer)
                files.forEach {
                    LiveGiftPlayer.play(this, GiftPlayItem(path = it.absolutePath,
                        fileType = when(it.extension){
                            "mp4" -> GiftFileType.MP4
                            else -> GiftFileType.WEBP
                        }, alphaPosition = when(it.nameWithoutExtension){
                            "left" -> AlphaPosition.Left
                            "right" -> AlphaPosition.Right
                            "bottom" -> AlphaPosition.Bottom
                            "top" -> AlphaPosition.Top
                            else -> null
                        }), giftContainer)
                }
            }else{
                ToastUtils.showShort("礼物动画文件不存在，请先拷贝")
            }
        }
    }
}