package com.fenggit.floatwindow;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;


import com.fenggit.floatwindow.permission.FloatPermission;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Author: felixhe
 * Date: 2019-12-06
 * Description:
 */
public class FloatWindow {

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private DisplayMetrics mDisplayMetrics;

    /**
     * 触摸点相对于view左上角的坐标
     */
    private float downX;
    private float downY;
    /**
     * 触摸点相对于屏幕左上角的坐标
     */
    private float rowX;
    private float rowY;
    /**
     * 悬浮窗显示标记
     */
    private boolean isShowing;
    /**
     * 拖动最小偏移量
     */
    private static final int MINIMUM_OFFSET = 5;

    private Context mContext;
    /**
     * 是否自动贴边
     */
    private boolean autoAlign;
    /**
     * 是否模态窗口
     */
    private boolean modality;
    /**
     * 是否可拖动
     */
    private boolean moveAble;


    /**
     * 透明度
     */
    private float alpha;

    /**
     * 初始位置
     */
    private int startX;
    private int startY;

    /**
     * View 高度
     */
    private int height;
    /**
     * View 宽度
     */
    private int width;


    /**
     * 内部定义的View，专门处理事件拦截的父View
     */
    private FloatView floatView;
    /**
     * 外部传进来的需要悬浮的View
     */
    private View contentView;

    private boolean isAddView;

    private FloatListener mFloatListener;

    private FloatWindow(With with) {
        this.mContext = with.context;
        this.autoAlign = with.autoAlign;
        this.modality = with.modality;
        this.contentView = with.contentView;
        this.moveAble = with.moveAble;
        this.startX = with.startX;
        this.startY = with.startY;
        this.alpha = with.alpha;
        this.height = with.height;
        this.width = with.width;

        initWindowManager();
        initLayoutParams();
        initFloatView();
    }

    private void initWindowManager() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //获取一个DisplayMetrics对象，该对象用来描述关于显示器的一些信息，例如其大小，密度和字体缩放。
        mDisplayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void initFloatView() {
        floatView = new FloatView(mContext);
        if (moveAble) {
            floatView.setOnTouchListener(new WindowTouchListener());
        }
    }

    private void initLayoutParams() {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (modality) {
            mLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            mLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        if (height != WindowManager.LayoutParams.WRAP_CONTENT) {
            mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        if (width != WindowManager.LayoutParams.WRAP_CONTENT) {
            mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        }
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        //此处mLayoutParams.type不建议使用TYPE_TOAST，因为在一些版本较低的系统中会出现拖动异常的问题，虽然它不需要权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //悬浮窗背景明暗度0~1，数值越大背景越暗，只有在flags设置了WindowManager.LayoutParams.FLAG_DIM_BEHIND 这个属性才会生效
        mLayoutParams.dimAmount = 0.0f;
        //悬浮窗透明度0~1，数值越大越不透明
        mLayoutParams.alpha = alpha;
        //悬浮窗起始位置
        mLayoutParams.x = startX;
        mLayoutParams.y = startY;
    }

    public void setFloatListener(FloatListener mFloatListener) {
        this.mFloatListener = mFloatListener;
    }

    public View getContentView() {
        return contentView;
    }

    public boolean isAddView() {
        return isAddView;
    }

    private FloatPermission mFloatPermission = new FloatPermission();

    /**
     * 将窗体添加到屏幕上
     */
    @SuppressLint("NewApi")
    public void show() {
//        if (!mFloatPermission.isHavePermission(mContext)) {
//            return;
//        }

        if (!isShowing()) {
            floatView.setVisibility(View.VISIBLE);

            if (!isAddView) {
                mWindowManager.addView(floatView, mLayoutParams);
                isAddView = true;
            }
            isShowing = true;
        }
    }

    public void hidden() {
        isShowing = false;
        if (floatView != null) {
            floatView.setVisibility(View.GONE);
        }
    }

    /**
     * 悬浮窗是否正在显示
     *
     * @return true if it's showing.
     */
    private boolean isShowing() {
        if (floatView != null && floatView.getVisibility() == View.VISIBLE) {
            return isShowing;
        }
        return false;
    }

    /**
     * 打开悬浮窗设置页
     * 部分第三方ROM无法直接跳转可使用{@link #openAppSettings(Context)}跳到应用详情页
     *
     * @param context
     * @return true if it's open successful.
     */
    public static boolean openOpsSettings(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } else {
                return openAppSettings(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 打开应用详情页
     *
     * @param context
     * @return true if it's open success.
     */
    public static boolean openAppSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断 悬浮窗口权限是否打开
     * 由于android未提供直接跳转到悬浮窗设置页的api，此方法使用反射去查找相关函数进行跳转
     * 部分第三方ROM可能不适用
     *
     * @param context
     * @return true 允许  false禁止
     */
    public static boolean isAppOpsOn(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = (Integer) method.invoke(object, arrayOfObject1);
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return false;
    }

    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }

    /**
     * 移除悬浮窗
     */
    public void remove() {
        if (isShowing()) {
            floatView.removeView(contentView);
            mWindowManager.removeView(floatView);
            isShowing = false;
            isAddView = false;
        }
    }

    /**
     * 用于获取系统状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        int identifier = Resources.getSystem().getIdentifier("status_bar_height",
                "dimen", "android");
        if (identifier > 0) {
            return Resources.getSystem().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    class FloatView extends FrameLayout {

        /**
         * 记录按下位置
         */
        int interceptX = 0;
        int interceptY = 0;

        public FloatView(Context context) {
            super(context);
            //这里由于一个ViewGroup不能add一个已经有Parent的contentView,所以需要先判断contentView是否有Parent
            //如果有则需要将contentView先移除
            if (contentView.getParent() != null && contentView.getParent() instanceof ViewGroup) {
                ((ViewGroup) contentView.getParent()).removeView(contentView);
            }

            addView(contentView);
        }

        /**
         * 解决点击与拖动冲突的关键代码
         *
         * @param ev
         * @return
         */
        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            //此回调如果返回true则表示拦截TouchEvent由自己处理，false表示不拦截TouchEvent分发出去由子view处理
            //解决方案：如果是拖动父View则返回true调用自己的onTouch改变位置，是点击则返回false去响应子view的点击事件
            boolean isIntercept = false;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    interceptX = (int) ev.getX();
                    interceptY = (int) ev.getY();
                    downX = ev.getX();
                    downY = ev.getY();
                    isIntercept = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    //在一些dpi较高的设备上点击view很容易触发 ACTION_MOVE，所以此处做一个过滤
                    isIntercept = Math.abs(ev.getX() - interceptX) > MINIMUM_OFFSET && Math.abs(ev.getY() - interceptY) > MINIMUM_OFFSET;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    break;
            }
            return isIntercept;
        }
    }

    class WindowTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            //获取触摸点相对于屏幕左上角的坐标
            rowX = event.getRawX();
            rowY = event.getRawY() - getStatusBarHeight();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    actionDown(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    actionMove(event);
                    break;
                case MotionEvent.ACTION_UP:
                    actionUp(event);
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    actionOutSide(event);
                    break;
                default:
                    break;
            }
            return false;
        }

        /**
         * 手指点击窗口外的事件
         *
         * @param event
         */
        private void actionOutSide(MotionEvent event) {
            //由于我们在layoutParams中添加了FLAG_WATCH_OUTSIDE_TOUCH标记，那么点击悬浮窗之外时此事件就会被响应
            //这里可以用来扩展点击悬浮窗外部响应事件
            if (mFloatListener != null) {
                mFloatListener.actionOutSide(event);
            }
        }

        /**
         * 手指抬起事件
         *
         * @param event
         */
        private void actionUp(MotionEvent event) {
            if (autoAlign) {
                autoAlign();
            }
        }

        /**
         * 拖动事件
         *
         * @param event
         */
        private void actionMove(MotionEvent event) {
            //拖动事件下一直计算坐标 然后更新悬浮窗位置
            updateLocation((rowX - downX), (rowY - downY));
        }

        /**
         * 更新位置
         */
        private void updateLocation(float x, float y) {
            mLayoutParams.x = (int) x;
            mLayoutParams.y = (int) y;
            mWindowManager.updateViewLayout(floatView, mLayoutParams);
        }

        /**
         * 手指按下事件
         *
         * @param event
         */
        private void actionDown(MotionEvent event) {
//            downX = event.getX();
//            downY = event.getY();
        }

        /**
         * 自动贴边
         */
        private void autoAlign() {
            float fromX = mLayoutParams.x;

            if (rowX <= mDisplayMetrics.widthPixels / 2) {
                mLayoutParams.x = 0;
            } else {
                mLayoutParams.x = mDisplayMetrics.widthPixels;
            }
            FLog.e("fromX：" + fromX + " || toX : " + mLayoutParams.x);
            //这里使用ValueAnimator来平滑计算起始X坐标到结束X坐标之间的值，并更新悬浮窗位置
            ValueAnimator animator = ValueAnimator.ofFloat(fromX, mLayoutParams.x);
            animator.setDuration(500);
            animator.setInterpolator(new BounceInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //这里会返回fromX ~ mLayoutParams.x之间经过计算的过渡值
                    float toX = (float) animation.getAnimatedValue();
                    FLog.e("toX : " + toX);
                    //我们直接使用这个值来更新悬浮窗位置
                    updateLocation(toX, mLayoutParams.y);
                }
            });
            animator.start();
        }
    }

    public static class With {
        private Context context;
        private boolean autoAlign;
        private boolean modality;
        private View contentView;
        private boolean moveAble;
        private float alpha = 1f;

        /**
         * View 高度
         */
        private int height = WindowManager.LayoutParams.WRAP_CONTENT;
        /**
         * View 宽度
         */
        private int width = WindowManager.LayoutParams.WRAP_CONTENT;

        /**
         * 初始位置
         */
        private int startX;
        private int startY;

        /**
         * @param context     上下文环境
         * @param contentView 需要悬浮的视图
         */
        public With(Context context, View contentView) {
            this.context = context;
            this.contentView = contentView;
        }

        /**
         * 是否自动贴边
         *
         * @param autoAlign
         * @return
         */
        public With setAutoAlign(boolean autoAlign) {
            this.autoAlign = autoAlign;
            return this;
        }

        /**
         * 是否模态窗口（事件是否可穿透当前窗口）
         *
         * @param modality
         * @return
         */
        public With setModality(boolean modality) {
            this.modality = modality;
            return this;
        }

        /**
         * 是否可拖动
         *
         * @param moveAble
         * @return
         */
        public With setMoveAble(boolean moveAble) {
            this.moveAble = moveAble;
            return this;
        }

        /**
         * 设置起始位置
         *
         * @param startX
         * @param startY
         * @return
         */
        public With setStartLocation(int startX, int startY) {
            this.startX = startX;
            this.startY = startY;
            return this;
        }

        public With setAlpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public With setHeight(int height) {
            this.height = height;
            return this;
        }

        public With setWidth(int width) {
            this.width = width;
            return this;
        }

        public FloatWindow create() {
            return new FloatWindow(this);
        }
    }
}