package com.example.giggle.appmanager.ui;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.adapter.ApkAdapter;
import com.example.giggle.appmanager.bean.ApkInfo;
import com.example.giggle.appmanager.utils.PermissionUtils;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private ApkAdapter apkAdapter;

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
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission
                    (Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                getData();
            }
        } else {
            getData();
        }
    }

    private void initView() {
    }

    private void getData() {

        showProgress();

        Observable.create(new ObservableOnSubscribe<List<ApkInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ApkInfo>> e) throws Exception {
                e.onNext(getApkInfo());
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ApkInfo>>() {
                    @Override
                    public void accept(List<ApkInfo> apkInfos) throws Exception {
                        fillAdapter(apkInfos);
                    }
                });
    }

    private void fillAdapter(List<ApkInfo> infos) {
        if (infos.size() <= 0) {
            mTvEmpty.setVisibility(View.VISIBLE);
        } else {
            mLayoutManager = new LinearLayoutManager(this);

            mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(null);
            mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

            mRecyclerViewExpandableItemManager.setOnGroupCollapseListener(this);
            mRecyclerViewExpandableItemManager.setOnGroupExpandListener(this);

            apkAdapter = new ApkAdapter(infos, this);

            mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(apkAdapter);
            mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);

            final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
            animator.setSupportsChangeAnimations(false);
            //指示箭头动画
            mRecyclerView.setItemAnimator(animator);

            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.setAdapter(mWrappedAdapter);

            mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);
            mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
            mTvEmpty.setVisibility(View.GONE);
        }
        hideProgress();
    }

    private ArrayList<ApkInfo> getApkInfo() {
        PackageManager pm = this.getPackageManager();
        PackageInfo packageInfo;
        ArrayList<ApkInfo> apkInfos = new ArrayList<>();

        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), new
                String[]{MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE, MediaStore
                .MediaColumns.DATE_MODIFIED, MediaStore.MediaColumns.TITLE}, null, null, null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                if (path != null && path.endsWith(".apk")) {
                    packageInfo = pm.getPackageArchiveInfo(path, 0);
                    if (packageInfo == null) {
                        continue;
                    }
                    ApkInfo apkInfo = new ApkInfo();
                    packageInfo.applicationInfo.publicSourceDir = path;
                    packageInfo.applicationInfo.sourceDir = path;
                    Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
                    apkInfo.setIcon(icon);
                    apkInfo.setApkPath(path);
                    apkInfo.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
                    apkInfo.setTime(cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns
                            .DATE_MODIFIED)));
//                    apkInfo.setLable(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns
// .TITLE)));
                    apkInfo.setLable(packageInfo.applicationInfo.loadLabel(pm).toString());
                    apkInfo.setVersionName(packageInfo.versionName);
                    apkInfo.setPackageName(packageInfo.packageName);
                    apkInfos.add(apkInfo);

                    updateProgress(apkInfos.size());
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return apkInfos;
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser, Object payload) {

    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser, Object payload) {
        if (fromUser) {
            adjustScrollPositionOnGroupExpanded(groupPosition);
        }
    }

    private void adjustScrollPositionOnGroupExpanded(int groupPosition) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getData();
                } else {
                    finish();
                }
        }
    }

    private void updateProgress(final int progress) {

        mTvProgress.post(new Runnable() {
            @Override
            public void run() {
                mTvProgress.setText(progress + "");
            }
        });
    }

    public void showProgress() {
        mImgLoading.show();
        mTvProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mImgLoading.hide();
        mTvProgress.setVisibility(View.INVISIBLE);
    }
}
