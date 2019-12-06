package com.fenggit.floatwindow;

import android.util.Log;

/**
 * Author: felixhe
 * Date: 2019-12-06 14:36
 * Description:
 */
public class FLog {
    public static final String TAG = "FloatWindow";

    public static boolean isDebug = true;

    public static void e(String message) {
        if (isDebug) {
            Log.e(TAG, message);
        }
    }
}
