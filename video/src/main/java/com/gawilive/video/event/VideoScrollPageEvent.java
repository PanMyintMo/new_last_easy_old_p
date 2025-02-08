package com.gawilive.video.event;

/**
 * Created by cxf on 2018/12/15.
 */

public class VideoScrollPageEvent {

    private final String mKey;
    private final int mPage;

    public VideoScrollPageEvent(String key, int page) {
        mKey = key;
        mPage = page;
    }

    public String getKey() {
        return mKey;
    }

    public int getPage() {
        return mPage;
    }

}
