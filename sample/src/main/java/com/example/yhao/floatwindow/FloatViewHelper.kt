package com.example.yhao.floatwindow

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import com.example.yhao.fixedfloatwindow.R
import com.yhao.floatwindow.FloatWindow
import com.yhao.floatwindow.MoveType
import com.yhao.floatwindow.Screen


/**
 * Author: felixhe
 * Date: 2019-07-15 10:16
 * Description:
 */

const val SHOW: Int = 10
const val HIDDEN: Int = 11
const val ANIM_TIME: Long = 200

class FloatViewHelper(var context: Context) {
    var isExpand: Boolean = false
    var isAnimRun: Boolean = false

    var mFloatBuilder: FloatWindow.B? = null
    var width: Int = 0

    var moreViewGroup: View? = null


    var mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg?.what == SHOW) {
                expand()
            } else if (msg?.what == HIDDEN) {
                unExpand()
            }
        }
    }


    fun initFloatView() {
        val view = LayoutInflater.from(context).inflate(R.layout.float_view, null)
        val closeView = view.findViewById<View>(R.id.fm_float_close)
        val imgView = view.findViewById<ImageView>(R.id.fm_float_img)
        val playView = view.findViewById<ImageView>(R.id.fm_float_play)
        val nextView = view.findViewById<View>(R.id.fm_float_next)
        moreViewGroup = view.findViewById<View>(R.id.fm_float_more)

        width = context.resources.getDimensionPixelOffset(R.dimen.float_more_width)

//        val songInfo = MusicManager.getInstance().nowPlayingSongInfo
//        if (songInfo != null) {
//            ImageLoaderProxy.get(context).load(songInfo.songCover).cropCircle().placeholder( R.drawable.default_record_album).into(imgView)
//        }

        view.setOnClickListener {
            expand()
        }
        /*imgView.setOnClickListener {
            expand()
        }*/

        closeView.setOnClickListener {
            FloatWindow.get()?.hide()
        }

        playView.setOnClickListener {
            /*if (MusicManager.getInstance().isPlaying) {
                playView.setImageLevel(1)
                MusicManager.getInstance().pauseMusic()
            } else {
                playView.setImageLevel(0)
                MusicManager.getInstance().playMusic()
            }*/
        }

        nextView.setOnClickListener {
            //MusicManager.getInstance().skipToNext()
        }

        if (mFloatBuilder == null) {
            mFloatBuilder = FloatWindow.with(context.applicationContext)
                    .setView(view)
                    .setX(0)
                    .setY(Screen.height, 0.7f)
                    .setMoveType(MoveType.slide)
                    //.setFilter(false, FMPlayActivity::class.java)
                    .setMoveStyle(500, BounceInterpolator())
                    .setDesktopShow(true)
            mFloatBuilder?.build()
        }

        FloatWindow.get()?.show()
        showMoreView(true, 0L)
    }

    fun showMoreView(isShow: Boolean, uptimeMillis: Long) {
        mHandler.removeMessages(SHOW)
        mHandler.removeMessages(HIDDEN)

        val what = if (isShow) SHOW else HIDDEN
        mHandler.sendEmptyMessageDelayed(what, uptimeMillis)
    }

    fun hidden() {
        FloatWindow.get()?.hide()
    }

    fun expand() {
        moreViewGroup?.visibility = View.VISIBLE
        showMoreView(false, 3000)

        /*if (isAnimRun || isExpand) {
            if (isExpand) {
                mHandler.sendEmptyMessageDelayed(HIDDEN, 3000)
            }
            return
        }
        isAnimRun = true

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                mHandler.postDelayed({
                    isExpand = true
                    isAnimRun = false

                    showMoreView(false, 3000)
                }, 50)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                moreViewGroup?.layoutParams?.width = 0
            }
        })
        valueAnimator.addUpdateListener { animation ->
            var v: Float = animation.animatedValue as Float
            moreViewGroup?.layoutParams?.width = (v * width).toInt()
            Log.e("hefeng","w:"+moreViewGroup?.layoutParams?.width)
            moreViewGroup?.requestLayout()
            //FloatWindow.get()?.view?.requestLayout()
        }

        var objectAnimator = ObjectAnimator.ofFloat(moreViewGroup, "alpha", 0f, 1f)
        var list = AnimatorSet()
        list.playTogether(valueAnimator, objectAnimator)
        list.duration = ANIM_TIME
        list.start()*/
    }

    fun unExpand() {
        moreViewGroup?.visibility = View.GONE

        /*if (isAnimRun) {
            return
        }

        isAnimRun = true

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isExpand = false
                isAnimRun = false
                moreViewGroup?.alpha = 1f
                //moreViewGroup?.background?.alpha = 255
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        valueAnimator.addUpdateListener { animation ->
            var v: Float = animation.animatedValue as Float
            moreViewGroup?.layoutParams?.width = width - (v * width).toInt()
            //moreViewGroup?.background?.alpha = ((1f - v) * 255).toInt()
            moreViewGroup?.requestLayout()
        }

        var objectAnimator = ObjectAnimator.ofFloat(moreViewGroup, "alpha", 1f, 0f)

        var list = AnimatorSet()
        list.playTogether(valueAnimator, objectAnimator)
        list.duration = ANIM_TIME
        list.start()*/
    }
}