package com.example.giggle.appmanager.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by leishifang on 2017/4/18 9:33.
 */

public class BaseInfo {

    /**
     * 名称
     */
    protected String lable;
    /**
     * 图标
     */
    protected Drawable icon;

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
