package com.gawilive.game.event;

/**
 * Created by cxf on 2019/3/21.
 */

public class GameWindowChangedEvent {

    private final boolean mOpen;
    private final int mGameViewHeight;

    public GameWindowChangedEvent(boolean open, int gameViewHeight) {
        mOpen = open;
        mGameViewHeight = gameViewHeight;
    }

    public int getGameViewHeight() {
        return mGameViewHeight;
    }

    public boolean isOpen() {
        return mOpen;
    }
}
