package com.silang.superfileview.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static ExceptionHandler handler = new ExceptionHandler();
    private Thread.UncaughtExceptionHandler defaultHandler;
    private File saveSpacePath;
    private File localErrorSave;
    private Context context;

    private ExceptionHandler() {
    }

    public static ExceptionHandler getInstance() {
        return handler;
    }

    public void initConfig(Context context) {
        this.context = context;
        saveSpacePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/007");
        localErrorSave = new File(saveSpacePath, "error.txt");
        if (!saveSpacePath.exists()) {
            saveSpacePath.mkdirs();
        }
        if (!localErrorSave.exists()) {
            try {
                localErrorSave.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread t, Throwable e) {
        writeErrorToLocal(t, e);
        upLoadException(t);
    }

    private void upLoadException(Thread t) {
    }

    private void writeErrorToLocal(Thread t, Throwable e) {
        try {
            BufferedWriter fos = new BufferedWriter(new FileWriter(localErrorSave, true));
            StringBuilder sb = new StringBuilder();
            sb.append("\n----------------------------------------------------------------------------------------\n");
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            sb.append(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date(System.currentTimeMillis()))
                    + "<---->包名::" + packageInfo.packageName
                    + "<---->版本名::" + packageInfo.versionName
                    + "<---->版本号::" + packageInfo.versionCode + "\n");
            sb.append("手机制造商::" + Build.MANUFACTURER + "\n");
            sb.append("手机型号::" + Build.MODEL + "\n");
            sb.append("CPU架构::" + Build.CPU_ABI + "\n");
            sb.append(e.toString() + "\n");
            StackTraceElement[] trace = e.getStackTrace();
            for (StackTraceElement traceElement : trace) {
                sb.append("\n\tat " + traceElement);
            }
            sb.append("\n");
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable se : suppressed) {
                sb.append("\tat " + se.getMessage());
            }
            fos.write(sb.toString());
            fos.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            defaultHandler.uncaughtException(t, e1);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}