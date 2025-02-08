package com.gawilive.video.event;

/**
 * Created by cxf on 2018/12/15.
 */

public class VideoDeleteEvent {

    private final String mVideoId;

    public VideoDeleteEvent(String videoId) {
        mVideoId = videoId;
    }

    public String getVideoId() {
        return mVideoId;
    }
}
