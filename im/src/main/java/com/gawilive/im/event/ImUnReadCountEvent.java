package com.gawilive.im.event;

/**
 * Created by cxf on 2018/10/24.
 */

public class ImUnReadCountEvent {

    private final String mUnReadCount;
    private final String mLiveRoomUnReadCount;


    public ImUnReadCountEvent(String unReadCount, String liveRoomUnReadCount) {
        mUnReadCount = unReadCount;
        mLiveRoomUnReadCount = liveRoomUnReadCount;
    }

    public String getUnReadCount() {
        return mUnReadCount;
    }

    public String getLiveRoomUnReadCount() {
        return mLiveRoomUnReadCount;
    }
}
