package com.silang.superfileview.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import com.silang.superfileview.util.LogUtil;

public class SuperFileView extends FrameLayout implements TbsReaderView.ReaderCallback {
    private static final String TAG = "SuperFileView";

    private Context mContext;
    private OnGetFilePathListener mOnGetFilePathListener;

    private TbsReaderView tbsReaderView;

    public SuperFileView(Context context) {
        this(context, null, 0);
    }

    public SuperFileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperFileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        tbsReaderView = new TbsReaderView(context, this);
        addView(tbsReaderView, new LinearLayout.LayoutParams(-1, -1));
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {
        LogUtil.e("****************************************************" + integer);
    }

    public void onStopDisplay() {
        if (tbsReaderView != null) {
            tbsReaderView.onStop();
        }
    }

    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";
        if (TextUtils.isEmpty(paramString)) {
            LogUtil.d(TAG, "paramString---->null");
            return str;
        }

        LogUtil.d(TAG, "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            LogUtil.d(TAG, "i <= -1");
            return str;
        }

        str = paramString.substring(i + 1);
        LogUtil.d(TAG, "paramString.substring(i + 1)------>" + str);
        return str;
    }

    public void setOnGetFilePathListener(OnGetFilePathListener onGetFilePathListener) {
        mOnGetFilePathListener = onGetFilePathListener;
    }

    public void displayFile(File file) {
        if (file != null && !TextUtils.isEmpty(file.toString())) {
            // 解决 TbsReaderTemp 文件夹不存在导致的加载文件失败
            String bsReaderTemp = Environment.getExternalStorageDirectory() + File.separator + "TbsReaderTemp";
            File fileDir = new File(bsReaderTemp);
            if (!fileDir.exists()) {
                LogUtil.d("准备创建 /storage/emulated/0/TbsReaderTemp ！！");
                boolean mkdir = fileDir.mkdir();
                if (!mkdir) {
                    LogUtil.e("创建 /storage/emulated/0/TbsReaderTemp 失败！！！！！");
                }
            }

            // 加载文件
            Bundle bundle = new Bundle();
            bundle.putString("tempPath", bsReaderTemp);
            bundle.putString("filePath", file.toString());
            if (tbsReaderView == null) {
                tbsReaderView = getTbsReaderView(mContext);
            }
            boolean bool = tbsReaderView.preOpen(getFileType(file.toString()), false);
            if (bool) {
                tbsReaderView.openFile(bundle);
            }
        } else {
            LogUtil.e("文件路径无效！");
        }
    }

    public void show() {
        if (mOnGetFilePathListener != null) {
            mOnGetFilePathListener.onGetFilePath(this);
        }
    }

    /***
     * 将获取File路径的工作，“外包”出去
     */
    public interface OnGetFilePathListener {
        void onGetFilePath(SuperFileView superFileView);
    }
}