package com.example.giggle.appmanager.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.bean.ProcessInfo;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
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
 * Created by leishifang on 2017/4/19 16:45.
 */

public class ProcessActivity extends AppCompatActivity {

    private static final String TAG = ProcessActivity.class.toString();
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
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

        Observable.create(new ObservableOnSubscribe<List<ProcessInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ProcessInfo>> e) throws Exception {
                List<ProcessInfo> infos = getRunningProcessesInfo();
                e.onNext(infos);
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProcessInfo>>() {
                    @Override
                    public void accept(List<ProcessInfo> processInfos) throws Exception {

                    }
                });
    }

    private List<ProcessInfo> getRunningProcessesInfo() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager packageManager = this.getPackageManager();
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        List<ProcessInfo> infos = new ArrayList<ProcessInfo>();

        for (AndroidAppProcess process : processes) {
            ProcessInfo info = new ProcessInfo();
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(process.getPackageName(),
                        0);
                info.setLable(applicationInfo.loadLabel(packageManager).toString());
                info.setIcon(applicationInfo.loadIcon(packageManager));
                info.setProcessName(process.name);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return infos;
    }

    private void fillAdapter(List<AndroidAppProcess> infos) {

    }
}
