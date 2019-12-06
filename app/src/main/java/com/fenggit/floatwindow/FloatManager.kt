package com.fenggit.floatwindow

import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast

/**
 * Author: felixhe
 * Date: 2019-12-06 14:53
 * Description:
 */
class FloatManager {
    var float: FloatWindow? = null

    fun initView(context: Context) {
        var view = LayoutInflater.from(context).inflate(R.layout.float_view, null)
        val close = view!!.findViewById<View>(R.id.fm_float_close)
        val next = view.findViewById<View>(R.id.fm_float_next)
        val play = view.findViewById<ImageView>(R.id.fm_float_play)

        close.setOnClickListener {
            float?.hidden()
        }

        next.setOnClickListener {
            Toast.makeText(context, "next", Toast.LENGTH_SHORT).show()
        }

        play.setOnClickListener {
            Toast.makeText(context, "play", Toast.LENGTH_SHORT).show()
        }

        float = FloatWindow.With(context, view)
                .setAutoAlign(true)  //是否自动贴边
                .setModality(false)
                .setMoveAble(true)   // 是否可拖动
                .setStartLocation(0, (getScreenHeight(context) * 0.7).toInt())
                .create()
    }

    fun getFloatWindow(): FloatWindow? {
        return float
    }

    fun getScreenHeight(context: Context): Int {
        var sPoint = Point()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getSize(sPoint)
        return sPoint.y
    }
}