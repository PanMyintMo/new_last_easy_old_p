package com.gawilive.live.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.dialog.AbsDialogFragment;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.RouteUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.im.bean.ImUserBean;
import com.gawilive.im.views.ChatListViewHolder;
import com.gawilive.live.R;
import com.gawilive.live.activity.LiveActivity;

/**
 * Created by cxf on 2018/10/24.
 * 直播间私信聊天列表
 */

public class LiveChatListDialogFragment extends AbsDialogFragment {

    private ChatListViewHolder mChatListViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_empty;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(300);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChatListViewHolder = new ChatListViewHolder(mContext, (ViewGroup) mRootView, ChatListViewHolder.TYPE_DIALOG);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String liveUid = bundle.getString(Constants.LIVE_UID);
            mChatListViewHolder.setLiveUid(liveUid);
        }
        mChatListViewHolder.setActionListener(new ChatListViewHolder.ActionListener() {
            @Override
            public void onCloseClick() {
                dismiss();
            }

            @Override
            public void onItemClick(ImUserBean bean) {
                if (Constants.MALL_IM_ADMIN.equals(bean.getId())) {
                    if (CommonAppConfig.getInstance().isTeenagerType()) {
                        ToastUtil.show(com.gawilive.common.R.string.a_137);
                    }else{
                        RouteUtil.forward(mContext, "com.gawilive.mall.activity.OrderMessageActivity");
                    }
                } else {
                    ((LiveActivity) mContext).openChatRoomWindow(bean, bean.getAttent() == 1);
                }
            }
        });
        mChatListViewHolder.addToParent();
        mChatListViewHolder.loadData();
    }

    @Override
    public void onDestroy() {
        if (mChatListViewHolder != null) {
            mChatListViewHolder.release();
        }
        super.onDestroy();
    }
}
