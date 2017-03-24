package com.example.giggle.appmanager.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.adapter.AppAdapter;
import com.example.giggle.appmanager.bean.AppInfo;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * Created by leishifang on 2017/3/16 10:55.
 */

public class AppListActivity extends AppCompatActivity implements RecyclerViewExpandableItemManager
        .OnGroupExpandListener, RecyclerViewExpandableItemManager.OnGroupCollapseListener {

    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private static final String TAG = "APPLISTACTIVITY";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.img_loading)
    AVLoadingIndicatorView mImgLoading;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout_app);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        showProgress();
        final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable
                (SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);

        // TODO: 2017/3/23 未进行生命周期控制
        Observable.create(new ObservableOnSubscribe<List<AppInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<AppInfo>> e) throws Exception {
                e.onNext(getInfo());
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AppInfo>>() {
                    @Override
                    public void accept(List<AppInfo> appInfos) throws Exception {
                        setAppList(appInfos);
                    }
                });
    }

    private List<AppInfo> getInfo() {
        PackageManager pm = getApplicationContext().getPackageManager();
        final List<PackageInfo> infos = pm.getInstalledPackages(0);
        final List<AppInfo> appInfos = new ArrayList<AppInfo>();
        final int[] progress = {0};
        for (PackageInfo info : infos) {
            String label = pm.getApplicationLabel(info.applicationInfo).toString();
            String packageName = info.packageName;
            String version = info.versionName;
            Drawable icon = pm.getApplicationIcon(info.applicationInfo);
            Long installTime = info.firstInstallTime;
            Long updateTime = info.lastUpdateTime;
            String path = info.applicationInfo.sourceDir;
            // TODO: 2017/3/24 弄懂flag是啥
            boolean isSys = !((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0);
            final AppInfo tempInfo = new AppInfo(label, packageName, version, icon, 0, installTime,
                    updateTime, path, isSys);

            try {
                Method mGetPackageSizeInfoMethod;
                mGetPackageSizeInfoMethod = PackageManager.class.getMethod("getPackageSizeInfo", new
                        Class[]{String.class, IPackageStatsObserver.class});
                mGetPackageSizeInfoMethod.invoke(pm, new Object[]{
                        packageName,
                        new IPackageStatsObserver.Stub() {
                            @Override
                            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                                    throws RemoteException {
                                //异步操作，appinfos需要等待所有item添加完毕
                                tempInfo.setSize(pStats.cacheSize + pStats.codeSize + pStats
                                        .dataSize);
                                appInfos.add(tempInfo);
                                updateProgress(progress[0]++, infos.size());
                            }
                        }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        //等待所有item添加完毕
        while (appInfos.size() < infos.size()) {
        }
        // 按照应用名排序
        Collections.sort(appInfos, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo info, AppInfo t1) {
                return info.getLable().compareTo(t1.getLable());
            }
        });
        return appInfos;
    }

    private void setAppList(List<AppInfo> infos) {
        mRecyclerViewExpandableItemManager.setOnGroupExpandListener(this);
        mRecyclerViewExpandableItemManager.setOnGroupCollapseListener(this);

        //adapter
        final AppAdapter myItemAdapter = new AppAdapter(infos);

        mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(myItemAdapter);       //
        // wrap for expanding

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        animator.setSupportsChangeAnimations(false);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);

        hideProgress();
    }

    private void updateProgress(final int progress, final int total) {

        mTvProgress.post(new Runnable() {
            @Override
            public void run() {
                mTvProgress.setText(progress + "/" + total);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save current state to support screen rotation, etc...
        if (mRecyclerViewExpandableItemManager != null) {
            outState.putParcelable(
                    SAVED_STATE_EXPANDABLE_ITEM_MANAGER,
                    mRecyclerViewExpandableItemManager.getSavedState());
        }
    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser, Object payload) {
        if (fromUser) {
            adjustScrollPositionOnGroupExpanded(groupPosition);
        }
    }

    private void adjustScrollPositionOnGroupExpanded(int groupPosition) {
        // TODO: 2017/3/24 scroll to the last group exactly
        int childItemHeight = this.getResources().getDimensionPixelSize(R.dimen.list_item_height);
        int topMargin = (int) (this.getResources().getDisplayMetrics().density * 16); //
        // top-spacing: 16dp
        int bottomMargin = topMargin; // bottom-spacing: 16dp
        mRecyclerViewExpandableItemManager.scrollToGroup(groupPosition, childItemHeight * 2, topMargin,
                bottomMargin);
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser, Object payload) {

    }
}
