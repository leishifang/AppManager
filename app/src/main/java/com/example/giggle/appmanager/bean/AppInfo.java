package com.example.giggle.appmanager.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by leishifang on 2017/3/21 14:33.
 */

public class AppInfo {

    /**
     * 应用名
     */
    private String lable;
    /**
     * 包名
     */
    private String packageNmae;
    private String versionName;
    private Drawable icon;
    /**
     * 应用占用空间 cace+code+data
     */
    private long size;
    /**
     * 第一次安装时间
     */
    private long firstInstallTime;
    /**
     * 最近一次更新时间
     */
    private long lastUpdateTime;

    public AppInfo() {
    }

    public AppInfo(String lable, String packageNmae, String versionName, Drawable icon, long size, long
            firstInstallTime, long lastUpdateTime) {
        this.lable = lable;
        this.packageNmae = packageNmae;
        this.versionName = versionName;
        this.icon = icon;
        this.size = size;
        this.firstInstallTime = firstInstallTime;
        this.lastUpdateTime = lastUpdateTime;
    }

    public AppInfo(String lable, String packageNmae, String version, Drawable icon, long size) {
        this(lable, packageNmae, version, icon, size, 0, 0);
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getPackageNmae() {
        return packageNmae;
    }

    public void setPackageNmae(String packageNmae) {
        this.packageNmae = packageNmae;
    }

    public String getVersion() {
        return versionName;
    }

    public void setVersion(String version) {
        this.versionName = version;
    }

    public long getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setFirstInstallTime(long firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
