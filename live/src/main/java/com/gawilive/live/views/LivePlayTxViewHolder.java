package com.gawilive.live.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.L;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.live.R;
import com.gawilive.live.activity.LiveAudienceActivity;
import com.gawilive.live.http.LiveHttpConsts;
import com.gawilive.live.http.LiveHttpUtil;

/**
 * Created by cxf on 2018/10/10.
 * 直播间播放器  腾讯播放器
 */

public class LivePlayTxViewHolder extends LiveRoomPlayViewHolder implements ITXLivePlayListener {

    private static final String TAG = "LiveTxPlayViewHolder";
    private ViewGroup mRoot;
    private ViewGroup mSmallContainer;
    private ViewGroup mLeftContainer;
    private ViewGroup mRightContainer;
    private ViewGroup mPkContainer;
    private TXCloudVideoView mVideoView;
    private View mLoading;
    private ImageView mCover;
    private TXLivePlayer mPlayer;
    private boolean mPaused;//是否切后台了
    private boolean mStarted;//是否开始了播放
    private boolean mEnd;//是否结束了播放
    private boolean mPausedPlay;//是否被动暂停了播放
    private boolean mChangeToLeft;
    private boolean mChangeToAnchorLinkMic;
    private String mUrl;
    private int mPlayType;
    private HttpCallback mGetTxLinkMicAccUrlCallback;
    private Handler mHandler;
    private int mVideoLastProgress;
    private float mVideoWidth;
    private float mVideoHeight;
    private int mRootHeight;


    public LivePlayTxViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_play_tx;
    }

    @Override
    public void init() {
        mRoot = findViewById(R.id.root);
        mRoot.post(new Runnable() {
            @Override
            public void run() {
                mRootHeight = mRoot.getHeight();
            }
        });
        mSmallContainer = (ViewGroup) findViewById(R.id.small_container);
        mLeftContainer = (ViewGroup) findViewById(R.id.left_container);
        mRightContainer = (ViewGroup) findViewById(R.id.right_container);
        mPkContainer = (ViewGroup) findViewById(R.id.pk_container);
        mLoading = findViewById(R.id.loading);
        mCover = (ImageView) findViewById(R.id.cover);
        mVideoView = (TXCloudVideoView) findViewById(R.id.video_view);

        mPlayer = new TXLivePlayer(mContext);
        mPlayer.setPlayListener(this);
        mPlayer.setPlayerView(mVideoView);
        mPlayer.enableHardwareDecode(false);
        mPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        TXLivePlayConfig playConfig = new TXLivePlayConfig();
        playConfig.setAutoAdjustCacheTime(true);
        playConfig.setMaxAutoAdjustCacheTime(5.0f);
        playConfig.setMinAutoAdjustCacheTime(1.0f);
        playConfig.setHeaders(CommonAppConfig.HEADER);
        mPlayer.setConfig(playConfig);

    }


    @Override
    public void onPlayEvent(int e, Bundle bundle) {
        if (mEnd) {
            return;
        }
        switch (e) {
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN://播放开始
                if (mLoading != null && mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                if (mLoading != null && mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME://第一帧
                hideCover();
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END://播放结束
                replay();
                break;
            case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION://获取视频宽高
                if (mChangeToLeft || mChangeToAnchorLinkMic) {
                    return;
                }
                mVideoWidth = bundle.getInt("EVT_PARAM1", 0);
                mVideoHeight = bundle.getInt("EVT_PARAM2", 0);
                changeVideoSize(false);
                break;
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT://播放失败
            case TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND:
                ToastUtil.show(WordUtil.getString(R.string.live_play_error));
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                int progress = bundle.getInt("EVT_PLAY_PROGRESS_MS");
                if (mVideoLastProgress == progress) {
                    replay();
                } else {
                    mVideoLastProgress = progress;
                }
                break;
        }
    }


    public void changeVideoSize(boolean landscape) {
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            float videoRatio = mVideoWidth / mVideoHeight;
            float p1 = mParentView.getWidth();
            float p2 = mParentView.getHeight();
            float parentWidth = p1;
            float parentHeight = p2;
            if (landscape) {
                parentWidth = Math.max(p1, p2);
                parentHeight = Math.min(p1, p2);
            } else {
                parentWidth = Math.min(p1, p2);
                parentHeight = Math.max(p1, p2);
            }
//            L.e("changeVideoSize", "mVideoWidth----->" + mVideoWidth + "  mVideoHeight------>" + mVideoHeight);
//            L.e("changeVideoSize", "parentWidth----->" + parentWidth + "  parentHeight------>" + parentHeight);
            float parentRatio = parentWidth / parentHeight;
            if (videoRatio != parentRatio) {
                FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
                if (videoRatio > 10f / 16f && videoRatio > parentRatio) {
                    p.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    p.height = (int) (parentWidth / videoRatio);
                    p.gravity = Gravity.CENTER;
                } else {
                    p.width = (int) (parentHeight * videoRatio);
                    p.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    p.gravity = Gravity.CENTER;
                }
                mVideoView.requestLayout();
                View innerView = mVideoView.getVideoView();
                ViewGroup.LayoutParams innerLp = innerView.getLayoutParams();
                innerLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                innerLp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                innerView.setLayoutParams(innerLp);

                ((LiveAudienceActivity) mContext).onVideoHeightChanged(p.height, mRootHeight);
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }


    @Override
    public void hideCover() {
        if (mCover != null) {
            mCover.animate().alpha(0).setDuration(500).start();
        }
    }

    @Override
    public void setCover(String coverUrl) {
        if (mCover != null) {
            ImgLoader.displayBlur(mContext, coverUrl, mCover);
        }
    }

    /**
     * 循环播放
     */
    private void replay() {
        if (mStarted && mPlayer != null) {
            mPlayer.resume();
        }
    }


    /**
     * 暂停播放
     */
    @Override
    public void pausePlay() {
        if (!mPausedPlay) {
            mPausedPlay = true;
            if (!mPaused) {
                if (mPlayer != null) {
                    mPlayer.setMute(true);
                }
            }
            if (mCover != null) {
                mCover.setAlpha(1f);
                if (mCover.getVisibility() != View.VISIBLE) {
                    mCover.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 暂停播放后恢复
     */
    @Override
    public void resumePlay() {
        if (mPausedPlay) {
            mPausedPlay = false;
            if (!mPaused) {
                if (mPlayer != null) {
                    mPlayer.setMute(false);
                }
            }
            hideCover();
        }
    }

    /**
     * 开始播放
     *
     * @param url 流地址
     */
    @Override
    public void play(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            int playType = -1;
            if (url.startsWith("rtmp://")) {
                playType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
            } else if (url.contains(".flv")) {
                playType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
            } else if (url.contains(".m3u8")) {
                playType = TXLivePlayer.PLAY_TYPE_LIVE_HLS;
            }
            if (playType == -1) {
                ToastUtil.show(R.string.live_play_error_2);
                return;
            }
            if (mPlayer != null) {
                int result = mPlayer.startLivePlay(url, playType);
                if (result == 0) {
                    mStarted = true;
                    mUrl = url;
                    mPlayType = playType;
                }
            }
            L.e(TAG, "play----url--->" + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopPlay() {
        mChangeToLeft = false;
        mChangeToAnchorLinkMic = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mCover != null) {
            mCover.setAlpha(1f);
            if (mCover.getVisibility() != View.VISIBLE) {
                mCover.setVisibility(View.VISIBLE);
            }
        }
        stopPlay2();
    }

    @Override
    public void stopPlay2() {
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
        }
    }

    @Override
    public void release() {
        mEnd = true;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        LiveHttpUtil.cancel(LiveHttpConsts.GET_TX_LINK_MIC_ACC_URL);
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        mPlayer = null;
        L.e(TAG, "release------->");
    }


    @Override
    public ViewGroup getSmallContainer() {
        return mSmallContainer;
    }


    @Override
    public ViewGroup getRightContainer() {
        return mRightContainer;
    }

    @Override
    public ViewGroup getPkContainer() {
        return mPkContainer;
    }

    @Override
    public void changeToLeft() {
        mChangeToLeft = true;
        if (mVideoView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
            params.width = mVideoView.getWidth() / 2;
            params.height = DpUtil.dp2px(250);
            params.topMargin = DpUtil.dp2px(130);
            mVideoView.setLayoutParams(params);
        }
        if (mLoading != null && mLeftContainer != null) {
            ViewParent viewParent = mLoading.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mLoading);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DpUtil.dp2px(24), DpUtil.dp2px(24));
            params.gravity = Gravity.CENTER;
            mLoading.setLayoutParams(params);
            mLeftContainer.addView(mLoading);
        }
    }

    @Override
    public void changeToBig() {
        mChangeToLeft = false;
        if (mVideoView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            mVideoView.setLayoutParams(params);
        }
        if (mLoading != null && mRoot != null) {
            ViewParent viewParent = mLoading.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mLoading);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DpUtil.dp2px(24), DpUtil.dp2px(24));
            params.gravity = Gravity.CENTER;
            mLoading.setLayoutParams(params);
            mRoot.addView(mLoading);
        }
    }

    @Override
    public void onResume() {
        if (!mPausedPlay && mPaused && mPlayer != null) {
            mPlayer.setMute(false);
        }
        mPaused = false;
    }

    @Override
    public void onPause() {
        if (!mPausedPlay && mPlayer != null) {
            mPlayer.setMute(true);
        }
        mPaused = true;
    }

    @Override
    public void onDestroy() {
        release();
        super.onDestroy();
    }

    /**
     * 腾讯sdk连麦时候切换低延时流
     */
    public void onLinkMicTxAccEvent(boolean linkMic) {
        if (mStarted && mPlayer != null && !TextUtils.isEmpty(mUrl)) {
            mPlayer.stopPlay(false);
            if (linkMic) {
                if (mGetTxLinkMicAccUrlCallback == null) {
                    mGetTxLinkMicAccUrlCallback = new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                if (obj != null) {
                                    String accUrl = obj.getString("streamUrlWithSignature");
                                    if (!TextUtils.isEmpty(accUrl) && mPlayer != null) {
                                        L.e(TAG, "低延时流----->" + accUrl);
                                        mPlayer.startLivePlay(accUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
                                    }
                                }
                            }
                        }
                    };
                }
                LiveHttpUtil.getTxLinkMicAccUrl(mUrl, mGetTxLinkMicAccUrlCallback);
            } else {
                mPlayer.startLivePlay(mUrl, mPlayType);
            }
        }
    }

    /**
     * 设置主播连麦模式
     *
     * @param anchorLinkMic
     */
    public void setAnchorLinkMic(final boolean anchorLinkMic, int delayTime) {
        if (mVideoView == null) {
            return;
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mChangeToAnchorLinkMic = anchorLinkMic;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
                if (anchorLinkMic) {
                    params.height = DpUtil.dp2px(250);
                    params.topMargin = DpUtil.dp2px(130);
                    params.gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
                } else {
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.topMargin = 0;
                    params.gravity = Gravity.CENTER;
                }
                mVideoView.setLayoutParams(params);

            }
        }, delayTime);

    }
}
