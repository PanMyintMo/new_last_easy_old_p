package com.gawilive.im.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.bean.ConfigBean;
import com.gawilive.common.event.FollowEvent;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.SpUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.common.views.AbsViewHolder;
import com.gawilive.im.R;
import com.gawilive.im.activity.*;
import com.gawilive.im.adapter.ImListAdapter;
import com.gawilive.im.bean.ImUserBean;
import com.gawilive.im.bean.SystemMessageBean;
import com.gawilive.im.dialog.SystemMessageDialogFragment;
import com.gawilive.im.event.ImMessagePromptEvent;
import com.gawilive.im.event.ImUserMsgEvent;
import com.gawilive.im.event.SystemMsgEvent;
import com.gawilive.im.http.ImHttpConsts;
import com.gawilive.im.http.ImHttpUtil;
import com.gawilive.im.utils.ImMessageUtil;

import com.lxj.xpopup.XPopup;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by cxf on 2018/10/24.
 */

public class ChatListViewHolder extends AbsViewHolder implements View.OnClickListener, ImListAdapter.ActionListener {

    public static final int TYPE_ACTIVITY = 0;
    public static final int TYPE_DIALOG = 1;
    private int mType;
    private View mBtnSystemMsg;
    private RecyclerView mRecyclerView;
    private ImListAdapter mAdapter;
    private ActionListener mActionListener;
    private View mSystemMsgRedPoint;//系统消息的红点
    private TextView mSystemMsgContent;
    private TextView mSystemTime;
    private HttpCallback mSystemMsgCallback;
    private View mBtnBack;
    private String mLiveUid;//主播的uid
    private boolean mPriMsgSwitchOpen;//私信开关是否开启
    private TextView mRedPointConcat;//联系人
    private TextView mRedPointLike;//赞
    private TextView mRedPointAt;//@我的
    private TextView mRedPointComment;//评论


    public ChatListViewHolder(Context context, ViewGroup parentView, int type) {
        super(context, parentView, type);
    }

    @Override
    protected void processArguments(Object... args) {
        mType = (int) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_list;
    }

    @Override
    public void init() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ImListAdapter(mContext);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mBtnBack = findViewById(R.id.btn_back);
        if (mType == TYPE_ACTIVITY) {
            mBtnBack.setOnClickListener(this);
        } else {
            mBtnBack.setVisibility(View.INVISIBLE);
            View top = findViewById(R.id.top);
            top.setBackgroundColor(0xfff9fafb);
        }
        findViewById(R.id.btn_ignore).setOnClickListener(this);
        View headView = mAdapter.getHeadView();
        View groupMore = headView.findViewById(R.id.group_more);
        if (mType == TYPE_DIALOG) {
            groupMore.setVisibility(View.GONE);
        } else {
            mRedPointConcat = headView.findViewById(R.id.red_point_concat);
            mRedPointLike = headView.findViewById(R.id.red_point_like);
            mRedPointAt = headView.findViewById(R.id.red_point_at);
            mRedPointComment = headView.findViewById(R.id.red_point_comment);
            headView.findViewById(R.id.btn_concat).setOnClickListener(this);
            headView.findViewById(R.id.btn_zan).setOnClickListener(this);
            headView.findViewById(R.id.btn_at).setOnClickListener(this);
            headView.findViewById(R.id.btn_comment).setOnClickListener(this);

            int unReadCountConcat = ImMessageUtil.getInstance().getUnReadMsgCount(Constants.IM_MSG_CONCAT);
            int unReadCountLike = ImMessageUtil.getInstance().getUnReadMsgCount(Constants.IM_MSG_LIKE);
            int unReadCountAt = ImMessageUtil.getInstance().getUnReadMsgCount(Constants.IM_MSG_AT);
            int unReadCountComment = ImMessageUtil.getInstance().getUnReadMsgCount(Constants.IM_MSG_COMMENT);
            showUnReadCount(mRedPointConcat, unReadCountConcat);
            showUnReadCount(mRedPointLike, unReadCountLike);
            showUnReadCount(mRedPointAt, unReadCountAt);
            showUnReadCount(mRedPointComment, unReadCountComment);
        }
        mBtnSystemMsg = headView.findViewById(R.id.btn_system_msg);
        mBtnSystemMsg.setOnClickListener(this);
        mSystemMsgRedPoint = headView.findViewById(R.id.red_point);
        mSystemMsgContent = headView.findViewById(R.id.msg);
        mSystemTime = headView.findViewById(R.id.time);
        mSystemMsgCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    SystemMessageBean bean = JSON.parseObject(info[0], SystemMessageBean.class);
                    if (mSystemMsgContent != null) {
                        mSystemMsgContent.setText(bean.getContent());
                    }
                    if (mSystemTime != null) {
                        mSystemTime.setText(bean.getAddtime());
                    }
                    if (SpUtil.getInstance().getBooleanValue(SpUtil.HAS_SYSTEM_MSG)) {
                        if (mSystemMsgRedPoint != null && mSystemMsgRedPoint.getVisibility() != View.VISIBLE) {
                            mSystemMsgRedPoint.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };
        ImageView avatar = headView.findViewById(R.id.avatar);
        avatar.setImageResource(CommonAppConfig.getInstance().getAppIconRes());
        EventBus.getDefault().register(this);
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        mPriMsgSwitchOpen = configBean != null && configBean.getPriMsgSwitch() == 1;
    }


    private void showUnReadCount(TextView redPointView, int unReadCount) {
        if (redPointView != null) {
            if (unReadCount > 0) {
                if (redPointView.getVisibility() != View.VISIBLE) {
                    redPointView.setVisibility(View.VISIBLE);
                }
                redPointView.setText(String.valueOf(unReadCount));
            } else {
                if (redPointView.getVisibility() == View.VISIBLE) {
                    redPointView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void release() {
        EventBus.getDefault().unregister(this);
        mActionListener = null;
        ImHttpUtil.cancel(ImHttpConsts.GET_SYSTEM_MESSAGE_LIST);
        ImHttpUtil.cancel(ImHttpConsts.GET_IM_USER_INFO);
    }

    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    public void loadData() {
        getSystemMessageList();
        if (!mPriMsgSwitchOpen) {
            return;
        }
        final boolean needAnchorItem = mType == TYPE_DIALOG
                && !TextUtils.isEmpty(mLiveUid) && !mLiveUid.equals(CommonAppConfig.getInstance().getUid());
        String uids = ImMessageUtil.getInstance().getConversationUids();
        if (TextUtils.isEmpty(uids)) {
            if (needAnchorItem) {
                uids = mLiveUid;
            } else {
                return;
            }
        } else {
            if (needAnchorItem) {
                if (!uids.contains(mLiveUid)) {
                    uids = mLiveUid + "," + uids;
                }
            }
        }
        ImHttpUtil.getImUserInfo(uids, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<ImUserBean> list = JSON.parseArray(Arrays.toString(info), ImUserBean.class);
                    list = ImMessageUtil.getInstance().getLastMsgInfoList(list);
                    if (mRecyclerView != null && mAdapter != null && list != null) {
                        if (needAnchorItem) {
                            int anchorItemPosition = -1;
                            for (int i = 0, size = list.size(); i < size; i++) {
                                ImUserBean bean = list.get(i);
                                if (bean != null) {
                                    if (mLiveUid.equals(bean.getId())) {
                                        anchorItemPosition = i;
                                        bean.setAnchorItem(true);
                                        if (!bean.isHasConversation()) {
                                            bean.setLastMessage(WordUtil.getString(R.string.im_live_anchor_msg));
                                        }
                                        break;
                                    }
                                }
                            }
                            if (anchorItemPosition > 0) {//把主播的会话排在最前面
                                Collections.sort(list, new Comparator<ImUserBean>() {
                                    @Override
                                    public int compare(ImUserBean bean1, ImUserBean bean2) {
                                        if (mLiveUid.equals(bean1.getId())) {
                                            return -1;
                                        } else if (mLiveUid.equals(bean2.getId())) {
                                            return 1;
                                        }
                                        return 0;
                                    }
                                });
                            }
                        }
                        mAdapter.setList(list);
                    }
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            if (mActionListener != null) {
                mActionListener.onCloseClick();
            }
        } else if (i == R.id.btn_ignore) {
            ignoreUnReadCount();

        } else if (i == R.id.btn_system_msg) {
            forwardSystemMessage();
        } else if (i == R.id.btn_concat) {
            ImMessageUtil.getInstance().markAllMessagesAsRead(Constants.IM_MSG_CONCAT, true);
            showUnReadCount(mRedPointConcat, 0);
            ImMsgConcatActivity.forward(mContext);
        } else if (i == R.id.btn_zan) {
            ImMessageUtil.getInstance().markAllMessagesAsRead(Constants.IM_MSG_LIKE, true);
            showUnReadCount(mRedPointLike, 0);
            showBottomZanListDialog();
        } else if (i == R.id.btn_at) {
            ImMessageUtil.getInstance().markAllMessagesAsRead(Constants.IM_MSG_AT, true);
            showUnReadCount(mRedPointAt, 0);
            ImMsgAtActivity.forward(mContext);
        } else if (i == R.id.btn_comment) {
            ImMessageUtil.getInstance().markAllMessagesAsRead(Constants.IM_MSG_COMMENT, true);
            showUnReadCount(mRedPointComment, 0);
            showBottomCommentListDialog();
        }

    }


    private void showBottomZanListDialog() {
        new XPopup.Builder(mContext)
                .asBottomList(WordUtil.getString(R.string.a_086), new String[]{WordUtil.getString(R.string.str_001), WordUtil.getString(R.string.str_002)}, (i, s) -> {
                    if (i == 0) {
                        ImMsgLikeNewActivity.forward(mContext);
                    } else {
                        ImMsgLikeActivity.forward(mContext);
                    }
                }).show();
    }

    private void showBottomCommentListDialog() {
        new XPopup.Builder(mContext)
                .asBottomList(WordUtil.getString(R.string.video_comment), new String[]{WordUtil.getString(R.string.str_003), WordUtil.getString(R.string.str_004)}, (i, s) -> {
                    if (i == 0) {
                        ImMsgCommentNewActivity.forward(mContext);
                    } else {
                        ImMsgCommentActivity.forward(mContext);
                    }
                }).show();
    }

    /**
     * 前往系统消息
     */
    private void forwardSystemMessage() {
        SpUtil.getInstance().setBooleanValue(SpUtil.HAS_SYSTEM_MSG, false);
        if (mSystemMsgRedPoint != null && mSystemMsgRedPoint.getVisibility() == View.VISIBLE) {
            mSystemMsgRedPoint.setVisibility(View.INVISIBLE);
        }
        if (mType == TYPE_ACTIVITY) {
            SystemMessageActivity.forward(mContext);
        } else {
            SystemMessageDialogFragment fragment = new SystemMessageDialogFragment();
            fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "SystemMessageDialogFragment");
        }
    }

    @Override
    public void onItemClick(ImUserBean bean) {
        if (bean != null) {
            ImMessageUtil.getInstance().markAllMessagesAsRead(bean.getId(), true);
            if (mActionListener != null) {
                mActionListener.onItemClick(bean);
            }
        }
    }

    @Override
    public void onItemDelete(ImUserBean bean, int size) {
        ImMessageUtil.getInstance().removeConversation(bean.getId());
    }

    public interface ActionListener {
        void onCloseClick();

        void onItemClick(ImUserBean bean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (e != null) {
            if (mAdapter != null) {
                mAdapter.setFollow(e.getToUid(), e.getIsAttention());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemMsgEvent(SystemMsgEvent e) {
        getSystemMessageList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUserMsgEvent(final ImUserMsgEvent e) {
        String uid = e.getUid();
        if (Constants.MALL_IM_ADMIN.equals(uid) && CommonAppConfig.getInstance().isTeenagerType()) {
            return;
        }
        if (Constants.IM_MSG_CONCAT.equals(uid)) {
            showUnReadCount(mRedPointConcat, e.getUnReadCount());
            return;
        }
        if (Constants.IM_MSG_LIKE.equals(uid)) {
            showUnReadCount(mRedPointLike, e.getUnReadCount());
            return;
        }
        if (Constants.IM_MSG_AT.equals(uid)) {
            showUnReadCount(mRedPointAt, e.getUnReadCount());
            return;
        }
        if (Constants.IM_MSG_COMMENT.equals(uid)) {
            showUnReadCount(mRedPointComment, e.getUnReadCount());
            return;
        }
        if (mPriMsgSwitchOpen && mRecyclerView != null && mAdapter != null) {
            int position = mAdapter.getPosition(e.getUid());
            if (position < 0) {
                ImHttpUtil.getImUserInfo(e.getUid(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            ImUserBean bean = JSON.parseObject(info[0], ImUserBean.class);
                            bean.setLastMessage(e.getLastMessage());
                            bean.setUnReadCount(e.getUnReadCount());
                            bean.setLastTime(e.getLastTime());
                            bean.setLastMsgId(e.getLastMsgId());
                            mAdapter.insertItem(bean);
                        }
                    }
                });
            } else {
                mAdapter.updateItem(e.getLastMessage(), e.getLastTime(), e.getUnReadCount(), position, e.getLastMsgId());
            }
        }
    }

    /**
     * 忽略未读
     */
    private void ignoreUnReadCount() {
        String[] unReadCountArr = ImMessageUtil.getInstance().getAllUnReadMsgCount();
        String unReadCount =
                unReadCountArr != null && unReadCountArr.length > 0 ? unReadCountArr[0] : "0";
        boolean hasSystemMsg = SpUtil.getInstance().getBooleanValue(SpUtil.HAS_SYSTEM_MSG);
        if ("0".equals(unReadCount) && !hasSystemMsg) {
            DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.im_msg_ignore_unread_3), true);
            return;
        }
        SpUtil.getInstance().setBooleanValue(SpUtil.HAS_SYSTEM_MSG, false);
        if (mSystemMsgRedPoint != null && mSystemMsgRedPoint.getVisibility() == View.VISIBLE) {
            mSystemMsgRedPoint.setVisibility(View.INVISIBLE);
        }
        ImMessageUtil.getInstance().markAllConversationAsRead();
        if (mAdapter != null) {
            mAdapter.resetAllUnReadCount();
        }
        showUnReadCount(mRedPointConcat, 0);
        showUnReadCount(mRedPointLike, 0);
        showUnReadCount(mRedPointAt, 0);
        showUnReadCount(mRedPointComment, 0);
        ToastUtil.show(R.string.im_msg_ignore_unread_2);
    }

    /**
     * 获取系统消息
     */
    private void getSystemMessageList() {
        ImHttpUtil.getSystemMessageList(1, mSystemMsgCallback);
    }


    /**
     * 撤回消息的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImMessagePromptEvent(ImMessagePromptEvent e) {
        if (mAdapter != null) {
            mAdapter.onPromptMessage(e.getMsgId(), e.getToUid(), e.isSelf());
        }
    }


}
