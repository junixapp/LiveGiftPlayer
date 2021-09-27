package com.lxj.livegiftplayer

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.penfeizhou.animation.webp.WebPDrawable

class WebpGiftPlayView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : androidx.appcompat.widget.AppCompatImageView(context, attributeSet, defStyleAttr){
        init {
            scaleType = ScaleType.CENTER_CROP
        }
        fun setDataSource(path: String, onEnd: ()->Unit){
            val webpDrawable = WebPDrawable.fromFile(path)
            webpDrawable.setLoopLimit(1)
            webpDrawable.registerAnimationCallback(object : Animatable2Compat.AnimationCallback(){
                override fun onAnimationStart(drawable: Drawable?) {
                    super.onAnimationStart(drawable)
                }
                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    onEnd()
                }
            })
            setImageDrawable(webpDrawable)
        }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if(drawable!=null && drawable is WebPDrawable){
            (drawable as WebPDrawable).apply {
                clearAnimationCallbacks()
                stop()
            }
        }
    }
}