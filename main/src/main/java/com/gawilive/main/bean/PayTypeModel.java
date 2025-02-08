package com.gawilive.main.bean;

import com.google.gson.annotations.SerializedName;

public class PayTypeModel {


    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("device")
    private String device;
    @SerializedName("showname")
    private String showname;
    @SerializedName("status")
    private String status;

    @SerializedName("href")
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getShowname() {
        return showname;
    }

    public void setShowname(String showname) {
        this.showname = showname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
