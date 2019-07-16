package com.example.yhao.floatwindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yhao.fixedfloatwindow.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

public class A_Activity extends AppCompatActivity {
    private static final String TAG = "FloatWindow";

    FloatViewHelper mFloatViewHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        setTitle("A");

        mFloatViewHelper = new FloatViewHelper(this);
    }


    public void show(View view) {
//        showFloat1();
//        showFloat2();

        mFloatViewHelper.initFloatView();

    }

    public void showFloat2() {
        View v = LayoutInflater.from(this).inflate(R.layout.activity_float_view, null);
        Button btn1 = v.findViewById(R.id.btn1);
        ImageView imageView = v.findViewById(R.id.img2);

        FloatWindow
                .with(getApplicationContext())
                .setView(v)
                //.setWidth(Screen.width, 0.2f) //设置悬浮控件宽高
                //.setHeight(Screen.width, 0.2f)
                .setX(Screen.width, 0)
                .setY(Screen.height, 0.6f)
                .setMoveType(MoveType.slide, 0, 0)
                .setMoveStyle(500, new BounceInterpolator())
                .setFilter(false, B_Activity.class)//只有在B_Activity，不显示
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(false)
                .build();

        FloatWindow.get().show();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(A_Activity.this, "onClick img", Toast.LENGTH_SHORT).show();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(A_Activity.this, "onClick btn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showFloat1() {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.drawable.icon);

        FloatWindow
                .with(getApplicationContext())
                .setView(imageView)
                //.setWidth(Screen.width, 0.2f) //设置悬浮控件宽高
                //.setHeight(Screen.width, 0.2f)
                .setX(Screen.width, 0.8f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide, 0, 0)
                .setMoveStyle(500, new BounceInterpolator())
                .setFilter(true, A_Activity.class, C_Activity.class)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .build();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(A_Activity.this, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void change(View view) {
        startActivity(new Intent(this, B_Activity.class));
    }


    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "onSuccess");
        }

        @Override
        public void onFail() {
            Log.d(TAG, "onFail");
        }
    };

    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            Log.d(TAG, "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            Log.d(TAG, "onShow");
        }

        @Override
        public void onHide() {
            Log.d(TAG, "onHide");
        }

        @Override
        public void onDismiss() {
            Log.d(TAG, "onDismiss");
        }

        @Override
        public void onMoveAnimStart() {
            Log.d(TAG, "onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
            Log.d(TAG, "onMoveAnimEnd");
        }

        @Override
        public void onBackToDesktop() {
            Log.d(TAG, "onBackToDesktop");
        }
    };
}
