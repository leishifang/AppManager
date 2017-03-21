package com.example.giggle.appmanager.ui;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.adapter.AppAdapter;
import com.example.giggle.appmanager.bean.AppInfo;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Method;
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
 * Created by leishifang on 2017/3/16 10:55.
 */

public class AppListActivity extends AppCompatActivity implements RecyclerViewExpandableItemManager
        .OnGroupExpandListener, RecyclerViewExpandableItemManager.OnGroupCollapseListener {

    private static final String TAG = "APPLISTACTIVITY";
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.img_loading)
    AVLoadingIndicatorView mImgLoading;
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

        mImgLoading.show();

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
        List<PackageInfo> infos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        for (PackageInfo info : infos) {

            String label = pm.getApplicationLabel(info.applicationInfo).toString();
            String packageName = info.packageName;
            String version = info.versionName;
            Drawable icon = pm.getApplicationIcon(info.applicationInfo);

            final AppInfo tempInfo = new AppInfo(label, packageName, version, icon, 0);

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
                                tempInfo.setSize(pStats.cacheSize + pStats.codeSize + pStats
                                        .dataSize);
                            }
                        }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            appInfos.add(tempInfo);
        }
        return appInfos;
    }

    private void setAppList(List<AppInfo> infos) {

        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(null);
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
        mRecyclerView.setHasFixedSize(false);

        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);

        mImgLoading.hide();
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser, Object payload) {
        if (fromUser) {
            adjustScrollPositionOnGroupExpanded(groupPosition);
        }
    }

    private void adjustScrollPositionOnGroupExpanded(int groupPosition) {
        int childItemHeight = this.getResources().getDimensionPixelSize(R.dimen.list_item_height);
        int topMargin = (int) (this.getResources().getDisplayMetrics().density * 16); //
        // top-spacing: 16dp
        int bottomMargin = topMargin; // bottom-spacing: 16dp

        mRecyclerViewExpandableItemManager.scrollToGroup(groupPosition, childItemHeight, topMargin,
                bottomMargin);
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser, Object payload) {

    }
}
