package com.silang.superfileview.view;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.silang.superfileview.R;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RecyclerView recyclerView;
    private List<String> dataList = new ArrayList<>();
    private List<String> pathList = new ArrayList<>();
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDataList();
        initPathList();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.recycler_item, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                Button button = holder.itemView.findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filePath = getFilePath(position);
                        if (!EasyPermissions.hasPermissions(MainActivity.this, PERMISSIONS)) {
                            EasyPermissions.requestPermissions(MainActivity.this, "需要访问手机存储权限！", 10086, PERMISSIONS);
                        } else {
                            FileDisplayActivity.show(MainActivity.this, filePath);
                        }
                    }
                });
                button.setText(getDataList().get(position));
            }

            @Override
            public int getItemCount() {
                return getDataList().size();
            }
        });
    }

    private void initDataList() {
        dataList.add("网络获取并打开doc文件");
        dataList.add("打开本地doc文件");
        dataList.add("打开本地txt文件");
        dataList.add("打开本地excel文件");
        dataList.add("打开本地ppt文件");
        dataList.add("打开本地pdf文件");
    }

    private void initPathList() {
    }

    private List<String> getDataList() {
        if (dataList != null && dataList.size() > 0) {
            return dataList;
        } else {
            dataList = new ArrayList<>();
            initDataList();
            return dataList;
        }
    }

    private String getFilePath(int position) {
        String path = null;
        switch (position) {
            case 0:
                path = "http://www.hrssgz.gov.cn/bgxz/sydwrybgxz/201101/P020110110748901718161.doc";
                break;
            case 1:
                path = "/storage/emulated/0/test.docx";
                break;
            case 2:
                path = "/storage/emulated/0/test.txt";
                break;
            case 3:
                path = "/storage/emulated/0/test.xlsx";
                break;
            case 4:
                path = "/storage/emulated/0/test.pptx";
                break;

            case 5:
                path = "/storage/emulated/0/test.pdf";
                break;
        }
        return path;
    }
}