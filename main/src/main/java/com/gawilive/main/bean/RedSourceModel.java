package com.gawilive.main.bean;

import com.google.gson.annotations.SerializedName;

// 积分适配器
public class RedSourceModel {


    @SerializedName("id")
    private String id;
    @SerializedName("uid")
    private String uid;
    @SerializedName("before_red_score")
    private String beforeRedScore;
    @SerializedName("red_score")
    private String redScore;
    @SerializedName("after_red_score")
    private String afterRedScore;
    @SerializedName("addtime")
    private String addtime;
    @SerializedName("str")
    private String str;
    @SerializedName("desc")
    private String desc;

    @SerializedName("green_score")
    private String greenScore;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBeforeRedScore() {
        return beforeRedScore;
    }

    public void setBeforeRedScore(String beforeRedScore) {
        this.beforeRedScore = beforeRedScore;
    }

    public String getRedScore() {
        return redScore;
    }

    public void setRedScore(String redScore) {
        this.redScore = redScore;
    }

    public String getAfterRedScore() {
        return afterRedScore;
    }

    public void setAfterRedScore(String afterRedScore) {
        this.afterRedScore = afterRedScore;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getGreenScore() {
        return greenScore;
    }

    public void setGreenScore(String greenScore) {
        this.greenScore = greenScore;
    }
}
