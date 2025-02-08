package com.gawilive.live.event;

public class LiveVoiceMicStatusEvent {
    private final int mPosition;
    private final int mStatus;

    public LiveVoiceMicStatusEvent(int position, int status) {
        mPosition = position;
        mStatus = status;
    }

    public int getPosition() {
        return mPosition;
    }

    public int getStatus() {
        return mStatus;
    }
}
