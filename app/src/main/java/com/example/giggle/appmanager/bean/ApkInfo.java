package com.example.giggle.appmanager.bean;

/**
 * Created by leishifang on 2017/4/18 14:43.
 */

public class ApkInfo extends BaseInfo {

    /**
     * 安装包大小
     */
    private long size;
    /**
     * 最后一次修改时间
     */
    private long time;

    /**
     * 安装包存放路径
     */
    private String apkPath;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }
}
