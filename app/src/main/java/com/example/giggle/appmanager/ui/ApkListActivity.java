package com.example.giggle.appmanager.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.bean.ApkInfo;
import com.example.giggle.appmanager.utils.PermissionUtils;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by leishifang on 2017/4/18 11:05.
 */

public class ApkListActivity extends AppCompatActivity implements RecyclerViewExpandableItemManager
        .OnGroupCollapseListener, RecyclerViewExpandableItemManager.OnGroupExpandListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private static final String TAG = "ApkListActivity";
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.img_loading)
    AVLoadingIndicatorView mImgLoading;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission
                    (Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                getApkInfo();
            }
        } else {
            getApkInfo();
        }
    }

    private ArrayList<ApkInfo> getApkInfo() {
        PackageManager pm = this.getPackageManager();

        ArrayList<ApkInfo> apkInfos = new ArrayList<>();
        ApkInfo apkInfo = new ApkInfo();

        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), new
                String[]{MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE, MediaStore
                .MediaColumns.DATE_MODIFIED, MediaStore.MediaColumns.TITLE}, null, null, null);

        Log.d(TAG, "cursor: " + cursor + cursor.getCount());

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                if (path!=null && path.endsWith(".apk")){
                    apkInfo.setApkPath(path);
                    apkInfo.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
                    apkInfo.setTime(cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)));
                    apkInfo.setLable(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)));

                    Log.d(TAG, "getApkInfo: " + apkInfo.toString());
                }
            } while (cursor.moveToNext());
        }
        return apkInfos;
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser, Object payload) {

    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser, Object payload) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getApkInfo();
                } else {
                    finish();
                }
        }
    }
}
