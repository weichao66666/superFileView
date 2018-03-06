package com.silang.superfileview.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.silang.superfileview.R;
import com.silang.superfileview.model.LoadFileModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.silang.superfileview.util.LogUtil;
import com.silang.superfileview.util.Md5Util;
import com.silang.superfileview.widget.SuperFileView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileDisplayActivity extends AppCompatActivity {
    private static final String TAG = "FileDisplayActivity";

    private SuperFileView superFileView;

    private String filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display);
        init();
    }

    public void init() {
        superFileView = findViewById(R.id.superFileView);
        superFileView.setOnGetFilePathListener(new SuperFileView.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(SuperFileView superFileView) {
                getFilePathAndShowFile(superFileView);
            }
        });

        Intent intent = this.getIntent();
        String path = (String) intent.getSerializableExtra("path");
        if (!TextUtils.isEmpty(path)) {
            LogUtil.d(TAG, "文件path:" + path);
            setFilePath(path);
        }
        superFileView.show();
    }

    private void getFilePathAndShowFile(SuperFileView superFileView) {
        if (getFilePath().contains("http")) {//网络地址要先下载
            downLoadFromNet(getFilePath(), superFileView);
        } else {
            superFileView.displayFile(new File(getFilePath()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("FileDisplayActivity-->onDestroy");
        if (superFileView != null) {
            superFileView.onStopDisplay();
        }
    }


    public static void show(Context context, String url) {
        Intent intent = new Intent(context, FileDisplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("path", url);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void setFilePath(String fileUrl) {
        this.filePath = fileUrl;
    }

    private String getFilePath() {
        return filePath;
    }

    private void downLoadFromNet(final String url, final SuperFileView superFileView) {
        //1.网络下载、存储路径、
        File cacheFile = getCacheFile(url);
        if (cacheFile.exists()) {
            if (cacheFile.length() <= 0) {
                LogUtil.d(TAG, "删除空文件！！");
                cacheFile.delete();
                return;
            }
        }

        LoadFileModel.loadPdfFile(url, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                LogUtil.d(TAG, "下载文件-->onResponse");
                boolean flag;
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    ResponseBody responseBody = response.body();
                    is = responseBody.byteStream();
                    long total = responseBody.contentLength();

                    File file1 = getCacheFileDir();
                    if (!file1.exists()) {
                        file1.mkdirs();
                        LogUtil.d(TAG, "创建缓存目录： " + file1.toString());
                    }

                    //fileN : /storage/emulated/0/pdf/kauibao20170821040512.pdf
                    File fileN = getCacheFile(url);//new File(getCacheDir(url), getFileName(url))

                    LogUtil.d(TAG, "创建缓存文件： " + fileN.toString());
                    if (!fileN.exists()) {
                        boolean mkdir = fileN.createNewFile();
                    }
                    fos = new FileOutputStream(fileN);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        LogUtil.d(TAG, "写入缓存文件" + fileN.getName() + "进度: " + progress);
                    }
                    fos.flush();
                    LogUtil.d(TAG, "文件下载成功,准备展示文件。");
                    //2.ACache记录文件的有效期
                    superFileView.displayFile(fileN);
                } catch (Exception e) {
                    LogUtil.d(TAG, "文件下载异常 = " + e.toString());
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d(TAG, "文件下载失败");
                File file = getCacheFile(url);
                if (!file.exists()) {
                    LogUtil.d(TAG, "删除下载失败文件");
                    file.delete();
                }
            }
        });
    }

    /***
     * 获取缓存目录
     *
     * @return
     */
    private File getCacheFileDir() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/007/");
    }

    /***
     * 绝对路径获取缓存文件
     *
     * @param url
     * @return
     */
    private File getCacheFile(String url) {
        File cacheFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/007/" + getFileName(url));
        LogUtil.d(TAG, "缓存文件 = " + cacheFile.toString());
        return cacheFile;
    }

    /***
     * 根据链接获取文件名（带类型的），具有唯一性
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        return Md5Util.hashKey(url) + "." + getFileType(url);
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
}