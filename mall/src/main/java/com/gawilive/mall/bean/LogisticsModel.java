package com.gawilive.mall.bean;

public class LogisticsModel {

    private String acceptTime;

    private String acceptAddress;

    private String language;

    private String express;

    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getAcceptAddress() {
        return acceptAddress;
    }

    public void setAcceptAddress(String acceptAddress) {
        this.acceptAddress = acceptAddress;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }
}
