package com.gawilive.beauty.bean;

public class TeXiaoWaterBean {

    private final int mThumb;
    private final int mRes;
    private final int mPositon;
    private boolean mChecked;

    public TeXiaoWaterBean(int thumb, int res, int positon) {
        mThumb = thumb;
        mRes = res;
        mPositon = positon;
    }


    public TeXiaoWaterBean(int thumb, int res, int positon, boolean checked) {
        this(thumb, res, positon);
        mChecked = checked;
    }

    public int getThumb() {
        return mThumb;
    }

    public int getRes() {
        return mRes;
    }

    public int getPositon() {
        return mPositon;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
