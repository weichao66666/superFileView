package com.silang.superfileview.model;

import android.text.TextUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoadFileModel {
    public static void loadPdfFile(String url, Callback<ResponseBody> callback) {
        if (!TextUtils.isEmpty(url)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.baidu.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            LoadFileApi loadFileApi = retrofit.create(LoadFileApi.class);
            Call<ResponseBody> call = loadFileApi.loadPdfFile(url);
            call.enqueue(callback);
        }
    }
}