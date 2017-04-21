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
    /**
     * 安装包版本名
     */
    private String versionName;
    /**
     * 包名
     */
    private String packageName;
    /**
     * ID 在apkAdapter中swipe功能getGroupId方法使用
     */
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "ApkInfo{" +
                "size=" + size +
                ", time=" + time +
                ", apkPath='" + apkPath + '\'' +
                "} " + super.toString();
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
