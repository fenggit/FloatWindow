package com.fenggit.floatwindow

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fenggit.floatwindow.permission.FloatPermission
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var mFloatManager: FloatManager
    lateinit var mFloatPermission: FloatPermission

    var mFloatWindow: FloatWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFloatManager = FloatManager()
        mFloatManager.initView(this)
        mFloatWindow = mFloatManager.getFloatWindow()

        mFloatPermission = FloatPermission()

        mFloatWindow?.setFloatListener {
            Toast.makeText(this, "click outside", Toast.LENGTH_SHORT).show()
        }

        btn_show.setOnClickListener {
//            mFloatWindow?.show()

            startService(Intent(this@MainActivity, FloatWindowService::class.java))

//            if (mFloatPermission.isHavePermission(this)) {
//                mFloatWindow?.show()
//            } else {
//                AlertDialog.Builder(this)
//                        .setTitle("授权")
//                        .setMessage("显示悬浮窗，需要开启悬浮窗权限")
//                        .setPositiveButton("去授权") { dialog, which ->
//                            mFloatPermission.gotoPermission(this@MainActivity)
//
//                        }.setNegativeButton("取消") { dialog, which ->
//                            dialog.dismiss()
//                        }.show()
//            }
        }

        btn_hidden.setOnClickListener {
            mFloatWindow?.hidden()
        }

        btn_next.setOnClickListener {
            startActivity(Intent(this, NextActivity::class.java))
        }
    }
}
