package com.gawilive.live.presenter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.bean.LevelBean;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.utils.CommonIconUtil;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.L;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.im.http.ImHttpUtil;
import com.gawilive.live.R;
import com.gawilive.live.activity.LiveActivity;
import com.gawilive.live.activity.LiveAnchorActivity;
import com.gawilive.live.event.LinkMicTxMixStreamEvent;
import com.gawilive.live.http.LiveHttpUtil;
import com.gawilive.live.interfaces.ILiveLinkMicViewHolder;
import com.gawilive.live.socket.SocketClient;
import com.gawilive.live.socket.SocketLinkMicAnchorUtil;
import com.gawilive.live.views.AbsLiveLinkMicPlayViewHolder;
import com.gawilive.live.views.LiveLinkMicPlayKsyViewHolder;
import com.gawilive.live.views.LiveLinkMicPlayTxViewHolder;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/11/16.
 * 主播与主播连麦逻辑
 */

public class LiveLinkMicAnchorPresenter implements View.OnClickListener {

    private final Context mContext;
    private final View mRoot;
    private final boolean mIsAnchor;//自己是否是主播
    private SocketClient mSocketClient;
    private final ViewGroup mRightContainer;
    private boolean mPlayingVideo;//是否播放对方主播的视频
    private AbsLiveLinkMicPlayViewHolder mLiveLinkMicPlayViewHolder;//连麦播放小窗口
    private String mPlayUrl;//自己直播间的播流地址
    private boolean mIsApplyDialogShow;//是否显示了申请连麦的弹窗
    private boolean mAcceptLinkMic;//是否接受连麦
    private boolean mIsLinkMic;//是否已经连麦了
    private String mApplyUid;//正在申请连麦的主播的uid
    private String mApplyStream;//正在申请连麦的主播的stream
    private String mPkUid;//正在连麦的对方主播的uid
    private TextView mLinkMicWaitText;
    private int mLinkMicWaitCount;//连麦弹窗等待倒计时
    private static final int LINK_MIC_COUNT_MAX = 10;
    private final String mLinkMicWaitString;
    private PopupWindow mLinkMicPopWindow;
    private Handler mHandler;
    private boolean mPaused;//是否执行了Activity周期的pause
    private long mLastApplyLinkMicTime;//上次申请连麦的时间
    private final ILiveLinkMicViewHolder mLiveRoomPlayViewHolder;
    private final int mLiveSdk;
    private String mSelfStream;//自己主播的stream

    private final View mPkFollowGroup;
    private final ImageView mPkFollowAvatar;
    private final TextView mPkFollowName;
    private final View mPkFollowIcon;

    public LiveLinkMicAnchorPresenter(Context context, ILiveLinkMicViewHolder linkMicViewHolder, boolean isAnchor, int liveSdk, View root) {
        mContext = context;
        mIsAnchor = isAnchor;
        mLiveSdk = liveSdk;
        mRoot = root;
        mPkFollowGroup = root.findViewById(R.id.group_pk_follow);
        mPkFollowAvatar = root.findViewById(R.id.avatar_pk_follow);
        mPkFollowName = root.findViewById(R.id.name_pk_follow);
        mPkFollowIcon = root.findViewById(R.id.icon_pk_follow);
        mLiveRoomPlayViewHolder = linkMicViewHolder;
        mRightContainer = linkMicViewHolder.getRightContainer();
        mLinkMicWaitString = WordUtil.getString(R.string.link_mic_wait);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mLinkMicWaitCount--;
                if (mLinkMicWaitCount > 0) {
                    if (mLinkMicWaitText != null) {
                        mLinkMicWaitText.setText(mLinkMicWaitString + "(" + mLinkMicWaitCount + "s)...");
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(0, 1000);
                        }
                    }
                } else {
                    if (mLinkMicPopWindow != null) {
                        mLinkMicPopWindow.dismiss();
                    }
                }
            }
        };
    }

    public void setSocketClient(SocketClient socketClient) {
        mSocketClient = socketClient;
    }

    public void setPlayUrl(String playUrl) {
        mPlayUrl = playUrl;
    }

    /**
     * 发起主播连麦申请
     *
     * @param pkUid  对方主播的uid
     * @param stream 自己直播间的stream
     */
    public void applyLinkMicAnchor(String pkUid, String playUrl, String stream) {
        if (!canOpenLinkMicAnchor()) {
            return;
        }
        mLastApplyLinkMicTime = System.currentTimeMillis();
        if (TextUtils.isEmpty(playUrl)) {
            playUrl = mPlayUrl;
        }
        SocketLinkMicAnchorUtil.linkMicAnchorApply(mSocketClient, playUrl, stream, pkUid);
        ToastUtil.show(R.string.link_mic_apply);
    }

    /**
     * 主播与主播连麦  主播收到其他主播发过来的连麦申请的回调
     *
     * @param u      对方主播的信息
     * @param stream 对方主播的stream
     */
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        if (!mIsAnchor) {
            return;
        }
        if (u == null || TextUtils.isEmpty(stream)) {
            return;
        }
        if (isLinkMic() || ((LiveActivity) mContext).isLinkMic()) {
            SocketLinkMicAnchorUtil.linkMicAnchorBusy(mSocketClient, u.getId());
            return;
        }
        if (((LiveActivity) mContext).isGamePlaying()) {
            SocketLinkMicAnchorUtil.linkMicPlayGaming(mSocketClient, u.getId());
            return;
        }
        if (!TextUtils.isEmpty(mApplyUid) && mApplyUid.equals(u.getId())) {
            return;
        }
        if (!mIsLinkMic && !mIsApplyDialogShow && System.currentTimeMillis() - mLastApplyLinkMicTime > 10000) {
            mApplyUid = u.getId();
            mApplyStream = stream;
            showApplyDialog(u);
        } else {
            SocketLinkMicAnchorUtil.linkMicAnchorBusy(mSocketClient, u.getId());
        }
    }

    /**
     * 显示申请连麦的弹窗
     */
    private void showApplyDialog(UserBean u) {
        if (mIsAnchor) {
            ((LiveAnchorActivity) mContext).hideLinkMicAnchorWindow();
        }
        mIsApplyDialogShow = true;
        mAcceptLinkMic = false;
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_link_mic_wait, null);
        ImageView avatar = v.findViewById(R.id.avatar);
        TextView name = v.findViewById(R.id.name);
        ImageView sex = v.findViewById(R.id.sex);
        ImageView level = v.findViewById(R.id.level);
        mLinkMicWaitText = v.findViewById(R.id.wait_text);
        v.findViewById(R.id.btn_refuse).setOnClickListener(this);
        v.findViewById(R.id.btn_accept).setOnClickListener(this);
        ImgLoader.display(mContext, u.getAvatar(), avatar);
        name.setText(u.getUserNiceName());
        sex.setImageResource(CommonIconUtil.getSexIcon(u.getSex()));
        LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(u.getLevelAnchor());
        if (levelBean != null) {
            ImgLoader.display(mContext, levelBean.getThumb(), level);
        }
        mLinkMicWaitCount = LINK_MIC_COUNT_MAX;
        mLinkMicWaitText.setText(mLinkMicWaitString + "(" + mLinkMicWaitCount + ")...");
        mLinkMicPopWindow = new PopupWindow(v, DpUtil.dp2px(280), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mLinkMicPopWindow.setBackgroundDrawable(new ColorDrawable());
        mLinkMicPopWindow.setOutsideTouchable(false);
        mLinkMicPopWindow.setFocusable(false);
        mLinkMicPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (mAcceptLinkMic) {
                    if (((LiveActivity) mContext).isGamePlaying()) {
                        ToastUtil.show(R.string.live_game_cannot_link_mic);
                        SocketLinkMicAnchorUtil.linkMicPlayGaming(mSocketClient, mApplyUid);
                        mApplyUid = null;
                        mPkUid = null;
                    } else {
                        LiveHttpUtil.livePkCheckLive(mApplyUid, mApplyStream, mSelfStream, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0 && info.length > 0) {
                                    if (mLiveSdk == Constants.LIVE_SDK_TX) {
                                        String playUrl = mPlayUrl;
                                        JSONObject obj = JSON.parseObject(info[0]);
                                        if (obj != null) {
                                            String accUrl = obj.getString("pull");
                                            if (!TextUtils.isEmpty(accUrl)) {
                                                playUrl = accUrl;
                                            }
                                        }
                                        SocketLinkMicAnchorUtil.linkMicAnchorAccept(mSocketClient, playUrl, mApplyUid);
                                    } else {
                                        SocketLinkMicAnchorUtil.linkMicAnchorAccept(mSocketClient, mPlayUrl, mApplyUid);
                                    }
                                    mIsLinkMic = true;
                                } else {
                                    ToastUtil.show(msg);
                                    mApplyUid = null;
                                    mPkUid = null;
                                }
                            }
                        });
                    }
                } else {
                    if (mLinkMicWaitCount == 0) {
                        SocketLinkMicAnchorUtil.linkMicNotResponse(mSocketClient, mApplyUid);
                    } else {
                        SocketLinkMicAnchorUtil.linkMicAnchorRefuse(mSocketClient, mApplyUid);
                    }
                    mApplyUid = null;
                    mPkUid = null;
                }
                mIsApplyDialogShow = false;
                mLinkMicWaitText = null;
                mLinkMicPopWindow = null;
            }
        });
        mLinkMicPopWindow.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    public String getPkUid() {
        return mPkUid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_refuse) {
            refuseLinkMic();

        } else if (i == R.id.btn_accept) {
            acceptLinkMic();

        } else if (i == R.id.btn_close_link_mic) {
            ToastUtil.show("点击了断开按钮");
            closeLinkMic();

        }
    }

    /**
     * 拒绝连麦
     */
    private void refuseLinkMic() {
        if (mLinkMicPopWindow != null) {
            mLinkMicPopWindow.dismiss();
        }
    }

    /**
     * 接受连麦
     */
    private void acceptLinkMic() {
        if (((LiveAnchorActivity) mContext).isBgmPlaying()) {
            DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.link_mic_close_bgm), new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    ((LiveAnchorActivity) mContext).stopBgm();
                    mAcceptLinkMic = true;
                    if (mLinkMicPopWindow != null) {
                        mLinkMicPopWindow.dismiss();
                    }
                }
            });
        } else {
            mAcceptLinkMic = true;
            if (mLinkMicPopWindow != null) {
                mLinkMicPopWindow.dismiss();
            }
        }
    }

    /**
     * 主播自己主动断开连麦
     */
    private void closeLinkMic() {
        SocketLinkMicAnchorUtil.linkMicAnchorClose(mSocketClient, mPkUid);
    }


    /**
     * 主播与主播连麦  所有人收到对方主播的播流地址的回调
     *
     * @param playUrl 对方主播的播流地址
     */
    public void onLinkMicAnchorPlayUrl(String pkUid, String playUrl) {
        //L.e("主播连麦----对方主播的播放地址---->" + playUrl);
        mApplyUid = null;
        mLastApplyLinkMicTime = 0;
        mIsLinkMic = true;
        mPkUid = pkUid;
        if (mIsAnchor || mLiveSdk != Constants.LIVE_SDK_TX) {
            if (mPlayingVideo) {
                return;
            }
            mPlayingVideo = true;
            if (mLiveRoomPlayViewHolder != null) {
                mLiveRoomPlayViewHolder.changeToLeft();
            }
            if (mLiveSdk == Constants.LIVE_SDK_TX) {
                mLiveLinkMicPlayViewHolder = new LiveLinkMicPlayTxViewHolder(mContext, mRightContainer);
            } else {
                mLiveLinkMicPlayViewHolder = new LiveLinkMicPlayKsyViewHolder(mContext, mRightContainer);
            }
            mLiveLinkMicPlayViewHolder.setOnCloseListener(mIsAnchor ? this : null);
            mLiveLinkMicPlayViewHolder.addToParent();
            mLiveLinkMicPlayViewHolder.play(playUrl);
            if (mIsAnchor) {
                ToastUtil.show(R.string.link_mic_anchor_accept_2);
                ((LiveAnchorActivity) mContext).setPkBtnVisible(true);
                if (mLiveSdk == Constants.LIVE_SDK_TX) {
                    //主播混流
                    String toStream = null;
                    int startIndex = playUrl.lastIndexOf("/");
                    int endIndex = playUrl.indexOf("?", startIndex);
                    if (startIndex >= 0 && startIndex < playUrl.length()
                            && endIndex >= 0 && endIndex < playUrl.length()
                            && startIndex < endIndex) {
                        toStream = playUrl.substring(startIndex + 1, endIndex);
                    }
                    if (!TextUtils.isEmpty(toStream)) {
                        EventBus.getDefault().post(new LinkMicTxMixStreamEvent(Constants.LINK_MIC_TYPE_ANCHOR, toStream));
                    }
                }
            }

        }
        showPkUidFollow(pkUid);
    }

    /**
     * 隐藏对方主播的关注信息
     */
    public void hidePkUidFollow() {
        if (mPkFollowGroup != null && mPkFollowGroup.getVisibility() != View.INVISIBLE) {
            mPkFollowGroup.setVisibility(View.INVISIBLE);
        }
        if (mPkFollowAvatar != null) {
            mPkFollowAvatar.setImageDrawable(null);
        }
        if (mPkFollowName != null) {
            mPkFollowName.setText(null);
        }
    }

    /**
     * 显示对方主播的关注信息
     *
     * @param pkUid 对方主播的uid
     */
    public void showPkUidFollow(final String pkUid) {
        ImHttpUtil.getImUserInfo(pkUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mPkFollowGroup != null && mPkFollowGroup.getVisibility() != View.VISIBLE) {
                        mPkFollowGroup.setVisibility(View.VISIBLE);
                    }
                    if (mPkFollowAvatar != null) {
                        mPkFollowAvatar.setImageDrawable(null);
                        ImgLoader.displayAvatar(mContext, obj.getString("avatar"), mPkFollowAvatar);
                    }
                    if (mPkFollowName != null) {
                        mPkFollowName.setText(obj.getString("user_nickname"));
                    }
                    if (mPkFollowIcon != null) {
                        if (obj.getIntValue("utot") == 1) {
                            if (mPkFollowIcon.getVisibility() != View.GONE) {
                                mPkFollowIcon.setVisibility(View.GONE);
                            }
                            mPkFollowIcon.setOnClickListener(null);
                        } else {
                            if (mPkFollowIcon.getVisibility() != View.VISIBLE) {
                                mPkFollowIcon.setVisibility(View.VISIBLE);
                            }
                            mPkFollowIcon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CommonHttpUtil.setAttention(pkUid, new CommonCallback<Integer>() {
                                        @Override
                                        public void callback(Integer isAttention) {
                                            if (isAttention == 1) {
                                                if (mPkFollowIcon.getVisibility() != View.GONE) {
                                                    mPkFollowIcon.setVisibility(View.GONE);
                                                }
                                                ToastUtil.show(R.string.im_follow_tip_2);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            }
        });
    }


    /**
     * 主播与主播连麦 断开连麦的回调
     */
    public void onLinkMicAnchorClose() {
        if (mIsLinkMic) {
            ToastUtil.show(R.string.link_mic_anchor_close);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mLinkMicPopWindow != null) {
            mLinkMicPopWindow.dismiss();
        }
        mLastApplyLinkMicTime = 0;
        mPlayingVideo = false;
        if (mLiveLinkMicPlayViewHolder != null) {
            mLiveLinkMicPlayViewHolder.release();
            mLiveLinkMicPlayViewHolder.removeFromParent();
        }
        mLiveLinkMicPlayViewHolder = null;
        if (mIsAnchor || mLiveSdk != Constants.LIVE_SDK_TX) {
            if (mLiveRoomPlayViewHolder != null) {
                mLiveRoomPlayViewHolder.changeToBig();
            }
        }
        mIsLinkMic = false;
        mApplyUid = null;
        mApplyStream = null;
        mPkUid = null;
        if (mIsAnchor) {
            ((LiveAnchorActivity) mContext).setPkBtnVisible(false);
            if (mLiveSdk == Constants.LIVE_SDK_TX) {
                EventBus.getDefault().post(new LinkMicTxMixStreamEvent(Constants.LINK_MIC_TYPE_ANCHOR, null));
            }
        }
        hidePkUidFollow();
    }


    /**
     * 主播与主播连麦 对方主播拒绝连麦的回调
     */
    public void onLinkMicAnchorRefuse() {
        mLastApplyLinkMicTime = 0;
        ToastUtil.show(R.string.link_mic_refuse_2);
    }

    /**
     * 主播与主播连麦  对方主播无响应的回调
     */
    public void onLinkMicNotResponse() {
        mLastApplyLinkMicTime = 0;
        ToastUtil.show(R.string.link_mic_anchor_not_response_2);
    }

    /**
     * 主播与主播连麦  对方主播正在忙的回调
     */
    public void onLinkMicAnchorBusy() {
        mLastApplyLinkMicTime = 0;
        ToastUtil.show(R.string.link_mic_anchor_busy_2);
    }

    public void pause() {
        mPaused = true;
        if (mLiveLinkMicPlayViewHolder != null) {
            mLiveLinkMicPlayViewHolder.pause();
        }
    }

    public void resume() {
        if (mPaused) {
            mPaused = false;
            if (mLiveLinkMicPlayViewHolder != null) {
                mLiveLinkMicPlayViewHolder.resume();
            }
        }
    }

    public void release() {
        hidePkUidFollow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        mSocketClient = null;
        if (mLiveLinkMicPlayViewHolder != null) {
            mLiveLinkMicPlayViewHolder.release();
        }
        mLiveLinkMicPlayViewHolder = null;
    }

    /**
     * 是否能够打开连麦的弹窗
     */
    public boolean canOpenLinkMicAnchor() {
        if (((LiveActivity) mContext).isGamePlaying()) {
            ToastUtil.show(R.string.live_game_cannot_link_mic);
            return false;
        }
        if (mIsLinkMic || ((LiveActivity) mContext).isLinkMic()) {
            ToastUtil.show(mIsAnchor ? R.string.live_link_mic_cannot_link_2 : R.string.live_link_mic_cannot_link);
            return false;
        }
        if (System.currentTimeMillis() - mLastApplyLinkMicTime < 11000) {
            ToastUtil.show(R.string.link_mic_apply_waiting);
            return false;
        }
        return true;
    }

    public boolean isLinkMic() {
        return mIsLinkMic;
    }

    public void clearData() {
        mPlayingVideo = false;
        mIsApplyDialogShow = false;
        mAcceptLinkMic = false;
        mIsLinkMic = false;
        mApplyUid = null;
        mApplyStream = null;
        mPkUid = null;
        mLinkMicWaitCount = 0;
        mLinkMicPopWindow = null;
        mLastApplyLinkMicTime = 0;
        hidePkUidFollow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mLiveLinkMicPlayViewHolder != null) {
            mLiveLinkMicPlayViewHolder.release();
            mLiveLinkMicPlayViewHolder.removeFromParent();
        }
        mLiveLinkMicPlayViewHolder = null;
        if (mLiveRoomPlayViewHolder != null) {
            mLiveRoomPlayViewHolder.changeToBig();
        }
    }

    public void setSelfStream(String selfStream) {
        mSelfStream = selfStream;
    }

    /**
     * 主播与主播连麦  对方主播正在游戏
     */
    public void onlinkMicPlayGaming() {
        mLastApplyLinkMicTime = 0;
        DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.link_mic_play_game));
    }
}
