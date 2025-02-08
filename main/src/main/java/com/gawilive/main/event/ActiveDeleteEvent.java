package com.gawilive.main.event;

public class ActiveDeleteEvent {

    private final String mActiveId;

    public ActiveDeleteEvent(String activeId) {
        mActiveId = activeId;
    }

    public String getActiveId() {
        return mActiveId;
    }
}
