package com.gawilive.main.event;

public class ActiveLikeEvent {
    private final int mFrom;
    private final String mActiveId;
    private final int mIsLike;//是否点赞了
    private final int mLikeNum;//点赞数


    public ActiveLikeEvent(int from, String activeId, int isLike, int likeNum) {
        mFrom = from;
        mActiveId = activeId;
        mIsLike = isLike;
        mLikeNum = likeNum;
    }


    public int getFrom() {
        return mFrom;
    }

    public String getActiveId() {
        return mActiveId;
    }

    public int getIsLike() {
        return mIsLike;
    }

    public int getLikeNum() {
        return mLikeNum;
    }
}

