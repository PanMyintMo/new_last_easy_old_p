package com.gawilive.video.activity;



import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.utils.KeyBoardUtil;
import com.gawilive.video.R;
import com.gawilive.video.views.VideoCommentViewHolder;

/**
 * Created by cxf on 2019/3/11.
 */

public abstract class AbsVideoCommentActivity extends AbsActivity implements KeyBoardUtil.KeyBoardHeightListener {

    protected VideoCommentViewHolder mVideoCommentViewHolder;
    private KeyBoardUtil mKeyBoardUtil;

    @Override
    protected void main() {
        super.main();
        mKeyBoardUtil = new KeyBoardUtil(findViewById(android.R.id.content), this);
    }


    /**
     * 显示评论
     */
    public void openCommentWindow(boolean showComment, boolean openFace,boolean openKeyBoard, String videoId, String videoUid) {
        if (mVideoCommentViewHolder == null) {
            mVideoCommentViewHolder = new VideoCommentViewHolder(mContext, findViewById(R.id.root));
            mVideoCommentViewHolder.addToParent();
        }
        mVideoCommentViewHolder.setVideoInfo(videoId, videoUid);
        mVideoCommentViewHolder.showBottom(showComment,openFace,openKeyBoard);
    }

    /**
     * 选择要@的人
     */
    public void chooseAtUser(){
        mVideoCommentViewHolder.chooseAtUser();
    }

    @Override
    public void onKeyBoardHeightChanged(int keyboardHeight) {
        if (mVideoCommentViewHolder != null) {
            mVideoCommentViewHolder.onKeyBoardHeightChanged(keyboardHeight);
        }
    }

    public void release() {
        if (mKeyBoardUtil != null) {
            mKeyBoardUtil.release();
        }
        mKeyBoardUtil = null;
        if (mVideoCommentViewHolder != null) {
            mVideoCommentViewHolder.release();
        }
        mVideoCommentViewHolder = null;
    }

}
