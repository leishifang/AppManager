package com.example.giggle.appmanager;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.giggle.appmanager.ui.ApkListActivity;
import com.example.giggle.appmanager.ui.AppListActivity;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.progress_storage)
    CircleProgressBar mProgressStorage;
    @BindView(R.id.progress_memory)
    CircleProgressBar mProgressMemory;
    @BindView(R.id.btn_app)
    RelativeLayout mBtnApp;
    @BindView(R.id.btn_apk)
    RelativeLayout mBtnApk;
    @BindView(R.id.btn_processes)
    RelativeLayout mBtnProcesses;
    /**
     * 已使用的内存百分比
     */
    private int radioUsedLast = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        updateStorageState();
        updateMemoryState();
        /**
         * 每秒一次，更新内存使用情况
         */
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        updateMemoryState();
                    }
                });
    }

    private void initView() {
        mBtnApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AppListActivity.class);
                startActivity(intent);
            }
        });
        mBtnApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ApkListActivity.class);
                startActivity(intent);
            }
        });
        mBtnProcesses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void updateMemoryState() {
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getMemoryInfo(info);
        int ratioUsed = (int) ((info.totalMem - info.availMem) * 100 / info.totalMem);
        ValueAnimator animator = ValueAnimator.ofInt(radioUsedLast, ratioUsed);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mProgressMemory.setProgress((Integer) valueAnimator.getAnimatedValue());
            }
        });
        animator.setDuration(1000);
        animator.start();
        radioUsedLast = ratioUsed;
    }

    public void updateStorageState() {
        long totalSpace = 0;
        long usableSpace = 0;
        if (getExternalStorageState()) {
            totalSpace += Environment.getExternalStorageDirectory().getTotalSpace();
            usableSpace += Environment.getExternalStorageDirectory().getUsableSpace();
        }
        totalSpace += Environment.getDataDirectory().getTotalSpace();
        usableSpace += Environment.getDataDirectory().getUsableSpace();
        int ratioUsed = (int) ((totalSpace - usableSpace) * 100 / totalSpace);

        ValueAnimator animator = ValueAnimator.ofInt(0, ratioUsed);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mProgressStorage.setProgress((int) valueAnimator.getAnimatedValue());
            }
        });
        animator.setDuration(2000);
        animator.start();
    }

    private boolean getExternalStorageState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                && Environment.isExternalStorageRemovable();
    }
}
