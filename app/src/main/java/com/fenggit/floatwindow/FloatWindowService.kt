package com.fenggit.floatwindow

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast

/**
 * Author: felixhe
 * Date: 2020/11/5 15:20
 * Description:
 */
class FloatWindowService : Service() {
    var mFloatWindow: FloatWindow? = null

    override fun onCreate() {
        super.onCreate()
        initView()
        Log.e("FloatWindowService", "onCreate:$this")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("FloatWindowService", "onStartCommand:$this")
        initView()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun initView() {
        if (mFloatWindow == null) {
            val view = LayoutInflater.from(this).inflate(R.layout.float_view, null)
            val close = view!!.findViewById<View>(R.id.fm_float_close)
            val next = view.findViewById<View>(R.id.fm_float_next)
            val play = view.findViewById<ImageView>(R.id.fm_float_play)

            close.setOnClickListener {
                mFloatWindow?.hidden()
            }

            close.setOnClickListener {
                mFloatWindow?.hidden()
            }

            next.setOnClickListener {
                Toast.makeText(this, "next", Toast.LENGTH_SHORT).show()
            }

            play.setOnClickListener {
                Toast.makeText(this, "play", Toast.LENGTH_SHORT).show()
            }

            mFloatWindow = FloatWindow.With(this, view)
                    .setAutoAlign(true)  // 是否自动贴边
                    .setModality(false)  // 是否模态窗口（事件是否可穿透当前窗口）
                    .setMoveAble(true)   // 是否可拖动
                    .setStartLocation(0, (getScreenHeight(this) * 0.7).toInt()) //设置起始位置
                    .create()
        }

        if (mFloatWindow?.isShowing == false) {
            mFloatWindow?.show()
        }
    }

    fun getScreenHeight(context: Context): Int {
        val sPoint = Point()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getSize(sPoint)
        return sPoint.y
    }
}