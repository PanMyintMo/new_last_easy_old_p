package com.gawilive.live.event;

/**
 * Created by cxf on 2019/3/25.
 */

public class LinkMicTxAccEvent {

    private final boolean mLinkMic;

    public LinkMicTxAccEvent(boolean linkMic) {
        mLinkMic = linkMic;
    }

    public boolean isLinkMic() {
        return mLinkMic;
    }
}
