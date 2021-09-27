package com.lxj.livegiftplayer

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.lxj.alphaplayer.view.AlphaVideoTextureView
import java.util.*

/**
 * 特效礼物播放器，支持MP4，SVGA，webp格式的动画
 */
object LiveGiftPlayer: LifecycleObserver {
    val TAG = "GiftPlayManager"
    var list = LinkedList<GiftPlayItem>()
    var playCallback: PlayCallback? = null
    private var isPlaying = false

    fun play(owner: LifecycleOwner, giftItem: GiftPlayItem, container: ViewGroup){
        owner.lifecycle.removeObserver(this)
        owner.lifecycle.addObserver(this)
        //1. 添加到队列，并尝试显示
        list.add(giftItem)

        //2. 开始播放
        innerPlay(container)
    }

    private fun innerPlay( container: ViewGroup){
        if(isPlaying) return
        val data = list.poll() ?: return

        container.removeAllViews()
        when(data.fileType){
            GiftFileType.MP4 -> { //mp4
                val mp4GiftPlayView = AlphaVideoTextureView(container.context)
                mp4GiftPlayView.setAlphaPosition(data.alphaPosition)
                container.addView(mp4GiftPlayView, FrameLayout.LayoutParams(-1, -1))
                mp4GiftPlayView.setOnPlayEndListener {
                    next(container, data)
                }
                mp4GiftPlayView.setVideoPath(data.path).start()
            }
            GiftFileType.WEBP -> {  //webp
                val webpGiftPlayView = WebpGiftPlayView(container.context)
                webpGiftPlayView.setDataSource(data.path, onEnd = {
                    next(container, data)
                })
                container.addView(webpGiftPlayView, FrameLayout.LayoutParams(-1, -1))
            }
            GiftFileType.SVGA -> { //

            }
        }
        isPlaying = true
        playCallback?.onStart(data)
    }

    private fun next(container: ViewGroup, data: GiftPlayItem){
        isPlaying = false
        playCallback?.onEnd(data)
        if(!list.isEmpty()){
            innerPlay(container)
        }else{
            container.removeAllViews()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() {
        isPlaying = false
        list.clear()
    }

     interface PlayCallback{
        fun onStart(gift: GiftPlayItem)
        fun onEnd(gift: GiftPlayItem)
    }
}