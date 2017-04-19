package com.example.giggle.appmanager.bean;

/**
 * Created by leishifang on 2017/4/19 16:59.
 */

public class ProcessInfo extends BaseInfo {

    private int pid;

    private String processName;

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
