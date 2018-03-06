package com.silang.superfileview.util;

import android.text.TextUtils;
import android.util.Log;

public final class LogUtil {
    private static final String LOG_TAG = "io.weichao.LogUtil";

    private static boolean DEBUG = true;

    private LogUtil() {
    }

    public static void d(String log) {
        d(LOG_TAG, log);
    }

    public static void d(String tag, String log) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) {
            Log.d(tag, log);
        }
    }

    public static void i(String log) {
        i(LOG_TAG, log);
    }

    public static void i(String tag, String log) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) {
            Log.i(tag, log);
        }
    }

    public static void e(String log) {
        e(LOG_TAG, log);
    }

    public static void e(String tag, String log) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) {
            Log.e(tag, log);
        }
    }
}