package com.example.giggle.appmanager.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by leishifang on 2017/3/21 14:33.
 */

public class AppInfo {

    private String lable;
    private String packageNmae;
    private String version;
    private Drawable icon;
    private long size;

    public AppInfo() {
    }

    public AppInfo(String lable, String packageNmae, String version, Drawable icon, long size) {
        this.lable = lable;
        this.packageNmae = packageNmae;
        this.version = version;
        this.icon = icon;
        this.size = size;
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
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
