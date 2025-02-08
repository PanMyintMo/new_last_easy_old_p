package com.gawilive.im.bean;

import java.io.File;

/**
 * Created by cxf on 2018/7/12.
 * IM 消息实体类
 */

public class ImMessageBean {

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VOICE = 3;
    public static final int TYPE_LOCATION = 4;
    public static final int TYPE_GOODS = 5;
    public static final int TYPE_PROMPT = 9;

    private String uid;//发消息的人的id
    private int type;
    private boolean fromSelf;
    private long time;
    private File imageFile;
    private File imageFile2;
    private boolean loading;
    private boolean sendFail;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFromSelf() {
        return fromSelf;
    }

    public void setFromSelf(boolean fromSelf) {
        this.fromSelf = fromSelf;
    }

    public long getTime() {
        return time;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }


    public File getImageFile2() {
        return imageFile2;
    }

    public void setImageFile2(File imageFile2) {
        this.imageFile2 = imageFile2;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isSendFail() {
        return sendFail;
    }

    public void setSendFail(boolean sendFail) {
        this.sendFail = sendFail;
    }

    public int getVoiceDuration() {
        int duration = 0;

        return duration;
    }

    public boolean isRead() {
        return false;
    }

    public int getMessageId() {
        return -1;
    }

}
